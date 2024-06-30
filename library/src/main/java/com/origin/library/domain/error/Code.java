package com.origin.library.domain.error;

enum Code {
  INVALID_PARAMETER(400),
  UNAUTHORIZED(401),
  PAYMENT_REQUIRED(402),
  FORBIDDEN(403),
  NOT_FOUND(404),
  METHOD_NOT_ALLOWED(405),
  INTERNAL_SERVER_ERROR(500),
  USERNAME_OR_PASSWORD_ERROR(1000),
  USER_NOT_FOUND(1001),
  BOOK_NOT_FOUND(1002);

  private final int value;

  private Code(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }
}