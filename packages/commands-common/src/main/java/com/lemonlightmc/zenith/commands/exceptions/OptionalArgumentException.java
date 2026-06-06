package com.lemonlightmc.zenith.commands.exceptions;

public class OptionalArgumentException extends CommandException {
  public OptionalArgumentException(final String argName1, final String argName2) {
    super(
        "Failed to register required argument " + argName1
            + " because it cannot follow the optional argument " + argName2);
  }

  public OptionalArgumentException(final String argName1) {
    super(
        "Failed to register required argument " + argName1
            + " because it cannot follow the optional argument!");
  }
}
