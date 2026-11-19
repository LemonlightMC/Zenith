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
import org.bukkit.inventory.ItemRarity;
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

/**
 * Reusable modification that can be applied to an {@link ItemStackBuilder}.
 */
@FunctionalInterface
public interface ItemModifier {

  /**
   * Applies this modifier to the given builder.
   *
   * @param builder the builder to modify
   */
  void apply(ItemStackBuilder<?> builder);

  /**
   * Returns a composed modifier that applies this modifier and then the given
   * one.
   */
  default ItemModifier andThen(final ItemModifier after) {
    if (after == null) {
      return this;
    }
    return builder -> {
      this.apply(builder);
      after.apply(builder);
    };
  }

  static ItemModifier empty() {
    return builder -> {
    };
  }

  static ItemModifier of(final ItemModifier modifier) {
    return modifier == null ? empty() : modifier;
  }

  /* -------------------- typed meta -------------------- */

  static <M extends ItemMeta> ItemModifier meta(final Class<M> type, final Consumer<? super M> consumer) {
    return builder -> builder.meta(type, consumer);
  }

  static ItemModifier editMeta(final Consumer<? super ItemMeta> metaEditor) {
    return builder -> builder.meta(metaEditor);
  }

  /* -------------------- enchantments -------------------- */

  static ItemModifier enchant(final Enchantment enchantment, final int level) {
    return builder -> builder.enchant(enchantment, level);
  }

  static ItemModifier enchantUnsafe(final Enchantment enchantment, final int level) {
    return builder -> builder.enchantUnsafe(enchantment, level);
  }

  static ItemModifier enchants(final Map<Enchantment, Integer> enchantments) {
    return builder -> builder.enchants(enchantments);
  }

  static ItemModifier enchantsUnsafe(final Map<Enchantment, Integer> enchantments) {
    return builder -> builder.enchantsUnsafe(enchantments);
  }

  static ItemModifier clearEnchants() {
    return ItemStackBuilder::clearEnchants;
  }

  static ItemModifier removeEnchant(final Enchantment enchantment) {
    return builder -> builder.removeEnchant(enchantment);
  }

  /* -------------------- display / flags -------------------- */

  static ItemModifier name(final String name) {
    return builder -> builder.name(name);
  }

  static ItemModifier itemName(final String name) {
    return builder -> builder.itemName(name);
  }

  static ItemModifier lore(final String... lines) {
    return builder -> builder.lore(lines);
  }

  static ItemModifier lore(final List<String> lines) {
    return builder -> builder.lore(lines);
  }

  static ItemModifier appendLore(final String... lines) {
    return builder -> builder.appendLore(lines);
  }

  static ItemModifier unbreakable(final boolean unbreakable) {
    return builder -> builder.unbreakable(unbreakable);
  }

  static ItemModifier flags(final ItemFlag... flags) {
    return builder -> builder.flags(flags);
  }

  static ItemModifier hideTooltip(final boolean hide) {
    return builder -> builder.hideTooltip(hide);
  }

  static ItemModifier tooltipStyle(final NamespacedKey tooltipStyle) {
    return builder -> builder.tooltipStyle(tooltipStyle);
  }

  static ItemModifier customModelData(final CustomModelDataComponent customModelData) {
    return builder -> builder.customModelData(customModelData);
  }

  static ItemModifier customModelData(final Integer data) {
    return builder -> builder.customModelData(data);
  }

  static ItemModifier maxStackSize(final int size) {
    return builder -> builder.maxStackSize(size);
  }

  static ItemModifier rarity(final ItemRarity rarity) {
    return builder -> builder.rarity(rarity);
  }

  static ItemModifier glider(final boolean isGlider) {
    return builder -> builder.glider(isGlider);
  }

  static ItemModifier breakSound(final Sound sound) {
    return builder -> builder.breakSound(sound);
  }

  /* -------------------- meta components -------------------- */

  static ItemModifier food(final FoodComponent component) {
    return builder -> builder.food(component);
  }

  static ItemModifier tool(final ToolComponent component) {
    return builder -> builder.tool(component);
  }

  static ItemModifier consumable(final ConsumableComponent component) {
    return builder -> builder.consumable(component);
  }

  static ItemModifier equippable(final EquippableComponent component) {
    return builder -> builder.equippable(component);
  }

  static ItemModifier weapon(final WeaponComponent component) {
    return builder -> builder.weapon(component);
  }

  static ItemModifier blocksAttacks(final BlocksAttacksComponent component) {
    return builder -> builder.blocksAttacks(component);
  }

  static ItemModifier useCooldown(final UseCooldownComponent component) {
    return builder -> builder.useCooldown(component);
  }

  static ItemModifier jukeboxPlayable(final JukeboxPlayableComponent component) {
    return builder -> builder.jukeboxPlayable(component);
  }

  static ItemModifier damageResistant(final Tag<DamageType> tag) {
    return builder -> builder.damageResistant(tag);
  }

  /* -------------------- attributes / pdc -------------------- */

  static ItemModifier attribute(final Attribute attribute, final AttributeModifier modifier) {
    return builder -> builder.attribute(attribute, modifier);
  }

  static ItemModifier addAttribute(final Attribute attribute, final AttributeModifier modifier) {
    return builder -> builder.addAttribute(attribute, modifier);
  }

  static <T, Z> ItemModifier persistentTag(final NamespacedKey key, final PersistentDataType<T, Z> type,
      final Z value) {
    return builder -> builder.pdc(key, type, value);
  }

  static ItemModifier tag(final NamespacedKey key, final String value) {
    return persistentTag(key, PersistentDataType.STRING, value);
  }

  static ItemModifier tag(final NamespacedKey key, final Integer value) {
    return persistentTag(key, PersistentDataType.INTEGER, value);
  }

  static ItemModifier tag(final NamespacedKey key, final Long value) {
    return persistentTag(key, PersistentDataType.LONG, value);
  }

  static ItemModifier tag(final NamespacedKey key, final Double value) {
    return persistentTag(key, PersistentDataType.DOUBLE, value);
  }

  static ItemModifier tag(final NamespacedKey key, final Boolean value) {
    return persistentTag(key, PersistentDataType.BOOLEAN, value);
  }

  static ItemModifier tag(final NamespacedKey key, final List<String> value) {
    return builder -> builder.pdc(key, PersistentDataType.LIST.strings(), value);
  }
}
