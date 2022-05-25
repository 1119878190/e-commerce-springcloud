package com.imooc.ecommerce.dao;

import com.imooc.ecommerce.entity.EcommerceUser;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * EcommerceUserDao 接口定义
 */
public interface EcommerceUserDao extends JpaRepository<EcommerceUser, Long> {


    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return
     */
    EcommerceUser findByUsername(String username);

    /**
     * 根据用户名 密码查询用户信息
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    EcommerceUser findByUsernameAndPassword(String username, String password);
}
