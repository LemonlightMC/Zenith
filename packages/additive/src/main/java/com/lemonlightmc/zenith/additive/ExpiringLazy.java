package com.lemonlightmc.zenith.additive;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.Supplier;

import com.lemonlightmc.zenith.additive.time.PolyTimeUnit;

/**
 * A lazily initialized supplier whose cached value can expire after a configured duration.
 *
 * <p>When no positive duration is configured, the value behaves like a regular lazy value and is
 * computed at most once. When a duration is configured, the cached value is recomputed after the
 * expiry time has passed.
 *
 * @param <T> the type of the lazily supplied value
 */
public class ExpiringLazy<T> extends Lazy<T> {

  private volatile long expiryTime;
  private volatile boolean initialized;
  private final long duration;

  /**
   * Creates a new expiring lazy value.
   *
   * @param supplier the supplier providing the value
   * @param duration the cache duration
   * @param unit the time unit for the cache duration, or {@code null} to disable expiration
   */
  public ExpiringLazy(final Supplier<T> supplier, final long duration, final PolyTimeUnit unit) {
    super(supplier);
    this.duration = unit == null || duration < 0 ? -1L : unit.toNanos(duration);
  }

  /**
   * Creates a new expiring lazy value using nanoseconds for the duration.
   *
   * @param supplier the supplier providing the value
   * @param duration the cache duration in nanoseconds
   */
  public ExpiringLazy(final Supplier<T> supplier, final long duration) {
    this(supplier, duration, PolyTimeUnit.NANOSSECONDS);
  }

  /**
   * Creates a new lazy value that never expires.
   *
   * @param supplier the supplier providing the value
   */
  public ExpiringLazy(final Supplier<T> supplier) {
    this(supplier, -1l, null);
  }

  /**
   * Creates an {@code ExpiringLazy} instance that obtains its value from the given supplier.
   *
   * @param <T> the type of the lazy value
   * @param supplier the supplier providing the value
   * @return a new {@code ExpiringLazy} instance
   * @throws IllegalArgumentException if {@code supplier} is {@code null}
   */
  public static <T> ExpiringLazy<T> of(final Supplier<T> supplier) {
    if (supplier == null) {
      throw new IllegalArgumentException("Supplier cant be null");
    }
    if (supplier instanceof Lazy) {
      return (ExpiringLazy<T>) supplier;
    } else {
      return new ExpiringLazy<T>(supplier);
    }
  }

  /**
   * Creates a lazily evaluated proxy value backed by an {@code ExpiringLazy} instance.
   *
   * @param supplier the supplier providing the value when needed
   * @param type the interface class that the proxy should implement
   * @param <T> the type of the lazy value
   * @return a proxy instance of type {@code T} that evaluates lazily
   * @throws IllegalArgumentException if {@code supplier} or {@code type} is {@code null}, or if
   *     {@code type} is not an interface
   */
  @SuppressWarnings("unchecked")
  public static <T> T val(final Supplier<T> supplier, final Class<T> type) {
    if (supplier == null) {
      throw new IllegalArgumentException("ExpiringLazy Supplier cant be null");
    }
    if (type == null) {
      throw new IllegalArgumentException("ExpiringLazy Type cant be null");
    }
    if (!type.isInterface()) {
      throw new IllegalArgumentException("ExpiringLazy Type has to be an interface");
    }
    final ExpiringLazy<T> lazy = ExpiringLazy.of(supplier);
    final InvocationHandler handler = (proxy, method, args) -> method.invoke(lazy.get(), args);
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
  }

  /**
   * Returns the current value, recomputing it when it has expired or has not yet been initialized.
   *
   * @return the current lazily computed value
   */
  @Override
  public T get() {
    if (initialized) {
      final T current = value;
      if (duration < 0L || System.nanoTime() <= expiryTime) {
        return current;
      }
    }

    synchronized (this) {
      if (!initialized || (duration >= 0L && System.nanoTime() > expiryTime)) {
        value = supplier.get();
        initialized = true;
        if (duration >= 0L) {
          expiryTime = System.nanoTime() + duration;
        }
      }
      return value;
    }
  }

  /**
   * Returns the currently cached value as an {@link Optional} without forcing initialization.
   *
   * @return an {@link Optional} containing the cached value, or empty if no value is cached
   */
  public Optional<T> asOptional() {
    return Optional.ofNullable(this.value);
  }

  /**
   * Returns whether a value has been initialized.
   *
   * @return {@code true} if a value has been computed at least once
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Returns whether a cached value is currently present.
   *
   * @return {@code true} if the cached value is non-{@code null}
   */
  public boolean isPresent() {
    return value != null;
  }

  /**
   * Returns whether no cached value is currently present.
   *
   * @return {@code true} if the cached value is {@code null}
   */
  public boolean isEmpty() {
    return value == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (int) (expiryTime ^ (expiryTime >>> 32));
    result = 31 * result + (initialized ? 1231 : 1237);
    return 31 * result + (int) (duration ^ (duration >>> 32));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
      return false;
    }
    final ExpiringLazy<?> other = (ExpiringLazy<?>) obj;
    return initialized == other.initialized && duration == other.duration;
  }

  @Override
  public String toString() {
    return "ExpiringLazy [expiryTime=" + expiryTime + ", supplier=" + supplier + ", initialized=" + initialized
        + ", value=" + value + ", duration=" + duration + "]";
  }
}
