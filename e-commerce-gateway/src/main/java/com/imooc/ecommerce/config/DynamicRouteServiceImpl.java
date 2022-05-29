package com.imooc.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 事件推送 Aware：动态更新路由网关 Service
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    /**
     * 写路由定义
     */
    private final RouteDefinitionWriter routeDefinitionWriter;

    /**
     * 获取路由定义
     */
    private final RouteDefinitionLocator routeDefinitionLocator;

    /**
     * 事件发布
     */
    private ApplicationEventPublisher publisher;

    public DynamicRouteServiceImpl(RouteDefinitionWriter routeDefinitionWriter, RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        // 完成时间推送句柄的初始化
        this.publisher = applicationEventPublisher;
    }


    /**
     * 增加路由定义
     *
     * @param definition
     * @return
     */
    public String addRouteDefinition(RouteDefinition definition) {

        log.info("gateway add route : [{}]", definition);

        // 保存路由配置并发布
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        // 发布时间通知给 Gateway 同步新增的路由定义
        this.publisher.publishEvent(new RefreshRoutesEvent(this));

        return "success";
    }

    /**
     * 更新路由
     *
     * @param definitions
     * @return
     */
    public String updateList(List<RouteDefinition> definitions) {
        log.info("gateway update route:[{}]", definitions);
        // 先拿到当前 Gateway 中存储的路由定义  删除缓存routerDefinition
        List<RouteDefinition> oldRouteDefinitions = routeDefinitionLocator.getRouteDefinitions().buffer().blockFirst();
        if (CollectionUtils.isNotEmpty(oldRouteDefinitions)) {
            // 删除缓存routerDefinition
            oldRouteDefinitions.forEach(definition -> {
                log.info("delete route definition : [{}]", definition);
                deleteById(definition.getId());
            });
        }

        // 把更新的路由定义同步到 gateway中
        definitions.forEach(definition -> updateByRouteDefinition(definition));
        return "success";
    }

    /**
     * 根据路由id 删除路由配置
     *
     * @param id
     * @return
     */
    private String deleteById(String id) {
        try {
            log.info("gateway delete route id: [{}]", id);
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
            // 发送事件通知给 gateway 更新路由定义
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "delete success";
        } catch (Exception e) {
            log.info("gateway delete route fail : [{}]", e.getMessage());
            return "delete fail";
        }
    }

    /**
     * 更新路由
     * <p>
     * 更新的实现策略比较简单： 删除 + 新增 = 更新
     *
     * @param definition
     * @return
     */
    private String updateByRouteDefinition(RouteDefinition definition) {

        try {
            log.info("gateway update route : [{}]", definition);
            this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        } catch (Exception e) {
            log.error("update fail ,not find route routeId: [{}]", definition.getId());
            return "update fail ,not find route routeId: " + definition.getId();
        }

        try {
            log.info("==================update=====================");
            this.routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception e) {
            return "update route fail ";
        }

    }
}
