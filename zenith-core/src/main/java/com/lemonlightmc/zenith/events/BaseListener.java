
package com.lemonlightmc.zenith.events;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class BaseListener implements Listener {

  protected NamespacedKey key;
  protected EventPriority priority;
  protected boolean enabled = true;
  protected boolean isRegistered = false;

  public BaseListener(final NamespacedKey key) {
    this(key, EventPriority.NORMAL);
  }

  public BaseListener(final NamespacedKey key, final EventPriority priority) {
    if (key == null) {
      throw new IllegalArgumentException("Invalid Listener Key: " + key);
    }
    this.key = key;
    this.priority = priority == null ? EventPriority.NORMAL : priority;
  }

  public void onRegister() {
  }

  public void onUnregister() {
  }

  public NamespacedKey getName() {
    return this.key;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(final boolean value) {
    this.enabled = value;
  }

  protected boolean shouldHandle() {
    return this.enabled && this.isRegistered;
  }

  public void setRegistered(final boolean value) {
    if (value != isRegistered) {
      if (value) {
        EventsAPI.register(this);
      } else {
        EventsAPI.unregister(this);
      }
    }
  }

  public void shouldRegister() {
    setRegistered(enabled);
  }

  public void register() {
    EventsAPI.register(this);
  }

  public void unregister() {
    EventsAPI.unregister(this);
  }

  @Override
  public String toString() {
    return "BaseListener [key=" + key + ", priority=" + priority + ", enabled=" + enabled + "]";
  }

  @Override
  public int hashCode() {
    final int result = 31 * (31 + key.hashCode()) + priority.hashCode();
    return 31 * result + (enabled ? 1231 : 1237);
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
    return key.equals(other.key) && priority == other.priority && enabled == other.enabled;
  }
}