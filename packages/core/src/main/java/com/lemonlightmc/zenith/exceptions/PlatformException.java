package com.lemonlightmc.zenith.exceptions;

/**
 * Indicates an error related to platform-specific behavior or compatibility.
 */
public class PlatformException extends RuntimeException {

  /**
   * Creates a new platform exception with the specified detail message.
   *
   * @param message the detail message
   */
  public PlatformException(String message) {
    super(message);
  }
}
