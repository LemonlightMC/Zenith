package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

/**
 * Serializes and deserializes {@link Material} values.
 */
public final class MaterialSerializer implements TypeSerializer<Material> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final MaterialSerializer INSTANCE = new MaterialSerializer();

  private MaterialSerializer() {
  }

  /**
   * Deserializes a material from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the resolved material, or {@code null} if unavailable
   * @throws SerializationException if the value cannot be resolved to a material
   */
  @Override
  public Material deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    String raw = null;
    if (node.isMap()) {
      raw = node.node("material").getString(
          node.node("type").getString(node.node("id").getString(
              node.node("key").getString(node.node("value").getString()))));
    } else {
      raw = node.getString();
    }
    if (raw == null || raw.isEmpty()) {
      return null;
    }
    raw = raw.trim();
    if (raw.isEmpty()) {
      return null;
    }

    // Try namespaced key form
    if (raw.indexOf(':') >= 0) {
      final NamespacedKey key = NamespacedKey.fromString(raw.toLowerCase(Locale.ROOT));
      if (key != null) {
        final Material byKey = Material.matchMaterial(key.toString());
        if (byKey != null) {
          return byKey;
        }
        final Material byKeyName = Material.matchMaterial(key.getKey());
        if (byKeyName != null) {
          return byKeyName;
        }
      }
    }

    final Material material = Material.matchMaterial(raw);
    if (material != null) {
      return material;
    }

    try {
      return Material.valueOf(raw.toUpperCase(Locale.ROOT).replace('-', '_').replace('.', '_').replace(' ', '_'));
    } catch (final Exception ex) {
      throw new SerializationException(node, Material.class, "Unknown material: " + raw, ex);
    }
  }

  /**
   * Serializes a material to a configuration node.
   *
   * @param type the source type
   * @param obj the material to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final Material obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    final NamespacedKey key = obj.getKeyOrNull();
    node.set(key == null ? obj.name() : key.toString());
  }
}
