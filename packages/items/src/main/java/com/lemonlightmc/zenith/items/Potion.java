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

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;
import com.lemonlightmc.zenith.additive.time.PolyTimeUnit;
import com.lemonlightmc.zenith.apis.PotionAPI;

/**
 * Mutable representation of a custom potion definition and its effects.
 */
public class Potion implements Cloneable<Potion>, ItemModifier {
  private NamespacedKey key;
  private Color color;
  private Material material = Material.POTION;
  private float durationScale = 1.0f;
  private PotionEffectTypeCategory category;
  private boolean isUpgradeable = true;
  private boolean isExtendable = true;
  private List<PotionEffect> effects;
  private List<org.bukkit.potion.PotionEffect> bukkitEffects = null;

  /**
   * Creates a custom potion definition.
   *
   * @param key           the namespaced key identifying the potion
   * @param effects       the custom potion effects
   * @param material      the potion item material
   * @param category      the potion effect category
   * @param color         the potion display color
   * @param isUpgradeable whether the potion can be upgraded
   * @param isExtendable  whether the potion can be extended
   * @param durationScale the duration scale applied to potion effects
   * @throws IllegalArgumentException if the key is {@code null}
   */
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

  /**
   * Creates a custom potion definition with default upgrade, extend, and duration
   * scale settings.
   *
   * @param key      the namespaced key identifying the potion
   * @param effects  the custom potion effects
   * @param material the potion item material
   * @param category the potion effect category
   * @param color    the potion display color
   */
  public Potion(final NamespacedKey key, final List<PotionEffect> effects, final Material material,
      final PotionEffectTypeCategory category, final Color color) {
    this(key, effects, material, category, color, true, true, 1.0f);
  }

  /**
   * Creates a custom potion definition with default color and behavior settings.
   *
   * @param key      the namespaced key identifying the potion
   * @param effects  the custom potion effects
   * @param material the potion item material
   * @param category the potion effect category
   */
  public Potion(final NamespacedKey key, final List<PotionEffect> effects, final Material material,
      final PotionEffectTypeCategory category) {
    this(key, effects, material, category, null, true, true, 1.0f);
  }

  /**
   * Creates a custom potion definition with default material, category, and
   * behavior settings.
   *
   * @param key     the namespaced key identifying the potion
   * @param effects the custom potion effects
   */
  public Potion(final NamespacedKey key, final List<PotionEffect> effects) {
    this(key, effects, Material.POTION, PotionEffectTypeCategory.NEUTRAL, null, true, true, 1.0f);
  }

  /**
   * Creates a copy of another potion definition.
   *
   * @param potion the potion to copy
   * @return a copied potion, or {@code null} if the input was {@code null}
   */
  public static Potion from(final Potion potion) {
    if (potion == null) {
      return null;
    }
    return new Potion(potion.key, potion.effects, potion.material, potion.category, potion.color, potion.isUpgradeable,
        potion.isExtendable, potion.durationScale);
  }

