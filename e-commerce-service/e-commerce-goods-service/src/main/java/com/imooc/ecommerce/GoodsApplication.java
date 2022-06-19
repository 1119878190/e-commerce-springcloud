package com.imooc.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 商品微服务启动入口
 * 启动依赖组件/中间件： redis mysql nacos kafka zipkin
 * swagger: http://127.0.0.1:8001/ecommerce-goods-service/doc.html
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class, args);
    }
}
