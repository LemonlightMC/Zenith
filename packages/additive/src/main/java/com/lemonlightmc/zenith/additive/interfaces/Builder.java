package com.lemonlightmc.zenith.additive.interfaces;

/**
 * Builds a value without requiring input parameters.
 *
 * @param <O> the type of value produced by this builder
 */
public interface Builder<O> {

  /**
   * Builds and returns a value.
   *
   * @return the built value
   */
  O build();
}
