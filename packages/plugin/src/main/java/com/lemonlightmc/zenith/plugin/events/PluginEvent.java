package com.lemonlightmc.zenith.plugin.events;

import org.bukkit.event.Event;

import com.lemonlightmc.zenith.plugin.IPlugin;

/**
 * Base event for plugin lifecycle notifications.
 */
public abstract class PluginEvent extends Event {
  private final IPlugin plugin;

  /**
   * Creates a new plugin event.
   *
   * @param plugin the plugin associated with this event
   */
  public PluginEvent(final IPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Returns the plugin involved in this event.
   *
   * @return the plugin associated with this event
   */
  public IPlugin getPlugin() {
    return plugin;
  }
}