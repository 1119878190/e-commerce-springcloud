package com.imooc.ecommerce.service.communication;

import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * <h1>使用 Feign 原生的 API ，而不是 OpenFeign = Feign + Ribbon</h1>
 */
@Slf4j
@Service
public class UseFeignApi {

    /**
     * 服务发现
     */
    @Resource
    private DiscoveryClient discoveryClient;

    /**
     * <h2>使用 Feign 的原生 API 调用远端服务</h2>
     * Feign 默认配置初始化，设置自定义配置，生成代理对象
     *
     * @return
     */
    public JwtToken thinkingInFeign(UsernameAndPassword usernameAndPassword) {

        // 通过反射去拿到 service-id
        String serviceId = null;
        Annotation[] annotations = AuthorityFeignClient.class.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(FeignClient.class)) {
                serviceId = ((FeignClient) annotation).value();
                log.info("get service id from AuthorityFeignClient : [{}]", serviceId);
                break;
            }
        }
        // 如果服务id不存在，直接抛出异常
        if (Objects.isNull(serviceId)) {
            throw new RuntimeException("can not get serviceId");
        }

        // 通过 serviceId 拿到实例
        List<ServiceInstance> targetInstances = discoveryClient.getInstances(serviceId);
        if (CollectionUtils.isEmpty(targetInstances)) {
            throw new RuntimeException(("can not get target instance from serviceId: " + serviceId));
        }

        // 随机选择一个服务实例
        ServiceInstance serviceInstance = targetInstances.get(
                new Random().nextInt(targetInstances.size())
        );
        log.info("choose service instance : [{}],[{}],[{}]", serviceId, serviceInstance.getHost(), serviceInstance.getPort());

        // Feign 客户端初始化 ，必须配置 encoder 、 decoder 、contract
        AuthorityFeignClient feignClient = Feign.builder()    // 1.Feign 默认配置初始化
                .encoder(new GsonEncoder())                    // 2.1 设置自定义配置
                .decoder(new GsonDecoder())                     // 2.2 设置自定义配置
                .logLevel(Logger.Level.FULL)                    // 2.3 设置自定义配置
                .contract(new SpringMvcContract())
                .target(                                        // 3.生成代理对象
                        AuthorityFeignClient.class,
                        String.format("http://%s:%s", serviceInstance.getHost(), serviceInstance.getPort())
                );

        return feignClient.getTokenByFeign(usernameAndPassword);

    }
}
