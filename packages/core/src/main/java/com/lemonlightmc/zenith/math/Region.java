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
 * Represents an immutable, serializable region defined by two positions.
 */
public final class Region implements GsonSerializable {
  /**
   * Deserializes a region from a JSON element.
   *
   * @param element JSON element containing the serialized region data
   * @return the deserialized region
   * @throws IllegalArgumentException if the element is not a valid region object
   */
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

  /**
   * Creates a region spanning two positions.
   *
   * @param a first position
   * @param b second position
   * @return the created region
   */
  public static Region of(final Position a, final Position b) {
    return new Region(a, b);
  }

  /**
   * Creates a single-point region from a position.
   *
   * @param a position defining the region
   * @return the created region
   */
  public static Region of(final Position a) {
    return new Region(a);
  }

  private final Position pos1;
  private final Position pos2;

  private final double width;
  private final double height;
  private final double depth;

  /**
   * Creates a region spanning two positions.
   *
   * @param pos1 first position
   * @param pos2 second position
   * @throws IllegalArgumentException if the positions belong to different worlds
   */
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

  /**
   * Creates a single-point region from a position.
   *
   * @param pos1 position defining the region
   */
  public Region(final Position pos1) {
    this.pos1 = pos1;
    this.pos2 = pos1;
    this.width = 1;
    this.height = 1;
    this.depth = 1;
  }

  /**
   * Checks whether a position is inside this region.
   *
   * @param pos position to test
   * @return {@code true} if the position is inside this region; otherwise {@code false}
   */
  public boolean inRegion(final Position pos) {
    if (pos == null) {
      return false;
    }
    return pos.getWorld().equals(this.pos1.getWorld()) && inRegion(pos.getX(), pos.getY(), pos.getZ());
  }

  /**
   * Checks whether a Bukkit location is inside this region.
   *
   * @param loc location to test
   * @return {@code true} if the location is inside this region; otherwise {@code false}
   */
  public boolean inRegion(final Location loc) {
    if (loc == null) {
      return false;
    }
    return loc.getWorld().equals(this.pos1.getWorld()) && inRegion(loc.getX(), loc.getY(), loc.getZ());
  }

  /**
   * Checks whether the given coordinates are inside this region.
   *
   * @param x x coordinate to test
   * @param y y coordinate to test
   * @param z z coordinate to test
   * @return {@code true} if the coordinates are inside this region; otherwise {@code false}
   */
  public boolean inRegion(final double x, final double y, final double z) {
    return x >= this.pos1.getX() && x <= this.pos2.getX()
        && y >= this.pos1.getY() && y <= this.pos2.getY()
        && z >= this.pos1.getZ() && z <= this.pos2.getZ();
  }

  /**
   * Gets the first corner position of this region.
   *
   * @return the first position
   */
  public Position getPos1() {
    return this.pos1;
  }

  /**
   * Gets the second corner position of this region.
   *
   * @return the second position
   */
  public Position getPos2() {
    return this.pos2;
  }

  /**
   * Gets the width of this region along the x axis.
   *
   * @return the region width
   */
  public double getWidth() {
    return this.width;
  }

  /**
   * Gets the height of this region along the y axis.
   *
   * @return the region height
   */
  public double getHeight() {
    return this.height;
  }

  /**
   * Gets the depth of this region along the z axis.
   *
   * @return the region depth
   */
  public double getDepth() {
    return this.depth;
  }

  /**
   * Serializes this region to JSON.
   *
   * @return the serialized JSON representation
   */
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

  /**
   * Serializes and deserializes {@link Region} values for Configurate.
   */
  public static final class RegionSerializer implements TypeSerializer<Region> {

    /**
     * Shared serializer instance.
     */
    public static final RegionSerializer INSTANCE = new RegionSerializer();

    private RegionSerializer() {
    }

    /**
     * Deserializes a region from a configuration node.
     *
     * @param type target type
     * @param node configuration node to read from
     * @return the deserialized region, or {@code null} if the node is empty
     * @throws SerializationException if the node does not contain a valid region value
     */
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

    /**
     * Serializes a region to a configuration node.
     *
     * @param type source type
     * @param obj region to serialize
     * @param node configuration node to write to
     * @throws SerializationException if serialization fails
     */
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
