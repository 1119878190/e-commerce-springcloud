package com.imooc.ecommerce.dao;

import com.imooc.ecommerce.entity.EcommerceBalance;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * EcommerceBalance Dao 接口定义
 */
public interface EcommerceBalanceDao extends JpaRepository<EcommerceBalance, Long> {

    EcommerceBalance findByUserId(Long userId);
}
