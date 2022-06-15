package com.imooc.ecommerce.service;

import com.imooc.ecommerce.balanceInfo.BalanceInfo;

/**
 * 余额相关的服务接口定义
 */
public interface IBalanceService {

    /**
     * 获取当前用户余额信息
     *
     * @return
     */
    BalanceInfo getCurrentUserBalanceInfo();

    /**
     * 扣减用户余额
     *
     * @param balanceInfo 代表想要扣减的余额
     * @return
     */
    BalanceInfo deductBalance(BalanceInfo balanceInfo);
}
