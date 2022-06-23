package com.imooc.ecommerce.service;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.common.TableId;
import com.imooc.ecommerce.goods.DeductGoodsInventory;
import com.imooc.ecommerce.goods.GoodsInfo;
import com.imooc.ecommerce.goods.SimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品微服务功能测试
 *
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsServiceTest {


    @Autowired
    private IGoodsService goodsService;

    @Test
    public void testGetGoodsInfoByTableId(){
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<GoodsInfo> goodsInfoByTableId = goodsService.getGoodsInfoByTableId(
                new TableId(ids.stream().map(item -> new TableId.Id(item)).collect(Collectors.toList())));
        log.info("test get goods info by table id : [{}]", JSON.toJSONString(goodsInfoByTableId));
    }

    @Test
    public void testGetSimpleGoodsInfoByPage(){
        log.info("test get simple goods info by page:[{}]", JSON.toJSONString(
                goodsService.getSimpleGoodsInfoByPage(1)
        ));
    }

    @Test
    public void testGetSimpleGoodsInfoByTableId(){
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<TableId.Id> tIds = ids.stream().map(TableId.Id::new).collect(Collectors.toList());
        List<SimpleGoodsInfo> goodsInfoByTableId = goodsService.getSimpleGoodsInfoByTableId(
                new TableId(tIds));
        log.info("test get goods info by table id : [{}]", JSON.toJSONString(goodsInfoByTableId));
    }

    @Test
    public void testDeductGoodsInventory(){
        List<DeductGoodsInventory> deductGoodsInventories = Arrays.asList(new DeductGoodsInventory(1L, 100),
                new DeductGoodsInventory(2L, 66));
        Boolean aBoolean = goodsService.deductGoodsInventory(deductGoodsInventories);
        log.info("deductGood is success:[{}]",aBoolean);
    }
}
