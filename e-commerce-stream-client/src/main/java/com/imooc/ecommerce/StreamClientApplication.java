package com.imooc.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * <h1>基于 Spring Cloud stream 构建消息驱动微服务</h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 21:15
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class StreamClientApplication {

    public static void main(String[] args) {

        SpringApplication.run(StreamClientApplication.class, args);
    }
}
