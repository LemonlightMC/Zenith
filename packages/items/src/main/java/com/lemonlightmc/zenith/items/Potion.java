package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;

import com.lemonlightmc.zenith.additive.time.PolyTimeUnit;
import com.lemonlightmc.zenith.apis.PotionAPI;
import com.lemonlightmc.zenith.interfaces.Cloneable;

public class Potion implements Cloneable<Potion> {
  private NamespacedKey key;
  private Color color;
  private Material material = Material.POTION;
  private float durationScale = 1.0f;
  private PotionEffectTypeCategory category;
  private boolean isUpgradeable = true;
  private boolean isExtendable = true;
  private List<PotionEffect> effects;
  private List<org.bukkit.potion.PotionEffect> bukkitEffects = null;

  public Potion(final NamespacedKey key, final List<PotionEffect> effects, final Material material,
      final PotionEffectTypeCategory category, final Color color, final boolean isUpgradeable,
      final boolean isExtendable,
      final float durationScale) {
    if (key == null) {
      throw new IllegalArgumentException("Potion Key cant be null");
    }
    this.key = key;
    this.effects = effects == null ? new ArrayList<>() : effects;
    this.material = material == null ? Material.POTION : material;
    this.category = category == null ? PotionEffectTypeCategory.NEUTRAL : category;
    this.color = color;
    this.isUpgradeable = isUpgradeable;
    this.isExtendable = isExtendable;
    this.durationScale = durationScale;
  }

  public Potion(final NamespacedKey key, final List<PotionEffect> effects, final Material material,
      final PotionEffectTypeCategory category, final Color color) {
    this(key, effects, material, category, color, true, true, 1.0f);
  }

  public Potion(final NamespacedKey key, final List<PotionEffect> effects, final Material material,
      final PotionEffectTypeCategory category) {
    this(key, effects, material, category, null, true, true, 1.0f);
  }

  public Potion(final NamespacedKey key, final List<PotionEffect> effects) {
    this(key, effects, Material.POTION, PotionEffectTypeCategory.NEUTRAL, null, true, true, 1.0f);
  }

