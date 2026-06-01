package com.lemonlightmc.zenith.messages;

import java.util.function.Supplier;

import org.bukkit.Bukkit;

import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.utils.StringUtils.Replaceable;

public class Logger {
  public static java.util.logging.Logger getLogger() {
    return Bukkit.getLogger();
  }

  public static void fine(String msg) {
    msg = MessageFormatter.format(msg);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().fine(msg);
  }

  public static void fine(String msg, final Replaceable... replaceables) {
    msg = MessageFormatter.format(msg, replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().fine(msg);
  }

  public static void fine(final Supplier<String> msgSupplier) {
    final String msg = MessageFormatter.format(msgSupplier.get());
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().fine(msg);
  }

  public static void fine(final Supplier<String> msgSupplier, final Replaceable... replaceables) {
    final String msg = MessageFormatter.format(msgSupplier.get(), replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().fine(msg);
  }

  public static void info(String msg) {
    msg = MessageFormatter.format(msg);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().info(msg);
  }

  public static void info(String msg, final Replaceable... replaceables) {
    msg = MessageFormatter.format(msg, replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().info(msg);
  }

  public static void info(final Supplier<String> msgSupplier) {
    final String msg = MessageFormatter.format(msgSupplier.get());
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().info(msg);
  }

  public static void info(final Supplier<String> msgSupplier, final Replaceable... replaceables) {
    final String msg = MessageFormatter.format(msgSupplier.get(), replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().info(msg);
  }

  public static void warn(String msg) {
    msg = MessageFormatter.format(msg);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().warning(msg);
  }

  public static void warn(String msg, final Replaceable... replaceables) {
    msg = MessageFormatter.format(msg, replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().warning(msg);
  }

  public static void warn(final Supplier<String> msgSupplier) {
    final String msg = MessageFormatter.format(msgSupplier.get());
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().warning(msg);
  }

  public static void warn(final Supplier<String> msgSupplier, final Replaceable... replaceables) {
    final String msg = MessageFormatter.format(msgSupplier.get(), replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().warning(msg);
  }

  public static void severe(String msg) {
    msg = MessageFormatter.format(msg);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().severe(msg);
  }

  public static void severe(String msg, final Replaceable... replaceables) {
    msg = MessageFormatter.format(msg, replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().severe(msg);
  }

  public static void severe(final Supplier<String> msgSupplier) {
    final String msg = MessageFormatter.format(msgSupplier.get());
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().severe(msg);
  }

  public static void severe(final Supplier<String> msgSupplier, final Replaceable... replaceables) {
    final String msg = MessageFormatter.format(msgSupplier.get(), replaceables);
    if (msg == null) {
      return;
    }
    Bukkit.getLogger().severe(msg);
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
            ZenithPlugin.getInstance().getName() +
            ".");
    severe("Description: " + description);
    severe("Contact the plugin author if you cannot fix this issue.");
    severe("*-----------------------------------------------------*");
    if (disable && Bukkit.getPluginManager().isPluginEnabled(ZenithPlugin.getInstance())) {
      Bukkit.getPluginManager().disablePlugin(ZenithPlugin.getInstance());
    }
  }

}
