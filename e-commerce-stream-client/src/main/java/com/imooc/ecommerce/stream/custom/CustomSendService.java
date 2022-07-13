package com.imooc.ecommerce.stream.custom;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.vo.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

/**
 * <h1>使用自定义的通信信道 CustomSource 实现消息的发送</h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 22:09
 **/
@Slf4j
@EnableBinding(CustomSource.class)
public class CustomSendService {

    private final CustomSource customSource;

    public CustomSendService(CustomSource customSource) {
        this.customSource = customSource;
    }

    /**
     * 使用自定义的输出信道发送消息
     */
    public void sendMessage(TestMessage testMessage) {
        String _message = JSON.toJSONString(testMessage);
        log.info("in CustomSendService send message:[{}]", _message);
        customSource.customOutput().send(
                MessageBuilder.withPayload(_message).build()
        );
    }
}
