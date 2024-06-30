package com.origin.library.infrastructure.util;

import java.util.Random;

public class RandomUtil {

  // FIXME: Optimize this implementation to make it more efficient
  public static String generateRandomString(int length) {
    String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder sb = new StringBuilder();
    Random random = new Random();

    for (int i = 0; i < length; i++) {
      sb.append(candidateChars.charAt(random.nextInt(candidateChars.length())));
    }

    return sb.toString();
  }
}
