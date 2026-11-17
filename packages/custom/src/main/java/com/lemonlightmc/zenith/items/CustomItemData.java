package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;

/**
 * Describes the identity, presentation, and behavior flags of a custom item.
 *
 * <p>This data includes the item's identifier, display name, lore, base material, and associated properties.
 */
public class CustomItemData {

  private String id;
  private String name;
  private List<String> lore;
  private Material material;
  private Set<CustomItemProperty> properties;

  /**
   * Creates custom item metadata with the provided identity and presentation values.
   *
   * @param id the custom item identifier
   * @param name the display name of the item
   * @param lore the lore lines for the item, or {@code null} for an empty list
   * @param material the base material of the item
   */
  public CustomItemData(String id, String name, List<String> lore,
      Material material) {
    this.id = id;
    this.name = name;
    this.lore = lore == null ? new ArrayList<>() : lore;
    this.material = material;
    properties = new HashSet<>();
  }

  /**
   * Returns the custom item identifier.
   *
   * @return the item identifier
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the display name of the custom item.
   *
   * @return the display name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the base material of the custom item.
   *
   * @return the base material
   */
  public Material getMaterial() {
    return material;
  }

  /**
   * Returns the lore lines associated with the custom item.
   *
   * @return the lore lines
   */
  public List<String> getLore() {
    return lore;
  }

  /**
   * Returns the properties associated with the custom item.
   *
   * @return the custom item properties
   */
  public Set<CustomItemProperty> getProperties() {
    return properties;
  }

  private boolean uniqueMaterial = true;

  /**
   * Returns whether this item requires a unique material match.
   *
   * @return {@code true} if the material must be unique; {@code false} otherwise
   */
  public boolean isUniqueMaterial() {
    return uniqueMaterial;
  }

  /**
   * Marks this item as not requiring a unique material match.
   *
   * @return this item data instance for chaining
   */
  public CustomItemData notUniqueMaterial() {
    uniqueMaterial = false;

    return this;
  }

  /**
   * Returns whether generated item instances should receive a unique identifier.
   *
   * @return {@code true} if each instance should have a unique identifier; {@code false} otherwise
   */
  public boolean isUniqueId() {
    return uniqueId;
  }

  private boolean uniqueId = false;

  /**
   * Enables assignment of a unique identifier to generated item instances.
   *
   * @return this item data instance for chaining
   */
  public CustomItemData withUUID() {
    uniqueId = true;

    return this;
  }

  /**
   * Adds a property to this custom item.
   *
   * @param property the property to add
   * @return this item data instance for chaining
   */
  public CustomItemData addProperty(CustomItemProperty property) {
    properties.add(property);
    return this;
  }

  /**
   * Adds multiple properties to this custom item.
   *
   * @param properties the properties to add
   * @return this item data instance for chaining
   */
  public CustomItemData addProperties(CustomItemProperty... properties) {
    for (CustomItemProperty prop : properties) {
      addProperty(prop);
    }
    return this;
  }

  /**
   * Checks whether a property with the given identifier is present.
   *
   * @param id the property identifier to check
   * @return {@code true} if a matching property is present; {@code false} otherwise
   */
  public boolean containProperty(String id) {
    return properties.stream().anyMatch((prop) -> prop.id().equals(id));
  }

  /**
   * Returns the property with the given identifier, if present.
   *
   * @param id the property identifier to find
   * @return the matching property, or {@code null} if none is found
   */
  public CustomItemProperty getProperty(String id) {
    for (CustomItemProperty prop : properties)
      if (prop.id().equals(id))
        return prop;

    return null;
  }

}
