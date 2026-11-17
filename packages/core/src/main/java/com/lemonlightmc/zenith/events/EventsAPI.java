package com.lemonlightmc.zenith.events;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.lemonlightmc.zenith.ZenithProvider;

/**
 * Utility API for building event subscriptions, dispatching events, and managing listener registration.
 */
public class EventsAPI {

  /**
   * Creates an event builder for the given event type with normal priority.
   *
   * @param eventClass the event class to listen for
   * @param <T> the event type
   * @return a new event builder
   */
  public static <T extends Event> EventBuilder<T> listen(final Class<T> eventClass) {
    return new EventBuilder<>(eventClass, EventPriority.NORMAL);
  }

  /**
   * Creates an event builder for the given event type and priority.
   *
   * @param eventClass the event class to listen for
   * @param priority the listener priority
   * @param <T> the event type
   * @return a new event builder
   */
  public static <T extends Event> EventBuilder<T> listen(final Class<T> eventClass, final EventPriority priority) {
    return new EventBuilder<>(eventClass, priority);
  }

  /**
   * Creates an event builder for the given event type with normal priority.
   *
   * @param eventClass the event class to listen for
   * @param <T> the event type
   * @return a new event builder
   */
  public static <T extends Event> EventBuilder<T> builder(final Class<T> eventClass) {
    return new EventBuilder<>(eventClass, EventPriority.NORMAL);
  }

  /**
   * Creates an event builder for the given event type and priority.
   *
   * @param eventClass the event class to listen for
   * @param priority the listener priority
   * @param <T> the event type
   * @return a new event builder
   */
  public static <T extends Event> EventBuilder<T> builder(final Class<T> eventClass, final EventPriority priority) {
    return new EventBuilder<>(eventClass, priority);
  }

  /**
   * Dispatches an event through the plugin manager.
   *
   * @param event the event to dispatch
   * @param <T> the event type
   * @return the dispatched event
   */
  public static <T extends Event> T call(final T event) {
    ZenithProvider.instance().getPluginManager().callEvent(event);
    return event;
  }

  /**
   * Dispatches an event asynchronously through the scheduler.
   *
   * @param event the event to dispatch
   * @param <T> the event type
   * @return the event instance
   */
  public static <T extends Event> T callAsync(final T event) {
    ZenithProvider.instance().getScheduler()
        .runAsync(() -> ZenithProvider.instance().getPluginManager().callEvent(event));
    return event;

  }

  /**
   * Dispatches an event on the scheduler's synchronous execution context.
   *
   * @param event the event to dispatch
   * @param <T> the event type
   * @return the event instance
   */
  public static <T extends Event> T callSync(final T event) {
    ZenithProvider.instance().getScheduler()
        .run(() -> ZenithProvider.instance().getPluginManager().callEvent(event));
    return event;
  }

  /**
   * Unregisters all listeners and dispatches a global unregister event.
   */
  public static void unregisterGlobalAll() {
    call(new ListenerAllUnregisterEvent());
    HandlerList.unregisterAll();
  }

  /**
   * Unregisters a listener from all handlers.
   *
   * @param listener the listener to unregister
   */
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

  /**
   * Unregisters a base listener from all handlers.
   *
   * @param listener the listener to unregister
   */
  public static void unregister(final BaseListener listener) {
    unregister(listener);
  }

  /**
   * Registers a listener with the plugin manager.
   *
   * @param listener the listener to register
   */
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
    ZenithProvider.instance().getPluginManager().registerEvents(listener, ZenithProvider.instance());
  }

  /**
   * Registers a base listener with the plugin manager.
   *
   * @param listener the listener to register
   */
  public static void register(final BaseListener listener) {
    register(listener);
  }

  /**
   * Registers an event executor for a specific event type and listener.
   *
   * @param event the event class to listen for
   * @param listener the listener instance
   * @param priority the listener priority
   * @param executor the event executor
   */
  public static void register(final Class<? extends Event> event, final Listener listener, final EventPriority priority,
      final EventExecutor executor) {
    register(event, listener, priority, executor, false);
  }

  /**
   * Registers an event executor for a specific event type and listener.
   *
   * @param event the event class to listen for
   * @param listener the listener instance
   * @param priority the listener priority, or {@code null} for {@link EventPriority#NORMAL}
   * @param executor the event executor
   * @param ignoreCancelled {@code true} to ignore cancelled events when supported
   */
  public static void register(final Class<? extends Event> event, final Listener listener, final EventPriority priority,
      final EventExecutor executor, final boolean ignoreCancelled) {
    if (listener == null || event == null || executor == null) {
      return;
    }
    call(new ListenerRegisterEvent(listener));
    ZenithProvider.instance().getPluginManager().registerEvent(event, listener,
        priority == null ? EventPriority.NORMAL : priority, executor, ZenithProvider.instance(),
        ignoreCancelled);
  }

  /**
   * Event fired when a listener is enabled.
   */
  public static class ListenerEnableEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    /**
     * Creates a new listener enable event.
     *
     * @param listener the affected listener
     */
    public ListenerEnableEvent(final Listener listener) {
      this.listener = listener;
    }

    /**
     * Returns the affected listener.
     *
     * @return the listener
     */
    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a listener is disabled.
   */
  public static class ListenerDisableEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    /**
     * Creates a new listener disable event.
     *
     * @param listener the affected listener
     */
    public ListenerDisableEvent(final Listener listener) {
      this.listener = listener;
    }

    /**
     * Returns the affected listener.
     *
     * @return the listener
     */
    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a listener is registered.
   */
  public static class ListenerRegisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    /**
     * Creates a new listener register event.
     *
     * @param listener the affected listener
     */
    public ListenerRegisterEvent(final Listener listener) {
      this.listener = listener;
    }

    /**
     * Returns the affected listener.
     *
     * @return the listener
     */
    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a listener is unregistered.
   */
  public static class ListenerUnregisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Listener listener;

    /**
     * Creates a new listener unregister event.
     *
     * @param listener the affected listener
     */
    public ListenerUnregisterEvent(final Listener listener) {
      this.listener = listener;
    }

    /**
     * Returns the affected listener.
     *
     * @return the listener
     */
    public Listener getListener() {
      return listener;
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when all listeners are unregistered.
   */
  public static class ListenerAllUnregisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Creates a new all-listeners unregister event.
     */
    public ListenerAllUnregisterEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }
}
