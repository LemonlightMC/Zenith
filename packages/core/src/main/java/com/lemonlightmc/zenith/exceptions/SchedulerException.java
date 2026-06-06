package com.lemonlightmc.zenith.exceptions;

public class SchedulerException extends RuntimeException {

  public SchedulerException(String message) {
    super("Exception occured in Scheduler: " + message);
  }

  public SchedulerException(String message, Throwable cause) {
    super("Exception occured in Scheduler: " + message, cause);
  }

  public SchedulerException(Throwable cause) {
    super("Exception occured in Scheduler!", cause);
  }
}
