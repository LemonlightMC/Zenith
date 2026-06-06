package com.lemonlightmc.zenith.items;

import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.lemonlightmc.zenith.interfaces.Cloneable;
import com.lemonlightmc.zenith.time.PolyTimeUnit;

@SerializableAs("PotionEffect")
public class PotionEffect implements ConfigurationSerializable, Cloneable<PotionEffect> {
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

  public PotionEffect(final PotionEffectType type, final long duration, final int amplifier, final boolean ambient,
      final boolean particles) {
    this(type, duration, amplifier, ambient, particles, true);
  }

  public PotionEffect(final PotionEffectType type, final long duration, final int amplifier, final boolean ambient) {
    this(type, duration, amplifier, ambient, true, true);
  }

  public PotionEffect(final PotionEffectType type, final long duration, final int amplifier) {
    this(type, duration, amplifier, true, true, true);
  }

  public static PotionEffect deserialize(final Map<String, Object> map) {
    return new PotionEffect(getEffectType(map), getInt(map, DURATION, 1), getInt(map, AMPLIFIER, 1),
        getBool(map, AMBIENT, false), getBool(map, PARTICLES, false), getBool(map, ICON, false));
  }

  public static PotionEffect from(final PotionEffectType type, final long duration, final int amplifier,
      final boolean ambient,
      final boolean particles, final boolean icon) {
    return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
  }

  public static PotionEffect from(final PotionEffectType type, final long duration, final int amplifier) {
    return new PotionEffect(type, duration, amplifier);
  }

  public PotionEffect setType(final PotionEffectType type) {
    if (type == null) {
      throw new IllegalArgumentException("Potion Effect Type cant be null");
    }
    this.type = type;
    return this;
  }

  public PotionEffectType getType() {
    return type;
  }

  public PotionEffect setAmplifier(final int amplifier) {
    if (amplifier < 0 || amplifier > 255) {
      throw new IllegalArgumentException("Potion Amplifier must be between 0 and 255");
    }
    this.amplifier = amplifier;
    return this;
  }

  public int getAmplifier() {
    return amplifier;
  }

  public PotionEffect setDuration(final long duration) {
    if (duration < -1) {
      throw new IllegalArgumentException("Potion Duration must be positive or infinite");
    }
    this.duration = duration;
    return this;
  }

  public PotionEffect setDuration(final long duration, final PolyTimeUnit unit) {
    if (duration < -1) {
      throw new IllegalArgumentException("Potion Duration must be positive or infinite");
    }
    this.duration = duration == INFINITE_DURATION ? INFINITE_DURATION
        : unit == null ? duration : unit.toTicks(duration);
    return this;
  }

  public PotionEffect setDurationInfinite() {
    this.duration = INFINITE_DURATION;
    return this;
  }

  public long getDuration() {
    return duration;
  }

  public PotionEffect setAmbient(final boolean ambient) {
    this.ambient = ambient;
    return this;
  }

  public boolean isAmbient() {
    return ambient;
  }

  public PotionEffect setParticles(final boolean particles) {
    this.particles = particles;
    return this;
  }

  public boolean hasParticles() {
    return particles;
  }

  public PotionEffect setIcon(final boolean icon) {
    this.icon = icon;
    return this;
  }

  public boolean hasIcon() {
    return icon;
  }

  public boolean isInfinite() {
    return duration == INFINITE_DURATION;
  }

  public boolean isStrongerThan(final PotionEffect other) {
    return amplifier > other.amplifier;
  }

  public boolean isWeakerThan(final PotionEffect other) {
    return amplifier < other.amplifier;
  }

  public boolean isLongerThan(final PotionEffect other) {
    return !other.isInfinite() && (duration > other.duration || isInfinite());
  }

  public boolean isShorterThan(final PotionEffect other) {
    return !isInfinite() && (duration < other.duration || other.isInfinite());
  }

  public boolean apply(final LivingEntity entity) {
    return entity.addPotionEffect(toBukkit());
  }

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
