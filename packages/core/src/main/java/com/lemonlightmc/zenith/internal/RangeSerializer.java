package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.Range.RangeStringParsed;
import com.lemonlightmc.zenith.additive.math.ranges.DoubleRange;
import com.lemonlightmc.zenith.additive.math.ranges.IntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.LongRange;

/**
 * Serializes and deserializes {@link Range} values.
 */
@SuppressWarnings("rawtypes")
public final class RangeSerializer implements TypeSerializer<Range> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final RangeSerializer INSTANCE = new RangeSerializer();

  private RangeSerializer() {
  }

  /**
   * Deserializes a range from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the parsed range, or {@code null} if unavailable
   * @throws SerializationException if the value cannot be parsed as a range
   */
  @Override
  public Range deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    String raw;

    // Map form: {min: 1, max: 5} / {from: 1, to: 5} (+ optional inclusivity flags)
    if (node.isMap()) {
      final ConfigurationNode n = node.node("value");
      if (n != null && !n.isNull() && !n.virtual()) {
        raw = n.getString();
        if (raw != null) {
          return parseStringRange(raw, node);
        }
      }

      final String min = firstString(node, "min", "from", "start");
      final String max = firstString(node, "max", "to", "end");
      final boolean minInclusive = node.node("min-inclusive").getBoolean(node.node("inclusive-min").getBoolean(true));
      final boolean maxInclusive = node.node("max-inclusive").getBoolean(node.node("inclusive-max").getBoolean(true));
      return parseStringRange(RangeStringParsed.from(min, max, minInclusive, maxInclusive), node);
    }

    raw = node.getString();
    if (raw != null) {
      return parseStringRange(raw, node);
    }

    // Scalar number node
    final Object scalar = node.raw();
    if (scalar instanceof Number) {
      return pointRange(String.valueOf(scalar), node);
    }

    return null;
  }

  private static Range parseStringRange(final String raw, final ConfigurationNode node)
      throws SerializationException {
    try {
      return Range.fromString(raw);
    } catch (final Exception ex) {
      throw new SerializationException(node, Range.class,
          "Invalid range: " + raw, ex);
    }
  }

  private static Range parseStringRange(final RangeStringParsed parsed, final ConfigurationNode node)
      throws SerializationException {
    try {
      return Range.fromString(parsed);
    } catch (final Exception ex) {
      throw new SerializationException(node, Range.class,
          "Invalid range: " + parsed.toString(), ex);
    }
  }

  private static Range pointRange(final String raw, final ConfigurationNode node) throws SerializationException {
    if (raw == null || raw.trim().isEmpty()) {
      return null;
    }
    final String value = raw.trim();
    try {
      if (NumberConversions.isDecimal(value)) {
        return DoubleRange.at(NumberConversions.parseDouble(value));
      }
      if (NumberConversions.fitsInt(value)) {
        return IntegerRange.at(NumberConversions.parseInt(value));
      }
      return LongRange.at(Long.parseLong(value));
    } catch (final Exception ex) {
      throw new SerializationException(node, Range.class, "Invalid range value: " + raw, ex);
    }
  }

  /**
   * Serializes a range to a configuration node.
   *
   * @param type the source type
   * @param obj the range to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final Range obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.node("min").set(obj.getMin());
    node.node("max").set(obj.getMax());
    if (!obj.isMinInclusive()) {
      node.node("min-inclusive").set(false);
    }
    if (!obj.isMaxInclusive()) {
      node.node("max-inclusive").set(false);
    }
  }

  private static String firstString(final ConfigurationNode node, final String... keys) {
    for (final String key : keys) {
      final ConfigurationNode child = node.node(key);
      if (!child.virtual()) {
        final String value = child.getString();
        if (value != null) {
          return value;
        }
        final Object raw = child.raw();
        if (raw != null) {
          return String.valueOf(raw);
        }
      }
    }
    return null;
  }
}
