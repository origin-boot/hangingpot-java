package com.origin.hangingpot.port.control;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author: YourName
 * @Date: 2024/6/13 11:34
 * @Description:
 **/

public final class ScheduledTask {

    public volatile ScheduledFuture<?> future;

    /**
     * 取消定时任务
     */
    public void cancel() {
        ScheduledFuture<?> scheduledFuture = this.future;
        if (Objects.nonNull(scheduledFuture)) {
            scheduledFuture.cancel(true);
        }
    }
}
