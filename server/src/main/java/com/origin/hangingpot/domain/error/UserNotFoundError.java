package com.origin.hangingpot.domain.error;

import org.springframework.http.HttpStatus;

public class UserNotFoundError extends Error {
	public UserNotFoundError() {
		super(Code.USER_NOT_FOUND.value(), "User not found", "", HttpStatus.NOT_FOUND.value());
	}

	@Override
	public UserNotFoundError setDetails(String details) {
		super.setDetails(details);
		return this;
	}
}
