package com.lemonlightmc.zenith.commands.exceptions;

public class InvalidArgumentBranchException extends CommandException {
  public InvalidArgumentBranchException(final String reason, final String argName) {
    super(
        "Branch cant be created due " + reason + " for Argument '" + argName + "'");
  }
}
