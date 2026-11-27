package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.items.ItemStackBuilder;

/**
 * Configurate serializer for {@link ItemStack} values backed by
 * {@link ItemStackBuilder}.
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

    if (!node.isMap() && !node.isList()) {
      final Material material = parseMaterial(node.getString(), node);
      if (material == null || material.isAir()) {
        throw new SerializationException(node, ItemStack.class, "ItemStack material cannot be null or air");
      }
      return ItemStackBuilder.of(material).build();
    }

    if (!node.isMap()) {
      throw new SerializationException(node, ItemStack.class, "ItemStack must be a mapping or material string");
    }

    // Compatibility fallback: Bukkit's native ConfigurationSerializable ItemStack format.
    if (looksLikeBukkitSerializedItem(node)) {
      try {
        return ItemStack.deserialize(toPlainMap(node));
      } catch (final Exception ex) {
        throw new SerializationException(node, ItemStack.class, "Failed to deserialize Bukkit-format ItemStack", ex);
      }
    }

    final ConfigurationNode materialNode = first(node, "material", "type", "id", "mat", "item");
    if (materialNode.virtual() || materialNode.isNull()) {
      throw new SerializationException(node, ItemStack.class, "ItemStack requires a material/type");
    }
    final Material material = parseMaterial(nodeValueToString(materialNode), materialNode);
    if (material == null || material.isAir()) {
      throw new SerializationException(materialNode, ItemStack.class,
          "Unknown or invalid material: " + nodeValueToString(materialNode));
    }

    final int amount = Math.max(1, first(node, "amount", "count", "quantity").getInt(1));
    final ItemStackBuilder<?> builder = ItemStackBuilder.of(material, amount);

    applyString(builder, first(node, "name", "display-name", "display_name", "displayname", "custom-name"));
    applyStringItemName(builder, first(node, "item-name", "item_name", "itemname"));
    applyLore(builder, first(node, "lore"));

    if (hasAny(node, "enchants", "enchantments", "enchantment")) {
      applyEnchantments(builder, first(node, "enchants", "enchantments", "enchantment"), false, node);
    }
    if (hasAny(node, "stored-enchants", "stored-enchantments", "stored_enchants", "stored_enchantments")) {
      applyEnchantments(builder,
          first(node, "stored-enchants", "stored-enchantments", "stored_enchants", "stored_enchantments"), true,
          node);
    }

    if (hasAny(node, "flags", "item-flags", "item_flags", "hide-flags", "hide_flags")) {
      applyFlags(builder, first(node, "flags", "item-flags", "item_flags", "hide-flags", "hide_flags"));
    }

    if (hasAny(node, "unbreakable")) {
      builder.unbreakable(first(node, "unbreakable").getBoolean(false));
    }
    if (hasAny(node, "damage")) {
      builder.damage(first(node, "damage").getInt(0));
    }
    if (hasAny(node, "max-damage", "max_damage")) {
      final ConfigurationNode n = first(node, "max-damage", "max_damage");
      builder.maxDamage(n.isNull() ? null : n.getInt());
    }
    if (hasAny(node, "repair-cost", "repair_cost")) {
      builder.repairCost(first(node, "repair-cost", "repair_cost").getInt(0));
    }
    if (hasAny(node, "custom-model-data", "custom_model_data", "cmd", "model-data", "model_data")) {
      applyCustomModelData(builder, first(node, "custom-model-data", "custom_model_data", "cmd", "model-data", "model_data"));
    }
    if (hasAny(node, "max-stack-size", "max_stack_size")) {
      final ConfigurationNode n = first(node, "max-stack-size", "max_stack_size");
      builder.maxStackSize(n.isNull() ? null : n.getInt());
    }
    if (hasAny(node, "hide-tooltip", "hide_tooltip")) {
      builder.hideTooltip(first(node, "hide-tooltip", "hide_tooltip").getBoolean(false));
    }
    if (hasAny(node, "tooltip-style", "tooltip_style")) {
      builder.tooltipStyle(parseNamespacedKey(first(node, "tooltip-style", "tooltip_style")));
    }
    if (hasAny(node, "glider")) {
      builder.glider(first(node, "glider").getBoolean(false));
    }
    if (hasAny(node, "glint", "enchantment-glint", "enchantment-glint-override", "glow")) {
      applyGlint(builder, first(node, "glint", "enchantment-glint", "enchantment-glint-override", "glow"));
    }
    if (hasAny(node, "rarity")) {
      builder.rarity(parseRarity(first(node, "rarity")));
    }
    if (hasAny(node, "item-model", "item_model")) {
      final NamespacedKey model = parseNamespacedKey(first(node, "item-model", "item_model"));
      if (model != null) {
        builder.meta(meta -> {
          try {
            meta.setItemModel(model);
          } catch (final Throwable ignored) {
            // API not available
          }
        });
      }
    }

    if (hasAny(node, "attributes", "attribute-modifiers", "attribute_modifiers")) {
      applyAttributes(builder, first(node, "attributes", "attribute-modifiers", "attribute_modifiers"), node);
    }

    applyColor(builder, node);
    applySkull(builder, node);
    applyPotion(builder, node);
    applyBook(builder, node);
    applyBanner(builder, node);
    applyFirework(builder, node);
    applyFireworkEffect(builder, node);
    applyTrim(builder, node);

    // Generic escape hatch: allow applying raw Bukkit serialized meta is not supported here by design.
    return builder.build();
  }

  /**
   * Serializes an {@link ItemStack} into a configuration node.
   *
   * @param type the source type being serialized
   * @param obj the item stack to serialize
   * @param node the configuration node to write to
   * @throws SerializationException if the item stack cannot be serialized
   */
  @SuppressWarnings("deprecation")
  @Override
  public void serialize(final Type type, final ItemStack obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null || obj.getType().isAir()) {
      node.set(null);
      return;
    }

    node.set(null);
    node.node("material").set(namespacedOrName(obj.getType()));
    if (obj.getAmount() != 1) {
      node.node("amount").set(obj.getAmount());
    }

    final ItemMeta meta = obj.getItemMeta();
    if (meta == null) {
      return;
    }

    if (meta.hasDisplayName()) {
      node.node("name").set(meta.getDisplayName());
    }
    try {
      if (meta.hasItemName()) {
        node.node("item-name").set(meta.getItemName());
      }
    } catch (final Throwable ignored) {
    }
    if (meta.hasLore() && meta.getLore() != null && !meta.getLore().isEmpty()) {
      final ConfigurationNode loreNode = node.node("lore");
      loreNode.setList(String.class, new ArrayList<>(meta.getLore()));
    }

    if (!meta.getEnchants().isEmpty()) {
      writeEnchantMap(node.node("enchantments"), meta.getEnchants());
    }

    if (meta instanceof final org.bukkit.inventory.meta.EnchantmentStorageMeta esm
        && !esm.getStoredEnchants().isEmpty()) {
      writeEnchantMap(node.node("stored-enchantments"), esm.getStoredEnchants());
    }

    if (!meta.getItemFlags().isEmpty()) {
      final List<String> flags = new ArrayList<>();
      for (final ItemFlag flag : meta.getItemFlags()) {
        flags.add(flag.name());
      }
      node.node("flags").setList(String.class, flags);
    }

    if (meta.isUnbreakable()) {
      node.node("unbreakable").set(true);
    }
    if (meta instanceof final Damageable damageable) {
      if (damageable.hasDamage()) {
        node.node("damage").set(damageable.getDamage());
      }
      try {
        if (damageable.hasMaxDamage()) {
          node.node("max-damage").set(damageable.getMaxDamage());
        }
      } catch (final Throwable ignored) {
      }
    }
    if (meta instanceof final Repairable repairable && repairable.hasRepairCost()) {
      node.node("repair-cost").set(repairable.getRepairCost());
    }
    try {
      if (meta.hasCustomModelData()) {
        node.node("custom-model-data").set(meta.getCustomModelData());
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (meta.hasMaxStackSize()) {
        node.node("max-stack-size").set(meta.getMaxStackSize());
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (meta.isHideTooltip()) {
        node.node("hide-tooltip").set(true);
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (meta.hasTooltipStyle() && meta.getTooltipStyle() != null) {
        node.node("tooltip-style").set(meta.getTooltipStyle().toString());
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (meta.isGlider()) {
        node.node("glider").set(true);
      }
    } catch (final Throwable ignored) {
    }
    try {
      final Boolean glint = meta.getEnchantmentGlintOverride();
      if (glint != null) {
        node.node("glint").set(glint);
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (meta.hasRarity() && meta.getRarity() != null) {
        node.node("rarity").set(meta.getRarity().name());
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (meta.hasItemModel() && meta.getItemModel() != null) {
        node.node("item-model").set(meta.getItemModel().toString());
      }
    } catch (final Throwable ignored) {
    }

    serializeAttributes(node, meta);
    serializeColor(node, meta);
    serializeSkull(node, meta);
    serializePotion(node, meta);
    serializeBook(node, meta);
    serializeBanner(node, meta);
    serializeFirework(node, meta);
    serializeFireworkEffect(node, meta);
    serializeTrim(node, meta);
  }

  /* -------------------- deserialization helpers -------------------- */

  private static void applyString(final ItemStackBuilder<?> builder, final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return;
    }
    builder.name(node.getString());
  }

  private static void applyStringItemName(final ItemStackBuilder<?> builder, final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return;
    }
    builder.itemName(node.getString());
  }

  private static void applyLore(final ItemStackBuilder<?> builder, final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return;
    }
    if (node.isList()) {
      final List<String> lore = new ArrayList<>();
      for (final ConfigurationNode child : node.childrenList()) {
        final String line = child.getString();
        if (line != null) {
          lore.add(line);
        }
      }
      builder.lore(lore);
      return;
    }
    final String single = node.getString();
    if (single != null) {
      builder.lore(single);
    }
  }

  private static void applyEnchantments(final ItemStackBuilder<?> builder, final ConfigurationNode node,
      final boolean stored, final ConfigurationNode parent) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return;
    }
    if (node.isMap()) {
      for (final Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
        final String key = String.valueOf(entry.getKey());
        final Enchantment enchantment = parseEnchantment(key, entry.getValue());
        if (enchantment == null) {
          continue;
        }
        final int level = entry.getValue().getInt(1);
        if (stored) {
          builder.storedEnchant(enchantment, level, true);
        } else {
          builder.enchantUnsafe(enchantment, level);
        }
      }
      return;
    }
    if (node.isList()) {
      for (final ConfigurationNode child : node.childrenList()) {
        if (child.isMap()) {
          final String key = firstNonVirtual(child, "type", "id", "name", "enchant", "enchantment", "key")
              .getString();
          final Enchantment enchantment = parseEnchantment(key, child);
          if (enchantment == null) {
            continue;
          }
          final int level = first(child, "level", "lvl", "amount").getInt(1);
          if (stored) {
            builder.storedEnchant(enchantment, level, true);
          } else {
            builder.enchantUnsafe(enchantment, level);
          }
          continue;
        }
        final String raw = child.getString();
        if (raw == null || raw.isBlank()) {
          continue;
        }
        final String[] parts = raw.split("[:\\s]+");
        final Enchantment enchantment = parseEnchantment(parts[0], child);
        if (enchantment == null) {
          continue;
        }
        int level = 1;
        if (parts.length > 1) {
          try {
            level = Integer.parseInt(parts[1]);
          } catch (final NumberFormatException ignored) {
          }
        }
        if (stored) {
          builder.storedEnchant(enchantment, level, true);
        } else {
          builder.enchantUnsafe(enchantment, level);
        }
      }
      return;
    }
    final String raw = node.getString();
    if (raw != null && !raw.isBlank()) {
      final String[] parts = raw.split("[:\\s]+");
      final Enchantment enchantment = parseEnchantment(parts[0], node);
      if (enchantment != null) {
        int level = 1;
        if (parts.length > 1) {
          try {
            level = Integer.parseInt(parts[1]);
          } catch (final NumberFormatException ignored) {
          }
        }
        if (stored) {
          builder.storedEnchant(enchantment, level, true);
        } else {
          builder.enchantUnsafe(enchantment, level);
        }
      }
    }
  }

  private static void applyFlags(final ItemStackBuilder<?> builder, final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return;
    }
    if (node.isList()) {
      for (final ConfigurationNode child : node.childrenList()) {
        final String raw = child.getString();
        if (raw == null) {
          continue;
        }
        if (isHideAll(raw)) {
          builder.hideAllFlags();
          return;
        }
        final ItemFlag flag = parseItemFlag(raw);
        if (flag != null) {
          builder.flags(flag);
        }
      }
      return;
    }
    final String raw = node.getString();
    if (raw != null) {
      if (isHideAll(raw) || node.getBoolean(false)) {
        builder.hideAllFlags();
      } else {
        final ItemFlag flag = parseItemFlag(raw);
        if (flag != null) {
          builder.flags(flag);
        }
      }
    }
  }

  private static void applyCustomModelData(final ItemStackBuilder<?> builder, final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      builder.customModelData((Integer) null);
      return;
    }
    if (!node.isMap() && !node.isList()) {
      try {
        builder.customModelData(Integer.parseInt(String.valueOf(node.raw())));
      } catch (final Exception ignored) {
        // Ignore unsupported legacy component string forms
      }
    }
  }

  private static void applyGlint(final ItemStackBuilder<?> builder, final ConfigurationNode node) {
    if (node == null || node.virtual()) {
      return;
    }
    if (node.isNull()) {
      builder.resetGlint();
      return;
    }
    final String raw = node.getString();
    if (raw != null && ("reset".equalsIgnoreCase(raw) || "default".equalsIgnoreCase(raw) || "null".equalsIgnoreCase(raw))) {
      builder.resetGlint();
      return;
    }
    builder.glint(node.getBoolean());
  }

  private static void applyAttributes(final ItemStackBuilder<?> builder, final ConfigurationNode node,
      final ConfigurationNode parent) {
    if (node == null || node.virtual() || node.isNull()) {
      return;
    }
    if (node.isList()) {
      for (final ConfigurationNode child : node.childrenList()) {
        applyAttributeEntry(builder, null, child);
      }
      return;
    }
    if (node.isMap()) {
      for (final Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
        final String key = String.valueOf(entry.getKey());
        final ConfigurationNode val = entry.getValue();
        if (val.isList()) {
          for (final ConfigurationNode child : val.childrenList()) {
            applyAttributeEntry(builder, key, child);
          }
        } else {
          applyAttributeEntry(builder, key, val);
        }
      }
    }
  }

  @SuppressWarnings("deprecation")
  private static void applyAttributeEntry(final ItemStackBuilder<?> builder, final String attributeKey,
      final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return;
    }
    String attrName = attributeKey;
    if (node.isMap()) {
      final ConfigurationNode typeNode = first(node, "attribute", "type", "name", "id", "key");
      if (!typeNode.virtual() && !typeNode.isNull()) {
        attrName = nodeValueToString(typeNode);
      }
    }
    final Attribute attribute = parseAttribute(attrName);
    if (attribute == null) {
      return;
    }
    if (!node.isMap()) {
      final double amount = node.getDouble(0.0);
      final NamespacedKey key = attributeKeyToModifierKey(attribute);
      final AttributeModifier modifier = createAttributeModifier(key, attribute, amount,
          AttributeModifier.Operation.ADD_NUMBER, null);
      if (modifier != null) {
        builder.attribute(attribute, modifier);
      }
      return;
    }

    final double amount = first(node, "amount", "value").getDouble(0.0);
    final AttributeModifier.Operation operation = parseAttributeOperation(
        first(node, "operation", "op").getString("add_number"));
    final String slotRaw = first(node, "slot", "slot-group", "slot_group", "equipment-slot-group").getString();
    final EquipmentSlotGroup slotGroup = parseSlotGroup(slotRaw);
    final String name = first(node, "modifier-name", "name", "id").getString(attribute.getKey().getKey());
    final String keyRaw = first(node, "key", "modifier-key", "modifier_key").getString();
    NamespacedKey key = keyRaw == null ? null : parseNamespacedKeyString(keyRaw);
    if (key == null) {
      key = new NamespacedKey("zenith", sanitizeKey(name + "_" + attribute.getKey().getKey()));
    }

    final AttributeModifier modifier = createAttributeModifier(key, attribute, amount, operation, slotGroup);
    if (modifier != null) {
      builder.attribute(attribute, modifier);
    }
  }

  @SuppressWarnings("deprecation")
  private static AttributeModifier createAttributeModifier(final NamespacedKey key, final Attribute attribute,
      final double amount, final AttributeModifier.Operation operation, final EquipmentSlotGroup slotGroup) {
    // Modern API (1.20.5+): (NamespacedKey, double, Operation[, EquipmentSlotGroup])
    try {
      if (slotGroup != null) {
        return AttributeModifier.class
            .getConstructor(NamespacedKey.class, double.class, AttributeModifier.Operation.class,
                EquipmentSlotGroup.class)
            .newInstance(key, amount, operation, slotGroup);
      }
      return AttributeModifier.class
          .getConstructor(NamespacedKey.class, double.class, AttributeModifier.Operation.class)
          .newInstance(key, amount, operation);
    } catch (final Throwable ignored) {
    }
    // Legacy API: (UUID, String, double, Operation[, EquipmentSlot])
    try {
      final UUID uuid = stableUuid(key == null ? attribute.getKey().toString() : key.toString());
      final String name = key == null ? attribute.getKey().toString() : key.toString();
      if (slotGroup != null) {
        try {
          final java.lang.reflect.Field any = EquipmentSlotGroup.class.getField("ANY");
          final Object anyGroup = any.get(null);
          // try with EquipmentSlot for older APIs
          final Class<?> slotClass = Class.forName("org.bukkit.inventory.EquipmentSlot");
          final org.bukkit.inventory.EquipmentSlot slot = slotGroupToEquipmentSlot(slotGroup);
          if (slot != null) {
            return AttributeModifier.class
                .getConstructor(UUID.class, String.class, double.class, AttributeModifier.Operation.class, slotClass)
                .newInstance(uuid, name, amount, operation, slot);
          }
        } catch (final Throwable ignored) {
        }
      }
      return AttributeModifier.class
          .getConstructor(UUID.class, String.class, double.class, AttributeModifier.Operation.class)
          .newInstance(uuid, name, amount, operation);
    } catch (final Throwable ignored) {
    }
    return null;
  }

  private static org.bukkit.inventory.EquipmentSlot slotGroupToEquipmentSlot(final EquipmentSlotGroup group) {
    if (group == null) {
      return null;
    }
    try {
      final String s = group.toString().toUpperCase(Locale.ROOT);
      return org.bukkit.inventory.EquipmentSlot.valueOf(s);
    } catch (final Throwable ignored) {
    }
    return null;
  }

  private static UUID stableUuid(final String seed) {
    try {
      return UUID.nameUUIDFromBytes(seed.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    } catch (final Exception e) {
      return UUID.randomUUID();
    }
  }

  private static void applyColor(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode colorNode = first(root, "color", "leather-color", "leather_color", "dye-color", "dye_color");
    if (colorNode.virtual() || colorNode.isNull()) {
      return;
    }
    final Color color = parseColor(colorNode);
    if (color == null) {
      return;
    }
    builder.meta(meta -> {
      if (meta instanceof final LeatherArmorMeta lam) {
        lam.setColor(color);
      }
      if (meta instanceof final PotionMeta pm) {
        pm.setColor(color);
      }
      try {
        if (meta instanceof final org.bukkit.inventory.meta.MapMeta mm) {
          mm.setColor(color);
        }
      } catch (final Throwable ignored) {
      }
    });
  }

  @SuppressWarnings("deprecation")
  private static void applySkull(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode skullNode = first(root, "skull", "head", "player-head", "player_head");
    final ConfigurationNode ownerNode = first(root, "skull-owner", "skull_owner", "owner", "player");
    final ConfigurationNode textureNode = first(root, "skull-texture", "skull_texture", "texture", "base64", "head-texture");
    if ((skullNode.virtual() || skullNode.isNull()) && (ownerNode.virtual() || ownerNode.isNull())
        && (textureNode.virtual() || textureNode.isNull())) {
      return;
    }

    builder.meta(SkullMeta.class, meta -> {
      ConfigurationNode owner = ownerNode;
      ConfigurationNode texture = textureNode;
      if (!skullNode.virtual() && skullNode.isMap()) {
        if (owner.virtual() || owner.isNull()) {
          owner = first(skullNode, "owner", "name", "player");
        }
        if (texture.virtual() || texture.isNull()) {
          texture = first(skullNode, "texture", "base64", "url", "value");
        }
      } else if (!skullNode.virtual() && !skullNode.isNull()) {
        owner = skullNode;
      }
      if (!owner.virtual() && !owner.isNull()) {
        final String raw = owner.getString();
        if (raw != null) {
          final UUID uuid = tryParseUuid(raw);
          if (uuid != null) {
            try {
              meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            } catch (final Throwable ex) {
              meta.setOwningPlayer(Bukkit.getOfflinePlayer(raw));
            }
          } else {
            try {
              meta.setOwningPlayer(Bukkit.getOfflinePlayer(raw));
            } catch (final Throwable ignored) {
            }
          }
        }
      }
      if (!texture.virtual() && !texture.isNull()) {
        applySkullTexture(meta, texture.getString());
      }
    });
  }

  @SuppressWarnings("deprecation")
  private static void applySkullTexture(final SkullMeta meta, final String texture) {
    if (texture == null || texture.isBlank()) {
      return;
    }
    final String raw = texture.trim();
    try {
      final org.bukkit.profile.PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "skull");
      final org.bukkit.profile.PlayerTextures textures = profile.getTextures();
      if (raw.startsWith("http://") || raw.startsWith("https://")) {
        textures.setSkin(java.net.URI.create(raw).toURL());
      } else {
        // Best-effort for base64 texture strings: try Paper's profile property API reflectively when present.
        try {
          final Class<?> propClass = Class.forName("com.destroystokyo.paper.profile.ProfileProperty");
          final Object prop = propClass.getConstructor(String.class, String.class).newInstance("textures", raw);
          final java.lang.reflect.Method setProperty = profile.getClass().getMethod("setProperty", String.class, propClass);
          setProperty.invoke(profile, "textures", prop);
        } catch (final Throwable ignored) {
          try {
            textures.setSkin(java.net.URI.create("http://textures.minecraft.net/texture/" + raw).toURL());
          } catch (final Throwable ignored2) {
          }
        }
      }
      profile.update().thenAccept(updated -> {
        try {
          meta.setOwnerProfile(updated);
        } catch (final Throwable ignored) {
        }
      });
      try {
        meta.setOwnerProfile(profile);
      } catch (final Throwable ignored) {
      }
    } catch (final Throwable ignored) {
      // Texture application not supported in this environment
    }
  }

  private static void applyPotion(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode potionNode = first(root, "potion", "potion-meta", "potion_meta");
    final boolean hasAny = !potionNode.virtual() && !potionNode.isNull()
        || hasAny(root, "base-potion-type", "base-potion", "potion-type", "base_potion_type", "potion_type")
        || hasAny(root, "custom-effects", "custom_effects", "potion-effects", "potion_effects")
        || hasAny(root, "potion-color", "potion_color");
    if (!hasAny) {
      return;
    }
    final ConfigurationNode src = (!potionNode.virtual() && potionNode.isMap()) ? potionNode : root;
    builder.meta(PotionMeta.class, meta -> {
      final ConfigurationNode colorNode = first(src, "color", "potion-color", "potion_color");
      if (!colorNode.virtual() && !colorNode.isNull()) {
        final Color color = parseColor(colorNode);
        if (color != null) {
          meta.setColor(color);
        }
      }
      final ConfigurationNode baseNode = first(src, "base-potion-type", "base-potion", "potion-type", "type",
          "base_potion_type", "potion_type");
      if (!baseNode.virtual() && !baseNode.isNull()) {
        applyBasePotionType(meta, baseNode.getString());
      }
      final ConfigurationNode effectsNode = first(src, "custom-effects", "custom_effects", "effects", "potion-effects",
          "potion_effects");
      if (!effectsNode.virtual() && !effectsNode.isNull()) {
        applyPotionEffects(meta, effectsNode);
      }
    });
  }

  private static void applyBasePotionType(final PotionMeta meta, final String raw) {
    if (raw == null || raw.isBlank()) {
      return;
    }
    final String normalized = raw.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    try {
      meta.setBasePotionType(PotionType.valueOf(normalized));
    } catch (final Throwable ignored) {
      try {
        final PotionType type = Registry.POTION.match(normalized.toLowerCase(Locale.ROOT));
        if (type != null) {
          meta.setBasePotionType(type);
        }
      } catch (final Throwable ignored2) {
      }
    }
  }

  private static void applyPotionEffects(final PotionMeta meta, final ConfigurationNode node) {
    if (node.isList()) {
      for (final ConfigurationNode child : node.childrenList()) {
        final PotionEffect effect = parsePotionEffectNode(child);
        if (effect != null) {
          meta.addCustomEffect(effect, true);
        }
      }
      return;
    }
    if (node.isMap()) {
      for (final Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
        final PotionEffect effect = parsePotionEffectEntry(String.valueOf(entry.getKey()), entry.getValue());
        if (effect != null) {
          meta.addCustomEffect(effect, true);
        }
      }
    }
  }

  private static PotionEffect parsePotionEffectNode(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    if (!node.isMap()) {
      return parsePotionEffectString(node.getString());
    }
    final String typeRaw = first(node, "type", "effect", "id", "name").getString();
    final PotionEffectType type = parsePotionEffectType(typeRaw);
    if (type == null) {
      return null;
    }
    final int duration = first(node, "duration", "time", "ticks").getInt(200);
    final int amplifier = first(node, "amplifier", "level", "strength", "power").getInt(0);
    final boolean ambient = first(node, "ambient").getBoolean(false);
    final boolean particles = first(node, "particles", "has-particles", "show-particles").getBoolean(true);
    final boolean icon = first(node, "icon", "has-icon", "show-icon").getBoolean(true);
    try {
      return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    } catch (final Throwable ex) {
      try {
        return new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier), ambient, particles);
      } catch (final Throwable ex2) {
        return null;
      }
    }
  }

  private static PotionEffect parsePotionEffectEntry(final String key, final ConfigurationNode value) {
    final PotionEffectType type = parsePotionEffectType(key);
    if (type == null) {
      return parsePotionEffectNode(value);
    }
    if (value == null || value.virtual() || value.isNull() || !value.isMap()) {
      final int level = value == null ? 1 : value.getInt(1);
      return new PotionEffect(type, 200, Math.max(0, level - 1), false, true, true);
    }
    final int duration = first(value, "duration", "time", "ticks").getInt(200);
    final int amplifier = first(value, "amplifier", "level", "strength", "power").getInt(0);
    final boolean ambient = first(value, "ambient").getBoolean(false);
    final boolean particles = first(value, "particles", "has-particles", "show-particles").getBoolean(true);
    final boolean icon = first(value, "icon", "has-icon", "show-icon").getBoolean(true);
    return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
  }

  private static PotionEffect parsePotionEffectString(final String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    final String[] parts = raw.split("[:\\s]+");
    final PotionEffectType type = parsePotionEffectType(parts[0]);
    if (type == null) {
      return null;
    }
    int duration = 200;
    int amplifier = 0;
    if (parts.length > 1) {
      try {
        duration = Integer.parseInt(parts[1]);
      } catch (final NumberFormatException ignored) {
      }
    }
    if (parts.length > 2) {
      try {
        amplifier = Integer.parseInt(parts[2]);
      } catch (final NumberFormatException ignored) {
      }
    }
    return new PotionEffect(type, duration, amplifier);
  }

  private static void applyBook(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode bookNode = first(root, "book", "book-meta", "book_meta");
    final ConfigurationNode src = (!bookNode.virtual() && bookNode.isMap()) ? bookNode : root;
    final boolean hasAny = hasAny(src, "title", "author", "pages", "generation")
        || (!bookNode.virtual() && bookNode.isMap());
    if (!hasAny) {
      return;
    }
    builder.meta(BookMeta.class, meta -> {
      final ConfigurationNode title = first(src, "title");
      if (!title.virtual() && !title.isNull()) {
        meta.setTitle(title.getString());
      }
      final ConfigurationNode author = first(src, "author");
      if (!author.virtual() && !author.isNull()) {
        meta.setAuthor(author.getString());
      }
      final ConfigurationNode generation = first(src, "generation");
      if (!generation.virtual() && !generation.isNull()) {
        try {
          meta.setGeneration(BookMeta.Generation.valueOf(generation.getString("").toUpperCase(Locale.ROOT)));
        } catch (final Throwable ignored) {
        }
      }
      final ConfigurationNode pages = first(src, "pages");
      if (!pages.virtual() && !pages.isNull()) {
        if (pages.isList()) {
          final List<String> list = new ArrayList<>();
          for (final ConfigurationNode child : pages.childrenList()) {
            final String page = child.getString();
            if (page != null) {
              list.add(page);
            }
          }
          meta.setPages(list);
        } else {
          final String page = pages.getString();
          if (page != null) {
            meta.setPages(page);
          }
        }
      }
    });
  }

  private static void applyBanner(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode bannerNode = first(root, "banner", "banner-meta", "banner_meta");
    final ConfigurationNode patternsNode = first(root, "patterns", "banner-patterns", "banner_patterns");
    if ((bannerNode.virtual() || bannerNode.isNull()) && (patternsNode.virtual() || patternsNode.isNull())) {
      return;
    }
    final ConfigurationNode src = (!bannerNode.virtual() && bannerNode.isMap()) ? bannerNode : root;
    final ConfigurationNode patterns = !patternsNode.virtual() && !patternsNode.isNull() ? patternsNode
        : first(src, "patterns", "banner-patterns", "banner_patterns");
    if (patterns.virtual() || patterns.isNull() || !patterns.isList()) {
      return;
    }
    builder.meta(BannerMeta.class, meta -> {
      final List<Pattern> list = new ArrayList<>();
      for (final ConfigurationNode child : patterns.childrenList()) {
        final Pattern pattern = parseBannerPattern(child);
        if (pattern != null) {
          list.add(pattern);
        }
      }
      if (!list.isEmpty()) {
        meta.setPatterns(list);
      }
    });
  }

  private static Pattern parseBannerPattern(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    if (!node.isMap()) {
      final String raw = node.getString();
      if (raw == null) {
        return null;
      }
      final String[] parts = raw.split("[:\\s]+");
      if (parts.length < 2) {
        return null;
      }
      final DyeColor color = parseDyeColor(parts[0]);
      final PatternType type = parsePatternType(parts[1]);
      if (color == null || type == null) {
        return null;
      }
      return new Pattern(color, type);
    }
    final DyeColor color = parseDyeColor(first(node, "color", "dye", "dye-color").getString());
    final PatternType type = parsePatternType(first(node, "pattern", "type", "pattern-type").getString());
    if (color == null || type == null) {
      return null;
    }
    return new Pattern(color, type);
  }

  private static void applyFirework(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode fireworkNode = first(root, "firework", "firework-meta", "firework_meta");
    final boolean hasAny = !fireworkNode.virtual() && !fireworkNode.isNull()
        || hasAny(root, "firework-power", "firework_power", "power") && hasAny(root, "firework-effects", "effects");
    if (!hasAny) {
      return;
    }
    final ConfigurationNode src = (!fireworkNode.virtual() && fireworkNode.isMap()) ? fireworkNode : root;
    builder.meta(FireworkMeta.class, meta -> {
      final ConfigurationNode powerNode = first(src, "power", "flight-duration", "flight_duration", "firework-power");
      if (!powerNode.virtual() && !powerNode.isNull()) {
        meta.setPower(Math.max(0, Math.min(255, powerNode.getInt(1))));
      }
      final ConfigurationNode effectsNode = first(src, "effects", "firework-effects", "firework_effects");
      if (!effectsNode.virtual() && !effectsNode.isNull() && effectsNode.isList()) {
        meta.clearEffects();
        for (final ConfigurationNode child : effectsNode.childrenList()) {
          final org.bukkit.FireworkEffect effect = parseFireworkEffect(child);
          if (effect != null) {
            meta.addEffect(effect);
          }
        }
      }
    });
  }

  private static void applyFireworkEffect(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode starNode = first(root, "firework-effect", "firework_effect", "star-effect", "star_effect",
        "effect");
    if (starNode.virtual() || starNode.isNull() || !starNode.isMap()) {
      // root itself might describe a star in some formats, but avoid ambiguity with firework.effects entries
      if (root.hasChild("firework-star") || root.hasChild("firework_star")) {
        final ConfigurationNode alt = first(root, "firework-star", "firework_star");
        if (!alt.virtual() && alt.isMap()) {
          builder.meta(FireworkEffectMeta.class, meta -> {
            final org.bukkit.FireworkEffect effect = parseFireworkEffect(alt);
            if (effect != null) {
              meta.setEffect(effect);
            }
          });
        }
      }
      return;
    }
    // Only apply as star meta if it looks like an effect definition and not a firework wrapper
    if (hasAny(starNode, "colors", "color", "type", "shape", "flicker", "trail")) {
      builder.meta(FireworkEffectMeta.class, meta -> {
        final org.bukkit.FireworkEffect effect = parseFireworkEffect(starNode);
        if (effect != null) {
          meta.setEffect(effect);
        }
      });
    }
  }

  private static org.bukkit.FireworkEffect parseFireworkEffect(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull() || !node.isMap()) {
      return null;
    }
    final String typeRaw = first(node, "type", "shape").getString("BALL");
    org.bukkit.FireworkEffect.Type type = org.bukkit.FireworkEffect.Type.BALL;
    if (typeRaw != null) {
      try {
        type = org.bukkit.FireworkEffect.Type.valueOf(typeRaw.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
      } catch (final Exception ignored) {
      }
    }
    final List<Color> colors = parseColors(first(node, "colors", "color"));
    if (colors.isEmpty()) {
      return null;
    }
    final List<Color> fades = parseColors(first(node, "fade-colors", "fade_colors", "fades", "fade"));
    final boolean flicker = first(node, "flicker").getBoolean(false);
    final boolean trail = first(node, "trail").getBoolean(false);
    try {
      final org.bukkit.FireworkEffect.Builder b = org.bukkit.FireworkEffect.builder().with(type).withColor(colors)
          .flicker(flicker).trail(trail);
      if (!fades.isEmpty()) {
        b.withFade(fades);
      }
      return b.build();
    } catch (final Exception e) {
      return null;
    }
  }

  private static void applyTrim(final ItemStackBuilder<?> builder, final ConfigurationNode root) {
    final ConfigurationNode trimNode = first(root, "trim", "armor-trim", "armor_trim");
    if (trimNode.virtual() || trimNode.isNull() || !trimNode.isMap()) {
      return;
    }
    final String materialRaw = first(trimNode, "material", "trim-material", "trim_material").getString();
    final String patternRaw = first(trimNode, "pattern", "trim-pattern", "trim_pattern").getString();
    if (materialRaw == null || patternRaw == null) {
      return;
    }
    final TrimMaterial material = parseTrimMaterial(materialRaw);
    final TrimPattern pattern = parseTrimPattern(patternRaw);
    if (material == null || pattern == null) {
      return;
    }
    builder.meta(ArmorMeta.class, meta -> {
      try {
        meta.setTrim(new ArmorTrim(material, pattern));
      } catch (final Throwable ignored) {
      }
    });
  }

  /* -------------------- serialization helpers -------------------- */

  private static void writeEnchantMap(final ConfigurationNode node, final Map<Enchantment, Integer> enchants)
      throws SerializationException {
    node.set(null);
    for (final Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
      if (entry.getKey() == null) {
        continue;
      }
      node.node(enchantKey(entry.getKey())).set(entry.getValue() == null ? 1 : entry.getValue());
    }
  }

  @SuppressWarnings("deprecation")
  private static void serializeAttributes(final ConfigurationNode node, final ItemMeta meta) {
    try {
      if (!meta.hasAttributeModifiers() || meta.getAttributeModifiers() == null
          || meta.getAttributeModifiers().isEmpty()) {
        return;
      }
      final ConfigurationNode attrs = node.node("attributes");
      attrs.set(null);
      for (final Map.Entry<Attribute, ? extends Collection<AttributeModifier>> entry : meta.getAttributeModifiers()
          .asMap().entrySet()) {
        if (entry.getKey() == null || entry.getValue() == null) {
          continue;
        }
        String attrNodeKey;
        try {
          attrNodeKey = entry.getKey().getKey().toString();
        } catch (final Throwable ignored) {
          attrNodeKey = entry.getKey().name();
        }
        final ConfigurationNode list = attrs.node(enchantKeyLike(attrNodeKey)).set(null);
        int idx = 0;
        for (final AttributeModifier mod : entry.getValue()) {
          if (mod == null) {
            continue;
          }
          final ConfigurationNode m = list.node(idx++);
          try {
            m.node("key").set(mod.getKey().toString());
          } catch (final Throwable ignored) {
            m.node("name").set(mod.getName());
          }
          m.node("amount").set(mod.getAmount());
          m.node("operation").set(mod.getOperation().name());
          try {
            if (mod.getSlotGroup() != null) {
              m.node("slot-group").set(mod.getSlotGroup().toString());
            }
          } catch (final Throwable ignored) {
          }
        }
      }
    } catch (final Throwable ignored) {
    }
  }

  private static void serializeColor(final ConfigurationNode node, final ItemMeta meta) {
    try {
      if (meta instanceof final LeatherArmorMeta lam) {
        node.node("color").set(colorToHex(lam.getColor()));
      }
    } catch (final Throwable ignored) {
    }
  }

  private static void serializeSkull(final ConfigurationNode node, final ItemMeta meta) {
    if (!(meta instanceof final SkullMeta skull)) {
      return;
    }
    try {
      if (skull.hasOwner() && skull.getOwningPlayer() != null && skull.getOwningPlayer().getUniqueId() != null) {
        node.node("skull-owner").set(skull.getOwningPlayer().getUniqueId().toString());
        return;
      }
    } catch (final Throwable ignored) {
    }
    try {
      final org.bukkit.OfflinePlayer owner = skull.getOwningPlayer();
      if (owner != null && owner.getName() != null) {
        node.node("skull-owner").set(owner.getName());
      }
    } catch (final Throwable ignored) {
    }
  }

  @SuppressWarnings("deprecation")
  private static void serializePotion(final ConfigurationNode node, final ItemMeta meta) {
    if (!(meta instanceof final PotionMeta potion)) {
      return;
    }
    final ConfigurationNode p = node.node("potion");
    boolean has = false;
    try {
      if (potion.hasColor() && potion.getColor() != null) {
        p.node("color").set(colorToHex(potion.getColor()));
        has = true;
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (potion.getBasePotionType() != null) {
        p.node("base-potion-type").set(potion.getBasePotionType().name());
        has = true;
      }
    } catch (final Throwable ignored) {
    }
    try {
      if (potion.hasCustomEffects()) {
        final ConfigurationNode effects = p.node("custom-effects");
        int i = 0;
        for (final PotionEffect effect : potion.getCustomEffects()) {
          final ConfigurationNode e = effects.node(i++);
          e.node("type").set(effect.getType().getKey().toString());
          e.node("duration").set(effect.getDuration());
          e.node("amplifier").set(effect.getAmplifier());
          e.node("ambient").set(effect.isAmbient());
          e.node("particles").set(effect.hasParticles());
          e.node("icon").set(effect.hasIcon());
        }
        has = true;
      }
    } catch (final Throwable ignored) {
    }
    if (!has) {
      node.removeChild("potion");
    }
  }

  private static void serializeBook(final ConfigurationNode node, final ItemMeta meta) throws SerializationException {
    if (!(meta instanceof final BookMeta book)) {
      return;
    }
    final ConfigurationNode b = node.node("book");
    boolean has = false;
    if (book.hasTitle() && book.getTitle() != null) {
      b.node("title").set(book.getTitle());
      has = true;
    }
    if (book.hasAuthor() && book.getAuthor() != null) {
      b.node("author").set(book.getAuthor());
      has = true;
    }
    try {
      if (book.hasGeneration() && book.getGeneration() != null) {
        b.node("generation").set(book.getGeneration().name());
        has = true;
      }
    } catch (final Throwable ignored) {
    }
    if (book.hasPages()) {
      b.node("pages").setList(String.class, new ArrayList<>(book.getPages()));
      has = true;
    }
    if (!has) {
      node.removeChild("book");
    }
  }

  @SuppressWarnings("deprecation")
  private static void serializeBanner(final ConfigurationNode node, final ItemMeta meta) throws SerializationException {
    if (!(meta instanceof final BannerMeta banner) || banner.getPatterns().isEmpty()) {
      return;
    }
    final ConfigurationNode list = node.node("banner").node("patterns");
    int i = 0;
    for (final Pattern pattern : banner.getPatterns()) {
      if (pattern == null || pattern.getColor() == null || pattern.getPattern() == null) {
        continue;
      }
      final ConfigurationNode p = list.node(i++);
      p.node("color").set(pattern.getColor().name());
      p.node("pattern").set(pattern.getPattern().name());
    }
  }

  private static void serializeFirework(final ConfigurationNode node, final ItemMeta meta) throws SerializationException {
    if (!(meta instanceof final FireworkMeta fw)) {
      return;
    }
    final ConfigurationNode f = node.node("firework");
    f.node("power").set(fw.getPower());
    if (fw.hasEffects()) {
      final ConfigurationNode effects = f.node("effects");
      int i = 0;
      for (final org.bukkit.FireworkEffect effect : fw.getEffects()) {
        writeFireworkEffect(effects.node(i++), effect);
      }
    }
  }

  private static void serializeFireworkEffect(final ConfigurationNode node, final ItemMeta meta)
      throws SerializationException {
    if (!(meta instanceof final FireworkEffectMeta star) || !star.hasEffect() || star.getEffect() == null) {
      return;
    }
    writeFireworkEffect(node.node("firework-effect"), star.getEffect());
  }

  private static void writeFireworkEffect(final ConfigurationNode node, final org.bukkit.FireworkEffect effect)
      throws SerializationException {
    node.node("type").set(effect.getType().name());
    node.node("flicker").set(effect.hasFlicker());
    node.node("trail").set(effect.hasTrail());
    final List<String> colors = new ArrayList<>();
    for (final Color c : effect.getColors()) {
      colors.add(colorToHex(c));
    }
    node.node("colors").setList(String.class, colors);
    if (!effect.getFadeColors().isEmpty()) {
      final List<String> fades = new ArrayList<>();
      for (final Color c : effect.getFadeColors()) {
        fades.add(colorToHex(c));
      }
      node.node("fade-colors").setList(String.class, fades);
    }
  }

  @SuppressWarnings("deprecation")
  private static void serializeTrim(final ConfigurationNode node, final ItemMeta meta) {
    if (!(meta instanceof final ArmorMeta armor)) {
      return;
    }
    try {
      if (!armor.hasTrim() || armor.getTrim() == null) {
        return;
      }
      final ArmorTrim trim = armor.getTrim();
      final ConfigurationNode t = node.node("trim");
      if (trim.getMaterial() != null) {
        try {
          t.node("material").set(trim.getMaterial().getKey().toString());
        } catch (final Throwable ignored) {
          t.node("material").set(String.valueOf(trim.getMaterial()));
        }
      }
      if (trim.getPattern() != null) {
        try {
          t.node("pattern").set(trim.getPattern().getKey().toString());
        } catch (final Throwable ignored) {
          t.node("pattern").set(String.valueOf(trim.getPattern()));
        }
      }
    } catch (final Throwable ignored) {
    }
  }

  /* -------------------- parsing primitives -------------------- */

  private static Material parseMaterial(final String raw, final ConfigurationNode node) throws SerializationException {
    if (raw == null || raw.trim().isEmpty()) {
      return null;
    }
    String s = raw.trim();
    if (s.indexOf(':') >= 0) {
      final NamespacedKey key = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
      if (key != null) {
        final Material byKey = Material.matchMaterial(key.toString());
        if (byKey != null) {
          return byKey;
        }
        final Material byKeyName = Material.matchMaterial(key.getKey());
        if (byKeyName != null) {
          return byKeyName;
        }
      }
    }
    final Material material = Material.matchMaterial(s);
    if (material != null) {
      return material;
    }
    try {
      return Material.valueOf(s.toUpperCase(Locale.ROOT).replace('-', '_').replace('.', '_').replace(' ', '_'));
    } catch (final Exception ex) {
      return null;
    }
  }

  @SuppressWarnings("deprecation")
  private static Enchantment parseEnchantment(final String raw, final ConfigurationNode node) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    final String s = raw.trim();
    final NamespacedKey key = parseNamespacedKeyString(s);
    if (key != null) {
      try {
        final Enchantment e = Registry.ENCHANTMENT.get(key);
        if (e != null) {
          return e;
        }
      } catch (final Throwable ignored) {
      }
    }
    try {
      final Enchantment byName = org.bukkit.enchantments.Enchantment.getByName(s.toUpperCase(Locale.ROOT));
      if (byName != null) {
        return byName;
      }
    } catch (final Throwable ignored) {
    }
    return null;
  }

  @SuppressWarnings("deprecation")
  private static PotionEffectType parsePotionEffectType(final String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    final String s = raw.trim();
    final NamespacedKey key = parseNamespacedKeyString(s);
    if (key != null) {
      try {
        final PotionEffectType t = Registry.EFFECT.get(key);
        if (t != null) {
          return t;
        }
      } catch (final Throwable ignored) {
      }
    }
    try {
      return PotionEffectType.getByName(s.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_'));
    } catch (final Throwable ignored) {
    }
    return null;
  }

  private static ItemFlag parseItemFlag(final String raw) {
    if (raw == null) {
      return null;
    }
    try {
      return ItemFlag.valueOf(raw.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_'));
    } catch (final Exception e) {
      return null;
    }
  }

  private static ItemRarity parseRarity(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    final String raw = node.getString();
    if (raw == null) {
      return null;
    }
    try {
      return ItemRarity.valueOf(raw.trim().toUpperCase(Locale.ROOT));
    } catch (final Exception e) {
      return null;
    }
  }

  @SuppressWarnings("deprecation")
  private static Attribute parseAttribute(final String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    final String s = raw.trim();
    final NamespacedKey key = parseNamespacedKeyString(s);
    if (key != null) {
      try {
        final Attribute a = Registry.ATTRIBUTE.get(key);
        if (a != null) {
          return a;
        }
      } catch (final Throwable ignored) {
      }
    }
    final String normalized = s.toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    try {
      return Attribute.valueOf(normalized);
    } catch (final Exception ignored) {
    }
    try {
      return Attribute.valueOf(normalized.startsWith("GENERIC_") || normalized.startsWith("PLAYER_")
          || normalized.startsWith("HORSE_") || normalized.startsWith("ZOMBIE_") ? normalized : "GENERIC_" + normalized);
    } catch (final Exception ignored) {
    }
    return null;
  }

  private static AttributeModifier.Operation parseAttributeOperation(final String raw) {
    if (raw == null) {
      return AttributeModifier.Operation.ADD_NUMBER;
    }
    final String n = raw.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    try {
      return AttributeModifier.Operation.valueOf(n);
    } catch (final Exception ignored) {
    }
    if ("ADD".equals(n) || "ADD_VALUE".equals(n) || "ADD_NUMBER".equals(n)) {
      return AttributeModifier.Operation.ADD_NUMBER;
    }
    if ("MULTIPLY_BASE".equals(n) || "ADD_SCALAR".equals(n)) {
      return AttributeModifier.Operation.ADD_SCALAR;
    }
    if ("MULTIPLY".equals(n) || "MULTIPLY_TOTAL".equals(n) || "MULTIPLY_SCALAR_1".equals(n)) {
      return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
    }
    return AttributeModifier.Operation.ADD_NUMBER;
  }

  private static EquipmentSlotGroup parseSlotGroup(final String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      return (EquipmentSlotGroup) EquipmentSlotGroup.class.getField(raw.trim().toUpperCase(Locale.ROOT)).get(null);
    } catch (final Throwable ignored) {
    }
    try {
      final String n = raw.trim().toLowerCase(Locale.ROOT);
      return EquipmentSlotGroup.getByName(n);
    } catch (final Throwable ignored) {
    }
    return null;
  }

  private static NamespacedKey parseNamespacedKey(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    if (node.isMap()) {
      final String key = first(node, "key", "value").getString();
      final String ns = first(node, "namespace").getString(NamespacedKey.MINECRAFT);
      if (key == null) {
        return null;
      }
      if (key.contains(":")) {
        return parseNamespacedKeyString(key);
      }
      try {
        return new NamespacedKey(ns.toLowerCase(Locale.ROOT), key.toLowerCase(Locale.ROOT));
      } catch (final Exception e) {
        return null;
      }
    }
    return parseNamespacedKeyString(node.getString());
  }

  private static NamespacedKey parseNamespacedKeyString(final String raw) {
    if (raw == null || raw.trim().isEmpty()) {
      return null;
    }
    final String s = raw.trim();
    try {
      final NamespacedKey k = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
      if (k != null) {
        return k;
      }
    } catch (final Exception ignored) {
    }
    try {
      return NamespacedKey.minecraft(s.toLowerCase(Locale.ROOT));
    } catch (final Exception ignored) {
    }
    return null;
  }

  private static Color parseColor(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    if (node.isMap()) {
      if (hasAny(node, "r", "red") || hasAny(node, "g", "green") || hasAny(node, "b", "blue")) {
        final int r = clamp(first(node, "r", "red").getInt(0));
        final int g = clamp(first(node, "g", "green").getInt(0));
        final int b = clamp(first(node, "b", "blue").getInt(0));
        return Color.fromRGB(r, g, b);
      }
    }
    if (node.raw() instanceof Number) {
      try {
        return Color.fromRGB(((Number) node.raw()).intValue() & 0xFFFFFF);
      } catch (final Exception ignored) {
      }
    }
    String raw = node.getString();
    if (raw == null) {
      return null;
    }
    raw = raw.trim();
    if (raw.isEmpty()) {
      return null;
    }
    try {
      if (raw.startsWith("#")) {
        return Color.fromRGB(Integer.parseInt(raw.substring(1), 16));
      }
      if (raw.length() == 6 && isHex(raw)) {
        return Color.fromRGB(Integer.parseInt(raw, 16));
      }
      if (raw.length() == 3 && isHex(raw)) {
        final String hex = "" + raw.charAt(0) + raw.charAt(0) + raw.charAt(1) + raw.charAt(1) + raw.charAt(2)
            + raw.charAt(2);
        return Color.fromRGB(Integer.parseInt(hex, 16));
      }
    } catch (final Exception ignored) {
    }
    if (raw.indexOf(',') >= 0) {
      final String[] parts = raw.split(",");
      if (parts.length >= 3) {
        try {
          return Color.fromRGB(clamp(Integer.parseInt(parts[0].trim())), clamp(Integer.parseInt(parts[1].trim())),
              clamp(Integer.parseInt(parts[2].trim())));
        } catch (final Exception ignored) {
        }
      }
    }
    try {
      return Color.fromRGB(Integer.decode(raw));
    } catch (final Exception ignored) {
    }
    return null;
  }

  private static List<Color> parseColors(final ConfigurationNode node) {
    final List<Color> out = new ArrayList<>();
    if (node == null || node.virtual() || node.isNull()) {
      return out;
    }
    if (node.isList()) {
      for (final ConfigurationNode child : node.childrenList()) {
        final Color c = parseColor(child);
        if (c != null) {
          out.add(c);
        }
      }
      return out;
    }
    final Color single = parseColor(node);
    if (single != null) {
      out.add(single);
    }
    return out;
  }

  private static DyeColor parseDyeColor(final String raw) {
    if (raw == null) {
      return null;
    }
    try {
      return DyeColor.valueOf(raw.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_'));
    } catch (final Exception e) {
      return null;
    }
  }

  @SuppressWarnings("deprecation")
  private static PatternType parsePatternType(final String raw) {
    if (raw == null) {
      return null;
    }
    final String n = raw.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    try {
      return PatternType.valueOf(n);
    } catch (final Exception ignored) {
    }
    try {
      return Registry.BANNER_PATTERN.get(parseNamespacedKeyString(raw));
    } catch (final Throwable ignored) {
    }
    return null;
  }

  private static TrimMaterial parseTrimMaterial(final String raw) {
    try {
      return Registry.TRIM_MATERIAL.get(parseNamespacedKeyString(raw));
    } catch (final Throwable ignored) {
    }
    return null;
  }

  private static TrimPattern parseTrimPattern(final String raw) {
    try {
      return Registry.TRIM_PATTERN.get(parseNamespacedKeyString(raw));
    } catch (final Throwable ignored) {
    }
    return null;
  }

  /* -------------------- small utilities -------------------- */

  private static boolean looksLikeBukkitSerializedItem(final ConfigurationNode node) {
    final Object typeMarker = node.node("==").raw();
    if (typeMarker instanceof String s && s.toLowerCase(Locale.ROOT).contains("itemstack")) {
      return true;
    }
    // Heuristic: Bukkit meta node often contains its own == marker (ItemMeta implementations).
    final ConfigurationNode meta = node.node("meta");
    if (!meta.virtual() && meta.isMap()) {
      final Object metaMarker = meta.node("==").raw();
      if (metaMarker instanceof String) {
        // Distinguish from our builder format which may use nested "potion"/"book" etc. without == markers.
        return !hasAny(node, "name", "display-name", "lore", "enchantments", "enchants", "item-flags", "flags",
            "custom-model-data", "stored-enchantments", "skull-owner", "item-name");
      }
    }
    return false;
  }

  private static Map<String, Object> toPlainMap(final ConfigurationNode node) {
    final Map<String, Object> out = new LinkedHashMap<>();
    for (final Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
      out.put(String.valueOf(entry.getKey()), toPlainObject(entry.getValue()));
    }
    return out;
  }

  private static Object toPlainObject(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    if (node.isMap()) {
      return toPlainMap(node);
    }
    if (node.isList()) {
      final List<Object> list = new ArrayList<>();
      for (final ConfigurationNode child : node.childrenList()) {
        list.add(toPlainObject(child));
      }
      return list;
    }
    return node.raw();
  }

  private static ConfigurationNode first(final ConfigurationNode root, final String... keys) {
    for (final String key : keys) {
      final ConfigurationNode node = root.node(key);
      if (!node.virtual()) {
        return node;
      }
    }
    // case-insensitive fallback
    if (root.isMap()) {
      for (final String key : keys) {
        for (final Map.Entry<Object, ? extends ConfigurationNode> entry : root.childrenMap().entrySet()) {
          if (String.valueOf(entry.getKey()).equalsIgnoreCase(key)) {
            return entry.getValue();
          }
        }
      }
    }
    return root.node(keys[0]);
  }

  private static ConfigurationNode firstNonVirtual(final ConfigurationNode root, final String... keys) {
    return first(root, keys);
  }

  private static boolean hasAny(final ConfigurationNode root, final String... keys) {
    for (final String key : keys) {
      if (!root.node(key).virtual()) {
        return true;
      }
    }
    if (root.isMap()) {
      for (final String key : keys) {
        for (final Object childKey : root.childrenMap().keySet()) {
          if (String.valueOf(childKey).equalsIgnoreCase(key)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private static String nodeValueToString(final ConfigurationNode node) {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    final Object raw = node.raw();
    return raw == null ? null : String.valueOf(raw);
  }

  private static boolean isHideAll(final String raw) {
    return "ALL".equalsIgnoreCase(raw) || "*".equalsIgnoreCase(raw) || "HIDE_ALL".equalsIgnoreCase(raw)
        || "HIDE-ALL".equalsIgnoreCase(raw) || "TRUE".equalsIgnoreCase(raw);
  }

  private static boolean isHex(final String s) {
    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      final boolean hex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
      if (!hex) {
        return false;
      }
    }
    return true;
  }

  private static int clamp(final int v) {
    return Math.max(0, Math.min(255, v));
  }

  @SuppressWarnings("deprecation")
  private static String namespacedOrName(final Material material) {
    try {
      final NamespacedKey key = material.getKey();
      if (key != null) {
        return key.toString();
      }
    } catch (final Throwable ignored) {
    }
    return material.name();
  }

  @SuppressWarnings("deprecation")
  private static String enchantKey(final Enchantment enchantment) {
    try {
      return enchantment.getKey().toString();
    } catch (final Throwable ignored) {
    }
    try {
      final NamespacedKey key = enchantment.getKeyOrNull();
      if (key != null) {
        return key.toString();
      }
    } catch (final Throwable ignored) {
    }
    return enchantment.getName();
  }

  private static String enchantKeyLike(final String raw) {
    return raw == null ? "unknown" : raw.toLowerCase(Locale.ROOT);
  }

  private static String colorToHex(final Color color) {
    if (color == null) {
      return null;
    }
    return String.format(Locale.ROOT, "#%06X", color.asRGB() & 0xFFFFFF);
  }

  private static UUID tryParseUuid(final String raw) {
    try {
      return UUID.fromString(raw);
    } catch (final Exception e) {
      return null;
    }
  }

  @SuppressWarnings("deprecation")
  private static NamespacedKey attributeKeyToModifierKey(final Attribute attribute) {
    try {
      return new NamespacedKey("zenith", "attr_" + attribute.getKey().getKey());
    } catch (final Throwable ignored) {
      return NamespacedKey.minecraft("zenith_attr");
    }
  }

  private static String sanitizeKey(final String raw) {
    if (raw == null || raw.isBlank()) {
      return "modifier";
    }
    return raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9/._-]", "_");
  }
}
