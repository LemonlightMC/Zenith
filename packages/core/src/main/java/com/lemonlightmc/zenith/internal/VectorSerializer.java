package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

public final class VectorSerializer implements TypeSerializer<Vector> {

  public static final VectorSerializer INSTANCE = new VectorSerializer();

  private VectorSerializer() {
  }

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
