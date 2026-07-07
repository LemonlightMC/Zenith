package com.lemonlightmc.zenith.wrapper;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.scheduler.ScheduledTask;

public class ConfirmRequest {
  CommandSender sender;
  String message;
  long ticksToExpire;
  ScheduledTask expiredTask = null;
  Runnable onSuccess = null;
  Runnable onExpired = null;

  public ConfirmRequest(final CommandSender sender, final String message) {
    this.sender = sender;
    this.message = message;
  }

  public static ConfirmableBuilder builder(final CommandSender sender, final String message) {
    return new ConfirmableBuilder(sender, message);
  }

  public void start() {
    if (expiredTask != null) {
      return;
    }
    expiredTask = ZenithProvider.instance().getScheduler().runLaterAsync(() -> {
      expire();
    }, ticksToExpire);
  }

  public void stop() {
    if (expiredTask != null) {
      expiredTask.cancel();
      expiredTask = null;
    }
  }

  public void success() {
    stop();
    ZenithProvider.instance().getScheduler().run(() -> {
      if (onSuccess != null) {
        onSuccess.run();
      }
    });
  }

  public void expire() {
    if (expiredTask == null) {
      return;
    }
    onExpired.run();
    ZenithProvider.instance().getScheduler().runLater(() -> {
      expiredTask = null;
    }, 1);
  }

  public CommandSender getSender() {
    return sender;
  }

  public String getMessage() {
    return message;
  }

  public Runnable getOnSuccess() {
    return onSuccess;
  }

  public Runnable getOnExpired() {
    return onExpired;
  }

  public static class ConfirmableBuilder implements Builder<ConfirmRequest> {
    ConfirmRequest request;

    public ConfirmableBuilder(final CommandSender sender, final String confirmableString) {
      request = new ConfirmRequest(sender, confirmableString);
    }

    public ConfirmableBuilder success(final Runnable success) {
      request.onSuccess = success;
      return this;
    }

    public ConfirmableBuilder expired(final Runnable expired) {
      request.onExpired = expired;
      return this;
    }

    public ConfirmableBuilder time(final long ticks) {
      request.ticksToExpire = ticks;
      return this;
    }

    public ConfirmableBuilder message(final String message) {
      request.message = message;
      return this;
    }

    @Override
    public ConfirmRequest build() {
      return request;
    }
  }
}
