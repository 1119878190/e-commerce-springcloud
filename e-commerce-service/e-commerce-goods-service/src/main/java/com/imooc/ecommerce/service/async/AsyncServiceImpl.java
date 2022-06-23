package com.imooc.ecommerce.service.async;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.constant.GoodsConstant;
import com.imooc.ecommerce.dao.EcommerceGoodsDao;
import com.imooc.ecommerce.entity.EcommerceGoods;
import com.imooc.ecommerce.goods.GoodsInfo;
import com.imooc.ecommerce.goods.SimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 异步服务接口实现
 */
@Service
@Slf4j
@Transactional
public class AsyncServiceImpl implements IAsyncService {

    @Autowired
    private EcommerceGoodsDao ecommerceGoodsDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 异步任务需要加上注解并指定线程池
     * 异步任务处理两件事
     * 1. 将商品信息保存到数据表
     * 2.更新商品缓存
     *
     * @param goodsInfos
     * @param taskId
     */
    @Async("getAsyncExecutor")
    @Override
    public void asyncImportGoods(List<GoodsInfo> goodsInfos, String taskId) {
        log.info("async task running taskId: [{}]", taskId);

        StopWatch stopWatch = StopWatch.createStarted();

        // 1. 如果是 goodsInfo 中存在重复的商品，不保存，直接返回，记录错误日志
        // 请求数据是否合法的标记

        boolean isIllegal = false;
        // 将商品信息字段 joint 在一起，用来判断是否存在重复
        HashSet<String> goodsJoinInfos = new HashSet<>(goodsInfos.size());
        // 过滤出来的，可以入库的商品信息(规则按照自己的业务需求自定义即可)
        List<GoodsInfo> filteredGoodsInfo = new ArrayList<>();

        // 走一遍过滤,过滤非法参数与判定当前请求是否合法;
        for (GoodsInfo goodsInfo : goodsInfos) {

            // 基本条件不满足,直接过滤
            if (goodsInfo.getPrice() <= 0 || goodsInfo.getSupply() <= 0) {
                log.info("goods info is invalid :[{}]", JSON.toJSONString(goodsInfo));
            }

            // 组合商品信息
            String jointInfo = String.format("%s,%s,%s", goodsInfo.getGoodsCategory(), goodsInfo.getBrandCategory(), goodsInfo.getGoodsName());
            if (goodsJoinInfos.contains(jointInfo)) {
                isIllegal = true;
            }

            // 加入到两个容器中
            goodsJoinInfos.add(jointInfo);
            filteredGoodsInfo.add(goodsInfo);
        }

        // 如果存在重复商品或者是没有需要入库的商品，直接打印日志返回
        if (isIllegal || CollectionUtils.isEmpty(filteredGoodsInfo)) {
            stopWatch.stop();
            log.warn("import nothing: [{}]", JSON.toJSONString(filteredGoodsInfo));
            log.info("check and import goods done:[{}ms]", stopWatch.getTime(TimeUnit.MILLISECONDS));
            return;
        }

        List<EcommerceGoods> ecommerceGoods = filteredGoodsInfo.stream().map(EcommerceGoods::to).collect(Collectors.toList());
        List<EcommerceGoods> targetGoods = new ArrayList<>(ecommerceGoods.size());

        // 2.保存goodsInfo之前先判断是否存在重复商品
        ecommerceGoods.forEach(g -> {

            if (null != ecommerceGoodsDao.findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName(
                    g.getGoodsCategory(), g.getBrandCategory(), g.getGoodsName()
            ).orElse(null)) {
                return;
            }
            targetGoods.add(g);
        });

        // 商品信息入库
        List<EcommerceGoods> savedGoods = IterableUtils.toList(ecommerceGoodsDao.saveAll(targetGoods));

        // 将入库商品信息同步到 Redis 中
        saveNewsGoodsInfoToRedis(savedGoods);
        log.info("save goods info to db and redis:[{}]", savedGoods.size());

        stopWatch.stop();
        log.info("check and import goods success : [{}ms]",
                stopWatch.getTime(TimeUnit.MILLISECONDS));

    }


    /**
     * 将保存到数据表中的数据缓存到 Redis 中
     * dict: key-> <id,SimpleGoodsInfo(json)>
     *
     * @param saveGoods
     */
    private void saveNewsGoodsInfoToRedis(List<EcommerceGoods> saveGoods) {

        // 由于 Redis 是内存存储, 只保存简单的商品信息
        List<SimpleGoodsInfo> simpleGoodsInfos = saveGoods.stream()
                .map(EcommerceGoods::toSimple).collect(Collectors.toList());

        Map<String, String> id2sonObject = new HashMap<>();
        simpleGoodsInfos.forEach(
                g -> id2sonObject.put(g.getId().toString(), JSON.toJSONString(g))
        );

        // 保存到 Redis 中
        stringRedisTemplate.opsForHash().putAll(
                GoodsConstant.ECOMMERCE_GOODS_DICT_KEY,
                id2sonObject
        );
    }
}
