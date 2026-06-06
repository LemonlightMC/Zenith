package com.lemonlightmc.zenith.commands.exceptions;

public class DuplicateArgumentException extends CommandException {

  public DuplicateArgumentException(final String argName) {
    super("Failed to register Argument" + argName + " because it is a duplicate!");
  }
}
