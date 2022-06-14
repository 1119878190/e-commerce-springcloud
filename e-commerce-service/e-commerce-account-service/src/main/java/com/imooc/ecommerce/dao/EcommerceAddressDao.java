package com.imooc.ecommerce.dao;

import com.imooc.ecommerce.entity.EcommerceAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * EcommerceAddress Dao 接口定义
 */
public interface EcommerceAddressDao extends JpaRepository<EcommerceAddress, Long> {


    /**
     * 根据用户 id 查询地址信息
     *
     * @param userId 用户id
     * @return
     */
    List<EcommerceAddress> findAllByUserId(Long userId);
}
