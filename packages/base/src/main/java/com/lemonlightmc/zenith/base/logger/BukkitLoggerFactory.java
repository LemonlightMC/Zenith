package com.lemonlightmc.zenith.base.logger;

import org.slf4j.ILoggerFactory;

/**
 * This class is responsible for creating SLF4J loggers based on a provided JUL
 * one. This is a
 * specific scenario that we need to rely on when creating Bukkit plugins.
 * That's due to the logging
 * design being based on a single instance shared across the whole plugin
 * (mainly for appending the
 * plugin name while not displaying technical information such as the class full
 * name).
 */
public final class BukkitLoggerFactory implements ILoggerFactory {

  private static java.util.logging.Logger staticLogger;

  public BukkitLoggerFactory() {
    // Bukkit logger must be provided by the library consumer
  }

  /**
   * Provides the Bukkit logger to be returned by any subsequent call to {@link
   * org.slf4j.LoggerFactory#getLogger(String)}.
   *
   * @param jdk14Logger The single JUL logger instance to be used each time the
   *                    {@link
   *                    org.slf4j.LoggerFactory#getLogger(String)} method is
   *                    called.
   */
  public static void provideBukkitLogger(final java.util.logging.Logger jdk14Logger) {
    if (staticLogger != null) {
      staticLogger.warning(
          "Bukkit logger redefinition attempt detected! Any such action is considered as "
              + "unexpected and thus is simply ignored to avoid harms");
      return;
    }
    staticLogger = jdk14Logger;
  }

  /** Resets the Bukkit logger. */
  static void resetBukkitLogger() {
    staticLogger = null;
  }

  @Override
  public org.slf4j.Logger getLogger(final String name) {
    // Name provided as argument is discarded in favor of the static Bukkit logger
    // one
    if (staticLogger == null) {
      throw new IllegalStateException("The Bukkit logger must be defined first");
    }
    return new BukkitLoggerAdapter(staticLogger, staticLogger.getName());
  }
}
