package com.lemonlightmc.zenith.events;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.lemonlightmc.zenith.ZenithProvider;

public class EventsAPI {

  public static <T extends Event> EventBuilder<T> listen(final Class<T> eventClass) {
    return new EventBuilder<>(eventClass, EventPriority.NORMAL);
  }

  public static <T extends Event> EventBuilder<T> listen(final Class<T> eventClass, final EventPriority priority) {
    return new EventBuilder<>(eventClass, priority);
  }

  public static <T extends Event> EventBuilder<T> builder(final Class<T> eventClass) {
    return new EventBuilder<>(eventClass, EventPriority.NORMAL);
  }

  public static <T extends Event> EventBuilder<T> builder(final Class<T> eventClass, final EventPriority priority) {
    return new EventBuilder<>(eventClass, priority);
  }

  public static <T extends Event> T call(final T event) {
    ZenithProvider.getInstance().getPluginManager().callEvent(event);
    return event;
  }

  public static <T extends Event> T callAsync(final T event) {
    ZenithProvider.getInstance().getScheduler()
        .runAsync(() -> ZenithProvider.getInstance().getPluginManager().callEvent(event));
    return event;

  }

  public static <T extends Event> T callSync(final T event) {
    ZenithProvider.getInstance().getScheduler()
        .run(() -> ZenithProvider.getInstance().getPluginManager().callEvent(event));
    return event;
  }

  public static void unregisterGlobalAll() {
    call(new ListenerAllUnregisterEvent());
    HandlerList.unregisterAll();
  }

  public static void unregister(final Listener listener) {
    if (listener == null) {
      return;
    }
    if (listener instanceof final BaseListener baseListener) {
      if (!baseListener.isRegistered) {
        return;
      }
      baseListener.isRegistered = false;
      baseListener.onUnregister();
    }
    call(new ListenerUnregisterEvent(listener));
    HandlerList.unregisterAll(listener);
  }

  public static void unregister(final BaseListener listener) {
    unregister(listener);
  }

  public static void register(final Listener listener) {
    if (listener == null) {
      return;
    }
    if (listener instanceof final BaseListener baseListener) {
      if (baseListener.isRegistered) {
        return;
      }
      baseListener.isRegistered = true;
      baseListener.onRegister();
    }
    call(new ListenerRegisterEvent(listener));
    ZenithProvider.getInstance().getPluginManager().registerEvents(listener, ZenithProvider.getInstance());
  }

  public static void register(final BaseListener listener) {
    register(listener);
  }

  public static void register(final Class<? extends Event> event, final Listener listener, final EventPriority priority,
      final EventExecutor executor) {
    register(event, listener, priority, executor, false);
  }

  public static void register(final Class<? extends Event> event, final Listener listener, final EventPriority priority,
      final EventExecutor executor, final boolean ignoreCancelled) {
    if (listener == null || event == null || executor == null) {
      return;
    }
    call(new ListenerRegisterEvent(listener));
    ZenithProvider.getInstance().getPluginManager().registerEvent(event, listener,
        priority == null ? EventPriority.NORMAL : priority, executor, ZenithProvider.getInstance(),
        ignoreCancelled);
  }

  public static class ListenerEnableEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    public ListenerEnableEvent(final Listener listener) {
      this.listener = listener;
    }

    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ListenerDisableEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    public ListenerDisableEvent(final Listener listener) {
      this.listener = listener;
    }

    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ListenerRegisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    public ListenerRegisterEvent(final Listener listener) {
      this.listener = listener;
    }

    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ListenerUnregisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    public ListenerUnregisterEvent(final Listener listener) {
      this.listener = listener;
    }

    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ListenerAllUnregisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    public ListenerAllUnregisterEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }
}
