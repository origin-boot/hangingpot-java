package com.origin.library.domain.success;

import lombok.Data;

@Data
public class Empty {
  private String message;

  public Empty() {
    this.message = "OK";
  }
}
