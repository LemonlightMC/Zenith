package com.lemonlightmc.zenith.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface EnchantmentTarget {

  public boolean applicable(ItemStack item);

  public static EnchantmentTarget empty() {
    return (item) -> true;
  }

  public static EnchantmentTarget item(final ItemStack target) {
    if (target == null) {
      return (item) -> true;
    }
    return (item) -> item == null ? false : target.equals(item);
  }

  public static EnchantmentTarget material(final Material target) {
    if (target == null) {
      return (item) -> true;
    }
    return (item) -> item == null ? false : item.getType().equals(target);
  }

  public static EnchantmentTarget category(final EnchantmentCategory category) {
    if (category == null) {
      return (item) -> true;
    }
    return (item) -> item == null ? false : category.applicable(item.getType());
  }

  public static EnchantmentTarget breakable() {
    return (item) -> item == null ? false : item.getType().getMaxDurability() > 0 && item.getMaxStackSize() == 1;
  }
}
