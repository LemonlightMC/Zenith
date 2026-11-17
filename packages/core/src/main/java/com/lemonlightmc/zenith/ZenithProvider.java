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

/**
 * Provides global access to the active Zenith plugin instance, configuration, and shared runtime services.
 *
 * <p>The provider is initialized once by the primary plugin thread and exposes convenience accessors for folders,
 * logging, scheduling, and messaging used throughout Zenith.
 */
public class ZenithProvider {
  private static Path PLUGINS_FOLDER = Path.of("plugins");
  private static Path LIBARIES_FOLDER = Path.of("libaries");

  private static Path ZENITH_FOLDER = Path.of("plugins", "zenith");
  private static IZenithPlugin instance;
  private static AtomicBoolean initialized = new AtomicBoolean(false);
  private static ZenithConfig config = ZenithConfig.from(ZENITH_FOLDER.resolve("config.properties"));
  private static Logger zenithLogger;
  private static Locale loggerLocale;

  /**
   * Returns the loaded global Zenith configuration.
   *
   * @return the global configuration
   */
  public static ZenithConfig config() {
    return config;
  }

  /**
   * Returns the server plugins folder path.
   *
   * @return the plugins folder path
   */
  public static Path pluginsFolder() {
    return PLUGINS_FOLDER;
  }

  /**
   * Returns the server libraries folder path.
   *
   * @return the libraries folder path
   */
  public static Path libariesFolder() {
    return LIBARIES_FOLDER;
  }

  /**
   * Returns the Zenith data folder path.
   *
   * @return the Zenith folder path
   */
  public static Path zenithFolder() {
    return ZENITH_FOLDER;
  }

  /**
   * Returns whether a plugin instance has been initialized.
   *
   * @return {@code true} if the provider has been initialized; {@code false} otherwise
   */
  public static boolean hasInstance() {
    return initialized.get();
  }

  /**
   * Initializes the provider with the active plugin instance.
   *
   * <p>Only the first initialization performed on the primary server thread is applied. Subsequent calls are
   * ignored and logged as warnings.
   *
   * @param plugin the plugin instance to register
   */
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

  /**
   * Returns the active Zenith plugin instance.
   *
   * @return the active plugin instance
   * @throws RuntimeException if the provider has not been initialized
   */
  public static IZenithPlugin instance() {
    if (instance == null) {
      throw new RuntimeException(
          "Plugin is not enabled - Plugin Instance can not be obtained!");
    }
    return instance;
  }

  /**
   * Returns the Bukkit server associated with the active plugin.
   *
   * @return the Bukkit server
   */
  public static Server server() {
    return instance.getServer();
  }

  /**
   * Returns the scheduler provided by the active plugin.
   *
   * @return the plugin scheduler
   */
  public static Scheduler scheduler() {
    return instance.getScheduler();
  }

  /**
   * Returns the Bukkit plugin manager associated with the active plugin.
   *
   * @return the plugin manager
   */
  public PluginManager pluginManager() {
    return instance.getPluginManager();
  }

  /**
   * Returns the message API provided by the active plugin.
   *
   * @return the message API
   */
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

  /**
   * Returns the SLF4J-compatible logger adapter for the active plugin.
   *
   * @return the plugin logger adapter
   */
  public static LoggerAdapter pluginLogger() {
    return instance.getSlf4jLogger();
  }

  /**
   * Returns the root Bukkit logger.
   *
   * @return the Bukkit logger
   */
  public static java.util.logging.Logger bukkitLogger() {
    return Bukkit.getLogger();
  }

  /**
   * Returns the shared Zenith logger.
   *
   * @return the Zenith logger
   * @throws IllegalStateException if the provider has not been initialized
   */
  public static Logger zenithLogger() {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return zenithLogger;
  }

  /**
   * Returns a named child logger of the shared Zenith logger.
   *
   * @param subLogger the child logger name
   * @return the child Zenith logger
   * @throws IllegalStateException if the provider has not been initialized
   */
  public static Logger zenithLogger(final String subLogger) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger);
  }

  /**
   * Returns a named child logger of the shared Zenith logger with an explicit level.
   *
   * @param subLogger the child logger name
   * @param level the log level to apply
   * @return the child Zenith logger
   * @throws IllegalStateException if the provider has not been initialized
   */
  public static Logger zenithLogger(final String subLogger, final Level level) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger, level);
  }

  /**
   * Returns a named child logger derived from the shared Zenith logger context.
   *
   * @param logger the logger context parameter accepted by this overload
   * @param subLogger the child logger name
   * @return the child Zenith logger
   * @throws IllegalStateException if the provider has not been initialized
   */
  public static Logger zenithLogger(final Logger logger, final String subLogger) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger);
  }

  /**
   * Returns a named child logger derived from the shared Zenith logger context with an explicit level.
   *
   * @param logger the logger context parameter accepted by this overload
   * @param subLogger the child logger name
   * @param level the log level to apply
   * @return the child Zenith logger
   * @throws IllegalStateException if the provider has not been initialized
   */
  public static Logger zenithLogger(final Logger logger, final String subLogger, final Level level) {
    if (zenithLogger == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return GlobalLogger.getLogger(zenithLogger, subLogger, level);
  }

}
