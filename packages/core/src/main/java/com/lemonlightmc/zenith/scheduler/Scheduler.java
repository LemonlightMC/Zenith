package com.lemonlightmc.zenith.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

public interface Scheduler {

  static final long DEFAULT_INTERVAL = 1L;
  static final long DEFAULT_DELAY = 0L;

  /**
   * Creates a new {@link ScheduledBuilder} for building and scheduling a task.
   *
   * @return A new {@link ScheduledBuilder}
   */
  ScheduledBuilder builder();

  /**
   * Returns a list of currently executing async workers.
   *
   * @return The currently active workers
   */
  List<BukkitWorker> getWorkers();

  /**
   * Returns a list of currently pending tasks.
   *
   * @return The currently pending tasks
   */
  List<BukkitTask> getPendingTasks();

  /**
   * Attempts to cancel all tasks scheduled by this plugin.
   */
  void cancelTasks();

  /**
   * Attempts to cancel all tasks scheduled by the specified plugin.
   *
   * @param plugin The plugin whose tasks should be cancelled
   */
  void cancelTasks(Plugin plugin);

  /**
   * Attempts to cancel the specified tasks.
   *
   * @param taskIds The ids of the tasks to cancel
   */
  void cancelTasks(final int taskId);

  /**
   * Attempts to cancel the specified tasks.
   *
   * @param taskIds The ids of the tasks to cancel
   */
  default void cancelTasks(final int... taskIds) {
    if (taskIds == null || taskIds.length == 0) {
      return;
    }
    for (final int id : taskIds) {
      cancelTasks(id);
    }
  }

  /**
   * Attempts to cancel the specified tasks.
   *
   * @param taskIds The ids of the tasks to cancel
   */
  default void cancelTasks(final Collection<Integer> taskIds) {
    if (taskIds == null || taskIds.isEmpty()) {
      return;
    }
    for (final int id : taskIds) {
      cancelTasks(id);
    }
  }

  /**
   * Returns whether the specified task is currently running.
   *
   * @param taskId The id of the task to check
   * @return true if the task is currently running, otherwise false
   */
  boolean isRunning(int taskId);

  /**
   * Returns whether the specified task is currently queued.
   *
   * @param taskId The id of the task to check
   * @return true if the task is currently queued, otherwise false
   */
  boolean isQueued(int taskId);

  /**
   * Returns whether the current thread is the global server thread.
   * <p>
   * <b>Folia</b>: checks the global region thread.
   * <br>
   * <b>Paper and Bukkit</b>: delegates to
   * {@link org.bukkit.Server#isPrimaryThread()}.
   */
  boolean isGlobalThread();

  /**
   * Returns whether the current thread is Bukkit's primary tick thread.
   *
   * @return {@link org.bukkit.Server#isPrimaryThread()}
   */
  default boolean isTickThread() {
    return Bukkit.getServer().isPrimaryThread();
  }

  /**
   * Returns whether the current thread owns the specified entity.
   * <p>
   * <b>Folia and Paper</b>: checks whether the current thread is ticking the
   * region that owns the entity. This is the safe ownership check because reading
   * an entity's location can be undefined unless the entity is owned by the
   * current region.
   * <br>
   * <b>Bukkit</b>: delegates to {@link org.bukkit.Server#isPrimaryThread()}.
   *
   * @param entity The entity to check
   * @return true if the current thread owns the entity
   */
  boolean isEntityThread(Entity entity);

  /**
   * Returns whether the current thread owns the region containing the specified
   * location.
   * <p>
   * <b>Folia and Paper</b>: checks whether the current thread is ticking the
   * region that owns the chunk containing the location.
   * <br>
   * <b>Bukkit</b>: delegates to {@link org.bukkit.Server#isPrimaryThread()}.
   *
   * @param location The location to check, must have a non-null world
   * @return true if the current thread owns the location's region
   */
  boolean isRegionThread(Location location);

  // ==================================
  // run
  // ==================================

