package com.lemonlightmc.zenith.plugin.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.plugin.IPlugin;

public class PluginReloadEvent extends PluginEvent {
  private static final HandlerList handlers = new HandlerList();

  public PluginReloadEvent(final IPlugin plugin) {
    super(plugin);
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
