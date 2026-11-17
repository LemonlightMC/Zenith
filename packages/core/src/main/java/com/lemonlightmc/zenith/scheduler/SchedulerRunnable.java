package com.lemonlightmc.zenith.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.scheduler.BukkitScheduler.ThreadContext;

/**
 * A {@link Runnable} base class for tasks that can schedule themselves and expose
 * information about their associated {@link ScheduledTask}.
 *
 * <p>Subclasses implement {@link #run()} and can use the provided scheduling helpers to
 * register this runnable with the Bukkit scheduler.</p>
 */
public abstract class SchedulerRunnable implements Runnable {

  /**
   * The currently scheduled task backing this runnable, or {@code null} if not scheduled.
   */
  protected ScheduledTask task;

  /**
   * Checks whether this task has been cancelled.
   *
   * @return {@code true} if the task is scheduled and cancelled; {@code false} otherwise
   */
  public synchronized boolean isCancelled() {
    return task != null && task.isCancelled();
  }

  /**
   * Cancels this task if it is currently scheduled.
   */
  public synchronized void cancel() {
    if (task != null) {
      task.cancel();
      task = null;
    }
  }

  /**
   * Gets the scheduled task backing this runnable.
   *
   * @return the backing task, or {@code null} if not scheduled
   */
  public synchronized ScheduledTask getTask() {
    return task;
  }

  /**
   * Gets the Bukkit task id of the scheduled task.
   *
   * @return the task id, or {@code -1} if unavailable
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized int getTaskId() {
    checkScheduled();
    return task == null ? -1 : task.getTaskId();
  }

  /**
   * Gets the plugin that owns the scheduled task.
   *
   * @return the owning plugin, or {@code null} if unavailable
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized Plugin getOwner() throws IllegalStateException {
    checkScheduled();
    return task == null ? null : task.getOwner();
  }

  /**
   * Gets the thread context in which the scheduled task runs.
   *
   * @return the thread context of the task
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized ThreadContext getThreadContext() throws IllegalStateException {
    checkScheduled();
    return task.getThreadContext();
  }

  /**
   * Checks whether the scheduled task is currently running.
   *
   * @return {@code true} if the task is running; {@code false} otherwise
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized boolean isRunning() throws IllegalStateException {
    checkScheduled();
    return task.isRunning();
  }

  /**
   * Checks whether the scheduled task is currently queued.
   *
   * @return {@code true} if the task is queued; {@code false} otherwise
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized boolean isQueued() throws IllegalStateException {
    checkScheduled();
    return task.isQueued();
  }

  /**
   * Checks whether the scheduled task runs synchronously.
   *
   * @return {@code true} if the task is synchronous; {@code false} otherwise
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized boolean isSync() throws IllegalStateException {
    checkScheduled();
    return task.isSync();
  }

  /**
   * Checks whether the scheduled task runs asynchronously.
   *
   * @return {@code true} if the task is asynchronous; {@code false} otherwise
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized boolean isAsync() throws IllegalStateException {
    checkScheduled();
    return task.isAsync();
  }

  /**
   * Checks whether the scheduled task was created with an initial delay.
   *
   * @return {@code true} if the task is delayed; {@code false} otherwise
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized boolean isDelayed() throws IllegalStateException {
    checkScheduled();
    return task.isDelayed();
  }

  /**
   * Gets the initial delay configured for the scheduled task.
   *
   * @return the delay in ticks
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized long getDelay() {
    return task.getDelay();
  }

  /**
   * Checks whether the scheduled task repeats.
   *
   * @return {@code true} if the task is repeating; {@code false} otherwise
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized boolean isRepeating() throws IllegalStateException {
    checkScheduled();
    return task.isRepeating();
  }

  /**
   * Gets the repeating interval configured for the scheduled task.
   *
   * @return the interval in ticks
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  public synchronized long getInterval() {
    return task.getInterval();
  }

  /**
   * Schedules this runnable to run synchronously on the next tick.
   *
   * @return the scheduled task
   * @throws IllegalStateException if this runnable has already been scheduled
   */
  public synchronized ScheduledTask runTask() throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTask(ZenithProvider.instance(), this), ThreadContext.SYNC, 0, -1);
  }

  /**
   * Schedules this runnable to run asynchronously.
   *
   * @return the scheduled task
   * @throws IllegalStateException if this runnable has already been scheduled
   */
  public synchronized ScheduledTask runTaskAsync() throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskAsynchronously(ZenithProvider.instance(), this), ThreadContext.ASYNC, 0, -1);
  }

  /**
   * Schedules this runnable to run synchronously after the specified delay.
   *
   * @param delay the delay in ticks before execution
   * @return the scheduled task
   * @throws IllegalStateException if this runnable has already been scheduled
   */
  public synchronized ScheduledTask runTaskLater(final long delay) throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskLater(ZenithProvider.instance(), this, delay), ThreadContext.SYNC, delay, -1);
  }

  /**
   * Schedules this runnable to run asynchronously after the specified delay.
   *
   * @param delay the delay in ticks before execution
   * @return the scheduled task
   * @throws IllegalStateException if this runnable has already been scheduled
   */
  public synchronized ScheduledTask runTaskLaterAsync(final long delay)
      throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskLaterAsynchronously(ZenithProvider.instance(), this, delay),
        ThreadContext.ASYNC, delay, -1);
  }

  /**
   * Schedules this runnable to run synchronously on a repeating interval.
   *
   * @param delay the initial delay in ticks before the first execution
   * @param interval the interval in ticks between executions
   * @return the scheduled task
   * @throws IllegalStateException if this runnable has already been scheduled
   */
  public synchronized ScheduledTask runTaskRepeating(final long delay, final long interval)
      throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimer(ZenithProvider.instance(), this, delay, interval),
        ThreadContext.SYNC, delay, interval);
  }

  /**
   * Schedules this runnable to run asynchronously on a repeating interval.
   *
   * @param delay the initial delay in ticks before the first execution
   * @param interval the interval in ticks between executions
   * @return the scheduled task
   * @throws IllegalStateException if this runnable has already been scheduled
   */
  public synchronized ScheduledTask runTaskRepeatingAsync(final long delay, final long interval)
      throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimerAsynchronously(ZenithProvider.instance(), this, delay,
            interval),
        ThreadContext.ASYNC, delay, interval);
  }

  /**
   * Ensures that this runnable has been scheduled.
   *
   * @throws IllegalStateException if the task has not been scheduled yet
   */
  private void checkScheduled() {
    if (task == null) {
      throw new IllegalStateException("Not scheduled yet");
    }
  }

  /**
   * Ensures that this runnable has not been scheduled yet.
   *
   * @throws IllegalStateException if the task is already scheduled
   */
  private void checkNotYetScheduled() {
    if (task != null) {
      throw new IllegalStateException("Already scheduled as " + task.getTaskId());
    }
  }

  @Override
  public synchronized int hashCode() {
    return 31 + ((task == null) ? 0 : task.hashCode());
  }

  @Override
  public synchronized boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SchedulerRunnable other = (SchedulerRunnable) obj;
    if (task == null) {
      return other.task == null;
    }
    return task.equals(other.task);
  }

  @Override
  public synchronized String toString() {
    return "SchedulerRunnable [task=" + task + "]";
  }
}