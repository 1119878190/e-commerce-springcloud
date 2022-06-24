package com.imooc.ecommerce.service.communication;

import com.imooc.ecommerce.vo.JwtToken;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <h1>与 Authority 服务通信的 Feign Client 接口定义</h1>
 *
 * @FeignClient value :注册服务中心的名称 serverId
 */
@FeignClient(contextId = "AuthorityFeignClient", value = "e-commerce-authority-center")
public interface AuthorityFeignClient {

    /**
     * <h2>通过 OpenFeign 访问 Authority 获取 Token </h2>
     * <p>
     * 路径： context-path + controller path
     *
     * @param usernameAndPassword
     * @return
     */
    @RequestMapping(value = "/ecommerce-authority-center/authority/token", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    JwtToken getTokenByFeign(@RequestBody UsernameAndPassword usernameAndPassword);

}
