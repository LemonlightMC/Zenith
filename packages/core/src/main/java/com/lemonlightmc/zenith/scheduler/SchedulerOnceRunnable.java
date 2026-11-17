package com.lemonlightmc.zenith.scheduler;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.ZenithProvider;

/**
 * A task that coalesces repeated run requests into a single scheduled execution.
 *
 * <p>Once scheduled, additional run requests do not schedule a second task until the
 * current execution completes. This is useful for deferring work to the next tick while
 * avoiding duplicate scheduling within the same tick.</p>
 *
 * <p>This class is thread-safe.</p>
 */
public abstract class SchedulerOnceRunnable implements Runnable {
  private static final int TASK_NOT_SCHEDULED = -1;
  private static final int TASK_SCHEDULED_SOON = -2;

  private final Runnable _logicProxy;
  private final AtomicInteger _scheduledId;

  /**
   * Creates a new once-scheduled runnable.
   */
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

  /**
   * Creates a {@link SchedulerOnceRunnable} that delegates execution to the given runnable.
   *
   * @param runnable the runnable to execute
   * @return a new once-scheduled runnable wrapper
   */
  public static SchedulerOnceRunnable create(final Runnable runnable) {
    return new SchedulerOnceRunnable() {
      @Override
      public void run() {
        runnable.run();
      }
    };
  }

  /**
   * Gets the plugin that owns scheduled executions of this task.
   *
   * @return the owning plugin instance
   */
  public Plugin getPlugin() {
    return ZenithProvider.instance();
  }

  /**
   * Checks whether this task is currently scheduled.
   *
   * @return {@code true} if the task is scheduled; {@code false} otherwise
   */
  public boolean isScheduled() {
    return this._scheduledId.get() != TASK_NOT_SCHEDULED;
  }

  /**
   * Schedules this task to run after the specified delay, replacing any existing schedule.
   *
   * @param delay the delay in ticks before execution
   */
  public void rerun(final long delay) {
    final int newTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(ZenithProvider.instance(), this._logicProxy,
        delay);
    final int previousTaskId = this._scheduledId.getAndSet(newTaskId);
    if (previousTaskId >= 0) {
      Bukkit.getScheduler().cancelTask(previousTaskId);
    }
  }

  /**
   * Schedules this task to run after the specified delay if it is not already scheduled.
   *
   * @param delay the delay in ticks before execution
   */
  public void runLater(final long delay) {
    if (this._scheduledId.compareAndSet(TASK_NOT_SCHEDULED, TASK_SCHEDULED_SOON)) {
      final int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(ZenithProvider.instance(), this._logicProxy,
          delay);
      if (!this._scheduledId.compareAndSet(TASK_SCHEDULED_SOON, taskId)) {
        Bukkit.getScheduler().cancelTask(taskId);
      }
    }
  }

  /**
   * Requests execution of this task on the next tick if it is not already scheduled.
   */
  @Override
  public void run() {
    runLater(1);
  }

  /**
   * Executes this task immediately if it is currently scheduled, cancelling the pending schedule.
   */
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

  /**
   * Cancels any pending scheduled execution of this task.
   */
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