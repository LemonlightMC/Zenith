package com.lemonlightmc.zenith.base.events;

import org.bukkit.event.HandlerList;
import com.lemonlightmc.zenith.base.IPlugin;

public class PluginEnableEvent extends PluginEvent {
  private static final HandlerList handlers = new HandlerList();

  public PluginEnableEvent(final IPlugin plugin) {
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
