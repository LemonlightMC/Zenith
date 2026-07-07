package com.lemonlightmc.zenith.messages;

import java.util.Locale;
import java.util.function.Supplier;

import org.apache.logging.log4j.message.Message;
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

}
