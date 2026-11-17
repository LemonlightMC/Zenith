package com.lemonlightmc.zenith.additive.interfaces;

/**
 * Represents a type that can produce a typed clone of itself.
 *
 * @param <T> the type of the cloned value
 */
public interface Cloneable<T> extends java.lang.Cloneable {

  /**
   * Creates and returns a copy of this object.
   *
   * @return a clone of this object
   * @throws CloneNotSupportedException if cloning is not supported
   */
  T clone() throws CloneNotSupportedException;
}
