package com.imooc.ecommerce.partition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;
import org.springframework.stereotype.Component;

/**
 * <h1>决定 Message 发送到哪个 partition 分区的策略</h1>
 *
 * @Author: lafe
 * @DateTime: 2022/7/19 21:42
 **/
@Slf4j
@Component
public class LafePartitionSelectorStrategy implements PartitionSelectorStrategy {


    /**
     * 选择分区的策略
     *
     * @param key
     * @param partitionCount
     * @return
     */
    @Override
    public int selectPartition(Object key, int partitionCount) {

        // 分区的算法根据情况具体而论，这里以 hashcode 取余为例
        int partition = key.toString().hashCode() % partitionCount;
        log.info("SpringCloud Stream lafe Selector info : [],[],[]", key.toString(), partitionCount, partition);
        return partition;
    }
}
