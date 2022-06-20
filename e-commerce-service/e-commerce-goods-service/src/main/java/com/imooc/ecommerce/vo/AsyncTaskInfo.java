package com.imooc.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 异步任务执行信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsyncTaskInfo {

    /**
     * 异步任务 id
     */
    private String taskId;

    /**
     * 异步任务 开始时间
     */
    private Date startTime;

    /**
     * 异步任务 结束时间
     */
    private Date endTime;

    /**
     * 异步任务总耗时
     */
    private String totalTime;


}
