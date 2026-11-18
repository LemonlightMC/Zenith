package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.version.ComplexVersion;
import com.lemonlightmc.zenith.additive.version.SemverVersion;
import com.lemonlightmc.zenith.additive.version.Version;

/**
 * Serializes and deserializes {@link Version} values.
 */
public final class VersionSerializer implements TypeSerializer<Version> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final VersionSerializer INSTANCE = new VersionSerializer();

  private VersionSerializer() {
  }

  /**
   * Deserializes a version value from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the parsed version, or {@code null} if unavailable
   * @throws SerializationException if the value cannot be parsed as a version
   */
  @Override
  public Version deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    final String raw = node.getString();
    if (raw == null || raw.trim().isEmpty()) {
      // Support map form with "value"/"version"
      final String alt = node.node("value").getString(node.node("version").getString());
      if (alt == null || alt.trim().isEmpty()) {
        return null;
      }
      return parse(node, alt.trim());
    }
    return parse(node, raw.trim());
  }

  private static Version parse(final ConfigurationNode node, final String value) throws SerializationException {
    try {
      return new SemverVersion(value);
    } catch (final Exception ignored) {
    }
    try {
      return new ComplexVersion(value);
    } catch (final Exception ex) {
      throw new SerializationException(node, Version.class, "Invalid version: " + value, ex);
    }
  }

  /**
   * Serializes a version value to a configuration node.
   *
   * @param type the source type
   * @param obj the version to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final Version obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.set(obj.toString());
  }
}
