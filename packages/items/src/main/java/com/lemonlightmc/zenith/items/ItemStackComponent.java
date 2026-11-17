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

/**
 * Represents a reusable modification that can be applied to an {@link ItemStackBuilder}.
 */
public interface ItemStackComponent {
  /**
   * Applies this component to the given builder.
   *
   * @param builder the builder to modify
   */
  void apply(ItemStackBuilder builder);

  /**
   * Creates a component that adds a safe enchantment to the item.
   *
   * @param enchantment the enchantment to add
   * @param level the enchantment level
   * @return a component that applies the enchantment
   */
  public static ItemStackComponent enchant(final Enchantment enchantment, final int level) {
    return builder -> builder.item.addEnchantment(enchantment, level);
  }

  /**
   * Creates a component that adds an unsafe enchantment to the item.
   *
   * @param enchantment the enchantment to add
   * @param level the enchantment level
   * @return a component that applies the enchantment
   */
  public static ItemStackComponent enchantUnsafe(final Enchantment enchantment, final int level) {
    return builder -> builder.item.addUnsafeEnchantment(enchantment, level);
  }

  /**
   * Creates a component that adds multiple safe enchantments to the item.
   *
   * @param enchantments the enchantments and levels to add
   * @return a component that applies the enchantments
   */
  public static ItemStackComponent enchants(final Map<Enchantment, Integer> enchantments) {
    return builder -> builder.item.addEnchantments(enchantments);
  }

  /**
   * Creates a component that adds multiple unsafe enchantments to the item.
   *
   * @param enchantments the enchantments and levels to add
   * @return a component that applies the enchantments
   */
  public static ItemStackComponent enchantsUnsafe(final Map<Enchantment, Integer> enchantments) {
    return builder -> builder.item.addUnsafeEnchantments(enchantments);
  }

  /**
   * Creates a component that removes all enchantments from the item.
   *
   * @return a component that clears enchantments
   */
  public static ItemStackComponent clearEnchants() {
    return builder -> builder.item.removeEnchantments();
  }

  /**
   * Creates a component that removes a specific enchantment from the item.
   *
   * @param enchantment the enchantment to remove
   * @return a component that removes the enchantment
   */
  public static ItemStackComponent removeEnchant(final Enchantment enchantment) {
    return builder -> builder.item.removeEnchantment(enchantment);
  }

  /**
   * Creates a component that sets the item's custom model data component.
   *
   * @param customModelData the custom model data component to apply
   * @return a component that updates custom model data
   */
  public static ItemStackComponent customModelData(final CustomModelDataComponent customModelData) {
    return builder -> builder.meta.setCustomModelDataComponent(customModelData);
  }

  /**
   * Creates a component that sets whether the item is unbreakable.
   *
   * @param unbreakable {@code true} to mark the item as unbreakable
   * @return a component that updates the unbreakable state
   */
  public static ItemStackComponent unbreakable(final boolean unbreakable) {
    return builder -> builder.meta.setUnbreakable(unbreakable);
  }

  /**
   * Creates a component that adds item flags to the item.
   *
   * @param flags the item flags to add
   * @return a component that applies the flags
   */
  public static ItemStackComponent flags(final ItemFlag... flags) {
    return builder -> builder.meta.addItemFlags(flags);
  }

  /**
   * Creates a component that shows or hides the item tooltip.
   *
   * @param hide {@code true} to hide the tooltip
   * @return a component that updates tooltip visibility
   */
  public static ItemStackComponent hideTooltip(final boolean hide) {
    return builder -> builder.meta.setHideTooltip(hide);
  }

  /**
   * Creates a component that sets the item's tooltip style.
   *
   * @param tooltipStyle the tooltip style key
   * @return a component that updates the tooltip style
   */
  public static ItemStackComponent tooltipStyle(final NamespacedKey tooltipStyle) {
    return builder -> builder.meta.setTooltipStyle(tooltipStyle);
  }

  /**
   * Creates a component that stores a value in the item's persistent data container.
   *
   * @param <T> the primitive data type
   * @param <Z> the complex data type
   * @param key the namespaced key to store the value under
   * @param type the persistent data type
   * @param value the value to store
   * @return a component that stores the persistent data value
   */
  public static <T, Z> ItemStackComponent persistentTag(final NamespacedKey key,
      final PersistentDataType<T, Z> type, final Z value) {
    return builder -> builder.meta.getPersistentDataContainer().set(key, type, value);
  }

  /**
   * Creates a component that stores a string tag in the item's persistent data container.
   *
   * @param key the namespaced key to store the value under
   * @param value the string value to store
   * @return a component that stores the tag
   */
  public static ItemStackComponent tag(final NamespacedKey key, final String value) {
    return persistentTag(key, PersistentDataType.STRING, value);
  }

  /**
   * Creates a component that stores an integer tag in the item's persistent data container.
   *
   * @param key the namespaced key to store the value under
   * @param value the integer value to store
   * @return a component that stores the tag
   */
  public static ItemStackComponent tag(final NamespacedKey key, final Integer value) {
    return persistentTag(key, PersistentDataType.INTEGER, value);
  }

  /**
   * Creates a component that stores a long tag in the item's persistent data container.
   *
   * @param key the namespaced key to store the value under
   * @param value the long value to store
   * @return a component that stores the tag
   */
  public static ItemStackComponent tag(final NamespacedKey key, final Long value) {
    return persistentTag(key, PersistentDataType.LONG, value);
  }

  /**
   * Creates a component that stores a double tag in the item's persistent data container.
   *
   * @param key the namespaced key to store the value under
   * @param value the double value to store
   * @return a component that stores the tag
   */
  public static ItemStackComponent tag(final NamespacedKey key, final Double value) {
    return persistentTag(key, PersistentDataType.DOUBLE, value);
  }

