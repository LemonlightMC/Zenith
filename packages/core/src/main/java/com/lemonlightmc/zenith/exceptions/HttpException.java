package com.lemonlightmc.zenith.exceptions;

import java.net.http.HttpResponse;

public class HttpException extends RuntimeException {

  public HttpException() {
  }

  public HttpException(HttpResponse<?> response) {
    super("HTTP " + response.request().method() + " " + response.statusCode() + ": " + response.uri().toString());
  }

  public HttpException(String msg) {
    super(msg);
  }

  public HttpException(Throwable cause) {
    super(cause);
  }

  public HttpException(String msg, Throwable cause) {
    super(msg, cause);
  }
}