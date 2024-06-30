package com.origin.library.domain.success;

import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class Ok<T> extends ResponseEntity<T> {

  public Ok(@Nullable T body, @Nullable MultiValueMap<String, String> headers,
      HttpStatusCode statusCode) {
    super(body, headers, statusCode);
  }

  public static <T> Ok<Empty> empty() {
    return new Ok<>(new Empty(), null, HttpStatus.OK);
  }

  public static <T> Ok<T> of(@Nullable T body) {
    return new Ok<>(body, null, HttpStatus.OK);
  }

  public static <T> Ok<T> of(@Nullable T body, @Nullable MultiValueMap<String, String> headers) {
    return new Ok<>(body, headers, HttpStatus.OK);
  }

  public static <T> Ok<T> of(@Nullable T body, @Nullable MultiValueMap<String, String> headers,
      HttpStatusCode statusCode) {

    return (Ok<T>) status(statusCode)
        .headers(HttpHeaders.readOnlyHttpHeaders(
            headers != null ? headers : new HttpHeaders()))
        .body(body);
  }
}