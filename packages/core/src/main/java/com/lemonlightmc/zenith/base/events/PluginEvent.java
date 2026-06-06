package com.lemonlightmc.zenith.base.events;

import org.bukkit.event.Event;

import com.lemonlightmc.zenith.base.IPlugin;

public abstract class PluginEvent extends Event {
  private final IPlugin plugin;

  public PluginEvent(final IPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Gets the plugin involved in this event
   *
   * @return Plugin for this event
   */
  public IPlugin getPlugin() {
    return plugin;
  }
}