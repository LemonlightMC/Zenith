package com.lemonlightmc.zenith.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.scheduler.Scheduler.ThreadContext;

public interface IScheduled extends BukkitTask {
  @Override
  public int getTaskId();

  @Override
  public Plugin getOwner();

  public BukkitTask getBacking();

  @Override
  public boolean isCancelled();

  @Override
  public void cancel();

  public boolean isRunning();

  public boolean isQueued();

  public ThreadContext getThreadContext();

  @Override
  public boolean isSync();

  public boolean isAsync();

  public boolean isDelayed();

  public long getDelay();

  public int getTimesRan();

}