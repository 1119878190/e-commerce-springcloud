package com.imooc.ecommerce.service.hystrix;


import com.imooc.ecommerce.service.NacosClientService;
import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;

/**
 * <h1> NacosClientService 实现包装类</h1>
 * Hystrix 舱壁模式 ：
 * 1.线程池
 * 2.信号量： 算法 + 数据结构， 有限状态机
 */
@Slf4j
public class NacosClientHystrixCommand extends HystrixCommand<List<ServiceInstance>> {

    /**
     * 需要保护的服务
     */
    private final NacosClientService nacosClientService;

    /**
     * 方法需要传递的参数
     */
    private final String serviceId;


    public NacosClientHystrixCommand(NacosClientService nacosClientService, String serviceId) {

        super(
                Setter.withGroupKey(
                        HystrixCommandGroupKey.Factory.asKey("NacosClientService"))// 进行分组，便于统计，自定义
                        .andCommandKey(HystrixCommandKey.Factory.asKey("NacosClientServiceCommand"))
                        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("NacosClientPool"))
                        // 线程池 key 配置
                        .andCommandPropertiesDefaults(
                                HystrixCommandProperties.Setter()
                                        .withExecutionIsolationStrategy(THREAD)  // 线程池隔离策略
                                        .withFallbackEnabled(true)          // 开启降级
                                        .withCircuitBreakerEnabled(true)    // 开启熔断器
                        )
        );

        // 可以配置信号量隔离策略  直接将super()中的替换就可以
//        Setter semaphore =
//                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("NacosClientService"))
//                        .andCommandKey(HystrixCommandKey.Factory.asKey("NacosClientServiceCommand"))
//                        .andCommandPropertiesDefaults(
//                                HystrixCommandProperties.Setter()
//                                .withCircuitBreakerRequestVolumeThreshold(10)
//                                .withCircuitBreakerSleepWindowInMilliseconds(5000)
//                                .withCircuitBreakerErrorThresholdPercentage(50)
//                                .withExecutionIsolationStrategy(SEMAPHORE)  // 使用信号量隔离策略
//                                // ......
//                        );


        this.nacosClientService = nacosClientService;
        this.serviceId = serviceId;
    }

    /**
     * <h2>要保护的方法调用写在  run 方法中</h2>
     *
     * @return
     * @throws Exception
     */
    @Override
    protected List<ServiceInstance> run() throws Exception {

        log.info("NacosClientService In Hystrix Command to Get Service Instance : [{}],[{}]", this.serviceId, Thread.currentThread().getName());
        return this.nacosClientService.getServiceInstances(this.serviceId);
    }

    /**
     * <h2>降级处理策略</h2>
     *
     * @return
     */
    @Override
    protected List<ServiceInstance> getFallback() {
        log.warn(" NacosClientService run error : [{}],[{}]",
                this.serviceId,Thread.currentThread().getName());
        return Collections.emptyList();
    }
}
