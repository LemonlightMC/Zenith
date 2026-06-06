package com.lemonlightmc.zenith.utils;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public class FastLazy<@Nullable T> implements Supplier<T> {

  private Supplier<T> supplier;
  private T value;

  public FastLazy(final Supplier<T> supplier) {
    if (supplier == null) {
      throw new IllegalArgumentException("Supplier cant be null");
    }
    this.supplier = supplier;
  }

  public static <@Nullable E> Lazy<E> from(final Supplier<E> supplier) {
    return new Lazy<E>(supplier);
  }

  @Override
  public T get() {
    if (supplier != null) {
      this.value = supplier.get();
      supplier = null;
    }
    return this.value;
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

  public boolean isEmptry() {
    return value == null;
  }

  @Override
  public int hashCode() {
    int result = 31 + ((supplier == null) ? 0 : supplier.hashCode());
    return 31 * result + ((value == null) ? 0 : value.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final FastLazy<?> other = (FastLazy<?>) obj;
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
    return "FastLazy [supplier=" + supplier + ", value=" + value + "]";
  }
}
