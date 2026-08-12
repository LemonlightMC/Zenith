package com.lemonlightmc.zenith.additive.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import com.lemonlightmc.zenith.additive.Lazy;
import com.lemonlightmc.zenith.additive.Reflect;

public final class GlobalLogger {
  private static final ConcurrentMap<String, LoggerAdapter> loggerRegistry;
  private static final String JUL_ROOT_LOGGER_NAME = "";
  private static Level defaultLevel = Level.INFO;
  private static Map<String, Level> logLevelMap = null;
  private static Lazy<LoggerAdapter> globalLogger = Lazy.of(() -> getLogger(""));

  static {
    loggerRegistry = new ConcurrentHashMap<String, LoggerAdapter>();

    // ensure jul initialization.
    java.util.logging.Logger.getLogger("");
  }

  public static void setRootLogger(final java.util.logging.Logger logger) {
    globalLogger = Lazy.of(() -> globalLogger.get().setParent(logger));
  }

  public static LoggerAdapter getRootLogger() {
    return globalLogger.get();
  }

  public static void setDefaultLogLevel(final Level level) {
    if (level == null) {
      throw new IllegalArgumentException("Default LogLevel of GlobalLogger cannot be null!");
    }
    GlobalLogger.defaultLevel = level;
  }

  public static void setLogLevelMapping(final Map<String, Level> logLevelMap) {
    GlobalLogger.logLevelMap = logLevelMap;
  }

  /**
   * Returns a Logger with the name of the calling class.
   *
   * @return The Logger for the calling class.
   * @throws UnsupportedOperationException if the calling class cannot be
   *                                       determined.
   */
  public static LoggerAdapter getLogger() {
    return getLogger(Reflect.getCallerClass(2), null);
  }

  /**
   * Gets an Logger.
   * 
   * @param name The name of the Logger to return.
   * @return The logger with the specified name.
   */
  public static LoggerAdapter getLogger(final String name) {
    return name != null
        ? createLogger(name, null, null)
        : getLogger(Reflect.getCallerClass(2), null);
  }

  /**
   * Gets an Logger.
   * 
   * @param name  The name of the Logger to return.
   * @param level The Level to use for the logger.
   * @return The logger with the specified name.
   */
  public static LoggerAdapter getLogger(final String name, final Level level) {
    return name != null
        ? createLogger(name, level, null)
        : getLogger(Reflect.getCallerClass(2), level);
  }

  /**
   * Returns a Logger using the fully qualified class name of the value as the
   * Logger name.
   *
   * @param value The value whose class name should be used as the Logger name. If
   *              null the name of the calling class
   *              will be used as the logger name.
   * @return The Logger.
   * @throws UnsupportedOperationException if {@code value} is {@code null} and
   *                                       the calling class cannot be
   *                                       determined.
   */
  public static LoggerAdapter getLogger(final Object value) {
    return getLogger(value != null ? value.getClass() : Reflect.getCallerClass(2), null);
  }

  /**
   * Returns a Logger using the fully qualified class name of the value as the
   * Logger name.
   *
   * @param value The value whose class name should be used as the Logger name. If
   *              null the name of the calling class
   *              will be used as the logger name.
   * @param level The Level to use for the logger.
   * @return The Logger.
   * @throws UnsupportedOperationException if {@code value} is {@code null} and
   *                                       the calling class cannot be
   *                                       determined.
   */
  public static LoggerAdapter getLogger(final Object value, final Level level) {
    return getLogger(value != null ? value.getClass() : Reflect.getCallerClass(2), level);
  }

