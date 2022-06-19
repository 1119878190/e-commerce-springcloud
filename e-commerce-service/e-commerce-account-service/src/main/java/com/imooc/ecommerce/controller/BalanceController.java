package com.imooc.ecommerce.controller;

import com.imooc.ecommerce.balanceInfo.BalanceInfo;
import com.imooc.ecommerce.service.IBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户余额服务 controller
 */
@Api(tags = "用户余额服务")
@Slf4j
@RestController
@RequestMapping("/balance")
public class BalanceController {

    @Resource
    private IBalanceService balanceService;


    @ApiOperation(value = "当前用户",notes = "获取当前用户余额信息",httpMethod = "GET")
    @GetMapping("/currentBalance")
    public BalanceInfo getCurrentUserBalanceInfo(){
        return balanceService.getCurrentUserBalanceInfo();
    }

    @ApiOperation(value = "扣减",notes = "扣减用户余额",httpMethod = "PUT")
    @PutMapping("/deductBalance")
    public BalanceInfo deductBalance(@RequestBody BalanceInfo balanceInfo){
        return balanceService.deductBalance(balanceInfo);
    }


}
