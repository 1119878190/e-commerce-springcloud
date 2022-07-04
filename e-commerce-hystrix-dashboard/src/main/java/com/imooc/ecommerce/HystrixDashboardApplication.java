package com.imooc.ecommerce;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * <h1>Hystrix dashboard 入口</h1>
 *
 * http://localhost:9999/ecommerce-hystrix-dashboard/hystrix
 * http://127.0.0.1:8000/ecommerce-nacos-client/actuator/hystrix.stream
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard // 开启 HystrixDashboard
public class HystrixDashboardApplication {

    public static void main(String[] args) {

        SpringApplication.run(HystrixDashboardApplication.class, args);

    }

}
