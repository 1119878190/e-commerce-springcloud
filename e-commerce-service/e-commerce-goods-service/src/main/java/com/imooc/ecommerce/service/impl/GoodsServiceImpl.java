package com.imooc.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.common.TableId;
import com.imooc.ecommerce.constant.GoodsConstant;
import com.imooc.ecommerce.dao.EcommerceGoodsDao;
import com.imooc.ecommerce.entity.EcommerceGoods;
import com.imooc.ecommerce.goods.DeductGoodsInventory;
import com.imooc.ecommerce.goods.GoodsInfo;
import com.imooc.ecommerce.goods.SimpleGoodsInfo;
import com.imooc.ecommerce.service.IGoodsService;
import com.imooc.ecommerce.vo.PageSimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private EcommerceGoodsDao ecommerceGoodsDao;


    @Override
    public List<GoodsInfo> getGoodsInfoByTableId(TableId tableId) {

        // 详细的商品信息，不能从 redis 中去拿
        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(Collectors.toList());
        log.info("get goods info by ids:[{}]", JSON.toJSONString(ids));
        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(ecommerceGoodsDao.findAllById(ids));

        List<GoodsInfo> goodsInfos = ecommerceGoods.stream().map(EcommerceGoods::toGoodsInfo).collect(Collectors.toList());
        return goodsInfos;
    }

    @Override
    public PageSimpleGoodsInfo getSimpleGoodsInfoByPage(int page) {
        // 分页不能从 redis cache 中去拿
        if (page <= 1) {
            page = 1;
        }
        // 这里分页的规则（你可以自由修改）:1页10条数据，按照id倒叙
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<EcommerceGoods> orderPage = ecommerceGoodsDao.findAll(pageRequest);

        // 是否还有更多页：总也数是否大于当前给定的页
        boolean hashMore = orderPage.getTotalPages() > page;
        return new PageSimpleGoodsInfo(orderPage.getContent().stream().map(EcommerceGoods::toSimple).collect(Collectors.toList()), hashMore);
    }

    @Override
    public List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(TableId tableId) {

        List<Object> goodsIds = tableId.getIds().stream()
                .map(i -> i.getId().toString()).collect(Collectors.toList());

        List<Object> cachedSimpleGoodsInfo = stringRedisTemplate.opsForHash().multiGet(GoodsConstant.ECOMMERCE_GOODS_DICT_KEY, goodsIds);

        // 如果从redis 中查到了商品信息 ，分两种情况
        if (CollectionUtils.isNotEmpty(cachedSimpleGoodsInfo)) {
            // 1. 如果从缓存中查询出所有需要的 SimpleGoodsInfo
            if (cachedSimpleGoodsInfo.size() == goodsIds.size()) {
                log.info("=============全部从redis中获取============");
                return parseCacheGoodsInfo(cachedSimpleGoodsInfo);
            } else {
                // 2.只查询出来一部分
                List<SimpleGoodsInfo> left = parseCacheGoodsInfo(cachedSimpleGoodsInfo);
                // 取差集 : 传递进来的参数 - 缓存中查到的 = 缓存中没有的
                Collection<Long> subtractIds = CollectionUtils.subtract(
                        goodsIds.stream()
                                .map(item -> Long.valueOf(item.toString())).collect(Collectors.toList()),
                        left.stream()
                                .map(item -> item.getId()).collect(Collectors.toList())
                );
                // 缓存中没有的 穿数据表并缓存
                List<SimpleGoodsInfo> right = queryGoodsFromDBAndCacheToRedis(
                        new TableId(subtractIds.stream().map(TableId.Id::new)
                                .collect(Collectors.toList())));
                // 合并 left 和 right 并返回
                log.info("一部分总redis，一部分db");
                return new ArrayList<>(CollectionUtils.union(left, right));
            }
        } else {
            return queryGoodsFromDBAndCacheToRedis(tableId);
        }

    }

    /**
     * 将缓存中的数据反序列化成 java pojo 对象
     *
     * @param cachedSimpleGoodsInfo
     * @return
     */
    private List<SimpleGoodsInfo> parseCacheGoodsInfo(List<Object> cachedSimpleGoodsInfo) {
        return cachedSimpleGoodsInfo.stream()
                .map(item -> JSON.parseObject(item.toString(), SimpleGoodsInfo.class))
                .collect(Collectors.toList());
    }

    /**
     * 从数据表中查询数据，并缓存到 redis 中
     *
     * @param tableId
     * @return
     */
    private List<SimpleGoodsInfo> queryGoodsFromDBAndCacheToRedis(TableId tableId) {
        // 从数据表中查询数据并作转换
        List<Long> ids = tableId.getIds().stream()
                .map(TableId.Id::getId).collect(Collectors.toList());
        log.info("get simple goods info by ids ( from db): [{}]", JSON.toJSONString(ids));

        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(ecommerceGoodsDao.findAllById(ids));

        List<SimpleGoodsInfo> simpleGoodsInfos = ecommerceGoods.stream().map(EcommerceGoods::toSimple).collect(Collectors.toList());

        // 将结果缓存，下一次直接可以从 redis 中查询
        log.info("cache goods info : [{}]", JSON.toJSONString(ids));

        HashMap<String, String> id2JsonObject = new HashMap<>();
        simpleGoodsInfos.stream().forEach(
                item -> id2JsonObject.put(item.getId().toString(), JSON.toJSONString(item))
        );

        // 保存到redis 中
        stringRedisTemplate.opsForHash().putAll(GoodsConstant.ECOMMERCE_GOODS_DICT_KEY, id2JsonObject);

        return simpleGoodsInfos;
    }


    @Override
    public Boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories) {

        // 检验参数是否合法
        deductGoodsInventories.forEach(item -> {
                    if (item.getCount() <= 0) {
                        throw new RuntimeException("purchase goods count need > 0");
                    }
                }
        );

        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(
                ecommerceGoodsDao.findAllById(
                        deductGoodsInventories.stream()
                                .map(DeductGoodsInventory::getGoodsId)
                                .collect(Collectors.toList()))
        );

        // 根据传递的 goodsIds 查询不到商品对象， 抛异常
        if (CollectionUtils.isEmpty(ecommerceGoods)) {
            throw new RuntimeException("cant not found any goods by request");
        }
        // 查询出来的商品数量与传递的不一致，跑异常
        if (ecommerceGoods.size() != deductGoodsInventories.size()) {
            throw new RuntimeException("request is not valid");
        }

        // goodsId -> DeductGoodsInventory
        Map<Long, DeductGoodsInventory> goodsId2Inventory = deductGoodsInventories.stream()
                .collect(Collectors.toMap(DeductGoodsInventory::getGoodsId, Function.identity()));

        ecommerceGoods.forEach(item -> {
            Long currentInventory = item.getInventory();
            Integer needDeductInventory = goodsId2Inventory.get(item.getId()).getCount();
            if (currentInventory < needDeductInventory) {
                log.error("goods inventory is not enough: [{}],[{}]", currentInventory, needDeductInventory);
                throw new RuntimeException("goods inventory is not enough: " + item.getId());
            }

            // 扣减库存
            item.setInventory(currentInventory - needDeductInventory);
            log.info("deduct goods inventory : [{}],[{}],[{}]", item.getId(), currentInventory, item.getInventory());

        });

        ecommerceGoodsDao.saveAll(ecommerceGoods);
        log.info("deduct goods inventory done");


        return true;
    }
}
