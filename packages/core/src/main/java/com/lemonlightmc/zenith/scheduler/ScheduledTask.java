package com.lemonlightmc.zenith.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.scheduler.BukkitScheduler.ThreadContext;

public interface ScheduledTask {

  /**
   * Returns the Bukkit task ID of this task, or -1 if the task is not currently
   * scheduled.
   *
   * @return the Bukkit task ID of this task, or -1 if the task is not currently
   *         scheduled
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public int getTaskId();

  /**
   * Returns the owning plugin of this task.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public Plugin getOwner();

  /**
   * Returns the initial delay of this task, or 0 if the task is not currently
   * scheduled or has no initial delay.
   * 
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public long getDelay();

  /**
   * Returns the repeating interval of this task, or -1 if the task is not
   * repeating.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public long getInterval();

  /**
   * Returns the underlying BukkitTask backing this task.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public BukkitTask getBacking();

  /**
   * Returns the thread context of this task.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public ThreadContext getThreadContext();

  /**
   * Returns the number of times this task has been executed. For repeating tasks
   * this will be incremented each time the task is executed, for non-repeating
   * tasks this will be either 0 (not yet executed) or 1 (already executed).
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public int getTimesRan();

  /**
   * Returns true if this task is currently running.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public boolean isCancelled();

  /**
   * Cancels this task if it is currently scheduled. If the task is not currently
   * scheduled, this method does nothing.
   */
  public void cancel();

  /**
   * Returns true if this task is currently scheduled and has not yet been
   * cancelled.
   */
  public boolean isRunning();

  /**
   * Returns true if this task is currently scheduled and is waiting to be
   * executed
   * (i.e. it has not yet started executing).
   */
  public boolean isQueued();

  /**
   * Returns true if this task is currently scheduled and is executing on the main
   * server thread.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public boolean isSync();

  /**
   * Returns true if this task is currently scheduled and is executing
   * asynchronously
   * off the main server thread.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public boolean isAsync();

  /**
   * Returns true if this task is currently scheduled and has a non-zero initial
   * delay.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public boolean isDelayed();

  /**
   * Returns true if this task is currently scheduled and has a repeating
   * interval.
   *
   * @throws IllegalStateException if the task is not currently scheduled
   */
  public boolean isRepeating();

}