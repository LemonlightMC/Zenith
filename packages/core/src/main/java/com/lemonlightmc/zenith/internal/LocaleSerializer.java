package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.Locale;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.StringUtils;

/**
 * Serializes and deserializes {@link Locale} values.
 */
public final class LocaleSerializer implements TypeSerializer<Locale> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final LocaleSerializer INSTANCE = new LocaleSerializer();

  private LocaleSerializer() {
  }

  /**
   * Deserializes a locale from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the parsed locale, or {@code null} if unavailable
   * @throws SerializationException if the value cannot be parsed as a locale
   */
  @Override
  public Locale deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    final Locale parsed = StringUtils.parseLocale(node.getString());
    if (parsed != null) {
      return parsed;
    }
    throw new SerializationException(node, Locale.class, "Invalid locale: " + node.getString());
  }

  /**
   * Serializes a locale to a configuration node.
   *
   * @param type the source type
   * @param obj  the locale to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails
   */
  @Override
  public void serialize(final Type type, final Locale obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }

    final String tag = obj.toString();
    node.set(tag == null || tag.isEmpty() ? obj.toString() : tag);
  }
}
