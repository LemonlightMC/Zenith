package com.lemonlightmc.zenith;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;

import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;
import com.lemonlightmc.zenith.additive.version.Version;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.scheduler.Scheduler;

/**
 * Represents the core plugin contract for a Zenith-based Bukkit plugin.
 *
 * <p>Implementations provide access to plugin metadata, shared services, and lifecycle hooks used by the Zenith
 * framework. Some Bukkit configuration and command-related methods are deprecated in favor of Zenith-managed
 * alternatives.
 */
public interface IZenithPlugin extends Plugin {

  /**
   * Returns descriptive metadata for this plugin.
   *
   * @return the plugin metadata
   */
  PluginInfo getInfo();

  /**
   * Returns the normalized plugin key.
   *
   * @return the plugin key
   */
  String getKey();

  /**
   * Returns the full display name of the plugin, including its version.
   *
   * @return the full plugin name
   */
  String getFullName();

  /**
   * Returns the configured plugin prefix used in formatted output.
   *
   * @return the plugin prefix
   */
  String getPrefix();

  /**
   * Returns the semantic version of the plugin.
   *
   * @return the plugin version
   */
  Version getVersion();

  /**
   * Returns the plugin JAR or descriptor file on disk.
   *
   * @return the plugin file
   */
  File getFile();

  /**
   * Resolves a file path relative to the plugin's data folder.
   *
   * @param path path segments relative to the data folder
   * @return the resolved data file
   */
  File getDataFile(String... path);

  /**
   * Returns the Bukkit plugin manager.
   *
   * @return the plugin manager
   */
  PluginManager getPluginManager();

  /**
   * Returns the Bukkit services manager.
   *
   * @return the services manager
   */
  ServicesManager getServicesManager();

  /**
   * Returns the scheduler used for task execution.
   *
   * @return the plugin scheduler
   */
  Scheduler getScheduler();

  /**
   * {@inheritDoc}
   */
  @Override
  java.util.logging.Logger getLogger();

  /**
   * Returns the SLF4J-compatible logger adapter for this plugin.
   *
   * @return the SLF4J logger adapter
   */
  LoggerAdapter getSlf4jLogger();

  /**
   * Returns the message API used for localization and message lookup.
   *
   * @return the message API
   */
  MessageAPI getMessageAPI();

  /**
   * {@inheritDoc}
   *
   * @deprecated use Zenith-managed configuration APIs instead
   */
  @Deprecated
  @Override
  FileConfiguration getConfig();

  /**
   * {@inheritDoc}
   *
   * @deprecated use Zenith-managed configuration APIs instead
   */
  @Override
  @Deprecated
  void reloadConfig();

  /**
   * {@inheritDoc}
   *
   * @deprecated use Zenith-managed configuration APIs instead
   */
  @Override
  @Deprecated
  void saveConfig();

  /**
   * Loads the plugin configuration using Zenith-managed handling.
   *
   * @deprecated use Zenith-managed configuration APIs instead
   */
  @Deprecated
  void loadConfig();

  /**
   * Loads the plugin configuration from a specific file.
   *
   * @param file the configuration file to load
   * @deprecated use Zenith-managed configuration APIs instead
   */
  @Deprecated
  void loadConfig(File file);

  /**
   * {@inheritDoc}
   *
   * @deprecated use Zenith-managed configuration APIs instead
   */
  @Override
  @Deprecated
  void saveDefaultConfig();

  /**
   * {@inheritDoc}
   *
   * @deprecated use Zenith-managed resource handling instead
   */
  @Deprecated
  @Override
  InputStream getResource(String filename);

  /**
   * {@inheritDoc}
   *
   * @deprecated use Zenith-managed resource handling instead
   */
  @Deprecated
  @Override
  void saveResource(String path, boolean replace);

  /**
   * {@inheritDoc}
   *
   * @deprecated command registration is managed by Zenith
   */
  @Deprecated
  PluginCommand getCommand(String name);

  /**
   * {@inheritDoc}
   */
  @Override
  void onLoad();

  /**
   * {@inheritDoc}
   */
  @Override
  void onEnable();

  /**
   * Called when the plugin is reloaded by Zenith.
   */
  void onReload();

  /**
   * {@inheritDoc}
   */
  @Override
  void onDisable();

  /**
   * {@inheritDoc}
   */
  @Override
  String toString();

  /**
   * {@inheritDoc}
   *
   * @deprecated command handling is managed by Zenith
   */
  @Override
  @Deprecated
  List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args);

  /**
   * {@inheritDoc}
   *
   * @deprecated command handling is managed by Zenith
   */
  @Override
  @Deprecated
  boolean onCommand(CommandSender sender, Command command, String label, String[] args);

  /**
   * {@inheritDoc}
   *
   * @deprecated world generation hooks are not managed by Zenith plugins
   */
  @Deprecated
  @Override
  ChunkGenerator getDefaultWorldGenerator(String worldName, String id);

  /**
   * {@inheritDoc}
   *
   * @deprecated biome provider hooks are not managed by Zenith plugins
   */
  @Deprecated
  @Override
  BiomeProvider getDefaultBiomeProvider(String worldName, String id);
}
