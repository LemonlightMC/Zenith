package com.lemonlightmc.zenith.scheduler;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

/**
 * Static access point for the globally configured {@link Scheduler} instance.
 */
public class GlobalScheduler {
  private static Scheduler instance;

  /**
   * Gets the configured global scheduler instance.
   *
   * @return the global scheduler
   * @throws IllegalStateException if no scheduler has been configured
   */
  static Scheduler getInstance() {
    if (instance == null) {
      throw new IllegalStateException("No Scheduler implementation has been set for GlobalScheduler");
    }
    return instance;
  }

  /**
   * Sets the global scheduler instance.
   *
   * @param scheduler the scheduler implementation to use globally
   */
  static void setInstance(final Scheduler scheduler) {
    GlobalScheduler.instance = scheduler;
  }

  /**
   * Creates a new builder for configuring and scheduling tasks.
   *
   * @return a new task builder
   */
  public static ScheduledBuilder builder() {
    return getInstance().builder();
  }

  /**
   * Gets the currently active asynchronous workers.
   *
   * @return the active workers
   */
  public static List<BukkitWorker> getWorkers() {
    return getInstance().getWorkers();
  }

  /**
   * Gets the currently pending scheduled tasks.
   *
   * @return the pending tasks
   */
  public static List<BukkitTask> getPendingTasks() {
    return getInstance().getPendingTasks();
  }

  /**
   * Cancels the tasks with the specified ids.
   *
   * @param taskIds the ids of the tasks to cancel
   */
  public static void cancelTasks(final Collection<Integer> taskIds) {
    getInstance().cancelTasks(taskIds);
  }

  /**
   * Cancels the tasks with the specified ids.
   *
   * @param taskIds the ids of the tasks to cancel
   */
  public static void cancelTasks(final int... taskIds) {
    getInstance().cancelTasks(taskIds);
  }

  /**
   * Cancels all tasks scheduled by the specified plugin.
   *
   * @param plugin the plugin whose tasks should be cancelled
   */
  public static void cancelTasks(final Plugin plugin) {
    getInstance().cancelTasks(plugin);
  }

  /**
   * Cancels all tasks scheduled by this plugin.
   */
  public static void cancelTasks() {
    getInstance().cancelTasks();
  }

  /**
   * Checks whether the specified task is currently running.
   *
   * @param taskId the id of the task to check
   * @return {@code true} if the task is running; {@code false} otherwise
   */
  public static boolean isRunning(final int taskId) {
    return getInstance().isRunning(taskId);
  }

