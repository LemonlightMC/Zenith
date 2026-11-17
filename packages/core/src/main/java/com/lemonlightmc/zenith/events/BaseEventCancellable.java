package com.lemonlightmc.zenith.events;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Cancellable;

import com.lemonlightmc.zenith.ZenithProvider;

/**
 * Base class for Zenith events that support cancellation.
 */
public abstract class BaseEventCancellable extends BaseEvent implements Cancellable {

  /**
   * Creates a synchronous cancellable event with a default key.
   */
  public BaseEventCancellable() {
    this(null, false);
  }

  /**
   * Creates a cancellable event with a default key and the specified async flag.
   *
   * @param isAsync {@code true} if the event is asynchronous; {@code false} otherwise
   */
  public BaseEventCancellable(final boolean isAsync) {
    this(null, isAsync);
  }

  /**
   * Creates a synchronous cancellable event with the specified key.
   *
   * @param key the event key, or {@code null} to generate a default key
   */
  public BaseEventCancellable(final NamespacedKey key) {
    this(key, false);
  }

  /**
   * Creates a cancellable event with the specified key and async flag.
   *
   * @param key the event key, or {@code null} to generate a default key
   * @param isAsync {@code true} if the event is asynchronous; {@code false} otherwise
   */
  public BaseEventCancellable(final NamespacedKey key, final boolean isAsync) {
    super(isAsync);
    this.key = key == null ? new NamespacedKey(ZenithProvider.instance().getKey(), getClass().getSimpleName()) : key;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  /**
   * {@inheritDoc}
   *
   * @param isCancelled {@code true} to cancel the event; {@code false} to allow it
   */
  @Override
  public void setCancelled(final boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

  /**
   * Cancels this event.
   */
  public void cancel() {
    this.isCancelled = true;
  }

  @Override
  public String toString() {
    return "BaseEvent [key=" + key + ", isAsync()=" + isAsynchronous() + ", cancelled=" + isCancelled + "]";
  }

}
