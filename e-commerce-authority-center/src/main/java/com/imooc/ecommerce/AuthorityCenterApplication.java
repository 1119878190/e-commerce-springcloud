package com.imooc.ecommerce;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 授权中心启动入口
 *
 * @EnableJpaAuditing  允许JPA的自动审计 配置让jpa自动填入创建日期
 */
@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
public class AuthorityCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorityCenterApplication.class, args);
    }
}
