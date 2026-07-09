package com.lemonlightmc.zenith.utils;

import com.lemonlightmc.zenith.additive.Cloneable;

public class CooldownHolder implements Cloneable<CooldownHolder> {

  private long startTime;
  private long duration;

  public CooldownHolder(final long startTime, final long duration) {
    this.startTime = startTime;
    this.duration = duration;
  }

  public CooldownHolder(final long duration) {
    this(System.currentTimeMillis(), duration);
  }

  public static CooldownHolder from(final long startTime, final long duration) {
    return new CooldownHolder(startTime, duration);
  }

  public static CooldownHolder from(final long duration) {
    return new CooldownHolder(System.currentTimeMillis(), duration);
  }

  public CooldownHolder add(final long time) {
    duration += time;
    return this;
  }

  public CooldownHolder remove(final long time) {
    duration -= time;
    return this;
  }

  public CooldownHolder set(final long time) {
    duration = time;
    return this;
  }

  public CooldownHolder reset() {
    startTime = System.currentTimeMillis();
    return this;
  }

  public CooldownHolder clear() {
    duration = 0;
    return this;
  }

  public long getDuration() {
    return duration;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return startTime + duration;
  }

  public long getRemaining() {
    return Math.max((startTime - duration) - System.currentTimeMillis(), 0);
  }

  public long getElapsed() {
    return System.currentTimeMillis() - startTime;
  }

  public boolean isOnFinished() {
    return System.currentTimeMillis() >= startTime + duration;
  }

  public boolean isOnCooldown() {
    return System.currentTimeMillis() < startTime + duration;
  }

  @Override
  public CooldownHolder clone() {
    return new CooldownHolder(startTime, duration);
  }

  @Override
  public int hashCode() {
    return 31 * ((int) (startTime ^ (startTime >>> 32))) + (int) (duration ^ (duration >>> 32)) + 961;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CooldownHolder other = (CooldownHolder) obj;
    return duration == other.duration && startTime == other.startTime;
  }

  @Override
  public String toString() {
    return "CooldownHolder [startTime=" + startTime + ", duration=" + duration + "]";
  }
}
