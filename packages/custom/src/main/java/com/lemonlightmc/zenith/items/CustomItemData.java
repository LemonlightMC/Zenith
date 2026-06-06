package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;

public class CustomItemData {

  private String id;
  private String name;
  private List<String> lore;
  private Material material;
  private Set<CustomItemProperty> properties;

  public CustomItemData(String id, String name, List<String> lore,
      Material material) {
    this.id = id;
    this.name = name;
    this.lore = lore == null ? new ArrayList<>() : lore;
    this.material = material;
    properties = new HashSet<>();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Material getMaterial() {
    return material;
  }

  public List<String> getLore() {
    return lore;
  }

  public Set<CustomItemProperty> getProperties() {
    return properties;
  }

  private boolean uniqueMaterial = true;

  public boolean isUniqueMaterial() {
    return uniqueMaterial;
  }

  public CustomItemData notUniqueMaterial() {
    uniqueMaterial = false;

    return this;
  }

  public boolean isUniqueId() {
    return uniqueId;
  }

  private boolean uniqueId = false;

  public CustomItemData withUUID() {
    uniqueId = true;

    return this;
  }

  public CustomItemData addProperty(CustomItemProperty property) {
    properties.add(property);
    return this;
  }

  public CustomItemData addProperties(CustomItemProperty... properties) {
    for (CustomItemProperty prop : properties) {
      addProperty(prop);
    }
    return this;
  }

  public boolean containProperty(String id) {
    return properties.stream().anyMatch((prop) -> prop.id().equals(id));
  }

  public CustomItemProperty getProperty(String id) {
    for (CustomItemProperty prop : properties)
      if (prop.id().equals(id))
        return prop;

    return null;
  }

}
