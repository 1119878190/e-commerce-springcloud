package com.imooc.ecommerce.controller;


import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.service.NacosClientService;
import com.imooc.ecommerce.service.hystrix.NacosClientHystrixCommand;
import com.imooc.ecommerce.service.hystrix.UseHystrixCommandAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <h1>Hystrix Controller</h1>
 */
@Slf4j
@RestController
@RequestMapping("/hystrix")
public class HystrixController {


    @Resource
    private UseHystrixCommandAnnotation useHystrixCommandAnnotation;

    @Resource
    private NacosClientService nacosClientService;

    /**
     * 通过注解的方式实现Hystrix 熔断降级
     *
     * @param serviceId
     * @return
     */
    @GetMapping("/hystrix-command-annotation")
    public List<ServiceInstance> getNacosClientInfoUseAnnotation(@RequestParam String serviceId) {
        log.info("request nacos client info use annotation : [{}],[{}]", serviceId, Thread.currentThread().getName());
        return useHystrixCommandAnnotation.getNacosClientInfo(serviceId);
    }


    /**
     * 通过编程的方式实现Hystrix 熔断降级
     *
     * @param serviceId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/simple-hystrix-command")
    public List<ServiceInstance> getServiceInstanceByServiceId(@RequestParam String serviceId) throws ExecutionException, InterruptedException {

        // 第一种方式  同步阻塞  （execute  = queue + get）
        List<ServiceInstance> serviceInstance01 = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).execute();  // 同步阻塞
        log.info("use execute to get service instances: [{}],[{}]", JSON.toJSONString(serviceInstance01), Thread.currentThread().getName());


        // 第二种方式  异步非阻塞（最常用到）
        List<ServiceInstance> serviceInstance02;
        Future<List<ServiceInstance>> future = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).queue(); // 异步非阻塞
        // 这里可以做一些别的事，需要的时候再拿结果
        serviceInstance02 = future.get();
        log.info("use queue to get service instances: [{}],[{}]", JSON.toJSONString(serviceInstance02), Thread.currentThread().getName());


        // 第三种方式  热响应调用 （启动了线程但没有执行，直到toBlocking才执行）
        Observable<List<ServiceInstance>> observable = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).observe();
        List<ServiceInstance> serviceInstances03 = observable.toBlocking().single();
        log.info("use observe to get service instances: [{}],[{}]", JSON.toJSONString(serviceInstances03), Thread.currentThread().getName());

        // 第四种方式  异步冷响应 （只有调用了toBlocking()方法才创建线程去执行）
        Observable<List<ServiceInstance>> toObservable = new NacosClientHystrixCommand(
                nacosClientService, serviceId
        ).toObservable();
        List<ServiceInstance> serviceInstances04 = toObservable.toBlocking().single();
        log.info("use toObservable to get service instances: [{}],[{}]", JSON.toJSONString(serviceInstances04), Thread.currentThread().getName());

        return serviceInstance01;
    }
}
