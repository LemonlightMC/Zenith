package com.lemonlightmc.zenith.events;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Base listener implementation with registration state, priority, and enablement helpers.
 */
public abstract class BaseListener implements Listener {

  /** The priority used when handling events. */
  protected EventPriority priority;
  /** Whether this listener is currently enabled. */
  protected boolean enabled = true;
  /** Whether this listener is currently registered. */
  protected boolean isRegistered = false;

  /**
   * Creates a listener with {@link EventPriority#NORMAL} priority.
   */
  public BaseListener() {
    this(EventPriority.NORMAL);
  }

  /**
   * Creates a listener with the specified event priority.
   *
   * @param priority the event priority to use, or {@code null} for {@link EventPriority#NORMAL}
   */
  public BaseListener(final EventPriority priority) {
    this.priority = priority == null ? EventPriority.NORMAL : priority;
  }

  /**
   * Called after this listener is registered.
   */
  public void onRegister() {
  }

  /**
   * Called after this listener is unregistered.
   */
  public void onUnregister() {
  }

  /**
   * Returns whether this listener is enabled.
   *
   * @return {@code true} if the listener is enabled; {@code false} otherwise
   */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * Sets whether this listener is enabled.
   *
   * @param value {@code true} to enable the listener; {@code false} to disable it
   */
  public void setEnabled(final boolean value) {
    this.enabled = value;
  }

  /**
   * Returns whether this listener should handle events in its current state.
   *
   * @return {@code true} if the listener is enabled and registered; {@code false} otherwise
   */
  protected boolean shouldHandle() {
    return this.enabled && this.isRegistered;
  }

  /**
   * Updates the registration state of this listener.
   *
   * @param value {@code true} to register the listener; {@code false} to unregister it
   */
  public void setRegistered(final boolean value) {
    if (value != isRegistered) {
      if (value) {
        EventsAPI.register(this);
      } else {
        EventsAPI.unregister(this);
      }
    }
  }

  /**
   * Synchronizes registration state with the current enabled state.
   */
  public void shouldRegister() {
    setRegistered(enabled);
  }

  /**
   * Registers this listener with the events API.
   */
  public void register() {
    EventsAPI.register(this);
  }

  /**
   * Unregisters this listener from the events API.
   */
  public void unregister() {
    EventsAPI.unregister(this);
  }

  @Override
  public String toString() {
    return "BaseListener [priority=" + priority + ", enabled=" + enabled + "]";
  }

  @Override
  public int hashCode() {
    return 31 * (31 + priority.hashCode()) + (enabled ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final BaseListener other = (BaseListener) obj;
    return priority == other.priority && enabled == other.enabled;
  }
}
