package com.lemonlightmc.zenith.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class BaseEvent extends Event {

  protected boolean isCancelled = false;
  protected String name = null;

  public BaseEvent() {
    super(false);
  }

  public BaseEvent(final boolean isAsync) {
    super(isAsync);
  }

  public BaseEvent(final String name) {
    super(false);
    this.name = name == null || name.isBlank() ? getClass().getSimpleName() : name;
  }

  public BaseEvent(final String name, final boolean isAsync) {
    super(isAsync);
    this.name = name == null || name.isBlank() ? getClass().getSimpleName() : name;
  }

  @Override
  public String getEventName() {
    return name;
  }

  public boolean call() {
    Bukkit.getPluginManager().callEvent(this);
    return isCancelled;
  }

  @Override
  public int hashCode() {
    int result = 31 + name.hashCode();
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
    return name.equals(other.name) && isCancelled == other.isCancelled && isAsynchronous() == other.isAsynchronous();
  }

  @Override
  public String toString() {
    return "BaseEvent [name=" + name + ", isAsync()=" + isAsynchronous() + "]";
  }

}
