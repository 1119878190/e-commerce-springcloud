package com.imooc.ecommerce.stream;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.vo.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

/**
 * <h1>使用默认的信道实现消息的接受</h1>
 * 读取的是yml中我们设置的默认的 input 信道
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 21:33
 **/
@Slf4j
@EnableBinding(Sink.class)
public class DefaultReceiveService {

    /**
     * 使用默认的输入信道接受消息
     * 读取的是yml中我们设置的默认的 input 信道
     *
     * @param payload
     */
    @StreamListener(Sink.INPUT)
    public void receiveMessage(Object payload) {

        log.info("in DefaultReceiveService consume message start");
        TestMessage testMessage = JSON.parseObject(
                payload.toString(), TestMessage.class
        );
        // 消费消息
        log.info("in DefaultReceiveService consume message success: [{}]", JSON.toJSONString(testMessage));


    }

}
