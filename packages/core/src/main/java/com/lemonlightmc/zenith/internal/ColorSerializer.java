package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.Locale;

import org.bukkit.Color;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

public final class ColorSerializer implements TypeSerializer<Color> {

  public static final ColorSerializer INSTANCE = new ColorSerializer();

  private ColorSerializer() {
  }

  @Override
  public Color deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    if (node.isMap()) {
      return Color.fromARGB(
          clamp(node.node("r").getInt(node.node("red").getInt(0))),
          clamp(node.node("g").getInt(node.node("green").getInt(0))),
          clamp(node.node("b").getInt(node.node("blue").getInt(0))),
          clamp(node.node("a").getInt(node.node("alpha").getInt(255))));
    }

    if (node.raw() instanceof Number) {
      return Color.fromRGB(NumberConversions.parseInt(node.getString()));
    }

    String raw = node.getString();
    if (raw == null || raw.trim().isEmpty()) {
      return null;
    }
    raw = raw.trim();

    // Hex: #RRGGBB / RRGGBB / #RGB
    if (raw.startsWith("#") || isHex(raw)) {
      try {
        String hex = raw.startsWith("#") ? raw.substring(1) : raw;
        if (hex.length() == 3) {
          hex = "" + hex.charAt(0) + hex.charAt(0) + hex.charAt(1) + hex.charAt(1) + hex.charAt(2) + hex.charAt(2);
        }
        if (hex.length() == 6) {
          return Color.fromRGB(NumberConversions.parseInt(hex, 16));
        }
      } catch (final Exception ex) {
        throw new SerializationException(node, Color.class, "Invalid hex color: " + raw, ex);
      }
    }

    // "r,g,b"
    if (raw.indexOf(',') >= 0) {
      final String[] parts = raw.split(",");
      if (parts.length == 3) {
        try {
          return Color.fromARGB(
              255,
              clamp(NumberConversions.parseInt(parts[0].trim())),
              clamp(NumberConversions.parseInt(parts[1].trim())),
              clamp(NumberConversions.parseInt(parts[2].trim())));
        } catch (final Exception ex) {
          throw new SerializationException(node, Color.class, "Invalid RGB color: " + raw, ex);
        }
      }
    }

    // Named defaults/common? fallback to integer parse
    try {
      return Color.fromRGB(Integer.decode(raw));
    } catch (final Exception ex) {
      throw new SerializationException(node, Color.class, "Invalid color: " + raw, ex);
    }
  }

  @Override
  public void serialize(final Type type, final Color obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    node.set(String.format(Locale.ROOT, "#%06X", obj.asRGB() & 0xFFFFFF));
  }

  private static boolean isHex(final String s) {
    if (s.length() != 6 && s.length() != 3) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
        return false;
      }
    }
    return true;
  }

  private static int clamp(final int v) {
    return v >= 0 ? v : v <= 255 ? v : 255;
  }
}
