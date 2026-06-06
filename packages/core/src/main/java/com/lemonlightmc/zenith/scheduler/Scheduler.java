package com.lemonlightmc.zenith.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import com.lemonlightmc.zenith.base.ZenithPlugin;

public class Scheduler implements IScheduler {

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

  private static long clamp(final long delay) {
    return Math.round(Math.min(Math.max(delay, 0), Integer.MAX_VALUE));
  }

  private static ScheduledTask wrap(final BukkitTask task, final ThreadContext ctx, final long interval,
      final long delay) {
    if (interval > 0) {
      return new RepeatingScheduledTask(task, task.getTaskId(), ctx, delay, interval);
    } else {
      return new ScheduledTask(task, task.getTaskId(), ctx, delay);
    }
  }

  @Override
  public ScheduledBuilder builder() {
    return new ScheduledBuilder();
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
    Bukkit.getScheduler().cancelTasks(ZenithPlugin.getInstance());
  }

  @Override
  public boolean isRunning(final int taskId) {
    return Bukkit.getScheduler().isCurrentlyRunning(taskId);
  }

  @Override
  public boolean isQueued(final int taskId) {
    return Bukkit.getScheduler().isQueued(taskId);
  }

  // run

  @Override
  public ScheduledTask run(final Runnable runnable) {
    return run(runnable, 0, -1);
  }

  @Override
  public ScheduledTask run(final Runnable runnable, final long delay) {
    return run(runnable, delay, -1);
  }

  @Override
  public ScheduledTask run(final Runnable runnable, long delay, long interval) {
    if (runnable == null) {
      return null;
    }
    delay = clamp(delay);
    interval = clamp(interval);
    if (interval > 0) {
      if (delay == 0) {
        return wrap(Bukkit.getScheduler().runTask(ZenithPlugin.getInstance(), runnable),
            ThreadContext.SYNC, delay, interval);
      } else {
        return wrap(Bukkit.getScheduler().runTaskLater(ZenithPlugin.getInstance(), runnable, delay),
            ThreadContext.SYNC, delay, interval);
      }
    } else {
      return wrap(Bukkit.getScheduler().runTaskTimer(ZenithPlugin.getInstance(), runnable, delay, interval),
          ThreadContext.SYNC, delay, interval);
    }
  }

  @Override
  public void run(final Consumer<ScheduledTask> consumer) {
    run(consumer, 0, 0);
  }

  @Override
  public void run(final Consumer<ScheduledTask> consumer, final long delay) {
    run(consumer, delay, 0);
  }

  @Override
  public void run(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    if (consumer == null) {
      return;
    }
    run(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, delay, interval);
  }

  @Override
  public Set<ScheduledTask> run(final Set<Runnable> runnables) {
    return run(runnables, 0, 0);
  }

  @Override
  public Set<ScheduledTask> run(final Set<Runnable> runnables, final long delay) {
    return run(runnables, delay, 0);
  }

