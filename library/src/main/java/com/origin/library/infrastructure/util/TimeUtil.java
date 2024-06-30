package com.origin.library.infrastructure.util;

public class TimeUtil {
  public static long getUnixTimestamp() {
    return System.currentTimeMillis() / 1000L;
  }
}
