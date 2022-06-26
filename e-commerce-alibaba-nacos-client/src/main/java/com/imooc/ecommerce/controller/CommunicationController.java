package com.imooc.ecommerce.controller;

import com.imooc.ecommerce.service.communication.AuthorityFeignClient;
import com.imooc.ecommerce.service.communication.UseFeignApi;
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
    private AuthorityFeignClient authorityFeignClient;
    private UseFeignApi useFeignApi;

    public CommunicationController(UseRestTemplateService useRestTemplateService,
                                   UseRibbonService useRibbonService,
                                   AuthorityFeignClient authorityFeignClient,
                                   UseFeignApi useFeignApi) {
        this.useRestTemplateService = useRestTemplateService;
        this.useRibbonService = useRibbonService;
        this.authorityFeignClient = authorityFeignClient;
        this.useFeignApi = useFeignApi;
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
    public JwtToken thinkInRibbon(@RequestBody UsernameAndPassword usernameAndPassword) {
        return useRibbonService.thinkingInRibbon(usernameAndPassword);
    }

    @PostMapping("/token-by-feign")
    public JwtToken getTokenByFeign(@RequestBody UsernameAndPassword usernameAndPassword) {
        return authorityFeignClient.getTokenByFeign(usernameAndPassword);
    }

    @PostMapping("/thinking-in-feign")
    public JwtToken thinkingInFeign(@RequestBody UsernameAndPassword usernameAndPassword) {
        return useFeignApi.thinkingInFeign(usernameAndPassword);
    }
}
