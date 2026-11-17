package com.lemonlightmc.zenith.math;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;

/**
 * Represents a lightweight, world-aware location that keeps only a weak reference to its world.
 *
 * <p>This type stores coordinates and rotation while avoiding strong ownership of a {@link World}
 * instance, making it useful for cached or transient positional data.</p>
 */
public record LiteLocation(Reference<World> world, double x, double y, double z, float pitch,
    float yaw) implements Cloneable<LiteLocation> {

  /**
   * Creates a new lite location for the given world, coordinates, and rotation.
   *
   * @param world world to reference weakly, or {@code null} for no world
   * @param x x coordinate
   * @param y y coordinate
   * @param z z coordinate
   * @param yaw yaw rotation in degrees
   * @param pitch pitch rotation in degrees
   */
  public LiteLocation(final World world, final double x, final double y, final double z, final float yaw,
      final float pitch) {
    this(world != null ? new WeakReference<>(world) : null, x, y, z, yaw, pitch);
  }

  /**
   * Creates a new lite location for the given world and coordinates with zero rotation.
   *
   * @param world world to reference weakly, or {@code null} for no world
   * @param x x coordinate
   * @param y y coordinate
   * @param z z coordinate
   */
  public LiteLocation(final World world, final double x, final double y, final double z) {
    this(world, x, y, z, 0, 0);
  }

  /**
   * Gets the referenced world.
   *
   * @return the referenced world, or {@code null} if no world was provided
   * @throws IllegalArgumentException if the referenced world has been unloaded
   */
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

  /**
   * Checks whether the referenced world is still loaded on the server.
   *
   * @return {@code true} if the world reference is present and currently loaded; otherwise {@code false}
   */
  public boolean isWorldLoaded() {
    if (this.world == null) {
      return false;
    }

    final World world = this.world.get();
    return world != null && world.equals(Bukkit.getWorld(world.getUID()));
  }

  /**
   * Converts this lite location to a Bukkit {@link org.bukkit.Location}.
   *
   * @return a new Bukkit location with the same world, coordinates, and rotation
   * @throws IllegalArgumentException if the referenced world has been unloaded
   */
  public org.bukkit.Location toLocation() {
    return new org.bukkit.Location(getWorld(), x, y, z, yaw, pitch);
  }

  /**
   * Converts this location's rotation values to a {@link Rotation}.
   *
   * @return a rotation representing this location's yaw and pitch
   */
  public Rotation toRotation() {
    return new Rotation(yaw, pitch);
  }

  /**
   * Converts this lite location to a {@link Position}.
   *
   * @return a position containing the same world reference and coordinates
   */
  public Position toPosition() {
    return new Position(world, x, y, z);
  }

  /**
   * Calculates the distance between this location and another lite location.
   *
   * @param o other location to measure against
   * @return the distance between the two locations
   * @throws IllegalArgumentException if the other location is {@code null}, either world is unavailable, or the worlds differ
   */
  public double distance(final LiteLocation o) {
    return Math.sqrt(distanceSquared(o));
  }

  /**
   * Calculates the squared distance between this location and another lite location.
   *
   * @param o other location to measure against
   * @return the squared distance between the two locations
   * @throws IllegalArgumentException if the other location is {@code null}, either world is unavailable, or the worlds differ
   */
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

  /**
   * Creates a copy of this lite location.
   *
   * @return a cloned lite location with the same values
   */
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