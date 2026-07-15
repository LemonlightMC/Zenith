package com.lemonlightmc.zenith.additive;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.function.Supplier;

import com.lemonlightmc.zenith.additive.time.PolyTimeUnit;

public class ExpiringLazy<T> extends Lazy<T> {

  private volatile long expiryTime;
  private volatile boolean initialized;
  private final long duration;

  public ExpiringLazy(final Supplier<T> supplier, final long duration, final PolyTimeUnit unit) {
    super(supplier);
    this.duration = unit == null || duration < 0 ? -1L : unit.toNanos(duration);
  }

  public ExpiringLazy(final Supplier<T> supplier, final long duration) {
    this(supplier, duration, PolyTimeUnit.NANOSSECONDS);
  }

  public ExpiringLazy(final Supplier<T> supplier) {
    this(supplier, -1l, null);
  }

  /**
   * Creates a {@code ExpiringLazy} instance that obtains its value from the given
   * {@code Supplier}.
   * The supplier is invoked at most once, and the result is cached for subsequent
   * calls.
   *
   * @param <T>      the type of the lazy value
   * @param supplier the supplier providing the value
   * @return a new {@code ExpiringLazy} instance
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
   * Creates a true <em>lazy value</em> of type {@code T}, implemented using a
   * {@linkplain java.lang.reflect.Proxy}
   * that delegates to a {@code ExpiringLazy} instance.
   *
   * @param supplier the supplier providing the value when needed
   * @param type     the interface class that the proxy should implement
   * @param <T>      the type of the lazy value
   * @return a new proxy instance of type {@code T} that evaluates lazily
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

  public Optional<T> asOptional() {
    return Optional.ofNullable(this.value);
  }

  public boolean isInitialized() {
    return initialized;
  }

  public boolean isPresent() {
    return value != null;
  }

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
