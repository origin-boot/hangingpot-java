package com.origin.hangingpot.port;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.origin.hangingpot.domain.event.DummyEvent;
import com.origin.hangingpot.infrastructure.util.TimeUtil;
import com.origin.hangingpot.port.control.SchedulingBackgroundTask;

@Component
public class BackgroundTaskController extends SchedulingBackgroundTask {
	@Autowired
	AsyncEventBus asyncEventBus;

	@Override
	protected void generateBackgroundTask() {

	}

	@Override
	protected void readBackgroundTask() {
		DummyEvent event = new DummyEvent();
		event.setName("dummy " + TimeUtil.getUnixTimestamp());
		asyncEventBus.post(event);
	}
}
