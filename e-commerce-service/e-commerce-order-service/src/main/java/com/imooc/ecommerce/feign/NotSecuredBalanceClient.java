package com.imooc.ecommerce.feign;

import com.imooc.ecommerce.balanceInfo.BalanceInfo;
import com.imooc.ecommerce.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <h1>用户账户服务 Feign 接口</h1>
 */
@FeignClient(
        contextId = "NotSecuredBalanceClient",
        value = "e-commerce-account-service"
)
public interface NotSecuredBalanceClient {


    /**
     * 扣减用户余额
     *
     * @param balanceInfo
     * @return
     */
    @RequestMapping(
            value = "/ecommerce-account-service/balance/deduct-balance",
            method = RequestMethod.PUT
    )
    CommonResponse<BalanceInfo> deductBalance(@RequestBody BalanceInfo balanceInfo);
}
