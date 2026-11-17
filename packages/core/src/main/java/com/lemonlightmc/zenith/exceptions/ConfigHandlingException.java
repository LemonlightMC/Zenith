package com.lemonlightmc.zenith.exceptions;

/**
 * Indicates a failure while handling configuration operations.
 */
public class ConfigHandlingException extends RuntimeException {
  /**
   * Creates a new configuration handling exception with no detail message.
   */
  public ConfigHandlingException() {
  }

  /**
   * Creates a new configuration handling exception with the specified detail message.
   *
   * @param msg the detail message
   */
  public ConfigHandlingException(String msg) {
    super(msg);
  }

  /**
   * Creates a new configuration handling exception with the specified cause.
   *
   * @param cause the cause of this exception
   */
  public ConfigHandlingException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new configuration handling exception with the specified detail message and cause.
   *
   * @param msg the detail message
   * @param cause the cause of this exception
   */
  public ConfigHandlingException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
