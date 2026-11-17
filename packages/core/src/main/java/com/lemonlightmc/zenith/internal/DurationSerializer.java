package com.lemonlightmc.zenith.internal;

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

    String value = node.getString();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    value = value.trim();

    try {
      return Duration.parse(value);
    } catch (final Exception ignored) {
    }

    try {
      final long millis = DurationParser.parse(value);
      if (millis != 0L || value.equals("0") || value.equalsIgnoreCase("0ms") || value.equalsIgnoreCase("0s")) {
        return Duration.ofMillis(millis);
      }
    } catch (final Exception ignored) {
    }

    try {
      return Duration.ofMillis(Long.parseLong(value));
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
