package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar} is created.
 */
public class BossBarCreateEvent extends BossBarChangedEvent {

  private static final HandlerList handlers = new HandlerList();

  public BossBarCreateEvent(final BossBar bossBar) {
    super(bossBar);
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
