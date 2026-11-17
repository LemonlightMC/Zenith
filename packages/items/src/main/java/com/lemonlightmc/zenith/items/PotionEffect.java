package com.lemonlightmc.zenith.items;

import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;
import com.lemonlightmc.zenith.additive.time.PolyTimeUnit;

/**
 * Mutable representation of a potion effect with serialization and Bukkit conversion support.
 */
@SerializableAs("PotionEffect")
public class PotionEffect implements ConfigurationSerializable, Cloneable<PotionEffect> {
  /** Duration value representing an infinite potion effect. */
  public static final long INFINITE_DURATION = -1;

  private static final String AMPLIFIER = "amplifier";
  private static final String DURATION = "duration";
  private static final String TYPE = "effect";
  private static final String AMBIENT = "ambient";
  private static final String PARTICLES = "has-particles";
  private static final String ICON = "has-icon";
  private int amplifier;
  private long duration;
  private PotionEffectType type;
  private boolean ambient;
  private boolean particles;
  private boolean icon;

  /**
   * Creates a potion effect with the given properties.
   *
   * @param type the Bukkit potion effect type
   * @param duration the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   * @param amplifier the amplifier value
   * @param ambient whether the effect should be ambient
   * @param particles whether particles should be shown
   * @param icon whether the effect icon should be shown
   * @throws IllegalArgumentException if the type is {@code null}, the duration is invalid, or the amplifier is out of range
   */
  public PotionEffect(final PotionEffectType type, final long duration, final int amplifier, final boolean ambient,
      final boolean particles,
      final boolean icon) {
    if (type == null) {
      throw new IllegalArgumentException("Potion Effect Type cant be null");
    }
    if (duration < -1) {
      throw new IllegalArgumentException("Potion Duration must be positive or infinite");
    }
    if (amplifier < 0 || amplifier > 255) {
      throw new IllegalArgumentException("Potion Amplifier must be between 0 and 255");
    }
    this.type = type;
    this.duration = duration;
    this.amplifier = amplifier;
    this.ambient = ambient;
    this.particles = particles;
    this.icon = icon;
  }

  /**
   * Creates a potion effect with the icon enabled by default.
   *
   * @param type the Bukkit potion effect type
   * @param duration the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   * @param amplifier the amplifier value
   * @param ambient whether the effect should be ambient
   * @param particles whether particles should be shown
   */
  public PotionEffect(final PotionEffectType type, final long duration, final int amplifier, final boolean ambient,
      final boolean particles) {
    this(type, duration, amplifier, ambient, particles, true);
  }

  /**
   * Creates a potion effect with particles and icon enabled by default.
   *
   * @param type the Bukkit potion effect type
   * @param duration the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   * @param amplifier the amplifier value
   * @param ambient whether the effect should be ambient
   */
  public PotionEffect(final PotionEffectType type, final long duration, final int amplifier, final boolean ambient) {
    this(type, duration, amplifier, ambient, true, true);
  }

  /**
   * Creates a potion effect with default visual settings.
   *
   * @param type the Bukkit potion effect type
   * @param duration the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   * @param amplifier the amplifier value
   */
  public PotionEffect(final PotionEffectType type, final long duration, final int amplifier) {
    this(type, duration, amplifier, true, true, true);
  }

  /**
   * Deserializes a potion effect from a configuration map.
   *
   * @param map the serialized potion effect data
   * @return the deserialized potion effect
   */
  public static PotionEffect deserialize(final Map<String, Object> map) {
    return new PotionEffect(getEffectType(map), getInt(map, DURATION, 1), getInt(map, AMPLIFIER, 1),
        getBool(map, AMBIENT, false), getBool(map, PARTICLES, false), getBool(map, ICON, false));
  }

  /**
   * Creates a potion effect with the given properties.
   *
   * @param type the Bukkit potion effect type
   * @param duration the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   * @param amplifier the amplifier value
   * @param ambient whether the effect should be ambient
   * @param particles whether particles should be shown
   * @param icon whether the effect icon should be shown
   * @return a new potion effect
   */
  public static PotionEffect from(final PotionEffectType type, final long duration, final int amplifier,
      final boolean ambient,
      final boolean particles, final boolean icon) {
    return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
  }

