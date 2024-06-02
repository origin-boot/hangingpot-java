package com.origin.hangingpot.port;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.origin.hangingpot.domain.event.DummyEvent;

@Component
public class EventController {
	private final Logger logger = Logger.getLogger(EventController.class.getName());

	public EventController(EventBus eventBus, AsyncEventBus asyncEventBus) {
		eventBus.register(this);
		asyncEventBus.register(this);
	}

	@Subscribe
	public void handleDummyEvent(DummyEvent event) throws InterruptedException {
		logger.info("handling dummy event: " + event.getId());
		Thread.sleep(10000);
		logger.info("handled dummy event: " + event.getId());
	}
}
