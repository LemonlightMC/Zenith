package com.lemonlightmc.zenith.exceptions;

/**
 * Exception thrown when dependency resolution or download fails.
 */
public class DependencyException extends RuntimeException {

  private final String dependencyName;

  public DependencyException(String dependencyName, String message) {
    super(message);
    this.dependencyName = dependencyName;
  }

  public DependencyException(String dependencyName, String message, Throwable cause) {
    super(message, cause);
    this.dependencyName = dependencyName;
  }

  public String getDependencyName() {
    return dependencyName;
  }

  @Override
  public String getMessage() {
    return "[" + dependencyName + "] " + super.getMessage();
  }
}
