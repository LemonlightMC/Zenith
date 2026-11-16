package com.lemonlightmc.zenith.math;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

import java.lang.reflect.Type;

public class Rotation {

  private double yaw;
  private double pitch;

  public Rotation(final double yaw, final double pitch) {
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public Rotation(final double yaw) {
    this.yaw = yaw;
    this.pitch = 0;
  }

  public Rotation() {
    this.yaw = 0;
    this.pitch = 0;
  }

  public double getYaw() {
    return this.yaw;
  }

  public void setYaw(final double yaw) {
    this.yaw = yaw;
  }

  public double getPitch() {
    return this.pitch;
  }

  public void setPitch(final double pitch) {
    this.pitch = pitch;
  }

  // between +/-180
  public double getNormalizedYaw() {
    return normalizeYaw(yaw);
  }

  // between +/-90
  public double getNormalizedPitch() {
    return normalizePitch(pitch);
  }

  @Override
  public int hashCode() {
    return 31 * (31 + Double.hashCode(yaw)) + Double.hashCode(pitch);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Rotation other = (Rotation) obj;
    return Double.doubleToLongBits(yaw) == Double.doubleToLongBits(other.yaw)
        && Double.doubleToLongBits(pitch) == Double.doubleToLongBits(other.pitch);
  }

  @Override
  public String toString() {
    return "Rotation [yaw=" + yaw + ", pitch=" + pitch + "]";
  }

  public static double normalizeYaw(final double yaw) {
    double normalizedYaw = yaw % 360.0F;
    if (normalizedYaw >= 180.0F) {
      normalizedYaw -= 360.0F;
    } else if (normalizedYaw < -180.0F) {
      normalizedYaw += 360.0F;
    }
    return normalizedYaw;
  }

  public static double normalizePitch(final double pitch) {
    return pitch > 90.0F ? 90.0F : Math.max(pitch, -90.0F);
  }

  public static final class RotationSerializer implements TypeSerializer<Rotation> {

    public static final RotationSerializer INSTANCE = new RotationSerializer();

    private RotationSerializer() {
    }

    @Override
    public Rotation deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
      if (node == null || node.virtual() || node.isNull()) {
        return null;
      }

      if (!node.isMap()) {
        final String scalar = node.getString();
        if (scalar == null || scalar.isEmpty()) {
          return null;
        }
        final String[] parts = scalar.split("[,\\s]+");
        try {
          if (parts.length == 1) {
            return new Rotation(NumberConversions.parseDouble(parts[0]));
          }
          if (parts.length >= 2) {
            return new Rotation(NumberConversions.parseDouble(parts[0]),
                NumberConversions.parseDouble(parts[1]));
          }
        } catch (final Exception e) {
          throw new SerializationException(node, Rotation.class, "Invalid rotation string: " + scalar, e);
        }
        throw new SerializationException(node, Rotation.class, "Rotation must be a map or 'yaw pitch' string");
      }

      return new Rotation(
          node.node("yaw").getDouble(node.node("y").getDouble(0.0)),
          node.node("pitch").getDouble(node.node("p").getDouble(0.0)));
    }

    @Override
    public void serialize(final Type type, final Rotation obj, final ConfigurationNode node)
        throws SerializationException {
      if (obj == null) {
        node.set(null);
        return;
      }
      node.node("yaw").set(obj.getYaw());
      node.node("pitch").set(obj.getPitch());
    }
  }
}