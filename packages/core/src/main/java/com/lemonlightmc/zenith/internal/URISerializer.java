package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.net.URI;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public final class URISerializer implements TypeSerializer<URI> {

  public static final URISerializer INSTANCE = new URISerializer();

  private URISerializer() {
  }

  @Override
  public URI deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    final String raw = node.getString();
    if (raw == null) {
      return null;
    }
    final String value = raw.trim();
    if (value.isEmpty()) {
      return null;
    }
    try {
      return URI.create(value);
    } catch (final Exception ex) {
      throw new SerializationException(node, URI.class, "Invalid URI: " + raw, ex);
    }
  }

  @Override
  public void serialize(final Type type, final URI obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.set(obj.toString());
  }
}
