package com.lemonlightmc.zenith.exceptions;

/**
 * Indicates a failure while loading, unloading, or reloading a module.
 */
public class ModuleLoadException extends RuntimeException {

  /**
   * Creates a new module load exception with the specified detail message.
   *
   * @param message the detail message
   */
  public ModuleLoadException(String message) {
    super(message);
  }
}
