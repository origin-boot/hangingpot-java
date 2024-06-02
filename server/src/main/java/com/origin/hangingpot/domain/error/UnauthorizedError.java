package com.origin.hangingpot.domain.error;

import org.springframework.http.HttpStatus;

public class UnauthorizedError extends Error{
	public UnauthorizedError() {
		super(Code.UNAUTHORIZED.value(), "Unauthorized", "", HttpStatus.UNAUTHORIZED.value());
	}
}
