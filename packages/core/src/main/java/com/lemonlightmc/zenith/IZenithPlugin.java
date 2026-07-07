package com.lemonlightmc.zenith;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;

import com.lemonlightmc.zenith.messages.MessageAPI;
import com.lemonlightmc.zenith.scheduler.Scheduler;
import com.lemonlightmc.zenith.version.Version;

public interface IZenithPlugin extends Plugin {

  public PluginInfo getInfo();

  public String getKey();

  public String getFullName();

  public String getPrefix();

  public Version getVersion();

  public File getFile();

  public File getDataFile(final String... path);

  public PluginManager getPluginManager();

  public ServicesManager getServicesManager();

  public Scheduler getScheduler();

  @Override
  public java.util.logging.Logger getLogger();

  public Logger getLog4jLogger();

  public MessageAPI getMessageAPI();

  @Deprecated
  @Override
  public FileConfiguration getConfig();

  @Override
  @Deprecated
  public void reloadConfig();

  @Override
  @Deprecated
  public void saveConfig();

  @Deprecated
  public void loadConfig();

  @Deprecated
  public void loadConfig(final File file);

  @Override
  @Deprecated
  public void saveDefaultConfig();

  @Deprecated
  @Override
  public InputStream getResource(final String filename);

  @Deprecated
  @Override
  public void saveResource(final String path, final boolean replace);

  @Deprecated
  public PluginCommand getCommand(final String name);

  @Override
  public void onLoad();

  @Override
  public void onEnable();

  public void onReload();

  @Override
  public void onDisable();

  @Override
  public String toString();

  @Override
  @Deprecated
  public List<String> onTabComplete(final CommandSender sender, final Command command, final String label,
      final String[] args);

  @Override
  @Deprecated
  public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args);

  @Deprecated
  @Override
  public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id);

  @Deprecated
  @Override
  public BiomeProvider getDefaultBiomeProvider(final String worldName, final String id);
}
