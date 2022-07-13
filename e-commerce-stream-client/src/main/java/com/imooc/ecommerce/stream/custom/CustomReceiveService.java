package com.imooc.ecommerce.stream.custom;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.vo.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * <h1>使用自定义的输入信道,实现消息的接受</h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 22:20
 **/
@Slf4j
@EnableBinding(CustomSink.class)
public class CustomReceiveService {

    /***
     * 使用自定义的输入信道接收消息
     * @param payload
     */
    @StreamListener(CustomSink.INPUT)
    public void receiveMessage(@Payload Object payload) {

        log.info("in CustomReceiveService consume message start");
        TestMessage testMessage = JSON.parseObject(payload.toString(), TestMessage.class);
        log.info("in CustomReceiveService consume message success : [{}]", JSON.toJSONString(testMessage));

    }

}
