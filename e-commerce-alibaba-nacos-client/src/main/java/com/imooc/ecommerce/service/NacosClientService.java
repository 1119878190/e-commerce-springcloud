package com.imooc.ecommerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NacosClientService {


    /**
     * 服务发现
     */
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 获取 Nacos 的注册列表
     */
    public List<ServiceInstance> getServiceInstances(String serverId) {

        // 测试 UseHystrixCommandAnnotation 的超时
//        try {
//            Thread.sleep(2000);
//        }catch (InterruptedException exception){
//
//        }

        // 测试 NacosClientHystrixCommand 熔断
        throw new RuntimeException("has exception");

//        log.info("request nacos client to get service instance info: [{}]", serverId);
//        return discoveryClient.getInstances(serverId);
    }
}
