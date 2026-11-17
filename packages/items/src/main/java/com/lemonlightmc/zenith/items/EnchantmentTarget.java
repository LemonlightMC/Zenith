package com.lemonlightmc.zenith.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Predicate-like target matcher used to determine whether an enchantment can apply to an item.
 */
@FunctionalInterface
public interface EnchantmentTarget {

  /**
   * Checks whether the given item is a valid target.
   *
   * @param item the item to test
   * @return {@code true} if the item is applicable
   */
  public boolean applicable(ItemStack item);

  /**
   * Creates a target that accepts all items.
   *
   * @return an always-applicable enchantment target
   */
  public static EnchantmentTarget empty() {
    return (item) -> true;
  }

  /**
   * Creates a target that matches a specific item stack.
   *
   * @param target the item stack to match
   * @return an enchantment target that matches the given item stack
   */
  public static EnchantmentTarget item(final ItemStack target) {
    if (target == null) {
      return (item) -> true;
    }
    return (item) -> item == null ? false : target.equals(item);
  }

  /**
   * Creates a target that matches a specific material.
   *
   * @param target the material to match
   * @return an enchantment target that matches the given material
   */
  public static EnchantmentTarget material(final Material target) {
    if (target == null) {
      return (item) -> true;
    }
    return (item) -> item == null ? false : item.getType().equals(target);
  }

  /**
   * Creates a target that matches items belonging to a category.
   *
   * @param category the enchantment category to match
   * @return an enchantment target that matches items in the given category
   */
  public static EnchantmentTarget category(final EnchantmentCategory category) {
    if (category == null) {
      return (item) -> true;
    }
    return (item) -> item == null ? false : category.applicable(item.getType());
  }

  /**
   * Creates a target that matches breakable, non-stackable items.
   *
   * @return an enchantment target for breakable items
   */
  public static EnchantmentTarget breakable() {
    return (item) -> item == null ? false : item.getType().getMaxDurability() > 0 && item.getMaxStackSize() == 1;
  }
}
