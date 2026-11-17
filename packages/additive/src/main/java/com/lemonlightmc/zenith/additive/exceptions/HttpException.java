package com.lemonlightmc.zenith.additive.exceptions;

import java.net.http.HttpResponse;

/**
 * Indicates a failure while performing an HTTP operation.
 */
public class HttpException extends RuntimeException {

  /**
   * Creates a new HTTP exception with no detail message or cause.
   */
  public HttpException() {
  }

  /**
   * Creates a new HTTP exception from an unsuccessful HTTP response.
   *
   * @param response the HTTP response that triggered this exception
   */
  public HttpException(HttpResponse<?> response) {
    super("HTTP " + response.request().method() + " " + response.statusCode() + ": " + response.uri().toString());
  }

  /**
   * Creates a new HTTP exception with the specified detail message.
   *
   * @param msg the detail message
   */
  public HttpException(String msg) {
    super(msg);
  }

  /**
   * Creates a new HTTP exception with the specified cause.
   *
   * @param cause the cause of this exception
   */
  public HttpException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new HTTP exception with the specified detail message and cause.
   *
   * @param msg the detail message
   * @param cause the cause of this exception
   */
  public HttpException(String msg, Throwable cause) {
    super(msg, cause);
  }
}