package com.lemonlightmc.zenith.apis.bossbar;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Schedules timed progress updates for a {@link BossBar}.
 *
 * <p>The timer decreases the boss bar progress on a fixed interval and optionally invokes a callback on each tick.
 */
public class BossBarTimer extends BukkitRunnable {

  /** A weak reference to the managed boss bar. */
  private final WeakReference<BossBar> bossBarRef;
  /** The amount subtracted from progress on each timer tick. */
  private final float progressMinus;
  /** The callback invoked after each timer update. */
  private final Consumer<BossBar> consumer;

  /**
   * Creates and starts a boss bar timer task.
   *
   * @param bossbar the boss bar to update
   * @param interval the tick interval between updates
   * @param duration the total duration used to calculate progress changes
   * @param delay the initial delay before the first update
   * @param consumer the callback invoked after each update
   */
  public BossBarTimer(final BossBar bossbar, final long interval, final long duration, final long delay,
      final Consumer<BossBar> consumer) {
    this.bossBarRef = new WeakReference<>(bossbar);
    this.progressMinus = interval / duration;
    this.consumer = consumer;
    runTaskTimerAsynchronously(null, delay, interval);
  }

  /**
   * Advances the boss bar timer by one scheduled tick.
   */
  @Override
  public void run() {
    if (bossBarRef.refersTo(null)) {
      cancel();
      return;
    }
    final BossBar bossBar = bossBarRef.get();
    final double newProgress = bossBar.getProgress() - progressMinus;
    if (newProgress <= 0) {
      for (final Player player : bossBar.getPlayers()) {
        bossBar.removePlayers(player);
      }
      cancel();
    } else {
      bossBar.setProgress(newProgress);
    }
    consumer.accept(bossBar);
  }
}