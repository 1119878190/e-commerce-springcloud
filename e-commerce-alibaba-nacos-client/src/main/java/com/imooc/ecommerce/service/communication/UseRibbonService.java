package com.imooc.ecommerce.service.communication;


import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.constant.CommonConstant;
import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Ribbon 实现微服务通信
 */
@Slf4j
@Service
public class UseRibbonService {

    private final RestTemplate restTemplate;

    @Resource
    private DiscoveryClient discoveryClient;

    public UseRibbonService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <h2>通过 Ribbon 调用 Authority 服务获取 Token</h2>
     *
     * @param usernameAndPassword
     * @return
     */
    public JwtToken getTokenFromAuthorityServiceByRibbon(UsernameAndPassword usernameAndPassword) {


        // 注意到 url 中的 ip 和端口换成了服务名称
        String requestUrl = String.format(
                "http://%s/ecommerce-authority-center/authority/token",
                CommonConstant.AUTHORITY_CENTER_SERVICE_ID
        );
        log.info("login request url and body: [{}],[{}]", requestUrl, JSON.toJSONString(usernameAndPassword));


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // 这里一定要使用自己注入的 RestTemplate,不能new
        return restTemplate.postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(usernameAndPassword), httpHeaders),
                JwtToken.class
        );

    }


    /**
     * 使用原生的 Ribbon Api 看看 Ribbon 是如何完成： 服务调用+ 负载均衡
     *
     * @param usernameAndPassword
     * @return
     */
    public JwtToken thinkingInRibbon(UsernameAndPassword usernameAndPassword) {

        String urlFormat = "http://%s/ecommerce-authority-center/authority/token";

        // 1.找到服务提供方的地址和端口
        List<ServiceInstance> targetInstances = discoveryClient.getInstances(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);

        // 构造 Ribbon 服务列表
        ArrayList<Server> servers = new ArrayList<>(targetInstances.size());
        for (ServiceInstance targetInstance : targetInstances) {
            servers.add(new Server(targetInstance.getHost(), targetInstance.getPort()));
            log.info("found target instance : [{}] -> [{}]", targetInstance.getHost(), targetInstance.getPort());
        }

        // 2.使用负载均衡策略实现远端服务调用
        BaseLoadBalancer loadBalancer = LoadBalancerBuilder.newBuilder()
                .buildFixedServerListLoadBalancer(servers);
        // 设置负载均衡策略
        loadBalancer.setRule(new RetryRule(new RandomRule(), 300));

        String result = LoadBalancerCommand.builder().withLoadBalancer(loadBalancer)
                .build().submit(server -> {
                    String targetUrl = String.format(
                            urlFormat,
                            String.format("%s:%s", server.getHost(), server.getPort())
                    );
                    log.info("target request url: [{}]", targetUrl);

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                    String tokenStr = new RestTemplate().postForObject(
                            targetUrl,
                            new HttpEntity<>(JSON.toJSONString(usernameAndPassword), httpHeaders),
                            String.class
                    );
                    return Observable.just(tokenStr);
                }).toBlocking().first().toString();

        return JSON.parseObject(result, JwtToken.class);

    }
}
