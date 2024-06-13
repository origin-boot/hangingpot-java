package com.origin.hangingpot.port.control.strategy;

import java.sql.SQLException;

public interface SyncStrategy {
    /**
     * 根据源头端、目标端ID以及时间范围进行同步
     */
    void SyncData(Long sourceId,Long destId,String startTime,String endTime) throws SQLException;
}
