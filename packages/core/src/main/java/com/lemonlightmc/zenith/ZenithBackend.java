package com.lemonlightmc.zenith;

import java.nio.file.Path;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.scheduler.Scheduler;

/**
 * Runtime backend for {@link ZenithProvider}.
 *
 * <p>Core exposes this contract; the plugin package provides the implementation.
 */
public interface ZenithBackend {

  boolean hasInstance();

  IZenithPlugin instance();

  ZenithConfig config();

  void reloadConfig();

  Path pluginsFolder();

  Path librariesFolder();

  Path zenithFolder();

  Server server();

  Scheduler scheduler();

  PluginManager pluginManager();

  MessageAPI messageAPI();

  LoggerAdapter pluginLogger();

  java.util.logging.Logger bukkitLogger();

  LoggerAdapter zenithLogger();

  LoggerAdapter zenithLogger(String subLogger);

  LoggerAdapter zenithLogger(String subLogger, Level level);

  LoggerAdapter zenithLogger(Logger logger, String subLogger);

  LoggerAdapter zenithLogger(Logger logger, String subLogger, Level level);
}
