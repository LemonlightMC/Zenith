package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.bukkit.NamespacedKey;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

/**
 * Serializes and deserializes {@link NamespacedKey} values.
 */
public final class NamespacedKeySerializer implements TypeSerializer<NamespacedKey> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final NamespacedKeySerializer INSTANCE = new NamespacedKeySerializer();

  private NamespacedKeySerializer() {
  }

  /**
   * Deserializes a namespaced key from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the parsed namespaced key, or {@code null} if unavailable
   * @throws SerializationException if the value cannot be parsed as a namespaced key
   */
  @Override
  public NamespacedKey deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    if (!node.isMap()) {
      final String raw = node.getString();
      if (raw == null || raw.trim().isEmpty()) {
        return null;
      }
      return parseCombined(node, raw);
    }

    String key = node.node("key").getString(node.node("value").getString());
    if (key == null || key.isEmpty()) {
      throw new SerializationException(node, NamespacedKey.class, "NamespacedKey requires a key");
    }
    key = key.toLowerCase(java.util.Locale.ROOT);
    if (key.contains(":")) {
      return parseCombined(node, key);
    }

    String namespace = node.node("namespace").getString();
    namespace = namespace == null || namespace.isEmpty() ? NamespacedKey.MINECRAFT
        : namespace.toLowerCase(java.util.Locale.ROOT);

    try {
      return new NamespacedKey(namespace, key);
    } catch (final Exception ex) {
      throw new SerializationException(node, NamespacedKey.class, "Invalid namespaced key: " + namespace + ":" + key,
          ex);
    }
  }

  private static NamespacedKey parseCombined(final ConfigurationNode node, String raw)
      throws SerializationException {
    raw = raw.trim();
    try {
      final NamespacedKey parsed = NamespacedKey.fromString(raw);
      if (parsed != null) {
        return parsed;
      }
    } catch (final Exception ignored) {
    }
    try {
      return NamespacedKey.minecraft(raw.toLowerCase(java.util.Locale.ROOT));
    } catch (final Exception ex) {
      throw new SerializationException(node, NamespacedKey.class, "Invalid namespaced key: " + raw, ex);
    }
  }

  /**
   * Serializes a namespaced key to a configuration node.
   *
   * @param type the source type
   * @param obj the namespaced key to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final NamespacedKey obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.set(obj.toString());
  }
}
