package com.lemonlightmc.zenith.exceptions;

/**
 * Exception thrown when dependency resolution or download fails.
 */
public class DependencyException extends RuntimeException {

  private final String dependencyName;

  /**
   * Creates a new dependency exception with the specified dependency name and message.
   *
   * @param dependencyName the name of the dependency involved in the failure
   * @param message the detail message
   */
  public DependencyException(String dependencyName, String message) {
    super(message);
    this.dependencyName = dependencyName;
  }

  /**
   * Creates a new dependency exception with the specified dependency name, message, and cause.
   *
   * @param dependencyName the name of the dependency involved in the failure
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public DependencyException(String dependencyName, String message, Throwable cause) {
    super(message, cause);
    this.dependencyName = dependencyName;
  }

  /**
   * Gets the name of the dependency associated with this failure.
   *
   * @return the dependency name
   */
  public String getDependencyName() {
    return dependencyName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMessage() {
    return "[" + dependencyName + "] " + super.getMessage();
  }
}
