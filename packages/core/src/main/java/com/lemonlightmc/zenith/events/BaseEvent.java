package com.lemonlightmc.zenith.events;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;

import com.lemonlightmc.zenith.ZenithProvider;

public abstract class BaseEvent extends Event {

  protected boolean isCancelled = false;
  protected NamespacedKey key = null;

  public BaseEvent() {
    this(null, false);
  }

  public BaseEvent(final boolean isAsync) {
    this(null, isAsync);
  }

  public BaseEvent(final NamespacedKey key) {
    this(key, false);
  }

  public BaseEvent(final NamespacedKey key, final boolean isAsync) {
    super(isAsync);
    this.key = key == null ? new NamespacedKey(ZenithProvider.instance().getKey(), getClass().getSimpleName()) : key;
  }

  @Override
  public String getEventName() {
    return key.getKey();
  }

  public boolean call() {
    Bukkit.getPluginManager().callEvent(this);
    return isCancelled;
  }

  @Override
  public int hashCode() {
    int result = 31 + key.hashCode();
    result = 31 * result + (isAsynchronous() ? 1231 : 1237);
    return 31 * result + (isCancelled ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final BaseEvent other = (BaseEvent) obj;
    return key.equals(other.key) && isCancelled == other.isCancelled && isAsynchronous() == other.isAsynchronous();
  }

  @Override
  public String toString() {
    return "BaseEvent [key=" + key + ", isAsync()=" + isAsynchronous() + "]";
  }

}
