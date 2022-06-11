package com.imooc.ecommerce.filter;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * HTTP 请求头部携带  Token 验证过滤器
 * @see com.imooc.ecommerce.filter.factory.HeaderTokenGatewayFilterFactory 中 返回了该filter 并在路由配置文件中json指定了改过滤器
 */
public class HeaderTokenGatewayFilter implements GatewayFilter, Ordered {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从 http header 中寻找 key 为 token, value 为 imooc 的键值对
        String name = exchange.getRequest().getHeaders().getFirst("token");
        if ("imooc".equals(name)){
            return chain.filter(exchange);
        }

        // 如果没有，则标记此次请求没有权限，并结束这次请求
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2;
    }
}
