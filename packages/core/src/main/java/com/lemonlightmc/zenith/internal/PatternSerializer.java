package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

/**
 * Serializes and deserializes {@link Pattern} values.
 */
public final class PatternSerializer implements TypeSerializer<Pattern> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final PatternSerializer INSTANCE = new PatternSerializer();

  private PatternSerializer() {
  }

  /**
   * Deserializes a regular expression pattern from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the compiled pattern, or {@code null} if unavailable
   * @throws SerializationException if the pattern cannot be parsed or compiled
   */
  @Override
  public Pattern deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    String pattern;
    int flags = 0;

    if (node.isMap()) {
      pattern = node.node("pattern").getString(node.node("regex").getString(node.node("value").getString()));
      flags = parseFlags(node.node("flags"));
      if (pattern == null || pattern.isEmpty()) {
        throw new SerializationException(node, Pattern.class, "Pattern map requires 'pattern' or 'regex'");
      }
    } else {
      pattern = node.getString();
      if (pattern == null) {
        return null;
      }
    }

    pattern = pattern.trim();
    if (pattern.isEmpty()) {
      return null;
    }
    try {
      return Pattern.compile(pattern, flags);
    } catch (final PatternSyntaxException ex) {
      throw new SerializationException(node, Pattern.class, "Invalid regex pattern: " + pattern, ex);
    }
  }

  private static int parseFlags(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return 0;
    }
    if (node.isList()) {
      int flags = 0;
      for (final ConfigurationNode child : node.childrenList()) {
        flags |= flagFromString(child.getString());
      }
      return flags;
    }
    final String raw = node.getString();
    if (raw != null) {
      int flags = 0;
      for (final String part : raw.split("[,|\\s]+")) {
        flags |= flagFromString(part);
      }
      return flags;
    }
    return node.getInt(0);
  }

  private static int flagFromString(final String raw) {
    if (raw == null || raw.isEmpty()) {
      return 0;
    }
    switch (raw.trim().toUpperCase(Locale.ROOT)) {
      case "CASE_INSENSITIVE":
      case "I":
        return Pattern.CASE_INSENSITIVE;
      case "MULTILINE":
      case "M":
        return Pattern.MULTILINE;
      case "DOTALL":
      case "S":
        return Pattern.DOTALL;
      case "UNICODE_CASE":
      case "U":
        return Pattern.UNICODE_CASE;
      case "CANON_EQ":
        return Pattern.CANON_EQ;
      case "UNIX_LINES":
      case "D":
        return Pattern.UNIX_LINES;
      case "LITERAL":
        return Pattern.LITERAL;
      case "UNICODE_CHARACTER_CLASS":
        return Pattern.UNICODE_CHARACTER_CLASS;
      case "COMMENTS":
      case "X":
        return Pattern.COMMENTS;
      default:
        return 0;
    }
  }

  /**
   * Serializes a regular expression pattern to a configuration node.
   *
   * @param type the source type
   * @param obj the pattern to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final Pattern obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    if (obj.flags() == 0) {
      node.set(obj.pattern());
      return;
    }
    node.node("pattern").set(obj.pattern());
    node.node("flags").set(obj.flags());
  }
}
