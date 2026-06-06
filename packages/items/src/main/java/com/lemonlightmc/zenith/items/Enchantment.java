package com.lemonlightmc.zenith.items;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.math.Numerals;

public class Enchantment extends org.bukkit.enchantments.Enchantment {

  private final String displayName;
  private final NamespacedKey key;
  private final int maxLevel;
  private final List<org.bukkit.enchantments.Enchantment> conflicts;
  private final boolean treasure;
  private final boolean cursed;
  private final EnchantmentTarget target;
  private boolean isRegistered = false;

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

  public Enchantment(final Plugin plugin, final String name, final int maxLevel,
      final boolean treasure, final boolean cursed,
      final EnchantmentTarget target, final List<org.bukkit.enchantments.Enchantment> conflicts) {
    this(new NamespacedKey(plugin, name), name, maxLevel, treasure, cursed, target, conflicts);
  }

  public static Enchantment createEnchantment(final NamespacedKey key, final int maxLevel, final boolean treasure,
      final boolean cursed, final EnchantmentTarget target, final List<org.bukkit.enchantments.Enchantment> conflicts) {
    return new Enchantment(key, key.getKey(), maxLevel, treasure, cursed, target, conflicts);
  }

  public void register() {
    isRegistered = true;
  }

  @Override
  public String getName() {
    return displayName;
  }

  public String getDisplayName(final int enchLevel) {
    if (enchLevel == 1 && maxLevel == 1) {
      return displayName;
    }
    return displayName + " " + Numerals.toRoman(enchLevel);
  }

  @Override
  public NamespacedKey getKey() {
    return key;
  }

  @Override
  public NamespacedKey getKeyOrThrow() {
    if (key == null) {
      throw new IllegalArgumentException("Enchantment key is null");
    }
    return key;
  }

  @Override
  public NamespacedKey getKeyOrNull() {
    return key;
  }

  @Override
  public int getMaxLevel() {
    return maxLevel;
  }

  @Override
  public int getStartLevel() {
    return 1;
  }

  @Override
  public org.bukkit.enchantments.EnchantmentTarget getItemTarget() {
    return null;
  }

  @Override
  public boolean isTreasure() {
    return treasure;
  }

  @Override
  public boolean isCursed() {
    return cursed;
  }

  public boolean conflictsWith(final Enchantment other) {
    return conflicts.contains(other);
  }

  @Override
  public boolean conflictsWith(final org.bukkit.enchantments.Enchantment other) {
    return conflicts.contains(other);
  }

  @Override
  public boolean canEnchantItem(final ItemStack item) {
    return target.applicable(item);
  }

  @Override
  public String getTranslationKey() {
    return key.toString();
  }

  @Override
  public boolean isRegistered() {
    return isRegistered;
  }
}