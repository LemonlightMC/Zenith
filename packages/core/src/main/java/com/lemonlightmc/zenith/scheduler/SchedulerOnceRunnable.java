package com.lemonlightmc.zenith.scheduler;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.ZenithProvider;

/**
 * Task that can be asked to run repeatedly, but once scheduled, will
 * not schedule a second time. This allows a task to run one tick delayed,
 * no matter how many times it is asked to run during a single tick.
 * Once executed, it can be started again for another execution in the
 * future.<br>
 * <br>
 * This class is thread-safe.
 */
public abstract class SchedulerOnceRunnable implements Runnable {
  private static final int TASK_NOT_SCHEDULED = -1;
  private static final int TASK_SCHEDULED_SOON = -2;

  private final Runnable _logicProxy;
  private final AtomicInteger _scheduledId;

  public SchedulerOnceRunnable() {
    this._scheduledId = new AtomicInteger(TASK_NOT_SCHEDULED);
    this._logicProxy = () -> {
      // First, reset the scheduled task id to TASK_NOT_SCHEDULED
      // If the task was still scheduled (not cancelled), run the task.
      int oldTaskId;
      do {
        oldTaskId = this._scheduledId.get();
      } while (!this._scheduledId.compareAndSet(oldTaskId, TASK_NOT_SCHEDULED));

      if (oldTaskId >= 0) {
        SchedulerOnceRunnable.this.run();
      }
    };
  }

  public static SchedulerOnceRunnable create(final Runnable runnable) {
    return new SchedulerOnceRunnable() {
      @Override
      public void run() {
        runnable.run();
      }
    };
  }

  public Plugin getPlugin() {
    return ZenithProvider.instance();
  }

  public boolean isScheduled() {
    return this._scheduledId.get() != TASK_NOT_SCHEDULED;
  }

  public void rerun(final long delay) {
    final int newTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(ZenithProvider.instance(), this._logicProxy,
        delay);
    final int previousTaskId = this._scheduledId.getAndSet(newTaskId);
    if (previousTaskId >= 0) {
      Bukkit.getScheduler().cancelTask(previousTaskId);
    }
  }

  public void runLater(final long delay) {
    if (this._scheduledId.compareAndSet(TASK_NOT_SCHEDULED, TASK_SCHEDULED_SOON)) {
      final int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(ZenithProvider.instance(), this._logicProxy,
          delay);
      if (!this._scheduledId.compareAndSet(TASK_SCHEDULED_SOON, taskId)) {
        Bukkit.getScheduler().cancelTask(taskId);
      }
    }
  }

  @Override
  public void run() {
    runLater(1);
  }

  public void runNowIfScheduled() {
    int oldTaskId;
    do {
      oldTaskId = this._scheduledId.get();
    } while (!this._scheduledId.compareAndSet(oldTaskId, TASK_NOT_SCHEDULED));

    if (oldTaskId >= 0) {
      Bukkit.getScheduler().cancelTask(oldTaskId);
      this.run();
    }
  }

  public void cancel() {
    int oldTaskId;
    do {
      oldTaskId = this._scheduledId.get();
    } while (!this._scheduledId.compareAndSet(oldTaskId, TASK_NOT_SCHEDULED));

    if (oldTaskId >= 0) {
      Bukkit.getScheduler().cancelTask(oldTaskId);
    }
  }
}