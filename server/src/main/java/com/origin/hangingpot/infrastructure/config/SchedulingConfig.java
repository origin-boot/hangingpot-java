package com.origin.hangingpot.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Author: YourName
 * @Date: 2024/6/13 11:28
 * @Description:
 **/

@Configuration
public class SchedulingConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        // 获取系统处理器个数, 作为线程池数量
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 定时任务执行线程池核心线程数
        taskScheduler.setPoolSize(corePoolSize);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setThreadNamePrefix("数据同步SchedulerThreadPool-");
        return taskScheduler;
    }

}
