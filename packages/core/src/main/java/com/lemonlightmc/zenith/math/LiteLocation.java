package com.lemonlightmc.zenith.math;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.lemonlightmc.zenith.interfaces.Cloneable;

public class LiteLocation implements Cloneable<LiteLocation> {
  private Reference<World> world;
  private double x;
  private double y;
  private double z;
  private float pitch;
  private float yaw;

  public LiteLocation(final World world, final double x, final double y, final double z, final float yaw,
      final float pitch) {
    if (world != null) {
      this.world = new WeakReference<>(world);
    }

    this.x = x;
    this.y = y;
    this.z = z;
    this.pitch = pitch;
    this.yaw = yaw;
  }

  public LiteLocation(final World world, final double x, final double y, final double z) {
    this(world, x, y, z, 0, 0);
  }

  public LiteLocation(final World world) {
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

  public boolean isWorldLoaded() {
    if (this.world == null) {
      return false;
    }

    final World world = this.world.get();
    return world != null && world.equals(Bukkit.getWorld(world.getUID()));
  }

  public void setWorld(final Reference<World> world) {
    this.world = world;
  }

  public double getX() {
    return x;
  }

  public void setX(final double x) {
    this.x = x;
  }

  public double getY() {
    return z;
  }

  public void setY(final double y) {
    this.y = y;
  }

  public double getZ() {
    return z;
  }

  public void setZ(final double z) {
    this.z = z;
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
    return new org.bukkit.Location(getWorld(), x, y, z, yaw, pitch);
  }

  public double distance(final LiteLocation o) {
    return Math.sqrt(distanceSquared(o));
  }

  public double distanceSquared(final LiteLocation o) {
    if (o == null) {
      throw new IllegalArgumentException("Cannot measure distance to a null location");
    } else if (o.getWorld() == null || getWorld() == null) {
      throw new IllegalArgumentException("Cannot measure distance to a null world");
    } else if (o.getWorld() != getWorld()) {
      throw new IllegalArgumentException(
          "Cannot measure distance between " + getWorld().getName() + " and " + o.getWorld().getName());
    }

    return (x - o.x) * (x - o.x) + (y - o.y) * (y - o.y) + (z - o.z) * (z - o.z);
  }

  @Override
  public LiteLocation clone() {
    try {
      return (LiteLocation) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new Error(e);
    }
  }

  @Override
  public int hashCode() {
    int result = 31 + ((world == null) ? 0 : world.hashCode());
    result = 31 * result + Double.hashCode(x);
    result = 31 * result + Double.hashCode(y);
    result = 31 * result + Double.hashCode(z);
    result = 31 * result + Float.floatToIntBits(pitch);
    return 31 * result + Float.floatToIntBits(yaw);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final LiteLocation other = (LiteLocation) obj;
    if (world == null) {
      if (other.world != null)
        return false;
    }
    return world.equals(other.world) && Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
        && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y)
        && Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z)
        && Float.floatToIntBits(pitch) == Float.floatToIntBits(other.pitch)
        && Float.floatToIntBits(yaw) == Float.floatToIntBits(other.yaw);
  }

  @Override
  public String toString() {
    return "LiteLocation [world=" + world + ", x=" + x + ", y=" + y + ", z=" + z + ", pitch=" + pitch + ", yaw=" + yaw
        + "]";
  }
}