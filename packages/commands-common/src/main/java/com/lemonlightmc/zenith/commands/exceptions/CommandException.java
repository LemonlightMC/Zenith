package com.lemonlightmc.zenith.commands.exceptions;

public class CommandException extends RuntimeException {

  public CommandException() {
  }

  public CommandException(String msg) {
    super(msg);
  }

  public CommandException(String msg, Throwable cause) {
    super(msg, cause);
  }

  protected CommandException(String msg, Throwable cause, boolean enableStackTrace) {
    super(msg, cause, enableStackTrace, enableStackTrace);
  }
}
