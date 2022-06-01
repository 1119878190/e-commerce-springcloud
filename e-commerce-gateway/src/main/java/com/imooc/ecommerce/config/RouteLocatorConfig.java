package com.imooc.ecommerce.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置登录请求转发规则
 * 配置路由规则有两种方式 1.配置文件  2.代码
 *
 *
 *
 * 配置文件方式：
 * [
 *   {
 *     "id": "e-commerce-nacos-client",
 *     "predicates": [
 *       {
 *         "args": {
 *           // /imooc为网关的context path ,ecommerce-nacos-client为nacos服务的contextPath
 *           "pattern": "/imooc/ecommerce-nacos-client/**"
 *         },
 *         "name": "Path"
 *       }
 *     ],
 *     // 转发到 nacos注册服务 servr-id
 *     "uri": "lb://e-commerce-nacos-client",
 *     "filters": [
 *       {
 *         // 自定义的局部过滤器
 *         "name": "HeaderToken"
 *       },
 *       {
 *         // 内置局部过滤器，跳过前缀 /imooc
 *         "name": "StripPrefix",
 *         "args": {
 *           "parts": "1"
 *         }
 *       }
 *     ]
 *   }
 * ]
 */
@Configuration
public class RouteLocatorConfig {

    /**
     * 使用代码定义路由规则，在网关层面拦截下登录和注册接口
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator loginRouteLocator(RouteLocatorBuilder builder) {

        // /imooc/e-commerce/login，/imooc/e-commerce-register 配置登录和注册转发到网关
        return builder.routes()
                .route(
                        "e-commerce-authority",
                        r -> r.path(
                                "/imooc/e-commerce/login",
                                "/imooc/e-commerce/register"
                        ).uri("http://localhost:9001")
                ).build();
    }
}
