package com.imooc.ecommerce.filter.factory;


import com.imooc.ecommerce.filter.HeaderTokenGatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;


/**
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
@Component
public class HeaderTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return new HeaderTokenGatewayFilter();
    }
}
