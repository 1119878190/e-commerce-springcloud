package com.imooc.ecommerce.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类 用于读取Nacos 相关的配置想，用于配置监听
 */
@Configuration
public class GatewayConfig {


    /**
     * 读取配置的超时时间
     */
    public static final long DEFAULT_TIMEOUT = 30000;

    /**
     * nacos 服务器地址
     */
    public static String NACOS_SERVER_ADDR;

    /**
     * 命名空间
     */
    public static String NACOS_NAMESPACE;

    /**
     * data-id  nacos中gateway配置信息的data-id
     */
    public static String NACOS_ROUTE_DATA_ID;

    /**
     * 分组 id  nacos中gateway配置信息的group
     */
    public static String NACOS_ROUTE_GROUP;

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    public void setNacosServerAddr(String nacosServerAddr) {
        NACOS_SERVER_ADDR = nacosServerAddr;
    }

    @Value("${spring.cloud.nacos.discovery.namespace}")
    public void setNacosNamespace(String nacosNamespace){
        NACOS_NAMESPACE = nacosNamespace;
    }

    @Value("${nacos.gateway.route.config.data-id}")
    public void setNacosRouteDataId(String nacosRouteDataId){
        NACOS_ROUTE_DATA_ID = nacosRouteDataId;
    }

    @Value("${nacos.gateway.route.config.group}")
    public void setNacosRouteGroup(String nacosRouteGroup){
        NACOS_ROUTE_GROUP = nacosRouteGroup;
    }
}
