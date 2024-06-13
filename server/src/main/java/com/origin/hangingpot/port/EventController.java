package com.origin.hangingpot.port;

import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.origin.hangingpot.domain.DatabaseConnection;
import com.origin.hangingpot.domain.ScheduleJob;
import com.origin.hangingpot.infrastructure.repository.DatabaseConnectionRepository;
import com.origin.hangingpot.port.control.CronTaskRegistrar;
import com.origin.hangingpot.port.control.strategy.context.SyncContext;
import jakarta.annotation.Resource;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.origin.hangingpot.domain.event.DummyEvent;

@Component
public class EventController {
	private final Logger logger = Logger.getLogger(EventController.class.getName());
	@Resource
	private CronTaskRegistrar cronTaskRegistrar;

	@Resource
	private SyncContext syncContext;

	@Resource
	private DatabaseConnectionRepository databaseConnectionRepository;

	public EventController(EventBus eventBus, AsyncEventBus asyncEventBus) {
		eventBus.register(this);

		asyncEventBus.register(this);
	}

	@Subscribe
	public void handleDummyEvent(ScheduleJob event) throws InterruptedException {
		logger.info("Received event: " + event.getJobName() + " " + event.getCronExpression() + " " + event.getRange() + " " + event.getProject().getId());
		DateTime date = DateUtil.date();


		CronTask cronTask = new CronTask(() -> {
			//获取当前时间
			DateTime now = DateUtil.date();
			//获取同步范围
			Long range = event.getRange();
			//获取同步开始时间
			DateTime startTime = now.offset(DateField.HOUR, (int) -range-1);//容错处理，多同步一小时
			String string = startTime.toString("yyyy-MM-dd HH:mm:ss");
			String nowString = DateUtil.now();
			System.out.println("开始时间："+string);
			System.out.println("结束时间："+nowString);
			//查询任务对应的数据库连接源头
			Optional<DatabaseConnection> source = databaseConnectionRepository.findBySourceTypeAndProjectId("源头端", event.getProject().getId());
			//查询任务对应的数据库连接目标端
			Optional<DatabaseConnection> target = databaseConnectionRepository.findBySourceTypeAndProjectId("目标端", event.getProject().getId());
			if(source.isPresent()&&target.isPresent()){
				DatabaseConnection databaseConnection = source.get();
				Long sourceId = databaseConnection.getId();
				DatabaseConnection databaseConnection1 = target.get();
				Long targetId = databaseConnection1.getId();
				syncContext.SyncData(sourceId, targetId, string, nowString, "Sync1");
			}

		}, event.getCronExpression());

		cronTaskRegistrar.addTask(cronTask,event.getId()+event.getJobName());
	}
}
