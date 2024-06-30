package com.origin.library.domain.error;

import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ErrorSerializer.class)
public class Error extends Exception {
  // FIXME: Change these properties to protected
  public int code;
  public String message;
  public String details;
  protected int status;

  private static final int DEFAULT_STATUS = 500;

  public Error(int code, String message) {
    new Error(code, message, "", DEFAULT_STATUS);
  }

  public Error(int code, String message, String details) {
    new Error(code, message, details, DEFAULT_STATUS);
  }

  public Error(int code, String message, String details, int status) {
    this.code = code;
    this.message = Optional.ofNullable(message).orElse("");
    this.details = Optional.ofNullable(details).orElse("");
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public Error setStatus(int status) {
    this.status = status;
    return this;
  }

  public Error setMessage(String message) {
    this.message = message;
    return this;
  }

  public String getDetails() {
    return details;
  }

  public Error setDetails(String details) {
    this.details = details;
    return this;
  }
}
