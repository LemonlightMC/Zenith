package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.google.common.collect.ImmutableList;
import com.lemonlightmc.zenith.interfaces.Cloneable;

// TODO: Rework FireworkEffect for 1.1
public class FireworkEffect
    implements ConfigurationSerializable, Cloneable<FireworkEffect> {
  public enum FireworkEffectType {
    BALL(org.bukkit.FireworkEffect.Type.BALL),
    BALL_LARGE(org.bukkit.FireworkEffect.Type.BALL_LARGE),
    STAR(org.bukkit.FireworkEffect.Type.STAR),
    BURST(org.bukkit.FireworkEffect.Type.BURST),
    CREEPER(org.bukkit.FireworkEffect.Type.CREEPER);

    org.bukkit.FireworkEffect.Type bukkitType;

    private FireworkEffectType(final org.bukkit.FireworkEffect.Type bukkitType) {
      this.bukkitType = bukkitType;
    }

    public org.bukkit.FireworkEffect.Type toBukkit() {
      return bukkitType;
    }
  }

  private static final String FLICKER = "flicker";
  private static final String TRAIL = "trail";
  private static final String COLORS = "colors";
  private static final String FADE_COLORS = "fade-colors";
  private static final String TYPE = "type";

  private boolean flicker;
  private boolean trail;
  private List<Color> colors;
  private List<Color> fadeColors;
  private FireworkEffectType type;

  public FireworkEffect(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors,
      final List<Color> fadeColors) {
    this.flicker = flicker;
    this.trail = trail;
    this.colors = colors == null ? new ArrayList<>() : colors;
    this.fadeColors = fadeColors == null ? new ArrayList<>() : fadeColors;
    this.type = type == null ? FireworkEffectType.BALL : type;
  }

  public FireworkEffect(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors) {
    this(type, flicker, trail, colors, null);
  }

  public FireworkEffect(final FireworkEffectType type, final List<Color> colors) {
    this(type, false, true, colors, null);
  }

  public FireworkEffect(final FireworkEffect effect) {
    if (effect == null) {
      return;
    }
    this.flicker = effect.flicker;
    this.trail = effect.trail;
    this.colors = effect.colors == null ? new ArrayList<>() : effect.colors;
    this.fadeColors = effect.fadeColors == null ? new ArrayList<>() : effect.fadeColors;
    this.type = effect.type == null ? FireworkEffectType.BALL : effect.type;
  }

  public static FireworkEffect from(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors,
      final List<Color> fadeColors) {
    return new FireworkEffect(type, flicker, trail, colors, fadeColors);
  }

  public static FireworkEffect from(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors) {
    return new FireworkEffect(type, flicker, trail, colors);
  }

  public static FireworkEffect from(final FireworkEffectType type, final List<Color> colors) {
    return new FireworkEffect(type, colors);
  }

  public boolean hasFlicker() {
    return flicker;
  }

  public FireworkEffect setFlicker(final boolean flicker) {
    this.flicker = flicker;
    return this;
  }

  public boolean hasTrail() {
    return trail;
  }

  public FireworkEffect setTrail(final boolean trail) {
    this.trail = trail;
    return this;
  }

  public boolean hasColors() {
    return colors != null && !colors.isEmpty();
  }

  public List<Color> getColors() {
    return colors;
  }

  public FireworkEffect setColors(final Color... colors) {
    if (colors != null) {
      this.colors = List.of(colors);
    }
    return this;
  }

  public FireworkEffect setColors(final Collection<Color> colors) {
    if (colors != null) {
      this.colors = List.copyOf(colors);
    }
    return this;
  }

  public FireworkEffect addColors(final Color... colors) {
    if (colors == null || colors.length == 0) {
      return this;
    }
    for (final Color color : colors) {
      if (color != null) {
        this.colors.add(color);
      }
    }
    return this;
  }

  public FireworkEffect addColors(final Collection<Color> colors) {
    if (colors == null || colors.isEmpty()) {
      return this;
    }
    for (final Color color : colors) {
      if (color != null) {
        this.colors.add(color);
      }
    }
    return this;
  }

  public List<Color> getFadeColors() {
    return fadeColors;
  }

  public boolean hasFadeColors() {
    return fadeColors != null && !fadeColors.isEmpty();
  }

  public FireworkEffect setFadeColors(final Color... colors) {
    if (colors != null && colors.length != 0) {
      this.fadeColors = List.of(colors);
    }
    return this;
  }

  public FireworkEffect setFadeColors(final Collection<Color> fadeColors) {
    if (colors != null && !colors.isEmpty()) {
      this.fadeColors = List.copyOf(fadeColors);
    }
    return this;
  }

  public FireworkEffect addFadeColors(final Color... colors) {
    if (colors == null || colors.length == 0) {
      return this;
    }
    for (final Color color : colors) {
      if (color != null) {
        this.fadeColors.add(color);
      }
    }
    return this;
  }

  public FireworkEffect addFadeColors(final Collection<Color> colors) {
    if (colors == null || colors.isEmpty()) {
      return this;
    }
    for (final Color color : colors) {
      if (color != null) {
        this.fadeColors.add(color);
      }
    }
    return this;
  }

  public FireworkEffectType getType() {
    return type;
  }

  public FireworkEffect setType(final FireworkEffectType type) {
    if (type == null) {
      throw new IllegalArgumentException("Type cant be null");
    }
    this.type = type;
    return this;
  }

  public static FireworkEffect deserialize(final Map<String, Object> map) {
    return new FireworkEffect(FireworkEffectType.valueOf((String) map.get(TYPE)), (boolean) map.get(FLICKER),
        (boolean) map.get(TRAIL), deserializeColorList(map.get(COLORS)), deserializeColorList(map.get(FADE_COLORS)));
  }

  private static List<Color> deserializeColorList(final Object values) throws IllegalArgumentException {
    if (values == null || !(values instanceof final Iterable<?> iterable)) {
      return null;
    }
    final List<Color> colors = new ArrayList<>();
    for (final Object obj : iterable) {
      if (obj != null && obj instanceof final Color color) {
        colors.add(color);
      }
    }
    return colors;
  }

  @Override
  public Map<String, Object> serialize() {
    return Map.<String, Object>of(TYPE, type.name(),
        FLICKER, flicker,
        TRAIL, trail,
        COLORS, colors,
        FADE_COLORS, fadeColors);
  }

  @Override
  public FireworkEffect clone() {
    return new FireworkEffect(this);
  }

  public org.bukkit.FireworkEffect toBukkit() {
    if (colors == null || colors.isEmpty()) {
      throw new IllegalStateException("Cannot make FireworkEffect without any color");
    }
    try {
      return org.bukkit.FireworkEffect.class.getConstructor().newInstance(flicker, trail,
          ImmutableList.copyOf(colors),
          ImmutableList.copyOf(fadeColors == null ? List.of() : fadeColors),
          type.toBukkit());
    } catch (final Exception e) {
      return null;
    }
  }

  @Override
  public int hashCode() {
    int result = 31 + (flicker ? 1231 : 1237);
    result = 31 * result + (trail ? 1231 : 1237);
    result = 31 * result + ((colors == null) ? 0 : colors.hashCode());
    result = 31 * result + ((fadeColors == null) ? 0 : fadeColors.hashCode());
    return 31 * result + ((type == null) ? 0 : type.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final FireworkEffect other = (FireworkEffect) obj;
    if (colors == null && other.colors != null || fadeColors == null && other.fadeColors != null) {
      return false;
    }
    return this.flicker == other.flicker
        && this.trail == other.trail
        && this.type == other.type
        && this.colors.equals(other.colors)
        && this.fadeColors.equals(other.fadeColors);
  }

  @Override
  public String toString() {
    return "FireworkEffect [flicker=" + flicker + ", trail=" + trail + ", colors=" + colors + ", fadeColors="
        + fadeColors + ", type=" + type + "]";
  }
}
