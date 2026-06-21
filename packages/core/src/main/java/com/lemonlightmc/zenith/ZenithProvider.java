package com.lemonlightmc.zenith;

import java.nio.file.Path;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import com.lemonlightmc.zenith.scheduler.Scheduler;

public class ZenithProvider {
  private static IZenithPlugin instance;

  private static Path PLUGINS_FOLDER = Path.of("plugins");
  private static Path LIBARIES_FOLDER = Path.of("libaries");
  private static Path ZENITH_FOLDER = Path.of("plugins", "zenith");
  private static Logger logger = Logger.getLogger("Zenith");

  public static Path getPluginsFolder() {
    return PLUGINS_FOLDER;
  }

  public static Path getLibariesFolder() {
    return LIBARIES_FOLDER;
  }

  public static Path getZenithFolder() {
    return ZENITH_FOLDER;
  }

  public static boolean hasInstance() {
    return instance != null;
  }

  public static void setInstance(final IZenithPlugin plugin) {
    if (instance != null) {
      throw new IllegalStateException("ZenithProvider instance has already been set.");
    }
    instance = plugin;
  }

  public static IZenithPlugin getInstance() {
    if (instance == null) {
      throw new RuntimeException(
          "Plugin is not enabled - Plugin Instance can not be obtained!");
    }
    return instance;
  }

  public static Server getServer() {
    return instance.getServer();
  }

  public static Logger getLogger() {
    return instance.getLogger();
  }

  public static Logger getZenithLogger() {
    return logger;
  }

  public static Logger getZenithLogger(String subLogger) {
    return Logger.getLogger("Zenith." + subLogger);
  }

  public static Scheduler getScheduler() {
    return instance.getScheduler();
  }

  public PluginManager getPluginManager() {
    return instance.getPluginManager();
  }
}
