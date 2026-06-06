package com.lemonlightmc.zenith.scheduler;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import com.lemonlightmc.zenith.ZenithProvider;

public class GlobalScheduler {

  public static ScheduledBuilder builder() {
    return ZenithProvider.getInstance().getScheduler().builder();
  }

  public static List<BukkitWorker> getWorkers() {
    return ZenithProvider.getInstance().getScheduler().getWorkers();
  }

  public static List<BukkitTask> getPendingTasks() {
    return ZenithProvider.getInstance().getScheduler().getPendingTasks();
  }

  public static void cancelTasks(Collection<Integer> taskIds) {
    ZenithProvider.getInstance().getScheduler().cancelTasks(taskIds);
  }

  public static void cancelTasks(int... taskIds) {
    ZenithProvider.getInstance().getScheduler().cancelTasks(taskIds);
  }

  public static void cancelTasks(Plugin plugin) {
    ZenithProvider.getInstance().getScheduler().cancelTasks(plugin);
  }

  public static void cancelTasks() {
    ZenithProvider.getInstance().getScheduler().cancelTasks();
  }

  public static boolean isRunning(int taskId) {
    return ZenithProvider.getInstance().getScheduler().isRunning(taskId);
  }

  public static boolean isQueued(int taskId) {
    return ZenithProvider.getInstance().getScheduler().isQueued(taskId);
  }

  public static ScheduledTask run(Runnable task) {
    return ZenithProvider.getInstance().getScheduler().run(task);
  }

  public static ScheduledTask run(Runnable task, long delay) {
    return ZenithProvider.getInstance().getScheduler().run(task, delay);
  }

  public static ScheduledTask run(Runnable task, long delay, long interval) {
    return ZenithProvider.getInstance().getScheduler().run(task, delay, interval);
  }

  public static void run(Consumer<ScheduledTask> consumer) {
    ZenithProvider.getInstance().getScheduler().run(consumer);
  }

  public static void run(Consumer<ScheduledTask> consumer, long delay) {
    ZenithProvider.getInstance().getScheduler().run(consumer, delay);
  }

  public static void run(Consumer<ScheduledTask> consumer, long delay, long interval) {
    ZenithProvider.getInstance().getScheduler().run(consumer, delay, interval);
  }

  public static Set<ScheduledTask> run(Set<Runnable> tasks) {
    return ZenithProvider.getInstance().getScheduler().run(tasks);
  }

  public static Set<ScheduledTask> run(Set<Runnable> tasks, long delay) {
    return ZenithProvider.getInstance().getScheduler().run(tasks, delay);
  }

  public static Set<ScheduledTask> run(Set<Runnable> tasks, long delay, long interval) {
    return ZenithProvider.getInstance().getScheduler().run(tasks, delay, interval);
  }

  public static ScheduledTask runAsync(Runnable task) {
    return ZenithProvider.getInstance().getScheduler().runAsync(task);
  }

  public static ScheduledTask runAsync(Runnable task, long delay, long interval) {
    return ZenithProvider.getInstance().getScheduler().runAsync(task, delay, interval);
  }

  public static ScheduledTask runAsync(Runnable task, long delay) {
    return ZenithProvider.getInstance().getScheduler().runAsync(task, delay);
  }

  public static void runAsync(Consumer<ScheduledTask> consumer) {
    ZenithProvider.getInstance().getScheduler().runAsync(consumer);
  }

  public static void runAsync(Consumer<ScheduledTask> consumer, long delay, long interval) {
    ZenithProvider.getInstance().getScheduler().runAsync(consumer, delay, interval);
  }

  public static void runAsync(Consumer<ScheduledTask> consumer, long delay) {
    ZenithProvider.getInstance().getScheduler().runAsync(consumer, delay);
  }

  public static Set<ScheduledTask> runAsync(Set<Runnable> tasks) {
    return ZenithProvider.getInstance().getScheduler().runAsync(tasks);
  }

  public static Set<ScheduledTask> runAsync(Set<Runnable> tasks, long delay) {
    return ZenithProvider.getInstance().getScheduler().runAsync(tasks, delay);
  }

  public static Set<ScheduledTask> runAsync(Set<Runnable> tasks, long delay, long interval) {
    return ZenithProvider.getInstance().getScheduler().runAsync(tasks, delay, interval);
  }

  public static ScheduledTask runLater(Runnable task, long delay) {
    return ZenithProvider.getInstance().getScheduler().runLater(task, delay);
  }

  public static Set<ScheduledTask> runLater(Set<Runnable> tasks, long delay) {
    return ZenithProvider.getInstance().getScheduler().runLater(tasks, delay);
  }

  public static void runLater(Consumer<ScheduledTask> consumer, long delay) {
    ZenithProvider.getInstance().getScheduler().runLater(consumer, delay);
  }

  public static ScheduledTask runLaterAsync(Runnable task, long delay) {
    return ZenithProvider.getInstance().getScheduler().runLaterAsync(task, delay);
  }

  public static Set<ScheduledTask> runLaterAsync(Set<Runnable> tasks, long delay) {
    return ZenithProvider.getInstance().getScheduler().runLaterAsync(tasks, delay);
  }

