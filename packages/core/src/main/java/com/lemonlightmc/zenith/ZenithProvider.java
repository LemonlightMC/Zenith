package com.lemonlightmc.zenith;

import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import com.lemonlightmc.zenith.scheduler.Scheduler;

public class ZenithProvider {
  private static IZenithPlugin instance;

  public static boolean hasInstance() {
    return instance != null;
  }

  public static void setInstance(IZenithPlugin plugin) {
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

  public static Scheduler getScheduler() {
    return instance.getScheduler();
  }

  public PluginManager getPluginManager() {
    return instance.getPluginManager();
  }
}
