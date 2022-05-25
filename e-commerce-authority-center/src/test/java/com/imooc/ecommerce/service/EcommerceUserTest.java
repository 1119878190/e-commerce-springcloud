package com.imooc.ecommerce.service;


import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.imooc.ecommerce.dao.EcommerceUserDao;
import com.imooc.ecommerce.entity.EcommerceUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class EcommerceUserTest {


    @Autowired
    private EcommerceUserDao ecommerceUserDao;

    @Test
    public void createUserRecord(){

        EcommerceUser ecommerceUser = new EcommerceUser();
        ecommerceUser.setUsername("test1");
        ecommerceUser.setPassword(MD5.create().digestHex("123456"));
        ecommerceUser.setExtraInfo("{}");
        ecommerceUserDao.save(ecommerceUser);

        log.info("save user : [{}]", JSONObject.toJSONString(ecommerceUser));
    }

}