  /**
   * Creates a potion effect with default visual settings.
   *
   * @param type the Bukkit potion effect type
   * @param duration the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   * @param amplifier the amplifier value
   * @return a new potion effect
   */
  public static PotionEffect from(final PotionEffectType type, final long duration, final int amplifier) {
    return new PotionEffect(type, duration, amplifier);
  }

  /**
   * Sets the Bukkit potion effect type.
   *
   * @param type the potion effect type
   * @return this potion effect
   * @throws IllegalArgumentException if the type is {@code null}
   */
  public PotionEffect setType(final PotionEffectType type) {
    if (type == null) {
      throw new IllegalArgumentException("Potion Effect Type cant be null");
    }
    this.type = type;
    return this;
  }

  /**
   * Gets the Bukkit potion effect type.
   *
   * @return the potion effect type
   */
  public PotionEffectType getType() {
    return type;
  }

  /**
   * Sets the amplifier for this effect.
   *
   * @param amplifier the amplifier value
   * @return this potion effect
   * @throws IllegalArgumentException if the amplifier is out of range
   */
  public PotionEffect setAmplifier(final int amplifier) {
    if (amplifier < 0 || amplifier > 255) {
      throw new IllegalArgumentException("Potion Amplifier must be between 0 and 255");
    }
    this.amplifier = amplifier;
    return this;
  }

  /**
   * Gets the amplifier for this effect.
   *
   * @return the amplifier value
   */
  public int getAmplifier() {
    return amplifier;
  }

  /**
   * Sets the duration of this effect in ticks.
   *
   * @param duration the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   * @return this potion effect
   * @throws IllegalArgumentException if the duration is invalid
   */
  public PotionEffect setDuration(final long duration) {
    if (duration < -1) {
      throw new IllegalArgumentException("Potion Duration must be positive or infinite");
    }
    this.duration = duration;
    return this;
  }

  /**
   * Sets the duration of this effect using the given time unit.
   *
   * @param duration the duration value
   * @param unit the time unit of the duration, or {@code null} to interpret as ticks
   * @return this potion effect
   * @throws IllegalArgumentException if the duration is invalid
   */
  public PotionEffect setDuration(final long duration, final PolyTimeUnit unit) {
    if (duration < -1) {
      throw new IllegalArgumentException("Potion Duration must be positive or infinite");
    }
    this.duration = duration == INFINITE_DURATION ? INFINITE_DURATION
        : unit == null ? duration : unit.toTicks(duration);
    return this;
  }

  /**
   * Marks this effect as infinite.
   *
   * @return this potion effect
   */
  public PotionEffect setDurationInfinite() {
    this.duration = INFINITE_DURATION;
    return this;
  }

  /**
   * Gets the duration of this effect in ticks.
   *
   * @return the duration in ticks, or {@link #INFINITE_DURATION} for infinite
   */
  public long getDuration() {
    return duration;
  }

  /**
   * Sets whether this effect is ambient.
   *
   * @param ambient {@code true} to mark the effect as ambient
   * @return this potion effect
   */
  public PotionEffect setAmbient(final boolean ambient) {
    this.ambient = ambient;
    return this;
  }

  /**
   * Checks whether this effect is ambient.
   *
   * @return {@code true} if the effect is ambient
   */
  public boolean isAmbient() {
    return ambient;
  }

  /**
   * Sets whether particles should be shown for this effect.
   *
   * @param particles {@code true} to show particles
   * @return this potion effect
   */
  public PotionEffect setParticles(final boolean particles) {
    this.particles = particles;
    return this;
  }

  /**
   * Checks whether particles are shown for this effect.
   *
   * @return {@code true} if particles are shown
   */
  public boolean hasParticles() {
    return particles;
  }

