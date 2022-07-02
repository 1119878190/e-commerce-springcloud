package com.imooc.ecommerce.service.communication.hystrix;


import com.imooc.ecommerce.service.communication.AuthorityFeignClient;
import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * OpenFeign 集成 Hystrix 第二种方式 fallbackFactory
 * <p>
 * 泛型为带有@FeignClient的接口服务,需要在其中指定fallbackFactory为该类
 */
@Slf4j
@Component
public class AuthorityFeignClientFallbackFactory implements FallbackFactory<AuthorityFeignClient> {

    @Override
    public AuthorityFeignClient create(Throwable throwable) {

        log.warn("authority feign client get token by feign request error (Hystrix FallbackFactory): [{}]", throwable.getMessage(), throwable);
        return new AuthorityFeignClient() {
            @Override
            public JwtToken getTokenByFeign(UsernameAndPassword usernameAndPassword) {
                return new JwtToken("error-fallbackFactory");
            }
        };
    }
}
