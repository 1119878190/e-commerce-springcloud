package com.imooc.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>消息传递对象： SpringCloud Stream + Kafka/RocketMQ </h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/13 21:17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestMessage {

    private Integer id;
    private String projectName;
    private String org;
    private String author;
    private String version;


    /**
     * 返回一个默认的消息，方便使用
     *
     * @return
     */
    public static TestMessage defaultMessage() {
        return new TestMessage(
                1,
                "e-ecommerce-stream-client",
                "imooc.com",
                "lafe",
                "1.0"
        );
    }

}
