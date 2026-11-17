package com.lemonlightmc.zenith.apis.bossbar.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.apis.bossbar.BossBar;

/**
 * Called when a {@link BossBar}'s progress changes.
 */
public class BossBarProgressEvent extends BossBarChangedEvent {

  /** The Bukkit handler list for this event type. */
  private static final HandlerList handlers = new HandlerList();
  /** The previous progress value. */
  private final double oldProgress;
  /** The new progress value. */
  private final double newProgress;

  /**
   * Creates a new boss bar progress event.
   *
   * @param bossBar the affected boss bar
   * @param oldProgress the previous progress value
   * @param newProgress the new progress value
   */
  public BossBarProgressEvent(final BossBar bossBar, final double oldProgress, final double newProgress) {
    super(bossBar);
    this.oldProgress = oldProgress;
    this.newProgress = newProgress;
  }

  /**
   * Returns the previous progress value.
   *
   * @return the previous progress value
   */
  public double oldProgress() {
    return oldProgress;
  }

  /**
   * Returns the new progress value.
   *
   * @return the new progress value
   */
  public double newProgress() {
    return newProgress;
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
