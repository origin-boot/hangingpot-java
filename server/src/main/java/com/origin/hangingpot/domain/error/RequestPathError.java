package com.origin.hangingpot.domain.error;

import org.springframework.http.HttpStatus;

public class RequestPathError extends Error {
	public RequestPathError() {
		super(Code.NOT_FOUND.value(), "Request path is not found", "", HttpStatus.NOT_FOUND.value());
	}
}
