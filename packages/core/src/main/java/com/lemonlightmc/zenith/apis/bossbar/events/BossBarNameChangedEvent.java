package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar}'s title/name changes.
 */
public class BossBarNameChangedEvent extends BossBarChangedEvent {

  private static final HandlerList handlers = new HandlerList();
  private final String oldName;
  private final String newName;

  public BossBarNameChangedEvent(final BossBar bossBar, final String oldName, final String newName) {
    super(bossBar);
    this.oldName = oldName;
    this.newName = newName;
  }

  public String oldName() {
    return oldName;
  }

  public String newName() {
    return newName;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
