package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar}'s progress changes.
 */
public class BossBarProgressEvent extends BossBarChangedEvent {

  private static final HandlerList handlers = new HandlerList();
  private final double oldProgress;
  private final double newProgress;

  public BossBarProgressEvent(final BossBar bossBar, final double oldProgress, final double newProgress) {
    super(bossBar);
    this.oldProgress = oldProgress;
    this.newProgress = newProgress;
  }

  public double oldProgress() {
    return oldProgress;
  }

  public double newProgress() {
    return newProgress;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
