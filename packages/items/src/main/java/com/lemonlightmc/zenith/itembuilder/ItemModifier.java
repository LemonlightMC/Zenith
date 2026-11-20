package com.lemonlightmc.zenith.itembuilder;

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
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.ShieldMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.inventory.meta.WritableBookMeta;
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

import net.kyori.adventure.text.Component;

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
  void apply(com.lemonlightmc.zenith.itembuilder.ItemStackBuilder<?> builder);

  default ItemModifier andThen(final ItemModifier after) {
    if (after == null) {
      return this;
    }
    return builder -> {
      this.apply(builder);
      after.apply(builder);
    };
  }

  static ItemModifier noop() {
    return builder -> {
    };
  }

  static ItemModifier of(final ItemModifier modifier) {
    return modifier == null ? noop() : modifier;
  }

  static ItemModifier when(final boolean condition, final ItemModifier modifier) {
    return condition ? of(modifier) : noop();
  }

  /* -------------------- typed meta / discoverability -------------------- */

  static <M extends ItemMeta> ItemModifier meta(final Class<M> type, final Consumer<? super M> consumer) {
    return builder -> builder.meta(type, consumer);
  }

  static ItemModifier editMeta(final Consumer<? super ItemMeta> metaEditor) {
    return builder -> builder.meta(metaEditor);
  }

  static ItemModifier skull(final Consumer<? super SkullMeta> consumer) {
    return meta(SkullMeta.class, consumer);
  }

  static ItemModifier potionMeta(final Consumer<? super PotionMeta> consumer) {
    return meta(PotionMeta.class, consumer);
  }

  static ItemModifier potion(final Consumer<? super PotionMeta> consumer) {
    return meta(PotionMeta.class, consumer);
  }

  static ItemModifier book(final Consumer<? super BookMeta> consumer) {
    return meta(BookMeta.class, consumer);
  }

  static ItemModifier writableBook(final Consumer<? super WritableBookMeta> consumer) {
    return meta(WritableBookMeta.class, consumer);
  }

  static ItemModifier banner(final Consumer<? super BannerMeta> consumer) {
    return meta(BannerMeta.class, consumer);
  }

  static ItemModifier leatherArmor(final Consumer<? super LeatherArmorMeta> consumer) {
    return meta(LeatherArmorMeta.class, consumer);
  }

  static ItemModifier firework(final Consumer<? super FireworkMeta> consumer) {
    return meta(FireworkMeta.class, consumer);
  }

  static ItemModifier fireworkEffect(final Consumer<? super FireworkEffectMeta> consumer) {
    return meta(FireworkEffectMeta.class, consumer);
  }

  static ItemModifier map(final Consumer<? super MapMeta> consumer) {
    return meta(MapMeta.class, consumer);
  }

  static ItemModifier compass(final Consumer<? super CompassMeta> consumer) {
    return meta(CompassMeta.class, consumer);
  }

  static ItemModifier crossbow(final Consumer<? super CrossbowMeta> consumer) {
    return meta(CrossbowMeta.class, consumer);
  }

  static ItemModifier bundle(final Consumer<? super BundleMeta> consumer) {
    return meta(BundleMeta.class, consumer);
  }

  static ItemModifier armor(final Consumer<? super ArmorMeta> consumer) {
    return meta(ArmorMeta.class, consumer);
  }

  static ItemModifier axolotlBucket(final Consumer<? super AxolotlBucketMeta> consumer) {
    return meta(AxolotlBucketMeta.class, consumer);
  }

  static ItemModifier tropicalFishBucket(final Consumer<? super TropicalFishBucketMeta> consumer) {
    return meta(TropicalFishBucketMeta.class, consumer);
  }

  static ItemModifier blockState(final Consumer<? super BlockStateMeta> consumer) {
    return meta(BlockStateMeta.class, consumer);
  }

  static ItemModifier knowledgeBook(final Consumer<? super KnowledgeBookMeta> consumer) {
    return meta(KnowledgeBookMeta.class, consumer);
  }

  static ItemModifier suspiciousStew(final Consumer<? super SuspiciousStewMeta> consumer) {
    return meta(SuspiciousStewMeta.class, consumer);
  }

  static ItemModifier musicInstrument(final Consumer<? super MusicInstrumentMeta> consumer) {
    return meta(MusicInstrumentMeta.class, consumer);
  }

  static ItemModifier ominousBottle(final Consumer<? super OminousBottleMeta> consumer) {
    return meta(OminousBottleMeta.class, consumer);
  }

  static ItemModifier shield(final Consumer<? super ShieldMeta> consumer) {
    return meta(ShieldMeta.class, consumer);
  }

  static ItemModifier damageable(final Consumer<? super Damageable> consumer) {
    return meta(Damageable.class, consumer);
  }

  static ItemModifier repairable(final Consumer<? super Repairable> consumer) {
    return meta(Repairable.class, consumer);
  }

  static ItemModifier enchantmentStorage(final Consumer<? super EnchantmentStorageMeta> consumer) {
    return meta(EnchantmentStorageMeta.class, consumer);
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

  static ItemModifier storedEnchant(final Enchantment enchantment, final int level) {
    return builder -> builder.storedEnchant(enchantment, level);
  }

  static ItemModifier storedEnchant(final Enchantment enchantment, final int level,
      final boolean ignoreLevelRestriction) {
    return builder -> builder.storedEnchant(enchantment, level, ignoreLevelRestriction);
  }

  static ItemModifier clearStoredEnchants() {
    return ItemStackBuilder::clearStoredEnchants;
  }

  /* -------------------- display / flags -------------------- */

  static ItemModifier name(final String name) {
    return builder -> builder.name(name);
  }

  static ItemModifier name(final Component name) {
    return builder -> builder.name(name);
  }

  static ItemModifier itemName(final String name) {
    return builder -> builder.itemName(name);
  }

  static ItemModifier itemName(final Component name) {
    return builder -> builder.itemName(name);
  }

  static ItemModifier lore(final String... lines) {
    return builder -> builder.lore(lines);
  }

  static ItemModifier lore(final List<String> lines) {
    return builder -> builder.lore(lines);
  }

  static ItemModifier lore(final Component... lines) {
    return builder -> builder.lore(lines);
  }

  static ItemModifier loreComponents(final List<? extends Component> lines) {
    return builder -> builder.loreComponents(lines);
  }

  static ItemModifier appendLore(final String... lines) {
    return builder -> builder.appendLore(lines);
  }

  static ItemModifier appendLore(final Component... lines) {
    return builder -> builder.appendLore(lines);
  }

  static ItemModifier unbreakable(final boolean unbreakable) {
    return builder -> builder.unbreakable(unbreakable);
  }

  static ItemModifier flags(final ItemFlag... flags) {
    return builder -> builder.flags(flags);
  }

  static ItemModifier hideAllFlags() {
    return ItemStackBuilder::hideAllFlags;
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

  static ItemModifier glint() {
    return ItemStackBuilder::glint;
  }

  static ItemModifier glint(final boolean glint) {
    return builder -> builder.glint(glint);
  }

  static ItemModifier resetGlint() {
    return ItemStackBuilder::resetGlint;
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

  static ItemModifier clearAttributes() {
    return ItemStackBuilder::clearAttributes;
  }

  static <T, Z> ItemModifier persistentTag(final NamespacedKey key, final PersistentDataType<T, Z> type,
      final Z value) {
    return builder -> builder.pdc(key, type, value);
  }

  static ItemModifier removePdc(final NamespacedKey key) {
    return builder -> builder.removePdc(key);
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