  /**
   * Checks whether the specified task is currently queued.
   *
   * @param taskId the id of the task to check
   * @return {@code true} if the task is queued; {@code false} otherwise
   */
  public static boolean isQueued(final int taskId) {
    return getInstance().isQueued(taskId);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask run(final Runnable task) {
    return getInstance().run(task);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param location the location whose region should execute the task
   * @param runnable the task to execute
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask run(final Location location, final Runnable runnable) {
    return getInstance().run(location, runnable);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param entity the entity whose owning region should execute the task
   * @param runnable the task to execute
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask run(final Entity entity, final Runnable runnable) {
    return getInstance().run(entity, runnable);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask run(final Runnable task, final long delay) {
    return getInstance().run(task, delay);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param delay the initial delay in ticks
   * @param interval the interval in ticks between executions
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask run(final Runnable task, final long delay, final long interval) {
    return getInstance().run(task, delay, interval);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   */
  public static void run(final Consumer<ScheduledTask> consumer) {
    getInstance().run(consumer);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param delay the delay in ticks before execution
   */
  public static void run(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().run(consumer, delay);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param delay the initial delay in ticks
   * @param interval the interval in ticks between executions
   */
  public static void run(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    getInstance().run(consumer, delay, interval);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> run(final Collection<Runnable> tasks) {
    return getInstance().run(tasks);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> run(final Collection<Runnable> tasks, final long delay) {
    return getInstance().run(tasks, delay);
  }

  /**
   * Delegates task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param delay the initial delay in ticks
   * @param interval the interval in ticks between executions
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> run(final Collection<Runnable> tasks, final long delay, final long interval) {
    return getInstance().run(tasks, delay, interval);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runAsync(final Runnable task) {
    return getInstance().runAsync(task);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param location the location whose region should execute the task
   * @param runnable the task to execute
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runAsync(final Location location, final Runnable runnable) {
    return getInstance().runAsync(location, runnable);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param entity the entity whose owning region should execute the task
   * @param runnable the task to execute
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runAsync(final Entity entity, final Runnable runnable) {
    return getInstance().runAsync(entity, runnable);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param delay the initial delay in ticks
   * @param interval the interval in ticks between executions
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runAsync(final Runnable task, final long delay, final long interval) {
    return getInstance().runAsync(task, delay, interval);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runAsync(final Runnable task, final long delay) {
    return getInstance().runAsync(task, delay);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   */
  public static void runAsync(final Consumer<ScheduledTask> consumer) {
    getInstance().runAsync(consumer);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param delay the initial delay in ticks
   * @param interval the interval in ticks between executions
   */
  public static void runAsync(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    getInstance().runAsync(consumer, delay, interval);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param delay the delay in ticks before execution
   */
  public static void runAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().runAsync(consumer, delay);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runAsync(final Collection<Runnable> tasks) {
    return getInstance().runAsync(tasks);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runAsync(final Collection<Runnable> tasks, final long delay) {
    return getInstance().runAsync(tasks, delay);
  }

  /**
   * Delegates asynchronous task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param delay the initial delay in ticks
   * @param interval the interval in ticks between executions
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runAsync(final Collection<Runnable> tasks, final long delay,
      final long interval) {
    return getInstance().runAsync(tasks, delay, interval);
  }

  /**
   * Delegates delayed task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runLater(final Runnable task, final long delay) {
    return getInstance().runLater(task, delay);
  }

  /**
   * Delegates delayed task scheduling to the global scheduler.
   *
   * @param location the location whose region should execute the task
   * @param runnable the task to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runLater(final Location location, final Runnable runnable,
      final long delay) {
    return getInstance().runLater(location, runnable, delay);
  }

  /**
   * Delegates delayed task scheduling to the global scheduler.
   *
   * @param entity the entity whose owning region should execute the task
   * @param runnable the task to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runLater(final Entity entity, final Runnable runnable,
      final long delay) {
    return getInstance().runLater(entity, runnable, delay);
  }

  /**
   * Delegates delayed task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runLater(final Collection<Runnable> tasks, final long delay) {
    return getInstance().runLater(tasks, delay);
  }

  /**
   * Delegates delayed task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param delay the delay in ticks before execution
   */
  public static void runLater(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().runLater(consumer, delay);
  }

  /**
   * Delegates delayed asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runLaterAsync(final Runnable task, final long delay) {
    return getInstance().runLaterAsync(task, delay);
  }

  /**
   * Delegates delayed asynchronous task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param delay the delay in ticks before execution
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runLaterAsync(final Collection<Runnable> tasks, final long delay) {
    return getInstance().runLaterAsync(tasks, delay);
  }

  /**
   * Delegates delayed asynchronous task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param delay the delay in ticks before execution
   */
  public static void runLaterAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().runLaterAsync(consumer, delay);
  }

  /**
   * Delegates repeating task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param interval the interval in ticks between executions
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runEvery(final Runnable task, final long interval) {
    return getInstance().runEvery(task, interval);
  }

  /**
   * Delegates repeating task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runEvery(final Runnable task, final long interval, final long delay) {
    return getInstance().runEvery(task, interval, delay);
  }

  /**
   * Delegates repeating task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param interval the interval in ticks between executions
   */
  public static void runEvery(final Consumer<ScheduledTask> consumer, final long interval) {
    getInstance().runEvery(consumer, interval);
  }

  /**
   * Delegates repeating task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   */
  public static void runEvery(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    getInstance().runEvery(consumer, interval, delay);
  }

  /**
   * Delegates repeating task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param interval the interval in ticks between executions
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runEvery(final Collection<Runnable> tasks, final long interval) {
    return getInstance().runEvery(tasks, interval);
  }

  /**
   * Delegates repeating task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runEvery(final Collection<Runnable> tasks, final long interval,
      final long delay) {
    return getInstance().runEvery(tasks, interval, delay);
  }

  /**
   * Delegates repeating asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param interval the interval in ticks between executions
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runEveryAsync(final Runnable task, final long interval) {
    return getInstance().runEveryAsync(task, interval);
  }

  /**
   * Delegates repeating asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled task, or {@code null} if scheduling failed
   */
  public static ScheduledTask runEveryAsync(final Runnable task, final long interval, final long delay) {
    return getInstance().runEveryAsync(task, interval, delay);
  }

  /**
   * Delegates repeating asynchronous task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param interval the interval in ticks between executions
   */
  public static void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval) {
    getInstance().runEveryAsync(consumer, interval);
  }

  /**
   * Delegates repeating asynchronous task scheduling to the global scheduler.
   *
   * @param consumer the consumer to execute
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   */
  public static void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    getInstance().runEveryAsync(consumer, interval, delay);
  }

  /**
   * Delegates repeating asynchronous task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param interval the interval in ticks between executions
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runEveryAsync(final Collection<Runnable> tasks, final long interval) {
    return getInstance().runEveryAsync(tasks, interval);
  }

  /**
   * Delegates repeating asynchronous task scheduling to the global scheduler.
   *
   * @param tasks the tasks to execute
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled tasks
   */
  public static Collection<ScheduledTask> runEveryAsync(final Collection<Runnable> tasks, final long interval,
      final long delay) {
    return getInstance().runEveryAsync(tasks, interval, delay);
  }

  /**
   * Delegates fixed-repetition task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @return the scheduled task
   */
  public static ScheduledTask repeat(final Runnable task, final int repetitions) {
    return getInstance().repeat(task, repetitions);
  }

  /**
   * Delegates fixed-repetition task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @return the scheduled task
   */
  public static ScheduledTask repeat(final Runnable task, final int repetitions, final long interval) {
    return getInstance().repeat(task, repetitions, interval);
  }

  /**
   * Delegates fixed-repetition task scheduling to the global scheduler.
   *
   * @param runnable the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled task
   */
  public static ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval,
      final long delay) {
    return getInstance().repeat(runnable, repetitions, interval, delay);
  }

  /**
   * Delegates fixed-repetition task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeat(final Runnable task, final int repetitions, final Runnable onComplete) {
    return getInstance().repeat(task, repetitions, onComplete);
  }

  /**
   * Delegates fixed-repetition task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeat(final Runnable task, final int repetitions, final long interval,
      final Runnable onComplete) {
    return getInstance().repeat(task, repetitions, interval, onComplete);
  }

  /**
   * Delegates fixed-repetition task scheduling to the global scheduler.
   *
   * @param runnable the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval,
      final long delay,
      final Runnable onComplete) {
    return getInstance().repeat(runnable, repetitions, interval, delay, onComplete);
  }

  /**
   * Delegates fixed-repetition asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @return the scheduled task
   */
  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions) {
    return getInstance().repeatAsync(task, repetitions);
  }

  /**
   * Delegates fixed-repetition asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @return the scheduled task
   */
  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions, final long interval) {
    return getInstance().repeatAsync(task, repetitions, interval);
  }

  /**
   * Delegates fixed-repetition asynchronous task scheduling to the global scheduler.
   *
   * @param runnable the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled task
   */
  public static ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay) {
    return getInstance().repeatAsync(runnable, repetitions, interval, delay);
  }

  /**
   * Delegates fixed-repetition asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions, final Runnable onComplete) {
    return getInstance().repeatAsync(task, repetitions, onComplete);
  }

  /**
   * Delegates fixed-repetition asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions, final long interval,
      final Runnable onComplete) {
    return getInstance().repeatAsync(task, repetitions, interval, onComplete);
  }

  /**
   * Delegates fixed-repetition asynchronous task scheduling to the global scheduler.
   *
   * @param runnable the task to execute
   * @param repetitions the number of executions
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay,
      final Runnable onComplete) {
    return getInstance().repeatAsync(runnable, repetitions, interval, delay, onComplete);
  }

  /**
   * Delegates conditional repeating task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param predicate the condition controlling repetition
   * @param interval the interval in ticks between executions
   * @return the scheduled task
   */
  public static ScheduledTask repeatWhile(final Runnable task, final Callable<Boolean> predicate, final long interval) {
    return getInstance().repeatWhile(task, predicate, interval);
  }

  /**
   * Delegates conditional repeating task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param predicate the condition controlling repetition
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled task
   */
  public static ScheduledTask repeatWhile(final Runnable task, final Callable<Boolean> predicate, final long interval,
      final long delay) {
    return getInstance().repeatWhile(task, predicate, interval, delay);
  }

  /**
   * Delegates conditional repeating task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param predicate the condition controlling repetition
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeatWhile(final Runnable task, final Callable<Boolean> predicate, final long interval,
      final long delay,
      final Runnable onComplete) {
    return getInstance().repeatWhile(task, predicate, interval, delay, onComplete);
  }

  /**
   * Delegates conditional repeating asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param predicate the condition controlling repetition
   * @param interval the interval in ticks between executions
   * @return the scheduled task
   */
  public static ScheduledTask repeatWhileAsync(final Runnable task, final Callable<Boolean> predicate,
      final long interval) {
    return getInstance().repeatWhileAsync(task, predicate, interval);
  }

  /**
   * Delegates conditional repeating asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param predicate the condition controlling repetition
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @return the scheduled task
   */
  public static ScheduledTask repeatWhileAsync(final Runnable task, final Callable<Boolean> predicate,
      final long interval, final long delay) {
    return getInstance().repeatWhileAsync(task, predicate, interval, delay);
  }

  /**
   * Delegates conditional repeating asynchronous task scheduling to the global scheduler.
   *
   * @param task the task to execute
   * @param predicate the condition controlling repetition
   * @param interval the interval in ticks between executions
   * @param delay the initial delay in ticks
   * @param onComplete the callback to run after completion
   * @return the scheduled task
   */
  public static ScheduledTask repeatWhileAsync(final Runnable task, final Callable<Boolean> predicate,
      final long interval, final long delay,
      final Runnable onComplete) {
    return getInstance().repeatWhileAsync(task, predicate, interval, delay, onComplete);
  }

  /**
   * Checks whether the current thread is the global server thread.
   *
   * @return {@code true} if the current thread is the global server thread; {@code false} otherwise
   */
  public static boolean isGlobalThread() {
    return getInstance().isGlobalThread();
  }

  /**
   * Checks whether the current thread is Bukkit's primary tick thread.
   *
   * @return {@code true} if the current thread is the primary tick thread; {@code false} otherwise
   */
  public static boolean isTickThread() {
    return getInstance().isTickThread();
  }

  /**
   * Checks whether the current thread owns the specified entity.
   *
   * @param entity the entity to check
   * @return {@code true} if the current thread owns the entity; {@code false} otherwise
   */
  public static boolean isEntityThread(final Entity entity) {
    return getInstance().isEntityThread(entity);
  }

  /**
   * Checks whether the current thread owns the region containing the specified location.
   *
   * @param location the location to check
   * @return {@code true} if the current thread owns the location's region; {@code false} otherwise
   */
  public static boolean isRegionThread(final Location location) {
    return getInstance().isRegionThread(location);
  }

  /**
   * Calls a method on the main server thread and returns a future for the result.
   *
   * @param task the callable to execute
   * @param <T> the result type
   * @return a future that completes with the callable result
   */
  public static <T> Future<T> callSyncMethod(final Callable<T> task) {
    return getInstance().callSyncMethod(task);
  }

}
