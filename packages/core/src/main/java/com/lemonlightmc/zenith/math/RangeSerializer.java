package com.lemonlightmc.zenith.math;

import java.lang.reflect.Type;
import java.math.BigInteger;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.ranges.BigIntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.DoubleRange;
import com.lemonlightmc.zenith.additive.math.ranges.IntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.LongRange;

@SuppressWarnings("rawtypes")
public final class RangeSerializer implements TypeSerializer<Range> {

  public static final RangeSerializer INSTANCE = new RangeSerializer();

  private RangeSerializer() {
  }

  @Override
  public Range deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    // Map form: {min: 1, max: 5} / {from: 1, to: 5} (+ optional inclusivity flags)
    if (node.isMap() && hasAny(node, "min", "max", "from", "to", "start", "end", "value")) {
      if (node.hasChild("value") && !hasAny(node, "min", "max", "from", "to", "start", "end")) {
        return pointRange(stringValue(node.node("value")), node);
      }
      final String min = firstString(node, "min", "from", "start");
      final String max = firstString(node, "max", "to", "end");
      final boolean minInclusive = node.node("min-inclusive").getBoolean(node.node("inclusive-min").getBoolean(true));
      final boolean maxInclusive = node.node("max-inclusive").getBoolean(node.node("inclusive-max").getBoolean(true));
      return buildRange(min, max, minInclusive, maxInclusive, node);
    }

    final String raw = node.getString();
    if (raw != null) {
      final String value = raw.trim();
      if (value.isEmpty()) {
        return null;
      }
      return parseStringRange(value, node);
    }

    // Scalar number node
    final Object scalar = node.raw();
    if (scalar instanceof Number) {
      return pointRange(String.valueOf(scalar), node);
    }

    return null;
  }

  private static Range parseStringRange(final String raw, final ConfigurationNode node) throws SerializationException {
    // "min..max" / "..max" / "min.."
    if (raw.contains("..")) {
      final int idx = raw.indexOf("..");
      final String left = idx <= 0 ? null : raw.substring(0, idx).trim();
      final String right = (idx + 2 >= raw.length()) ? null : raw.substring(idx + 2).trim();
      return buildRange(emptyToNull(left), emptyToNull(right), true, true, node);
    }

    // "[1,5]" / "(1,5]" / "1,5" / "1 5"
    if (raw.startsWith("[") || raw.startsWith("(") || raw.endsWith("]") || raw.endsWith(")")
        || raw.indexOf(',') >= 0 || raw.indexOf(' ') >= 0) {
      final boolean minInclusive = !raw.startsWith("(");
      final boolean maxInclusive = !raw.endsWith(")");
      final String cleaned = raw
          .replace("[", "")
          .replace("]", "")
          .replace("(", "")
          .replace(")", "")
          .trim();
      final String[] parts = cleaned.split("[,\\s]+");
      if (parts.length == 2) {
        return buildRange(emptyToNull(parts[0].trim()), emptyToNull(parts[1].trim()), minInclusive, maxInclusive, node);
      }
      throw new SerializationException(node, Range.class, "Invalid range: " + raw);
    }

    // Single value -> point range
    return pointRange(raw, node);
  }

  private static Range buildRange(final String minStr, final String maxStr, final boolean minInclusive,
      final boolean maxInclusive, final ConfigurationNode node) throws SerializationException {
    final String sample = firstNonEmpty(minStr, maxStr);
    if (sample == null) {
      return IntegerRange.ALL;
    }

    try {
      if (isDecimal(sample) || isDecimal(maxStr)) {
        final Double min = (minStr == null) ? null : NumberConversions.parseDouble(minStr);
        final Double max = (maxStr == null) ? null : NumberConversions.parseDouble(maxStr);
        return new DoubleRange(min, minInclusive, max, maxInclusive);
      }

      if (fitsInt(sample) && (maxStr == null || fitsInt(maxStr))) {
        final Integer min = (minStr == null) ? null : NumberConversions.parseInt(minStr);
        final Integer max = (maxStr == null) ? null : NumberConversions.parseInt(maxStr);
        return new IntegerRange(min, minInclusive, max, maxInclusive);
      }

      if (fitsLong(sample) && (maxStr == null || fitsLong(maxStr))) {
        final Long min = (minStr == null) ? null : Long.parseLong(minStr.trim());
        final Long max = (maxStr == null) ? null : Long.parseLong(maxStr.trim());
        return new LongRange(min, minInclusive, max, maxInclusive);
      }

      final BigInteger min = (minStr == null) ? null : new BigInteger(minStr.trim());
      final BigInteger max = (maxStr == null) ? null : new BigInteger(maxStr.trim());
      return new BigIntegerRange(min, minInclusive, max, maxInclusive);
    } catch (final Exception ex) {
      throw new SerializationException(node, Range.class,
          "Invalid range: " + (minStr == null ? "" : minStr) + ".." + (maxStr == null ? "" : maxStr), ex);
    }
  }

  private static Range pointRange(final String raw, final ConfigurationNode node) throws SerializationException {
    if (raw == null || raw.trim().isEmpty()) {
      return null;
    }
    final String value = raw.trim();
    try {
      if (isDecimal(value)) {
        return DoubleRange.at(NumberConversions.parseDouble(value));
      }
      if (fitsInt(value)) {
        return IntegerRange.at(NumberConversions.parseInt(value));
      }
      return LongRange.at(Long.parseLong(value));
    } catch (final Exception ex) {
      throw new SerializationException(node, Range.class, "Invalid range value: " + raw, ex);
    }
  }

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

  private static boolean isDecimal(final String s) {
    return s != null && (s.indexOf('.') >= 0 || s.indexOf('e') >= 0 || s.indexOf('E') >= 0);
  }

  private static boolean fitsInt(final String s) {
    if (s == null) {
      return false;
    }
    try {
      final long v = Long.parseLong(s.trim());
      return v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE;
    } catch (final Exception e) {
      return false;
    }
  }

  private static boolean fitsLong(final String s) {
    if (s == null) {
      return false;
    }
    try {
      Long.parseLong(s.trim());
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  private static boolean hasAny(final ConfigurationNode node, final String... keys) {
    for (final String key : keys) {
      if (node.hasChild(key)) {
        return true;
      }
    }
    return false;
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

  private static String stringValue(final ConfigurationNode node) {
    if (node == null || node.virtual()) {
      return null;
    }
    final String value = node.getString();
    if (value != null) {
      return value;
    }
    final Object raw = node.raw();
    return raw == null ? null : String.valueOf(raw);
  }

  private static String firstNonEmpty(final String... values) {
    for (final String value : values) {
      if (value != null && !value.isEmpty()) {
        return value;
      }
    }
    return null;
  }

  private static String emptyToNull(final String value) {
    return (value == null || value.isEmpty()) ? null : value;
  }
}
