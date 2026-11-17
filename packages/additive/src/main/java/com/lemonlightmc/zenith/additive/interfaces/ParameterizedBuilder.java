
package com.lemonlightmc.zenith.additive.interfaces;

/**
 * Builds a value from a provided input parameter.
 *
 * @param <I> the input parameter type
 * @param <O> the type of value produced by this builder
 */
public interface ParameterizedBuilder<I, O> {

  /**
   * Builds and returns a value using the given input.
   *
   * @param param the input parameter
   * @return the built value
   */
  O build(I param);
}
