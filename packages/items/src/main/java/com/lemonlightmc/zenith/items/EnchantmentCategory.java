package com.lemonlightmc.zenith.items;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public enum EnchantmentCategory implements Iterable<Enchantment> {

  ANY() {

    @Override
    public List<Enchantment> get() {
      return anyEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return anyEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return true;
    }
  },

  UNKOWN() {

    @Override
    public List<Enchantment> get() {
      return List.<Enchantment>of();
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return List.<Enchantment>of().iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return false;
    }
  },

  DEFAULT() {

    @Override
    public List<Enchantment> get() {
      return defaultEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return defaultEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return true;
    }
  },

  ANY_TOOL() {

    @Override
    public List<Enchantment> get() {
      return toolsEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return toolsEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return HOE.applicable(material) || PICKAXE.applicable(material) || SHOVEL.applicable(material)
          || AXE.applicable(material);
    }
  },

  AXE() {

    @Override
    public List<Enchantment> get() {
      return axeEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return axeEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.WOODEN_AXE.equals(material) || Material.STONE_AXE.equals(material)
          || Material.GOLDEN_AXE.equals(material)
          || Material.IRON_AXE.equals(material)
          || Material.DIAMOND_AXE.equals(material) || Material.NETHERITE_AXE.equals(material);
    }
  },

  PICKAXE() {

    @Override
    public List<Enchantment> get() {
      return toolsEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return toolsEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.WOODEN_PICKAXE.equals(material) || Material.STONE_PICKAXE.equals(material)
          || Material.GOLDEN_PICKAXE.equals(material)
          || Material.IRON_PICKAXE.equals(material)
          || Material.DIAMOND_PICKAXE.equals(material) || Material.NETHERITE_PICKAXE.equals(material);
    }
  },

  SHOVEL() {

    @Override
    public List<Enchantment> get() {
      return toolsEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return toolsEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.WOODEN_SHOVEL.equals(material) || Material.STONE_SHOVEL.equals(material)
          || Material.GOLDEN_SHOVEL.equals(material)
          || Material.IRON_SHOVEL.equals(material)
          || Material.DIAMOND_SHOVEL.equals(material) || Material.NETHERITE_SHOVEL.equals(material);
    }
  },

  HOE() {

    @Override
    public List<Enchantment> get() {
      return toolsEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return toolsEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.WOODEN_HOE.equals(material) || Material.STONE_HOE.equals(material)
          || Material.GOLDEN_HOE.equals(material)
          || Material.IRON_HOE.equals(material)
          || Material.DIAMOND_HOE.equals(material) || Material.NETHERITE_HOE.equals(material);
    }
  },

  SHIELD() {

    @Override
    public List<Enchantment> get() {
      return defaultEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return defaultEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.SHIELD.equals(material);
    }
  },

  ELYTRA() {
    @Override
    public List<Enchantment> get() {
      return defaultEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return defaultEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.ELYTRA.equals(material);
    }
  },

  SHEARS() {

    @Override
    public List<Enchantment> get() {
      return shearsEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return shearsEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.SHEARS.equals(material);
    }
  },

  BRUSH() {

    @Override
    public List<Enchantment> get() {
      return defaultEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return defaultEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.BRUSH.equals(material);
    }
  },

  FLINT_STEEL() {
    @Override
    public List<Enchantment> get() {
      return defaultEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return defaultEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.FLINT_AND_STEEL.equals(material);
    }
  },

  WARPED_FUNGUS_ON_A_STICK() {
    @Override
    public List<Enchantment> get() {
      return defaultEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return defaultEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.WARPED_FUNGUS_ON_A_STICK.equals(material);
    }
  },

  CARROT_ON_A_STICK() {
    @Override
    public List<Enchantment> get() {
      return defaultEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return defaultEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.CARROT_ON_A_STICK.equals(material);
    }
  },

  MACE() {
    @Override
    public List<Enchantment> get() {
      return maceEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return maceEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.MACE.equals(material);
    }
  },

  SWORD() {

    @Override
    public List<Enchantment> get() {
      return swordEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return swordEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.WOODEN_SWORD.equals(material) || Material.STONE_SWORD.equals(material)
          || Material.GOLDEN_SWORD.equals(material)
          || Material.IRON_SWORD.equals(material)
          || Material.DIAMOND_SWORD.equals(material) || Material.NETHERITE_SWORD.equals(material);
    }
  },

  ANY_ARMOR() {

    @Override
    public List<Enchantment> get() {
      return armorEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return armorEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return HELMET.applicable(material) || CHESTPLATE.applicable(material) || LEGGINGS.applicable(material)
          || BOOTS.applicable(material);
    }
  },

  HELMET() {

    @Override
    public List<Enchantment> get() {
      return helmetEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return helmetEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.LEATHER_HELMET.equals(material) || Material.GOLDEN_HELMET.equals(material)
          || Material.IRON_HELMET.equals(material) || Material.CHAINMAIL_HELMET.equals(material)
          || Material.DIAMOND_HELMET.equals(material) || Material.NETHERITE_HELMET.equals(material)
          || Material.TURTLE_HELMET.equals(material);
    }
  },

  CHESTPLATE() {

    @Override
    public List<Enchantment> get() {
      return armorEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return armorEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.LEATHER_CHESTPLATE.equals(material) || Material.GOLDEN_CHESTPLATE.equals(material)
          || Material.IRON_CHESTPLATE.equals(material) || Material.CHAINMAIL_CHESTPLATE.equals(material)
          || Material.DIAMOND_CHESTPLATE.equals(material) || Material.NETHERITE_CHESTPLATE.equals(material);
    }
  },

  LEGGINGS() {

    @Override
    public List<Enchantment> get() {
      return leggingsEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return leggingsEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.LEATHER_LEGGINGS.equals(material) || Material.GOLDEN_LEGGINGS.equals(material)
          || Material.IRON_LEGGINGS.equals(material) || Material.CHAINMAIL_LEGGINGS.equals(material)
          || Material.DIAMOND_LEGGINGS.equals(material) || Material.NETHERITE_LEGGINGS.equals(material);
    }
  },

  BOOTS() {

    @Override
    public List<Enchantment> get() {
      return bootsEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return bootsEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.LEATHER_BOOTS.equals(material) || Material.GOLDEN_BOOTS.equals(material)
          || Material.IRON_BOOTS.equals(material) || Material.CHAINMAIL_BOOTS.equals(material)
          || Material.DIAMOND_BOOTS.equals(material) || Material.NETHERITE_BOOTS.equals(material);
    }
  },

  BOW() {

    @Override
    public List<Enchantment> get() {
      return bowEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return bowEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.BOW.equals(material);
    }
  },

  CROSSBOW() {

    @Override
    public List<Enchantment> get() {
      return crossbowEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return crossbowEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.CROSSBOW.equals(material);
    }
  },

  FISHING_ROD() {

    @Override
    public List<Enchantment> get() {
      return fishingRodEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return fishingRodEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.FISHING_ROD.equals(material);
    }
  },

  TRIDENT() {

    @Override
    public List<Enchantment> get() {
      return tridentEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return tridentEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return Material.TRIDENT.equals(material);
    }
  },

  CURSES() {

    @Override
    public List<Enchantment> get() {
      return cursesEnchantments;
    }

    @Override
    public java.util.Iterator<Enchantment> iterator() {
      return cursesEnchantments.iterator();
    }

    @Override
    public boolean applicable(final Material material) {
      return ANY_ARMOR.applicable(material) || Material.COMPASS.equals(material)
          || Material.RECOVERY_COMPASS.equals(material);
    }
  };

  private static List<Enchantment> anyEnchantments = org.bukkit.Registry.ENCHANTMENT.stream().toList();
  private static List<Enchantment> cursesEnchantments = List.of(Enchantment.VANISHING_CURSE,
      Enchantment.BINDING_CURSE);
  private static List<Enchantment> defaultEnchantments = List.of(Enchantment.MENDING,
      Enchantment.UNBREAKING);

  private static List<Enchantment> swordEnchantments = merge(Arrays.asList(
      Enchantment.SHARPNESS, Enchantment.BANE_OF_ARTHROPODS, Enchantment.SMITE, Enchantment.SWEEPING_EDGE,
      Enchantment.FIRE_ASPECT, Enchantment.KNOCKBACK, Enchantment.LOOTING), defaultEnchantments);
  private static List<Enchantment> maceEnchantments = merge(
      Arrays.asList(Enchantment.BANE_OF_ARTHROPODS, Enchantment.SMITE, Enchantment.FIRE_ASPECT,
          Enchantment.WIND_BURST, Enchantment.DENSITY, Enchantment.BREACH),
      defaultEnchantments);

  private static List<Enchantment> bowEnchantments = merge(Arrays.asList(
      Enchantment.FLAME, Enchantment.INFINITY, Enchantment.POWER, Enchantment.PUNCH), defaultEnchantments);
  private static List<Enchantment> crossbowEnchantments = merge(Arrays.asList(
      Enchantment.MULTISHOT, Enchantment.PIERCING, Enchantment.QUICK_CHARGE), defaultEnchantments);

  private static List<Enchantment> armorEnchantments = merge(Arrays.asList(Enchantment.BLAST_PROTECTION,
      Enchantment.FIRE_PROTECTION, Enchantment.PROJECTILE_PROTECTION, Enchantment.PROTECTION, Enchantment.THORNS,
      Enchantment.VANISHING_CURSE,
      Enchantment.BINDING_CURSE),
      defaultEnchantments);
  private static List<Enchantment> helmetEnchantments = merge(Arrays.asList(
      Enchantment.AQUA_AFFINITY, Enchantment.RESPIRATION), armorEnchantments);
  private static List<Enchantment> leggingsEnchantments = merge(Arrays.asList(Enchantment.SWIFT_SNEAK),
      armorEnchantments);;
  private static List<Enchantment> bootsEnchantments = merge(Arrays.asList(
      Enchantment.DEPTH_STRIDER, Enchantment.FROST_WALKER, Enchantment.FEATHER_FALLING, Enchantment.SOUL_SPEED),
      armorEnchantments);

  private static List<Enchantment> fishingRodEnchantments = merge(Arrays.asList(
      Enchantment.LURE, Enchantment.LUCK_OF_THE_SEA), defaultEnchantments);
  private static List<Enchantment> shearsEnchantments = merge(Arrays.asList(
      Enchantment.EFFICIENCY), defaultEnchantments);
  private static List<Enchantment> tridentEnchantments = merge(Arrays.asList(
      Enchantment.CHANNELING, Enchantment.IMPALING, Enchantment.LOYALTY,
      Enchantment.RIPTIDE), defaultEnchantments);

  private static List<Enchantment> toolsEnchantments = merge(Arrays.asList(
      Enchantment.FORTUNE, Enchantment.SILK_TOUCH, Enchantment.EFFICIENCY), defaultEnchantments);
  private static List<Enchantment> axeEnchantments = merge(Arrays.asList(
      Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS, Enchantment.SHARPNESS), toolsEnchantments);

  public abstract List<Enchantment> get();

  @Override
  public abstract Iterator<Enchantment> iterator();

  public abstract boolean applicable(Material material);

  public static EnchantmentCategory getByMaterial(final Material material) {
    if (material == null) {
      return null;
    }
    if (material.toString().contains("_HELMET")) {
      return EnchantmentCategory.HELMET;
    } else if (material.toString().contains("_CHESTPLATE")) {
      return EnchantmentCategory.CHESTPLATE;
    } else if (material.toString().contains("_BOOTS")) {
      return EnchantmentCategory.BOOTS;
    } else if (material.toString().contains("_LEGGINGS")) {
      return EnchantmentCategory.LEGGINGS;
    } else if (material.toString().contains("_PICKAXE")) {
      return EnchantmentCategory.PICKAXE;
    } else if (material.toString().contains("_SHOVEL")) {
      return EnchantmentCategory.SHOVEL;
    } else if (material.toString().contains("_AXE")) {
      return EnchantmentCategory.AXE;
    } else if (material.toString().contains("_HOE")) {
      return EnchantmentCategory.HOE;
    } else if (material.toString().contains("_SHIELD")) {
      return EnchantmentCategory.HOE;
    } else if (material.toString().contains("_BRUSH")) {
      return EnchantmentCategory.HOE;
    } else if (material.toString().contains("_FLINT_STEEL")) {
      return EnchantmentCategory.HOE;
    } else if (material.toString().contains("_SWORD")) {
      return EnchantmentCategory.SWORD;
    } else if (material == Material.BOW) {
      return EnchantmentCategory.BOW;
    } else if (material == Material.CROSSBOW) {
      return EnchantmentCategory.CROSSBOW;
    } else if (material == Material.TRIDENT) {
      return EnchantmentCategory.TRIDENT;
    } else if (material == Material.FISHING_ROD) {
      return EnchantmentCategory.FISHING_ROD;
    }
    return EnchantmentCategory.ANY;
  }

  public static EnchantmentCategory getByItemStack(final ItemStack itemStack) {
    if (itemStack == null) {
      return null;
    }
    final Material material = itemStack.getType();
    return getByMaterial(material);
  }

  private static <T> List<T> merge(final List<T> list1, final List<T> list2) {
    for (final T t : list2) {
      if (!list1.contains(t)) {
        list1.add(t);
      }
    }
    return list1;
  }
}
