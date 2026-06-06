package com.lemonlightmc.zenith.commands.exceptions;

public class InvalidCommandNameException extends CommandException {

  public InvalidCommandNameException(final String commandName) {
    super(
        "Invalid command with name '" + commandName + "' cannot be registered!");
  }
}
