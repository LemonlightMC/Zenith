package com.lemonlightmc.zenith.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lemonlightmc.zenith.scheduler.ScheduledBuilder;
import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.scheduler.Scheduler.ThreadContext;

public class ScheduledBuilder {
  private ThreadContext context = ThreadContext.SYNC;
  private long delay = 0;
  private long interval = 0;

  public ScheduledBuilder() {
  }

  public static ScheduledBuilder create() {
    return new ScheduledBuilder();
  }

  public ScheduledBuilder async() {
    this.context = ThreadContext.ASYNC;
    return this;
  }

  public ScheduledBuilder sync() {
    this.context = ThreadContext.SYNC;
    return this;
  }

  public ScheduledBuilder delay(final long delay) {
    this.delay = delay;
    return this;
  }

  public ScheduledBuilder delay(final long delay, final TimeUnit unit) {
    this.delay = unit.toMillis(delay);
    return this;
  }

  public ScheduledBuilder delayTicks(final long ticks) {
    this.delay = ticks;
    return this;
  }

  public ScheduledBuilder every(final long interval) {
    this.delay = interval;
    return this;
  }

  public ScheduledBuilder every(final long interval, final TimeUnit unit) {
    this.interval = unit.toMillis(interval);
    return this;
  }

  public ScheduledBuilder everyTicks(final long ticks) {
    this.interval = ticks;
    return this;
  }

  public ScheduledTask run(final Runnable runnable) {
    if (this.context == ThreadContext.ASYNC) {
      return ZenithPlugin.getInstance().getScheduler().runAsync(runnable, delay, interval);
    } else {
      return ZenithPlugin.getInstance().getScheduler().run(runnable, delay, interval);
    }
  }

  public void run(final Consumer<ScheduledTask> consumer) {
    if (this.context == ThreadContext.ASYNC) {
      ZenithPlugin.getInstance().getScheduler().runAsync(consumer, delay, interval);
    } else {
      ZenithPlugin.getInstance().getScheduler().run(consumer, delay, interval);
    }
  }

  @Override
  public int hashCode() {
    int result = 31 + context.hashCode();
    result = 31 * result + (int) (delay ^ (delay >>> 32));
    return 31 * result + (int) (interval ^ (interval >>> 32));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ScheduledBuilder other = (ScheduledBuilder) obj;
    return context == other.context && delay == other.delay && interval == other.interval;
  }

  @Override
  public String toString() {
    return "ScheduledBuilder [context=" + context + ", delay=" + delay + ", interval=" + interval + "]";
  }
}
