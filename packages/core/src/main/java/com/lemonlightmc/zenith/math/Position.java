package com.lemonlightmc.zenith.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.interfaces.JsonBuilder;
import com.lemonlightmc.zenith.json.GsonSerializable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;
import java.lang.reflect.Type;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * An immutable and serializable location object
 */
public final class Position implements GsonSerializable {
  public static Position deserialize(final JsonElement element) {
    if (!element.isJsonObject()) {
      throw new IllegalArgumentException("Element is not a JsonObject");
    }
    final JsonObject object = element.getAsJsonObject();
    if (!object.has("x") || !object.has("y") || !object.has("z") || !object.has("world")) {
      throw new IllegalArgumentException("Element is missing x, y, z, or world");
    }
    return of(object.get("world").getAsString(), object.get("x").getAsDouble(), object.get("y").getAsDouble(),
        object.get("z").getAsDouble());
  }

  public static Position of(final String world, final double x, final double y, final double z) {
    return new Position(world, x, y, z);
  }

  public static Position of(final String world, final String x, final String y, final String z) {
    return new Position(world, x, y, z);
  }

  public static Position of(final World world, final double x, final double y, final double z) {
    return new Position(world, x, y, z);
  }

  public static Position of(final Location location) {
    return new Position(location);

  }

  public static Position of(final Block block) {
    return new Position(block);
  }

  public static Position of(final Position position) {
    return new Position(position);
  }

  private final double x;
  private final double y;
  private final double z;
  private final Reference<World> world;

  private volatile Location bukkitLocation = null;

  public Position(final Reference<World> world, final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world;
  }

  public Position(final World world, final double x, final double y, final double z) {
    this(world != null ? new WeakReference<>(world) : null, x, y, z);
  }

  public Position(final String world, final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    if (world != null && world.isBlank()) {
      this.world = null;
    } else {
      final World world2 = Bukkit.getServer().getWorld(world.trim());
      this.world = world2 == null ? null : new WeakReference<>(world2);
    }
  }

  public Position(final String world, final String x, final String y, final String z) {
    this.x = NumberConversions.parseDouble(x);
    this.y = NumberConversions.parseDouble(y);
    this.z = NumberConversions.parseDouble(z);
    if (world != null && world.isBlank()) {
      this.world = null;
    } else {
      final World world2 = Bukkit.getServer().getWorld(world.trim());
      this.world = world2 == null ? null : new WeakReference<>(world2);
    }
  }

  public Position(final Location location) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    this.x = location.getX();
    this.y = location.getY();
    this.z = location.getZ();
    this.world = new WeakReference<>(location.getWorld());
  }

  public Position(final Block block) {
    if (block == null) {
      throw new IllegalArgumentException("Block cannot be null");
    }
    this.x = block.getX();
    this.y = block.getY();
    this.z = block.getZ();
    this.world = new WeakReference<>(block.getWorld());
  }

  public Position(final Position position) {
    if (position == null) {
      throw new IllegalArgumentException("Block cannot be null");
    }
    this.x = position.x;
    this.y = position.y;
    this.z = position.z;
    this.world = position.world;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public double getZ() {
    return this.z;
  }

  public World getWorld() {
    if (this.world == null) {
      return null;
    }

    final World world = this.world.get();
    if (world == null) {
      throw new IllegalArgumentException("World is unloaded");
    }
    return world;
  }

  public Location toLocation() {
    if (this.bukkitLocation == null) {
      this.bukkitLocation = new Location(getWorld(), this.x, this.y, this.z);
    }

    return this.bukkitLocation.clone();
  }

  public Block toBlock() {
    return toLocation().getBlock();
  }

  public Position toBlockPosition() {
    return new Position(getWorld(), Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
  }

  public Chunk toChunk() {
    return toLocation().getChunk();
  }

  public Position getRelative(final BlockFace face) {
    if (face == null) {
      throw new IllegalArgumentException("BlockFace cannot be null");
    }
    return new Position(this.world, this.x + face.getModX(), this.y + face.getModY(), this.z + face.getModZ());
  }

  public Position getRelative(final BlockFace face, final double distance) {
    if (face == null) {
      throw new IllegalArgumentException("BlockFace cannot be null");
    }
    return new Position(
        this.world, this.x + (face.getModX() * distance), this.y + (face.getModY() * distance),
        this.z + (face.getModZ() * distance));
  }

  public Position add(final double x, final double y, final double z) {
    return new Position(this.world, this.x + x, this.y + y, this.z + z);
  }

  public Position subtract(final double x, final double y, final double z) {
    return new Position(this.world, this.x - x, this.y - y, this.z - z);
  }

  public Region regionWith(final Position other) {
    return Region.of(this, other);
  }

  @Override
  public JsonObject serialize() {
    return JsonBuilder.object()
        .add("x", this.x)
        .add("y", this.y)
        .add("z", this.z)
        .add("world", getWorld().getName())
        .build();
  }

  @Override
  public int hashCode() {
    long temp;
    temp = Double.doubleToLongBits(x);
    int result = 31 + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return 31 * result + ((world == null) ? 0 : world.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Position other = (Position) obj;
    if (world == null) {
      if (other.world != null) {
        return false;
      }
    } else if (!world.equals(other.world)) {
      return false;
    }
    return Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
        && Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z)
        && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
  }

  @Override
  public String toString() {
    return "Position(x=" + x + ", y=" + y + ", z=" + z + ", world=" + this.getWorld() + ")";
  }

  public static final class PositionSerializer implements TypeSerializer<Position> {

    public static final PositionSerializer INSTANCE = new PositionSerializer();

    private PositionSerializer() {
    }

    @Override
    public Position deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
      if (node == null || node.virtual() || node.isNull()) {
        return null;
      }

      if (!node.isMap()) {
        final String scalar = node.getString();
        if (scalar == null || scalar.isEmpty()) {
          return null;
        }
        final String[] parts = scalar.split("[,\\s]+");
        if (parts.length == 4) {
          try {
            return Position.of(parts[0], parts[1],
                parts[2],
                parts[3]);
          } catch (final Exception e) {
            throw new SerializationException(node, Position.class, "Invalid position string: " + scalar, e);
          }
        }
        throw new SerializationException(node, Position.class, "Position must be a map or 'world x y z' string");
      }

      final String world = node.node("world").getString(node.node("w").getString());
      if (world == null || world.isEmpty()) {
        throw new SerializationException(node, Position.class, "Position requires 'world'");
      }

      return Position.of(world, node.node("x").getString(), node.node("y").getString(), node.node("z").getString());
    }

    @Override
    public void serialize(final Type type, final Position obj, final ConfigurationNode node)
        throws SerializationException {
      if (obj == null) {
        node.set(null);
        return;
      }
      node.node("world").set(obj.getWorld() == null ? null : obj.getWorld().getName());
      node.node("x").set(obj.getX());
      node.node("y").set(obj.getY());
      node.node("z").set(obj.getZ());
    }
  }

}
