package com.lemonlightmc.zenith.base;

import java.nio.file.Path;
import org.bukkit.Server;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;

import com.lemonlightmc.zenith.messages.MessageStore;
import com.lemonlightmc.zenith.scheduler.Scheduler;
import com.lemonlightmc.zenith.version.Version;

public interface IPlugin {

  public void onLoad();

  public void onEnable();

  public void onReload();

  public void onDisable();

  public boolean isEnabled();

  public PluginInfo getInfo();

  public String getKey();

  public String getName();

  public String getFullName();

  public String getPrefix();

  public Version getVersion();

  public Path getDataFolder();

  public boolean isNaggable();

  public void setNaggable(boolean canNag);

  public PluginLoader getPluginLoader();

  public PluginManager getPluginManager();

  public Scheduler getScheduler();

  public ServicesManager getServicesManager();

  public Server getServer();

  public java.util.logging.Logger getLogger();

  public MessageStore getMessageStore();
}
