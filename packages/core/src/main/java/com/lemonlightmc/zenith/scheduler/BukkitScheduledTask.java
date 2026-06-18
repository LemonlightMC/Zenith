package com.lemonlightmc.zenith.scheduler;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.exceptions.SchedulerException;
import com.lemonlightmc.zenith.scheduler.BukkitScheduler.ThreadContext;

public class BukkitScheduledTask implements ScheduledTask {

  protected final BukkitTask backing;
  protected final AtomicInteger counter = new AtomicInteger(0);
  protected final AtomicBoolean cancelled = new AtomicBoolean(false);
  protected final int taskId;
  protected final ThreadContext ctx;
  protected final long delay;
  protected final long interval;

  public BukkitScheduledTask(final BukkitTask backing, final ThreadContext ctx,
      final long delay, final long interval) {
    this.backing = backing;
    if (backing == null) {
      throw new SchedulerException("Failed to create Task from null BukkitTask");
    }
    this.taskId = backing.getTaskId();
    if (this.taskId < 0 && this.taskId != -1) {
      throw new SchedulerException("Failed to create Task from Id " + this.taskId);
    }
    this.ctx = ctx;
    this.delay = delay;
    this.interval = interval;
  }

  /**
   * {@inheritDoc}
   */
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

  @Override
  public boolean isRepeating() {
    return interval > 0;
  }

  @Override
  public long getInterval() {
    return interval;
  }

  @Override
  public int hashCode() {
    int result = 31 + backing.hashCode();
    result = 31 * result + counter.hashCode();
    result = 31 * result + cancelled.hashCode();
    result = 31 * result + ctx.hashCode();
    result = 31 * result + (int) (delay ^ (delay >>> 32));
    result = 31 * result + (int) (interval ^ (interval >>> 32));
    return 31 * result + taskId;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final BukkitScheduledTask other = (BukkitScheduledTask) obj;
    if (backing == null) {
      if (other.backing != null) {
        return false;
      }
    } else if (!backing.equals(other.backing)) {
      return false;
    }
    if (counter == null) {
      if (other.counter != null) {
        return false;
      }
    } else if (!counter.equals(other.counter)) {
      return false;
    }
    if (cancelled == null) {
      if (other.cancelled != null) {
        return false;
      }
    } else if (!cancelled.equals(other.cancelled)) {
      return false;
    }
    return taskId == other.taskId && ctx == other.ctx && delay == other.delay && interval == other.interval;
  }

  @Override
  public String toString() {
    return "ScheduledTask [cancelled=" + cancelled + ", taskId=" + taskId + ", ctx=" + ctx + ", delay=" + delay
        + ", interval=" + interval + "]";
  }
}