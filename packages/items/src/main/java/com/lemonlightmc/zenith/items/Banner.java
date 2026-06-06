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

import com.lemonlightmc.zenith.interfaces.Cloneable;

public class Banner implements Cloneable<Banner>, ConfigurationSerializable {
  private List<Pattern> patterns;
  private DyeColor baseColor;

  public Banner(final DyeColor baseColor, final List<Pattern> patterns) {
    this.baseColor = baseColor == null ? DyeColor.WHITE : baseColor;
    this.patterns = patterns == null ? new ArrayList<>() : patterns;
  }

  public Banner(final Banner banner) {
    if (banner == null) {
      this.baseColor = DyeColor.WHITE;
      this.patterns = new ArrayList<>();
      return;
    }
    this.baseColor = banner.baseColor == null ? DyeColor.WHITE : banner.baseColor;
    this.patterns = banner.patterns == null ? new ArrayList<>() : banner.patterns;
  }

  public Banner(final List<Pattern> patterns) {
    this(null, patterns);
  }

  public Banner() {
    this(null, null);
  }

  public static Banner from(final BannerMeta meta) {
    final Banner banner = new Banner();
    return banner.setPatterns(meta.getPatterns());
  }

  public static Banner from(final org.bukkit.block.Banner bukkitBanner) {
    final Banner banner = new Banner();
    banner.setBaseColor(bukkitBanner.getBaseColor());
    banner.setPatterns(bukkitBanner.getPatterns());
    return banner;
  }

  public static Banner from(final Banner banner) {
    return new Banner(banner);
  }

  public static Banner from(final DyeColor color, final List<Pattern> patterns) {
    return new Banner(color, patterns);
  }

  public static Banner from(final List<Pattern> patterns) {
    return new Banner(patterns);
  }

  public static Banner from() {
    return new Banner();
  }

  public List<Pattern> getPatterns() {
    return patterns;
  }

  public Banner setPatterns(final Pattern... patterns) {
    this.patterns = List.of(patterns);
    return this;
  }

  public Banner setPatterns(final List<Pattern> patterns) {
    this.patterns = patterns;
    return this;
  }

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

  public Banner addPattern(final PatternType patternType, final DyeColor color) {
    if (patternType == null || color == null) {
      return this;
    }
    this.patterns.add(new Pattern(color, patternType));
    return this;
  }

  public Banner addPattern(final int idx, final PatternType patternType, final DyeColor color) {
    if (patternType == null || color == null || idx < 0 || idx >= patterns.size()) {
      return this;
    }
    this.patterns.add(idx, new Pattern(color, patternType));
    return this;
  }

  public Banner setPattern(final int idx, final Pattern pattern) {
    if (pattern == null || idx < 0 || idx >= patterns.size()) {
      return this;
    }
    this.patterns.add(idx, pattern);
    return this;
  }

  public Banner setPattern(final int idx, final PatternType patternType, final DyeColor color) {
    if (patternType == null || color == null || idx < 0 || idx >= patterns.size()) {
      return this;
    }
    this.patterns.set(idx, new Pattern(color, patternType));
    return this;
  }

  public Banner removePattern(final int idx) {
    if (idx < 0 || idx >= patterns.size()) {
      return this;
    }
    patterns.remove(idx);
    return this;
  }

  public int numberOfPatterns() {
    return patterns.size();
  }

  public DyeColor getBaseColor() {
    return baseColor;
  }

  public Banner setBaseColor(final DyeColor baseColor) {
    if (baseColor == null) {
      throw new IllegalArgumentException("Base color cannot be null");
    }
    this.baseColor = baseColor;
    return this;
  }

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

  public ItemStack toItem() {
    final ItemStack item = new ItemStack(toMaterial(), 1);
    item.setItemMeta(apply(item.getItemMeta()));
    return item;
  }

  public org.bukkit.block.Banner apply(final org.bukkit.block.Banner banner) {
    if (banner == null) {
      return banner;
    }
    banner.setPatterns(patterns);
    banner.setBaseColor(baseColor);
    return banner;
  }

  public ItemMeta apply(final ItemMeta meta) {
    if (meta == null || !(meta instanceof final BannerMeta bannerMeta)) {
      return meta;
    }
    bannerMeta.setPatterns(patterns);
    return bannerMeta;
  }

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

  @Override
  public Map<String, Object> serialize() {
    return Map.of("patterns", patterns, "baseColor", baseColor);
  }

  @SuppressWarnings("unchecked")
  public static Banner deserialize(final Map<String, Object> map) {
    final List<Pattern> patterns = (List<Pattern>) map.get("patterns");
    final DyeColor baseColor = (DyeColor) map.get("baseColor");
    return new Banner(baseColor, patterns);
  }
}
