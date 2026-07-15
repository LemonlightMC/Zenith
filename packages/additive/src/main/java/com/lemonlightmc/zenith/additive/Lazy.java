package com.lemonlightmc.zenith.additive;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {

  protected volatile Supplier<T> supplier;
  protected volatile T value;

  public Lazy(final Supplier<T> supplier) {
    if (supplier == null) {
      throw new IllegalArgumentException("Lazy Supplier cant be null");
    }
    this.supplier = supplier;
  }

  /**
   * Creates a {@code Lazy} instance that obtains its value from the given
   * {@code Supplier}.
   * The supplier is invoked at most once, and the result is cached for subsequent
   * calls.
   *
   * @param <T>      the type of the lazy value
   * @param supplier the supplier providing the value
   * @return a new {@code Lazy} instance
   */
  public static <T> Lazy<T> of(final Supplier<T> supplier) {
    if (supplier == null) {
      throw new IllegalArgumentException("Supplier cant be null");
    }
    if (supplier instanceof Lazy) {
      return (Lazy<T>) supplier;
    } else {
      return new Lazy<T>(supplier);
    }
  }

  /**
   * Creates a true <em>lazy value</em> of type {@code T}, implemented using a
   * {@linkplain java.lang.reflect.Proxy}
   * that delegates to a {@code Lazy} instance.
   *
   * @param supplier the supplier providing the value when needed
   * @param type     the interface class that the proxy should implement
   * @param <T>      the type of the lazy value
   * @return a new proxy instance of type {@code T} that evaluates lazily
   */
  @SuppressWarnings("unchecked")
  public static <T> T val(final Supplier<T> supplier, final Class<T> type) {
    if (supplier == null) {
      throw new IllegalArgumentException("Lazy Supplier cant be null");
    }
    if (type == null) {
      throw new IllegalArgumentException("Lazy Type cant be null");
    }
    if (!type.isInterface()) {
      throw new IllegalArgumentException("Lazy Type has to be an interface");
    }
    final Lazy<T> lazy = Lazy.of(supplier);
    final InvocationHandler handler = (proxy, method, args) -> method.invoke(lazy.get(), args);
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
  }

  @Override
  public T get() {
    Supplier<T> current = supplier;
    if (current != null) {
      synchronized (this) {
        current = supplier;
        if (current != null) {
          value = current.get();
          supplier = null;
        }
      }
    }
    return value;
  }

  public Optional<T> asOptional() {
    return Optional.ofNullable(this.value);
  }

  public boolean isEvaluated() {
    return supplier == null;
  }

  public boolean isPresent() {
    return value != null;
  }

  public boolean isEmpty() {
    return value == null;
  }

  public Lazy<T> consume(final Consumer<? super T> action) {
    Objects.requireNonNull(action, "action is null");
    action.accept(get());
    return this;
  }

  @Override
  public int hashCode() {
    final int result = 31 + ((supplier == null) ? 0 : supplier.hashCode());
    return 31 * result + ((value == null) ? 0 : value.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Lazy<?> other = (Lazy<?>) obj;
    if (supplier == null) {
      if (other.supplier != null) {
        return false;
      }
    } else if (!supplier.equals(other.supplier)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Lazy [supplier=" + supplier + ", value=" + value + "]";
  }
}
