package com.lemonlightmc.zenith.additive.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.lemonlightmc.zenith.additive.Reflect;

/**
 * SLF4J logger factory implementation backed by {@link GlobalLogger}.
 */
final class LoggerFactory implements ILoggerFactory {

  /**
   * Creates a new logger factory instance.
   */
  public LoggerFactory() {
  }

  /**
   * Returns a logger for the given name.
   *
   * @param name the logger name
   * @return the logger instance for the given name
   */
  @Override
  public Logger getLogger(final String name) {
    return name != null
        ? GlobalLogger.getLogger(name, null)
        : GlobalLogger.getLogger(Reflect.getCallerClass(2), null);
  }

}