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

/**
 * Fluent builder for configuring and registering event subscriptions.
 *
 * @param <T> the event type handled by the subscription
 */
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

  /**
   * Creates a new event builder for the specified event type and priority.
   *
   * @param eventClass the event class to listen for
   * @param priority the listener priority, or {@code null} for {@link EventPriority#NORMAL}
   * @throws IllegalArgumentException if {@code eventClass} is {@code null}
   */
  public EventBuilder(final Class<T> eventClass, final EventPriority priority) {
    if (eventClass == null) {
      throw new IllegalArgumentException("EventClass must be defined");
    }
    this.eventClass = eventClass;
    this.priority = priority == null ? EventPriority.NORMAL : priority;
  }

  /**
   * Creates a new event builder with normal priority.
   *
   * @param eventClass the event class to listen for
   * @param <T> the event type
   * @return a new event builder
   */
  public static <T extends Event> EventBuilder<T> newBuilder(final Class<T> eventClass) {
    return new EventBuilder<T>(eventClass, EventPriority.NORMAL);
  }

  /**
   * Creates a new event builder with the specified priority.
   *
   * @param eventClass the event class to listen for
   * @param priority the listener priority
   * @param <T> the event type
   * @return a new event builder
   */
  public static <T extends Event> EventBuilder<T> newBuilder(final Class<T> eventClass,
      final EventPriority priority) {
    return new EventBuilder<T>(eventClass, priority);
  }

  /**
   * Adds a filter that must pass before handlers are invoked.
   *
   * @param predicate the filter predicate
   * @return this builder for chaining
   */
  public EventBuilder<T> filter(final Predicate<T> predicate) {
    if (predicate != null) {
      this.filters.add(predicate);
    }
    return this;
  }

  /**
   * Sets the consumer used to handle exceptions thrown by handlers.
   *
   * @param exceptionConsumer the exception consumer
   * @return this builder for chaining
   */
  public EventBuilder<T> exceptionConsumer(final BiConsumer<? super T, Throwable> exceptionConsumer) {
    if (exceptionConsumer != null) {
      this.exceptionConsumer = exceptionConsumer;
    }
    return this;
  }

  /**
   * Enables handling of subclasses of the configured event type.
   *
   * @return this builder for chaining
   */
  public EventBuilder<T> handleSubclasses() {
    this.handleSubclasses = true;
    return this;
  }

  /**
   * Returns the handlers configured on this builder.
   *
   * @return the configured handlers
   */
  public List<BiConsumer<EventSubscriptionListener<T>, ? super T>> handlers() {
    return handlers;
  }

  /**
   * Registers a handler for the event and creates the subscription.
   *
   * @param handler the event handler
   * @return the registered event subscription listener
   */
  public EventSubscriptionListener<T> handler(final Consumer<? super T> handler) {
    this.handlers.add(new ConsumerToBiConsumerSecond<>(handler));
    return register();
  }

  /**
   * Registers a handler that receives both the subscription and the event.
   *
   * @param handler the event handler
   * @return the registered event subscription listener
   */
  public EventSubscriptionListener<T> handler(final BiConsumer<EventSubscriptionListener<T>, ? super T> handler) {
    this.handlers.add(handler);
    return register();
  }

  /**
   * Registers the configured subscription with at least one handler.
   *
   * @return the registered event subscription listener
   * @throws IllegalStateException if no handlers have been configured
   */
  public EventSubscriptionListener<T> register() {
    if (this.handlers.isEmpty()) {
      throw new IllegalStateException("No handlers have been registered");
    }
    return new EventSubscriptionListener<>(this, handlers);
  }

  /**
   * Adds an expiry test evaluated at the specified stages.
   *
   * @param predicate the expiry predicate
   * @param testPoints the stages at which to evaluate the predicate
   * @return this builder for chaining
   */
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

  /**
   * Adds an expiry condition evaluated before filtering and after handling.
   *
   * @param predicate the expiry predicate
   * @return this builder for chaining
   */
  public EventBuilder<T> expireIf(final Predicate<T> predicate) {
    return expireIf(new PredicateToBiPredicate<>(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
  }

  /**
   * Expires the subscription after the specified duration.
   *
   * @param duration the duration amount
   * @param unit the time unit of the duration
   * @return this builder for chaining
   */
  public EventBuilder<T> expireAfter(final long duration, final TimeUnit unit) {
    if (unit == null) {
      return this;
    }
    Preconditions.checkArgument(duration >= 1, "duration < 1");
    final long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
    return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
  }

  /**
   * Expires the subscription after the specified number of handled calls.
   *
   * @param maxCalls the maximum number of calls
   * @return this builder for chaining
   */
  public EventBuilder<T> expireAfter(final long maxCalls) {
    Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
    return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE,
        ExpiryTestStage.POST_HANDLE);
  }

  /**
   * Adapts a single-argument predicate to a bi-predicate by ignoring the first argument.
   *
   * @param <T> the ignored first argument type
   * @param <U> the tested argument type
   */
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

  /**
   * Stages at which expiry predicates may be evaluated.
   */
  public enum ExpiryTestStage {

    /**
     * Evaluates the expiry predicate before filtering or handling.
     */
    PRE,

    /**
     * Evaluates the expiry predicate after filters have been applied.
     */
    POST_FILTER,

    /**
     * Evaluates the expiry predicate after handlers have been invoked.
     */
    POST_HANDLE

  }
}
