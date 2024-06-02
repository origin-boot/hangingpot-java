package com.origin.hangingpot.domain.error;

import org.springframework.http.HttpStatus;

public class UsernameOrPasswordError extends Error {
	public UsernameOrPasswordError() {
		super(Code.USERNAME_OR_PASSWORD_ERROR.value(), "Username or password is incorrect", "",
				HttpStatus.BAD_REQUEST.value());
	}

	@Override
	public UsernameOrPasswordError setDetails(String details) {
		super.setDetails(details);
		return this;
	}
}
