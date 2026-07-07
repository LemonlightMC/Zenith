package com.lemonlightmc.zenith.messages;

import java.io.PrintStream;
import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.simple.internal.SimpleProvider;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.bukkit.Bukkit;

import com.lemonlightmc.zenith.ZenithProvider;

public class Logger {
  private final static Locale locale = ZenithProvider.config().get("localization.logger-locale", Locale.ENGLISH);

  public static java.util.logging.Logger bukkitLogger() {
    return Bukkit.getLogger();
  }

  public static org.apache.logging.log4j.Logger logger() {
    return ZenithProvider.instance().getLog4jLogger();
  }

  private static String retrieveMessage(String msg) {
    if (msg == null || msg.length() == 0) {
      return null;
    }
    if (msg.startsWith("messages.")) {
      msg = ZenithProvider.instance().getMessageAPI().translate(msg.substring(9), locale);
    }
    return MessageFormatter.format(msg);
  }

  public static org.apache.logging.log4j.Logger getLogger() {
    return ZenithLoggerContext.INSTANCE.getLogger();
  }

  public static org.apache.logging.log4j.Logger getLogger(final String name) {
    return ZenithLoggerContext.INSTANCE.getLogger(name);
  }

  public static org.apache.logging.log4j.Logger getLogger(final String name, final Level level) {
    return ZenithLoggerContext.INSTANCE.getLogger(name, level);
  }

  public static org.apache.logging.log4j.Logger getLogger(final String name, final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(name, factory);
  }

  public static org.apache.logging.log4j.Logger getLogger(final String name, final Level level,
      final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(name);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Class<?> cls) {
    return ZenithLoggerContext.INSTANCE.getLogger(cls);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Class<?> cls, final Level level) {
    return ZenithLoggerContext.INSTANCE.getLogger(cls, level);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Class<?> cls, final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(cls, factory);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Class<?> cls, final Level level,
      final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(cls, level, factory);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Object value) {
    return ZenithLoggerContext.INSTANCE.getLogger(value);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Object value, final Level level) {
    return ZenithLoggerContext.INSTANCE.getLogger(value, level);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Object value, final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(value, factory);
  }

  public static org.apache.logging.log4j.Logger getLogger(final Object value, final Level level,
      final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(value, level, factory);
  }

  public static org.apache.logging.log4j.Logger getLogger(final org.apache.logging.log4j.Logger parent,
      final String name) {
    return ZenithLoggerContext.INSTANCE.getLogger(parent.getName() + " " + name);
  }

  public static org.apache.logging.log4j.Logger getLogger(final org.apache.logging.log4j.Logger parent,
      final String name, final Level level) {
    return ZenithLoggerContext.INSTANCE.getLogger(parent.getName() + " " + name, level);
  }

  public static org.apache.logging.log4j.Logger getLogger(final org.apache.logging.log4j.Logger parent,
      final String name,
      final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(parent.getName() + " " + name, factory);
  }

  public static org.apache.logging.log4j.Logger getLogger(final org.apache.logging.log4j.Logger parent,
      final String name, final Level level,
      final MessageFactory factory) {
    return ZenithLoggerContext.INSTANCE.getLogger(parent.getName() + " " + name);
  }

  private static Supplier<String> createSupplier(final String msg) {
    return () -> {
      return retrieveMessage(msg);
    };
  }

  private static Supplier<String> createSupplier(final Supplier<String> msgSupplier) {
    return () -> {
      if (msgSupplier == null) {
        return null;
      }
      return retrieveMessage(msgSupplier.get());
    };
  }

  private static Supplier<Message> createSupplier(final String msg, final Object... replaceables) {
    return () -> {
      return logger().getMessageFactory().newMessage(retrieveMessage(msg), replaceables);
    };
  }

  private static Supplier<Message> createSupplier(final Supplier<String> msgSupplier, final Object... replaceables) {
    return () -> {
      if (msgSupplier == null) {
        return null;
      }
      return logger().getMessageFactory().newMessage(retrieveMessage(msgSupplier.get()), replaceables);
    };
  }

