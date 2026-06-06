package com.lemonlightmc.zenith.commands.exceptions;

public class MissingCommandExecutorException extends CommandException {

  public MissingCommandExecutorException(final String commandName) {
    super("Cant register a Command " + commandName + " because it does not declare any executors!");
  }
}
