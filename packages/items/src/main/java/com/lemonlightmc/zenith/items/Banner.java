package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;

/**
 * Mutable representation of a banner's base color and patterns.
 */
public class Banner implements Cloneable<Banner>, ConfigurationSerializable, ItemModifier {
  private List<Pattern> patterns;
  private DyeColor baseColor;

  /**
   * Creates a banner with the given base color and patterns.
   *
   * @param baseColor the base color of the banner
   * @param patterns  the banner patterns to apply
   */
  public Banner(final DyeColor baseColor, final List<Pattern> patterns) {
    this.baseColor = baseColor == null ? DyeColor.WHITE : baseColor;
    this.patterns = patterns == null ? new ArrayList<>() : patterns;
  }

  /**
   * Creates a copy of another banner.
   *
   * @param banner the banner to copy
   */
  public Banner(final Banner banner) {
    if (banner == null) {
      this.baseColor = DyeColor.WHITE;
      this.patterns = new ArrayList<>();
      return;
    }
    this.baseColor = banner.baseColor == null ? DyeColor.WHITE : banner.baseColor;
    this.patterns = banner.patterns == null ? new ArrayList<>() : banner.patterns;
  }

  /**
   * Creates a banner with the given patterns and a default white base color.
   *
   * @param patterns the banner patterns to apply
   */
  public Banner(final List<Pattern> patterns) {
    this(null, patterns);
  }

  /**
   * Creates an empty white banner.
   */
  public Banner() {
    this(null, null);
  }

  /**
   * Creates a banner from Bukkit banner metadata.
   *
   * @param meta the banner metadata to read from
   * @return a banner containing the metadata's patterns
   */
  public static Banner from(final BannerMeta meta) {
    final Banner banner = new Banner();
    return banner.setPatterns(meta.getPatterns());
  }

  /**
   * Creates a banner from a Bukkit banner block state.
   *
   * @param bukkitBanner the Bukkit banner to read from
   * @return a banner containing the block state's base color and patterns
   */
  public static Banner from(final org.bukkit.block.Banner bukkitBanner) {
    final Banner banner = new Banner();
    banner.setBaseColor(bukkitBanner.getBaseColor());
    banner.setPatterns(bukkitBanner.getPatterns());
    return banner;
  }

  /**
   * Creates a copy of the given banner.
   *
   * @param banner the banner to copy
   * @return a new banner instance
   */
  public static Banner from(final Banner banner) {
    return new Banner(banner);
  }

  /**
   * Creates a banner from a base color and patterns.
   *
   * @param color    the base color of the banner
   * @param patterns the banner patterns to apply
   * @return a new banner instance
   */
  public static Banner from(final DyeColor color, final List<Pattern> patterns) {
    return new Banner(color, patterns);
  }

  /**
   * Creates a banner from the given patterns and a default white base color.
   *
   * @param patterns the banner patterns to apply
   * @return a new banner instance
   */
  public static Banner from(final List<Pattern> patterns) {
    return new Banner(patterns);
  }

  /**
   * Creates an empty white banner.
   *
   * @return a new banner instance
   */
  public static Banner from() {
    return new Banner();
  }

  /**
   * Gets the patterns applied to this banner.
   *
   * @return the banner patterns
   */
  public List<Pattern> getPatterns() {
    return patterns;
  }

  /**
   * Replaces the banner patterns with the given values.
   *
   * @param patterns the patterns to set
   * @return this banner
   */
  public Banner setPatterns(final Pattern... patterns) {
    this.patterns = List.of(patterns);
    return this;
  }

  /**
   * Replaces the banner patterns with the given list.
   *
   * @param patterns the patterns to set
   * @return this banner
   */
  public Banner setPatterns(final List<Pattern> patterns) {
    this.patterns = patterns;
    return this;
  }

  /**
   * Adds patterns to this banner.
   *
   * @param patterns the patterns to add
   * @return this banner
   */
  public Banner addPatterns(final Pattern... patterns) {
    if (patterns == null || patterns.length == 0) {
      return this;
    }
    for (final Pattern pattern : patterns) {
      if (pattern != null) {
        this.patterns.add(pattern);
      }
    }
    return this;
  }

