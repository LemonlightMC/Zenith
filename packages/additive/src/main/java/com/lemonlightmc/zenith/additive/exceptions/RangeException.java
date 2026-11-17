package com.lemonlightmc.zenith.additive.exceptions;

/**
 * Indicates that a value is outside an expected range.
 */
public class RangeException extends RuntimeException {

  /**
   * Creates a new range exception with the specified detail message.
   *
   * @param message the detail message
   */
  public RangeException(String message) {
    super(message);
  }
}
