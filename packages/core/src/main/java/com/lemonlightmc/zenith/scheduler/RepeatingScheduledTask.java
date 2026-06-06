package com.lemonlightmc.zenith.scheduler;

import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.scheduler.Scheduler.ThreadContext;

public class RepeatingScheduledTask extends ScheduledTask {

  private final long interval;

  public RepeatingScheduledTask(final BukkitTask backing, final int taskId, final ThreadContext ctx,
      final long delay, final long interval) {
    super(backing, taskId, ctx, delay);
    this.interval = interval;
  }

  public boolean isRepeating() {
    return interval > 0;
  }

  public long getInterval() {
    return interval;
  }

  @Override
  public int hashCode() {
    return 31 * super.hashCode() + (int) (interval ^ (interval >>> 32));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    final RepeatingScheduledTask other = (RepeatingScheduledTask) obj;
    return interval == other.interval;
  }

  @Override
  public String toString() {
    return "RepeatingScheduledTask [taskId=" + taskId + ", interval=" + interval + ", ctx=" + ctx + ", delay=" + delay
        + "]";
  }
}
