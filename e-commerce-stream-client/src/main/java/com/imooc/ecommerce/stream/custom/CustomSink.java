package com.imooc.ecommerce.stream.custom;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * <h1>自定义输入信道</h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 22:17
 **/
public interface CustomSink {

    String INPUT = "customInput";

    /**
     * 输入信道的名称是 customInput  需要使用 Stream 绑定器在 yml 文件中配置
     *
     * @return
     */
    @Input(CustomSink.INPUT)
    SubscribableChannel customInput();

}
