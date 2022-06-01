package com.imooc.ecommerce.filter;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.constant.CommonConstant;
import com.imooc.ecommerce.constant.GatewayConstant;
import com.imooc.ecommerce.util.TokenParseUtil;
import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.LoginUserInfo;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 全局登录鉴权过滤器
 */
@Slf4j
@Component
public class GlobalLoginOrRegisterFilter implements GlobalFilter, Ordered {


    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 登录，注册，鉴权
     * 1.如果时登录或注册，则去授权中心拿到 token 并返给客户端
     * 2.如果时访问其他的服务，则鉴权，没有权限 返回 401
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 1.如果是登录
        if (request.getURI().getPath().contains(GatewayConstant.LOGIN_URI)) {
            // 去授权中心拿 token
            String token = getTokenFromAuthorityCenter(
                    request, GatewayConstant.AUTHORITY_CENTER_TOKEN_URI_FORMAT
            );
            // 往header写入 中不能设置null
            response.getHeaders().add(
                    CommonConstant.JWT_USER_INFO_KEY,
                    null == token ? "null" : token
            );
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        // 2.如果是注册
        if (request.getURI().getPath().contains(GatewayConstant.REGISTER_URI)) {
            // 去授权中心拿token: 先创建用户 ，再返回token
            String token = getTokenFromAuthorityCenter(
                    request,
                    GatewayConstant.AUTHORITY_CENTER_REGISTER_URI_FORMAT
            );
            response.getHeaders().add(
                    CommonConstant.JWT_USER_INFO_KEY,
                    null == token ? "null" : token
            );
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        // 3.访问其它的服务，则鉴权，校验是否能够从 token 中解析出用户信息
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(CommonConstant.JWT_USER_INFO_KEY);
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
        } catch (Exception e) {
            log.error("parse user info from token error:[{}]", e.getMessage(), e);
        }

        // 获取不到登录用户信息,返回 401
        if (null == loginUserInfo) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 解析通过，则放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 一定要比 GlobalCacheRequestBodyFilter 优先级低
        return HIGHEST_PRECEDENCE + 2;
    }

    /**
     * 从 授权中心获取 token
     *
     * @param request
     * @param uriFormat
     * @return
     */
    private String getTokenFromAuthorityCenter(ServerHttpRequest request, String uriFormat) {

        // service id 就是服务名字，负载均衡
        ServiceInstance serviceInstance = loadBalancerClient.choose(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
        log.info("Nacos Client Info: [{}],[{}],[{}]",
                serviceInstance.getServiceId(), serviceInstance.getInstanceId(), JSON.toJSONString(serviceInstance.getMetadata()));

        // 格式化请求地址url
        String requestUrl = String.format(uriFormat, serviceInstance.getHost(), serviceInstance.getPort());
        // 拿到请求体中的数据
        UsernameAndPassword requestBody = JSON.parseObject(parseBodyFromRequest(request), UsernameAndPassword.class);
        log.info("login request url and body :[{}],[{}]", requestUrl, JSON.toJSONString(requestBody));

        // 调用授权中心获取token
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        JwtToken jwtToken = restTemplate.postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(requestBody), httpHeaders),
                JwtToken.class
        );

        if (Objects.nonNull(jwtToken)) {
            return jwtToken.getToken();
        }
        return null;
    }

    private String parseBodyFromRequest(ServerHttpRequest request) {
        // 获取请求体
        Flux<DataBuffer> body = request.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();

        // 订阅缓冲区去消费请求提中的数据 GlobalCacheRequestBodyFilter
        body.subscribe(buffer -> {
            CharBuffer charbuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            // 一定要使用 DataBufferUtils.release 释放掉，否则 会出现内存泄露
            DataBufferUtils.release(buffer);
            bodyRef.set(charbuffer.toString());
        });

        // 获取 request body
        return bodyRef.get();

    }
}
