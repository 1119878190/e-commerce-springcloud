package com.imooc.ecommerce.controller;

import com.imooc.ecommerce.service.communication.UseRestTemplateService;
import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>微服务通信 Controller</h1>
 */
@RestController
@RequestMapping("/communication")
public class CommunicationController {

    private final UseRestTemplateService useRestTemplateService;

    public CommunicationController(UseRestTemplateService useRestTemplateService) {
        this.useRestTemplateService = useRestTemplateService;
    }

    @PostMapping("/rest-template")
    public JwtToken getTokenFromAuthorityService(@RequestBody UsernameAndPassword usernameAndPassword) {
        return useRestTemplateService.getTokenFromAuthorityService(usernameAndPassword);
    }

    @PostMapping("/rest-template-load-balancer")
    public JwtToken getTokenFromAuthorityServiceWithLoadBalancer(@RequestBody UsernameAndPassword usernameAndPassword) {
        return useRestTemplateService.getTokenFromAuthorityServiceWithLoadBalancer(usernameAndPassword);
    }

}
