package com.lemonlightmc.zenith.base;

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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;

import com.lemonlightmc.zenith.config.Configurate;
import com.lemonlightmc.zenith.files.ResourceUtils;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.messages.MessageStore;
import com.lemonlightmc.zenith.ZenithPlugin;
import com.lemonlightmc.zenith.scheduler.Scheduler;
import com.lemonlightmc.zenith.utils.StringUtils;
import com.lemonlightmc.zenith.version.Version;

public abstract class ZenithPlugin extends org.bukkit.plugin.java.JavaPlugin implements ZenithPlugin {

  private final Scheduler scheduler;

  private final PluginInfo info;
  private final MessageStore messageStore;

  public ZenithPlugin() {
    super();
    this.info = new PluginInfo(getDescription());
    this.scheduler = new Scheduler();
    messageStore = new MessageStore();
  }

  public static boolean hasInstance() {
    return instance != null;
  }

  @SuppressWarnings("unchecked")
  public static <I extends ZenithPlugin> I getInstance() {
    if (instance == null) {
      throw new RuntimeException(
          "Plugin is not enabled - Plugin Instance can not be obtained!");
    }
    return (I) instance;
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

  public PluginManager getPluginManager() {
    return Bukkit.getServer().getPluginManager();
  }

  public ServicesManager getServicesManager() {
    return Bukkit.getServer().getServicesManager();
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

  @Override
  public java.util.logging.Logger getLogger() {
    return Bukkit.getServer().getLogger();
  }

  public MessageStore getMessageStore() {
    return messageStore;
  }

  @Deprecated
  @Override
  public FileConfiguration getConfig() {
    throw new UnsupportedOperationException(
        "FileConfiguration is not supported in PluginBase. Use Configurate instead.");
  }

  @Override
  @Deprecated
  public void reloadConfig() {
    Configurate.reloadAll();
  }

  @Override
  @Deprecated
  public void saveConfig() {
    Configurate.saveAll();
  }

  @Deprecated
  public void loadConfig() {
    Configurate.loadAll();
  }

  @Deprecated
  public void loadConfig(final File file) {
    Configurate.load(file.getName());
  }

  @Override
  @Deprecated
  public void saveDefaultConfig() {
    Configurate.createDefaults();
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
  public void onLoad() {
    MessageFormatter.setPlaceholdersSupport(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
    messageStore.loadAll();
    if (Configurate.options().createDefaults()) {
      Configurate.createDefaults();
    }
  }

  @Override
  public void onEnable() {
    if (Configurate.options().autoLoad()) {
      Configurate.loadAll();
    }
  }

  public void onReload() {
    MessageFormatter.setPlaceholdersSupport(Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
    messageStore.reloadAll();
    if (Configurate.options().autoReload()) {
      Configurate.reloadAll();
    }
  }

  @Override
  public void onDisable() {
    if (Configurate.options().autoSave()) {
      Configurate.saveAll();
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
