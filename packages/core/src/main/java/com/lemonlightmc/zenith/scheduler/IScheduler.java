package com.lemonlightmc.zenith.scheduler;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

public interface IScheduler {

        public ScheduledBuilder builder();

        public List<BukkitWorker> getWorkers();

        public List<BukkitTask> getPendingTasks();

        public void cancelTasks(Collection<Integer> taskIds);

        public void cancelTasks(int... taskIds);

        public void cancelTasks(Plugin plugin);

        public void cancelTasks();

        public boolean isRunning(int taskId);

        public boolean isQueued(int taskId);

        // run
        public ScheduledTask run(Runnable task);

        public ScheduledTask run(Runnable task, long delay);

        public ScheduledTask run(Runnable task, long delay, long interval);

        public void run(Consumer<ScheduledTask> consumer);

        public void run(Consumer<ScheduledTask> consumer, long delay);

        public void run(Consumer<ScheduledTask> consumer, long delay, long interval);

        public Set<ScheduledTask> run(Set<Runnable> tasks);

        public Set<ScheduledTask> run(Set<Runnable> tasks, long delay);

        public Set<ScheduledTask> run(Set<Runnable> tasks, long delay, long interval);

        // runAsync
        public ScheduledTask runAsync(Runnable task);

        public ScheduledTask runAsync(Runnable task, long delay, long interval);

        public ScheduledTask runAsync(Runnable task, long delay);

        public void runAsync(Consumer<ScheduledTask> consumer);

        public void runAsync(Consumer<ScheduledTask> consumer, long delay, long interval);

        public void runAsync(Consumer<ScheduledTask> consumer, long delay);

        public Set<ScheduledTask> runAsync(Set<Runnable> tasks);

        public Set<ScheduledTask> runAsync(Set<Runnable> tasks, long delay);

        public Set<ScheduledTask> runAsync(Set<Runnable> tasks, long delay, long interval);

        // runLater

        public ScheduledTask runLater(Runnable task, long delay);

        public Set<ScheduledTask> runLater(Set<Runnable> tasks, long delay);

        public void runLater(Consumer<ScheduledTask> consumer, long delay);

        // runLaterAsync

        public ScheduledTask runLaterAsync(Runnable task, long delay);

        public Set<ScheduledTask> runLaterAsync(Set<Runnable> tasks, long delay);

        public void runLaterAsync(Consumer<ScheduledTask> consumer, long delay);
        // runEvery

        public ScheduledTask runEvery(Runnable task, long interval);

        public ScheduledTask runEvery(Runnable task, long interval, long delay);

        public void runEvery(Consumer<ScheduledTask> consumer, long interval);

        public void runEvery(Consumer<ScheduledTask> consumer, long interval, long delay);

        public Set<ScheduledTask> runEvery(Set<Runnable> tasks, long interval);

        public Set<ScheduledTask> runEvery(Set<Runnable> tasks, long interval, long delay);

        // runEveryAsync

        public ScheduledTask runEveryAsync(Runnable task, long interval);

        public ScheduledTask runEveryAsync(Runnable task, long interval, long delay);

        public void runEveryAsync(Consumer<ScheduledTask> consumer, long interval);

        public void runEveryAsync(Consumer<ScheduledTask> consumer, long interval, long delay);

        public Set<ScheduledTask> runEveryAsync(Set<Runnable> tasks, long interval);

        public Set<ScheduledTask> runEveryAsync(Set<Runnable> tasks, long interval, long delay);

        // repeat

        public ScheduledTask repeat(Runnable task, int repetitions);

        public ScheduledTask repeat(Runnable task, int repetitions, long interval);

        public ScheduledTask repeat(Runnable runnable, int repetitions, long interval, long delay);

        public ScheduledTask repeat(Runnable task, int repetitions, Runnable onComplete);

        public ScheduledTask repeat(Runnable task, int repetitions, long interval, Runnable onComplete);

        public ScheduledTask repeat(Runnable runnable, int repetitions, long interval, long delay, Runnable onComplete);

        // repeatAsync

        public ScheduledTask repeatAsync(Runnable task, int repetitions);

        public ScheduledTask repeatAsync(Runnable task, int repetitions, long interval);

        public ScheduledTask repeatAsync(Runnable runnable, int repetitions, long interval, long delay);

        public ScheduledTask repeatAsync(Runnable task, int repetitions, Runnable onComplete);

        public ScheduledTask repeatAsync(Runnable task, int repetitions, long interval,
                        Runnable onComplete);

        public ScheduledTask repeatAsync(Runnable runnable, int repetitions, long interval, long delay,
                        Runnable onComplete);

        // repeatWhile

        public ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval);

        public ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval,
                        long delay);

        public ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval,
                        Runnable onComplete);

        public ScheduledTask repeatWhile(Runnable task, Callable<Boolean> predicate, long interval,
                        long delay,
                        Runnable onComplete);

        // repeatWhileAsync

        public ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate,
                        long interval);

        public ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate,
                        long interval,
                        Runnable onComplete);

        public ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate,
                        long interval, long delay);

        public ScheduledTask repeatWhileAsync(Runnable task, Callable<Boolean> predicate, long interval,
                        long delay,
                        Runnable onComplete);

}