package com.imooc.ecommerce.stream.custom;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * <h1>自定义输出信道</h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 22:05
 **/
public interface CustomSource {


    String OUTPUT = "customOutput";


    /**
     * 输出信道的名称是 customOutput ，需要使用 Stream 绑定器在yml 文件中声明
     */
    @Output(CustomSource.OUTPUT)
    MessageChannel customOutput();

}
