package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.UUID;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.UUIDUtils;

public final class UUIDSerializer implements TypeSerializer<UUID> {

  public static final UUIDSerializer INSTANCE = new UUIDSerializer();

  private UUIDSerializer() {
  }

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
