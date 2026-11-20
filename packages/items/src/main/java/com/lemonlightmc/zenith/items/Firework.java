package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.FireworkMeta;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;

/**
 * Mutable representation of a firework and its configured effects.
 */
public class Firework implements Cloneable<Firework>, ConfigurationSerializable, ItemModifier {
  private List<FireworkEffect> effects;
  private int power;

  /**
   * Creates a firework with the given effects and power.
   *
   * @param effects the firework effects to include
   * @param power   the firework flight power
   */
  public Firework(final List<FireworkEffect> effects, final int power) {
    this.effects = effects == null ? new ArrayList<>() : effects;
    this.power = power < 0 ? 1 : power;
  }

  /**
   * Creates a firework with the given effects and a default power of 1.
   *
   * @param effects the firework effects to include
   */
  public Firework(final List<FireworkEffect> effects) {
    this(effects, 1);
  }

  /**
   * Creates a copy of another firework.
   *
   * @param firework the firework to copy
   */
  public Firework(final Firework firework) {
    if (firework == null) {
      this.effects = new ArrayList<>();
      this.power = 1;
      return;
    }
    this.effects = firework.effects == null ? new ArrayList<>() : firework.effects;
    this.power = firework.power < 0 ? 1 : power;
  }

  /**
   * Spawns a firework entity at the given location.
   *
   * @param loc the location at which to spawn the firework
   * @return the spawned firework entity, or {@code null} if the location is
   *         {@code null}
   */
  public org.bukkit.entity.Firework spawnEntity(final Location loc) {
    if (loc == null) {
      return null;
    }
    final org.bukkit.entity.Firework entity = (org.bukkit.entity.Firework) loc.getWorld().spawnEntity(loc,
        EntityType.FIREWORK_ROCKET);
    final FireworkMeta meta = entity.getFireworkMeta();
    for (final FireworkEffect fireworkEffect : effects) {
      meta.addEffects(fireworkEffect.toBukkit());
    }
    meta.setPower(power);
    return entity;
  }

  /**
   * Creates, but does not spawn, a firework entity at the given location.
   *
   * @param loc the location for the created firework entity
   * @return the created firework entity, or {@code null} if the location is
   *         {@code null}
   */
  public org.bukkit.entity.Firework toEntity(final Location loc) {
    if (loc == null) {
      return null;
    }
    final org.bukkit.entity.Firework entity = (org.bukkit.entity.Firework) loc.getWorld().createEntity(loc,
        org.bukkit.entity.Firework.class);
    final FireworkMeta meta = entity.getFireworkMeta();
    for (final FireworkEffect fireworkEffect : effects) {
      meta.addEffects(fireworkEffect.toBukkit());
    }
    meta.setPower(power);
    return entity;
  }

  /**
   * Gets the configured firework effects.
   *
   * @return the firework effects
   */
  public List<FireworkEffect> getEffects() {
    return effects;
  }

  /**
   * Checks whether this firework has any effects.
   *
   * @return {@code true} if at least one effect is configured
   */
  public boolean hasEffects() {
    return effects != null && !effects.isEmpty();
  }

  /**
   * Removes all configured firework effects.
   *
   * @return this firework
   */
  public Firework clearEffects() {
    if (effects != null) {
      effects.clear();
    }
    return this;
  }

  /**
   * Removes the specified firework effects.
   *
   * @param effects the effects to remove
   * @return this firework
   */
  public Firework removeEffects(final FireworkEffect... effects) {
    if (effects == null || effects.length == 0 || this.effects == null || this.effects.isEmpty()) {
      return this;
    }
    return removeEffects(List.of(effects));
  }

  /**
   * Removes the specified firework effects.
   *
   * @param effects the effects to remove
   * @return this firework
   */
  public Firework removeEffects(final List<FireworkEffect> effects) {
    if (effects == null || effects.isEmpty() || this.effects == null || this.effects.isEmpty()) {
      return this;
    }
    for (final FireworkEffect fireworkEffect : effects) {
      if (fireworkEffect != null) {
        this.effects.remove(fireworkEffect);
      }
    }
    return this;
  }

