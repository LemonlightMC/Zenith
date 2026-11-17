package com.lemonlightmc.zenith.items;

/**
 * Represents a named property that can be attached to a custom item.
 */
public interface CustomItemProperty {

  /**
   * Returns the normalized identifier for this property.
   *
   * @return the property identifier derived from its name
   */
  public default String id() {
    return name().replaceAll(" ", "").toLowerCase();
  }

  /**
   * Returns the display name of this property.
   *
   * @return the property name
   */
  public String name();

}
