package com.lemonlightmc.zenith.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.scheduler.BukkitScheduler.ThreadContext;

public abstract class SchedulerRunnable implements Runnable {

  protected ScheduledTask task;

  /**
   * Returns true if this task has been cancelled, false otherwise. If the task is
   * not currently scheduled, this will return false.
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

  public synchronized ScheduledTask getTask() {
    return task;
  }

  public synchronized int getTaskId() {
    checkScheduled();
    return task == null ? -1 : task.getTaskId();
  }

  public synchronized Plugin getOwner() throws IllegalStateException {
    checkScheduled();
    return task == null ? null : task.getOwner();
  }

  public synchronized ThreadContext getThreadContext() throws IllegalStateException {
    checkScheduled();
    return task.getThreadContext();
  }

  public synchronized boolean isRunning() throws IllegalStateException {
    checkScheduled();
    return task.isRunning();
  }

  public synchronized boolean isQueued() throws IllegalStateException {
    checkScheduled();
    return task.isQueued();
  }

  public synchronized boolean isSync() throws IllegalStateException {
    checkScheduled();
    return task.isSync();
  }

  public synchronized boolean isAsync() throws IllegalStateException {
    checkScheduled();
    return task.isAsync();
  }

  public synchronized boolean isDelayed() throws IllegalStateException {
    checkScheduled();
    return task.isDelayed();
  }

  public synchronized long getDelay() {
    return task.getDelay();
  }

  public synchronized boolean isRepeating() throws IllegalStateException {
    checkScheduled();
    return task.isRepeating();
  }

  public synchronized long getInterval() {
    return task.getInterval();
  }

  public synchronized ScheduledTask runTask() throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTask(ZenithProvider.instance(), this), ThreadContext.SYNC, 0, -1);
  }

  public synchronized ScheduledTask runTaskAsync() throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskAsynchronously(ZenithProvider.instance(), this), ThreadContext.ASYNC, 0, -1);
  }

  public synchronized ScheduledTask runTaskLater(final long delay) throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskLater(ZenithProvider.instance(), this, delay), ThreadContext.SYNC, delay, -1);
  }

  public synchronized ScheduledTask runTaskLaterAsync(final long delay)
      throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskLaterAsynchronously(ZenithProvider.instance(), this, delay),
        ThreadContext.ASYNC, delay, -1);
  }

  public synchronized ScheduledTask runTaskRepeating(final long delay, final long interval)
      throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimer(ZenithProvider.instance(), this, delay, interval),
        ThreadContext.SYNC, delay, interval);
  }

  public synchronized ScheduledTask runTaskRepeatingAsync(final long delay, final long interval)
      throws IllegalStateException {
    checkNotYetScheduled();
    return new BukkitScheduledTask(
        Bukkit.getScheduler().runTaskTimerAsynchronously(ZenithProvider.instance(), this, delay,
            interval),
        ThreadContext.ASYNC, delay, interval);
  }

  private void checkScheduled() {
    if (task == null) {
      throw new IllegalStateException("Not scheduled yet");
    }
  }

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