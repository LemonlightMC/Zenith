package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;
import com.lemonlightmc.zenith.itembuilder.ItemModifier;
import com.lemonlightmc.zenith.itembuilder.ItemStackBuilder;

// TODO: Rework FireworkEffect for 1.1
/**
 * Mutable representation of a firework effect and its visual properties.
 */
public class FireworkEffect
    implements ConfigurationSerializable, Cloneable<FireworkEffect>, ItemModifier {
  /**
   * Supported firework effect shape types.
   */
  public enum FireworkEffectType {
    /** A small ball explosion. */
    BALL(org.bukkit.FireworkEffect.Type.BALL),
    /** A large ball explosion. */
    BALL_LARGE(org.bukkit.FireworkEffect.Type.BALL_LARGE),
    /** A star-shaped explosion. */
    STAR(org.bukkit.FireworkEffect.Type.STAR),
    /** A burst explosion. */
    BURST(org.bukkit.FireworkEffect.Type.BURST),
    /** A creeper-shaped explosion. */
    CREEPER(org.bukkit.FireworkEffect.Type.CREEPER);

    org.bukkit.FireworkEffect.Type bukkitType;

    private FireworkEffectType(final org.bukkit.FireworkEffect.Type bukkitType) {
      this.bukkitType = bukkitType;
    }

    /**
     * Converts this type to the corresponding Bukkit firework effect type.
     *
     * @return the Bukkit firework effect type
     */
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

  /**
   * Creates a firework effect with the given properties.
   *
   * @param type the explosion shape type
   * @param flicker whether the effect should flicker
   * @param trail whether the effect should leave a trail
   * @param colors the primary colors of the effect
   * @param fadeColors the fade colors of the effect
   */
  public FireworkEffect(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors,
      final List<Color> fadeColors) {
    this.flicker = flicker;
    this.trail = trail;
    this.colors = colors == null ? new ArrayList<>() : colors;
    this.fadeColors = fadeColors == null ? new ArrayList<>() : fadeColors;
    this.type = type == null ? FireworkEffectType.BALL : type;
  }

  /**
   * Creates a firework effect without fade colors.
   *
   * @param type the explosion shape type
   * @param flicker whether the effect should flicker
   * @param trail whether the effect should leave a trail
   * @param colors the primary colors of the effect
   */
  public FireworkEffect(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors) {
    this(type, flicker, trail, colors, null);
  }

  /**
   * Creates a firework effect with default flicker and trail settings.
   *
   * @param type the explosion shape type
   * @param colors the primary colors of the effect
   */
  public FireworkEffect(final FireworkEffectType type, final List<Color> colors) {
    this(type, false, true, colors, null);
  }

  /**
   * Creates a copy of another firework effect.
   *
   * @param effect the firework effect to copy
   */
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

  /**
   * Creates a firework effect with the given properties.
   *
   * @param type the explosion shape type
   * @param flicker whether the effect should flicker
   * @param trail whether the effect should leave a trail
   * @param colors the primary colors of the effect
   * @param fadeColors the fade colors of the effect
   * @return a new firework effect
   */
  public static FireworkEffect from(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors,
      final List<Color> fadeColors) {
    return new FireworkEffect(type, flicker, trail, colors, fadeColors);
  }

  /**
   * Creates a firework effect without fade colors.
   *
   * @param type the explosion shape type
   * @param flicker whether the effect should flicker
   * @param trail whether the effect should leave a trail
   * @param colors the primary colors of the effect
   * @return a new firework effect
   */
  public static FireworkEffect from(final FireworkEffectType type, final boolean flicker, final boolean trail,
      final List<Color> colors) {
    return new FireworkEffect(type, flicker, trail, colors);
  }

  /**
   * Creates a firework effect with default flicker and trail settings.
   *
   * @param type the explosion shape type
   * @param colors the primary colors of the effect
   * @return a new firework effect
   */
  public static FireworkEffect from(final FireworkEffectType type, final List<Color> colors) {
    return new FireworkEffect(type, colors);
  }

  /**
   * Checks whether this effect flickers.
   *
   * @return {@code true} if flicker is enabled
   */
  public boolean hasFlicker() {
    return flicker;
  }

  /**
   * Sets whether this effect flickers.
   *
   * @param flicker {@code true} to enable flicker
   * @return this firework effect
   */
  public FireworkEffect setFlicker(final boolean flicker) {
    this.flicker = flicker;
    return this;
  }

  /**
   * Checks whether this effect leaves a trail.
   *
   * @return {@code true} if trail is enabled
   */
  public boolean hasTrail() {
    return trail;
  }

  /**
   * Sets whether this effect leaves a trail.
   *
   * @param trail {@code true} to enable trail
   * @return this firework effect
   */
  public FireworkEffect setTrail(final boolean trail) {
    this.trail = trail;
    return this;
  }

  /**
   * Checks whether this effect has primary colors.
   *
   * @return {@code true} if at least one primary color is configured
   */
  public boolean hasColors() {
    return colors != null && !colors.isEmpty();
  }

  /**
   * Gets the primary colors of this effect.
   *
   * @return the primary colors
   */
  public List<Color> getColors() {
    return colors;
  }

  /**
   * Replaces the primary colors of this effect.
   *
   * @param colors the primary colors to set
   * @return this firework effect
   */
  public FireworkEffect setColors(final Color... colors) {
    if (colors != null) {
      this.colors = List.of(colors);
    }
    return this;
  }

  /**
   * Replaces the primary colors of this effect.
   *
   * @param colors the primary colors to set
   * @return this firework effect
   */
  public FireworkEffect setColors(final Collection<Color> colors) {
    if (colors != null) {
      this.colors = List.copyOf(colors);
    }
    return this;
  }

  /**
   * Adds primary colors to this effect.
   *
   * @param colors the colors to add
   * @return this firework effect
   */
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

  /**
   * Adds primary colors to this effect.
   *
   * @param colors the colors to add
   * @return this firework effect
   */
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

  /**
   * Gets the fade colors of this effect.
   *
   * @return the fade colors
   */
  public List<Color> getFadeColors() {
    return fadeColors;
  }

  /**
   * Checks whether this effect has fade colors.
   *
   * @return {@code true} if at least one fade color is configured
   */
  public boolean hasFadeColors() {
    return fadeColors != null && !fadeColors.isEmpty();
  }

  /**
   * Replaces the fade colors of this effect.
   *
   * @param colors the fade colors to set
   * @return this firework effect
   */
  public FireworkEffect setFadeColors(final Color... colors) {
    if (colors != null && colors.length != 0) {
      this.fadeColors = List.of(colors);
    }
    return this;
  }

  /**
   * Replaces the fade colors of this effect.
   *
   * @param fadeColors the fade colors to set
   * @return this firework effect
   */
  public FireworkEffect setFadeColors(final Collection<Color> fadeColors) {
    if (fadeColors != null) {
      this.fadeColors = new ArrayList<>(fadeColors);
    }
    return this;
  }

  /**
   * Adds fade colors to this effect.
   *
   * @param colors the fade colors to add
   * @return this firework effect
   */
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

  /**
   * Adds fade colors to this effect.
   *
   * @param colors the fade colors to add
   * @return this firework effect
   */
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

  /**
   * Gets the explosion shape type of this effect.
   *
   * @return the firework effect type
   */
  public FireworkEffectType getType() {
    return type;
  }

  /**
   * Sets the explosion shape type of this effect.
   *
   * @param type the firework effect type
   * @return this firework effect
   * @throws IllegalArgumentException if the type is {@code null}
   */
  public FireworkEffect setType(final FireworkEffectType type) {
    if (type == null) {
      throw new IllegalArgumentException("Type cant be null");
    }
    this.type = type;
    return this;
  }

  /**
   * Deserializes a firework effect from a configuration map.
   *
   * @param map the serialized firework effect data
   * @return the deserialized firework effect
   */
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

  /**
   * Serializes this firework effect into a configuration map.
   *
   * @return a serialized representation of this firework effect
   */
  @Override
  public Map<String, Object> serialize() {
    return Map.<String, Object>of(TYPE, type.name(),
        FLICKER, flicker,
        TRAIL, trail,
        COLORS, colors,
        FADE_COLORS, fadeColors);
  }

  /**
   * Creates a copy of this firework effect.
   *
   * @return a cloned firework effect
   */
  @Override
  public FireworkEffect clone() {
    return new FireworkEffect(this);
  }

  /**
   * Converts this effect into a Bukkit firework effect.
   *
   * @return the Bukkit firework effect, or {@code null} if conversion fails
   * @throws IllegalStateException if no primary colors are configured
   */
  public org.bukkit.FireworkEffect toBukkit() {
    if (colors == null || colors.isEmpty()) {
      throw new IllegalStateException("Cannot make FireworkEffect without any color");
    }
    try {
      final org.bukkit.FireworkEffect.Builder builder = org.bukkit.FireworkEffect.builder()
          .with(type == null ? FireworkEffectType.BALL.toBukkit() : type.toBukkit())
          .flicker(flicker)
          .trail(trail)
          .withColor(colors);
      if (fadeColors != null && !fadeColors.isEmpty()) {
        builder.withFade(fadeColors);
      }
      return builder.build();
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Applies this effect to firework star metadata.
   */
  public org.bukkit.inventory.meta.FireworkEffectMeta apply(final org.bukkit.inventory.meta.FireworkEffectMeta meta) {
    if (meta == null) {
      return null;
    }
    final org.bukkit.FireworkEffect bukkit = toBukkit();
    if (bukkit != null) {
      meta.setEffect(bukkit);
    }
    return meta;
  }

  /**
   * Applies this effect to a builder targeting firework star meta.
   */
  @Override
  public void apply(final ItemStackBuilder<?> builder) {
    applyTo(builder);
  }

  /**
   * Applies this effect to a builder targeting firework star meta.
   */
  public void applyTo(final ItemStackBuilder<?> builder) {
    if (builder == null) {
      return;
    }
    builder.meta(org.bukkit.inventory.meta.FireworkEffectMeta.class, this::apply);
  }

  /**
   * Returns this effect as a reusable item modifier.
   */
  public ItemModifier asModifier() {
    return this::applyTo;
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
