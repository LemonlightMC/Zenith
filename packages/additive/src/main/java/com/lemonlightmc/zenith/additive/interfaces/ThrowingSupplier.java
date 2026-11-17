package com.lemonlightmc.zenith.additive.interfaces;

/**
 * A supplier operation that may throw a checked exception.
 *
 * @param <T> the supplied value type
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

  /**
   * Gets a result.
   *
   * @return the supplied result
   * @throws Exception if the operation fails
   */
  T get() throws Exception;
}