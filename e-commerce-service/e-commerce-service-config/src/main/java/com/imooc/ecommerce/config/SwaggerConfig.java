package com.imooc.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger 配置类
 * <p>
 * 原生： /swagger-ui.html
 * 美化： /doc.html
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {


    /**
     * Swagger 实例 Bean 时 Docket ，所以通过配置 Docket 实例来配置Swagger
     *
     * @return
     */
    @Bean
    public Docket docket() {

        return new Docket(DocumentationType.SWAGGER_2)
                // 展示在 Swagger 页面上的自定义工程描述信息
                .apiInfo(apiInfo())
                // 选择展示哪些接口
                .select()
                // 只有 com.imooc.ecommerce 包内的才去展示
                .apis(RequestHandlerSelectors.basePackage("com.imooc.ecommerce"))
                .paths(PathSelectors.any())
                .build();
    }


    /**
     * Swagger 的描述信息
     *
     * @return
     */
    public ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("imooc-micro-service")
                .description("e-commerce-springcloud-service")
                .contact(new Contact("lafe", "www.test.com", "test@qq.com"))
                .version("1.0")
                .build();

    }
}
