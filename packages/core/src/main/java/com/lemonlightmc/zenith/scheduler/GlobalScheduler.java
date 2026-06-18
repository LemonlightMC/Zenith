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

public class GlobalScheduler {
  private static Scheduler instance;

  static Scheduler getInstance() {
    if (instance == null) {
      throw new IllegalStateException("No Scheduler implementation has been set for GlobalScheduler");
    }
    return instance;
  }

  static void setInstance(final Scheduler scheduler) {
    GlobalScheduler.instance = scheduler;
  }

  public static ScheduledBuilder builder() {
    return getInstance().builder();
  }

  public static List<BukkitWorker> getWorkers() {
    return getInstance().getWorkers();
  }

  public static List<BukkitTask> getPendingTasks() {
    return getInstance().getPendingTasks();
  }

  public static void cancelTasks(final Collection<Integer> taskIds) {
    getInstance().cancelTasks(taskIds);
  }

  public static void cancelTasks(final int... taskIds) {
    getInstance().cancelTasks(taskIds);
  }

  public static void cancelTasks(final Plugin plugin) {
    getInstance().cancelTasks(plugin);
  }

  public static void cancelTasks() {
    getInstance().cancelTasks();
  }

  public static boolean isRunning(final int taskId) {
    return getInstance().isRunning(taskId);
  }

  public static boolean isQueued(final int taskId) {
    return getInstance().isQueued(taskId);
  }

  public static ScheduledTask run(final Runnable task) {
    return getInstance().run(task);
  }

  public static ScheduledTask run(final Location location, final Runnable runnable) {
    return getInstance().run(location, runnable);
  }

  public static ScheduledTask run(final Entity entity, final Runnable runnable) {
    return getInstance().run(entity, runnable);
  }

  public static ScheduledTask run(final Runnable task, final long delay) {
    return getInstance().run(task, delay);
  }

  public static ScheduledTask run(final Runnable task, final long delay, final long interval) {
    return getInstance().run(task, delay, interval);
  }

  public static void run(final Consumer<ScheduledTask> consumer) {
    getInstance().run(consumer);
  }

  public static void run(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().run(consumer, delay);
  }

  public static void run(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    getInstance().run(consumer, delay, interval);
  }

  public static Collection<ScheduledTask> run(final Collection<Runnable> tasks) {
    return getInstance().run(tasks);
  }

  public static Collection<ScheduledTask> run(final Collection<Runnable> tasks, final long delay) {
    return getInstance().run(tasks, delay);
  }

  public static Collection<ScheduledTask> run(final Collection<Runnable> tasks, final long delay, final long interval) {
    return getInstance().run(tasks, delay, interval);
  }

  public static ScheduledTask runAsync(final Runnable task) {
    return getInstance().runAsync(task);
  }

  public static ScheduledTask runAsync(final Location location, final Runnable runnable) {
    return getInstance().runAsync(location, runnable);
  }

  public static ScheduledTask runAsync(final Entity entity, final Runnable runnable) {
    return getInstance().runAsync(entity, runnable);
  }

  public static ScheduledTask runAsync(final Runnable task, final long delay, final long interval) {
    return getInstance().runAsync(task, delay, interval);
  }

  public static ScheduledTask runAsync(final Runnable task, final long delay) {
    return getInstance().runAsync(task, delay);
  }

  public static void runAsync(final Consumer<ScheduledTask> consumer) {
    getInstance().runAsync(consumer);
  }

  public static void runAsync(final Consumer<ScheduledTask> consumer, final long delay, final long interval) {
    getInstance().runAsync(consumer, delay, interval);
  }

