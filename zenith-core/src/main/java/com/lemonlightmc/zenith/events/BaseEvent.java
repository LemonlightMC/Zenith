package com.lemonlightmc.zenith.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class BaseEvent extends Event {

  protected boolean isCancelled = false;
  protected String name = null;

  public BaseEvent() {
    super(false);
  }

  public BaseEvent(boolean isAsync) {
    super(isAsync);
  }

  public BaseEvent(String name) {
    super(false);
    this.name = name == null || name.isBlank() ? getClass().getSimpleName() : name;
  }

  public BaseEvent(String name, boolean isAsync) {
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
}
