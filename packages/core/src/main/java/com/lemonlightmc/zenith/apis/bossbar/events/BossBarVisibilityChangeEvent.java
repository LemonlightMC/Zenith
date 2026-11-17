package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar}'s visibility changes.
 */
public class BossBarVisibilityChangeEvent extends BossBarChangedEvent {

  /** The Bukkit handler list for this event type. */
  private static final HandlerList handlers = new HandlerList();
  /** The new visibility state. */
  private final boolean visible;
  /** The player associated with this visibility change, if applicable. */
  private final Player player;

  /**
   * Creates a new boss bar visibility change event without a player association.
   *
   * @param bossBar the affected boss bar
   * @param visible the new visibility state
   */
  public BossBarVisibilityChangeEvent(final BossBar bossBar, final boolean visible) {
    this(bossBar, visible, null);
  }

  /**
   * Creates a new boss bar visibility change event.
   *
   * @param bossBar the affected boss bar
   * @param visible the new visibility state
   * @param player the player associated with the change, or {@code null} for a global change
   */
  public BossBarVisibilityChangeEvent(final BossBar bossBar, final boolean visible, final Player player) {
    super(bossBar);
    this.visible = visible;
    this.player = player;
  }

  /**
   * Returns the new visibility state.
   *
   * @return {@code true} if visible; {@code false} otherwise
   */
  public boolean visible() {
    return visible;
  }

  /**
   * Returns the player associated with this visibility change, if any.
   *
   * @return the associated player, or {@code null} if the change is global
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
