package com.lemonlightmc.zenith.events;

import org.bukkit.event.Cancellable;

public abstract class BaseEventCancellable extends BaseEvent implements Cancellable {

  public BaseEventCancellable() {
    super(false);
    this.isCancelled = false;
  }

  public BaseEventCancellable(final boolean isAsync) {
    super(isAsync);
    this.isCancelled = false;
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
    return "BaseEvent [name=" + name + ", isAsync()=" + isAsynchronous() + ", cancelled=" + isCancelled + "]";
  }

}
