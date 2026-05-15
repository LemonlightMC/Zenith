package com.lemonlightmc.zenith.events;

import org.bukkit.event.Cancellable;

public abstract class BaseEventCancellable extends BaseEvent implements Cancellable {

  public BaseEventCancellable() {
    super(false);
    this.isCancelled = false;
  }

  public BaseEventCancellable(boolean isAsync) {
    super(isAsync);
    this.isCancelled = false;
  }

  @Override
  public boolean isCancelled() {
    return this.isCancelled;
  }

  @Override
  public void setCancelled(boolean isCancelled) {
    this.isCancelled = isCancelled;
  }

  public void cancel() {
    this.isCancelled = true;
  }
}
