package com.imooc.ecommerce.controller;


import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.service.NacosClientService;
import com.imooc.ecommerce.service.hystrix.*;
import com.imooc.ecommerce.service.hystrix.requestMerge.NacosClientCollapseCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Observer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Resource
    private CacheHystrixCommandAnnotation cacheHystrixCommandAnnotation;

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
     * 通过编程的方式实现Hystrix 熔断降级(线程池)
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

    /**
     * 通过编程的方式实现Hystrix 熔断降级(信号量)
     *
     * @param serviceId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/hystrix-observable-command")
    public List<ServiceInstance> getServiceInstanceByServiceIdObservable(@RequestParam String serviceId) {

        List<String> serviceIds = Arrays.asList(serviceId, serviceId, serviceId);

        List<List<ServiceInstance>> result = new ArrayList<>();
        NacosClientHystrixObservableCommand nacosClientHystrixObservableCommand = new NacosClientHystrixObservableCommand(nacosClientService, serviceIds);

        // 异步执行命令
        Observable<List<ServiceInstance>> observe = nacosClientHystrixObservableCommand.observe();

        // 注册获取结果
        observe.subscribe(new Observer<List<ServiceInstance>>() {
            // 执行 onNext 后再去执行 onCompleted
            @Override
            public void onCompleted() {
                log.info("all tasks is complete:[{}],[{}]", serviceId, Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onNext(List<ServiceInstance> serviceInstances) {
                result.add(serviceInstances);
            }
        });

        log.info("observable command result is : [{}],[{}]", JSON.toJSONString(result), Thread.currentThread().getName());
        return result.get(0);
    }

    /**
     * Hystrix 亲求缓存：一次controller调用中，多次调用service，只会发起一次
     *
     * @param serviceId
     */
    @GetMapping("/cache-hystrix-command")
    public void HystrixCommand(@RequestParam String serviceId) {

        // 使用缓存 command ，发起两次请求
        // 因为我们第二次传递了相同的key即serviceId，所以只执行了一次，第二次是通过缓存拿到信息
        CacheHystrixCommand cacheHystrixCommand1 = new CacheHystrixCommand(nacosClientService, serviceId);
        CacheHystrixCommand cacheHystrixCommand2 = new CacheHystrixCommand(nacosClientService, serviceId);

        List<ServiceInstance> result1 = cacheHystrixCommand1.execute();
        List<ServiceInstance> result2 = cacheHystrixCommand2.execute();


        log.info("result1,result2: [{}],[{}]", JSON.toJSONString(result1), JSON.toJSONString(result2));

        // 清除缓存
        CacheHystrixCommand.flushRequestCache(serviceId);


        CacheHystrixCommand cacheHystrixCommand3 = new CacheHystrixCommand(nacosClientService, serviceId);
        CacheHystrixCommand cacheHystrixCommand4 = new CacheHystrixCommand(nacosClientService, serviceId);
        List<ServiceInstance> result3 = cacheHystrixCommand3.execute();
        List<ServiceInstance> result4 = cacheHystrixCommand4.execute();
        log.info("result3,result4: [{}],[{}]", JSON.toJSONString(result3), JSON.toJSONString(result4));
    }


    @GetMapping("/cache-annotation-01")
    public List<ServiceInstance> useCacheByAnnotation01(@RequestParam String serviceId) {
        log.info("use cache by annotation01 to get nacos client info : [{}]", serviceId);
        List<ServiceInstance> result01 = cacheHystrixCommandAnnotation.useCacheByAnnotation01(serviceId);
        List<ServiceInstance> result02 = cacheHystrixCommandAnnotation.useCacheByAnnotation01(serviceId);
        // 清除缓存
        cacheHystrixCommandAnnotation.flushCacheByAnnotation01(serviceId);

        return cacheHystrixCommandAnnotation.useCacheByAnnotation01(serviceId);
    }

    @GetMapping("/cache-annotation-02")
    public List<ServiceInstance> useCacheByAnnotation02(@RequestParam String serviceId) {
        log.info("use cache by annotation02 to get nacos client info : [{}]", serviceId);
        List<ServiceInstance> result01 = cacheHystrixCommandAnnotation.useCacheByAnnotation02(serviceId);
        List<ServiceInstance> result02 = cacheHystrixCommandAnnotation.useCacheByAnnotation02(serviceId);
        // 清除缓存
        cacheHystrixCommandAnnotation.flushCacheByAnnotation02(serviceId);

        return cacheHystrixCommandAnnotation.useCacheByAnnotation02(serviceId);
    }

    @GetMapping("/cache-annotation-03")
    public List<ServiceInstance> useCacheByAnnotation03(@RequestParam String serviceId) {
        log.info("use cache by annotation03 to get nacos client info : [{}]", serviceId);
        List<ServiceInstance> result01 = cacheHystrixCommandAnnotation.useCacheByAnnotation03(serviceId);
        List<ServiceInstance> result02 = cacheHystrixCommandAnnotation.useCacheByAnnotation03(serviceId);
        // 清除缓存
        cacheHystrixCommandAnnotation.flushCacheByAnnotation03(serviceId);

        return cacheHystrixCommandAnnotation.useCacheByAnnotation03(serviceId);
    }

    /**
     * 编程实现请求合并
     */
    @GetMapping("/request-merge")
    public void requestMerge() throws ExecutionException, InterruptedException {

        // 这三个会被合并 我们设置的合并的超时时间为300ms
        NacosClientCollapseCommand collapseCommand01 = new NacosClientCollapseCommand(nacosClientService, "e-commerce-nacos-client1");
        NacosClientCollapseCommand collapseCommand02 = new NacosClientCollapseCommand(nacosClientService, "e-commerce-nacos-client2");
        NacosClientCollapseCommand collapseCommand03 = new NacosClientCollapseCommand(nacosClientService, "e-commerce-nacos-client3");

        Future<List<ServiceInstance>> future01 = collapseCommand01.queue();
        Future<List<ServiceInstance>> future02 = collapseCommand02.queue();
        Future<List<ServiceInstance>> future03 = collapseCommand03.queue();

        future01.get();
        future02.get();
        future03.get();

        Thread.sleep(2000);


        NacosClientCollapseCommand collapseCommand004 = new NacosClientCollapseCommand(nacosClientService, "e-commerce-nacos-client4");
        Future<List<ServiceInstance>> future04 = collapseCommand004.queue();
        future04.get();
    }


    /**
     * <h2>注解方式实现请求合并</h2>
     */
    @GetMapping("/request-merge-annotation")
    public void requestMergeAnnotation() throws ExecutionException, InterruptedException {

        Future<List<ServiceInstance>> future01 = nacosClientService.findNacosClientInfo("e-commerce-nacos-client1");
        Future<List<ServiceInstance>> future02 = nacosClientService.findNacosClientInfo("e-commerce-nacos-client2");
        Future<List<ServiceInstance>> future03 = nacosClientService.findNacosClientInfo("e-commerce-nacos-client3");

        future01.get();
        future02.get();
        future03.get();

        Thread.sleep(2000);

        Future<List<ServiceInstance>> future04 = nacosClientService.findNacosClientInfo("e-commerce-nacos-client4");
        future04.get();
    }

}
