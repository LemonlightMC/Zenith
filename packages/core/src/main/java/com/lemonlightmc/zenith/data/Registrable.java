package com.lemonlightmc.zenith.data;

/**
 * Represents an object that can be registered in a {@link Registry} by a key.
 *
 * @param <K> the key type used to identify the registrable object
 */
public interface Registrable<K> {

  /**
   * Returns the key that uniquely identifies this registrable object.
   *
   * @return the registration key
   */
  public K getKey();

  /**
   * Called when this object is registered.
   */
  public default void onRegister() {
  }

  /**
   * Called when this object is removed from a registry.
   */
  public default void onRemove() {
  }
}
