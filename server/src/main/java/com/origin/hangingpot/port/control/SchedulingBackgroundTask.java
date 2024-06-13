package com.origin.hangingpot.port.control;

import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;

public abstract class SchedulingBackgroundTask {
	private final Logger logger = Logger.getLogger(SchedulingBackgroundTask.class.getName());

	abstract protected void generateBackgroundTask();

	abstract protected void readBackgroundTask();

//	@Scheduled(cron = "0 * * * * ?")
//	public void generateBackgroundTaskCron() {
//		logger.info("generating background task");
//		generateBackgroundTask();
//	}

	// @Scheduled(cron = "0 * * * * ?")
//	@Scheduled(cron = "*/5 * * * * ?")
//	public void readBackgroundTaskCron() {
//		logger.info("reading background task");
//		readBackgroundTask();
//	}

}
