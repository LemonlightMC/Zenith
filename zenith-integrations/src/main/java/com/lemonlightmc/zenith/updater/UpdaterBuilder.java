package com.lemonlightmc.zenith.updater;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.updater.PlatformData.AbstractPlatformData;
import com.lemonlightmc.zenith.updater.PlatformData.GithubData;
import com.lemonlightmc.zenith.updater.PlatformData.HangarData;
import com.lemonlightmc.zenith.updater.PlatformData.ModrinthData;
import com.lemonlightmc.zenith.updater.PlatformData.SpigotData;

public class UpdaterBuilder implements Builder<Updater> {
  private final Plugin plugin;
  private final PluginData pluginData;
  private long checkFrequency = 600;

  public UpdaterBuilder(Plugin plugin) {
    this.plugin = plugin;
    this.pluginData = new PluginData(plugin, new ArrayList<>());
  }

  public UpdaterBuilder github(String githubRepo) {
    return platform(new GithubData(githubRepo));
  }

  public UpdaterBuilder hangar(String hangarProjectSlug) {
    return platform(new HangarData(hangarProjectSlug));
  }

  public UpdaterBuilder modrinth(String modrinthProjectId, boolean featuredOnly) {
    return platform(new ModrinthData(modrinthProjectId, featuredOnly));
  }

  public UpdaterBuilder spigot(String spigotResourceId) {
    return platform(new SpigotData(spigotResourceId));
  }

  public UpdaterBuilder platform(AbstractPlatformData platformData) {
    this.pluginData.addPlatform(platformData);
    return this;
  }

  public UpdaterBuilder checkSchedule(long seconds) {
    this.checkFrequency = seconds;
    return this;
  }

  @Override
  public Updater build() {
    if (pluginData.getPlatformData().isEmpty()) {
      throw new IllegalStateException("At least 1 platform must be registered before building the Updater.");
    }

    Updater updater = new Updater(plugin, pluginData);

    if (checkFrequency > 0) {
      Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, updater::checkForUpdate, 0,
          checkFrequency * 20);
    }

    return updater;
  }
}
