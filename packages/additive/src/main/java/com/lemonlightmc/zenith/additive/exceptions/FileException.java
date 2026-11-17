package com.lemonlightmc.zenith.additive.exceptions;

/**
 * Indicates a failure while performing a file-related operation.
 */
public class FileException extends RuntimeException {

  /**
   * Creates a new file exception with no detail message or cause.
   */
  public FileException() {
  }

  /**
   * Creates a new file exception with the specified detail message.
   *
   * @param msg the detail message
   */
  public FileException(String msg) {
    super(msg);
  }

  /**
   * Creates a new file exception with the specified cause.
   *
   * @param cause the cause of this exception
   */
  public FileException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new file exception with the specified detail message and cause.
   *
   * @param msg the detail message
   * @param cause the cause of this exception
   */
  public FileException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
