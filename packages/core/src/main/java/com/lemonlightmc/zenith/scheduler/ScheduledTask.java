package com.lemonlightmc.zenith.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.exceptions.SchedulerException;
import com.lemonlightmc.zenith.scheduler.Scheduler.ThreadContext;

public class ScheduledTask implements IScheduled {
  public static final int TASK_NOT_SCHEDULED = -1;
  public static final int TASK_SCHEDULED_SOON = -2;

  protected final BukkitTask backing;
  protected final AtomicInteger counter = new AtomicInteger(0);
  protected final AtomicBoolean cancelled = new AtomicBoolean(false);
  protected final int taskId;
  protected final ThreadContext ctx;
  protected final long delay;

  public ScheduledTask(final BukkitTask backing, final int taskId, final ThreadContext ctx,
      final long delay) {
    if (backing == null || taskId < 0 && taskId != -1) {
      throw new SchedulerException("Failed to create Task from Id " + taskId);
    }
    this.backing = backing;
    this.taskId = taskId;
    this.ctx = ctx;
    this.delay = delay;
  }

  @Override
  public void cancel() {
    if (cancelled.getAndSet(true)) {
      return;
    }
    if (taskId != -1) {
      Bukkit.getScheduler().cancelTask(taskId);
    }
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled.get();
  }

  @Override
  public int getTaskId() {
    return taskId;
  }

  @Override
  public BukkitTask getBacking() {
    return this.backing;
  }

  @Override
  public boolean isRunning() {
    return taskId != -1 && Bukkit.getScheduler().isCurrentlyRunning(taskId);
  }

  @Override
  public boolean isQueued() {
    return taskId != -1 && Bukkit.getScheduler().isQueued(taskId);
  }

  @Override
  public int getTimesRan() {
    return this.counter.get();
  }

  @Override
  public Plugin getOwner() {
    return backing.getOwner();
  }

  @Override
  public ThreadContext getThreadContext() {
    return ctx;
  }

  @Override
  public boolean isSync() {
    return ctx == ThreadContext.SYNC;
  }

  @Override
  public boolean isAsync() {
    return ctx == ThreadContext.ASYNC;
  }

  @Override
  public boolean isDelayed() {
    return delay > 0;
  }

  @Override
  public long getDelay() {
    return delay;
  }

  public boolean softEquals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ScheduledTask other = (ScheduledTask) obj;
    return ctx == other.ctx && delay == other.delay && backing.equals(other.backing);
  }

  @Override
  public int hashCode() {
    int result = 31 + backing.hashCode();
    result = 31 * result + counter.hashCode();
    result = 31 * result + cancelled.hashCode();
    result = 31 * result + taskId;
    result = 31 * result + ctx.hashCode();
    return 31 * result + (int) (delay ^ (delay >>> 32));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ScheduledTask other = (ScheduledTask) obj;
    return taskId == other.taskId && ctx == other.ctx && delay == other.delay && backing.equals(other.backing)
        && counter.equals(other.counter) && cancelled.equals(other.cancelled);
  }

  @Override
  public String toString() {
    return "ScheduledTask [taskId=" + taskId + ", ctx=" + ctx + ", delay=" + delay + "]";
  }
}