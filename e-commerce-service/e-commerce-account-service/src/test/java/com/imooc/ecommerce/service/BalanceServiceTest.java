package com.imooc.ecommerce.service;

import com.alibaba.fastjson.JSONObject;
import com.imooc.ecommerce.balanceInfo.BalanceInfo;
import com.imooc.ecommerce.filter.AccessContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BalanceServiceTest  extends BaseTest{

    @Autowired
    private IBalanceService balanceService;

    @Test
    public void testGetCurrentUserBalanceInfo(){
        BalanceInfo currentUserBalanceInfo = balanceService.getCurrentUserBalanceInfo();
        log.info("test get current user balance info: [{}]", JSONObject.toJSONString(currentUserBalanceInfo));
    }

    @Test
    public void tesDeductBalance(){
        BalanceInfo balanceInfo = balanceService.deductBalance(new BalanceInfo(AccessContext.getLoginUserInfo().getId(), 100L));
    }

}
