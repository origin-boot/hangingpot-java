package com.origin.hangingpot.port.control;

import com.google.common.eventbus.AsyncEventBus;
import com.origin.hangingpot.domain.ScheduleJob;
import com.origin.hangingpot.infrastructure.repository.ScheduleJobRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: YourName
 * @Date: 2024/6/13 14:30
 * @Description:
 **/
@Component
public class StartRunner implements CommandLineRunner {
    @Autowired
    AsyncEventBus asyncEventBus;
    @Resource
    private ScheduleJobRepository scheduleJobRepository;
    @Override
    public void run(String... args) throws Exception {
        List<ScheduleJob> byStatus = scheduleJobRepository.findByStatus(false);

        byStatus.forEach(item ->{


            asyncEventBus.post(item);
        });
    }
}
