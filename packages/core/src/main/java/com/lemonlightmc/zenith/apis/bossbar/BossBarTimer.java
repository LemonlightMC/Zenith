package com.lemonlightmc.zenith.apis.bossbar;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BossBarTimer extends BukkitRunnable {

  private final WeakReference<BossBar> bossBarRef;
  private final float progressMinus;
  private final Consumer<BossBar> consumer;

  public BossBarTimer(final BossBar bossbar, final long interval, final long duration, final long delay,
      final Consumer<BossBar> consumer) {
    this.bossBarRef = new WeakReference<>(bossbar);
    this.progressMinus = interval / duration;
    this.consumer = consumer;
    runTaskTimerAsynchronously(null, delay, interval);
  }

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