package com.origin.library.port.control;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {

  @Autowired
  protected IdentityHandlerInterceptor identityHandlerInterceptor;

  // FIXME: other controllers should extend this class, add common methods here
}
