package com.imooc.ecommerce.config;


import com.imooc.ecommerce.filter.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
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

    /**
     * 让 MVC 加载 Swagger 的静态资源
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INFO/resource");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INFO/resource");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INFO/resource/webjars");


        super.addResourceHandlers(registry);
    }
}
