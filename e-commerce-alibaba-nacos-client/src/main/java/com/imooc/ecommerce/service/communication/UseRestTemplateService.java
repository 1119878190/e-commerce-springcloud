package com.imooc.ecommerce.service.communication;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.constant.CommonConstant;
import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * <h1>使用 RestTemplate 实现微服务通信方式</h1>
 */
@Slf4j
@Service
public class UseRestTemplateService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;


    /**
     * <h2>从授权服务中获取 JwtToken</h2>
     *
     * @param usernameAndPassword
     * @return
     */
    public JwtToken getTokenFromAuthorityService(UsernameAndPassword usernameAndPassword) {

        // 第一种方案：写死url
        String requestUtl = "http://127.0.0.1:7000/ecommerce-authority-center/authority/token";
        log.info("RestTemplate request url and body:[{}],[{}]",
                requestUtl, JSON.toJSONString(usernameAndPassword));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new RestTemplate().postForObject(
                requestUtl,
                new HttpEntity<>(JSON.toJSONString(usernameAndPassword), httpHeaders),
                JwtToken.class
        );

    }


    /**
     * <h2>从授权服务中获取 JwtToken ，且带有负载均衡</h2>
     *
     * @param usernameAndPassword
     * @return
     */
    public JwtToken getTokenFromAuthorityServiceWithLoadBalancer(UsernameAndPassword usernameAndPassword) {

        // 第二种方案：通过注册中心拿到服务器的信息(是所有的示例)，选择一个，再去发起调用
        ServiceInstance serviceInstance = loadBalancerClient.choose(
                CommonConstant.AUTHORITY_CENTER_SERVICE_ID
        );
        log.info("Nacos Client info: [{}],[{}],[{}]",
                serviceInstance.getServiceId(), serviceInstance.getInstanceId(),
                JSON.toJSONString(serviceInstance.getMetadata()));

        String requestUrl = String.format(
                "http://%s:%s/ecommerce-authority-center/authority/token",
                serviceInstance.getHost(),
                serviceInstance.getPort()
        );
        log.info("login request url and body: [{}],[{}]", requestUrl, JSON.toJSONString(usernameAndPassword));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new RestTemplate().postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(usernameAndPassword), httpHeaders),
                JwtToken.class
        );
    }


}
