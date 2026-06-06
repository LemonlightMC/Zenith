package com.lemonlightmc.zenith.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.scheduler.Scheduler.ThreadContext;

public abstract class SchedulerRunnable implements Runnable {

  protected ScheduledTask task;

  public synchronized boolean isCancelled() {
    return task != null && task.isCancelled();
  }

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
    return task == null ? -1 : task.getTaskId();
  }

  public synchronized Plugin getOwner() throws IllegalStateException {
    return task == null ? null : task.getOwner();
  }

  public synchronized ThreadContext getThreadContext() throws IllegalStateException {
    return task == null ? null : task.getThreadContext();
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
    return task == null ? 0 : task.getDelay();
  }

  public synchronized boolean isRepeating() throws IllegalStateException {
    checkScheduled();
    if (task instanceof final RepeatingScheduledTask repeatingTask) {
      return repeatingTask.isRepeating();
    }
    return false;
  }

  public synchronized long getInterval() {
    if (task != null && task instanceof final RepeatingScheduledTask repeatingTask) {
      return repeatingTask.getInterval();
    }
    return -1;
  }

  public synchronized ScheduledTask runTask() throws IllegalStateException {
    checkNotYetScheduled();
    return setupTask(
        Bukkit.getScheduler().runTask(ZenithPlugin.getInstance(), this), ThreadContext.SYNC, 0, -1);
  }

  public synchronized ScheduledTask runTaskAsync() throws IllegalStateException {
    checkNotYetScheduled();
    return setupTask(
        Bukkit.getScheduler().runTaskAsynchronously(ZenithPlugin.getInstance(), this), ThreadContext.ASYNC, 0, -1);
  }

  public synchronized ScheduledTask runTaskLater(final long delay) throws IllegalStateException {
    checkNotYetScheduled();
    return setupTask(
        Bukkit.getScheduler().runTaskLater(ZenithPlugin.getInstance(), this, delay), ThreadContext.SYNC, delay, -1);
  }

  public synchronized ScheduledTask runTaskLaterAsync(final long delay)
      throws IllegalStateException {
    checkNotYetScheduled();
    return setupTask(
        Bukkit.getScheduler().runTaskLaterAsynchronously(ZenithPlugin.getInstance(), this, delay),
        ThreadContext.ASYNC, delay, -1);
  }

  public synchronized ScheduledTask runTaskRepeating(final long delay, final long interval)
      throws IllegalStateException {
    checkNotYetScheduled();
    return setupTask(
        Bukkit.getScheduler().runTaskTimer(ZenithPlugin.getInstance(), this, delay, interval),
        ThreadContext.SYNC, delay, interval);
  }

  public synchronized ScheduledTask runTaskRepeatingAsync(final long delay, final long interval)
      throws IllegalStateException {
    checkNotYetScheduled();
    return setupTask(
        Bukkit.getScheduler().runTaskTimerAsynchronously(ZenithPlugin.getInstance(), this, delay,
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

  private ScheduledTask setupTask(final BukkitTask task, final ThreadContext ctx, final long delay,
      final long interval) {
    if (interval > 0) {
      this.task = new RepeatingScheduledTask(task, task.getTaskId(), ctx, delay, interval);
    } else {
      this.task = new ScheduledTask(task, task.getTaskId(), ctx, delay);
    }
    return this.task;
  }

  @Override
  public int hashCode() {
    return 31 + ((task == null) ? 0 : task.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    SchedulerRunnable other = (SchedulerRunnable) obj;
    if (task == null && other.task != null) {
      return false;
    }
    return task.equals(other.task);
  }

  @Override
  public String toString() {
    return "SchedulerRunnable [task=" + task + "]";
  }
}