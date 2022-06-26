package com.imooc.ecommerce.controller;


import com.imooc.ecommerce.service.hystrix.UseHystrixCommandAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <h1>Hystrix Controller</h1>
 */
@Slf4j
@RestController
@RequestMapping("/hystrix")
public class HystrixController {


    @Resource
    private UseHystrixCommandAnnotation useHystrixCommandAnnotation;

    @GetMapping("/hystrix-command-annotation")
    public List<ServiceInstance> getNacosClientInfoUseAnnotation(@RequestParam String serviceId) {
        log.info("request nacos client info use annotation : [{}],[{}]", serviceId, Thread.currentThread().getName());
        return useHystrixCommandAnnotation.getNacosClientInfo(serviceId);
    }
}
