package com.lemonlightmc.zenith.additive.logger;

import com.lemonlightmc.zenith.additive.StringFormatter;

/**
 * Represents a formatted trace entry message for logger entry tracing.
 *
 * @param message the formatted entry message
 */
public record EntryMessage(String message) {
  private static final String EMPTY_MESSAGE = "params()";

  /**
   * Creates an empty entry message.
   *
   * @return an empty entry message
   */
  public static EntryMessage of() {
    return new EntryMessage(EMPTY_MESSAGE);
  }

  /**
   * Creates an entry message from the given text.
   *
   * @param message the message text
   * @return an entry message containing the provided or default text
   */
  public static EntryMessage of(final String message) {
    return new EntryMessage(message == null || message.isEmpty() ? EMPTY_MESSAGE : message);
  }

  /**
   * Creates a formatted entry message from a message pattern and parameters.
   *
   * @param message the message pattern
   * @param params the formatting parameters
   * @return a formatted entry message
   */
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

  /**
   * Logs this entry message at trace level using the given logger.
   *
   * @param logger the logger to write to
   * @return this entry message instance
   */
  public EntryMessage log(LoggerAdapter logger) {
    logger.trace(message);
    return this;
  }
}