  public static void debug(final String msg) {
    logger().debug(createSupplier(msg));
  }

  public static void debug(final String msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void debug(final Supplier<String> msg) {
    logger().debug(createSupplier(msg));
  }

  public static void debug(final Supplier<String> msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void info(final String msg) {
    logger().debug(createSupplier(msg));
  }

  public static void info(final String msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void info(final Supplier<String> msg) {
    logger().debug(createSupplier(msg));
  }

  public static void info(final Supplier<String> msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void warn(final String msg) {
    logger().debug(createSupplier(msg));
  }

  public static void warn(final String msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void warn(final Supplier<String> msg) {
    logger().debug(createSupplier(msg));
  }

  public static void warn(final Supplier<String> msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void severe(final String msg) {
    logger().debug(createSupplier(msg));
  }

  public static void severe(final String msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void severe(final Supplier<String> msg) {
    logger().debug(createSupplier(msg));
  }

  public static void severe(final Supplier<String> msg, final Object... replaceables) {
    logger().debug(createSupplier(msg, replaceables));
  }

  public static void error(final String description) {
    error(null, description, true);
  }

  public static void error(
      final Throwable throwable,
      final String description) {
    error(throwable, description, true);
  }

  public static void error(
      final Throwable throwable,
      final String description,
      final boolean disable) {
    if (throwable != null) {
      throwable.printStackTrace();
    }

    severe("*-----------------------------------------------------*");
    severe(
        "An error has occurred in " +
            ZenithProvider.instance().getName() +
            ".");
    severe("Description: " + description);
    severe("Contact the plugin author if you cannot fix this issue.");
    severe("*-----------------------------------------------------*");
    if (disable && Bukkit.getPluginManager().isPluginEnabled(ZenithProvider.instance())) {
      Bukkit.getPluginManager().disablePlugin(ZenithProvider.instance());
    }
  }

  // LoggerContext

  public static class ZenithLoggerContext implements LoggerContext {
    static final ZenithLoggerContext INSTANCE = new ZenithLoggerContext();

    private static final MessageFactory DEFAULT_MESSAGE_FACTORY = ParameterizedMessageFactory.INSTANCE;
    private final boolean showLogName;
    private final boolean showThreadContext;
    private final boolean showDateTime;
    private final String dateTimeFormat;

    private final Level defaultLevel;
    private final Map<String, Level> logLevelMap;

    private final PropertiesUtil props;
    private final PrintStream stream;
    private final LoggerRegistry<ExtendedLogger> loggerRegistry = new LoggerRegistry<>();

    public ZenithLoggerContext() {
      final SimpleProvider.Config config = SimpleProvider.Config.INSTANCE;
      props = config.props;
      stream = config.stream;

      showLogName = ZenithProvider.config().get("logging.format.showLoggerName", true);
      showThreadContext = ZenithProvider.config().get("logging.format.showThreadContext", true);
      showDateTime = ZenithProvider.config().get("logging.format.showDateTime", true);
      dateTimeFormat = ZenithProvider.config().get("logging.format.dateTimeFormat", "yyyy/MM/dd HH:mm:ss:SSS zzz");
      defaultLevel = ZenithProvider.config().get("logging.loglevels.default", Level.WARN);
      logLevelMap = ZenithProvider.config().get("logging.loglevels", Map.of("default", Level.WARN));
    }

    @Override
    public Object getExternalContext() {
      return null;
    }

    /**
     * Returns a Logger with the name of the calling class.
     *
     * @return The Logger for the calling class.
     * @throws UnsupportedOperationException if the calling class cannot be
     *                                       determined.
     */
    public ExtendedLogger getLogger() {
      return getLogger(StackLocatorUtil.getCallerClass(2));
    }

    /**
     * Gets an ExtendedLogger.
     * 
     * @param name The name of the Logger to return.
     * @return The logger with the specified name.
     */
    @Override
    public ExtendedLogger getLogger(final String name) {
      return name != null
          ? getLogger(name, null, DEFAULT_MESSAGE_FACTORY)
          : getLogger(StackLocatorUtil.getCallerClass(2), null, DEFAULT_MESSAGE_FACTORY);
    }

    /**
     * Gets an ExtendedLogger.
     * 
     * @param name  The name of the Logger to return.
     * @param level The Level to use for the logger.
     * @return The logger with the specified name.
     */
    public ExtendedLogger getLogger(final String name, final Level level) {
      return name != null
          ? getLogger(name, null, DEFAULT_MESSAGE_FACTORY)
          : getLogger(StackLocatorUtil.getCallerClass(2), null, DEFAULT_MESSAGE_FACTORY);
    }

    /**
     * Gets an ExtendedLogger.
     * 
     * @param name           The name of the Logger to return.
     * @param messageFactory The message factory is used only when creating a
     *                       logger, subsequent use does not change
     *                       the logger but will log a warning if mismatched.
     * @return The logger with the specified name.
     */
    @Override
    public ExtendedLogger getLogger(final String name, final MessageFactory messageFactory) {
      return name != null
          ? getLogger(name, null, messageFactory)
          : getLogger(StackLocatorUtil.getCallerClass(2), null, messageFactory);
    }

    /**
     * Gets an ExtendedLogger.
     * 
     * @param name           The name of the Logger to return.
     * @param level          The Level to use for the logger.
     * @param messageFactory The message factory is used only when creating a
     *                       logger, subsequent use does not change
     *                       the logger but will log a warning if mismatched.
     * @return The logger with the specified name.
     */
    public ExtendedLogger getLogger(final String name, Level level, final MessageFactory messageFactory) {
      final MessageFactory effectiveMessageFactory = messageFactory != null ? messageFactory : DEFAULT_MESSAGE_FACTORY;
      final ExtendedLogger oldLogger = loggerRegistry.getLogger(name, effectiveMessageFactory);
      if (oldLogger != null) {
        return oldLogger;
      }
      level = level == null ? logLevelMap.getOrDefault(name, defaultLevel) : level;
      final ExtendedLogger newLogger = new SimpleLogger(
          name,
          level,
          showLogName,
          true,
          showDateTime,
          showThreadContext,
          dateTimeFormat,
          messageFactory,
          props,
          stream);
      loggerRegistry.putIfAbsent(name, effectiveMessageFactory, newLogger);
      return newLogger;
    }

    /**
     * Gets an ExtendedLogger using the fully qualified name of the Class as the
     * Logger name.
     * 
     * @param cls The Class whose name should be used as the Logger name.
     * @return The logger.
     */
    @Override
    public ExtendedLogger getLogger(Class<?> cls) {
      cls = getCallerClass(cls);
      final String canonicalName = cls.getCanonicalName();
      return getLogger(canonicalName != null ? canonicalName : cls.getName(), null, DEFAULT_MESSAGE_FACTORY);
    }

    /**
     * Gets an ExtendedLogger using the fully qualified name of the Class as the
     * Logger name.
     * 
     * @param cls   The Class whose name should be used as the Logger name.
     * @param level The Level to use for the logger.
     * @return The logger.
     */
    public ExtendedLogger getLogger(Class<?> cls, final Level level) {
      cls = getCallerClass(cls);
      final String canonicalName = cls.getCanonicalName();
      return getLogger(canonicalName != null ? canonicalName : cls.getName(), level, DEFAULT_MESSAGE_FACTORY);
    }

    /**
     * Gets an ExtendedLogger using the fully qualified name of the Class as the
     * Logger name.
     * 
     * @param cls            The Class whose name should be used as the Logger name.
     * @param level          The Level to use for the logger.
     * @param messageFactory The message factory is used only when creating a
     *                       logger, subsequent use does not change the
     *                       logger but will log a warning if mismatched.
     * @return The logger.
     */
    public ExtendedLogger getLogger(Class<?> cls, final Level level, final MessageFactory messageFactory) {
      cls = getCallerClass(cls);
      final String canonicalName = cls.getCanonicalName();
      return getLogger(canonicalName != null ? canonicalName : cls.getName(), level, messageFactory);
    }

    /**
     * Returns a Logger with the name of the calling class.
     *
     * @param messageFactory The message factory is used only when creating a
     *                       logger, subsequent use does not change the
     *                       logger but will log a warning if mismatched.
     * @return The Logger for the calling class.
     * @throws UnsupportedOperationException if the calling class cannot be
     *                                       determined.
     */
    public ExtendedLogger getLogger(final MessageFactory messageFactory) {
      return getLogger(StackLocatorUtil.getCallerClass(2), null, messageFactory);
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
    public ExtendedLogger getLogger(final Object value) {
      return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2));
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
    public ExtendedLogger getLogger(final Object value, final Level level) {
      return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2), level,
          DEFAULT_MESSAGE_FACTORY);
    }

    /**
     * Returns a Logger using the fully qualified class name of the value as the
     * Logger name.
     *
     * @param value          The value whose class name should be used as the Logger
     *                       name. If null the name of the calling class
     *                       will be used as the logger name.
     * @param messageFactory The message factory is used only when creating a
     *                       logger, subsequent use does not change the
     *                       logger but will log a warning if mismatched.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code value} is {@code null} and
     *                                       the calling class cannot be
     *                                       determined.
     */
    public ExtendedLogger getLogger(final Object value, final MessageFactory messageFactory) {
      return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2), null, messageFactory);
    }

    /**
     * Returns a Logger using the fully qualified class name of the value as the
     * Logger name.
     *
     * @param value          The value whose class name should be used as the Logger
     *                       name. If null the name of the calling class
     *                       will be used as the logger name.
     * @param level          The Level to use for the logger.
     * @param messageFactory The message factory is used only when creating a
     *                       logger, subsequent use does not change the
     *                       logger but will log a warning if mismatched.
     * @return The Logger.
     * @throws UnsupportedOperationException if {@code value} is {@code null} and
     *                                       the calling class cannot be
     *                                       determined.
     */
    public ExtendedLogger getLogger(final Object value, final Level level, final MessageFactory messageFactory) {
      return getLogger(value != null ? value.getClass() : StackLocatorUtil.getCallerClass(2), level, messageFactory);
    }

    private static Class<?> getCallerClass(Class<?> cls) {
      if (cls != null) {
        return cls;
      }
      cls = StackLocatorUtil.getCallerClass(3);
      if (cls == null) {
        throw new IllegalArgumentException("No class provided, and an appropriate one cannot be found.");
      }
      return cls;
    }

    @Override
    public LoggerRegistry<ExtendedLogger> getLoggerRegistry() {
      return loggerRegistry;
    }

    @Override
    public boolean hasLogger(final String name) {
      return loggerRegistry.hasLogger(name, DEFAULT_MESSAGE_FACTORY);
    }

    @Override
    public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
      return loggerRegistry.hasLogger(name, messageFactoryClass);
    }

    @Override
    public boolean hasLogger(final String name, final MessageFactory messageFactory) {
      return loggerRegistry.hasLogger(name, messageFactory == null ? DEFAULT_MESSAGE_FACTORY : messageFactory);
    }
  }

  // LoggerContextFactory

  public static class ZenithLoggerContextFactory implements LoggerContextFactory {

    public static final ZenithLoggerContextFactory INSTANCE = new ZenithLoggerContextFactory();

    @Override
    public ZenithLoggerContext getContext(
        final String fqcn, final ClassLoader loader, final Object externalContext, final boolean currentContext) {
      return ZenithLoggerContext.INSTANCE;
    }

    @Override
    public ZenithLoggerContext getContext(
        final String fqcn,
        final ClassLoader loader,
        final Object externalContext,
        final boolean currentContext,
        final URI configLocation,
        final String name) {
      return ZenithLoggerContext.INSTANCE;
    }

    @Override
    public void removeContext(final LoggerContext removeContext) {
      // do nothing
    }

    @Override
    public boolean isClassLoaderDependent() {
      return false;
    }
  }

}
