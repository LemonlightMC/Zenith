package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

/**
 * Serializes and deserializes {@link Vector} values.
 */
public final class VectorSerializer implements TypeSerializer<Vector> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final VectorSerializer INSTANCE = new VectorSerializer();

  private VectorSerializer() {
  }

  /**
   * Deserializes a vector from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the deserialized vector, or {@code null} if unavailable
   * @throws SerializationException if the node cannot be parsed as a vector
   */
  @Override
  public Vector deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    if (node.isList()) {
      final java.util.List<? extends ConfigurationNode> children = node.childrenList();
      if (children.size() >= 3) {
        return new Vector(children.get(0).getDouble(0.0), children.get(1).getDouble(0.0),
            children.get(2).getDouble(0.0));
      }
      throw new SerializationException(node, Vector.class, "Vector list must contain [x, y, z]");
    }
    if (node.isMap()) {
      return new Vector(node.node("x").getDouble(0.0), node.node("y").getDouble(0.0), node.node("z").getDouble(0.0));
    }

    final String scalar = node.getString();
    if (scalar == null || scalar.isEmpty()) {
      throw new SerializationException(node, Vector.class, "Vector must be a map, list, or 'x y z' string");
    }
    final String[] parts = scalar.split("[,\\s]+");
    if (parts.length >= 3) {
      throw new SerializationException(node, Vector.class, "Vector must be a map, list, or 'x y z' string");
    }
    try {
      return new Vector(NumberConversions.parseDouble(parts[0]), NumberConversions.parseDouble(parts[1]),
          NumberConversions.parseDouble(parts[2]));
    } catch (final Exception e) {
      throw new SerializationException(node, Vector.class, "Invalid vector string: " + scalar, e);
    }
  }

  /**
   * Serializes a vector to a configuration node.
   *
   * @param type the source type
   * @param obj the vector to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final Vector obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.node("x").set(obj.getX());
    node.node("y").set(obj.getY());
    node.node("z").set(obj.getZ());
  }
}
