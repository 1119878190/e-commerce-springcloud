package com.imooc.ecommerce.config;


import com.imooc.ecommerce.filter.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Web Mcv 配置
 */
@Configuration
public class ImoocWebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        // 添加用户身份统一登录拦截的拦截器
        registry.addInterceptor(new LoginUserInterceptor())
                .addPathPatterns("/**")
                .order(0);
    }
}
