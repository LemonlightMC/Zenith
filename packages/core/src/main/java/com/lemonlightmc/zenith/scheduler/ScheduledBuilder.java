package com.lemonlightmc.zenith.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lemonlightmc.zenith.additive.interfaces.ParameterizedBuilder;
import com.lemonlightmc.zenith.scheduler.BukkitScheduler.ThreadContext;

/**
 * Builder for configuring and scheduling tasks through a {@link Scheduler} implementation.
 *
 * <p>Use this builder to choose synchronous or asynchronous execution, configure an initial
 * delay and a repeating interval, and then schedule the task with {@link #run(Runnable)} or
 * {@link #build(Runnable)}.</p>
 */
public class ScheduledBuilder implements ParameterizedBuilder<Runnable, Callable<ScheduledTask>> {
  private ThreadContext context = ThreadContext.SYNC;
  private long delay = 0;
  private long interval = 0;
  private final Scheduler scheduler;

  /**
   * Creates a new scheduled task builder backed by the given scheduler.
   *
   * @param scheduler the scheduler used to schedule built tasks
   * @throws IllegalArgumentException if {@code scheduler} is {@code null}
   */
  ScheduledBuilder(final Scheduler scheduler) {
    if (scheduler == null) {
      throw new IllegalArgumentException("No Scheduler for the Builder was provided!");
    }
    this.scheduler = scheduler;
  }

  /**
   * Configures the task to run asynchronously.
   *
   * @return this builder instance
   */
  public ScheduledBuilder async() {
    this.context = ThreadContext.ASYNC;
    return this;
  }

  /**
   * Configures the task to run synchronously.
   *
   * @return this builder instance
   */
  public ScheduledBuilder sync() {
    this.context = ThreadContext.SYNC;
    return this;
  }

  /**
   * Sets the initial delay before the task is first executed.
   *
   * @param ticks the initial delay in ticks
   * @return this builder instance
   */
  public ScheduledBuilder delay(final long ticks) {
    this.delay = ticks;
    return this;
  }

  /**
   * Sets the initial delay before the task is first executed.
   *
   * @param delay the initial delay value
   * @param unit the time unit of the delay value
   * @return this builder instance
   */
  public ScheduledBuilder delay(final long delay, final TimeUnit unit) {
    this.delay = unit.toMillis(delay);
    return this;
  }

  /**
   * Sets the repeating interval between subsequent task executions.
   *
   * @param ticks the repeating interval in ticks
   * @return this builder instance
   */
  public ScheduledBuilder every(final long ticks) {
    this.interval = ticks;
    return this;
  }

  /**
   * Sets the repeating interval between subsequent task executions.
   *
   * @param interval the repeating interval value
   * @param unit the time unit of the interval value
   * @return this builder instance
   */
  public ScheduledBuilder every(final long interval, final TimeUnit unit) {
    this.interval = unit.toMillis(interval);
    return this;
  }

  /**
   * Schedules the provided runnable using the current builder configuration.
   *
   * @param runnable the task to schedule
   * @return the scheduled task, or {@code null} if the scheduler cannot create one
   */
  public ScheduledTask run(final Runnable runnable) {
    if (this.context == ThreadContext.ASYNC) {
      return scheduler.runAsync(runnable, delay, interval);
    } else {
      return scheduler.run(runnable, delay, interval);
    }
  }

  /**
   * Schedules a consumer-based task using the current builder configuration.
   *
   * <p>The scheduled task is passed to the consumer when it executes.</p>
   *
   * @param consumer the consumer to schedule
   */
  public void run(final Consumer<ScheduledTask> consumer) {
    if (this.context == ThreadContext.ASYNC) {
      scheduler.runAsync(consumer, delay, interval);
    } else {
      scheduler.run(consumer, delay, interval);
    }
  }

  /**
   * Builds a callable that schedules the provided runnable when invoked.
   *
   * @param runnable the task to schedule
   * @return a callable that produces the scheduled task
   */
  @Override
  public Callable<ScheduledTask> build(final Runnable runnable) {
    return new Callable<ScheduledTask>() {
      @Override
      public ScheduledTask call() throws Exception {
        if (context == ThreadContext.ASYNC) {
          return scheduler.runAsync(runnable, delay, interval);
        } else {
          return scheduler.run(runnable, delay, interval);
        }
      }
    };
  }

  @Override
  public int hashCode() {
    int result = 31 + context.hashCode();
    result = 31 * result + (int) (delay ^ (delay >>> 32));
    return 31 * result + (int) (interval ^ (interval >>> 32));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ScheduledBuilder other = (ScheduledBuilder) obj;
    return context == other.context && delay == other.delay && interval == other.interval;
  }

  @Override
  public String toString() {
    return "ScheduledBuilder [context=" + context + ", delay=" + delay + ", interval=" + interval + "]";
  }

}
