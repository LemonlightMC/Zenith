package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

/**
 * Serializes/deserializes a {@link World} by its name.
 */
public final class WorldNameSerializer implements TypeSerializer<World> {

  public static final WorldNameSerializer INSTANCE = new WorldNameSerializer();

  private WorldNameSerializer() {
  }

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
