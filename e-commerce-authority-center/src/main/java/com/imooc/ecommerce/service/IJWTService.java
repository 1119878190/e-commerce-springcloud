package com.imooc.ecommerce.service;


import com.imooc.ecommerce.vo.UsernameAndPassword;

/**
 * JWT 相关服务接口定义
 */
public interface IJWTService {


    /**
     * 生成 JWT Token ,使用默认的超时时间
     *
     * @param username 用户名
     * @param password 密码
     * @return
     * @throws Exception
     */
    String generateToken(String username, String password) throws Exception;

    /**
     * 生成 JWT Token , 可以指定超时时间  单位是天
     *
     * @param username 用户名
     * @param password 密码
     * @param expire   过期时间
     * @return
     * @throws Exception
     */
    String generateToken(String username, String password, int expire) throws Exception;


    /**
     * 注册用户 并生成TOKEN
     *
     * @param usernameAndPassword
     * @return TOKEN
     */
    String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception;
}
