package com.lemonlightmc.zenith.events;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class EventSubscriptionListener<T extends Event> implements EventExecutor, Listener {

  private final Class<T> eventClass;
  private final EventPriority priority;

  private final BiConsumer<? super T, Throwable> exceptionConsumer;
  private final boolean handleSubclasses;

  private final Predicate<T>[] filters;
  private final BiPredicate<EventSubscriptionListener<T>, T>[] preExpiryTests;
  private final BiPredicate<EventSubscriptionListener<T>, T>[] midExpiryTests;
  private final BiPredicate<EventSubscriptionListener<T>, T>[] postExpiryTests;
  private final BiConsumer<EventSubscriptionListener<T>, ? super T>[] handlers;

  private final AtomicLong callCount = new AtomicLong(0);
  private final AtomicBoolean active = new AtomicBoolean(true);

  @SuppressWarnings("unchecked")
  EventSubscriptionListener(final EventBuilder<T> builder,
      final List<BiConsumer<EventSubscriptionListener<T>, ? super T>> handlers) {
    this.eventClass = builder.eventClass;
    this.priority = builder.priority;
    this.exceptionConsumer = builder.exceptionConsumer;
    this.handleSubclasses = builder.handleSubclasses;

    this.filters = builder.filters.toArray(new Predicate[builder.filters.size()]);
    this.preExpiryTests = builder.preExpiryTests.toArray(new BiPredicate[builder.preExpiryTests.size()]);
    this.midExpiryTests = builder.midExpiryTests.toArray(new BiPredicate[builder.midExpiryTests.size()]);
    this.postExpiryTests = builder.postExpiryTests.toArray(new BiPredicate[builder.postExpiryTests.size()]);
    this.handlers = handlers.toArray(new BiConsumer[handlers.size()]);
  }

  void register() {
    EventsAPI.register(this.eventClass, this, this.priority, this, false);
  }

  @Override
  public void execute(final Listener listener, final Event event) {
    // check we actually want this event
    if (this.handleSubclasses) {
      if (!this.eventClass.isInstance(event)) {
        return;
      }
    } else {
      if (event.getClass() != this.eventClass) {
        return;
      }
    }

    // this handler is disabled, so unregister from the event.
    if (!this.active.get()) {
      event.getHandlers().unregister(listener);
      return;
    }

    // obtain the event instance
    final T eventInstance = this.eventClass.cast(event);

    // check pre-expiry tests
    for (final BiPredicate<EventSubscriptionListener<T>, T> test : this.preExpiryTests) {
      if (test.test(this, eventInstance)) {
        event.getHandlers().unregister(listener);
        this.active.set(false);
        return;
      }
    }

    // begin "handling" of the event
    try {
      // check the filters
      for (final Predicate<T> filter : this.filters) {
        if (!filter.test(eventInstance)) {
          return;
        }
      }

      // check mid-expiry tests
      for (final BiPredicate<EventSubscriptionListener<T>, T> test : this.midExpiryTests) {
        if (test.test(this, eventInstance)) {
          event.getHandlers().unregister(listener);
          this.active.set(false);
          return;
        }
      }

      // call the handler
      for (final BiConsumer<EventSubscriptionListener<T>, ? super T> handler : this.handlers) {
        handler.accept(this, eventInstance);
      }

      // increment call counter
      this.callCount.incrementAndGet();
    } catch (final Throwable t) {
      this.exceptionConsumer.accept(eventInstance, t);
    }

    // check post-expiry tests
    for (final BiPredicate<EventSubscriptionListener<T>, T> test : this.postExpiryTests) {
      if (test.test(this, eventInstance)) {
        event.getHandlers().unregister(listener);
        this.active.set(false);
        return;
      }
    }
  }

  public Class<T> getEventClass() {
    return this.eventClass;
  }

  public boolean isActive() {
    return this.active.get();
  }

  public boolean isClosed() {
    return !this.active.get();
  }

  public long getCallCounter() {
    return this.callCount.get();
  }

  public boolean unregister() {
    if (!this.active.getAndSet(false)) {
      return false;
    }
    unregisterListener(this.eventClass, this);
    return true;
  }

  @SuppressWarnings("JavaReflectionMemberAccess")
  private static void unregisterListener(final Class<? extends Event> eventClass, final Listener listener) {
    try {
      // unfortunately we can't cache this reflect call, as the method is static
      final Method getHandlerListMethod = eventClass.getMethod("getHandlerList");
      final HandlerList handlerList = (HandlerList) getHandlerListMethod.invoke(null);
      handlerList.unregister(listener);
    } catch (final Throwable t) {
      // ignored
    }
  }
}
