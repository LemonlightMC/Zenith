package com.lemonlightmc.zenith.apis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.lemonlightmc.zenith.items.Potion;

import java.util.Collection;
import java.util.Set;

public class PotionAPI {

  public static final Set<Material> potionMaterials = Set.of(Material.POTION, Material.SPLASH_POTION,
      Material.LINGERING_POTION, Material.TIPPED_ARROW);

  public static boolean isPotionItem(final ItemStack item) {
    return potionMaterials.contains(item.getType());
  }

  public static void applyPotion(final Player p, final Potion potion) {
    if (potion == null) {
      return;
    }
    potion.playerApplyPotion(p);
  }

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

  public static void removePotion(final Player p, final Potion potion) {
    if (potion == null) {
      return;
    }
    potion.playerRemovePotion(p);
  }

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

  public static boolean hasPotion(final Player p, final Potion potion) {
    return potion == null ? true : potion.playerHasPotion(p);
  }

  public static void applyEffect(final Player p, final PotionEffect effect) {
    if (effect == null) {
      return;
    }
    p.addPotionEffect(effect);
  }

  public static void removeEffect(final Player p, final PotionEffect effect) {
    if (effect == null) {
      return;
    }
    p.removePotionEffect(effect.getType());
  }

  public static void applyEffects(final Player p, final Collection<PotionEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return;
    }
    p.addPotionEffects(effects);
  }

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

  public static boolean hasEffect(final Player p, final PotionEffect effect) {
    return p == null ? false : p.hasPotionEffect(effect.getType());
  }

  public static Collection<PotionEffect> getEffects(final Player p) {
    return p == null ? null : p.getActivePotionEffects();
  }
}