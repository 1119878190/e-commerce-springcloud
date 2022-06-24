package com.imooc.ecommerce.controller;

import com.imooc.ecommerce.service.communication.UseRestTemplateService;
import com.imooc.ecommerce.service.communication.UseRibbonService;
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
    private final UseRibbonService useRibbonService;

    public CommunicationController(UseRestTemplateService useRestTemplateService, UseRibbonService useRibbonService) {
        this.useRestTemplateService = useRestTemplateService;
        this.useRibbonService = useRibbonService;
    }

    @PostMapping("/rest-template")
    public JwtToken getTokenFromAuthorityService(@RequestBody UsernameAndPassword usernameAndPassword) {
        return useRestTemplateService.getTokenFromAuthorityService(usernameAndPassword);
    }

    @PostMapping("/rest-template-load-balancer")
    public JwtToken getTokenFromAuthorityServiceWithLoadBalancer(@RequestBody UsernameAndPassword usernameAndPassword) {
        return useRestTemplateService.getTokenFromAuthorityServiceWithLoadBalancer(usernameAndPassword);
    }

    @PostMapping("/ribbon")
    public JwtToken getTokenFromAuthorityServiceByRibbon(@RequestBody UsernameAndPassword usernameAndPassword) {
        return useRibbonService.getTokenFromAuthorityServiceByRibbon(usernameAndPassword);
    }

    @PostMapping("/thinkInRibbon")
    public JwtToken thinkInRibbon(@RequestBody UsernameAndPassword usernameAndPassword){
        return useRibbonService.thinkingInRibbon(usernameAndPassword);
    }
}