  /**
   * Schedules a task to be executed on the next tick.
   * <p>
   * <b>Folia and Paper</b>: runs on the global region.
   * <br>
   * <b>Bukkit</b>: runs on the main thread.
   *
   * @param task The task to execute
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask run(Runnable task);

  /**
   * Schedules a task to be executed after the specified delay.
   * <p>
   * <b>Folia and Paper</b>: runs on the global region.
   * <br>
   * <b>Bukkit</b>: runs on the main thread.
   *
   * @param task  The task to execute
   * @param delay The delay, in ticks
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask run(Runnable task, long delay);

  /**
   * Schedules a repeating task to be executed after the initial delay with the
   * specified interval.
   * <p>
   * <b>Folia and Paper</b>: runs on the global region.
   * <br>
   * <b>Bukkit</b>: runs on the main thread.
   *
   * @param task     The task to execute
   * @param delay    The initial delay, in ticks.
   * @param interval The interval between executions, in ticks
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask run(Runnable task, long delay, long interval);

  /**
   * Schedules a task consumer to be executed on the next tick.
   * <p>
   * The consumer receives the {@link ScheduledTask} that represents its own
   * scheduled execution.
   *
   * @param consumer The consumer to execute
   */
  default void run(final Consumer<ScheduledTask> consumer) {
    if (consumer == null) {
      return;
    }
    run(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    });
  }

  /**
   * Schedules a task consumer to be executed after the specified delay.
   *
   * @param consumer The consumer to execute
   * @param delay    The delay, in ticks
   */
  default void run(final Consumer<ScheduledTask> consumer, final long delay) {
    if (consumer == null) {
      return;
    }
    run(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, delay);
  }

  /**
   * Schedules a repeating task consumer to be executed after the initial delay
   * with the specified interval.
   *
   * @param consumer The consumer to execute
   * @param delay    The initial delay, in ticks.
   * @param interval The interval between executions, in ticks
   */
  default void run(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    if (consumer == null) {
      return;
    }
    run(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, delay, interval);
  }

  /**
   * Schedules tasks to be executed on the next tick.
   *
   * @param runnables The tasks to execute
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> run(final Collection<Runnable> runnables) {
    if (runnables == null || runnables.isEmpty()) {
      return List.of();
    }
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(run(runnable));
    }
    return scheduledTasks;
  }

  /**
   * Schedules tasks to be executed after the specified delay.
   *
   * @param runnables The tasks to execute
   * @param delay     The delay, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> run(final Collection<Runnable> runnables, final long delay) {
    if (runnables == null || runnables.isEmpty()) {
      return List.of();
    }
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(run(runnable, delay));
    }
    return scheduledTasks;
  }

  /**
   * Schedules repeating tasks to be executed after the initial delay with the
   * specified interval.
   *
   * @param runnables The tasks to execute
   * @param delay     The initial delay, in ticks.
   * @param interval  The interval between executions, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> run(final Collection<Runnable> runnables, final long delay, final long interval) {
    if (runnables == null || runnables.isEmpty()) {
      return List.of();
    }
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(run(runnable, delay, interval));
    }
    return scheduledTasks;
  }

  /**
   * Schedules a task to be executed on the next tick for the region that owns the
   * location.
   * <p>
   * <b>Folia and Paper</b>: runs on the owning region.
   * <br>
   * <b>Bukkit</b>: same as {@link #run(Runnable)}.
   *
   * @param location The location whose region should execute the task
   * @param runnable The task to execute
   * @return The scheduled task, or null if the task could not be scheduled
   */
  default ScheduledTask run(final Location location, final Runnable runnable) {
    return run(runnable);
  }

  /**
   * Schedules a task to be executed on the next tick for the region that owns the
   * entity.
   * <p>
   * <b>Folia and Paper</b>: runs on the owning region.
   * <br>
   * <b>Bukkit</b>: same as {@link #run(Runnable)}.
   *
   * @param entity   The entity whose owning region should execute the task
   * @param runnable The task to execute
   * @return The scheduled task, or null if the task could not be scheduled
   */
  default ScheduledTask run(final Entity entity, final Runnable runnable) {
    return run(runnable);
  }

  // ==================================
  // runLater
  // ==================================

  /**
   * Schedules a task to be executed after the specified delay.
   * <p>
   * <b>Folia and Paper</b>: runs on the global region.
   * <br>
   * <b>Bukkit</b>: runs on the main thread.
   *
   * @param runnable The task to execute
   * @param delay    The delay, in ticks
   * @return The scheduled task, or null if the task could not be scheduled
   */
  default ScheduledTask runLater(final Runnable runnable, final long delay) {
    return run(runnable, delay);
  }

  /**
   * Schedules a task to be executed after the specified delay for the region that
   * owns the location.
   * <p>
   * <b>Folia and Paper</b>: runs on the owning region.
   * <br>
   * <b>Bukkit</b>: same as {@link #runLater(Runnable, long)}.
   *
   * @param location The location whose region should execute the task
   * @param runnable The task to execute
   * @param delay    The delay, in ticks.
   * @return The scheduled task, or null if the task could not be scheduled
   */
  default ScheduledTask runLater(final Location location, final Runnable runnable, final long delay) {
    return runLater(runnable, delay);
  }

  /**
   * Schedules a task to be executed after the specified delay for the region that
   * owns the entity.
   * <p>
   * <b>Folia and Paper</b>: runs on the owning region.
   * <br>
   * <b>Bukkit</b>: same as {@link #runLater(Runnable, long)}.
   *
   * @param entity   The entity whose owning region should execute the task
   * @param runnable The task to execute
   * @param delay    The delay, in ticks.
   * @return The scheduled task, or null if the task could not be scheduled
   */
  default ScheduledTask runLater(final Entity entity, final Runnable runnable, final long delay) {
    return runLater(runnable, delay);
  }

  /**
   * Schedules a task to be executed after the time delay has passed.
   *
   * @param consumer The consumer to execute
   * @param delay    The delay before execution, in ticks
   */
  default void runLater(final Consumer<ScheduledTask> consumer, final long delay) {
    run(consumer, delay);
  }

  /**
   * Schedules the specified tasks to be executed after the delay has passed.
   *
   * @param runnables The tasks to execute
   * @param delay     The delay before execution, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runLater(final Collection<Runnable> runnables, final long delay) {
    return run(runnables, delay);
  }

  // ==================================
  // runAsync
  // ==================================

  /**
   * Schedules a task to be executed asynchronously
   *
   * @param task The task to execute
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask runAsync(Runnable task);

  /**
   * Schedules a task to be executed asynchronously after the specified delay in
   * ticks
   *
   * @param task  The task to execute
   * @param delay The delay, in ticks
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask runAsync(Runnable task, long delay);

  /**
   * Schedules a repeating task to be executed after the initial delay with the
   * specified period asynchronously
   *
   * @param task     The task to execute
   * @param delay    The initial delay, in ticks.
   * @param interval The period, in ticks.
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask runAsync(Runnable task, long delay, long interval);

  /**
   * Schedules a task to be executed asynchronously
   *
   * @param consumer The consumer that accepts the ScheduledTask to execute
   */
  default void runAsync(final Consumer<ScheduledTask> consumer) {
    if (consumer == null) {
      return;
    }
    runAsync(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    });
  }

  /**
   * Schedules a task to be executed asynchronously after the specified delay in
   * ticks
   *
   * @param consumer The consumer that accepts the ScheduledTask to execute
   * @param delay    The delay, in ticks
   */
  default void runAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    if (consumer == null) {
      return;
    }
    runAsync(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, delay);
  }

  /**
   * Schedules a repeating task to be executed after the initial delay with the
   * specified period asynchronously
   *
   * @param consumer The consumer that accepts the ScheduledTask to execute
   * @param delay    The initial delay, in ticks.
   * @param interval The period, in ticks.
   */
  default void runAsync(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    if (consumer == null) {
      return;
    }
    runAsync(new SchedulerRunnable() {
      @Override
      public void run() {
        consumer.accept(this.task);
      }
    }, delay, interval);
  }

  /**
   * Schedules tasks to be executed asynchronously
   *
   * @param runnables The tasks to execute
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runAsync(final Collection<Runnable> runnables) {
    if (runnables == null || runnables.isEmpty()) {
      return List.of();
    }
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(runAsync(runnable));
    }
    return scheduledTasks;
  }

  /**
   * Schedules tasks to be executed asynchronously after the specified delay in
   * ticks
   *
   * @param runnables The tasks to execute
   * @param delay     The delay, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runAsync(final Collection<Runnable> runnables, final long delay) {
    if (runnables == null || runnables.isEmpty()) {
      return List.of();
    }
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(runAsync(runnable, delay));
    }
    return scheduledTasks;
  }

  /**
   * Schedules a repeating task to be executed after the initial delay with the
   * specified period asynchronously
   *
   * @param runnables The tasks to execute
   * @param delay     The initial delay, in ticks.
   * @param interval  The period, in ticks.
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runAsync(final Collection<Runnable> runnables, final long delay,
      final long interval) {
    if (runnables == null || runnables.isEmpty()) {
      return List.of();
    }
    final List<ScheduledTask> scheduledTasks = new ArrayList<>();
    for (final Runnable runnable : runnables) {
      scheduledTasks.add(runAsync(runnable, delay, interval));
    }
    return scheduledTasks;
  }

  /**
   * Schedules an async task for the region that owns the location.
   * <p>
   * <b>Folia and Paper</b>: uses the region scheduler for the owning region.
   * <br>
   * <b>Bukkit</b>: same as {@link #runAsync(Runnable)}.
   *
   * @param location The location whose region should execute the task
   * @param runnable The task to execute
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask runAsync(final Location location, final Runnable runnable);

  /**
   * Schedules an async task for the region that owns the entity.
   * <p>
   * <b>Folia and Paper</b>: uses the entity scheduler for the owning entity.
   * <br>
   * <b>Bukkit</b>: same as {@link #runAsync(Runnable)}.
   *
   * @param entity   The entity whose owning region should execute the task
   * @param runnable The task to execute
   * @return The scheduled task, or null if the task could not be scheduled
   */
  ScheduledTask runAsync(final Entity entity, final Runnable runnable);

  // ==================================
  // runLaterAsync
  // ==================================

  /**
   * Schedules the specified task to be executed asynchronously after the time
   * delay has passed.
   *
   * @param runnable The task to execute
   * @param delay    The delay before execution, in ticks
   * @return The {@link ScheduledTask} that represents the scheduled task
   */
  default ScheduledTask runLaterAsync(final Runnable runnable, final long delay) {
    return runAsync(runnable, delay);
  }

  /**
   * Schedules the specified task consumer to be executed asynchronously after the
   * delay has passed.
   *
   * @param consumer The consumer to execute
   * @param delay    The delay before execution, in ticks
   */
  default void runLaterAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    runAsync(consumer, delay);
  }

  /**
   * Schedules the specified tasks to be executed asynchronously after the delay
   * has passed.
   *
   * @param runnables The tasks to execute
   * @param delay     The delay before execution, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runLaterAsync(final Collection<Runnable> runnables, final long delay) {
    return runAsync(runnables, delay);
  }

  // ==================================
  // runEvery
  // ==================================

  /**
   * Schedules a repeating task to be executed after the initial delay with the
   * specified interval.
   *
   * @param runnable The task to execute
   * @param interval The interval between executions, in ticks
   * @param delay    The initial delay, in ticks
   * @return The scheduled task, or null if the task could not be scheduled
   */
  default ScheduledTask runEvery(final Runnable runnable, final long interval, final long delay) {
    return run(runnable, delay, interval);
  }

  /**
   * Schedules a repeating task to be executed immediately with the specified
   * interval.
   *
   * @param runnable The task to execute
   * @param interval The interval between executions, in ticks
   * @return The scheduled task, or null if the task could not be scheduled
   */
  default ScheduledTask runEvery(final Runnable runnable, final long interval) {
    return run(runnable, DEFAULT_DELAY, interval);
  }

  /**
   * Schedules a repeating task consumer to be executed immediately with the
   * specified interval.
   *
   * @param consumer The consumer to execute
   * @param interval The interval between executions, in ticks
   */
  default void runEvery(final Consumer<ScheduledTask> consumer, final long interval) {
    run(consumer, DEFAULT_DELAY, interval);
  }

  /**
   * Schedules a repeating task consumer to be executed after the initial delay
   * with the specified interval.
   *
   * @param consumer The consumer to execute
   * @param interval The interval between executions, in ticks
   * @param delay    The initial delay, in ticks
   */
  default void runEvery(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    run(consumer, delay, interval);
  }

  /**
   * Schedules repeating tasks to be executed immediately with the specified
   * interval.
   *
   * @param runnables The tasks to execute
   * @param interval  The interval between executions, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runEvery(final Collection<Runnable> runnables, final long interval) {
    return run(runnables, DEFAULT_DELAY, interval);
  }

  /**
   * Schedules repeating tasks to be executed after the initial delay with the
   * specified interval.
   *
   * @param runnables The tasks to execute
   * @param interval  The interval between executions, in ticks
   * @param delay     The initial delay, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runEvery(final Collection<Runnable> runnables, final long interval,
      final long delay) {
    return run(runnables, delay, interval);
  }

  // ==================================
  // runEveryAsync
  // ==================================

  /**
   * Schedules the specified task to be periodically executed asynchronously with
   * the specified interval.
   *
   * @param runnable The task to execute
   * @param interval The time between task executions after the first execution of
   *                 the task, in ticks
   * @return The {@link ScheduledTask} that represents the scheduled task
   */
  default ScheduledTask runEveryAsync(final Runnable runnable, final long interval) {
    return runAsync(runnable, DEFAULT_DELAY, interval);
  }

  /**
   * Schedules the specified task to be executed asynchronously after the initial
   * delay has passed, and then periodically executed with the specified
   * interval.
   *
   * @param runnable The task to execute
   * @param interval The time between task executions after the first execution of
   *                 the task, in ticks
   * @param delay    The time delay to pass before the first execution of the
   *                 task, in ticks
   * @return The {@link ScheduledTask} that represents the scheduled task
   */
  default ScheduledTask runEveryAsync(final Runnable runnable, final long interval, final long delay) {
    return runAsync(runnable, delay, interval);
  }

  /**
   * Schedules the specified task consumer to be periodically executed
   * asynchronously with the specified interval.
   *
   * @param consumer The consumer to execute
   * @param interval The time between task executions after the first execution of
   *                 the task, in ticks
   */
  default void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval) {
    runAsync(consumer, DEFAULT_DELAY, interval);
  }

  /**
   * Schedules the specified task consumer to be executed asynchronously after the
   * initial delay, and then periodically with the specified interval.
   *
   * @param consumer The consumer to execute
   * @param interval The time between task executions after the first execution of
   *                 the task, in ticks
   * @param delay    The time delay to pass before the first execution of the
   *                 task, in ticks
   */
  default void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    runAsync(consumer, delay, interval);
  }

  /**
   * Schedules the specified tasks to be periodically executed asynchronously with
   * the specified interval.
   *
   * @param runnables The tasks to execute
   * @param interval  The time between task executions after the first execution
   *                  of
   *                  the task, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runEveryAsync(final Collection<Runnable> runnables, final long interval) {
    return runAsync(runnables, DEFAULT_DELAY, interval);
  }

  /**
   * Schedules the specified tasks to be executed asynchronously after the initial
   * delay, and then periodically with the specified interval.
   *
   * @param runnables The tasks to execute
   * @param interval  The time between task executions after the first execution
   *                  of
   *                  the task, in ticks
   * @param delay     The time delay to pass before the first execution of the
   *                  task, in ticks
   * @return The scheduled tasks, or an empty collection if no tasks were provided
   */
  default Collection<ScheduledTask> runEveryAsync(final Collection<Runnable> runnables, final long interval,
      final long delay) {
    return runAsync(runnables, delay, interval);
  }

  // ==================================
  // repeat
  // ==================================

  default ScheduledTask repeat(final Runnable runnable, final int repetitions) {
    return repeat(runnable, repetitions, DEFAULT_INTERVAL, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval) {
    return repeat(runnable, repetitions, interval, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval, final long delay) {
    return repeat(runnable, repetitions, interval, delay, null);
  }

  default ScheduledTask repeat(final Runnable runnable, final int repetitions, final Runnable onComplete) {
    return repeat(runnable, repetitions, DEFAULT_INTERVAL, DEFAULT_DELAY, onComplete);
  }

  default ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval,
      final Runnable onComplete) {
    return repeat(runnable, repetitions, DEFAULT_INTERVAL, DEFAULT_DELAY, onComplete);
  }

  default ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval, final long delay,
      final Runnable onComplete) {
    final long repetitions0 = Math.round(Math.min(Math.max(repetitions, 0), Integer.MAX_VALUE));
    return run(new SchedulerRunnable() {
      private int index = 0;

      @Override
      public void run() {
        if (++this.index >= repetitions0) {
          this.cancel();
          if (onComplete == null) {
            return;
          }
          onComplete.run();
          return;
        }
        runnable.run();
      }
    }, delay, interval);
  }

  // ==================================
  // repeatAsync
  // ==================================

  default ScheduledTask repeatAsync(final Runnable runnable, final int repetitions) {
    return repeatAsync(runnable, repetitions, DEFAULT_INTERVAL, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval) {
    return repeatAsync(runnable, repetitions, interval, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay) {
    return repeatAsync(runnable, repetitions, interval, delay, null);
  }

  default ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final Runnable onComplete) {
    return repeatAsync(runnable, repetitions, DEFAULT_INTERVAL, DEFAULT_DELAY, onComplete);
  }

  default ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final Runnable onComplete) {
    return repeatAsync(runnable, repetitions, interval, DEFAULT_DELAY, onComplete);
  }

  default ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay,
      final Runnable onComplete) {
    final long repetitions0 = Math.round(Math.min(Math.max(repetitions, 0), Integer.MAX_VALUE));
    return runAsync(new SchedulerRunnable() {
      private int index = 0;

      @Override
      public void run() {
        if (++this.index >= repetitions0) {
          this.cancel();
          if (onComplete == null) {
            return;
          }
          onComplete.run();
          return;
        }
        runnable.run();
      }
    }, delay, interval);
  }

  // ==================================
  // repeatWhile
  // ==================================

  default ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate) {
    return repeatWhile(runnable, predicate, DEFAULT_INTERVAL, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate,
      final long interval) {
    return repeatWhile(runnable, predicate, interval, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate,
      final long interval, final long delay) {
    return repeatWhile(runnable, predicate, interval, delay, null);
  }

  default ScheduledTask repeatWhile(final Runnable runnable, final Callable<Boolean> predicate, final long interval,
      final long delay,
      final Runnable onComplete) {
    return run(new SchedulerRunnable() {
      @Override
      public void run() {
        try {
          if (!predicate.call()) {
            this.cancel();
            if (onComplete == null) {
              return;
            }

            onComplete.run();
            return;
          }

          runnable.run();
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }, delay, interval);
  }

  // ==================================
  // repeatWhileAsync
  // ==================================

  default ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate) {
    return repeatWhile(runnable, predicate, DEFAULT_INTERVAL, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate,
      final long interval) {
    return repeatWhile(runnable, predicate, interval, DEFAULT_DELAY, null);
  }

  default ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate,
      final long interval, final long delay) {
    return repeatWhile(runnable, predicate, interval, delay, null);
  }

  default ScheduledTask repeatWhileAsync(final Runnable runnable, final Callable<Boolean> predicate,
      final long interval, final long delay, final Runnable onComplete) {
    return runAsync(new SchedulerRunnable() {
      @Override
      public void run() {
        try {
          if (!predicate.call()) {
            this.cancel();
            if (onComplete == null) {
              return;
            }

            onComplete.run();
            return;
          }

          runnable.run();
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }, delay, interval);
  }
  // ==================================
  // callMethod
  // ==================================

  /**
   * Calls a method on the main server thread and returns a future for the result.
   * The task is executed by the Bukkit main thread or the Folia/Paper global
   * region thread.
   * <p>
   * The {@link Future#get()} methods must not be called from the main server
   * thread.
   * <p>
   * There may be scheduler latency before {@link Future#isDone()} returns true.
   *
   * @param <T>  The result type
   * @param task The callable to execute
   * @return A future that completes with the callable result
   */
  default <T> Future<T> callSyncMethod(final Callable<T> task) {
    final CompletableFuture<T> completableFuture = new CompletableFuture<>();
    run(() -> {
      try {
        completableFuture.complete(task.call());
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    });
    return completableFuture;
  }

}
