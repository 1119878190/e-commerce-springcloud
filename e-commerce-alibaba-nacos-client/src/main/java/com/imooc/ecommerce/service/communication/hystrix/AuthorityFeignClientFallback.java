package com.imooc.ecommerce.service.communication.hystrix;


import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.service.communication.AuthorityFeignClient;
import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OpenFeign 集成 Hystrix 第一种方式 fallback
 * <h1> AuthorityFeignClient 后备 fallback</h1>
 * 实现 带有@FeignClient的接口服务中的方法，并指定该类为兜底类
 */
@Slf4j
@Component
public class AuthorityFeignClientFallback implements AuthorityFeignClient {

    @Override
    public JwtToken getTokenByFeign(UsernameAndPassword usernameAndPassword) {
        log.info("authority feign client get token by feign  request error (Hystrix Fallback):[{}]", JSON.toJSONString(usernameAndPassword));
        return new JwtToken("error");
    }
}
