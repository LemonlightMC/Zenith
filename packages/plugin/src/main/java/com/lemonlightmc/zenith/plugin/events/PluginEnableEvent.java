package com.lemonlightmc.zenith.plugin.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.plugin.IPlugin;

/**
 * Called when a plugin is enabled.
 */
public class PluginEnableEvent extends PluginEvent {
  private static final HandlerList handlers = new HandlerList();

  /**
   * Creates a new plugin enable event.
   *
   * @param plugin the plugin being enabled
   */
  public PluginEnableEvent(final IPlugin plugin) {
    super(plugin);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  /**
   * Returns the handler list for this event type.
   *
   * @return the handler list
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
