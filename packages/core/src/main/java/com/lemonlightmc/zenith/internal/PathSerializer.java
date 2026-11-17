package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class PathSerializer implements TypeSerializer<Path> {

  public static final PathSerializer INSTANCE = new PathSerializer();

  private PathSerializer() {
  }

  @Override
  public Path deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    String raw = node.getString();
    if (raw == null) {
      return null;
    }
    raw = raw.trim();
    if (raw.isEmpty()) {
      return null;
    }
    try {
      return Paths.get(raw);
    } catch (final Exception ex) {
      throw new SerializationException(node, Path.class, "Invalid path: " + raw, ex);
    }
  }

  @Override
  public void serialize(final Type type, final Path obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    // Use portable/normalized string form
    node.set(obj.toString().replace('\\', '/'));
  }
}
