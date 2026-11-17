package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar} is added to a player.
 */
public class BossBarAddEvent extends BossBarChangedEvent {

  /** The Bukkit handler list for this event type. */
  private static final HandlerList handlers = new HandlerList();
  /** The player associated with this event. */
  private final Player player;

  /**
   * Creates a new boss bar add event.
   *
   * @param bossBar the affected boss bar
   * @param player the player receiving the boss bar
   */
  public BossBarAddEvent(final BossBar bossBar, final Player player) {
    super(bossBar);
    this.player = player;
  }

  /**
   * Returns the player receiving the boss bar.
   *
   * @return the associated player
   */
  public Player player() {
    return player;
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
