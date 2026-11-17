package com.lemonlightmc.zenith.plugin;

import java.nio.file.Path;

import org.bukkit.Server;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;

import com.lemonlightmc.zenith.PluginInfo;
import com.lemonlightmc.zenith.additive.version.Version;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.scheduler.Scheduler;

/**
 * Defines the core lifecycle and integration contract for Zenith plugins.
 */
public interface IPlugin {

  /**
   * Called when the plugin is loaded.
   */
  public void onLoad();

  /**
   * Called when the plugin is enabled.
   */
  public void onEnable();

  /**
   * Called when the plugin is reloaded.
   */
  public void onReload();

  /**
   * Called when the plugin is disabled.
   */
  public void onDisable();

  /**
   * Returns whether the plugin is currently enabled.
   *
   * @return {@code true} if the plugin is enabled; {@code false} otherwise
   */
  public boolean isEnabled();

  /**
   * Returns descriptive metadata for this plugin.
   *
   * @return the plugin information
   */
  public PluginInfo getInfo();

  /**
   * Returns the plugin's internal key identifier.
   *
   * @return the plugin key
   */
  public String getKey();

  /**
   * Returns the plugin's display name.
   *
   * @return the plugin name
   */
  public String getName();

  /**
   * Returns the plugin's full display name.
   *
   * @return the full plugin name
   */
  public String getFullName();

  /**
   * Returns the message prefix associated with this plugin.
   *
   * @return the plugin prefix
   */
  public String getPrefix();

  /**
   * Returns the plugin version.
   *
   * @return the plugin version
   */
  public Version getVersion();

  /**
   * Returns the plugin's data directory.
   *
   * @return the plugin data folder path
   */
  public Path getDataFolder();

  /**
   * Returns whether the plugin can be nagged for missing or outdated information.
   *
   * @return {@code true} if nagging is enabled; {@code false} otherwise
   */
  public boolean isNaggable();

  /**
   * Sets whether the plugin can be nagged for missing or outdated information.
   *
   * @param canNag {@code true} to enable nagging; {@code false} to disable it
   */
  public void setNaggable(boolean canNag);

  /**
   * Returns the Bukkit plugin loader used for this plugin.
   *
   * @return the plugin loader
   */
  public PluginLoader getPluginLoader();

  /**
   * Returns the Bukkit plugin manager.
   *
   * @return the plugin manager
   */
  public PluginManager getPluginManager();

  /**
   * Returns the scheduler associated with this plugin.
   *
   * @return the scheduler
   */
  public Scheduler getScheduler();

  /**
   * Returns the Bukkit services manager.
   *
   * @return the services manager
   */
  public ServicesManager getServicesManager();

  /**
   * Returns the Bukkit server instance.
   *
   * @return the server
   */
  public Server getServer();

  /**
   * Returns the logger used by this plugin.
   *
   * @return the plugin logger
   */
  public java.util.logging.Logger getLogger();

  /**
   * Returns the message API used for localized messaging.
   *
   * @return the message API
   */
  public MessageAPI messageAPI();
}
