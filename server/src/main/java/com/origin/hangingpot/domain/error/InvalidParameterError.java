package com.origin.hangingpot.domain.error;

import org.springframework.http.HttpStatus;

public class InvalidParameterError extends Error {
	public InvalidParameterError() {
		super(Code.INVALID_PARAMETER.value(), "Invalid parameter", "", HttpStatus.BAD_REQUEST.value());
	}
}