  /**
   * Sets whether the effect icon should be shown.
   *
   * @param icon {@code true} to show the icon
   * @return this potion effect
   */
  public PotionEffect setIcon(final boolean icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Checks whether the effect icon is shown.
   *
   * @return {@code true} if the icon is shown
   */
  public boolean hasIcon() {
    return icon;
  }

  /**
   * Checks whether this effect is infinite.
   *
   * @return {@code true} if the duration is infinite
   */
  public boolean isInfinite() {
    return duration == INFINITE_DURATION;
  }

  /**
   * Checks whether this effect has a higher amplifier than another effect.
   *
   * @param other the effect to compare against
   * @return {@code true} if this effect is stronger
   */
  public boolean isStrongerThan(final PotionEffect other) {
    return amplifier > other.amplifier;
  }

  /**
   * Checks whether this effect has a lower amplifier than another effect.
   *
   * @param other the effect to compare against
   * @return {@code true} if this effect is weaker
   */
  public boolean isWeakerThan(final PotionEffect other) {
    return amplifier < other.amplifier;
  }

  /**
   * Checks whether this effect lasts longer than another effect.
   *
   * @param other the effect to compare against
   * @return {@code true} if this effect is longer
   */
  public boolean isLongerThan(final PotionEffect other) {
    return !other.isInfinite() && (duration > other.duration || isInfinite());
  }

  /**
   * Checks whether this effect lasts shorter than another effect.
   *
   * @param other the effect to compare against
   * @return {@code true} if this effect is shorter
   */
  public boolean isShorterThan(final PotionEffect other) {
    return !isInfinite() && (duration < other.duration || other.isInfinite());
  }

  /**
   * Applies this effect to the given living entity.
   *
   * @param entity the entity to receive the effect
   * @return {@code true} if the effect was successfully applied
   */
  public boolean apply(final LivingEntity entity) {
    return entity.addPotionEffect(toBukkit());
  }

  /**
   * Converts this effect into a Bukkit potion effect.
   *
   * @return the Bukkit potion effect representation
   */
  public org.bukkit.potion.PotionEffect toBukkit() {
    return new org.bukkit.potion.PotionEffect(type, (int) duration, amplifier, ambient, particles, icon);
  }

  @SuppressWarnings("deprecation")
  private static PotionEffectType getEffectType(final Map<?, ?> map) {
    final Object obj = map.get(TYPE);
    if (obj instanceof final String value) {
      final NamespacedKey key = NamespacedKey.fromString(value);
      return key == null ? null : Registry.EFFECT.get(key);
    } else if (obj instanceof final Integer integer) {
      return integer != null && integer > 0 ? PotionEffectType.getById(integer) : null;
    }
    return null;
  }

  private static int getInt(final Map<?, ?> map, final Object key, final int def) {
    final Object num = map.get(key);
    return num instanceof Integer ? (Integer) num : def;
  }

  private static boolean getBool(final Map<?, ?> map, final Object key, final boolean def) {
    final Object bool = map.get(key);
    return bool instanceof Boolean ? (Boolean) bool : def;
  }

  /**
   * Serializes this potion effect into a configuration map.
   *
   * @return a serialized representation of this potion effect
   */
  @Override
  public Map<String, Object> serialize() {
    return Map.of(
        TYPE, type.getKeyOrThrow().toString(),
        DURATION, duration,
        AMPLIFIER, amplifier,
        AMBIENT, ambient,
        PARTICLES, particles,
        ICON, icon);
  }

  /**
   * Creates a copy of this potion effect.
   *
   * @return a cloned potion effect
   */
  @Override
  public PotionEffect clone() {
    return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PotionEffect)) {
      return false;
    }
    final PotionEffect that = (PotionEffect) obj;
    return this.type.equals(that.type) && this.ambient == that.ambient && this.amplifier == that.amplifier
        && this.duration == that.duration && this.particles == that.particles && this.icon == that.icon;
  }

  @Override
  public int hashCode() {
    int hash = 31 + type.hashCode();
    hash = hash * 31 + amplifier;
    hash = hash * 31 + Long.hashCode(duration);
    hash ^= 0x22222222 >> (ambient ? 1 : -1);
    hash ^= 0x22222222 >> (particles ? 1 : -1);
    hash ^= 0x22222222 >> (icon ? 1 : -1);
    return hash;
  }

  @Override
  public String toString() {
    return type.getKeyOrNull() + (ambient ? ":(" : ":") + duration + "t-x" + amplifier + (ambient ? ")" : "");
  }
}
