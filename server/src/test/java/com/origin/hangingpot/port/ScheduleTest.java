package com.origin.hangingpot.port;

import com.google.common.eventbus.AsyncEventBus;
import com.origin.hangingpot.domain.ScheduleJob;
import com.origin.hangingpot.infrastructure.repository.ScheduleJobRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author: YourName
 * @Date: 2024/6/13 11:53
 * @Description:
 **/
@SpringBootTest
public class ScheduleTest {

    @Resource
    private ScheduleJobRepository scheduleJobRepository;

    @Autowired
    AsyncEventBus asyncEventBus;

    @Test
    void testSchedule(){
        List<ScheduleJob> byStatus = scheduleJobRepository.findByStatus(false);
        asyncEventBus.post(byStatus.get(0));
    }
}
