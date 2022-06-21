package com.imooc.ecommerce.service.async;

import com.imooc.ecommerce.constant.AsyncTaskStatusEnum;
import com.imooc.ecommerce.goods.GoodsInfo;
import com.imooc.ecommerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 异步任务执行管理器
 * 对异步任务进行包装管理，记录并塞入异步任务信息
 */
@Slf4j
@Component
public class AsyncTaskManager {


    /**
     * 异步任务执行信息容器
     */
    private final Map<String, AsyncTaskInfo> taskContainer = new HashMap<>(16);

    @Resource
    private IAsyncService asyncService;


    /**
     * 初始化异步任务
     *
     * @return
     */
    public AsyncTaskInfo initTask() {
        AsyncTaskInfo asyncTaskInfo = new AsyncTaskInfo();
        // 设置一个唯一的异步任务id，只要唯一即可
        asyncTaskInfo.setTaskId(UUID.randomUUID().toString());
        asyncTaskInfo.setStatus(AsyncTaskStatusEnum.STARTED);
        asyncTaskInfo.setStartTime(new Date());

        // 初始化的时候就要把异步任务执行信息放入到容器中
        taskContainer.put(asyncTaskInfo.getTaskId(), asyncTaskInfo);
        return asyncTaskInfo;
    }


    /**
     * 提交异步任务
     *
     * @param goodsInfoList
     * @return
     */
    public AsyncTaskInfo submit(List<GoodsInfo> goodsInfoList) {
        // 初始化一个异步任务的监控信息
        AsyncTaskInfo asyncTaskInfo = initTask();
        asyncService.asyncImportGoods(goodsInfoList, asyncTaskInfo.getTaskId());
        return asyncTaskInfo;

    }

    /**
     * 设置异步任务状态信息
     *
     * @param asyncTaskInfo
     */
    public void setTaskInfo(AsyncTaskInfo asyncTaskInfo) {
        taskContainer.put(asyncTaskInfo.getTaskId(), asyncTaskInfo);
    }

    /**
     * 获取异步任务执行信息
     *
     * @param taskId
     * @return
     */
    public AsyncTaskInfo getTaskInfo(String taskId) {
        return taskContainer.get(taskId);
    }


}
