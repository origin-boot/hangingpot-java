package com.origin.hangingpot.domain.event;

import java.util.UUID;

import lombok.Data;

@Data
class Event {
	protected String id;
	Event() {
		this.id = UUID.randomUUID().toString();
	}
}