  /**
   * Adds the specified firework effects.
   *
   * @param effects the effects to add
   * @return this firework
   */
  public Firework addEffects(final FireworkEffect... effects) {
    if (effects == null || effects.length == 0) {
      return this;
    }
    return addEffects(List.of(effects));
  }

  /**
   * Adds the specified firework effects.
   *
   * @param effects the effects to add
   * @return this firework
   */
  public Firework addEffects(final List<FireworkEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return this;
    }
    for (final FireworkEffect fireworkEffect : effects) {
      if (fireworkEffect != null) {
        this.effects.add(fireworkEffect);
      }
    }
    return this;
  }

  /**
   * Replaces the configured firework effects.
   *
   * @param effects the effects to set
   * @return this firework
   */
  public Firework setEffects(final FireworkEffect... effects) {
    if (effects != null && effects.length != 0) {
      this.effects = List.of(effects);
    }
    return this;
  }

  /**
   * Replaces the configured firework effects.
   *
   * @param effects the effects to set
   * @return this firework
   */
  public Firework setEffects(final List<FireworkEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return this;
    }
    this.effects = effects;
    return this;
  }

  /**
   * Gets the number of configured effects.
   *
   * @return the configured effect count
   */
  public int effectCount() {
    return this.effects.size();
  }

  /**
   * Gets the firework flight power.
   *
   * @return the firework power
   */
  public int getPower() {
    return this.power;
  }

  /**
   * Sets the firework flight power.
   *
   * @param power the firework power
   * @return this firework
   */
  public Firework setPower(final int power) {
    this.power = power;
    return this;
  }

  /**
   * Applies this firework's effects and power to rocket item metadata.
   */
  public FireworkMeta apply(final FireworkMeta meta) {
    if (meta == null) {
      return null;
    }
    meta.clearEffects();
    if (effects != null) {
      for (final FireworkEffect fireworkEffect : effects) {
        if (fireworkEffect == null) {
          continue;
        }
        final org.bukkit.FireworkEffect bukkit = fireworkEffect.toBukkit();
        if (bukkit != null) {
          meta.addEffect(bukkit);
        }
      }
    }
    meta.setPower(power);
    return meta;
  }

  /**
   * Applies this firework configuration to the given builder (rocket meta).
   */
  @Override
  public void apply(final ItemStackBuilder<?> builder) {
    if (builder == null) {
      return;
    }
    builder.meta(FireworkMeta.class, this::apply);
  }

  /**
   * Returns this firework as a reusable item modifier.
   */
  public ItemModifier asModifier() {
    return this::apply;
  }

  /**
   * Deserializes a firework from a configuration map.
   *
   * @param map the serialized firework data
   * @return the deserialized firework
   */
  public static Firework deserialize(final Map<String, Object> map) {
    return new Firework(deserializeEffectList(map.get("effects")), (int) map.get("power"));
  }

  @SuppressWarnings("unchecked")
  private static List<FireworkEffect> deserializeEffectList(final Object values) {
    if (values == null || !(values instanceof final List list)) {
      return List.of();
    }
    final List<FireworkEffect> effects = new ArrayList<>();
    for (final Object value : list) {
      if (value instanceof Map) {
        effects.add(FireworkEffect.deserialize((Map<String, Object>) value));
      }
    }
    return effects;
  }

  /**
   * Serializes this firework into a configuration map.
   *
   * @return a serialized representation of this firework
   */
  @Override
  public Map<String, Object> serialize() {
    return Map.<String, Object>of("effects", effects, "power", power);
  }

  /**
   * Creates a copy of this firework.
   *
   * @return a cloned firework
   */
  @Override
  public Firework clone() {
    return new Firework(this);
  }

  @Override
  public int hashCode() {
    return 31 * (31 + effects.hashCode()) + power;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Firework other = (Firework) obj;
    if (effects == null && other.effects != null) {
      return false;
    }
    return power == other.power && effects.equals(other.effects);
  }

  @Override
  public String toString() {
    return "Firework [effects=" + effects + ", power=" + power + "]";
  }
}
