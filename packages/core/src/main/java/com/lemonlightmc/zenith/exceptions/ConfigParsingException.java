package com.lemonlightmc.zenith.exceptions;

/**
 * Indicates a failure while parsing configuration data.
 */
public class ConfigParsingException extends RuntimeException {
  private final String message;

  /**
   * Creates a new configuration parsing exception with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public ConfigParsingException(final String message, Throwable cause) {
    super(message, cause);
    this.message = message;
  }

  /**
   * Creates a new configuration parsing exception with the specified message.
   *
   * @param message the detail message
   */
  public ConfigParsingException(final String message) {
    super(message, null);
    this.message = message;
  }

  /**
   * Gets the raw parsing error message without additional formatting.
   *
   * @return the raw error message
   */
  public String getRawMessage() {
    return message;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMessage() {
    return message;
  }
}