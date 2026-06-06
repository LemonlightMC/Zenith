package com.lemonlightmc.zenith.utils;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.lemonlightmc.zenith.time.PolyTimeUnit;

public class Lazy<@Nullable T> implements Supplier<T> {

  private Supplier<T> supplier;
  private volatile T value;
  private volatile long expiryTime;
  private final long duration;

  public Lazy(final Supplier<T> supplier, final long duration, final PolyTimeUnit unit) {
    if (supplier == null) {
      throw new IllegalArgumentException("Supplier cant be null");
    }
    this.supplier = supplier;
    this.duration = unit == null || duration < 0 ? -1L : unit.toNanos(duration);
  }

  public Lazy(final Supplier<T> supplier, final long duration) {
    this(supplier, duration, PolyTimeUnit.NANOSSECONDS);
  }

  public Lazy(final Supplier<T> supplier) {
    this(supplier, -1l, null);
  }

  public static <@Nullable E> Lazy<E> from(final Supplier<E> supplier) {
    return new Lazy<E>(supplier);
  }

  @Override
  public T get() {
    T result = value;
    final long nanos = System.nanoTime();

    if (supplier != null || nanos > expiryTime) {
      synchronized (this) {
        if (supplier != null || nanos > expiryTime) {
          this.value = result = supplier.get();
          this.supplier = null;
          this.expiryTime = nanos + duration;
        }
      }
    }
    return result;
  }

  public Optional<T> asOptional() {
    return Optional.ofNullable(this.value);
  }

  public boolean isInitialized() {
    return supplier == null;
  }

  public boolean isPresent() {
    return value != null;
  }

  public boolean isEmpty() {
    return value == null;
  }

  @Override
  public int hashCode() {
    int result = 31 + ((supplier == null) ? 0 : supplier.hashCode());
    result = 31 * result + ((value == null) ? 0 : value.hashCode());
    result = 31 * result + (int) (duration ^ (duration >>> 32));
    return 31 * result + (int) (expiryTime ^ (expiryTime >>> 32));
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
    return duration == other.duration && expiryTime == other.expiryTime;
  }

  @Override
  public String toString() {
    return "Lazy [supplier=" + supplier + ", value=" + value + ", duration=" + duration + ", expiryTime=" + expiryTime
        + "]";
  }
}
