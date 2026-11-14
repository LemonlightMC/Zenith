package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar} is added to a player.
 */
public class BossBarAddEvent extends BossBarChangedEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Player player;

  public BossBarAddEvent(final BossBar bossBar, final Player player) {
    super(bossBar);
    this.player = player;
  }

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
