package com.lemonlightmc.zenith;

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

public abstract class ZenithPlugin extends org.bukkit.plugin.java.JavaPlugin
    implements com.lemonlightmc.zenith.IZenithPlugin {

  private final BukkitScheduler scheduler;
  private final LoggerAdapter logger;
  private final PluginInfo info;
  private final MessageAPI messageAPI;
  private Lazy<ModuleAPI> moduleAPI = Lazy.of(() -> new ModuleAPI(this));

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

  @Override
  public void onLoad() {
    MessageFormatter.setPlaceholdersSupport(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
  }

  @Override
  public void onEnable() {
    moduleAPI.get().loadAll();
  }

  public void onReload() {
    MessageFormatter.setPlaceholdersSupport(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
    ZenithProvider.reloadZenithConfig();
  }

  @Override
  public void onDisable() {
  }

  public PluginInfo getInfo() {
    return info;
  }

  public String getKey() {
    return info.getKey();
  }

  public String getFullName() {
    return info.getFullName();
  }

  public String getPrefix() {
    return info.getPrefix();
  }

  public Version getVersion() {
    return info.getVersion();
  }

  @Override
  public File getFile() {
    return super.getFile();
  }

  public File getDataFile(final String... path) {
    if (path == null || path.length == 0) {
      return this.getDataFolder();
    }
    return new File(this.getDataFolder(), StringUtils.join(File.separator, path));
  }

  @Override
  public Scheduler getScheduler() {
    return scheduler;
  }

  @Override
  public LoggerAdapter getSlf4jLogger() {
    return logger;
  }

  public MessageAPI messageAPI() {
    return messageAPI;
  }

  public ModuleAPI moduleAPI() {
    return moduleAPI.get();
  }

  @Deprecated
  @Override
  public FileConfiguration getConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  @Override
  @Deprecated
  public void reloadConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  @Override
  @Deprecated
  public void saveConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  @Deprecated
  public void loadConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  @Deprecated
  public void loadConfig(final File file) {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  @Override
  @Deprecated
  public void saveDefaultConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported by Zenith. Use Configurate instead (industry standard)!");
  }

  @Deprecated
  @Override
  public InputStream getResource(final String filename) {
    return ResourceUtils.getResourceStream(filename);
  }

  @Deprecated
  @Override
  public void saveResource(final String path, final boolean replace) {
    final File file = ResourceUtils.getResourceFile(path);
    if (file == null) {
      return;
    }
    ResourceUtils.saveResource(file, new File(getDataFolder(), path));
  }

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

  @Override
  public String toString() {
    return info.getFullName();
  }

  @Override
  @Deprecated
  public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
      final String[] args) {
    throw new UnsupportedOperationException(
        "onTabComplete is not supported in Main Plugin. Create Command with CommandAPI instead!");
  }

  @Override
  @Deprecated
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
    throw new UnsupportedOperationException(
        "onCommand is not supported in Main Plugin. Create Command with CommandAPI instead!");
  }

  @Deprecated
  @Override
  public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
    return null;
  }

  @Deprecated
  @Override
  public BiomeProvider getDefaultBiomeProvider(final String worldName, final String id) {
    return null;
  }
}
