package com.lemonlightmc.zenith.exceptions;

/**
 * Signals that a required runtime or dependency version is outdated.
 */
public class OutdatedVersionError extends Error {

  /**
   * Creates a new outdated version error with the specified detail message.
   *
   * @param message the detail message
   */
  public OutdatedVersionError(final String message) {
    super(message);
  }
}
