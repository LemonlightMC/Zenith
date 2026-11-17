package com.lemonlightmc.zenith.items;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.additive.math.Numerals;

/**
 * Custom Bukkit enchantment with display metadata and applicability rules.
 */
public class Enchantment extends org.bukkit.enchantments.Enchantment {

  private final String displayName;
  private final NamespacedKey key;
  private final int maxLevel;
  private final List<org.bukkit.enchantments.Enchantment> conflicts;
  private final boolean treasure;
  private final boolean cursed;
  private final EnchantmentTarget target;
  private boolean isRegistered = false;

  /**
   * Creates a custom enchantment.
   *
   * @param key the namespaced key identifying the enchantment
   * @param displayName the base display name of the enchantment
   * @param maxLevel the maximum enchantment level
   * @param treasure whether the enchantment is treated as treasure
   * @param cursed whether the enchantment is cursed
   * @param target the target matcher that determines which items can receive the enchantment
   * @param conflicts enchantments that conflict with this enchantment
   * @throws IllegalArgumentException if the key is {@code null}, the name is blank, or the max level is invalid
   */
  public Enchantment(final NamespacedKey key, final String displayName, final int maxLevel, final boolean treasure,
      final boolean cursed,
      final EnchantmentTarget target, final List<org.bukkit.enchantments.Enchantment> conflicts) {
    if (key == null) {
      throw new IllegalArgumentException("Enchantment key cannot be null");
    }
    if (displayName == null || displayName.isBlank()) {
      throw new IllegalArgumentException("Enchantment name cannot be empty");
    }
    if (maxLevel <= 0) {
      throw new IllegalArgumentException("Enchantment max level must be greater than 0");
    }
    this.key = key;
    this.displayName = "&r&7" + displayName;
    this.maxLevel = maxLevel;
    this.treasure = treasure;
    this.cursed = cursed;
    this.target = target == null ? EnchantmentTarget.empty() : target;
    this.conflicts = conflicts == null ? List.of() : conflicts;
  }

  /**
   * Creates a custom enchantment using a plugin-scoped key.
   *
   * @param plugin the plugin that owns the enchantment key
   * @param name the enchantment name and key value
   * @param maxLevel the maximum enchantment level
   * @param treasure whether the enchantment is treated as treasure
   * @param cursed whether the enchantment is cursed
   * @param target the target matcher that determines which items can receive the enchantment
   * @param conflicts enchantments that conflict with this enchantment
   */
  public Enchantment(final Plugin plugin, final String name, final int maxLevel,
      final boolean treasure, final boolean cursed,
      final EnchantmentTarget target, final List<org.bukkit.enchantments.Enchantment> conflicts) {
    this(new NamespacedKey(plugin, name), name, maxLevel, treasure, cursed, target, conflicts);
  }

  /**
   * Creates a custom enchantment using the key's string value as its display name.
   *
   * @param key the namespaced key identifying the enchantment
   * @param maxLevel the maximum enchantment level
   * @param treasure whether the enchantment is treated as treasure
   * @param cursed whether the enchantment is cursed
   * @param target the target matcher that determines which items can receive the enchantment
   * @param conflicts enchantments that conflict with this enchantment
   * @return the created custom enchantment
   */
  public static Enchantment createEnchantment(final NamespacedKey key, final int maxLevel, final boolean treasure,
      final boolean cursed, final EnchantmentTarget target, final List<org.bukkit.enchantments.Enchantment> conflicts) {
    return new Enchantment(key, key.getKey(), maxLevel, treasure, cursed, target, conflicts);
  }

  /**
   * Marks this enchantment as registered.
   */
  public void register() {
    isRegistered = true;
  }

  /**
   * Gets the formatted base name of this enchantment.
   *
   * @return the formatted enchantment name
   */
  @Override
  public String getName() {
    return displayName;
  }

  /**
   * Gets the display name for this enchantment at the given level.
   *
   * @param enchLevel the enchantment level to display
   * @return the formatted display name, including a Roman numeral when appropriate
   */
  public String getDisplayName(final int enchLevel) {
    if (enchLevel == 1 && maxLevel == 1) {
      return displayName;
    }
    return displayName + " " + Numerals.toRoman(enchLevel);
  }

  /**
   * Gets the namespaced key for this enchantment.
   *
   * @return the enchantment key
   */
  @Override
  public NamespacedKey getKey() {
    return key;
  }

  /**
   * Gets the namespaced key for this enchantment, failing if it is missing.
   *
   * @return the enchantment key
   * @throws IllegalArgumentException if the key is {@code null}
   */
  @Override
  public NamespacedKey getKeyOrThrow() {
    if (key == null) {
      throw new IllegalArgumentException("Enchantment key is null");
    }
    return key;
  }

  /**
   * Gets the namespaced key for this enchantment, or {@code null} if unavailable.
   *
   * @return the enchantment key, or {@code null}
   */
  @Override
  public NamespacedKey getKeyOrNull() {
    return key;
  }

  /**
   * Gets the maximum supported level for this enchantment.
   *
   * @return the maximum enchantment level
   */
  @Override
  public int getMaxLevel() {
    return maxLevel;
  }

  /**
   * Gets the starting level for this enchantment.
   *
   * @return the starting enchantment level
   */
  @Override
  public int getStartLevel() {
    return 1;
  }

  /**
   * Gets the Bukkit item target for this enchantment.
   *
   * @return {@code null} because applicability is handled by {@link #canEnchantItem(ItemStack)}
   */
  @Override
  public org.bukkit.enchantments.EnchantmentTarget getItemTarget() {
    return null;
  }

  /**
   * Checks whether this enchantment is a treasure enchantment.
   *
   * @return {@code true} if this enchantment is treasure-only
   */
  @Override
  public boolean isTreasure() {
    return treasure;
  }

  /**
   * Checks whether this enchantment is cursed.
   *
   * @return {@code true} if this enchantment is cursed
   */
  @Override
  public boolean isCursed() {
    return cursed;
  }

  /**
   * Checks whether this enchantment conflicts with another custom enchantment.
   *
   * @param other the custom enchantment to compare against
   * @return {@code true} if the enchantments conflict
   */
  public boolean conflictsWith(final Enchantment other) {
    return conflicts.contains(other);
  }

  /**
   * Checks whether this enchantment conflicts with another Bukkit enchantment.
   *
   * @param other the enchantment to compare against
   * @return {@code true} if the enchantments conflict
   */
  @Override
  public boolean conflictsWith(final org.bukkit.enchantments.Enchantment other) {
    return conflicts.contains(other);
  }

  /**
   * Checks whether this enchantment can be applied to the given item.
   *
   * @param item the item to test
   * @return {@code true} if the item is an applicable target
   */
  @Override
  public boolean canEnchantItem(final ItemStack item) {
    return target.applicable(item);
  }

  /**
   * Gets the translation key for this enchantment.
   *
   * @return the translation key derived from the enchantment key
   */
  @Override
  public String getTranslationKey() {
    return key.toString();
  }

  /**
   * Checks whether this enchantment has been marked as registered.
   *
   * @return {@code true} if this enchantment has been registered
   */
  @Override
  public boolean isRegistered() {
    return isRegistered;
  }
}