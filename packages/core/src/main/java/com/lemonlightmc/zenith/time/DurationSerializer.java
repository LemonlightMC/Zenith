package com.lemonlightmc.zenith.time;

import java.lang.reflect.Type;
import java.time.Duration;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.time.DurationParser;

public final class DurationSerializer implements TypeSerializer<Duration> {

  public static final DurationSerializer INSTANCE = new DurationSerializer();

  private DurationSerializer() {
  }

  @Override
  public Duration deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    final Object raw = node.raw();
    if (raw instanceof Number) {
      // Treat numeric values as milliseconds.
      return Duration.ofMillis(((Number) raw).longValue());
    }

    final String value = node.getString();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }

    final String s = value.trim();
    try {
      return Duration.parse(s);
    } catch (final Exception ignored) {
    }

    try {
      final long millis = DurationParser.parse(s);
      if (millis != 0L || s.equals("0") || s.equalsIgnoreCase("0ms") || s.equalsIgnoreCase("0s")) {
        return Duration.ofMillis(millis);
      }
    } catch (final Exception ignored) {
    }

    try {
      return Duration.ofMillis(Long.parseLong(s));
    } catch (final Exception ex) {
      throw new SerializationException(node, Duration.class, "Invalid duration: " + value, ex);
    }
  }

  @Override
  public void serialize(final Type type, final Duration obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.set(obj.toString());
  }
}
