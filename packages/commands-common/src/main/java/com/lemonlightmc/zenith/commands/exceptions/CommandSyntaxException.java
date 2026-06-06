package com.lemonlightmc.zenith.commands.exceptions;

import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.exceptions.ExceptionContainer;

public class CommandSyntaxException extends CommandException {
  public static final int CONTEXT_AMOUNT = 10;
  public static boolean enableCommandStackTraces = true;

  private final String message;
  private final StringReader reader;

  public CommandSyntaxException(final StringReader reader, final String message) {
    super(message, null, enableCommandStackTraces);
    this.message = message;
    this.reader = reader;
  }

  public CommandSyntaxException(final StringReader reader, final String message,
      final boolean enableCommandStackTrace) {
    super(message, null, enableCommandStackTrace);
    this.message = message;
    this.reader = reader;
  }

  public static void setCommandStackTraces(final boolean value) {
    enableCommandStackTraces = value;
  }

  public static boolean hasCommandStackTraces() {
    return enableCommandStackTraces;
  }

  public String getRawMessage() {
    return message;
  }

  public String getInput() {
    return reader.getString();
  }

  public int getCursor() {
    return reader.getCursor();
  }

  public StringReader getReader() {
    return reader;
  }

  @Override
  public String getMessage() {
    String message = this.message;
    final String context = getContext();
    if (context != null) {
      message += " at position " + getCursor() + ": " + context;
    }
    return message;
  }

  public String getContext() {
    if (getInput() == null || getCursor() < 0) {
      return null;
    }
    final StringBuilder builder = new StringBuilder();
    final int cursor = Math.min(getInput().length(), this.getCursor());

    if (cursor > CONTEXT_AMOUNT) {
      builder.append("...");
    }

    builder.append(getInput().substring(Math.max(0, cursor - CONTEXT_AMOUNT), cursor));
    builder.append("<--[HERE]");

    return builder.toString();
  }

  public static class CommandSyntaxExceptionContainer extends ExceptionContainer<CommandSyntaxException> {
    public CommandSyntaxExceptionContainer(final ExceptionContainerFunction function) {
      super(CommandSyntaxException.class, function);
    }

    public CommandSyntaxExceptionContainer(final String message) {
      super(CommandSyntaxException.class, message);
    }
  }
}
