package com.origin.library.domain.error;

import org.springframework.http.HttpStatus;

public class RequestMethodError extends Error {
  public RequestMethodError() {
    super(Code.METHOD_NOT_ALLOWED.value(), "Request method is not supported", "",
        HttpStatus.METHOD_NOT_ALLOWED.value());
  }
}
