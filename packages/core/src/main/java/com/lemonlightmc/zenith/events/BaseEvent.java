package com.lemonlightmc.zenith.events;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;

import com.lemonlightmc.zenith.ZenithProvider;

/**
 * Base class for Zenith events that provides common identity and dispatch behavior.
 *
 * <p>Each event is associated with a {@link NamespacedKey} and can be dispatched through Bukkit's plugin manager.
 */
public abstract class BaseEvent extends Event {

  /** Indicates whether the event has been cancelled. */
  protected boolean isCancelled = false;
  /** The namespaced key identifying this event. */
  protected NamespacedKey key = null;

  /**
   * Creates a synchronous event with a default key.
   */
  public BaseEvent() {
    this(null, false);
  }

  /**
   * Creates an event with a default key and the specified async flag.
   *
   * @param isAsync {@code true} if the event is asynchronous; {@code false} otherwise
   */
  public BaseEvent(final boolean isAsync) {
    this(null, isAsync);
  }

  /**
   * Creates a synchronous event with the specified key.
   *
   * @param key the event key, or {@code null} to generate a default key
   */
  public BaseEvent(final NamespacedKey key) {
    this(key, false);
  }

  /**
   * Creates an event with the specified key and async flag.
   *
   * @param key the event key, or {@code null} to generate a default key
   * @param isAsync {@code true} if the event is asynchronous; {@code false} otherwise
   */
  public BaseEvent(final NamespacedKey key, final boolean isAsync) {
    super(isAsync);
    this.key = key == null ? new NamespacedKey(ZenithProvider.instance().getKey(), getClass().getSimpleName()) : key;
  }

  /**
   * {@inheritDoc}
   *
   * @return the event key name
   */
  @Override
  public String getEventName() {
    return key.getKey();
  }

  /**
   * Dispatches this event through Bukkit's plugin manager.
   *
   * @return {@code true} if the event is cancelled after dispatch; {@code false} otherwise
   */
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
