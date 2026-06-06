package com.lemonlightmc.zenith.math;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;

import com.lemonlightmc.zenith.interfaces.Cloneable;

public class Location2D implements Cloneable<Location2D>, ConfigurationSerializable {
  private Reference<World> world;
  private double x;
  private double z;
  private float pitch;
  private float yaw;

  public Location2D(final World world, final double x, final double z, final float yaw, final float pitch) {
    if (world != null) {
      this.world = new WeakReference<>(world);
    }

    this.x = x;
    this.z = z;
    this.pitch = pitch;
    this.yaw = yaw;
  }

  public Location2D(final World world, final double x, final double z) {
    this(world, x, z, 0, 0);
  }

  public Location2D(final World world) {
    this.world = (world == null) ? null : new WeakReference<>(world);
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

  public void setWorld(final Reference<World> world) {
    this.world = world;
  }

  public boolean isWorldLoaded() {
    if (this.world == null) {
      return false;
    }

    final World world = this.world.get();
    return world != null && world.equals(Bukkit.getWorld(world.getUID()));
  }

  public org.bukkit.Chunk getChunk() {
    return getWorld().getChunkAt((int) x, (int) z);
  }

  public org.bukkit.block.Block getBlock() {
    return getWorld().getBlockAt((int) x, 0, (int) z);
  }

  public double getX() {
    return x;
  }

  public void setX(final double x) {
    this.x = x;
  }

  public int getBlockX() {
    return NumberConversions.floor(x);
  }

  public double getZ() {
    return z;
  }

  public void setZ(final double z) {
    this.z = z;
  }

  public int getBlockZ() {
    return NumberConversions.floor(z);
  }

  public float getPitch() {
    return pitch;
  }

  public void setPitch(final float pitch) {
    this.pitch = pitch;
  }

  public float getYaw() {
    return yaw;
  }

  public void setYaw(final float yaw) {
    this.yaw = yaw;
  }

  public org.bukkit.Location toLocation() {
    return new org.bukkit.Location(getWorld(), x, 0, z, yaw, pitch);
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  public double lengthSquared() {
    return NumberConversions.square(x) + NumberConversions.square(z);
  }

  public double distance(final Location2D o) {
    return Math.sqrt(distanceSquared(o));
  }

  public double distanceSquared(final Location2D o) {
    if (o == null) {
      throw new IllegalArgumentException("Cannot measure distance to a null location");
    } else if (o.getWorld() == null || getWorld() == null) {
      throw new IllegalArgumentException("Cannot measure distance to a null world");
    } else if (o.getWorld() != getWorld()) {
      throw new IllegalArgumentException(
          "Cannot measure distance between " + getWorld().getName() + " and " + o.getWorld().getName());
    }

    return NumberConversions.square(x - o.x) + NumberConversions.square(z - o.z);
  }

  public Location2D zero() {
    x = 0;
    z = 0;
    return this;
  }

  public Location2D add(final Location2D vec) {
    if (vec == null || vec.getWorld() != getWorld()) {
      throw new IllegalArgumentException("Cannot add Locations of differing worlds");
    }

    x += vec.x;
    z += vec.z;
    return this;
  }

  public Location2D add(final double x, final double z) {
    this.x += x;
    this.z += z;
    return this;
  }

  public Location2D subtract(final Location2D vec) {
    if (vec == null || vec.getWorld() != getWorld()) {
      throw new IllegalArgumentException("Cannot add Locations of differing worlds");
    }

    x -= vec.x;
    z -= vec.z;
    return this;
  }

  public Location2D subtract(final double x, final double z) {
    this.x -= x;
    this.z -= z;
    return this;
  }

  public Location2D multiply(final double m) {
    x *= m;
    z *= m;
    return this;
  }

  @Override
  public Location2D clone() {
    try {
      return (Location2D) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new Error(e);
    }
  }

  @Override
  public Map<String, Object> serialize() {
    final HashMap<String, Object> data = new HashMap<String, Object>();

    if (this.world != null) {
      data.put("world", getWorld().getName());
    }

    data.put("x", this.x);
    data.put("z", this.z);
    data.put("yaw", this.yaw);
    data.put("pitch", this.pitch);

    return data;
  }

  public static Location2D deserialize(final Map<String, Object> args) {
    World world = null;
    if (args.containsKey("world")) {
      world = Bukkit.getWorld((String) args.get("world"));
      if (world == null) {
        throw new IllegalArgumentException("unknown world");
      }
    }

    return new Location2D(world, NumberConversions.toDouble(args.get("x")),
        NumberConversions.toDouble(args.get("z")), NumberConversions.toFloat(args.get("yaw")),
        NumberConversions.toFloat(args.get("pitch")));
  }
}