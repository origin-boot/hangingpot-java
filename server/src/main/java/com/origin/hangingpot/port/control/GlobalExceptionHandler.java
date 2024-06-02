package com.origin.hangingpot.port.control;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.origin.hangingpot.domain.error.Error;
import com.origin.hangingpot.domain.error.InternalServerError;
import com.origin.hangingpot.domain.error.InvalidParameterError;
import com.origin.hangingpot.domain.error.RequestMethodError;
import com.origin.hangingpot.domain.error.RequestPathError;
import com.origin.hangingpot.infrastructure.util.ExceptionUtil;

@ControllerAdvice
public class GlobalExceptionHandler {

	private final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Error> handleException(Exception ex) {

		String stackTrace = ExceptionUtil.getStackTrace(ex, true, 1000);
		logger.warning(stackTrace);

		if (ex instanceof Error) {
			Error e = (Error) ex;
			e.setDetails(e.getDetails() + " " + stackTrace);
			return ResponseEntity.status(e.getStatus()).body(e);
		}

		// FIXME: Use switch or cache to optimize the processing of multiple ifs
		if (ex instanceof MethodArgumentNotValidException) {
			Error e = new InvalidParameterError().setDetails(stackTrace);
			return ResponseEntity.status(e.getStatus()).body(e);
		}

		if (ex instanceof NoResourceFoundException) {
			Error e = new RequestPathError().setDetails(stackTrace);
			return ResponseEntity.status(e.getStatus()).body(e);
		}

		if (ex instanceof HttpRequestMethodNotSupportedException) {
			Error e = new RequestMethodError().setDetails(stackTrace);
			return ResponseEntity.status(e.getStatus()).body(e);
		}

		Error e = new InternalServerError().setDetails(stackTrace);
		return ResponseEntity.status(e.getStatus()).body(e);
	}
}
