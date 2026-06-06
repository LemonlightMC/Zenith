package com.lemonlightmc.zenith.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import com.google.common.base.Preconditions;

public class EventBuilder<T extends Event> {
  final Class<T> eventClass;
  final EventPriority priority;

  boolean handleSubclasses = false;
  BiConsumer<? super T, Throwable> exceptionConsumer = (e, t) -> {
  };

  final List<Predicate<T>> filters = new ArrayList<>(3);
  final List<BiPredicate<EventSubscriptionListener<T>, T>> preExpiryTests = new ArrayList<>(0);
  final List<BiPredicate<EventSubscriptionListener<T>, T>> midExpiryTests = new ArrayList<>(0);
  final List<BiPredicate<EventSubscriptionListener<T>, T>> postExpiryTests = new ArrayList<>(0);
  private final List<BiConsumer<EventSubscriptionListener<T>, ? super T>> handlers = new ArrayList<>(1);

  public EventBuilder(final Class<T> eventClass, final EventPriority priority) {
    if (eventClass == null) {
      throw new IllegalArgumentException("EventClass must be defined");
    }
    this.eventClass = eventClass;
    this.priority = priority == null ? EventPriority.NORMAL : priority;
  }

  public static <T extends Event> EventBuilder<T> newBuilder(final Class<T> eventClass) {
    return new EventBuilder<T>(eventClass, EventPriority.NORMAL);
  }

  public static <T extends Event> EventBuilder<T> newBuilder(final Class<T> eventClass,
      final EventPriority priority) {
    return new EventBuilder<T>(eventClass, priority);
  }

  public EventBuilder<T> filter(final Predicate<T> predicate) {
    if (predicate != null) {
      this.filters.add(predicate);
    }
    return this;
  }

  public EventBuilder<T> exceptionConsumer(final BiConsumer<? super T, Throwable> exceptionConsumer) {
    if (exceptionConsumer != null) {
      this.exceptionConsumer = exceptionConsumer;
    }
    return this;
  }

  public EventBuilder<T> handleSubclasses() {
    this.handleSubclasses = true;
    return this;
  }

  public List<BiConsumer<EventSubscriptionListener<T>, ? super T>> handlers() {
    return handlers;
  }

  public EventSubscriptionListener<T> handler(final Consumer<? super T> handler) {
    this.handlers.add(new ConsumerToBiConsumerSecond<>(handler));
    return register();
  }

  public EventSubscriptionListener<T> handler(final BiConsumer<EventSubscriptionListener<T>, ? super T> handler) {
    this.handlers.add(handler);
    return register();
  }

  public EventSubscriptionListener<T> register() {
    if (this.handlers.isEmpty()) {
      throw new IllegalStateException("No handlers have been registered");
    }
    return new EventSubscriptionListener<>(this, handlers);
  }

  public EventBuilder<T> expireIf(final BiPredicate<EventSubscriptionListener<T>, T> predicate,
      final ExpiryTestStage... testPoints) {
    if (predicate == null || testPoints == null || testPoints.length == 0) {
      return this;
    }
    for (final ExpiryTestStage testPoint : testPoints) {
      switch (testPoint) {
        case PRE:
          this.preExpiryTests.add(predicate);
          break;
        case POST_FILTER:
          this.midExpiryTests.add(predicate);
          break;
        case POST_HANDLE:
          this.postExpiryTests.add(predicate);
          break;
        default:
          throw new IllegalArgumentException("Unknown ExpiryTestPoint: " + testPoint);
      }
    }
    return this;
  }

  public EventBuilder<T> expireIf(final Predicate<T> predicate) {
    return expireIf(new PredicateToBiPredicate<>(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
  }

  public EventBuilder<T> expireAfter(final long duration, final TimeUnit unit) {
    if (unit == null) {
      return this;
    }
    Preconditions.checkArgument(duration >= 1, "duration < 1");
    final long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
    return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
  }

  public EventBuilder<T> expireAfter(final long maxCalls) {
    Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
    return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE,
        ExpiryTestStage.POST_HANDLE);
  }

  public static final class PredicateToBiPredicate<T, U>
      implements BiPredicate<T, U> {
    final Predicate<U> delegate;

    PredicateToBiPredicate(final Predicate<U> delegate) {
      this.delegate = delegate;
    }

    @Override
    public boolean test(final T t, final U u) {
      return this.delegate.test(u);
    }
  }

  private static final class ConsumerToBiConsumerSecond<T, U>
      implements BiConsumer<T, U> {
    final Consumer<U> delegate;

    ConsumerToBiConsumerSecond(final Consumer<U> delegate) {
      this.delegate = delegate;
    }

    @Override
    public void accept(final T t, final U u) {
      this.delegate.accept(u);
    }
  }

  public enum ExpiryTestStage {

    /**
     * The expiry predicate should be tested before the event is filtered or handled
     */
    PRE,

    /**
     * The expiry predicate should be tested after the subscriptions filters have
     * been evaluated
     */
    POST_FILTER,

    /**
     * The expiry predicate should be tested after the event has been handled
     */
    POST_HANDLE

  }
}
