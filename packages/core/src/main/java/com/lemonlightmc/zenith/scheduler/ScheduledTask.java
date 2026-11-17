package com.lemonlightmc.zenith.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.scheduler.BukkitScheduler.ThreadContext;

/**
 * Represents a task scheduled through the Zenith scheduler abstraction.
 */
public interface ScheduledTask {

  /**
   * Gets the Bukkit task id for this task.
   *
   * @return the Bukkit task id, or {@code -1} if the task is not currently scheduled
   */
  int getTaskId();

  /**
   * Gets the plugin that owns this task.
   *
   * @return the owning plugin
   */
  Plugin getOwner();

  /**
   * Gets the initial delay configured for this task.
   *
   * @return the delay in ticks, or {@code 0} if no delay was configured
   */
  long getDelay();

  /**
   * Gets the repeating interval configured for this task.
   *
   * @return the interval in ticks, or {@code -1} if the task is not repeating
   */
  long getInterval();

  /**
   * Gets the underlying Bukkit task backing this scheduled task.
   *
   * @return the backing Bukkit task
   */
  BukkitTask getBacking();

  /**
   * Gets the thread context in which this task executes.
   *
   * @return the thread context
   */
  ThreadContext getThreadContext();

  /**
   * Gets the number of times this task has been executed.
   *
   * @return the execution count
   */
  int getTimesRan();

  /**
   * Checks whether this task has been cancelled.
   *
   * @return {@code true} if the task is cancelled; {@code false} otherwise
   */
  boolean isCancelled();

  /**
   * Cancels this task if it is currently scheduled.
   */
  void cancel();

  /**
   * Checks whether this task is currently running.
   *
   * @return {@code true} if the task is running; {@code false} otherwise
   */
  boolean isRunning();

  /**
   * Checks whether this task is currently queued for execution.
   *
   * @return {@code true} if the task is queued; {@code false} otherwise
   */
  boolean isQueued();

  /**
   * Checks whether this task executes on the main server thread.
   *
   * @return {@code true} if the task is synchronous; {@code false} otherwise
   */
  boolean isSync();

  /**
   * Checks whether this task executes asynchronously off the main server thread.
   *
   * @return {@code true} if the task is asynchronous; {@code false} otherwise
   */
  boolean isAsync();

  /**
   * Checks whether this task was scheduled with an initial delay.
   *
   * @return {@code true} if the task has a non-zero delay; {@code false} otherwise
   */
  boolean isDelayed();

  /**
   * Checks whether this task is scheduled to repeat.
   *
   * @return {@code true} if the task repeats; {@code false} otherwise
   */
  boolean isRepeating();

}