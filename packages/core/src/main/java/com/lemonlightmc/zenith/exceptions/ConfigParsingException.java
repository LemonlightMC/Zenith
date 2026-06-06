package com.lemonlightmc.zenith.exceptions;

public class ConfigParsingException extends RuntimeException {
  private final String message;

  public ConfigParsingException(final String message, Throwable cause) {
    super(message, cause);
    this.message = message;
  }

  public ConfigParsingException(final String message) {
    super(message, null);
    this.message = message;
  }

  public String getRawMessage() {
    return message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}