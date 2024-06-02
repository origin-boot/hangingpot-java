package com.origin.hangingpot.domain.error;

import org.springframework.http.HttpStatus;

public class RequestForbiddenError extends Error {
	public RequestForbiddenError() {
		super(Code.FORBIDDEN.value(), "Request is forbidden", "", HttpStatus.FORBIDDEN.value());
	}
	
	@Override
	public RequestForbiddenError setDetails(String details) {
		super.setDetails(details);
		return this;
	}
}
