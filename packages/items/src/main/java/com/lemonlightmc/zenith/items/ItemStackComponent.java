package com.lemonlightmc.zenith.items;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.BlocksAttacksComponent;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.inventory.meta.components.WeaponComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.persistence.PersistentDataType;

public interface ItemStackComponent {
  void apply(ItemStackBuilder builder);

  public static ItemStackComponent enchant(final Enchantment enchantment, final int level) {
    return builder -> builder.item.addEnchantment(enchantment, level);
  }

  public static ItemStackComponent enchantUnsafe(final Enchantment enchantment, final int level) {
    return builder -> builder.item.addUnsafeEnchantment(enchantment, level);
  }

  public static ItemStackComponent enchants(final Map<Enchantment, Integer> enchantments) {
    return builder -> builder.item.addEnchantments(enchantments);
  }

  public static ItemStackComponent enchantsUnsafe(final Map<Enchantment, Integer> enchantments) {
    return builder -> builder.item.addUnsafeEnchantments(enchantments);
  }

  public static ItemStackComponent clearEnchants() {
    return builder -> builder.item.removeEnchantments();
  }

  public static ItemStackComponent removeEnchant(final Enchantment enchantment) {
    return builder -> builder.item.removeEnchantment(enchantment);
  }

  public static ItemStackComponent customModelData(final CustomModelDataComponent customModelData) {
    return builder -> builder.meta.setCustomModelDataComponent(customModelData);
  }

  public static ItemStackComponent unbreakable(final boolean unbreakable) {
    return builder -> builder.meta.setUnbreakable(unbreakable);
  }

  public static ItemStackComponent flags(final ItemFlag... flags) {
    return builder -> builder.meta.addItemFlags(flags);
  }

  public static ItemStackComponent hideTooltip(final boolean hide) {
    return builder -> builder.meta.setHideTooltip(hide);
  }

  public static ItemStackComponent tooltipStyle(final NamespacedKey tooltipStyle) {
    return builder -> builder.meta.setTooltipStyle(tooltipStyle);
  }

  public static <T, Z> ItemStackComponent persistentTag(final NamespacedKey key,
      final PersistentDataType<T, Z> type, final Z value) {
    return builder -> builder.meta.getPersistentDataContainer().set(key, type, value);
  }

  public static ItemStackComponent tag(final NamespacedKey key, final String value) {
    return persistentTag(key, PersistentDataType.STRING, value);
  }

  public static ItemStackComponent tag(final NamespacedKey key, final Integer value) {
    return persistentTag(key, PersistentDataType.INTEGER, value);
  }

  public static ItemStackComponent tag(final NamespacedKey key, final Long value) {
    return persistentTag(key, PersistentDataType.LONG, value);
  }

  public static ItemStackComponent tag(final NamespacedKey key, final Double value) {
    return persistentTag(key, PersistentDataType.DOUBLE, value);
  }

  public static ItemStackComponent tag(final NamespacedKey key, final Boolean value) {
    return persistentTag(key, PersistentDataType.BOOLEAN, value);
  }

  public static ItemStackComponent tag(final NamespacedKey key, final List<String> value) {
    return builder -> builder.meta.getPersistentDataContainer().set(key,
        PersistentDataType.LIST.strings(), value);
  }

  public static ItemStackComponent attribute(final Attribute attribute,
      final AttributeModifier modifier) {
    return builder -> {
      final ItemMeta meta = builder.meta;
      meta.removeAttributeModifier(attribute);
      meta.addAttributeModifier(attribute, modifier);
    };
  }

  public static ItemStackComponent addAttribute(final Attribute attribute,
      final AttributeModifier modifier) {
    return builder -> builder.meta.addAttributeModifier(attribute, modifier);
  }

  public static ItemStackComponent breakSound(final Sound sound) {
    return builder -> builder.meta.setBreakSound(sound);
  }

  public static ItemStackComponent consumable(final ConsumableComponent component) {
    return builder -> builder.meta.setConsumable(component);
  }

  public static ItemStackComponent food(final FoodComponent component) {
    return builder -> builder.meta.setFood(component);
  }

  public static ItemStackComponent equippable(final EquippableComponent component) {
    return builder -> builder.meta.setEquippable(component);
  }

  public static ItemStackComponent damageResistant(final Tag<DamageType> tag) {
    return builder -> builder.meta.setDamageResistant(tag);
  }

  public static ItemStackComponent glider(final boolean isGlider) {
    return builder -> builder.meta.setGlider(isGlider);
  }

  public static ItemStackComponent jukeboxPlayable(final JukeboxPlayableComponent component) {
    return builder -> builder.meta.setJukeboxPlayable(component);
  }

  public static ItemStackComponent maxStackSize(final int size) {
    return builder -> builder.meta.setMaxStackSize(size);
  }

  public static ItemStackComponent rarity(final org.bukkit.inventory.ItemRarity rarity) {
    return builder -> builder.meta.setRarity(rarity);
  }

  public static ItemStackComponent tool(final ToolComponent component) {
    return builder -> builder.meta.setTool(component);
  }

  public static ItemStackComponent weapon(final WeaponComponent component) {
    return builder -> builder.meta.setWeapon(component);
  }

  public static ItemStackComponent useCooldown(final UseCooldownComponent component) {
    return builder -> builder.meta.setUseCooldown(component);
  }

  public static ItemStackComponent blocksAttacks(final BlocksAttacksComponent component) {
    return builder -> builder.meta.setBlocksAttacks(component);
  }

  public static ItemStackComponent editMeta(final Consumer<ItemMeta> metaEditor) {
    return builder -> {
      if (metaEditor != null) {
        metaEditor.accept(builder.meta);
      }
    };
  }
}
