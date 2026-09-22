package com.lemonlightmc.zenith.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.interfaces.JsonBuilder;
import com.lemonlightmc.zenith.json.GsonSerializable;

import org.bukkit.Location;

/**
 * An immutable and serializable region object
 */
public final class Region implements GsonSerializable {
  public static Region deserialize(final JsonElement element) {
    if (!element.isJsonObject()) {
      throw new IllegalArgumentException("Element is not a JsonObject");
    }
    final JsonObject object = element.getAsJsonObject();
    if (!object.has("min") || !object.has("max")) {
      throw new IllegalArgumentException("Element is missing min or max");
    }

    return of(Position.deserialize(object.get("min")), Position.deserialize(object.get("max")));
  }

  public static Region of(final Position a, final Position b) {
    return new Region(a, b);
  }

  public static Region of(final Position a) {
    return new Region(a);
  }

  private final Position pos1;
  private final Position pos2;

  private final double width;
  private final double height;
  private final double depth;

  public Region(final Position pos1, final Position pos2) {
    if (pos1 == null) {
      this.pos1 = pos1;
      this.pos2 = pos1;
      this.width = 1;
      this.height = 1;
      this.depth = 1;
      return;
    } else if (pos2 == null) {
      this.pos1 = pos2;
      this.pos2 = pos2;
      this.width = 1;
      this.height = 1;
      this.depth = 1;
      return;
    }

    if (!pos1.getWorld().equals(pos2.getWorld())) {
      throw new IllegalArgumentException("Positions are in different worlds");
    }

    this.pos1 = pos1;
    this.pos2 = pos2;

    this.width = Math.abs(this.pos2.getX() - this.pos1.getX());
    this.height = Math.abs(this.pos2.getY() - this.pos1.getY());
    this.depth = Math.abs(this.pos2.getZ() - this.pos1.getZ());
  }

  public Region(final Position pos1) {
    this.pos1 = pos1;
    this.pos2 = pos1;
    this.width = 1;
    this.height = 1;
    this.depth = 1;
  }

  public boolean inRegion(final Position pos) {
    if (pos == null) {
      return false;
    }
    return pos.getWorld().equals(this.pos1.getWorld()) && inRegion(pos.getX(), pos.getY(), pos.getZ());
  }

  public boolean inRegion(final Location loc) {
    if (loc == null) {
      return false;
    }
    return loc.getWorld().equals(this.pos1.getWorld()) && inRegion(loc.getX(), loc.getY(), loc.getZ());
  }

  public boolean inRegion(final double x, final double y, final double z) {
    return x >= this.pos1.getX() && x <= this.pos2.getX()
        && y >= this.pos1.getY() && y <= this.pos2.getY()
        && z >= this.pos1.getZ() && z <= this.pos2.getZ();
  }

  public Position getPos1() {
    return this.pos1;
  }

  public Position getPos2() {
    return this.pos2;
  }

  public double getWidth() {
    return this.width;
  }

  public double getHeight() {
    return this.height;
  }

  public double getDepth() {
    return this.depth;
  }

  @Override
  public JsonObject serialize() {
    return JsonBuilder.object()
        .add("pos1", this.pos1)
        .add("pos2", this.pos2)
        .build();
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Region)) {
      return false;
    }
    final Region other = (Region) o;
    return pos1.equals(other.pos1) && pos2.equals(other.pos2);
  }

  @Override
  public int hashCode() {
    return (31 + pos1.hashCode()) * 31 + pos2.hashCode();
  }

  @Override
  public String toString() {
    return "Region(min=" + pos1 + ", max=" + pos2 + ")";
  }

}
