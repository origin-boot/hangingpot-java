package com.origin.hangingpot.port.control;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: YourName
 * @Date: 2024/6/13 11:30
 * @Description:
 **/

@Slf4j
@Component
@SuppressWarnings("all")
public class CronTaskRegistrar implements DisposableBean {

    @Resource
    private TaskScheduler taskScheduler;

    // 保存任务Id和定时任务
    private final Map<String, ScheduledTask> scheduledTaskMap = new ConcurrentHashMap<>(64);

    // 添加任务
    public void addTask(Runnable task, String cronExpression,String jobId) {
        addTask(new CronTask(task, cronExpression),jobId);
    }

    public void addTask(CronTask cronTask,String jobId) {
        if (Objects.nonNull(cronTask)) {
            Runnable task = cronTask.getRunnable();
            if (this.scheduledTaskMap.containsKey(jobId)) {
                removeTask(jobId);
            }
            // 保存任务Id和定时任务
            this.scheduledTaskMap.put(jobId, scheduleTask(cronTask));
        }
    }

    // 通过任务Id，取消定时任务
    public void removeTask(String jobId) {
        ScheduledTask scheduledTask = this.scheduledTaskMap.remove(jobId);
        if (Objects.nonNull(scheduledTask)) {
            scheduledTask.cancel();
        }
        String s = """
                2131312
                """;
    }

    public ScheduledTask scheduleTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }

    // 销毁
    @Override
    public void destroy() {
        this.scheduledTaskMap.values().forEach(ScheduledTask::cancel);
        this.scheduledTaskMap.clear();
    }
}
