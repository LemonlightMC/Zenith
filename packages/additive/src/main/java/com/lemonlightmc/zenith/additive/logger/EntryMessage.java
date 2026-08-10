package com.lemonlightmc.zenith.additive.logger;

import com.lemonlightmc.zenith.additive.StringFormatter;

public record EntryMessage(String message) {
  private static final String EMPTY_MESSAGE = "params()";

  public static EntryMessage of() {
    return new EntryMessage(EMPTY_MESSAGE);
  }

  public static EntryMessage of(final String message) {
    return new EntryMessage(message == null || message.isEmpty() ? EMPTY_MESSAGE : message);
  }

  public static EntryMessage of(final String message, final Object... params) {
    if (message != null && !message.isEmpty()) {
      return new EntryMessage(StringFormatter.format(message, params));
    } else if (params == null || params.length == 0) {
      return new EntryMessage(EMPTY_MESSAGE);
    } else {
      final StringBuilder sb = new StringBuilder("params(");
      for (int i = 0; i < params.length; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append("{}");
      }
      sb.append(")");
      return new EntryMessage(StringFormatter.format(sb.toString(), params));
    }
  }

  public EntryMessage log(LoggerAdapter logger) {
    logger.trace(message);
    return this;
  }
}
