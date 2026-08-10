package com.lemonlightmc.zenith.additive.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.lemonlightmc.zenith.additive.Reflect;

final class LoggerFactory implements ILoggerFactory {

  public LoggerFactory() {
  }

  @Override
  public Logger getLogger(final String name) {
    return name != null
        ? GlobalLogger.getLogger(name, null)
        : GlobalLogger.getLogger(Reflect.getCallerClass(2), null);
  }

}