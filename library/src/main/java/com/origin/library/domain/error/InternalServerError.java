package com.origin.library.domain.error;

import org.springframework.http.HttpStatus;

public class InternalServerError extends Error {
  public InternalServerError() {
    super(Code.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error", "",
        HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}
