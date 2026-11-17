package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar}'s title changes.
 */
public class BossBarNameChangedEvent extends BossBarChangedEvent {

  /** The Bukkit handler list for this event type. */
  private static final HandlerList handlers = new HandlerList();
  /** The previous title value. */
  private final String oldName;
  /** The new title value. */
  private final String newName;

  /**
   * Creates a new boss bar name changed event.
   *
   * @param bossBar the affected boss bar
   * @param oldName the previous title
   * @param newName the new title
   */
  public BossBarNameChangedEvent(final BossBar bossBar, final String oldName, final String newName) {
    super(bossBar);
    this.oldName = oldName;
    this.newName = newName;
  }

  /**
   * Returns the previous boss bar title.
   *
   * @return the previous title
   */
  public String oldName() {
    return oldName;
  }

  /**
   * Returns the new boss bar title.
   *
   * @return the new title
   */
  public String newName() {
    return newName;
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
