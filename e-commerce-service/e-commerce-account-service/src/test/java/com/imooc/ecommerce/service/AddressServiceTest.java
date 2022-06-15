package com.imooc.ecommerce.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imooc.ecommerce.account.AddressInfo;
import com.imooc.ecommerce.common.TableId;
import com.imooc.ecommerce.filter.AccessContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;


/**
 * 用户地址相关服务功能测试
 */
@Slf4j
public class AddressServiceTest extends BaseTest {


    @Autowired
    private IAddressService addressService;


    /**
     * 测试创建用户地址信息
     */
    @Test
    public void testCreateAddressInfo() {
        AddressInfo.AddressItem addressItem = new AddressInfo.AddressItem();
        addressItem.setUsername("test1");
        addressItem.setPhone("13879639740");
        addressItem.setCity("南京");
        addressItem.setProvince("江苏");
        addressItem.setAddressDetail("雨花台区");
        log.info("test create address info : [{}]", JSON.toJSONString(addressItem));

        addressService.createAddressInfo(new AddressInfo(AccessContext.getLoginUserInfo().getId(), Arrays.asList(addressItem)));
    }


    /**
     * 获取当前用户的地址信息
     */
    @Test
    public void testGetCurrentAddressInfo() {

        AddressInfo currentAddressInfo = addressService.getCurrentAddressInfo();
        log.info("get current address info : [{}]", JSONObject.toJSONString(currentAddressInfo));

    }


    /**
     * 测试通过 id 获取用户地址信息
     */
    @Test
    public void testGetCurrentById() {
        AddressInfo addressInfoById = addressService.getAddressInfoById(1L);
        log.info("test get address info by id: [{}]", JSONObject.toJSONString(addressInfoById));
    }


    @Test
    public void testGetAddressInfoByTableId() {
        log.info("test get address info by table id: [{}]",JSONObject.toJSONString(
                addressService.getAddressInfoByTableId(new TableId(Collections.singletonList(new TableId.Id(1L))))
        ));
    }

}
