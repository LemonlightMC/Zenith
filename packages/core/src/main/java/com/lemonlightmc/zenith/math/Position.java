package com.lemonlightmc.zenith.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.interfaces.JsonBuilder;
import com.lemonlightmc.zenith.json.GsonSerializable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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

  public static Position of(final World world, final double x, final double y, final double z) {
    return of(world, x, y, z);
  }

  public static Position of(final Location location) {
    if (location == null) {
      throw new IllegalArgumentException("Location cannot be null");
    }
    return of(location.getWorld(), location.getX(), location.getY(), location.getZ());
  }

  public static Position of(final Block block) {
    if (block == null) {
      throw new IllegalArgumentException("Block cannot be null");
    }
    return of(block.getLocation());
  }

  private final double x;
  private final double y;
  private final double z;
  private final Reference<World> world;

  private volatile Location bukkitLocation = null;

  public Position(final String world, final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world != null ? new WeakReference<>(Bukkit.getServer().getWorld(world)) : null;
  }

  public Position(final World world, final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world != null ? new WeakReference<>(world) : null;
  }

  public Position(final Reference<World> world, final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.world = world;
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
}
