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

import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.items.Enchantment;

/**
 * Utility methods for querying and registering Bukkit enchantments.
 */
public class EnchantmentAPI {
  private static final org.bukkit.enchantments.Enchantment[] values = values();
  private static final NamespacedKey[] keys = keys();

  /**
   * Gets an enchantment by its namespaced key.
   *
   * @param key the namespaced key of the enchantment
   * @return the matching enchantment, or {@code null} if the key is {@code null} or not found
   */
  public static org.bukkit.enchantments.Enchantment getByKey(final NamespacedKey key) {
    if (key == null) {
      return null;
    }
    return Registry.ENCHANTMENT.get(key);
  }

  /**
   * Gets an enchantment by its key string.
   *
   * @param name the enchantment key string to resolve
   * @return the matching enchantment, or {@code null} if the name is {@code null} or not found
   */
  public static org.bukkit.enchantments.Enchantment getByName(final String name) {
    if (name == null) {
      return null;
    }
    return getByKey(NamespacedKey.fromString(name.toLowerCase(Locale.ROOT)));
  }

  /**
   * Returns the namespaced keys of all registered enchantments.
   *
   * @return an array containing the keys of all registered enchantments
   */
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

  /**
   * Returns all registered enchantments.
   *
   * @return an array containing all registered enchantments
   */
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

  /**
   * Checks whether an enchantment with the given key is registered.
   *
   * @param key the namespaced key to check
   * @return {@code true} if the enchantment is registered, otherwise {@code false}
   */
  public static boolean isRegistered(final NamespacedKey key) {
    return key == null ? false : Registry.ENCHANTMENT.get(key) != null;
  }

  /**
   * Registers a custom enchantment with the Bukkit enchantment registry.
   *
   * @param enchantment the custom enchantment to register
   */
  public static void register(final Enchantment enchantment) {
    if (enchantment == null) {
      return;
    }
    if (isRegistered(enchantment.getKey())) {
      GlobalLogger.warn("Enchantment " + enchantment.getName() + " has already been registered");
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
      GlobalLogger.warn("Failed to register the Enchantment: " + enchantment.getName());
      e.printStackTrace();
    }
  }

  /**
   * Checks whether an item has the specified Bukkit enchantment.
   *
   * @param item the item to inspect
   * @param enchantment the enchantment to look for
   * @return {@code true} if the item has the enchantment, otherwise {@code false}
   */
  public static boolean hasEnchantment(final ItemStack item, final org.bukkit.enchantments.Enchantment enchantment) {
    return enchantment == null ? false : hasEnchantment(item, enchantment.getKeyOrNull());
  }

  /**
   * Checks whether an item has the specified custom enchantment.
   *
   * @param item the item to inspect
   * @param enchantment the custom enchantment to look for
   * @return {@code true} if the item has the enchantment, otherwise {@code false}
   */
  public static boolean hasEnchantment(final ItemStack item, final Enchantment enchantment) {
    return enchantment == null ? false : hasEnchantment(item, enchantment.getKey());
  }

  /**
   * Checks whether an item has an enchantment with the given key.
   *
   * @param item the item to inspect
   * @param key the namespaced key of the enchantment to look for
   * @return {@code true} if the item has the enchantment, otherwise {@code false}
   */
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

  /**
   * Gets the level of the specified Bukkit enchantment on an item.
   *
   * @param item the item to inspect
   * @param enchantment the enchantment to look up
   * @return the enchantment level, or {@code 0} if the item does not have the enchantment
   */
  public static int getLevel(final ItemStack item, final org.bukkit.enchantments.Enchantment enchantment) {
    return enchantment == null ? 0 : getLevel(item, enchantment.getKeyOrNull());
  }

  /**
   * Gets the level of the specified custom enchantment on an item.
   *
   * @param item the item to inspect
   * @param enchantment the custom enchantment to look up
   * @return the enchantment level, or {@code 0} if the item does not have the enchantment
   */
  public static int getLevel(final ItemStack item, final Enchantment enchantment) {
    return enchantment == null ? 0 : getLevel(item, enchantment.getKey());
  }

  /**
   * Gets the level of an enchantment with the given key on an item.
   *
   * @param item the item to inspect
   * @param key the namespaced key of the enchantment to look up
   * @return the enchantment level, or {@code 0} if the item does not have the enchantment
   */
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
