package com.imooc.ecommerce.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 通过 nacos 下发 动态路由配置，监听 nacos 中路由配置变更
 *
 * @DependsOn("GatewayConfig") 依赖 GatewayConfig类，会在 GatewayConfig 后加载
 */
@Slf4j
@Component
@DependsOn({"gatewayConfig"})
public class DynamicRouteServiceImplByNacos {

    /**
     * Nacos 配置服务
     */
    private ConfigService configService;

    private final DynamicRouteServiceImpl dynamicRouteService;

    public DynamicRouteServiceImplByNacos(DynamicRouteServiceImpl dynamicRouteService) {
        this.dynamicRouteService = dynamicRouteService;
    }


    /**
     * Bean 在 容器中构造完成之后会执行 init 方法
     */
    @PostConstruct
    public void init() {
        log.info("gateway route init .....");

        try {
            // 初始化 Nacos 配置客户端
            configService = initConfigService();
            if (Objects.isNull(configService)) {
                log.error("init config service fail");
                return;
            }
            // 通过 Nacos Config 并指定路由配置路径去获取路由配置
            String configInfo = configService.getConfig(
                    GatewayConfig.NACOS_ROUTE_DATA_ID,
                    GatewayConfig.NACOS_ROUTE_GROUP,
                    GatewayConfig.DEFAULT_TIMEOUT
            );
            log.info("get current gateway config : [{}]", configInfo);
            List<RouteDefinition> routeDefinitions = JSON.parseArray(configInfo, RouteDefinition.class);
            if (CollectionUtils.isNotEmpty(routeDefinitions)) {
                for (RouteDefinition routeDefinition : routeDefinitions) {
                    log.info("init gateway config: [{}]", routeDefinition.toString());
                    dynamicRouteService.addRouteDefinition(routeDefinition);
                }
            }

        } catch (Exception e) {
            log.error("gateway route init has some error: [{}]", e.getMessage());
        }

        // 设置监听器 如果发生更改，同步到gateway中
        dynamicRouteByNacosListener(GatewayConfig.NACOS_ROUTE_DATA_ID,GatewayConfig.NACOS_ROUTE_GROUP);
    }


    /**
     * 初始化 Nacos Config
     *
     * @return
     */
    private ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);
            return configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            log.error("init gateway nacos config error: [{}]", e.getMessage());
            return null;
        }
    }


    /**
     * 监听 Nacos 下发的动态路由配置信息
     *
     * @param dateId
     * @param group
     */
    private void dynamicRouteByNacosListener(String dateId, String group) {
        try {
            // 给 Nacos Config 客户端增加一个监听器
            configService.addListener(dateId, group, new Listener() {

                /**
                 * 字节提供线程池执行操作，可以不指定
                 * @return
                 */
                @Override
                public Executor getExecutor() {
                    return null;
                }

                /**
                 * 监听器收到配置更新
                 *
                 * @param configInfo Nacos 中最新的配置定义
                 */
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("start to update config: [{}]", configInfo);
                    List<RouteDefinition> routeDefinitions = JSON.parseArray(configInfo, RouteDefinition.class);
                    log.info("update route : [{}]", routeDefinitions.toString());
                    dynamicRouteService.updateList(routeDefinitions);
                }
            });


        } catch (NacosException e) {
            log.error("dynamic update gateway config error: [{}]", e.getMessage(), e);
        }
    }

}
