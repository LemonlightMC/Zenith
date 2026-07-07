package com.lemonlightmc.zenith;

import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.files.FileUtils;
import com.lemonlightmc.zenith.files.ResourceUtils;
import com.lemonlightmc.zenith.scheduler.Scheduler;

public class ZenithProvider {
  private static IZenithPlugin instance;
  private static AtomicBoolean initialized = new AtomicBoolean(false);

  private static Path PLUGINS_FOLDER = Path.of("plugins");
  private static Path LIBARIES_FOLDER = Path.of("libaries");
  private static Path ZENITH_FOLDER = Path.of("plugins", "zenith");
  private static Logger logger = LogManager.getLogger("Zenith");
  private static ZenithConfig config = ZenithConfig.from(ZENITH_FOLDER.resolve("config.properties"));

  static {
    LogManager.setFactory(com.lemonlightmc.zenith.messages.Logger.ZenithLoggerContextFactory.INSTANCE);
    logger = LogManager.getLogger("Zenith");
  }

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

  public static void setInstance(final IZenithPlugin plugin) {
    if (initialized.getAndSet(true)) {
      throw new IllegalStateException("ZenithProvider instance has already been set.");
    }
    instance = plugin;

    PLUGINS_FOLDER = plugin.getDataFolder().toPath().getParent();
    LIBARIES_FOLDER = PLUGINS_FOLDER.getParent().resolve("libaries");
    ZENITH_FOLDER = PLUGINS_FOLDER.resolve("zenith");
    FileUtils.mkdirs(ZENITH_FOLDER);
    config = ZenithConfig.from(ZENITH_FOLDER.resolve("config.yml"));

    logger.debug("ZenithProvider initialized (from plugin: " + plugin.getInfo().getFullName() + ")");
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

  public static Logger logger() {
    return instance.getLog4jLogger();
  }

  public static Logger zenithLogger() {
    return logger;
  }

  public static Logger zenithLogger(final String subLogger) {
    return com.lemonlightmc.zenith.messages.Logger.getLogger("[Zenith] " + subLogger);
  }

  public static Logger zenithLogger(final String subLogger, final Level level) {
    return com.lemonlightmc.zenith.messages.Logger.getLogger("[Zenith] " + subLogger);
  }

  public static Logger zenithLogger(final Logger logger, final String subLogger) {
    return com.lemonlightmc.zenith.messages.Logger.getLogger(logger.getName() + " " + subLogger);
  }

  public static Logger zenithLogger(final Logger logger, final String subLogger, final Level level) {
    return com.lemonlightmc.zenith.messages.Logger.getLogger(logger.getName() + " " + subLogger);
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

  // TODO: switch to yaml!!
  public static class ZenithConfig {

    private final Properties properties;

    public ZenithConfig(final Properties properties) {
      this.properties = properties;
    }

    public Properties getProperties() {
      return properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String key) {
      final Object obj = properties.get(key);
      return obj != null ? (T) obj : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final String key, final T defaultValue) {
      final Object obj = properties.get(key);
      return obj != null ? (T) obj : defaultValue == null ? null : (T) defaultValue;
    }

    public boolean containsKey(final String key) {
      return properties.containsKey(key);
    }

    public static ZenithConfig from(final Path path) {
      return new ZenithConfig(ResourceUtils.loadProperties(path));
    }

    public static ZenithConfig from(final Properties properties) {
      return new ZenithConfig(properties);
    }
  }
}