  /**
   * Adds patterns to this banner.
   *
   * @param patterns the patterns to add
   * @return this banner
   */
  public Banner addPatterns(final List<Pattern> patterns) {
    if (patterns == null || patterns.isEmpty()) {
      return this;
    }
    for (final Pattern pattern : patterns) {
      if (pattern != null) {
        this.patterns.add(pattern);
      }
    }
    return this;
  }

  /**
   * Removes patterns from this banner.
   *
   * @param patterns the patterns to remove
   * @return this banner
   */
  public Banner removePatterns(final Pattern... patterns) {
    if (patterns == null || patterns.length == 0) {
      return this;
    }
    for (final Pattern pattern : patterns) {
      if (pattern != null) {
        this.patterns.remove(pattern);
      }
    }
    return this;
  }

  /**
   * Removes patterns from this banner.
   *
   * @param patterns the patterns to remove
   * @return this banner
   */
  public Banner removePatterns(final List<Pattern> patterns) {
    if (patterns == null || patterns.isEmpty()) {
      return this;
    }
    for (final Pattern pattern : patterns) {
      if (pattern != null) {
        this.patterns.remove(pattern);
      }
    }
    return this;
  }

  /**
   * Adds a pattern to this banner.
   *
   * @param patternType the pattern type to add
   * @param color       the pattern color
   * @return this banner
   */
  public Banner addPattern(final PatternType patternType, final DyeColor color) {
    if (patternType == null || color == null) {
      return this;
    }
    this.patterns.add(new Pattern(color, patternType));
    return this;
  }

  /**
   * Inserts a pattern at the specified index.
   *
   * @param idx         the index at which to insert the pattern
   * @param patternType the pattern type to add
   * @param color       the pattern color
   * @return this banner
   */
  public Banner addPattern(final int idx, final PatternType patternType, final DyeColor color) {
    if (patternType == null || color == null || idx < 0 || idx >= patterns.size()) {
      return this;
    }
    this.patterns.add(idx, new Pattern(color, patternType));
    return this;
  }

  /**
   * Inserts a pattern at the specified index.
   *
   * @param idx     the index at which to insert the pattern
   * @param pattern the pattern to insert
   * @return this banner
   */
  public Banner setPattern(final int idx, final Pattern pattern) {
    if (pattern == null || idx < 0 || idx >= patterns.size()) {
      return this;
    }
    this.patterns.add(idx, pattern);
    return this;
  }

  /**
   * Replaces the pattern at the specified index.
   *
   * @param idx         the index of the pattern to replace
   * @param patternType the new pattern type
   * @param color       the new pattern color
   * @return this banner
   */
  public Banner setPattern(final int idx, final PatternType patternType, final DyeColor color) {
    if (patternType == null || color == null || idx < 0 || idx >= patterns.size()) {
      return this;
    }
    this.patterns.set(idx, new Pattern(color, patternType));
    return this;
  }

  /**
   * Removes the pattern at the specified index.
   *
   * @param idx the index of the pattern to remove
   * @return this banner
   */
  public Banner removePattern(final int idx) {
    if (idx < 0 || idx >= patterns.size()) {
      return this;
    }
    patterns.remove(idx);
    return this;
  }

  /**
   * Gets the number of patterns on this banner.
   *
   * @return the pattern count
   */
  public int numberOfPatterns() {
    return patterns.size();
  }

  /**
   * Gets the base color of this banner.
   *
   * @return the base color
   */
  public DyeColor getBaseColor() {
    return baseColor;
  }

  /**
   * Sets the base color of this banner.
   *
   * @param baseColor the new base color
   * @return this banner
   * @throws IllegalArgumentException if the base color is {@code null}
   */
  public Banner setBaseColor(final DyeColor baseColor) {
    if (baseColor == null) {
      throw new IllegalArgumentException("Base color cannot be null");
    }
    this.baseColor = baseColor;
    return this;
  }

