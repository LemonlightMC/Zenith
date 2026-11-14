package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;
import com.lemonlightmc.zenith.events.BaseEvent;

/**
 * Base Bukkit event for changes related to a {@link BossBar}.
 */
public class BossBarChangedEvent extends BaseEvent {

  private static final HandlerList handlers = new HandlerList();
  private final BossBar bossBar;

  public BossBarChangedEvent(final BossBar bossBar) {
    this.bossBar = bossBar;
  }

  public BossBar bossBar() {
    return bossBar;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
