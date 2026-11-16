package com.lemonlightmc.zenith.math;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.interfaces.JsonBuilder;
import com.lemonlightmc.zenith.json.GsonSerializable;
import com.lemonlightmc.zenith.math.Position.PositionSerializer;

import java.lang.reflect.Type;

import org.bukkit.Location;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

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

  public static final class RegionSerializer implements TypeSerializer<Region> {

    public static final RegionSerializer INSTANCE = new RegionSerializer();

    private RegionSerializer() {
    }

    @Override
    public Region deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
      if (node == null || node.virtual() || node.isNull()) {
        return null;
      }
      if (!node.isMap()) {
        throw new SerializationException(node, Region.class, "Region must be a mapping");
      }

      // Support {pos1:{...}, pos2:{...}} / {min:{...}, max:{...}}
      final ConfigurationNode p1n = !node.node("pos1").virtual() ? node.node("pos1") : node.node("min");
      final ConfigurationNode p2n = !node.node("pos2").virtual() ? node.node("pos2") : node.node("max");

      if (!p1n.virtual() && !p2n.virtual()) {
        final Position p1 = PositionSerializer.INSTANCE.deserialize(Position.class, p1n);
        final Position p2 = PositionSerializer.INSTANCE.deserialize(Position.class, p2n);
        if (p1 == null || p2 == null) {
          throw new SerializationException(node, Region.class, "Region requires both positions");
        }
        return Region.of(p1, p2);
      }

      // Support single position
      if (!node.node("pos").virtual()) {
        final Position p = PositionSerializer.INSTANCE.deserialize(Position.class, node.node("pos"));
        return p == null ? null : Region.of(p);
      }

      // Support {world,x1,y1,z1,x2,y2,z2}
      final String world = node.node("world").getString();
      if (world != null) {
        final Position p1 = Position.of(world, node.node("x1").getString(node.node("min-x").getString()),
            node.node("y1").getString(node.node("min-y").getString()),
            node.node("z1").getString(node.node("min-z").getString()));
        final Position p2 = Position.of(world,
            node.node("x2").getString(node.node("max-x").getString((String.valueOf(p1.getX())))),
            node.node("y2").getString(node.node("max-y").getString((String.valueOf(p1.getY())))),
            node.node("z2").getString(node.node("max-z").getString(String.valueOf(p1.getZ()))));
        return Region.of(p1, p2);
      }

      throw new SerializationException(node, Region.class, "Invalid region format");
    }

    @Override
    public void serialize(final Type type, final Region obj, final ConfigurationNode node)
        throws SerializationException {
      if (obj == null) {
        node.set(null);
        return;
      }
      PositionSerializer.INSTANCE.serialize(Position.class, obj.getPos1(), node.node("pos1"));
      PositionSerializer.INSTANCE.serialize(Position.class, obj.getPos2(), node.node("pos2"));
    }
  }

}
