package com.imooc.ecommerce.partition;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.vo.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * <h1>自定义从 Message 中提取 partition key 的策略</h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/19 21:36
 **/
@Slf4j
@Component
public class LafePartitionKeyExtractorStrategy implements PartitionKeyExtractorStrategy {

    @Override
    public Object extractKey(Message<?> message) {

        TestMessage testMessage = JSON.parseObject(message.getPayload().toString(), TestMessage.class);
        // 自定义提取 key (这里的 key 根据业务具体实现，这里就简单的以 projectName 为 key )
        String key = testMessage.getProjectName();
        log.info("SpringCloud Stream Lafe Partition Key: [{}]", key);
        return key;
    }
}
