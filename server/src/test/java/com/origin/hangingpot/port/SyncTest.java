package com.origin.hangingpot.port;

import com.origin.hangingpot.port.control.strategy.context.SyncContext;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: YourName
 * @Date: 2024/6/5 18:34
 * @Description:
 **/
@SpringBootTest

public class SyncTest {

    @Resource
    private SyncContext syncContext;

    @Test
    void TestSync(){
        syncContext.SyncData(1L,2L,"2024-01-01 00:00:00","2024-01-22 00:00:00","Sync1");
    }
}
