package com.lemonlightmc.zenith.plugin;

import java.io.File;
import java.nio.file.Path;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;

import com.lemonlightmc.zenith.PluginInfo;
import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.additive.version.Version;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.plugin.events.PluginDisableEvent;
import com.lemonlightmc.zenith.plugin.events.PluginEnableEvent;
import com.lemonlightmc.zenith.plugin.events.PluginLoadEvent;
import com.lemonlightmc.zenith.plugin.events.PluginReloadEvent;
import com.lemonlightmc.zenith.scheduler.BukkitScheduler;
import com.lemonlightmc.zenith.scheduler.Scheduler;

/**
 * Base implementation of {@link IPlugin} providing common lifecycle, metadata, and integration behavior.
 */
public abstract class PluginBase implements IPlugin {

  private final PluginLoader loader;
  private Server server = null;
  private Scheduler scheduler = null;
  private ClassLoader classLoader = null;
  private MessageAPI messageAPI = null;

  private final File file;
  private final File dataFolder;
  private final PluginInfo info;
  private boolean naggable = true;
  private boolean isEnabled = false;

  private static PluginBase instance = null;

  /**
   * Creates a new plugin base instance.
   *
   * @param loader the plugin loader used for this plugin
   * @param info the Bukkit plugin description
   * @param dataFolder the plugin data folder
   * @param file the plugin file
   */
  protected PluginBase(
      final JavaPluginLoader loader,
      final PluginDescriptionFile info,
      final File dataFolder,
      final File file) {
    classLoader = this.getClass().getClassLoader();
    this.server = Bukkit.getServer();
    this.dataFolder = dataFolder;
    this.loader = loader;
    this.file = file;
    this.info = new PluginInfo(info);
    PluginBase.instance = this;
  }

  /**
   * Returns whether a plugin instance has been initialized.
   *
   * @return {@code true} if an instance is available; {@code false} otherwise
   */
  public static boolean hasInstance() {
    return instance != null;
  }

  /**
   * Returns the current plugin instance.
   *
   * @param <I> the expected plugin type
   * @return the current plugin instance
   * @throws RuntimeException if the plugin instance is not available
   */
  @SuppressWarnings("unchecked")
  public static <I extends PluginBase> I getInstance() {
    if (instance == null) {
      throw new RuntimeException(
          "Plugin is not enabled - Plugin Instance can not be obtained!");
    }
    return (I) instance;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onLoad() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDisable() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onEnable() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onReload() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

  /**
   * Updates the enabled state of this plugin and triggers the corresponding lifecycle transition.
   *
   * @param enabled {@code true} to enable the plugin; {@code false} to disable it
   */
  public void setEnabled(final boolean enabled) {
    if (isEnabled != enabled) {
      isEnabled = enabled;

      if (isEnabled) {
        enable();
      } else {
        disable();
      }
    }
  }

  /**
   * Performs plugin load initialization and emits the load event.
   */
  public void load() {
    MessageFormatter.setPlaceholdersSupport(server.getPluginManager().isPluginEnabled("PlaceholderAPI"));
    if (messageAPI == null) {
      messageAPI = new MessageAPI();
    }
    if (scheduler == null) {
      scheduler = new BukkitScheduler();
    }
    onLoad();
    getPluginManager().callEvent(new PluginLoadEvent(this));
  }

  /**
   * Enables the plugin and emits the enable event.
   */
  public void enable() {
    isEnabled = true;
    onEnable();
    getPluginManager().callEvent(new PluginEnableEvent(this));
  }

  /**
   * Reloads plugin-managed state and emits the reload event.
   */
  public void reload() {
    MessageFormatter.setPlaceholdersSupport(server.getPluginManager().isPluginEnabled("PlaceholderAPI"));
    ZenithProvider.reloadZenithConfig();
    onReload();
    getPluginManager().callEvent(new PluginReloadEvent(this));
  }

  /**
   * Disables the plugin and emits the disable event.
   */
  public void disable() {
    getPluginManager().callEvent(new PluginDisableEvent(this));
    onDisable();
    isEnabled = false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PluginInfo getInfo() {
    return info;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getKey() {
    return info.getKey();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return info.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFullName() {
    return info.getFullName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrefix() {
    return info.getPrefix();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Version getVersion() {
    return info.getVersion();
  }

  /**
   * Returns the plugin file backing this plugin.
   *
   * @return the plugin file
   */
  protected File getFile() {
    return file;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path getDataFolder() {
    return dataFolder.toPath();
  }

  /**
   * Resolves a file within the plugin data folder.
   *
   * @param path path segments relative to the data folder
   * @return the resolved file, or the data folder itself if no segments are provided
   */
  public File getDataFile(final String... path) {
    if (path == null || path.length == 0) {
      return dataFolder;
    }
    return new File(dataFolder, StringUtils.join(File.separator, path));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Server getServer() {
    return server;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PluginLoader getPluginLoader() {
    return loader;
  }

  /**
   * Returns the class loader associated with this plugin.
   *
   * @return the plugin class loader
   */
  public ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PluginManager getPluginManager() {
    return server.getPluginManager();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ServicesManager getServicesManager() {
    return server.getServicesManager();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Scheduler getScheduler() {
    return scheduler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public java.util.logging.Logger getLogger() {
    return server.getLogger();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MessageAPI messageAPI() {
    return messageAPI;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNaggable() {
    return naggable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNaggable(final boolean canNag) {
    this.naggable = canNag;
  }

  @Override
  public String toString() {
    return info.getFullName();
  }

  @Override
  public int hashCode() {
    return 31 * (31 + file.hashCode()) + info.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final PluginBase other = (PluginBase) obj;
    return info.equals(other.info) && file.equals(other.file);
  }
}
