package com.origin.library.port.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService extends com.origin.library.infrastructure.jwt.JwtService {

  @Autowired
  public JwtService(
      @Value("${jwt.sign}") final String sign,
      @Value("${jwt.secret}") final String secret,
      @Value("${jwt.expiration}") final int expiration) {

    super(sign, secret, expiration);
  }
}
