package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.Locale;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class MaterialSerializer implements TypeSerializer<Material> {

  public static final MaterialSerializer INSTANCE = new MaterialSerializer();

  private MaterialSerializer() {
  }

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
