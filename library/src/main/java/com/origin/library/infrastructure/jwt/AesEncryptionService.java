package com.origin.library.infrastructure.jwt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.origin.library.infrastructure.util.ExceptionUtil;

import java.util.Base64;
import java.util.logging.Logger;

public class AesEncryptionService {

  private static final String ALGORITHM = "AES";
  private static Logger logger = Logger.getLogger(AesEncryptionService.class.getName());

  private String secret;

  public AesEncryptionService(String secret) {
    this.secret = secret;
  }

  public String encryptString(String input) {
    try {
      SecretKeySpec key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] encryptedBytes = cipher.doFinal(input.getBytes());
      return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      logger.warning(ExceptionUtil.getStackTrace(e, true));
      return null;
    }
  }

  public String decryptString(String encodedInput) {
    try {
      SecretKeySpec key = new SecretKeySpec(secret.getBytes(), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decodedBytes = Base64.getDecoder().decode(encodedInput);
      byte[] decryptedBytes = cipher.doFinal(decodedBytes);
      return new String(decryptedBytes);
    } catch (Exception e) {
      logger.warning(ExceptionUtil.getStackTrace(e, true));
      return null;
    }
  }
}