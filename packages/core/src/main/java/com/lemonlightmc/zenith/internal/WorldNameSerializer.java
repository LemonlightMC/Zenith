package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

/**
 * Serializes and deserializes a {@link World} by its name.
 */
public final class WorldNameSerializer implements TypeSerializer<World> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final WorldNameSerializer INSTANCE = new WorldNameSerializer();

  private WorldNameSerializer() {
  }

  /**
   * Deserializes a world reference from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the resolved world, or {@code null} if unavailable
   * @throws SerializationException if deserialization fails
   */
  @Override
  public World deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    String name = null;
    if (node.isMap()) {
      name = node.node("name").getString(node.node("world").getString(node.node("value").getString()));
    } else {
      name = node.getString();
    }

    if (name == null) {
      return null;
    }
    name = name.trim();
    if (name.isEmpty()) {
      return null;
    }
    return Bukkit.getWorld(name);
  }

  /**
   * Serializes a world reference to a configuration node.
   *
   * @param type the source type
   * @param obj the world to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final World obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.set(obj.getName());
  }
}
