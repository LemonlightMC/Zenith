package com.lemonlightmc.zenith.apis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.lemonlightmc.zenith.items.Enchantment;
import com.lemonlightmc.zenith.messages.Logger;

public class EnchantmentAPI {
  private static final org.bukkit.enchantments.Enchantment[] values = values();
  private static final NamespacedKey[] keys = keys();

  public static org.bukkit.enchantments.Enchantment getByKey(final NamespacedKey key) {
    if (key == null) {
      return null;
    }
    return Registry.ENCHANTMENT.get(key);
  }

  public static org.bukkit.enchantments.Enchantment getByName(final String name) {
    if (name == null) {
      return null;
    }
    return getByKey(NamespacedKey.fromString(name.toLowerCase(Locale.ROOT)));
  }

  public static NamespacedKey[] keys() {
    if (keys != null) {
      return keys;
    }
    final List<NamespacedKey> arr = new ArrayList<>();
    for (final org.bukkit.enchantments.Enchantment enchantment : Registry.ENCHANTMENT) {
      arr.add(enchantment.getKeyOrNull());
    }
    return arr.toArray(new NamespacedKey[0]);
  }

  public static org.bukkit.enchantments.Enchantment[] values() {
    if (values != null) {
      return values;
    }
    final List<org.bukkit.enchantments.Enchantment> arr = new ArrayList<>();
    for (final org.bukkit.enchantments.Enchantment enchantment : Registry.ENCHANTMENT) {
      arr.add(enchantment);
    }
    return arr.toArray(new org.bukkit.enchantments.Enchantment[0]);
  }

  public static boolean isRegistered(final NamespacedKey key) {
    return key == null ? false : Registry.ENCHANTMENT.get(key) != null;
  }

  public static void register(final Enchantment enchantment) {
    if (enchantment == null) {
      return;
    }
    if (isRegistered(enchantment.getKey())) {
      Logger.warn("Enchantment " + enchantment.getName() + " has already been registered");
      return;
    }
    try {
      final Field f = org.bukkit.enchantments.Enchantment.class.getDeclaredField("acceptingNew");
      f.setAccessible(true);
      f.set(null, true);
      final Method method = org.bukkit.enchantments.Enchantment.class.getDeclaredMethod("registerEnchantment");
      f.setAccessible(true);
      enchantment.register();
      method.invoke(enchantment);
    } catch (final Exception e) {
      Logger.warn("Failed to register the Enchantment: " + enchantment.getName());
      e.printStackTrace();
    }
  }

  public static boolean hasEnchantment(final ItemStack item, final org.bukkit.enchantments.Enchantment enchantment) {
    return enchantment == null ? false : hasEnchantment(item, enchantment.getKeyOrNull());
  }

  public static boolean hasEnchantment(final ItemStack item, final Enchantment enchantment) {
    return enchantment == null ? false : hasEnchantment(item, enchantment.getKey());
  }

  public static boolean hasEnchantment(final ItemStack item, final NamespacedKey key) {
    if (key == null || item == null) {
      return false;
    }
    final ItemMeta meta = item.getItemMeta();
    if (meta == null || meta.getEnchants() == null || meta.getEnchants().isEmpty()) {
      return false;
    }
    for (final Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
      if (key.equals(entry.getKey() == null ? null : entry.getKey().getKeyOrNull())) {
        return true;
      }
    }
    return false;
  }

  public static int getLevel(final ItemStack item, final org.bukkit.enchantments.Enchantment enchantment) {
    return enchantment == null ? 0 : getLevel(item, enchantment.getKeyOrNull());
  }

  public static int getLevel(final ItemStack item, final Enchantment enchantment) {
    return enchantment == null ? 0 : getLevel(item, enchantment.getKey());
  }

  public static int getLevel(final ItemStack item, final NamespacedKey key) {
    if (key == null || item == null) {
      return 0;
    }
    final ItemMeta meta = item.getItemMeta();
    if (meta == null || meta.getEnchants() == null || meta.getEnchants().isEmpty()) {
      return 0;
    }
    for (final Map.Entry<org.bukkit.enchantments.Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
      if (key.equals(entry.getKey() == null ? null : entry.getKey().getKeyOrNull())) {
        return entry.getValue();
      }
    }
    return 0;
  }
}
