package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar} is removed.
 */
public class BossBarRemoveEvent extends BossBarChangedEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;

  public BossBarRemoveEvent(final BossBar bossBar) {
    this(bossBar, null);
  }

  public BossBarRemoveEvent(final BossBar bossBar, final Player player) {
    super(bossBar);
    this.player = player;
  }

  /**
   * Gets the player this removal is associated with, or {@code null} if global.
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
