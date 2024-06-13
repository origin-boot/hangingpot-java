package com.origin.hangingpot.port;

import com.origin.hangingpot.domain.ScheduleJob;
import com.origin.hangingpot.infrastructure.repository.ScheduleJobRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.origin.hangingpot.domain.event.DummyEvent;
import com.origin.hangingpot.infrastructure.util.TimeUtil;
import com.origin.hangingpot.port.control.SchedulingBackgroundTask;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BackgroundTaskController extends SchedulingBackgroundTask {
	@Autowired
	AsyncEventBus asyncEventBus;
	@Resource
	private ScheduleJobRepository scheduleJobRepository;




	@Override
	protected void generateBackgroundTask() {

	}

	@Override
	protected void readBackgroundTask() {
		List<ScheduleJob> byStatus = scheduleJobRepository.findByStatus(false);

		byStatus.forEach(item ->{


			asyncEventBus.post(item);
		});

	}
}
