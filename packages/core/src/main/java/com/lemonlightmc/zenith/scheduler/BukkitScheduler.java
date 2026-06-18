package com.lemonlightmc.zenith.scheduler;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import com.lemonlightmc.zenith.ZenithProvider;

public class BukkitScheduler implements Scheduler {
  private static final int NO_REPEATING = -1;

  public static enum ThreadContext {
    SYNC,
    ASYNC;

    public static ThreadContext forCurrentThread() {
      return Bukkit.getServer().isPrimaryThread() ? SYNC : ASYNC;
    }

    public static ThreadContext forThread(final Thread thread) {
      return (Bukkit.getServer().isPrimaryThread() && thread == Thread.currentThread())
          ? SYNC
          : ASYNC;
    }
  }

  private static long clamp(final long num) {
    return Math.min(Math.max(num, 0), Integer.MAX_VALUE);
  }

  @Override
  public ScheduledBuilder builder() {
    return new ScheduledBuilder(this);
  }

  @Override
  public List<BukkitWorker> getWorkers() {
    return Bukkit.getScheduler().getActiveWorkers();
  }

  @Override
  public List<BukkitTask> getPendingTasks() {
    return Bukkit.getScheduler().getPendingTasks();
  }

  @Override
  public void cancelTasks(final Collection<Integer> taskIds) {
    for (final int id : taskIds) {
      Bukkit.getScheduler().cancelTask(id);
    }
  }

  @Override
  public void cancelTasks(final int taskId) {
    Bukkit.getScheduler().cancelTask(taskId);
  }

  @Override
  public void cancelTasks(final int... taskIds) {
    for (final int id : taskIds) {
      Bukkit.getScheduler().cancelTask(id);
    }
  }

  @Override
  public void cancelTasks(final Plugin plugin) {
    Bukkit.getScheduler().cancelTasks(plugin);
  }

  @Override
  public void cancelTasks() {
    Bukkit.getScheduler().cancelTasks(ZenithProvider.getInstance());
  }

  @Override
  public boolean isRunning(final int taskId) {
    return Bukkit.getScheduler().isCurrentlyRunning(taskId);
  }

  @Override
  public boolean isQueued(final int taskId) {
    return Bukkit.getScheduler().isQueued(taskId);
  }

  @Override
  public boolean isGlobalThread() {
    return Bukkit.getServer().isPrimaryThread();
  }

  @Override
  public boolean isEntityThread(final Entity entity) {
    return Bukkit.getServer().isPrimaryThread();
  }

  @Override
  public boolean isRegionThread(final Location location) {
    return Bukkit.getServer().isPrimaryThread();
  }

  // run
  // CraftBukkit's scheduler will always redirect to runTaskTimer, which is why we
  // can short-cut the other methods!!

  @Override
  public ScheduledTask run(final Runnable runnable) {
    if (runnable == null) {
      return null;
    }
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimer(ZenithProvider.getInstance(), runnable, 0, NO_REPEATING),
        ThreadContext.SYNC, 0, NO_REPEATING);
  }

  @Override
  public ScheduledTask run(final Runnable runnable, final long delay) {
    return run(runnable, delay, NO_REPEATING);
  }

  @Override
  public ScheduledTask run(final Runnable runnable, long delay, long interval) {
    if (runnable == null) {
      return null;
    }
    delay = clamp(delay);
    interval = interval == NO_REPEATING ? NO_REPEATING : clamp(interval);
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimer(ZenithProvider.getInstance(), runnable, delay, interval),
        ThreadContext.SYNC, delay, interval);
  }

  @Override
  public void run(final Consumer<ScheduledTask> consumer) {
    if (consumer == null) {
      return;
    }
    Bukkit.getScheduler().runTaskTimer(ZenithProvider.getInstance(), new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, 0, NO_REPEATING);
  }

  @Override
  public void run(final Consumer<ScheduledTask> consumer, final long delay) {
    if (consumer == null) {
      return;
    }
    Bukkit.getScheduler().runTaskTimer(ZenithProvider.getInstance(), new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, clamp(delay), NO_REPEATING);
  }

  @Override
  public void run(final Consumer<ScheduledTask> consumer, long delay, long interval) {
    if (consumer == null) {
      return;
    }
    delay = clamp(delay);
    interval = interval == NO_REPEATING ? NO_REPEATING : clamp(interval);
    Bukkit.getScheduler().runTaskTimer(ZenithProvider.getInstance(), new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, delay, interval);
  }

  // runAsync
  @Override
  public ScheduledTask runAsync(final Runnable runnable) {
    if (runnable == null) {
      throw new IllegalArgumentException("Scheduler Runnable cannot be null");
    }
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimerAsynchronously(ZenithProvider.getInstance(), runnable, 0, 0),
        ThreadContext.SYNC, 0, 0);
  }

  @Override
  public ScheduledTask runAsync(final Runnable runnable, long delay) {
    if (runnable == null) {
      return null;
    }
    delay = clamp(delay);
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimerAsynchronously(ZenithProvider.getInstance(), runnable, delay, NO_REPEATING),
        ThreadContext.ASYNC, delay, NO_REPEATING);
  }

  @Override
  public ScheduledTask runAsync(final Runnable runnable, long delay, long interval) {
    if (runnable == null) {
      return null;
    }
    delay = clamp(delay);
    interval = interval == -1 ? -1 : clamp(interval);
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimerAsynchronously(ZenithProvider.getInstance(), runnable, delay, interval),
        ThreadContext.ASYNC, delay, interval);
  }

  @Override
  public ScheduledTask runAsync(final Location location, final Runnable runnable) {
    return runAsync(runnable);
  }

  @Override
  public ScheduledTask runAsync(final Entity entity, final Runnable runnable) {
    return runAsync(runnable);
  }
}