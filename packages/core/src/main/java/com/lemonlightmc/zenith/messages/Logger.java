package com.lemonlightmc.zenith.messages;

import java.util.Locale;

import com.lemonlightmc.zenith.ZenithProvider;

public class Logger {
  private final static Locale locale = ZenithProvider.config().get("localization.logger-locale", Locale.ENGLISH);

  private static String retrieveMessage(String msg) {
    if (msg == null || msg.length() == 0) {
      return null;
    }
    if (msg.startsWith("messages.")) {
      msg = ZenithProvider.instance().getMessageAPI().translate(msg.substring(9), locale);
    }
    return MessageFormatter.format(msg);
  }

  // LoggerContext
  /*
   * public static class ZenithLoggerContext implements LoggerContext {
   * static final ZenithLoggerContext INSTANCE = new ZenithLoggerContext();
   * 
   * private final boolean showLogName;
   * private final boolean showThreadContext;
   * private final boolean showDateTime;
   * private final String dateTimeFormat;
   * 
   * private final Level defaultLevel;
   * private final Map<String, Level> logLevelMap;
   * 
   * public ZenithLoggerContext() {
   * showLogName = ZenithProvider.config().get("logging.format.showLoggerName",
   * true);
   * showThreadContext =
   * ZenithProvider.config().get("logging.format.showThreadContext", true);
   * showDateTime = ZenithProvider.config().get("logging.format.showDateTime",
   * true);
   * dateTimeFormat = ZenithProvider.config().get("logging.format.dateTimeFormat",
   * "yyyy/MM/dd HH:mm:ss:SSS zzz");
   * defaultLevel = ZenithProvider.config().get("logging.loglevels.default",
   * Level.WARNING);
   * logLevelMap = ZenithProvider.config().get("logging.loglevels",
   * Map.of("default", Level.WARNING));
   * }
   * 
   * }
   */
}
