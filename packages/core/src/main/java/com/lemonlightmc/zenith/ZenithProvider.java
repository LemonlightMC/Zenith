package com.lemonlightmc.zenith;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import com.lemonlightmc.zenith.additive.files.FileUtils;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.scheduler.Scheduler;

public class ZenithProvider {
  private static Path PLUGINS_FOLDER = Path.of("plugins");
  private static Path LIBARIES_FOLDER = Path.of("libaries");

  private static Path ZENITH_FOLDER = Path.of("plugins", "zenith");
  private static IZenithPlugin instance;
  private static AtomicBoolean initialized = new AtomicBoolean(false);
  private static ZenithConfig config = ZenithConfig.from(ZENITH_FOLDER.resolve("config.properties"));
  private static Logger zenithLogger;
  private static Locale loggerLocale;

  public static ZenithConfig config() {
    return config;
  }

  public static Path pluginsFolder() {
    return PLUGINS_FOLDER;
  }

  public static Path libariesFolder() {
    return LIBARIES_FOLDER;
  }

  public static Path zenithFolder() {
    return ZENITH_FOLDER;
  }

  public static boolean hasInstance() {
    return initialized.get();
  }

  public static synchronized void setInstance(final IZenithPlugin plugin) {
    if (initialized.getAndSet(true)) {
      // plugin SHOULD already be initialized
      GlobalLogger.warn("ZenithProvider instance has already been set.");
      return;
    }
    if (!Bukkit.getServer().isPrimaryThread()) {
      return;
    }
    instance = plugin;

    PLUGINS_FOLDER = plugin.getDataFolder().toPath().getParent();
    LIBARIES_FOLDER = PLUGINS_FOLDER.getParent().resolve("libaries");
    ZENITH_FOLDER = PLUGINS_FOLDER.resolve("zenith");
    FileUtils.mkdirs(ZENITH_FOLDER);

    GlobalLogger.setRootLogger(instance.getLogger());
    reloadZenithConfig();
    GlobalLogger.getRootLogger().setTransformer((msg) -> {
      if (msg == null || msg.length() == 0) {
        return null;
      }
      if (msg.startsWith("messages.")) {
        return instance().getMessageAPI().translate(msg.substring(9), loggerLocale);
      }
      return msg;
    });

    zenithLogger = GlobalLogger.getLogger("Zenith");
    zenithLogger.debug("ZenithProvider initialized (from plugin: " + plugin.getInfo().getFullName() + ")");
  }

  public static IZenithPlugin instance() {
    if (instance == null) {
      throw new RuntimeException(
          "Plugin is not enabled - Plugin Instance can not be obtained!");
    }
    return instance;
  }

  public static Server server() {
    return instance.getServer();
  }

  public static Scheduler scheduler() {
    return instance.getScheduler();
  }

  public PluginManager pluginManager() {
    return instance.getPluginManager();
  }

  public static MessageAPI messageAPI() {
    return instance.getMessageAPI();
  }

  /**
   * Re-reads the global {@code config.yml} and re-applies the derived logger
   * settings. Useful for reloading the plugin at runtime.
   */
  public static void reloadZenithConfig() {
    config = ZenithConfig.from(ZENITH_FOLDER.resolve("config.yml"));
    loggerLocale = config.localization.loggerLocale;
    GlobalLogger.setLogLevelMapping(config.logging.logLevels);
    GlobalLogger.setDefaultLogLevel(
        config.logging.logLevels.getOrDefault("default", Level.INFO));
  }

  public static LoggerAdapter pluginLogger() {
    return instance.getSlf4jLogger();
  }

  public static java.util.logging.Logger bukkitLogger() {
    return Bukkit.getLogger();
  }

  public static Logger zenithLogger() {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return zenithLogger;
  }

  public static Logger zenithLogger(final String subLogger) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger);
  }

  public static Logger zenithLogger(final String subLogger, final Level level) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger, level);
  }

  public static Logger zenithLogger(final Logger logger, final String subLogger) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger);
  }

  public static Logger zenithLogger(final Logger logger, final String subLogger, final Level level) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger, level);
  }

}