  /**
   * Creates a custom potion definition.
   *
   * @param key     the namespaced key identifying the potion
   * @param effects the custom potion effects
   * @return a new potion definition
   */
  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects) {
    return new Potion(key, effects);
  }

  /**
   * Creates a custom potion definition.
   *
   * @param key      the namespaced key identifying the potion
   * @param effects  the custom potion effects
   * @param material the potion item material
   * @param category the potion effect category
   * @return a new potion definition
   */
  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects,
      final Material material,
      final PotionEffectTypeCategory category) {
    return new Potion(key, effects, material, category, null);
  }

  /**
   * Creates a custom potion definition.
   *
   * @param key      the namespaced key identifying the potion
   * @param effects  the custom potion effects
   * @param material the potion item material
   * @param category the potion effect category
   * @param color    the potion display color
   * @return a new potion definition
   */
  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects,
      final Material material,
      final PotionEffectTypeCategory category, final Color color) {
    return new Potion(key, effects, material, category, color);
  }

  /**
   * Creates a custom potion definition.
   *
   * @param key           the namespaced key identifying the potion
   * @param effects       the custom potion effects
   * @param material      the potion item material
   * @param category      the potion effect category
   * @param color         the potion display color
   * @param isUpgradeable whether the potion can be upgraded
   * @param isExtendable  whether the potion can be extended
   * @param durationScale the duration scale applied to potion effects
   * @return a new potion definition
   */
  public static Potion from(final NamespacedKey key, final List<PotionEffect> effects,
      final Material material,
      final PotionEffectTypeCategory category, final Color color, final boolean isUpgradeable,
      final boolean isExtendable,
      final float durationScale) {
    return new Potion(key, effects, material, category, color, isUpgradeable, isExtendable, durationScale);
  }

  /**
   * Sets the namespaced key identifying this potion.
   *
   * @param key the potion key
   * @return this potion
   * @throws IllegalArgumentException if the key is {@code null}
   */
  public Potion setKey(final NamespacedKey key) {
    if (key == null) {
      throw new IllegalArgumentException("Potion Key cant be null");
    }
    this.key = key;
    return this;
  }

  /**
   * Gets the namespaced key identifying this potion.
   *
   * @return the potion key
   */
  public NamespacedKey getKey() {
    return key;
  }

  /**
   * Sets the display color of this potion.
   *
   * @param color the potion color
   * @return this potion
   */
  public Potion setColor(final Color color) {
    this.color = color;
    return this;
  }

  /**
   * Sets the display color of this potion from an AWT color.
   *
   * @param color the AWT color to convert
   * @return this potion
   */
  public Potion setColor(final java.awt.Color color) {
    if (color != null) {
      this.color = Color.fromARGB(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }
    return this;
  }

  /**
   * Gets the display color of this potion.
   *
   * @return the potion color
   */
  public Color getColor() {
    return color;
  }

  /**
   * Sets the item material used for this potion.
   *
   * @param material the potion item material
   * @return this potion
   * @throws IllegalArgumentException if the material is {@code null}
   */
  public Potion setMaterial(final Material material) {
    if (material == null) {
      throw new IllegalArgumentException("Potion Material cant be null");
    }
    this.material = material;
    return this;
  }

  /**
   * Gets the item material used for this potion.
   *
   * @return the potion item material
   */
  public Material getMaterial() {
    return material;
  }

  /**
   * Sets the category of this potion.
   *
   * @param category the potion effect category
   * @return this potion
   */
  public Potion setCategory(final PotionEffectTypeCategory category) {
    this.category = category;
    return this;
  }

  /**
   * Gets the category of this potion.
   *
   * @return the potion effect category
   */
  public PotionEffectTypeCategory getCategory() {
    return category;
  }

  /**
   * Marks this potion as harmful.
   *
   * @return this potion
   */
  public Potion setHarmful() {
    this.category = PotionEffectTypeCategory.HARMFUL;
    return this;
  }

  /**
   * Marks this potion as neutral.
   *
   * @return this potion
   */
  public Potion setNeutral() {
    this.category = PotionEffectTypeCategory.NEUTRAL;
    return this;
  }

  /**
   * Marks this potion as beneficial.
   *
   * @return this potion
   */
  public Potion setBeneficial() {
    this.category = PotionEffectTypeCategory.BENEFICIAL;
    return this;
  }

  /**
   * Checks whether this potion is harmful.
   *
   * @return {@code true} if the potion category is harmful
   */
  public boolean isHarmful() {
    return this.category.equals(PotionEffectTypeCategory.HARMFUL);
  }

  /**
   * Checks whether this potion is neutral.
   *
   * @return {@code true} if the potion category is neutral
   */
  public boolean isNeutral() {
    return this.category.equals(PotionEffectTypeCategory.NEUTRAL);
  }

  /**
   * Checks whether this potion is beneficial.
   *
   * @return {@code true} if the potion category is beneficial
   */
  public boolean isBeneficial() {
    return this.category.equals(PotionEffectTypeCategory.BENEFICIAL);
  }

  /**
   * Sets the duration scale applied to this potion.
   *
   * @param scale the duration scale
   * @return this potion
   */
  public Potion setDurationScale(final float scale) {
    this.durationScale = scale;
    return this;
  }

  /**
   * Gets the duration scale applied to this potion.
   *
   * @return the duration scale
   */
  public float getDurationScale() {
    return durationScale;
  }

  /**
   * Checks whether this potion can be extended.
   *
   * @return {@code true} if the potion is extendable
   */
  public boolean isExtendable() {
    return isExtendable;
  }

  /**
   * Checks whether this potion can be upgraded.
   *
   * @return {@code true} if the potion is upgradeable
   */
  public boolean isUpgradeable() {
    return isUpgradeable;
  }

  /**
   * Sets whether this potion can be extended.
   *
   * @param value {@code true} if the potion should be extendable
   * @return this potion
   */
  public Potion setExtendable(final boolean value) {
    this.isExtendable = value;
    return this;
  }

  /**
   * Sets whether this potion can be upgraded.
   *
   * @param value {@code true} if the potion should be upgradeable
   * @return this potion
   */
  public Potion setUpgradeable(final boolean value) {
    this.isUpgradeable = value;
    return this;
  }

  /**
   * Replaces the potion's custom effects.
   *
   * @param effects the effects to set
   * @return this potion
   */
  public Potion setEffects(final PotionEffect... effects) {
    if (effects != null && effects.length != 0) {
      this.effects = List.of(effects);
      bukkitEffects = null;
    }
    return this;
  }

  /**
   * Replaces the potion's custom effects.
   *
   * @param effects the effects to set
   * @return this potion
   */
  public Potion setEffects(final List<PotionEffect> effects) {
    if (effects != null && !effects.isEmpty()) {
      this.effects = effects;
      bukkitEffects = null;
    }
    return this;
  }

  /**
   * Adds a custom effect to this potion.
   *
   * @param type      the Bukkit potion effect type
   * @param duration  the effect duration
   * @param unit      the time unit of the duration, or {@code null} to interpret
   *                  as ticks
   * @param amplifier the effect amplifier
   * @return this potion
   */
  public Potion addEffect(final PotionEffectType type, final long duration, final PolyTimeUnit unit,
      final int amplifier) {
    if (duration <= 0 || amplifier <= 0 || type == null) {
      return this;
    }
    effects.add(new PotionEffect(type, unit == null ? duration : unit.toTicks(duration), amplifier));
    bukkitEffects = null;
    return this;
  }

  /**
   * Adds a custom effect to this potion.
   *
   * @param type      the Bukkit potion effect type
   * @param duration  the effect duration in ticks
   * @param amplifier the effect amplifier
   * @return this potion
   */
  public Potion addEffect(final PotionEffectType type, final long duration, final int amplifier) {
    if (duration <= 0 || amplifier <= 0 || type == null) {
      return this;
    }
    effects.add(new PotionEffect(type, duration, amplifier));
    bukkitEffects = null;
    return this;
  }

  /**
   * Adds custom effects to this potion.
   *
   * @param effects the effects to add
   * @return this potion
   */
  public Potion addEffects(final PotionEffect... effects) {
    if (effects != null && effects.length != 0) {
      addEffects(List.of(effects));
    }
    return this;
  }

  /**
   * Adds custom effects to this potion.
   *
   * @param effects the effects to add
   * @return this potion
   */
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

  /**
   * Checks whether this potion contains the given custom effect.
   *
   * @param effect the effect to check
   * @return {@code true} if the effect is present
   */
  public boolean hasEffect(final PotionEffect effect) {
    return effects.contains(effect);
  }

  /**
   * Removes custom effects from this potion.
   *
   * @param effects the effects to remove
   * @return this potion
   */
  public Potion removeEffects(final PotionEffect... effects) {
    if (effects == null || effects.length == 0 || this.effects == null || this.effects.isEmpty()) {
      return this;
    }
    return removeEffects(List.of(effects));
  }

  /**
   * Removes custom effects from this potion.
   *
   * @param effects the effects to remove
   * @return this potion
   */
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

  /**
   * Removes all custom effects from this potion.
   *
   * @return this potion
   */
  public Potion clearEffects() {
    effects.clear();
    bukkitEffects = null;
    return this;
  }

  /**
   * Gets the custom effects configured for this potion.
   *
   * @return the custom potion effects
   */
  public List<PotionEffect> getEffects() {
    return effects;
  }

  /**
   * Gets Bukkit potion effects equivalent to the configured custom effects.
   *
   * @return the Bukkit potion effects, or {@code null} if no effects are
   *         configured
   */
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

  /**
   * Applies this potion's effects to a player.
   *
   * @param p the player to apply the potion to
   * @return this potion
   */
  public Potion playerApplyPotion(final Player p) {
    if (p != null) {
      p.addPotionEffects(getBukkitEffects());
    }
    return this;
  }

  /**
   * Removes this potion's effects from a player.
   *
   * @param p the player to remove the potion from
   * @return this potion
   */
  public Potion playerRemovePotion(final Player p) {
    if (p == null) {
      return this;
    }
    for (final org.bukkit.potion.PotionEffect potionEffect : getBukkitEffects()) {
      p.removePotionEffect(potionEffect.getType());
    }
    return this;
  }

  /**
   * Checks whether a player currently has all effects from this potion.
   *
   * @param p the player to inspect
   * @return {@code true} if the player has all of this potion's effects
   */
  public boolean playerHasPotion(final Player p) {
    if (p == null) {
      return false;
    }
    for (final org.bukkit.potion.PotionEffect potionEffect : getBukkitEffects()) {
      if (!p.hasPotionEffect(potionEffect.getType())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Creates an item stack representing this potion using its configured material.
   *
   * @return the created potion item
   */
  public ItemStack getItem() {
    return getItem(new ItemStack(this.material));
  }

  /**
   * Creates an item stack representing this potion using the given material.
   *
   * @param material the material to use for the item stack
   * @return the created potion item
   */
  public ItemStack getItem(final Material material) {
    return getItem(new ItemStack(material));
  }

  /**
   * Applies this potion's data to the given potion item stack.
   *
   * @param item the potion item stack to update
   * @return the updated potion item
   * @throws IllegalArgumentException if the item is not a potion-like item
   */
  public ItemStack getItem(final ItemStack item) {
    if (!PotionAPI.isPotionItem(item)) {
      throw new IllegalArgumentException("Invalid Potion Item");
    }
    final PotionMeta meta = (PotionMeta) item.getItemMeta();
    apply(meta);
    item.setItemMeta(meta);
    return item;
  }

  /**
   * Applies this potion's data to potion item metadata.
   */
  public PotionMeta apply(final PotionMeta meta) {
    if (meta == null) {
      return null;
    }
    if (key != null) {
      meta.setItemName(key.toString());
    }
    if (color != null) {
      meta.setColor(color);
    }
    meta.setDurationScale(durationScale);
    if (effects != null) {
      for (final PotionEffect effect : effects) {
        if (effect != null) {
          meta.addCustomEffect(effect.toBukkit(), false);
        }
      }
    }
    return meta;
  }

  /**
   * Applies this potion definition to the given builder.
   */
  @Override
  public void apply(final ItemStackBuilder<?> builder) {
    if (builder == null) {
      return;
    }
    if (material != null) {
      builder.type(material);
    }
    builder.meta(PotionMeta.class, this::apply);
  }

  /**
   * Returns this potion as a reusable item modifier.
   */
  public ItemModifier asModifier() {
    return this::apply;
  }

  /**
   * Creates a copy of this potion.
   *
   * @return a cloned potion
   */
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
