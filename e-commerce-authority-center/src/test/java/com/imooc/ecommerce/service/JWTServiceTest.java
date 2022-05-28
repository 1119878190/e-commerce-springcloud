package com.imooc.ecommerce.service;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.util.TokenParseUtil;
import com.imooc.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * JWT 相关服务测试类
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class JWTServiceTest {


    @Autowired
    private  IJWTService ijwtService;


    @Test
    public void testGenerateAndParseToken() throws Exception{
        String jwtToken = ijwtService.generateToken("test1","e10adc3949ba59abbe56e057f20f883e");
        log.info("jwt token is : [{}]",jwtToken);

        LoginUserInfo loginUserInfo = TokenParseUtil.parseUserInfoFromToken(jwtToken);
        log.info("parse token : [{}]", JSON.toJSONString(loginUserInfo));
    }


    @Test
    public void parseToken() throws Exception{

        String jwtToken = "eyJhbGciOiJSUzI1NiJ9.eyJlLWNvbW1lcmNlLXVzZXIiOiJ7XCJpZFwiOjEwLFwidXNlcm5hbWVcIjpcInRlc3QxXCJ9IiwianRpIjoiMjBjNGM1OWMtZmI1Yy00ODBlLThhNWEtYTdiZDA2NDJlZGVhIiwiZXhwIjoxNjUzNzUzNjAwfQ.Yce17VGgovMsJQaMQSKLQwFR4rbeUfqwDgi0IruaD524YEFEg_TT9iX7Q0oNI08HFsb_8sf9awsmhH-M-0X2O6NYZ02QTgmVksO-YF4F_xpp0ESrZflPBdr1eFvzTh5nHjq4yP8zEQuncIaoGLb2PT32NVf76sJiEVamvD8sq2HYYhLNPrHKePsBQHG79oF2qe9PCoH92QXpw7I-mR1WjtL-iJWdcYIJYBMDMZux86M6EA3OAS1e_cRnCYcnNQIh4qZCtXK3vbfWFsTAF_Pjkyj42lz99yCSkvyt3E83c3cpXu-04AOLT_PYmdjLeD5SYtCg0-7keriJRCAk9Uove6";

        LoginUserInfo loginUserInfo = TokenParseUtil.parseUserInfoFromToken(jwtToken);
        log.info("parse token : [{}]", JSON.toJSONString(loginUserInfo));
    }


}
