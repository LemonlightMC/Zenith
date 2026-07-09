package com.lemonlightmc.zenith.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.lemonlightmc.zenith.additive.Cloneable;
import com.lemonlightmc.zenith.additive.Lazy;
import com.lemonlightmc.zenith.additive.time.PolyTimeUnit;

public class CooldownStore<T> implements Cloneable<CooldownStore<T>>, Iterable<T> {
  private final HashMap<T, CooldownHolder> cooldowns;
  public long defaultDuration;
  private static Lazy<CooldownStore<?>> globalStore = Lazy.of(() -> new CooldownStore<>());

  public CooldownStore() {
    this(1000L);
  }

  public CooldownStore(final long defaultDuration) {
    this.defaultDuration = defaultDuration;
    this.cooldowns = new HashMap<>();
  }

  public CooldownStore(final CooldownStore<T> store) {
    this.cooldowns = new HashMap<>(store.cooldowns);
    this.defaultDuration = store.defaultDuration;
  }

  public static CooldownStore<?> global() {
    return globalStore.get();
  }

  public static <T> CooldownStore<T> from() {
    return new CooldownStore<T>();
  }

  public static <T> CooldownStore<T> from(final CooldownStore<T> store) {
    return new CooldownStore<T>(store);
  }

  public static <T> CooldownStore<T> from(final long defaultDuration, final PolyTimeUnit unit) {
    return new CooldownStore<T>(unit.toMillis(defaultDuration));
  }

  public CooldownStore<T> setDefaultDuration(final long defaultDuration) {
    this.defaultDuration = defaultDuration;
    return this;
  }

  public CooldownStore<T> setDefaultDuration(final long defaultDuration, final PolyTimeUnit unit) {
    this.defaultDuration = unit.toMillis(defaultDuration);
    return this;
  }

  public long getDefaultDuration() {
    return this.defaultDuration;
  }

  public CooldownStore<T> set(final T key, final long time) {
    if (time < 0) {
      return this;
    }
    this.cooldowns.compute(key,
        (k, v) -> v == null ? new CooldownHolder(System.currentTimeMillis(), time) : v.set(time));
    return this;
  }

  public CooldownStore<T> set(final T key) {
    return set(key, defaultDuration);
  }

  public CooldownStore<T> add(final T key, final long time) {
    if (time < 0) {
      return this;
    }
    this.cooldowns.compute(key,
        (k, v) -> v == null ? new CooldownHolder(System.currentTimeMillis(), time) : v.add(time));
    return this;
  }

  public CooldownStore<T> add(final T key) {
    return add(key, defaultDuration);
  }

  public CooldownHolder get(final T key) {
    return this.cooldowns.get(key);
  }

  public boolean contains(final T key) {
    return this.cooldowns.containsKey(key);
  }

  public CooldownStore<T> reset(final T key) {
    this.cooldowns.compute(key,
        (k, v) -> v == null ? new CooldownHolder(System.currentTimeMillis(), defaultDuration) : v.reset());
    return this;
  }

  public CooldownStore<T> remove(final T key) {
    this.cooldowns.remove(key);
    return this;
  }

  public CooldownStore<T> clear() {
    this.cooldowns.clear();
    return this;
  }

  public HashMap<T, CooldownHolder> getEntries() {
    return this.cooldowns;
  }

  public Set<T> keySet() {
    return this.cooldowns.keySet();
  }

  public boolean isOnCooldown(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    return holder == null ? false : holder.isOnCooldown();
  }

  public long getStartTime(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    return holder == null ? -1 : holder.getStartTime();
  }

  public long getEndTime(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    return holder == null ? -1 : holder.getEndTime();
  }

  public long getDuration(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    return holder == null ? -1 : holder.getDuration();
  }

  public long getRemaining(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    return holder == null ? -1 : holder.getRemaining();
  }

  public long getElapsed(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    return holder == null ? -1 : holder.getElapsed();
  }

  public boolean stopIfFinished(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    if (holder == null) {
      return true;
    } else if (holder.isOnFinished()) {
      cooldowns.remove(key);
      return true;
    }
    return false;
  }

  public boolean stopIfOnCooldown(final T key) {
    final CooldownHolder holder = this.cooldowns.get(key);
    if (holder == null) {
      return false;
    } else if (holder.isOnCooldown()) {
      cooldowns.remove(key);
      return true;
    }
    return false;
  }

  @Override
  public CooldownStore<T> clone() {
    return new CooldownStore<T>(this);
  }

  @Override
  public Iterator<T> iterator() {
    return this.cooldowns.keySet().iterator();
  }

  @Override
  public int hashCode() {
    return 31 * cooldowns.hashCode() + (int) (defaultDuration ^ (defaultDuration >>> 32)) + 961;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CooldownStore<?> other = (CooldownStore<?>) obj;
    if (cooldowns == null && other.getEntries() != null) {
      return false;
    }
    return cooldowns.equals(other.getEntries()) && defaultDuration == other.getDefaultDuration();
  }

  @Override
  public String toString() {
    return "CooldownStore [cooldowns=" + cooldowns + ", defaultDuration=" + defaultDuration + "]";
  }
}
