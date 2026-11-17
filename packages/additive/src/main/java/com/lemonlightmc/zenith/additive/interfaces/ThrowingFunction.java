package com.lemonlightmc.zenith.additive.interfaces;

/**
 * A function operation that may throw a checked exception.
 *
 * @param <T> the input value type
 * @param <R> the result value type
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {

  /**
   * Applies this function to the given argument.
   *
   * @param t the input argument
   * @return the function result
   * @throws Exception if the operation fails
   */
  R apply(T t) throws Exception;
}