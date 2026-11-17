package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar} is removed.
 */
public class BossBarRemoveEvent extends BossBarChangedEvent {

  /** The Bukkit handler list for this event type. */
  private static final HandlerList handlers = new HandlerList();
  /** The player associated with this removal, if applicable. */
  private final Player player;

  /**
   * Creates a new boss bar remove event without a player association.
   *
   * @param bossBar the removed boss bar
   */
  public BossBarRemoveEvent(final BossBar bossBar) {
    this(bossBar, null);
  }

  /**
   * Creates a new boss bar remove event.
   *
   * @param bossBar the removed boss bar
   * @param player the player associated with the removal, or {@code null} for a global removal
   */
  public BossBarRemoveEvent(final BossBar bossBar, final Player player) {
    super(bossBar);
    this.player = player;
  }

  /**
   * Returns the player associated with this removal, if any.
   *
   * @return the associated player, or {@code null} if the removal is global
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
