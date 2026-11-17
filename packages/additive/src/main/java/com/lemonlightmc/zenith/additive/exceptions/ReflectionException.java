package com.lemonlightmc.zenith.additive.exceptions;

/**
 * Indicates a failure while performing a reflective operation.
 */
public class ReflectionException extends RuntimeException {

  /**
   * Creates a new reflection exception with the specified detail message.
   *
   * @param message the detail message
   */
  public ReflectionException(String message) {
    super("Failed at Reflection due " + message);
  }

  /**
   * Creates a new reflection exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public ReflectionException(String message, Throwable cause) {
    super("Failed at Reflection due " + message, cause);
  }

  /**
   * Creates a new reflection exception with the specified cause.
   *
   * @param cause the cause of this exception
   */
  public ReflectionException(Throwable cause) {
    super("Failed at Reflection due ", cause);
  }
}
