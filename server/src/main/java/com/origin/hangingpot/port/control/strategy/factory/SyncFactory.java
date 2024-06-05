package com.origin.hangingpot.port.control.strategy.factory;

import com.origin.hangingpot.infrastructure.util.SpringUtils;
import com.origin.hangingpot.port.control.strategy.SyncStrategy;
import org.springframework.stereotype.Service;

/**
 * @Author: YourName
 * @Date: 2024/6/5 18:29
 * @Description:
 **/
@Service
public class SyncFactory {
    public static SyncStrategy getSyncStrategy(String type){
        return SpringUtils.getBean(type,SyncStrategy.class);
    }
}
