package com.imooc.ecommerce.service.communication;


import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * <h1>OpenFeign 配置类</h1>
 */
@Configuration
public class FeignConfig {

    /**
     * <h2>开始 OpenFeign 日志</h2>
     *
     * @return
     */
    @Bean
    public Logger.Level feignLogger() {
        return Logger.Level.FULL; // 需要注意，日志级别需要修改成 debug
    }


    /**
     * <h2>OpenFeign 开启重试</h2>
     * <p>
     * period = 100 发起当前请求的时间间隔 ，单位是 ms
     * maxPeriod = 1000 发起当前请求的最大时间间隔，单位是ms
     * maxAttempts = 5 最多亲求次数
     *
     * @return
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(
                100,
                SECONDS.toMillis(1),
                5
        );
    }


    public static final int CONNECT_TIME_MILLS = 5000;
    public static final int READ_TIME_MILLS = 5000;

    /**
     * <h2>对亲求的连接和响应时间进行限制</h2>
     *
     * @return
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(
                CONNECT_TIME_MILLS, TimeUnit.MILLISECONDS,
                READ_TIME_MILLS, TimeUnit.MILLISECONDS,
                true
        );
    }

}
