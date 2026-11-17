package com.lemonlightmc.zenith.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import com.lemonlightmc.zenith.IZenithPlugin;
import com.lemonlightmc.zenith.PluginInfo;
import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.Lazy;
import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.additive.files.ResourceUtils;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;
import com.lemonlightmc.zenith.additive.version.Version;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.modular.ModuleAPI;
import com.lemonlightmc.zenith.scheduler.BukkitScheduler;
import com.lemonlightmc.zenith.scheduler.Scheduler;

/**
 * Base Bukkit plugin implementation for Zenith plugins.
 */
public abstract class ZenithPlugin extends JavaPlugin
    implements IZenithPlugin {

  private final BukkitScheduler scheduler;
  private final LoggerAdapter logger;
  private final PluginInfo info;
  private final MessageAPI messageAPI;
  private Lazy<ModuleAPI> moduleAPI = Lazy.of(() -> new ModuleAPI(this));

  /**
   * Creates a new Zenith plugin instance and initializes core services.
   */
  public ZenithPlugin() {
    super();
    this.info = new PluginInfo(getDescription());
    this.scheduler = new BukkitScheduler();
    logger = GlobalLogger.getLogger(super.getLogger().getName());
    messageAPI = new MessageAPI();
    if (!ZenithProvider.hasInstance()) {
      ZenithProvider.setInstance(this);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onLoad() {
    MessageFormatter.setPlaceholdersSupport(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onEnable() {
    moduleAPI.get().loadAll();
  }

  /**
   * Reloads plugin-managed state and shared Zenith configuration.
   */
  public void onReload() {
    MessageFormatter.setPlaceholdersSupport(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
    ZenithProvider.reloadZenithConfig();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onDisable() {
  }

  /**
   * Returns descriptive metadata for this plugin.
   *
   * @return the plugin information
   */
  public PluginInfo getInfo() {
    return info;
  }

  /**
   * Returns the plugin's internal key identifier.
   *
   * @return the plugin key
   */
  public String getKey() {
    return info.getKey();
  }

  /**
   * Returns the plugin's full display name.
   *
   * @return the full plugin name
   */
  public String getFullName() {
    return info.getFullName();
  }

  /**
   * Returns the message prefix associated with this plugin.
   *
   * @return the plugin prefix
   */
  public String getPrefix() {
    return info.getPrefix();
  }

  /**
   * Returns the plugin version.
   *
   * @return the plugin version
   */
  public Version getVersion() {
    return info.getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public File getFile() {
    return super.getFile();
  }

  /**
   * Resolves a file within the plugin data folder.
   *
   * @param path path segments relative to the data folder
   * @return the resolved file, or the data folder itself if no segments are provided
   */
  public File getDataFile(final String... path) {
    if (path == null || path.length == 0) {
      return this.getDataFolder();
    }
    return new File(this.getDataFolder(), StringUtils.join(File.separator, path));
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
  public LoggerAdapter getSlf4jLogger() {
    return logger;
  }

  /**
   * Returns the message API used for localized messaging.
   *
   * @return the message API
   */
  public MessageAPI messageAPI() {
    return messageAPI;
  }

  /**
   * Returns the module API used to manage plugin modules.
   *
   * @return the module API
   */
  public ModuleAPI moduleAPI() {
    return moduleAPI.get();
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @return never returns normally
   * @throws UnsupportedOperationException always thrown because Bukkit file configuration is not supported
   * @deprecated use Configurate-based configuration instead
   */
  @Deprecated
  @Override
  public FileConfiguration getConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @throws UnsupportedOperationException always thrown because Bukkit file configuration is not supported
   * @deprecated use Configurate-based configuration instead
   */
  @Override
  @Deprecated
  public void reloadConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @throws UnsupportedOperationException always thrown because Bukkit file configuration is not supported
   * @deprecated use Configurate-based configuration instead
   */
  @Override
  @Deprecated
  public void saveConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @throws UnsupportedOperationException always thrown because Bukkit file configuration is not supported
   * @deprecated use Configurate-based configuration instead
   */
  @Deprecated
  public void loadConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @param file the configuration file that would be loaded
   * @throws UnsupportedOperationException always thrown because Bukkit file configuration is not supported
   * @deprecated use Configurate-based configuration instead
   */
  @Deprecated
  public void loadConfig(final File file) {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @throws UnsupportedOperationException always thrown because Bukkit file configuration is not supported
   * @deprecated use Configurate-based configuration instead
   */
  @Override
  @Deprecated
  public void saveDefaultConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  /**
   * Returns a resource stream from the plugin's resources.
   *
   * @param filename the resource path to look up
   * @return the resource stream, or {@code null} if the resource is not found
   * @deprecated prefer Zenith resource utilities directly where appropriate
   */
  @Deprecated
  @Override
  public InputStream getResource(final String filename) {
    return ResourceUtils.getResourceStream(filename);
  }

  /**
   * Saves a bundled resource to the plugin data folder.
   *
   * @param path the resource path to save
   * @param replace whether to replace an existing file
   * @deprecated prefer Zenith resource utilities directly where appropriate
   */
  @Deprecated
  @Override
  public void saveResource(final String path, final boolean replace) {
    final File file = ResourceUtils.getResourceFile(path);
    if (file == null) {
      return;
    }
    ResourceUtils.saveResource(file, new File(getDataFolder(), path));
  }

  /**
   * Returns a plugin command registered to this plugin.
   *
   * @param name the command name or alias
   * @return the plugin command, or {@code null} if it is unavailable
   * @deprecated prefer CommandAPI-based command registration
   */
  @Override
  @Deprecated
  public PluginCommand getCommand(final String name) {
    final String alias = name.toLowerCase(java.util.Locale.ENGLISH);
    PluginCommand command = getServer().getPluginCommand(alias);

    if (command == null || command.getPlugin() != this) {
      command = getServer()
          .getPluginCommand(
              info.getName().toLowerCase(java.util.Locale.ENGLISH) +
                  ":" +
                  alias);
    }

    if (command != null && command.getPlugin() == this) {
      return command;
    } else {
      return null;
    }
  }

  /**
   * Returns a string representation of this plugin.
   *
   * @return the plugin's full name
   */
  @Override
  public String toString() {
    return info.getFullName();
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @param sender the command sender
   * @param command the executed command
   * @param label the alias used
   * @param args the command arguments
   * @return never returns normally
   * @throws UnsupportedOperationException always thrown because command handling is managed by CommandAPI
   * @deprecated use CommandAPI-based command registration instead
   */
  @Override
  @Deprecated
  public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    throw new UnsupportedOperationException(
        "onTabComplete is not supported in Main Plugin. Create Command with CommandAPI instead!");
  }

  /**
   * Not supported by Zenith plugins.
   *
   * @param sender the command sender
   * @param command the executed command
   * @param label the alias used
   * @param args the command arguments
   * @return never returns normally
   * @throws UnsupportedOperationException always thrown because command handling is managed by CommandAPI
   * @deprecated use CommandAPI-based command registration instead
   */
  @Override
  @Deprecated
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    throw new UnsupportedOperationException(
        "onCommand is not supported in Main Plugin. Create Command with CommandAPI instead!");
  }

  /**
   * Returns the default world generator for this plugin.
   *
   * @param worldName the world name
   * @param id the generator identifier
   * @return {@code null} because no default world generator is provided
   * @deprecated override in a concrete plugin only when a custom world generator is required
   */
  @Deprecated
  @Override
  public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
    return null;
  }

  /**
   * Returns the default biome provider for this plugin.
   *
   * @param worldName the world name
   * @param id the biome provider identifier
   * @return {@code null} because no default biome provider is provided
   * @deprecated override in a concrete plugin only when a custom biome provider is required
   */
  @Deprecated
  @Override
  public BiomeProvider getDefaultBiomeProvider(final String worldName, final String id) {
    return null;
  }
}
