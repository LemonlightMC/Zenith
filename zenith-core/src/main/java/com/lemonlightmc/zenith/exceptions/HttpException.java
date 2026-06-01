package com.lemonlightmc.zenith.exceptions;

public class HttpException extends RuntimeException {

  public HttpException() {
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