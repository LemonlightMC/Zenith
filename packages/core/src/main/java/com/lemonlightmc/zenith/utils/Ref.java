package com.lemonlightmc.zenith.utils;

import java.util.Optional;

public class Ref<T> {
  public T value;

  public static <T> Ref<T> of(final T value) {
    return new Ref<T>(value);
  }

  public Ref(final T value) {
    this.value = value;
  }

  public boolean isPresent() {
    return value != null;
  }

  public T get() {
    return value;
  }

  public void set(final T newValue) {
    this.value = newValue;
  }

  public Optional<T> asOptional() {
    return Optional.ofNullable(value);
  }

  @Override
  public int hashCode() {
    return 31 + (value == null ? 0 : value.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Ref<?> other = (Ref<?>) obj;
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
    return "Ref [value=" + value + "]";
  }
}
