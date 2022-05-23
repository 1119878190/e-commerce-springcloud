package com.imooc.ecommerce.controller;

import com.imooc.ecommerce.service.NacosClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/nacos-client")
@RestController
@Slf4j
public class NacosClientController {


    @Autowired
    private NacosClientService nacosClientService;


    /**
     * 根据 service id 获取服务所有的实例信息
     *
     * @param serviceId
     * @return
     */
    @GetMapping("/service-instance")
    public List<ServiceInstance> getServiceInstances(
            @RequestParam(defaultValue = "e-commerce-nacos-client") String serviceId) {

        log.info("coming in log nacos client info : [{}]", serviceId);
        return nacosClientService.getServiceInstances(serviceId);
    }


}
