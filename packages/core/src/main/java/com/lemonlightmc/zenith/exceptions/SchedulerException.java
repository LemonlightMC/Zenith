package com.lemonlightmc.zenith.exceptions;

/**
 * Indicates an error related to task scheduling or scheduled task execution.
 */
public class SchedulerException extends RuntimeException {

  /**
   * Creates a new scheduler exception with the specified detail message.
   *
   * @param message the detail message
   */
  public SchedulerException(String message) {
    super("Exception occured in Scheduler: " + message);
  }

  /**
   * Creates a new scheduler exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public SchedulerException(String message, Throwable cause) {
    super("Exception occured in Scheduler: " + message, cause);
  }

  /**
   * Creates a new scheduler exception with the specified cause.
   *
   * @param cause the cause of this exception
   */
  public SchedulerException(Throwable cause) {
    super("Exception occured in Scheduler!", cause);
  }
}
