package com.lemonlightmc.zenith.scheduler;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.lemonlightmc.zenith.interfaces.ParameterizedBuilder;
import com.lemonlightmc.zenith.scheduler.BukkitScheduler.ThreadContext;

/**
 * Builder utility for constructing and scheduling tasks with the
 * {@link Scheduler} implementations.
 *
 * <p>
 * Use the builder to select synchronous or asynchronous execution,
 * configure an initial delay and a repeating interval, and then call
 * {@link #run(Runnable)} or {@link #build(Runnable)} to schedule the task.
 */
public class ScheduledBuilder implements ParameterizedBuilder<Runnable, Callable<ScheduledTask>> {
  private ThreadContext context = ThreadContext.SYNC;
  private long delay = 0;
  private long interval = 0;
  private final Scheduler scheduler;

  ScheduledBuilder(final Scheduler scheduler) {
    if (scheduler == null) {
      throw new IllegalArgumentException("No Scheduler for the Builder was provided!");
    }
    this.scheduler = scheduler;
  }

  /**
   * Sets the execution context of the task to asynchronous. By default, tasks are
   * scheduled to run synchronously.
   */
  public ScheduledBuilder async() {
    this.context = ThreadContext.ASYNC;
    return this;
  }

  /**
   * Sets the execution context of the task to synchronous. By default, tasks are
   * scheduled to run synchronously.
   */
  public ScheduledBuilder sync() {
    this.context = ThreadContext.SYNC;
    return this;
  }

  /**
   * Sets the initial delay before the task is first executed. By default, there
   * is no initial delay.
   * 
   * @param ticks the initial delay before the task is first executed
   * @return this builder instance for chaining
   */
  public ScheduledBuilder delay(final long ticks) {
    this.delay = ticks;
    return this;
  }

  /**
   * Sets the initial delay before the task is first executed. By default, there
   * is no initial delay.
   * 
   * @param delay the initial delay before the task is first executed
   * 
   * @param unit  the time unit of the delay parameter
   * @return this builder instance for chaining
   */
  public ScheduledBuilder delay(final long delay, final TimeUnit unit) {
    this.delay = unit.toMillis(delay);
    return this;
  }

  /**
   * Sets the repeating interval between subsequent executions of the task. By
   * default, tasks are not repeating.
   * 
   * @param ticks the repeating interval between subsequent executions of the task
   * @return this builder instance for chaining
   */
  public ScheduledBuilder every(final long ticks) {
    this.interval = ticks;
    return this;
  }

  /**
   * Sets the repeating interval between subsequent executions of the task. By
   * default, tasks are not repeating.
   * 
   * @param interval the repeating interval between subsequent executions of the
   *                 task
   * @param unit     the time unit of the interval parameter
   * @return this builder instance for chaining
   */
  public ScheduledBuilder every(final long interval, final TimeUnit unit) {
    this.interval = unit.toMillis(interval);
    return this;
  }

  /**
   * Runs the task with the provided Runnable. The returned ScheduledTask can be
   * used to query the task's metadata and cancel it if necessary.
   * 
   * @param runnable the Runnable representing the task to be scheduled
   * @return a ScheduledTask representing the scheduled task, or null if the
   *         provided Runnable was null
   */
  public ScheduledTask run(final Runnable runnable) {
    if (this.context == ThreadContext.ASYNC) {
      return scheduler.runAsync(runnable, delay, interval);
    } else {
      return scheduler.run(runnable, delay, interval);
    }
  }

  /**
   * Runs the task with the provided consumer, which accepts the scheduled task as
   * a parameter. This allows the consumer to query the task's metadata and
   * cancel it if necessary.
   * 
   * @param consumer the Consumer representing the task to be scheduled, which
   *                 accepts the scheduled task as a parameter
   * @return a ScheduledTask representing the scheduled task, or null if the
   *         provided Consumer was null
   */
  public void run(final Consumer<ScheduledTask> consumer) {
    if (this.context == ThreadContext.ASYNC) {
      scheduler.runAsync(consumer, delay, interval);
    } else {
      scheduler.run(consumer, delay, interval);
    }
  }

  /**
   * Builds a Callable that can be used to schedule the task with the provided
   * Runnable. The Callable will return the ScheduledTask instance representing
   * the scheduled task when called.
   * 
   * @param runnable the Runnable representing the task to be scheduled
   * @return a Callable that can be used to schedule the task, or null if the
   *         provided Runnable was null
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
