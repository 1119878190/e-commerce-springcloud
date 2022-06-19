package com.imooc.ecommerce.controller;


import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.annotation.IgnoreResponseAdvice;
import com.imooc.ecommerce.service.IJWTService;
import com.imooc.ecommerce.util.TokenParseUtil;
import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对外暴露的授权服务接口
 */

@Slf4j
@RestController
@RequestMapping("/authority")
public class AuthorityController {


    private final IJWTService ijwtService;

    public AuthorityController(IJWTService ijwtService) {
        this.ijwtService = ijwtService;
    }


    /**
     * 从授权中心获取token ，且返回信息中没有统一的包装
     *
     * @param usernameAndPassword
     * @return
     */
    @IgnoreResponseAdvice
    @PostMapping("/token")
    public JwtToken token(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception {
        log.info("request to get token with params:[{}]", JSON.toJSONString(usernameAndPassword));
        return new JwtToken(ijwtService.generateToken(usernameAndPassword.getUsername(), usernameAndPassword.getPassword()));
    }


    /**
     * 注册用户，并返回注册用户的token，即通过授权中心创建用户
     *
     * @param usernameAndPassword
     * @return
     */
    @IgnoreResponseAdvice
    @PostMapping("/register")
    public JwtToken register(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception {
        log.info("register user with param: [{}]", JSON.toJSONString(usernameAndPassword));
        return new JwtToken(ijwtService.registerUserAndGenerateToken(usernameAndPassword));
    }

    /**
     * 解析 token
     *
     * @See TokenParseUtil
     */
    public void test(String token) throws Exception {
        TokenParseUtil.parseUserInfoFromToken(token);
    }
}
