package com.lemonlightmc.zenith.additive.interfaces;

/**
 * A predicate operation that may throw a checked exception.
 *
 * @param <T> the input value type
 */
@FunctionalInterface
public interface ThrowingPredicate<T> {

  /**
   * Evaluates this predicate on the given argument.
   *
   * @param t the input argument
   * @return {@code true} if the argument matches the predicate
   * @throws Exception if the operation fails
   */
  boolean test(T t) throws Exception;
}