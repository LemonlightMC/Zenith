package com.lemonlightmc.zenith.base.logger;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.spi.DefaultLoggingEventBuilder;

/**
 * This class is responsible for adapting the Bukkit logger instance against the
 * SLF4J {@link
 * org.slf4j.Logger} interface.
 */
public final class BukkitLoggerAdapter extends LegacyAbstractLogger {

  private final transient Logger logger;

  public BukkitLoggerAdapter(final Logger logger, final String name) {
    super.name = name;
    this.logger = logger;
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isLoggable(Level.FINEST);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isLoggable(Level.FINE);
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isLoggable(Level.INFO);
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isLoggable(Level.WARNING);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isLoggable(Level.SEVERE);
  }

  @Override
  protected String getFullyQualifiedCallerName() {
    return getClass().getName();
  }

  @Override
  protected void handleNormalizedLoggingCall(final org.slf4j.event.Level level, final org.slf4j.Marker marker,
      String msg, final Object[] args, final Throwable throwable) {
    final Level julLevel = toJulLevel(level);
    if (!logger.isLoggable(julLevel)) {
      return;
    }

    msg = MessageFormatter.basicArrayFormat(msg, args);
    final LogRecord julLogRecord = new LogRecord(julLevel, msg);
    julLogRecord.setLoggerName(name);
    julLogRecord.setThrown(throwable);
    inferCallerLocation(julLogRecord, getClass().getName());

    logger.log(julLogRecord);
  }

  private Level toJulLevel(final org.slf4j.event.Level slf4jLevel) {
    switch (slf4jLevel) {
      case TRACE:
        return Level.FINEST;
      case DEBUG:
        return Level.FINE;
      case INFO:
        return Level.INFO;
      case WARN:
        return Level.WARNING;
      case ERROR:
        return Level.SEVERE;
      default:
        throw new IllegalStateException("Unrecognized SLF4J level: " + slf4jLevel);
    }
  }

  private static void inferCallerLocation(
      final LogRecord julLogRecord, final String adapterOrSubstituteCallerFqcn) {
    // The first element is the top-most call on the execution stack i.e. always the
    // following line:
    // com.djaytan.bukkit.slf4j/com.djaytan.bukkit.slf4j.internal.JulLogRecordFactory.inferCallerLocation(JulLogRecordFactory.java:<line_number>)
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();

    // First, search for a method in a logger implementation class.
    int firstLoggerImplClassIndex = -1;
    for (int i = 0; i < stackTraceElements.length; i++) {
      final String className = stackTraceElements[i].getClassName();

      if (isLoggerImplClass(className, adapterOrSubstituteCallerFqcn)) {
        firstLoggerImplClassIndex = i;
        break;
      }
    }

    // Now search for the first frame called before the logger implementation
    // classes.
    int inferedCallerClassNameIndex = -1;
    for (int i = firstLoggerImplClassIndex + 1; i < stackTraceElements.length; i++) {
      final String className = stackTraceElements[i].getClassName();

      if (!isLoggerImplClass(className, adapterOrSubstituteCallerFqcn)) {
        inferedCallerClassNameIndex = i;
        break;
      }
    }

    // We haven't found a suitable frame, so let's just punt. This is acceptable as
    // we are only
    // committed to making a "best effort" here.
    if (inferedCallerClassNameIndex == -1) {
      return;
    }

    final StackTraceElement stackTraceElement = stackTraceElements[inferedCallerClassNameIndex];
    julLogRecord.setSourceClassName(stackTraceElement.getClassName());
    julLogRecord.setSourceMethodName(stackTraceElement.getMethodName());
  }

  private static final String[] LOGGER_IMPL_CLASS_NAMES = {
      BukkitLoggerAdapter.class.getName(),
      LegacyAbstractLogger.class.getName(),
      AbstractLogger.class.getName(),
      SubstituteLogger.class.getName(),
      DefaultLoggingEventBuilder.class.getName(),
  };

  private static boolean isLoggerImplClass(
      final String className, final String adapterOrSubstituteCallerFqcn) {
    if (className.equals(adapterOrSubstituteCallerFqcn)) {
      return true;
    }

    for (final String loggerImplClassName : LOGGER_IMPL_CLASS_NAMES) {
      if (loggerImplClassName.equals(className)) {
        return true;
      }
    }
    return false;
  }
}