  /**
   * Gets an Logger using the fully qualified name of the Class as the
   * Logger name.
   * 
   * @param cls The Class whose name should be used as the Logger name.
   * @return The logger.
   */
  public static LoggerAdapter getLogger(Class<?> cls) {
    if (cls == null) {
      cls = Reflect.getCallerClass(2);
      if (cls == null) {
        throw new IllegalArgumentException("No class provided, and an appropriate one cannot be found.");
      }
    }
    final String canonicalName = cls.getCanonicalName();
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), null, null);
  }

  /**
   * Gets an Logger using the fully qualified name of the Class as the
   * Logger name.
   * 
   * @param cls   The Class whose name should be used as the Logger name.
   * @param level The Level to use for the logger.
   * @return The logger.
   */
  public static LoggerAdapter getLogger(Class<?> cls, final Level level) {
    if (cls == null) {
      cls = Reflect.getCallerClass(2);
      if (cls == null) {
        throw new IllegalArgumentException("No class provided, and an appropriate one cannot be found.");
      }
    }
    final String canonicalName = cls.getCanonicalName();
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), level, null);
  }

  /**
   * Gets an Logger.
   * 
   * @param parent The parent logger to use.
   * @param name   The name of the Logger to return.
   * @return The logger with the specified name.
   */
  public static LoggerAdapter getLogger(final Logger parent, final String name) {
    return name != null
        ? createLogger(name, null, parent)
        : getLogger(parent, Reflect.getCallerClass(2), null);
  }

  /**
   * Gets an Logger.
   * 
   * @param parent The parent logger to use.
   * @param name   The name of the Logger to return.
   * @param level  The Level to use for the logger.
   * @return The logger with the specified name.
   */
  public static LoggerAdapter getLogger(final Logger parent, final String name, final Level level) {
    return name != null
        ? createLogger(name, level, parent)
        : getLogger(parent, Reflect.getCallerClass(2), level);
  }

  /**
   * Returns a Logger using the fully qualified class name of the value as the
   * Logger name.
   *
   * @param parent The parent logger to use.
   * @param value  The value whose class name should be used as the Logger name.
   *               If
   *               null the name of the calling class
   *               will be used as the logger name.
   * @return The Logger.
   * @throws UnsupportedOperationException if {@code value} is {@code null} and
   *                                       the calling class cannot be
   *                                       determined.
   */
  public static LoggerAdapter getLogger(final Logger parent, final Object value) {
    return getLogger(parent, value != null ? value.getClass() : Reflect.getCallerClass(2), null);
  }

  /**
   * Returns a Logger using the fully qualified class name of the value as the
   * Logger name.
   *
   * @param parent The parent logger to use.
   * @param value  The value whose class name should be used as the Logger name.
   *               If
   *               null the name of the calling class
   *               will be used as the logger name.
   * @param level  The Level to use for the logger.
   * @return The Logger.
   * @throws UnsupportedOperationException if {@code value} is {@code null} and
   *                                       the calling class cannot be
   *                                       determined.
   */
  public static LoggerAdapter getLogger(final Logger parent, final Object value, final Level level) {
    return getLogger(parent, value != null ? value.getClass() : Reflect.getCallerClass(2), level);
  }

  /**
   * Gets an Logger using the fully qualified name of the Class as the
   * Logger name.
   *
   * @param parent The parent logger to use.
   * @param cls    The Class whose name should be used as the Logger name.
   * @return The logger.
   */
  public static LoggerAdapter getLogger(final Logger parent, Class<?> cls) {
    if (cls == null) {
      cls = Reflect.getCallerClass(2);
      if (cls == null) {
        throw new IllegalArgumentException("No class provided, and an appropriate one cannot be found.");
      }
    }
    final String canonicalName = cls.getCanonicalName();
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), null, parent);
  }

  /**
   * Gets an Logger using the fully qualified name of the Class as the
   * Logger name.
   * 
   * @param parent The parent logger to use.
   * @param cls    The Class whose name should be used as the Logger name.
   * @param level  The Level to use for the logger.
   * @return The logger.
   */
  public static LoggerAdapter getLogger(final Logger parent, Class<?> cls, final Level level) {
    if (cls == null) {
      cls = Reflect.getCallerClass(2);
      if (cls == null) {
        throw new IllegalArgumentException("No class provided, and an appropriate one cannot be found.");
      }
    }
    final String canonicalName = cls.getCanonicalName();
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), level, null);
  }

  private static LoggerAdapter createLogger(String name, Level level, final Logger parent) {
    // the root logger is called "" in JUL
    if (name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) {
      name = JUL_ROOT_LOGGER_NAME;
    }
    if (level == null) {
      level = logLevelMap == null || logLevelMap.isEmpty() ? defaultLevel : logLevelMap.get(name);
      if (level == null) {
        level = defaultLevel;
      }
    }

    final LoggerAdapter oldLogger = loggerRegistry.get(name);
    if (oldLogger != null)
      return oldLogger;
    else {
      final LoggerAdapter newInstance = new LoggerAdapter(java.util.logging.Logger.getLogger(name), name, level);
      newInstance.setParent(parent);
      final LoggerAdapter oldInstance = loggerRegistry.putIfAbsent(name, newInstance);
      return oldInstance == null ? newInstance : oldInstance;
    }
  }

  public static boolean hasLogger(String name) {
    // the root logger is called "" in JUL
    if (name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) {
      name = JUL_ROOT_LOGGER_NAME;
    }
    return loggerRegistry.containsKey(name);
  }

  public static void trace(final String msg) {
    globalLogger.get().trace(msg);
  }

  public static void trace(final String msg, final Object arg) {
    globalLogger.get().trace(msg, arg);
  }

  public static void trace(final String msg, final Object arg1, final Object arg2) {
    globalLogger.get().trace(msg, arg1, arg2);
  }

  public static void trace(final String msg, final Object... arguments) {
    globalLogger.get().trace(msg, arguments);
  }

  public static void trace(final Supplier<?> msgSupplier) {
    globalLogger.get().trace(msgSupplier);
  }

  public static void trace(final String msg, final Supplier<?>... paramSuppliers) {
    globalLogger.get().trace(msg, paramSuppliers);
  }

  public static void debug(final String msg) {
    globalLogger.get().debug(msg);
  }

  public static void debug(final String msg, final Object arg) {
    globalLogger.get().debug(msg, arg);
  }

  public static void debug(final String msg, final Object arg1, final Object arg2) {
    globalLogger.get().debug(msg, arg1, arg2);
  }

  public static void debug(final String msg, final Object... arguments) {
    globalLogger.get().debug(msg, arguments);
  }

  public static void debug(final Supplier<?> msgSupplier) {
    globalLogger.get().debug(msgSupplier);
  }

  public static void debug(final String msg, final Supplier<?>... paramSuppliers) {
    globalLogger.get().debug(msg, paramSuppliers);
  }

  public static void info(final String msg) {
    globalLogger.get().info(msg);
  }

  public static void info(final String msg, final Object arg) {
    globalLogger.get().info(msg, arg);
  }

  public static void info(final String msg, final Object arg1, final Object arg2) {
    globalLogger.get().info(msg, arg1, arg2);
  }

  public static void info(final String msg, final Object... arguments) {
    globalLogger.get().info(msg, arguments);
  }

  public static void info(final Supplier<?> msgSupplier) {
    globalLogger.get().info(msgSupplier);
  }

  public static void info(final String msg, final Supplier<?>... paramSuppliers) {
    globalLogger.get().info(msg, paramSuppliers);
  }

  public static void warn(final String msg) {
    globalLogger.get().warn(msg);
  }

  public static void warn(final String msg, final Object arg) {
    globalLogger.get().warn(msg, arg);
  }

  public static void warn(final String msg, final Object arg1, final Object arg2) {
    globalLogger.get().warn(msg, arg1, arg2);
  }

  public static void warn(final String msg, final Object... arguments) {
    globalLogger.get().warn(msg, arguments);
  }

  public static void warn(final Supplier<?> msgSupplier) {
    globalLogger.get().warn(msgSupplier);
  }

  public static void warn(final String msg, final Supplier<?>... paramSuppliers) {
    globalLogger.get().warn(msg, paramSuppliers);
  }

  public static void error(final String msg) {
    globalLogger.get().error(msg);
  }

  public static void error(final String msg, final Object arg) {
    globalLogger.get().error(msg, arg);
  }

  public static void error(final String msg, final Object arg1, final Object arg2) {
    globalLogger.get().error(msg, arg1, arg2);
  }

  public static void error(final String msg, final Object... arguments) {
    globalLogger.get().error(msg, arguments);
  }

  public static void error(final Supplier<?> msgSupplier) {
    globalLogger.get().error(msgSupplier);
  }

  public static void error(final String msg, final Supplier<?>... paramSuppliers) {
    globalLogger.get().error(msg, paramSuppliers);
  }
}