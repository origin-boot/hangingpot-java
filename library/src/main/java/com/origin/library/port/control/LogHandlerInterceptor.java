package com.origin.library.port.control;

import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson2.JSON;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

@Component
public class LogHandlerInterceptor implements HandlerInterceptor {

  private final Logger logger = Logger.getLogger(LogHandlerInterceptor.class.getName());
  private String createTimeAttr = "createTime";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    request.setAttribute(createTimeAttr, System.currentTimeMillis());

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {

    long startTime = (Long) request.getAttribute(createTimeAttr);
    long endTime = System.currentTimeMillis();
    long executeTime = endTime - startTime;

    LogEntry logEntry = new LogEntry(request, response, executeTime);
    // FIXME: Use the more general Formatter class to replace the toJSONString
    // method
    logger.info(logEntry.toJsonString());
  }

}

class LogEntry {
  public String ip;
  public String method;
  public String path;
  public Map<String, String> requestHeaders;
  public String requestBody;
  public Map<String, String> requestParams;
  public Map<String, String> responseHeaders;
  public String responseBody;
  public long executeTime;
  public int status;
  public String user;
  public String message;

  LogEntry(HttpServletRequest request, HttpServletResponse response, long executeTime) {
    // FIXME: parse remote address to normal ip
    this.ip = request.getRemoteAddr();
    this.method = request.getMethod();
    this.path = request.getRequestURI();
    this.requestHeaders = getHeaders(request);
    this.requestBody = getBody(request);
    this.requestParams = getParams(request);
    this.responseHeaders = getHeaders(response);
    this.responseBody = getBody(response);
    this.executeTime = executeTime;
    this.status = response.getStatus();
    this.user = getUser(request, response);
    this.message = "http request";
  }

  String toJsonString() {
    return JSON.toJSONString(this);
  }

  Map<String, String> getHeaders(HttpServletRequest request) {
    Enumeration<String> headerNames = request.getHeaderNames();
    Map<String, String> headers = new HashMap<>();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.put(headerName, request.getHeader(headerName));
    }
    return headers;
  }

  Map<String, String> getHeaders(HttpServletResponse response) {
    Map<String, String> headers = new HashMap<>();
    for (String headerName : response.getHeaderNames()) {
      headers.put(headerName, response.getHeader(headerName));
    }
    return headers;
  }

  Map<String, String> getParams(HttpServletRequest request) {
    Enumeration<String> paramNames = request.getParameterNames();
    Map<String, String> params = new HashMap<>();
    while (paramNames.hasMoreElements()) {
      String paramName = paramNames.nextElement();
      params.put(paramName, request.getParameter(paramName));
    }
    return params;
  }

  String getBody(HttpServletRequest request) {
    // FIXME: get request body
    return "";
  }

  String getBody(HttpServletResponse response) {
    // FIXME: get response body
    return "";
  }

  String getUser(HttpServletRequest request, HttpServletResponse response) {
    Object value = request.getAttribute(IdentityHandlerInterceptor.ATTRIBUTE);
    if (value != null) {
      return value.toString();
    }
    value = response.getHeader(IdentityHandlerInterceptor.ATTRIBUTE);
    return value == null ? "" : value.toString();
  }
}
