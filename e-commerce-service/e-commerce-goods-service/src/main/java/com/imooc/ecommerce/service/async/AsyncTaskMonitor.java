package com.imooc.ecommerce.service.async;

import com.imooc.ecommerce.constant.AsyncTaskStatusEnum;
import com.imooc.ecommerce.vo.AsyncTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 异步任务执行监控切面
 */
@Slf4j
@Aspect
@Component
public class AsyncTaskMonitor {

    private final AsyncTaskManager asyncTaskManager;

    private AsyncTaskMonitor(AsyncTaskManager asyncTaskManager) {
        this.asyncTaskManager = asyncTaskManager;
    }


    @Pointcut
    public void test() {

    }

    /**
     * <h2>异步任务执行的环绕切面</h2>
     * 环绕切面 让我们可以在方法执行前和执行之后做一些额外的操作
     *
     * @param proceedingJoinPoint
     * @return
     */
    @Around("execution(* com.imooc.ecommerce.service.async.AsyncServiceImpl.*(..))")
    public Object taskHandle(ProceedingJoinPoint proceedingJoinPoint) {
        // 获取taskId，调用异步任务传入的第二个参数taskId
        String taskId = proceedingJoinPoint.getArgs()[1].toString();

        // 获取任务信息，在提交任务的时候就已经放入到容器中了
        AsyncTaskInfo taskInfo = asyncTaskManager.getTaskInfo(taskId);
        log.info("AsyncTaskMonitor is monitoring async tsk: [{}] ", taskId);

        taskInfo.setStatus(AsyncTaskStatusEnum.RUNNING);
        asyncTaskManager.setTaskInfo(taskInfo); // 设置为运行状态

        AsyncTaskStatusEnum status;
        Object result;
        try {
            // 执行异步任务
            result = proceedingJoinPoint.proceed();
            status = AsyncTaskStatusEnum.SUCCESS;
        } catch (Throwable ex) {
            // 异步任务出现了异常
            result = null;
            status = AsyncTaskStatusEnum.FAILED;
            log.error("AsyncTaskMonitor: async task [{}] is failed ,Error info :[{}]", taskInfo.getTaskId(), ex.getMessage(), ex);
        }

        // 设置异步任务其它的信息
        taskInfo.setEndTime(new Date());
        taskInfo.setStatus(status);
        taskInfo.setTotalTime(String.valueOf(taskInfo.getEndTime().getTime() - taskInfo.getEndTime().getTime()));
        asyncTaskManager.setTaskInfo(taskInfo);

        return result;

    }

}
