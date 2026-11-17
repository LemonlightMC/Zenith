package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;
import com.lemonlightmc.zenith.events.BaseEvent;

/**
 * Base Bukkit event for changes related to a {@link BossBar}.
 */
public class BossBarChangedEvent extends BaseEvent {

  /** The Bukkit handler list for this event type. */
  private static final HandlerList handlers = new HandlerList();
  /** The boss bar associated with this event. */
  private final BossBar bossBar;

  /**
   * Creates a new boss bar change event.
   *
   * @param bossBar the affected boss bar
   */
  public BossBarChangedEvent(final BossBar bossBar) {
    this.bossBar = bossBar;
  }

  /**
   * Returns the boss bar associated with this event.
   *
   * @return the affected boss bar
   */
  public BossBar bossBar() {
    return bossBar;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  /**
   * Returns the Bukkit handler list for this event type.
   *
   * @return the handler list
   */
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
