package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar}'s visibility changes.
 */
public class BossBarVisibilityChangeEvent extends BossBarChangedEvent {

  private static final HandlerList handlers = new HandlerList();
  private final boolean visible;
  private final Player player;

  public BossBarVisibilityChangeEvent(final BossBar bossBar, final boolean visible) {
    this(bossBar, visible, null);
  }

  public BossBarVisibilityChangeEvent(final BossBar bossBar, final boolean visible, final Player player) {
    super(bossBar);
    this.visible = visible;
    this.player = player;
  }

  public boolean visible() {
    return visible;
  }

  /**
   * Gets the player this visibility change is associated with, or {@code null} if
   * global.
   */
  public Player player() {
    return player;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
