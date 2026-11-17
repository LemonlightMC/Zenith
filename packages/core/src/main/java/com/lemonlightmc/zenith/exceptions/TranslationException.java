package com.lemonlightmc.zenith.exceptions;

/**
 * Indicates a failure during message translation or localization.
 */
public class TranslationException extends RuntimeException {
  /**
   * Creates a new translation exception with no detail message.
   */
  public TranslationException() {
    super();
  }

  /**
   * Creates a new translation exception with the specified detail message.
   *
   * @param message the detail message
   */
  public TranslationException(String message) {
    super(message);
  }

  /**
   * Creates a new translation exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new translation exception with the specified cause.
   *
   * @param cause the cause of this exception
   */
  public TranslationException(Throwable cause) {
    super(cause);
  }
}
