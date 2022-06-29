package com.imooc.ecommerce.service.hystrix;

import com.imooc.ecommerce.service.NacosClientService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheRemove;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 使用注解方式开启 Hystrix 请求缓存
 */
@Slf4j
@Service
public class CacheHystrixCommandAnnotation {


    @Autowired
    private NacosClientService nacosClientService;


    // 第一种 Hystrix Cache 注解使用的方法 指定cacheKey方法，指定cacheRemove方法
    @CacheResult(cacheKeyMethod = "getCacheKey")
    @HystrixCommand(commandKey = "CacheHystrixCommandAnnotation")
    public List<ServiceInstance> useCacheByAnnotation01(String serviceId) {
        log.info("use cache01 to get nacos client info : [{}]", serviceId);
        return nacosClientService.getServiceInstances(serviceId);

    }

    public String getCacheKey(String cacheId) {
        return cacheId;
    }

    @CacheRemove(commandKey = "CacheHystrixCommandAnnotation", cacheKeyMethod = "getCacheKey")
    @HystrixCommand
    public void flushCacheByAnnotation01(String cacheId) {
        log.info("flush hystrix cache key : [{}]", cacheId);
    }


    // （------最常使用-------）第二种 Hystrix Cache 注解使用的方法 指定方法参数作为cacheKey
    @CacheResult
    @HystrixCommand(commandKey = "CacheHystrixCommandAnnotation")
    public List<ServiceInstance> useCacheByAnnotation02(@CacheKey String serviceId) {
        log.info("use cache02 to get nacos client info : [{}]", serviceId);
        return nacosClientService.getServiceInstances(serviceId);
    }

    @CacheRemove(commandKey = "CacheHystrixCommandAnnotation")
    @HystrixCommand
    public void flushCacheByAnnotation02(@CacheKey String cacheId) {
        log.info("flush hystrix cache key : [{}]", cacheId);
    }


    // 第三种 Hystrix Cache 注解使用的方法 没有指定cacheKey 默认是所有的入参
    @CacheResult
    @HystrixCommand(commandKey = "CacheHystrixCommandAnnotation")
    public List<ServiceInstance> useCacheByAnnotation03(String serviceId) {
        log.info("use cache03 to get nacos client info : [{}]", serviceId);
        return nacosClientService.getServiceInstances(serviceId);
    }

    @CacheRemove(commandKey = "CacheHystrixCommandAnnotation")
    @HystrixCommand
    public void flushCacheByAnnotation03(String cacheId) {
        log.info("flush hystrix cache key : [{}]", cacheId);
    }

}
