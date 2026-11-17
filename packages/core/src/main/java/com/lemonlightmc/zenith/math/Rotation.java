package com.lemonlightmc.zenith.math;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

import java.lang.reflect.Type;

/**
 * Represents a yaw and pitch rotation.
 */
public class Rotation {

  private double yaw;
  private double pitch;

  /**
   * Creates a rotation with the given yaw and pitch.
   *
   * @param yaw yaw rotation in degrees
   * @param pitch pitch rotation in degrees
   */
  public Rotation(final double yaw, final double pitch) {
    this.yaw = yaw;
    this.pitch = pitch;
  }

  /**
   * Creates a rotation with the given yaw and a pitch of zero.
   *
   * @param yaw yaw rotation in degrees
   */
  public Rotation(final double yaw) {
    this.yaw = yaw;
    this.pitch = 0;
  }

  /**
   * Creates a rotation with zero yaw and pitch.
   */
  public Rotation() {
    this.yaw = 0;
    this.pitch = 0;
  }

  /**
   * Gets the yaw component of this rotation.
   *
   * @return the yaw rotation in degrees
   */
  public double getYaw() {
    return this.yaw;
  }

  /**
   * Sets the yaw component of this rotation.
   *
   * @param yaw yaw rotation in degrees
   */
  public void setYaw(final double yaw) {
    this.yaw = yaw;
  }

  /**
   * Gets the pitch component of this rotation.
   *
   * @return the pitch rotation in degrees
   */
  public double getPitch() {
    return this.pitch;
  }

  /**
   * Sets the pitch component of this rotation.
   *
   * @param pitch pitch rotation in degrees
   */
  public void setPitch(final double pitch) {
    this.pitch = pitch;
  }

  /**
   * Gets the yaw normalized to the range {@code [-180, 180)}.
   *
   * @return the normalized yaw
   */
  public double getNormalizedYaw() {
    return normalizeYaw(yaw);
  }

  /**
   * Gets the pitch clamped to the range {@code [-90, 90]}.
   *
   * @return the normalized pitch
   */
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

  /**
   * Normalizes a yaw value to the range {@code [-180, 180)}.
   *
   * @param yaw yaw value to normalize
   * @return the normalized yaw value
   */
  public static double normalizeYaw(final double yaw) {
    double normalizedYaw = yaw % 360.0F;
    if (normalizedYaw >= 180.0F) {
      normalizedYaw -= 360.0F;
    } else if (normalizedYaw < -180.0F) {
      normalizedYaw += 360.0F;
    }
    return normalizedYaw;
  }

  /**
   * Clamps a pitch value to the range {@code [-90, 90]}.
   *
   * @param pitch pitch value to normalize
   * @return the normalized pitch value
   */
  public static double normalizePitch(final double pitch) {
    return pitch > 90.0F ? 90.0F : Math.max(pitch, -90.0F);
  }

  /**
   * Serializes and deserializes {@link Rotation} values for Configurate.
   */
  public static final class RotationSerializer implements TypeSerializer<Rotation> {

    /**
     * Shared serializer instance.
     */
    public static final RotationSerializer INSTANCE = new RotationSerializer();

    private RotationSerializer() {
    }

    /**
     * Deserializes a rotation from a configuration node.
     *
     * @param type target type
     * @param node configuration node to read from
     * @return the deserialized rotation, or {@code null} if the node is empty
     * @throws SerializationException if the node does not contain a valid rotation value
     */
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

    /**
     * Serializes a rotation to a configuration node.
     *
     * @param type source type
     * @param obj rotation to serialize
     * @param node configuration node to write to
     * @throws SerializationException if serialization fails
     */
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