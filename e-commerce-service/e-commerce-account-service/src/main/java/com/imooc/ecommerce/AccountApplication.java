package com.imooc.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * swagger：127.0.0.1:8003/ecommerce-account-service/swagger-ui.html
 * swagger：127.0.0.1:8003/ecommerce-account-service/doc.html
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
}