  @Override
  public Set<ScheduledTask> run(final Set<Runnable> runnables, final long delay, final long interval) {
    if (runnables == null || runnables.isEmpty()) {
      return Set.of();
    }
    final Set<ScheduledTask> scheduledTasks = new HashSet<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(run(runnable, delay, interval));
    }
    return scheduledTasks;
  }

  // runAsync
  @Override
  public ScheduledTask runAsync(final Runnable runnable) {
    return runAsync(runnable, 0, 0);
  }

  @Override
  public ScheduledTask runAsync(final Runnable runnable, final long delay) {
    return runAsync(runnable, delay, 0);
  }

  @Override
  public ScheduledTask runAsync(final Runnable runnable, long delay, long interval) {
    if (runnable == null) {
      return null;
    }
    delay = clamp(delay);
    interval = clamp(interval);
    if (interval > 0) {
      if (delay == 0) {
        return wrap(Bukkit.getScheduler().runTaskAsynchronously(ZenithPlugin.getInstance(), runnable),
            ThreadContext.SYNC, delay, interval);
      } else {
        return wrap(Bukkit.getScheduler().runTaskLaterAsynchronously(ZenithPlugin.getInstance(), runnable, delay),
            ThreadContext.SYNC, delay, interval);
      }
    } else {
      return wrap(
          Bukkit.getScheduler().runTaskTimerAsynchronously(ZenithPlugin.getInstance(), runnable, delay, interval),
          ThreadContext.SYNC, delay, interval);
    }
  }

  @Override
  public void runAsync(final Consumer<ScheduledTask> consumer) {
    runAsync(consumer, 0, 0);
  }

  @Override
  public void runAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    runAsync(consumer, delay, 0);
  }

  @Override
  public void runAsync(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    if (consumer == null) {
      return;
    }
    runAsync(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, delay, interval);
  }

  @Override
  public Set<ScheduledTask> runAsync(final Set<Runnable> runnables) {
    return runAsync(runnables, 0, 0);
  }

  @Override
  public Set<ScheduledTask> runAsync(final Set<Runnable> runnables, final long delay) {
    return runAsync(runnables, delay, 0);
  }

  @Override
  public Set<ScheduledTask> runAsync(final Set<Runnable> runnables, final long delay, final long interval) {
    if (runnables == null || runnables.isEmpty()) {
      return Set.of();
    }
    final Set<ScheduledTask> scheduledTasks = new HashSet<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(runAsync(runnable, delay, interval));
    }
    return scheduledTasks;
  }
  // runLater

  @Override
  public ScheduledTask runLater(final Runnable runnable, final long delay) {
    return run(runnable, delay);
  }

  @Override
  public void runLater(final Consumer<ScheduledTask> consumer, final long delay) {
    run(consumer, delay, 0);
  }

  @Override
  public Set<ScheduledTask> runLater(final Set<Runnable> runnables, final long delay) {
    return run(runnables, delay);
  }

  // runLaterAsync

  @Override
  public ScheduledTask runLaterAsync(final Runnable runnable, final long delay) {
    return runAsync(runnable, delay);

  }

  @Override
  public void runLaterAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    runAsync(consumer, delay, 0);
  }

  @Override
  public Set<ScheduledTask> runLaterAsync(final Set<Runnable> runnables, final long delay) {
    return runAsync(runnables, delay);
  }

  // runEvery
  @Override
  public ScheduledTask runEvery(final Runnable runnable, final long interval, final long delay) {
    return run(runnable, interval, delay);
  }

  @Override
  public ScheduledTask runEvery(final Runnable runnable, final long interval) {
    return run(runnable, interval, 0);
  }

  @Override
  public void runEvery(final Consumer<ScheduledTask> consumer, final long interval) {
    run(consumer, interval, 0);
  }

  @Override
  public void runEvery(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    run(consumer, interval, delay);
  }

  @Override
  public Set<ScheduledTask> runEvery(final Set<Runnable> runnables, final long interval) {
    return run(runnables, interval, 0);
  }

  @Override
  public Set<ScheduledTask> runEvery(final Set<Runnable> runnables, final long interval, final long delay) {
    return run(runnables, delay, interval);
  }

  // runEveryAsync

  @Override
  public ScheduledTask runEveryAsync(final Runnable runnable, final long interval) {
    return runAsync(runnable, 0, interval);
  }

  @Override
  public ScheduledTask runEveryAsync(final Runnable runnable, final long interval, final long delay) {
    return runAsync(runnable, delay, interval);
  }

  @Override
  public void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval) {
    runAsync(consumer, interval, 0);
  }

  @Override
  public void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    runAsync(consumer, interval, delay);
  }

  @Override
  public Set<ScheduledTask> runEveryAsync(final Set<Runnable> runnables, final long interval) {
    return runAsync(runnables, 0, interval);
  }

  @Override
  public Set<ScheduledTask> runEveryAsync(final Set<Runnable> runnables, final long interval, final long delay) {
    return runAsync(runnables, delay, interval);
  }

  // repeat

  @Override
  public ScheduledTask repeat(final Runnable runnable, final int repetitions) {
    return repeat(runnable, repetitions, 0, 1, null);
  }

  @Override
  public ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval) {
    return repeat(runnable, repetitions, 0, interval, null);
  }

  @Override
  public ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval, final long delay) {
    return repeat(runnable, repetitions, delay, interval, null);
  }

  @Override
  public ScheduledTask repeat(final Runnable runnable, final int repetitions, final Runnable onComplete) {
    return repeat(runnable, repetitions, 0, 1, onComplete);
  }

  @Override
  public ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval,
      final Runnable onComplete) {
    return repeat(runnable, repetitions, 0, 1, onComplete);
  }

  @Override
  public ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval, final long delay,
      final Runnable onComplete) {
    final long repetitions0 = clamp(repetitions);
    return run(new SchedulerRunnable() {
      private int index = 0;

      @Override
      public void run() {
        if (++this.index >= repetitions0) {
          this.cancel();
          if (onComplete == null) {
            return;
          }
          onComplete.run();
          return;
        }
        runnable.run();
      }
    }, delay, interval);
  }

  // repeatAsync

  @Override
  public ScheduledTask repeatAsync(final Runnable runnable, final int repetitions) {
    return repeatAsync(runnable, repetitions, 0, 1, null);
  }

  @Override
  public ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval) {
    return repeatAsync(runnable, repetitions, 0, interval, null);
  }

  @Override
  public ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay) {
    return repeatAsync(runnable, repetitions, delay, interval, null);
  }

  @Override
  public ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final Runnable onComplete) {
    return repeatAsync(runnable, repetitions, 0, 1, onComplete);
  }

  @Override
  public ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final Runnable onComplete) {
    return repeatAsync(runnable, repetitions, 0, 1, onComplete);
  }

  @Override
  public ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay,
      final Runnable onComplete) {
    final long repetitions0 = clamp(repetitions);
    return runAsync(new SchedulerRunnable() {
      private int index = 0;

      @Override
      public void run() {
        if (++this.index >= repetitions0) {
          this.cancel();
          if (onComplete == null) {
            return;
          }
          onComplete.run();
          return;
        }
        runnable.run();
      }
    }, delay, interval);
  }

  // repeatWhile

  @Override
  public ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate, final long delay) {
    return repeatWhile(runnable, predicate, delay, 0, null);
  }

  @Override
  public ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate, final long delay,
      final long interval) {
    return repeatWhile(runnable, predicate, interval, delay, null);
  }

  @Override
  public ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate, final long delay,
      final Runnable onComplete) {
    return repeatWhile(runnable, predicate, delay, 0, onComplete);
  }

  @Override
  public ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate, final long delay,
      final long interval, final Runnable onComplete) {
    return run(new SchedulerRunnable() {
      @Override
      public void run() {
        try {
          if (!predicate.call()) {
            this.cancel();
            if (onComplete == null) {
              return;
            }

            onComplete.run();
            return;
          }

          runnable.run();
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }, delay, interval);
  }

  // repeatWhileAsync

  @Override
  public ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate, final long delay) {
    return repeatWhile(runnable, predicate, delay, 0, null);
  }

  @Override
  public ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate, final long delay,
      final long interval) {
    return repeatWhile(runnable, predicate, interval, delay, null);
  }

  @Override
  public ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate, final long delay,
      final Runnable onComplete) {
    return repeatWhile(runnable, predicate, delay, 0, onComplete);
  }

  @Override
  public ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate, final long delay,
      final long interval, final Runnable onComplete) {
    return runAsync(new SchedulerRunnable() {
      @Override
      public void run() {
        try {
          if (!predicate.call()) {
            this.cancel();
            if (onComplete == null) {
              return;
            }

            onComplete.run();
            return;
          }

          runnable.run();
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }, delay, interval);
  }

  @Override
  public String toString() {
    return "Scheduler []";
  }
}