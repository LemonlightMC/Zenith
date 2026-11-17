package com.lemonlightmc.zenith.additive;

import java.util.Optional;

/**
 * A mutable reference holder for a single value.
 *
 * @param <T> the referenced value type
 */
public class Ref<T> {
  /** The referenced value. */
  public T value;

  /**
   * Creates a new reference containing the given value.
   *
   * @param <T> the referenced value type
   * @param value the initial value
   * @return a new reference containing the given value
   */
  public static <T> Ref<T> of(final T value) {
    return new Ref<T>(value);
  }

  /**
   * Creates a new reference containing the given value.
   *
   * @param value the initial value
   */
  public Ref(final T value) {
    this.value = value;
  }

  /**
   * Returns whether the referenced value is non-{@code null}.
   *
   * @return {@code true} if the referenced value is present
   */
  public boolean isPresent() {
    return value != null;
  }

  /**
   * Returns the referenced value.
   *
   * @return the referenced value
   */
  public T get() {
    return value;
  }

  /**
   * Updates the referenced value.
   *
   * @param newValue the new value to store
   */
  public void set(final T newValue) {
    this.value = newValue;
  }

  /**
   * Returns the referenced value as an {@link Optional}.
   *
   * @return an {@link Optional} containing the referenced value
   */
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
