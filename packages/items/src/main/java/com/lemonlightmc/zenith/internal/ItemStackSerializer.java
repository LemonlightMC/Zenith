package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

/**
 * Configurate serializer for {@link ItemStack} values.
 */
public final class ItemStackSerializer implements TypeSerializer<ItemStack> {

  /**
   * Shared serializer instance.
   */
  public static final ItemStackSerializer INSTANCE = new ItemStackSerializer();

  private ItemStackSerializer() {
  }

  /**
   * Deserializes an {@link ItemStack} from a configuration node.
   *
   * @param type the target type being deserialized
   * @param node the configuration node to read from
   * @return the deserialized item stack, or {@code null} if the node is null or empty
   * @throws SerializationException if the node cannot be converted into an item stack
   */
  @Override
  public ItemStack deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    if (!node.isMap()) {
      throw new SerializationException(node, ItemStack.class, "ItemStack must be a mapping");
    }
    try {
      final Map<String, Object> map = toMap(node);
      return ItemStack.deserialize(map);
    } catch (final SerializationException e) {
      throw e;
    } catch (final Exception ex) {
      throw new SerializationException(node, ItemStack.class, "Failed to deserialize ItemStack", ex);
    }
  }

  /**
   * Serializes an {@link ItemStack} into a configuration node.
   *
   * @param type the source type being serialized
   * @param obj the item stack to serialize
   * @param node the configuration node to write to
   * @throws SerializationException if the item stack cannot be serialized
   */
  @Override
  public void serialize(final Type type, final ItemStack obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    try {
      fromMap(obj.serialize(), node);
    } catch (final Exception ex) {
      throw new SerializationException(node, ItemStack.class, "Failed to serialize ItemStack", ex);
    }
  }

  private static Map<String, Object> toMap(final ConfigurationNode node) throws SerializationException {
    final Map<String, Object> out = new LinkedHashMap<>();
    for (final Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
      final String key = String.valueOf(entry.getKey());
      out.put(key, toObject(entry.getValue()));
    }
    return out;
  }

  private static Object toObject(final ConfigurationNode node) throws SerializationException {
    if (node == null || node.isNull() || node.virtual()) {
      return null;
    }
    if (node.isMap()) {
      return toMap(node);
    }
    if (node.isList()) {
      final java.util.List<Object> list = new java.util.ArrayList<>();
      for (final ConfigurationNode child : node.childrenList()) {
        list.add(toObject(child));
      }
      return list;
    }
    final Object raw = node.raw();
    return raw;
  }

  private static void fromMap(final Map<String, Object> map, final ConfigurationNode node) throws SerializationException {
    if (map == null) {
      node.set(null);
      return;
    }
    for (final Map.Entry<String, Object> entry : map.entrySet()) {
      final ConfigurationNode child = node.node(entry.getKey());
      fromObject(entry.getValue(), child);
    }
  }

  private static void fromObject(final Object value, final ConfigurationNode node) throws SerializationException {
    if (value == null) {
      node.set(null);
      return;
    }
    if (value instanceof Map) {
      node.set(null);
      for (final Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
        fromObject(e.getValue(), node.node(String.valueOf(e.getKey())));
      }
      return;
    }
    if (value instanceof ConfigurationSerializable) {
      final Map<String, Object> ser = new LinkedHashMap<>(((ConfigurationSerializable) value).serialize());
      // Keep type info if present in typical Bukkit serialized form? deserialize() of nested usually needs == key
      // but ConfigurationSerializable.serialize() may not include it; best-effort store as map.
      fromMap(ser, node);
      return;
    }
    if (value instanceof java.util.Collection) {
      node.set(null);
      int i = 0;
      for (final Object o : (java.util.Collection<?>) value) {
        fromObject(o, node.node(i++));
      }
      // convert indexed map to list
      final java.util.List<Object> list = new java.util.ArrayList<>();
      for (final ConfigurationNode child : node.childrenList()) {
        list.add(toObject(child));
      }
      node.setList(Object.class, list);
      return;
    }
    if (value instanceof Enum) {
      node.set(((Enum<?>) value).name());
      return;
    }
    node.set(value);
  }
}