  /**
   * Gets the banner material corresponding to the current base color.
   *
   * @return the material for this banner's base color
   */
  public Material toMaterial() {
    switch (baseColor) {
      case WHITE: {
        return Material.WHITE_BANNER;
      }
      case ORANGE: {
        return Material.ORANGE_BANNER;
      }
      case MAGENTA: {
        return Material.MAGENTA_BANNER;
      }
      case LIGHT_BLUE: {
        return Material.LIGHT_BLUE_BANNER;
      }
      case YELLOW: {
        return Material.YELLOW_BANNER;
      }
      case LIME: {
        return Material.LIME_BANNER;
      }
      case PINK: {
        return Material.PINK_BANNER;
      }
      case GRAY: {
        return Material.GRAY_BANNER;
      }
      case LIGHT_GRAY: {
        return Material.LIGHT_GRAY_BANNER;
      }
      case CYAN: {
        return Material.CYAN_BANNER;
      }
      case PURPLE: {
        return Material.PURPLE_BANNER;
      }
      case BLUE: {
        return Material.BLUE_BANNER;
      }
      case BROWN: {
        return Material.BROWN_BANNER;
      }
      case GREEN: {
        return Material.GREEN_BANNER;
      }
      case RED: {
        return Material.RED_BANNER;
      }
      default: {
        return Material.WHITE_BANNER;
      }
    }
  }

  /**
   * Creates an item stack representing this banner.
   *
   * @return an item stack with this banner's color and patterns
   */
  public ItemStack toItem() {
    final ItemStack item = new ItemStack(toMaterial(), 1);
    item.setItemMeta(apply(item.getItemMeta()));
    return item;
  }

  /**
   * Applies this banner's properties to a Bukkit banner block state.
   *
   * @param banner the Bukkit banner to update
   * @return the updated banner, or {@code null} if the input was {@code null}
   */
  public org.bukkit.block.Banner apply(final org.bukkit.block.Banner banner) {
    if (banner == null) {
      return banner;
    }
    banner.setPatterns(patterns);
    banner.setBaseColor(baseColor);
    return banner;
  }

  /**
   * Applies this banner's patterns to banner item metadata.
   *
   * @param meta the item metadata to update
   * @return the updated metadata, or the original metadata if it is not banner
   *         metadata
   */
  public ItemMeta apply(final ItemMeta meta) {
    if (meta == null || !(meta instanceof final BannerMeta bannerMeta)) {
      return meta;
    }
    bannerMeta.setPatterns(patterns);
    return bannerMeta;
  }

  /**
   * Applies this banner (material and patterns) to the given builder.
   */
  @Override
  public void apply(final ItemStackBuilder<?> builder) {
    if (builder == null) {
      return;
    }
    builder.type(toMaterial());
    builder.meta(BannerMeta.class, m -> m.setPatterns(patterns == null ? List.of() : patterns));
  }

  /**
   * Returns this banner as a reusable item modifier.
   */
  public ItemModifier asModifier() {
    return this::apply;
  }

  /**
   * Creates a copy of this banner.
   *
   * @return a cloned banner
   */
  @Override
  public Banner clone() {
    return new Banner(this);
  }

  @Override
  public int hashCode() {
    return 31 * (31 + patterns.hashCode()) + baseColor.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Banner other = (Banner) obj;
    if (patterns == null && other.patterns != null) {
      return false;
    }
    return baseColor == other.baseColor && patterns.equals(other.patterns);
  }

  @Override
  public String toString() {
    return "Banner [patterns=" + patterns + ", baseColor=" + baseColor + "]";
  }

  /**
   * Serializes this banner into a configuration map.
   *
   * @return a serialized representation of this banner
   */
  @Override
  public Map<String, Object> serialize() {
    return Map.of("patterns", patterns, "baseColor", baseColor);
  }

  /**
   * Deserializes a banner from a configuration map.
   *
   * @param map the serialized banner data
   * @return the deserialized banner
   */
  @SuppressWarnings("unchecked")
  public static Banner deserialize(final Map<String, Object> map) {
    final List<Pattern> patterns = (List<Pattern>) map.get("patterns");
    final DyeColor baseColor = (DyeColor) map.get("baseColor");
    return new Banner(baseColor, patterns);
  }
}
