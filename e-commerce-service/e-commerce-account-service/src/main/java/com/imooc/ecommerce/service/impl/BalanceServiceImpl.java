package com.imooc.ecommerce.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.imooc.ecommerce.balanceInfo.BalanceInfo;
import com.imooc.ecommerce.dao.EcommerceBalanceDao;
import com.imooc.ecommerce.entity.EcommerceBalance;
import com.imooc.ecommerce.filter.AccessContext;
import com.imooc.ecommerce.service.IBalanceService;
import com.imooc.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 用于余额相关服务接口实现
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BalanceServiceImpl implements IBalanceService {

    @Autowired
    private EcommerceBalanceDao ecommerceBalanceDao;


    @Override
    public BalanceInfo getCurrentUserBalanceInfo() {
        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();

        BalanceInfo balanceInfo = new BalanceInfo(loginUserInfo.getId(), 0L);
        EcommerceBalance ecommerceBalance = ecommerceBalanceDao.findByUserId(loginUserInfo.getId());
        if (Objects.nonNull(ecommerceBalance)) {
            balanceInfo.setBalance(ecommerceBalance.getBalance());
        } else {
            // 如果没有余额记录，这里创建出来，余额设置为0
            EcommerceBalance ecommerceBalance1 = new EcommerceBalance();
            ecommerceBalance1.setUserId(loginUserInfo.getId());
            ecommerceBalance1.setBalance(0L);
            ecommerceBalanceDao.save(ecommerceBalance1);
            log.info("init user balance record: [{}]", JSONObject.toJSONString(ecommerceBalance1));
        }
        return balanceInfo;
    }

    @Override
    public BalanceInfo deductBalance(BalanceInfo balanceInfo) {

        LoginUserInfo loginUserInfo = AccessContext.getLoginUserInfo();

        // 扣减用户余额 基本规则： 扣减额 <= 当前用户余额
        EcommerceBalance ecommerceBalance = ecommerceBalanceDao.findByUserId(loginUserInfo.getId());
        if (Objects.isNull(ecommerceBalance) || ecommerceBalance.getBalance() < balanceInfo.getBalance()) {
            throw new RuntimeException("user balance is not enough");
        }

        Long sourceBalance = ecommerceBalance.getBalance();
        ecommerceBalance.setBalance(sourceBalance - balanceInfo.getBalance());
        log.info("deduct balance : [{}], [{}], [{}]", ecommerceBalanceDao.save(ecommerceBalance).getId(), sourceBalance, balanceInfo.getBalance());

        return new BalanceInfo(loginUserInfo.getId(), ecommerceBalance.getBalance());
    }
}