  public static void runLaterAsync(Consumer<ScheduledTask> consumer, long delay) {
    ZenithProvider.getInstance().getScheduler().runLaterAsync(consumer, delay);
  }

  public static ScheduledTask runEvery(Runnable task, long interval) {
    return ZenithProvider.getInstance().getScheduler().runEvery(task, interval);
  }

  public static ScheduledTask runEvery(Runnable task, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().runEvery(task, interval, delay);
  }

  public static void runEvery(Consumer<ScheduledTask> consumer, long interval) {
    ZenithProvider.getInstance().getScheduler().runEvery(consumer, interval);
  }

  public static void runEvery(Consumer<ScheduledTask> consumer, long interval, long delay) {
    ZenithProvider.getInstance().getScheduler().runEvery(consumer, interval, delay);
  }

  public static Set<ScheduledTask> runEvery(Set<Runnable> tasks, long interval) {
    return ZenithProvider.getInstance().getScheduler().runEvery(tasks, interval);
  }

  public static Set<ScheduledTask> runEvery(Set<Runnable> tasks, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().runEvery(tasks, interval, delay);
  }

  public static ScheduledTask runEveryAsync(Runnable task, long interval) {
    return ZenithProvider.getInstance().getScheduler().runEveryAsync(task, interval);
  }

  public static ScheduledTask runEveryAsync(Runnable task, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().runEveryAsync(task, interval, delay);
  }

  public static void runEveryAsync(Consumer<ScheduledTask> consumer, long interval) {
    ZenithProvider.getInstance().getScheduler().runEveryAsync(consumer, interval);
  }

  public static void runEveryAsync(Consumer<ScheduledTask> consumer, long interval, long delay) {
    ZenithProvider.getInstance().getScheduler().runEveryAsync(consumer, interval, delay);
  }

  public static Set<ScheduledTask> runEveryAsync(Set<Runnable> tasks, long interval) {
    return ZenithProvider.getInstance().getScheduler().runEveryAsync(tasks, interval);
  }

  public static Set<ScheduledTask> runEveryAsync(Set<Runnable> tasks, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().runEveryAsync(tasks, interval, delay);
  }

  public static ScheduledTask repeat(Runnable task, int repetitions) {
    return ZenithProvider.getInstance().getScheduler().repeat(task, repetitions);
  }

  public static ScheduledTask repeat(Runnable task, int repetitions, long interval) {
    return ZenithProvider.getInstance().getScheduler().repeat(task, repetitions, interval);
  }

  public static ScheduledTask repeat(Runnable runnable, int repetitions, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().repeat(runnable, repetitions, interval, delay);
  }

  public static ScheduledTask repeat(Runnable task, int repetitions, Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeat(task, repetitions, onComplete);
  }

  public static ScheduledTask repeat(Runnable task, int repetitions, long interval, Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeat(task, repetitions, interval, onComplete);
  }

  public static ScheduledTask repeat(Runnable runnable, int repetitions, long interval, long delay,
      Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeat(runnable, repetitions, interval, delay, onComplete);
  }

  public static ScheduledTask repeatAsync(Runnable task, int repetitions) {
    return ZenithProvider.getInstance().getScheduler().repeatAsync(task, repetitions);
  }

  public static ScheduledTask repeatAsync(Runnable task, int repetitions, long interval) {
    return ZenithProvider.getInstance().getScheduler().repeatAsync(task, repetitions, interval);
  }

  public static ScheduledTask repeatAsync(Runnable runnable, int repetitions, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().repeatAsync(runnable, repetitions, interval, delay);
  }

  public static ScheduledTask repeatAsync(Runnable task, int repetitions, Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeatAsync(task, repetitions, onComplete);
  }

  public static ScheduledTask repeatAsync(Runnable task, int repetitions, long interval, Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeatAsync(task, repetitions, interval, onComplete);
  }

  public static ScheduledTask repeatAsync(Runnable runnable, int repetitions, long interval, long delay,
      Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeatAsync(runnable, repetitions, interval, delay, onComplete);
  }

  public static ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval) {
    return ZenithProvider.getInstance().getScheduler().repeatWhile(task, predicate, interval);
  }

  public static ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().repeatWhile(task, predicate, interval, delay);
  }

  public static ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval,
      Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeatWhile(task, predicate, interval, onComplete);
  }

  public static ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval, long delay,
      Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeatWhile(task, predicate, interval, delay, onComplete);
  }

  public static ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate, long interval) {
    return ZenithProvider.getInstance().getScheduler().repeatWhileAsync(task, predicate, interval);
  }

  public static ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate, long interval,
      Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeatWhileAsync(task, predicate, interval, onComplete);
  }

  public static ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate, long interval, long delay) {
    return ZenithProvider.getInstance().getScheduler().repeatWhileAsync(task, predicate, interval, delay);
  }

  public static ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate, long interval, long delay,
      Runnable onComplete) {
    return ZenithProvider.getInstance().getScheduler().repeatWhileAsync(task, predicate, interval, delay, onComplete);
  }

}
