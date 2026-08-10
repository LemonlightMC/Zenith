package com.lemonlightmc.zenith.additive.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import com.lemonlightmc.zenith.additive.Reflect;

public final class GlobalLogger {
  private static final ConcurrentMap<String, LoggerAdapter> loggerRegistry;
  private static final String JUL_ROOT_LOGGER_NAME = "";
  private static final Level defaultLevel;
  private static final Map<String, Level> logLevelMap;

  static {
    loggerRegistry = new ConcurrentHashMap<String, LoggerAdapter>();
    defaultLevel = Level.INFO;
    logLevelMap = new ConcurrentHashMap<String, Level>();

    // ensure jul initialization.
    java.util.logging.Logger.getLogger("");
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
        ? createLogger(name, null)
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
        ? createLogger(name, level)
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
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), null);
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
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), level);
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
        ? createLogger(name, null)
        : getLogger(Reflect.getCallerClass(2), null);
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
        ? createLogger(name, level)
        : getLogger(Reflect.getCallerClass(2), level);
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
    return getLogger(value != null ? value.getClass() : Reflect.getCallerClass(2), null);
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
    return getLogger(value != null ? value.getClass() : Reflect.getCallerClass(2), level);
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
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), null).setParent(parent);
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
    return createLogger(canonicalName != null ? canonicalName : cls.getName(), level);
  }

  private static LoggerAdapter createLogger(String name, Level level) {
    // the root logger is called "" in JUL
    if (name.equalsIgnoreCase(Logger.ROOT_LOGGER_NAME)) {
      name = JUL_ROOT_LOGGER_NAME;
    }
    if (level == null) {
      level = logLevelMap.isEmpty() ? defaultLevel : logLevelMap.get(name);
      if (level == null) {
        level = defaultLevel;
      }
    }

    final LoggerAdapter oldLogger = loggerRegistry.get(name);
    if (oldLogger != null)
      return oldLogger;
    else {
      final LoggerAdapter newInstance = new LoggerAdapter(java.util.logging.Logger.getLogger(name), name, level);
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
}