  public static Potion from(final Potion potion) {
    if (potion == null) {
      return null;
    }
    return new Potion(potion.key, potion.effects, potion.material, potion.category, potion.color, potion.isUpgradeable,
        potion.isExtendable, potion.durationScale);
  }

  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects) {
    return new Potion(key, effects);
  }

  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects,
      final Material material,
      final PotionEffectTypeCategory category) {
    return new Potion(key, effects, material, category, null);
  }

  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects,
      final Material material,
      final PotionEffectTypeCategory category, final Color color) {
    return new Potion(key, effects, material, category, color);
  }

  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects,
      final Material material,
      final PotionEffectTypeCategory category, final Color color, final boolean isUpgradeable,
      final boolean isExtendable,
      final float durationScale) {
    return new Potion(key, effects, material, category, color, isUpgradeable, isExtendable, durationScale);
  }

  public Potion setKey(final NamespacedKey key) {
    if (key == null) {
      throw new IllegalArgumentException("Potion Key cant be null");
    }
    this.key = key;
    return this;
  }

  public NamespacedKey getKey() {
    return key;
  }

  public Potion setColor(final Color color) {
    this.color = color;
    return this;
  }

  public Potion setColor(final java.awt.Color color) {
    if (color != null) {
      this.color = Color.fromARGB(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }
    return this;
  }

  public Color getColor() {
    return color;
  }

  public Potion setMaterial(final Material material) {
    if (material == null) {
      throw new IllegalArgumentException("Potion Material cant be null");
    }
    this.material = material;
    return this;
  }

  public Material getMaterial() {
    return material;
  }

  public Potion setCategory(final PotionEffectTypeCategory category) {
    this.category = category;
    return this;
  }

  public PotionEffectTypeCategory getCategory() {
    return category;
  }

  public Potion setHarmful() {
    this.category = PotionEffectTypeCategory.HARMFUL;
    return this;
  }

  public Potion setNeutral() {
    this.category = PotionEffectTypeCategory.NEUTRAL;
    return this;
  }

  public Potion setBeneficial() {
    this.category = PotionEffectTypeCategory.BENEFICIAL;
    return this;
  }

  public boolean isHarmful() {
    return this.category.equals(PotionEffectTypeCategory.HARMFUL);
  }

  public boolean isNeutral() {
    return this.category.equals(PotionEffectTypeCategory.NEUTRAL);
  }

  public boolean isBeneficial() {
    return this.category.equals(PotionEffectTypeCategory.BENEFICIAL);
  }

  public Potion setDurationScale(final float scale) {
    this.durationScale = scale;
    return this;
  }

  public float getDurationScale() {
    return durationScale;
  }

  public boolean isExtendable() {
    return isExtendable;
  }

  public boolean isUpgradeable() {
    return isUpgradeable;
  }

  public Potion setExtendable(final boolean value) {
    this.isExtendable = value;
    return this;
  }

  public Potion setUpgradeable(final boolean value) {
    this.isUpgradeable = value;
    return this;
  }

  public Potion setEffects(final PotionEffect... effects) {
    if (effects != null && effects.length != 0) {
      this.effects = List.of(effects);
      bukkitEffects = null;
    }
    return this;
  }

  public Potion setEffects(final List<PotionEffect> effects) {
    if (effects != null && !effects.isEmpty()) {
      this.effects = effects;
      bukkitEffects = null;
    }
    return this;
  }

  public Potion addEffect(final PotionEffectType type, final long duration, final PolyTimeUnit unit,
      final int amplifier) {
    if (duration <= 0 || amplifier <= 0 || type == null) {
      return this;
    }
    effects.add(new PotionEffect(type, unit == null ? duration : unit.toTicks(duration), amplifier));
    bukkitEffects = null;
    return this;
  }

  public Potion addEffect(final PotionEffectType type, final long duration, final int amplifier) {
    if (duration <= 0 || amplifier <= 0 || type == null) {
      return this;
    }
    effects.add(new PotionEffect(type, duration, amplifier));
    bukkitEffects = null;
    return this;
  }

  public Potion addEffects(final PotionEffect... effects) {
    if (effects != null && effects.length != 0) {
      addEffects(List.of(effects));
    }
    return this;
  }

  public Potion addEffects(final List<PotionEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return this;
    }
    for (final PotionEffect potionEffect : effects) {
      if (potionEffect != null) {
        this.effects.add(potionEffect);
      }
    }
    bukkitEffects = null;
    return this;
  }

  public boolean hasEffect(final PotionEffect effect) {
    return effects.contains(effect);
  }

  public Potion removeEffects(final PotionEffect... effects) {
    if (effects == null || effects.length == 0 || this.effects == null || this.effects.isEmpty()) {
      return this;
    }
    return removeEffects(List.of(effects));
  }

  public Potion removeEffects(final List<PotionEffect> effects) {
    if (effects == null || effects.isEmpty() || this.effects == null || this.effects.isEmpty()) {
      return this;
    }
    for (final PotionEffect potionEffect : effects) {
      if (potionEffect != null) {
        this.effects.remove(potionEffect);
      }
    }
    bukkitEffects = null;
    return this;
  }

  public Potion clearEffects() {
    effects.clear();
    bukkitEffects = null;
    return this;
  }

  public List<PotionEffect> getEffects() {
    return effects;
  }

  public List<org.bukkit.potion.PotionEffect> getBukkitEffects() {
    if (bukkitEffects != null || effects == null || effects.isEmpty()) {
      return bukkitEffects;
    }
    bukkitEffects = new ArrayList<>();
    for (final PotionEffect potionEffect : effects) {
      bukkitEffects.add(potionEffect.toBukkit());
    }
    return bukkitEffects;
  }

  public Potion playerApplyPotion(Player p) {
    if (p != null) {
      p.addPotionEffects(getBukkitEffects());
    }
    return this;
  }

  public Potion playerRemovePotion(Player p) {
    if (p == null) {
      return this;
    }
    for (org.bukkit.potion.PotionEffect potionEffect : getBukkitEffects()) {
      p.removePotionEffect(potionEffect.getType());
    }
    return this;
  }

  public boolean playerHasPotion(Player p) {
    if (p == null) {
      return false;
    }
    for (org.bukkit.potion.PotionEffect potionEffect : getBukkitEffects()) {
      if (!p.hasPotionEffect(potionEffect.getType())) {
        return false;
      }
    }
    return true;
  }

  public ItemStack getItem() {
    return getItem(new ItemStack(this.material));
  }

  public ItemStack getItem(final Material material) {
    return getItem(new ItemStack(material));
  }

  public ItemStack getItem(final ItemStack item) {
    if (!PotionAPI.isPotionItem(item)) {
      throw new IllegalArgumentException("Invalid Potion Item");
    }
    final PotionMeta meta = (PotionMeta) item.getItemMeta();

    meta.setItemName(key.toString());
    meta.setColor(color);
    meta.setDurationScale(durationScale);
    for (final PotionEffect effect : effects) {
      meta.addCustomEffect(effect.toBukkit(), false);
    }

    item.setItemMeta(meta);
    return item;
  }

  @Override
  public Potion clone() {
    return new Potion(key, effects, material, category, color, isUpgradeable, isExtendable, durationScale);
  }

  @Override
  public String toString() {
    return "Potion [key=" + key + ", color=" + color + ", material=" + material + ", category=" + category
        + ", effects=" + effects + "]";
  }

  @Override
  public int hashCode() {
    int result = 31 + key.hashCode();
    result = 31 * result + ((color == null) ? 0 : color.hashCode());
    result = 31 * result + ((material == null) ? 0 : material.hashCode());
    result = 31 * result + Float.floatToIntBits(durationScale);
    result = 31 * result + ((category == null) ? 0 : category.hashCode());
    result = 31 * result + (isUpgradeable ? 1231 : 1237);
    result = 31 * result + (isExtendable ? 1231 : 1237);
    return 13 * result + effects.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Potion other = (Potion) obj;
    if (color == null && other.color != null || category == null && other.category != null) {
      return false;
    }
    return key.equals(other.key) && color.equals(other.color)
        && Float.floatToIntBits(durationScale) == Float.floatToIntBits(other.durationScale)
        && material == other.material && category == other.category && isUpgradeable == other.isUpgradeable
        && isExtendable == other.isExtendable && effects.equals(other.effects);
  }
}
