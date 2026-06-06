package com.lemonlightmc.zenith.base;

import java.io.File;
import java.nio.file.Path;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;

import com.lemonlightmc.zenith.base.events.PluginDisableEvent;
import com.lemonlightmc.zenith.base.events.PluginEnableEvent;
import com.lemonlightmc.zenith.base.events.PluginLoadEvent;
import com.lemonlightmc.zenith.base.events.PluginReloadEvent;
import com.lemonlightmc.zenith.config.Configurate;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.messages.MessageStore;
import com.lemonlightmc.zenith.scheduler.Scheduler;
import com.lemonlightmc.zenith.utils.StringUtils;
import com.lemonlightmc.zenith.version.Version;

public abstract class PluginBase implements IPlugin {

  private final PluginLoader loader;
  private Server server = null;
  private Scheduler scheduler = null;
  private ClassLoader classLoader = null;
  private MessageStore messageStore = null;

  private final File file;
  private final File dataFolder;
  private final PluginInfo info;
  private boolean naggable = true;
  private boolean isEnabled = false;

  private static PluginBase instance = null;

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

  public static boolean hasInstance() {
    return instance != null;
  }

  @SuppressWarnings("unchecked")
  public static <I extends PluginBase> I getInstance() {
    if (instance == null) {
      throw new RuntimeException(
          "Plugin is not enabled - Plugin Instance can not be obtained!");
    }
    return (I) instance;
  }

  @Override
  public void onLoad() {
  }

  @Override
  public void onDisable() {
  }

  @Override
  public void onEnable() {
  }

  @Override
  public void onReload() {
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

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

  public void load() {
    MessageFormatter.setPlaceholdersSupport(server.getPluginManager().isPluginEnabled("PlaceholderAPI"));
    if (messageStore == null) {
      messageStore = new MessageStore();
    }
    messageStore.loadAll();
    if (scheduler == null) {
      scheduler = new Scheduler();
    }
    if (Configurate.options().createDefaults()) {
      Configurate.createDefaults();
    }
    onLoad();
    getPluginManager().callEvent(new PluginLoadEvent(this));
  }

  public void enable() {
    isEnabled = true;
    if (Configurate.options().autoLoad()) {
      Configurate.loadAll();
    }
    onEnable();
    getPluginManager().callEvent(new PluginEnableEvent(this));
  }

  public void reload() {
    MessageFormatter.setPlaceholdersSupport(server.getPluginManager().isPluginEnabled("PlaceholderAPI"));
    messageStore.reloadAll();
    if (Configurate.options().autoReload()) {
      Configurate.reloadAll();
    }
    onReload();
    getPluginManager().callEvent(new PluginReloadEvent(this));
  }

  public void disable() {
    getPluginManager().callEvent(new PluginDisableEvent(this));
    onDisable();
    if (Configurate.options().autoSave()) {
      Configurate.saveAll();
    }
    isEnabled = false;
  }

  @Override
  public PluginInfo getInfo() {
    return info;
  }

  @Override
  public String getKey() {
    return info.getKey();
  }

  @Override
  public String getName() {
    return info.getName();
  }

  @Override
  public String getFullName() {
    return info.getFullName();
  }

  @Override
  public String getPrefix() {
    return info.getPrefix();
  }

  @Override
  public Version getVersion() {
    return info.getVersion();
  }

  protected File getFile() {
    return file;
  }

  @Override
  public Path getDataFolder() {
    return dataFolder.toPath();
  }

  public File getDataFile(final String... path) {
    if (path == null || path.length == 0) {
      return dataFolder;
    }
    return new File(dataFolder, StringUtils.join(File.separator, path));
  }

  @Override
  public Server getServer() {
    return server;
  }

  @Override
  public PluginLoader getPluginLoader() {
    return loader;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  @Override
  public PluginManager getPluginManager() {
    return server.getPluginManager();
  }

  @Override
  public ServicesManager getServicesManager() {
    return server.getServicesManager();
  }

  @Override
  public Scheduler getScheduler() {
    return scheduler;
  }

  @Override
  public java.util.logging.Logger getLogger() {
    return server.getLogger();
  }

  @Override
  public MessageStore getMessageStore() {
    return messageStore;
  }

  @Override
  public boolean isNaggable() {
    return naggable;
  }

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
