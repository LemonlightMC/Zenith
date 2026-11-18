package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

/**
 * Serializes and deserializes {@link Location} values.
 */
public final class LocationSerializer implements TypeSerializer<Location> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final LocationSerializer INSTANCE = new LocationSerializer();

  private LocationSerializer() {
  }

  /**
   * Deserializes a location from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the parsed location, or {@code null} if unavailable
   * @throws SerializationException if the value cannot be parsed as a location
   */
  @Override
  public Location deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    if (!node.isMap()) {
      final String raw = node.getString();
      if (raw == null || raw.trim().isEmpty()) {
        return null;
      }
      return parseStringLocation(node, raw.trim());
    }

    final String worldName = node.node("world").getString(node.node("w").getString());
    return new Location(worldName == null || worldName.isEmpty() ? null : Bukkit.getWorld(worldName),
        node.node("x").getDouble(0.0),
        node.node("y").getDouble(0.0),
        node.node("z").getDouble(0.0),
        (float) node.node("yaw").getDouble(0.0),
        (float) node.node("pitch").getDouble(0.0));
  }

  private static Location parseStringLocation(final ConfigurationNode node, final String raw)
      throws SerializationException {
    final String[] parts = raw.split("[ ,:]");
    if (parts.length < 3) {
      throw new SerializationException(node, Location.class,
          "Location string must be '[name:]x:y:z[:yaw[:pitch]]', '[name] x y z [yaw [pitch]]', or '[name,]x,y,z[,yaw[,pitch]]': "
              + raw);
    }

    try {
      int offset = 0;
      final World world;
      if (hasWorldToken(parts)) {
        offset = 1;
        final String worldName = parts[0].trim();
        world = worldName.isEmpty() ? null : Bukkit.getWorld(worldName);
      } else {
        world = null;
      }

      if (parts.length - offset < 3) {
        throw new SerializationException(node, Location.class, "Location string is missing coordinates: " + raw);
      }

      return new Location(world,
          NumberConversions.parseDouble(parts[offset].trim()),
          NumberConversions.parseDouble(parts[offset + 1].trim()),
          NumberConversions.parseDouble(parts[offset + 2].trim()),
          parts.length > offset ? NumberConversions.parseFloat(parts[offset + 3].trim()) : 0.0f,
          parts.length > offset ? NumberConversions.parseFloat(parts[offset + 4].trim()) : 0.0f);
    } catch (final SerializationException ex) {
      throw ex;
    } catch (final Exception ex) {
      throw new SerializationException(node, Location.class, "Invalid location string: " + raw, ex);
    }
  }

  private static boolean hasWorldToken(final String[] parts) {
    if (parts.length <= 3) {
      return false; // x y z
    }
    if (parts.length == 4) {
      // "world x y z" vs "x y z yaw" -> prefer world when first token is not numeric.
      return !NumberConversions.isNumeric(parts[0]);
    }
    if (parts.length == 5) {
      // Ambiguous: "world x y z yaw" vs "x y z yaw pitch"
      // -> prefer world when first token is not numeric.
      return !NumberConversions.isNumeric(parts[0]);
    }
    // 6+ tokens -> assume world is present as the first token.
    return true;
  }

  /**
   * Serializes a location to a configuration node.
   *
   * @param type the source type
   * @param obj the location to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final Location obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    final World world = obj.getWorld();
    if (world != null) {
      node.node("world").set(world.getName());
    }
    node.node("x").set(obj.getX());
    node.node("y").set(obj.getY());
    node.node("z").set(obj.getZ());
    if (obj.getYaw() != 0.0f) {
      node.node("yaw").set(obj.getYaw());
    }
    if (obj.getPitch() != 0.0f) {
      node.node("pitch").set(obj.getPitch());
    }
  }
}