  public static void runAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().runAsync(consumer, delay);
  }

  public static Collection<ScheduledTask> runAsync(final Collection<Runnable> tasks) {
    return getInstance().runAsync(tasks);
  }

  public static Collection<ScheduledTask> runAsync(final Collection<Runnable> tasks, final long delay) {
    return getInstance().runAsync(tasks, delay);
  }

  public static Collection<ScheduledTask> runAsync(final Collection<Runnable> tasks, final long delay,
      final long interval) {
    return getInstance().runAsync(tasks, delay, interval);
  }

  public static ScheduledTask runLater(final Runnable task, final long delay) {
    return getInstance().runLater(task, delay);
  }

  public static ScheduledTask runLater(final Location location, final Runnable runnable,
      final long delay) {
    return getInstance().runLater(location, runnable, delay);
  }

  public static ScheduledTask runLater(final Entity entity, final Runnable runnable,
      final long delay) {
    return getInstance().runLater(entity, runnable, delay);
  }

  public static Collection<ScheduledTask> runLater(final Collection<Runnable> tasks, final long delay) {
    return getInstance().runLater(tasks, delay);
  }

  public static void runLater(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().runLater(consumer, delay);
  }

  public static ScheduledTask runLaterAsync(final Runnable task, final long delay) {
    return getInstance().runLaterAsync(task, delay);
  }

  public static Collection<ScheduledTask> runLaterAsync(final Collection<Runnable> tasks, final long delay) {
    return getInstance().runLaterAsync(tasks, delay);
  }

  public static void runLaterAsync(final Consumer<ScheduledTask> consumer, final long delay) {
    getInstance().runLaterAsync(consumer, delay);
  }

  public static ScheduledTask runEvery(final Runnable task, final long interval) {
    return getInstance().runEvery(task, interval);
  }

  public static ScheduledTask runEvery(final Runnable task, final long interval, final long delay) {
    return getInstance().runEvery(task, interval, delay);
  }

  public static void runEvery(final Consumer<ScheduledTask> consumer, final long interval) {
    getInstance().runEvery(consumer, interval);
  }

  public static void runEvery(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    getInstance().runEvery(consumer, interval, delay);
  }

  public static Collection<ScheduledTask> runEvery(final Collection<Runnable> tasks, final long interval) {
    return getInstance().runEvery(tasks, interval);
  }

  public static Collection<ScheduledTask> runEvery(final Collection<Runnable> tasks, final long interval,
      final long delay) {
    return getInstance().runEvery(tasks, interval, delay);
  }

  public static ScheduledTask runEveryAsync(final Runnable task, final long interval) {
    return getInstance().runEveryAsync(task, interval);
  }

  public static ScheduledTask runEveryAsync(final Runnable task, final long interval, final long delay) {
    return getInstance().runEveryAsync(task, interval, delay);
  }

  public static void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval) {
    getInstance().runEveryAsync(consumer, interval);
  }

  public static void runEveryAsync(final Consumer<ScheduledTask> consumer, final long interval, final long delay) {
    getInstance().runEveryAsync(consumer, interval, delay);
  }

  public static Collection<ScheduledTask> runEveryAsync(final Collection<Runnable> tasks, final long interval) {
    return getInstance().runEveryAsync(tasks, interval);
  }

  public static Collection<ScheduledTask> runEveryAsync(final Collection<Runnable> tasks, final long interval,
      final long delay) {
    return getInstance().runEveryAsync(tasks, interval, delay);
  }

  public static ScheduledTask repeat(final Runnable task, final int repetitions) {
    return getInstance().repeat(task, repetitions);
  }

  public static ScheduledTask repeat(final Runnable task, final int repetitions, final long interval) {
    return getInstance().repeat(task, repetitions, interval);
  }

  public static ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval,
      final long delay) {
    return getInstance().repeat(runnable, repetitions, interval, delay);
  }

  public static ScheduledTask repeat(final Runnable task, final int repetitions, final Runnable onComplete) {
    return getInstance().repeat(task, repetitions, onComplete);
  }

  public static ScheduledTask repeat(final Runnable task, final int repetitions, final long interval,
      final Runnable onComplete) {
    return getInstance().repeat(task, repetitions, interval, onComplete);
  }

  public static ScheduledTask repeat(final Runnable runnable, final int repetitions, final long interval,
      final long delay,
      final Runnable onComplete) {
    return getInstance().repeat(runnable, repetitions, interval, delay, onComplete);
  }

  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions) {
    return getInstance().repeatAsync(task, repetitions);
  }

  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions, final long interval) {
    return getInstance().repeatAsync(task, repetitions, interval);
  }

  public static ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay) {
    return getInstance().repeatAsync(runnable, repetitions, interval, delay);
  }

  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions, final Runnable onComplete) {
    return getInstance().repeatAsync(task, repetitions, onComplete);
  }

  public static ScheduledTask repeatAsync(final Runnable task, final int repetitions, final long interval,
      final Runnable onComplete) {
    return getInstance().repeatAsync(task, repetitions, interval, onComplete);
  }

  public static ScheduledTask repeatAsync(final Runnable runnable, final int repetitions, final long interval,
      final long delay,
      final Runnable onComplete) {
    return getInstance().repeatAsync(runnable, repetitions, interval, delay, onComplete);
  }

  public static ScheduledTask repeatWhile(final Runnable task, final Callable<Boolean> predicate, final long interval) {
    return getInstance().repeatWhile(task, predicate, interval);
  }

  public static ScheduledTask repeatWhile(final Runnable task, final Callable<Boolean> predicate, final long interval,
      final long delay) {
    return getInstance().repeatWhile(task, predicate, interval, delay);
  }

  public static ScheduledTask repeatWhile(final Runnable task, final Callable<Boolean> predicate, final long interval,
      final long delay,
      final Runnable onComplete) {
    return getInstance().repeatWhile(task, predicate, interval, delay, onComplete);
  }

  public static ScheduledTask repeatWhileAsync(final Runnable task, final Callable<Boolean> predicate,
      final long interval) {
    return getInstance().repeatWhileAsync(task, predicate, interval);
  }

  public static ScheduledTask repeatWhileAsync(final Runnable task, final Callable<Boolean> predicate,
      final long interval, final long delay) {
    return getInstance().repeatWhileAsync(task, predicate, interval, delay);
  }

  public static ScheduledTask repeatWhileAsync(final Runnable task, final Callable<Boolean> predicate,
      final long interval, final long delay,
      final Runnable onComplete) {
    return getInstance().repeatWhileAsync(task, predicate, interval, delay, onComplete);
  }

  public static boolean isGlobalThread() {
    return getInstance().isGlobalThread();
  }

  public static boolean isTickThread() {
    return getInstance().isTickThread();
  }

  public static boolean isEntityThread(final Entity entity) {
    return getInstance().isEntityThread(entity);
  }

  public static boolean isRegionThread(final Location location) {
    return getInstance().isRegionThread(location);
  }

  public static <T> Future<T> callSyncMethod(final Callable<T> task) {
    return getInstance().callSyncMethod(task);
  }

}
