package com.lemonlightmc.zenith.exceptions;

public class ConfigHandlingException extends RuntimeException {
  public ConfigHandlingException() {
  }

  public ConfigHandlingException(String msg) {
    super(msg);
  }

  public ConfigHandlingException(Throwable cause) {
    super(cause);
  }

  public ConfigHandlingException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
