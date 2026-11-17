package com.lemonlightmc.zenith.additive.interfaces;

/**
 * Represents a value that can be serialized to JSON.
 */
public interface Serializable {

  /**
   * Serializes this value to its JSON representation.
   *
   * @return the JSON representation of this value
   */
  String toJson();
}
