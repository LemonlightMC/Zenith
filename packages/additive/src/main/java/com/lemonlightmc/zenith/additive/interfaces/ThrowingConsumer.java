package com.lemonlightmc.zenith.additive.interfaces;

/**
 * A consumer operation that may throw a checked exception.
 *
 * @param <T> the input value type
 */
@FunctionalInterface
public interface ThrowingConsumer<T> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   * @throws Exception if the operation fails
   */
  void accept(T t) throws Exception;
}