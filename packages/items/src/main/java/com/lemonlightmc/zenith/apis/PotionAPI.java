package com.lemonlightmc.zenith.apis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.lemonlightmc.zenith.items.Potion;

import java.util.Collection;
import java.util.Set;

/**
 * Utility methods for working with potion items, custom potions, and Bukkit potion effects.
 */
public class PotionAPI {

  /**
   * Materials that represent potion-like items.
   */
  public static final Set<Material> potionMaterials = Set.of(Material.POTION, Material.SPLASH_POTION,
      Material.LINGERING_POTION, Material.TIPPED_ARROW);

  /**
   * Checks whether the given item stack is a potion-like item.
   *
   * @param item the item stack to check
   * @return {@code true} if the item is a potion, splash potion, lingering potion, or tipped arrow
   */
  public static boolean isPotionItem(final ItemStack item) {
    return potionMaterials.contains(item.getType());
  }

  /**
   * Applies a custom potion to a player.
   *
   * @param p the player to apply the potion to
   * @param potion the custom potion to apply
   */
  public static void applyPotion(final Player p, final Potion potion) {
    if (potion == null) {
      return;
    }
    potion.playerApplyPotion(p);
  }

  /**
   * Applies multiple custom potions to a player.
   *
   * @param p the player to apply the potions to
   * @param potions the custom potions to apply
   */
  public static void applyPotion(final Player p, final Collection<Potion> potions) {
    if (potions == null || potions.isEmpty()) {
      return;
    }
    for (final Potion potion : potions) {
      if (potion == null) {
        return;
      }
      potion.playerApplyPotion(p);
    }
  }

  /**
   * Removes a custom potion from a player.
   *
   * @param p the player to remove the potion from
   * @param potion the custom potion to remove
   */
  public static void removePotion(final Player p, final Potion potion) {
    if (potion == null) {
      return;
    }
    potion.playerRemovePotion(p);
  }

  /**
   * Removes multiple custom potions from a player.
   *
   * @param p the player to remove the potions from
   * @param potions the custom potions to remove
   */
  public static void removePotion(final Player p, final Collection<Potion> potions) {
    if (potions == null || potions.isEmpty()) {
      return;
    }
    for (final Potion potion : potions) {
      if (potion == null) {
        return;
      }
      potion.playerRemovePotion(p);
    }
  }

  /**
   * Checks whether a player currently has all effects from a custom potion.
   *
   * @param p the player to inspect
   * @param potion the custom potion to check
   * @return {@code true} if the potion is {@code null} or the player has all potion effects
   */
  public static boolean hasPotion(final Player p, final Potion potion) {
    return potion == null ? true : potion.playerHasPotion(p);
  }

  /**
   * Applies a Bukkit potion effect to a player.
   *
   * @param p the player to apply the effect to
   * @param effect the effect to apply
   */
  public static void applyEffect(final Player p, final PotionEffect effect) {
    if (effect == null) {
      return;
    }
    p.addPotionEffect(effect);
  }

  /**
   * Removes a Bukkit potion effect from a player.
   *
   * @param p the player to remove the effect from
   * @param effect the effect to remove
   */
  public static void removeEffect(final Player p, final PotionEffect effect) {
    if (effect == null) {
      return;
    }
    p.removePotionEffect(effect.getType());
  }

  /**
   * Applies multiple Bukkit potion effects to a player.
   *
   * @param p the player to apply the effects to
   * @param effects the effects to apply
   */
  public static void applyEffects(final Player p, final Collection<PotionEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return;
    }
    p.addPotionEffects(effects);
  }

  /**
   * Removes multiple Bukkit potion effects from a player.
   *
   * @param p the player to remove the effects from
   * @param effects the effects to remove
   */
  public static void removeEffects(final Player p, final Collection<PotionEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return;
    }
    for (final PotionEffect effect : effects) {
      if (effect == null) {
        return;
      }
      p.removePotionEffect(effect.getType());
    }
  }

  /**
   * Checks whether a player has the specified Bukkit potion effect type active.
   *
   * @param p the player to inspect
   * @param effect the effect to check
   * @return {@code true} if the player has the effect type, otherwise {@code false}
   */
  public static boolean hasEffect(final Player p, final PotionEffect effect) {
    return p == null ? false : p.hasPotionEffect(effect.getType());
  }

  /**
   * Gets the active Bukkit potion effects for a player.
   *
   * @param p the player to inspect
   * @return the player's active potion effects, or {@code null} if the player is {@code null}
   */
  public static Collection<PotionEffect> getEffects(final Player p) {
    return p == null ? null : p.getActivePotionEffects();
  }
}