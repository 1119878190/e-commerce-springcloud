package com.imooc.ecommerce.stream;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.vo.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

/**
 * <h1>使用默认的通信信道，实现消息的发送</h1>
 *
 * 发送的是yml中我们设置的默认的 input 信道
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 21:23
 **/
@Slf4j
@EnableBinding(Source.class)
public class DefaultSendService {


    @Resource
    private Source source;


    /**
     * 使用默认的输出信道发送消息
     *
     * @param testMessage
     */
    public void sendMessage(TestMessage testMessage) {

        String _message = JSON.toJSONString(testMessage);
        log.info("in DefaultSendService send message: [{}]", _message);

        // Spring Messaging ,统一消息的编程模型，是 Stream 组件的重要组成部分之一
        source.output().send(MessageBuilder.withPayload(_message).build());
    }

}
