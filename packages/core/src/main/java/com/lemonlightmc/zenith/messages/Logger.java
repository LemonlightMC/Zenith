package com.lemonlightmc.zenith.messages;

import java.util.Locale;
import java.util.function.Supplier;

import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import com.lemonlightmc.zenith.ZenithProvider;

public class Logger {
  private final static Locale locale = ZenithProvider.getConfig().get("localization.logger-locale", Locale.ENGLISH);

  public static java.util.logging.Logger getBukkitLogger() {
    return Bukkit.getLogger();
  }

  public static org.apache.logging.log4j.Logger getLogger() {
    return ZenithProvider.getInstance().getLog4jLogger();
  }

  private static String retrieveMessage(String msg) {
    if (msg == null || msg.length() == 0) {
      return null;
    }
    if (msg.startsWith("messages.")) {
      msg = ZenithProvider.getInstance().getMessageAPI().translate(msg.substring(9), locale);
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
      return getLogger().getMessageFactory().newMessage(retrieveMessage(msg), replaceables);
    };
  }

  private static Supplier<Message> createSupplier(final Supplier<String> msgSupplier, final Object... replaceables) {
    return () -> {
      if (msgSupplier == null) {
        return null;
      }
      return getLogger().getMessageFactory().newMessage(retrieveMessage(msgSupplier.get()), replaceables);
    };
  }

  public static void debug(final String msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void debug(final String msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
  }

  public static void debug(final Supplier<String> msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void debug(final Supplier<String> msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
  }

  public static void info(final String msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void info(final String msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
  }

  public static void info(final Supplier<String> msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void info(final Supplier<String> msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
  }

  public static void warn(final String msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void warn(final String msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
  }

  public static void warn(final Supplier<String> msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void warn(final Supplier<String> msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
  }

  public static void severe(final String msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void severe(final String msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
  }

  public static void severe(final Supplier<String> msg) {
    getLogger().debug(createSupplier(msg));
  }

  public static void severe(final Supplier<String> msg, final Object... replaceables) {
    getLogger().debug(createSupplier(msg, replaceables));
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
            ZenithProvider.getInstance().getName() +
            ".");
    severe("Description: " + description);
    severe("Contact the plugin author if you cannot fix this issue.");
    severe("*-----------------------------------------------------*");
    if (disable && Bukkit.getPluginManager().isPluginEnabled(ZenithProvider.getInstance())) {
      Bukkit.getPluginManager().disablePlugin(ZenithProvider.getInstance());
    }
  }

}
