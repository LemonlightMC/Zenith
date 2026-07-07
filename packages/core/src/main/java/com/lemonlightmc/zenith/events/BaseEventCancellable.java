package com.lemonlightmc.zenith.events;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Cancellable;

import com.lemonlightmc.zenith.ZenithProvider;

public abstract class BaseEventCancellable extends BaseEvent implements Cancellable {

  public BaseEventCancellable() {
    this(null, false);
  }

  public BaseEventCancellable(final boolean isAsync) {
    this(null, isAsync);
  }

  public BaseEventCancellable(final NamespacedKey key) {
    this(key, false);
  }

  public BaseEventCancellable(final NamespacedKey key, final boolean isAsync) {
    super(isAsync);
    this.key = key == null ? new NamespacedKey(ZenithProvider.instance().getKey(), getClass().getSimpleName()) : key;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(final boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

  public void cancel() {
    this.isCancelled = true;
  }

  @Override
  public String toString() {
    return "BaseEvent [key=" + key + ", isAsync()=" + isAsynchronous() + ", cancelled=" + isCancelled + "]";
  }

}
