package com.lemonlightmc.zenith.commands.exceptions;

import java.util.List;

import com.lemonlightmc.zenith.commands.argumentsbase.Argument;

public class GreedyArgumentException extends CommandException {

  public GreedyArgumentException(final String argName, final String str) {
    super("Argument " + argName
        + " does not satisfies GreedyStringArgument requirements! Only one GreedyStringArgument can be declared, at the end of a List"
        + " Found arguments: " + str);
  }

  public static <T> String buildArgsStr(final List<Argument<?, ?, T>> arguments) {
    final StringBuilder builder = new StringBuilder();
    for (final Argument<?, ?, T> arg : arguments) {
      builder.append(arg.getName()).append("<").append(arg.getClass().getSimpleName()).append("> ");
    }
    return builder.toString();
  }
}