  /**
   * Creates a component that stores a boolean tag in the item's persistent data container.
   *
   * @param key the namespaced key to store the value under
   * @param value the boolean value to store
   * @return a component that stores the tag
   */
  public static ItemStackComponent tag(final NamespacedKey key, final Boolean value) {
    return persistentTag(key, PersistentDataType.BOOLEAN, value);
  }

  /**
   * Creates a component that stores a list of string tags in the item's persistent data container.
   *
   * @param key the namespaced key to store the value under
   * @param value the string values to store
   * @return a component that stores the tag list
   */
  public static ItemStackComponent tag(final NamespacedKey key, final List<String> value) {
    return builder -> builder.meta.getPersistentDataContainer().set(key,
        PersistentDataType.LIST.strings(), value);
  }

  /**
   * Creates a component that replaces modifiers for the given attribute.
   *
   * @param attribute the attribute to modify
   * @param modifier the modifier to apply
   * @return a component that updates the attribute modifier
   */
  public static ItemStackComponent attribute(final Attribute attribute,
      final AttributeModifier modifier) {
    return builder -> {
      final ItemMeta meta = builder.meta;
      meta.removeAttributeModifier(attribute);
      meta.addAttributeModifier(attribute, modifier);
    };
  }

  /**
   * Creates a component that adds an attribute modifier.
   *
   * @param attribute the attribute to modify
   * @param modifier the modifier to add
   * @return a component that adds the attribute modifier
   */
  public static ItemStackComponent addAttribute(final Attribute attribute,
      final AttributeModifier modifier) {
    return builder -> builder.meta.addAttributeModifier(attribute, modifier);
  }

  /**
   * Creates a component that sets the item's break sound.
   *
   * @param sound the break sound to apply
   * @return a component that updates the break sound
   */
  public static ItemStackComponent breakSound(final Sound sound) {
    return builder -> builder.meta.setBreakSound(sound);
  }

  /**
   * Creates a component that sets the item's consumable component.
   *
   * @param component the consumable component to apply
   * @return a component that updates the consumable data
   */
  public static ItemStackComponent consumable(final ConsumableComponent component) {
    return builder -> builder.meta.setConsumable(component);
  }

  /**
   * Creates a component that sets the item's food component.
   *
   * @param component the food component to apply
   * @return a component that updates the food data
   */
  public static ItemStackComponent food(final FoodComponent component) {
    return builder -> builder.meta.setFood(component);
  }

  /**
   * Creates a component that sets the item's equippable component.
   *
   * @param component the equippable component to apply
   * @return a component that updates the equippable data
   */
  public static ItemStackComponent equippable(final EquippableComponent component) {
    return builder -> builder.meta.setEquippable(component);
  }

  /**
   * Creates a component that sets the item's damage resistance tag.
   *
   * @param tag the damage resistance tag to apply
   * @return a component that updates damage resistance
   */
  public static ItemStackComponent damageResistant(final Tag<DamageType> tag) {
    return builder -> builder.meta.setDamageResistant(tag);
  }

  /**
   * Creates a component that marks the item as a glider.
   *
   * @param isGlider {@code true} if the item should act as a glider
   * @return a component that updates the glider state
   */
  public static ItemStackComponent glider(final boolean isGlider) {
    return builder -> builder.meta.setGlider(isGlider);
  }

  /**
   * Creates a component that sets the item's jukebox playable component.
   *
   * @param component the jukebox playable component to apply
   * @return a component that updates jukebox playback data
   */
  public static ItemStackComponent jukeboxPlayable(final JukeboxPlayableComponent component) {
    return builder -> builder.meta.setJukeboxPlayable(component);
  }

  /**
   * Creates a component that sets the item's maximum stack size.
   *
   * @param size the maximum stack size
   * @return a component that updates the maximum stack size
   */
  public static ItemStackComponent maxStackSize(final int size) {
    return builder -> builder.meta.setMaxStackSize(size);
  }

  /**
   * Creates a component that sets the item's rarity.
   *
   * @param rarity the item rarity to apply
   * @return a component that updates the rarity
   */
  public static ItemStackComponent rarity(final org.bukkit.inventory.ItemRarity rarity) {
    return builder -> builder.meta.setRarity(rarity);
  }

  /**
   * Creates a component that sets the item's tool component.
   *
   * @param component the tool component to apply
   * @return a component that updates the tool data
   */
  public static ItemStackComponent tool(final ToolComponent component) {
    return builder -> builder.meta.setTool(component);
  }

  /**
   * Creates a component that sets the item's weapon component.
   *
   * @param component the weapon component to apply
   * @return a component that updates the weapon data
   */
  public static ItemStackComponent weapon(final WeaponComponent component) {
    return builder -> builder.meta.setWeapon(component);
  }

  /**
   * Creates a component that sets the item's use cooldown component.
   *
   * @param component the use cooldown component to apply
   * @return a component that updates the cooldown data
   */
  public static ItemStackComponent useCooldown(final UseCooldownComponent component) {
    return builder -> builder.meta.setUseCooldown(component);
  }

  /**
   * Creates a component that sets the item's block-attack component.
   *
   * @param component the block-attack component to apply
   * @return a component that updates block-attack behavior
   */
  public static ItemStackComponent blocksAttacks(final BlocksAttacksComponent component) {
    return builder -> builder.meta.setBlocksAttacks(component);
  }

  /**
   * Creates a component that applies a custom edit to the item metadata.
   *
   * @param metaEditor the metadata editor to apply
   * @return a component that edits the item metadata
   */
  public static ItemStackComponent editMeta(final Consumer<ItemMeta> metaEditor) {
    return builder -> {
      if (metaEditor != null) {
        metaEditor.accept(builder.meta);
      }
    };
  }
}
