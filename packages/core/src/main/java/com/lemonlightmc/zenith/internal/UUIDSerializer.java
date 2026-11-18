package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.UUID;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.UUIDUtils;

/**
 * Serializes and deserializes {@link UUID} values.
 */
public final class UUIDSerializer implements TypeSerializer<UUID> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final UUIDSerializer INSTANCE = new UUIDSerializer();

  private UUIDSerializer() {
  }

  /**
   * Deserializes a UUID from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the parsed UUID, or {@code null} if unavailable
   * @throws SerializationException if the value cannot be parsed as a UUID
   */
  @Override
  public UUID deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    final String raw = node.getString();
    try {
      return UUIDUtils.parseUUID(raw);
    } catch (final IllegalArgumentException ignored) {
    }
    throw new SerializationException(node, UUID.class, "Invalid UUID: " + raw);
  }

  /**
   * Serializes a UUID to a configuration node.
   *
   * @param type the source type
   * @param obj the UUID to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final UUID obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.set(obj.toString());
  }
}
