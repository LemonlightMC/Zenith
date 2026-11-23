package com.lemonlightmc.zenith.utils.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.time.TimeRange;
import com.lemonlightmc.zenith.internal.ConditionSerializer;
import com.lemonlightmc.zenith.math.Region;
import com.lemonlightmc.zenith.utils.CooldownStore;
import com.lemonlightmc.zenith.utils.Plugins;

/**
 * Collection of built-in condition implementations and helpers.
 */
public class Conditions {

  public static final ConditionSerializer SERIALIZER = ConditionSerializer.INSTANCE;

  /**
   * Defines whether a condition list acts as a whitelist or blacklist.
   */
  public static enum ConditionListType {
    WHITELIST(false),
    BLACKLIST(true);

    private final boolean negate;

    private ConditionListType(final boolean negate) {
      this.negate = negate;
    }

    public boolean apply(final boolean value) {
      return negate ? !value : value;
    }
  }

  // -------------------- Existing conditions

  public static class WorldConditions<S extends CommandSender> implements Condition<S> {

    private final Set<String> worlds;
    private final ConditionListType type;

    private WorldConditions(final Set<String> worlds, final ConditionListType type) {
      this.worlds = toLowerCaseSet(worlds);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<String> allowed_worlds) {
      if (allowed_worlds == null || allowed_worlds.isEmpty()) {
        return NeverCondition.of();
      }
      return new WorldConditions<>(allowed_worlds, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<String> denied_worlds) {
      if (denied_worlds == null || denied_worlds.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new WorldConditions<>(denied_worlds, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<String> worlds, final ConditionListType type) {
      if (worlds == null || worlds.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new WorldConditions<>(worlds, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (!context.isPlayer()) {
        return false;
      }
      return type.apply(worlds.contains(context.player().getWorld().getName().toLowerCase(Locale.ROOT)));
    }
  }

  public static class BiomeConditions<S extends CommandSender> implements Condition<S> {

    private final Set<String> biomes;
    private final ConditionListType type;

    private BiomeConditions(final Set<String> biomes, final ConditionListType type) {
      this.biomes = toLowerCaseSet(biomes);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<String> allowed_biomes) {
      if (allowed_biomes == null || allowed_biomes.isEmpty()) {
        return NeverCondition.of();
      }
      return new BiomeConditions<>(allowed_biomes, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<String> denied_biomes) {
      if (denied_biomes == null || denied_biomes.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new BiomeConditions<>(denied_biomes, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<String> biomes, final ConditionListType type) {
      if (biomes == null || biomes.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new BiomeConditions<>(biomes, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      try {
        final Biome biome = player.getWorld().getBiome(player.getLocation());
        final String b = biome == null ? "" : biome.toString().toLowerCase(Locale.ROOT);
        final String key = biome != null && biome.getKeyOrNull() != null
            ? biome.getKeyOrNull().toString().toLowerCase(Locale.ROOT)
            : "";
        final String keyOnly = biome != null && biome.getKeyOrNull() != null
            ? biome.getKeyOrNull().getKey().toLowerCase(Locale.ROOT)
            : "";
        return type.apply(biomes.contains(b) || biomes.contains(key) || biomes.contains(keyOnly));
      } catch (final Throwable ignored) {
        return false;
      }
    }
  }

  public static class WeatherConditions<S extends CommandSender> implements Condition<S> {

    private final Set<String> weathers;
    private final ConditionListType type;

    private WeatherConditions(final Set<String> weathers, final ConditionListType type) {
      this.weathers = toLowerCaseSet(weathers);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<String> allowed_weathers) {
      if (allowed_weathers == null || allowed_weathers.isEmpty()) {
        return NeverCondition.of();
      }
      return new WeatherConditions<>(allowed_weathers, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<String> denied_weathers) {
      if (denied_weathers == null || denied_weathers.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new WeatherConditions<>(denied_weathers, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<String> weathers,
        final ConditionListType type) {
      if (weathers == null || weathers.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new WeatherConditions<>(weathers, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final World world = player.getWorld();
      final String now = world.isThundering() ? "thunder" : (world.hasStorm() ? "rain" : "clear");
      return type.apply(weathers.contains(now));
    }
  }

  public static class MoonConditions<S extends CommandSender> implements Condition<S> {

    private final Set<Integer> moonPhases;
    private final ConditionListType type;

    private MoonConditions(final Set<Integer> moonPhases, final ConditionListType type) {
      this.moonPhases = moonPhases == null ? new HashSet<>() : new HashSet<>(moonPhases);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<Integer> moonPhases) {
      if (moonPhases == null || moonPhases.isEmpty()) {
        return NeverCondition.of();
      }
      return new MoonConditions<>(moonPhases, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<Integer> moonPhases) {
      if (moonPhases == null || moonPhases.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new MoonConditions<>(moonPhases, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<Integer> moonPhases,
        final ConditionListType type) {
      if (moonPhases == null || moonPhases.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new MoonConditions<>(moonPhases, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final int value = getMoonPhase(player);
      return type.apply(moonPhases.contains(value));
    }

    private static int getMoonPhase(final Player player) {
      final long day = player.getWorld().getFullTime() / 24000L;
      return (int) Math.floorMod(day, 8);
    }
  }

  public static class TimeRangeConditions<S extends CommandSender> implements Condition<S> {

    private final Set<TimeRange> timeRanges;
    private final ConditionListType type;

    private TimeRangeConditions(final Set<TimeRange> timeRanges, final ConditionListType type) {
      this.timeRanges = timeRanges == null ? new HashSet<>() : new HashSet<>(timeRanges);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<TimeRange> timeRanges) {
      if (timeRanges == null || timeRanges.isEmpty()) {
        return NeverCondition.of();
      }
      return new TimeRangeConditions<>(timeRanges, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<TimeRange> timeRanges) {
      if (timeRanges == null || timeRanges.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new TimeRangeConditions<>(timeRanges, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<TimeRange> timeRanges,
        final ConditionListType type) {
      if (timeRanges == null || timeRanges.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new TimeRangeConditions<>(timeRanges, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final int t = (int) Math.floorMod(player.getWorld().getFullTime(), 24000);
      boolean match = false;
      for (final TimeRange timeRange : timeRanges) {
        if (timeRange != null && timeRange.contains(t)) {
          match = true;
          break;
        }
      }
      return type.apply(match);
    }
  }

  public static class AdvancementConditions<S extends CommandSender> implements Condition<S> {

    private final Set<String> advancements;
    private final ConditionListType type;
    private final java.util.List<NamespacedKey> cachedKeys;

    private AdvancementConditions(final Set<String> advancements, final ConditionListType type) {
      this.advancements = advancements == null ? new HashSet<>() : new HashSet<>(advancements);
      this.type = type == null ? ConditionListType.WHITELIST : type;
      final java.util.List<NamespacedKey> keys = new java.util.ArrayList<>();
      for (final String adv : this.advancements) {
        if (adv == null || adv.isEmpty())
          continue;
        final NamespacedKey key = adv.indexOf(':') >= 0 ? NamespacedKey.fromString(adv) : NamespacedKey.minecraft(adv);
        if (key != null)
          keys.add(key);
      }
      this.cachedKeys = java.util.Collections.unmodifiableList(keys);
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<String> advancements) {
      if (advancements == null || advancements.isEmpty()) {
        return NeverCondition.of();
      }
      return new AdvancementConditions<>(advancements, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<String> advancements) {
      if (advancements == null || advancements.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new AdvancementConditions<>(advancements, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<String> advancements,
        final ConditionListType type) {
      if (advancements == null || advancements.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new AdvancementConditions<>(advancements, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (cachedKeys.isEmpty()) {
        return false;
      }
      boolean value = true;
      boolean evaluated = false;
      for (final NamespacedKey key : cachedKeys) {
        final Advancement a = Bukkit.getAdvancement(key);
        if (a == null)
          continue;
        final AdvancementProgress prog = player.getAdvancementProgress(a);
        value = value && type.apply(prog.isDone());
        if (!value) {
          return false;
        }
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public static class RecipeConditions<S extends CommandSender> implements Condition<S> {

    private final Set<String> recipes;
    private final ConditionListType type;
    private final java.util.List<NamespacedKey> cachedKeys;

    private RecipeConditions(final Set<String> recipes, final ConditionListType type) {
      this.recipes = recipes == null ? new HashSet<>() : new HashSet<>(recipes);
      this.type = type == null ? ConditionListType.WHITELIST : type;
      final java.util.List<NamespacedKey> keys = new java.util.ArrayList<>();
      for (final String recipe : this.recipes) {
        if (recipe == null || recipe.isEmpty())
          continue;
        final NamespacedKey key = recipe.indexOf(':') >= 0 ? NamespacedKey.fromString(recipe)
            : NamespacedKey.minecraft(recipe);
        if (key != null)
          keys.add(key);
      }
      this.cachedKeys = java.util.Collections.unmodifiableList(keys);
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<String> recipes) {
      if (recipes == null || recipes.isEmpty()) {
        return NeverCondition.of();
      }
      return new RecipeConditions<>(recipes, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<String> recipes) {
      if (recipes == null || recipes.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new RecipeConditions<>(recipes, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<String> recipes, final ConditionListType type) {
      if (recipes == null || recipes.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new RecipeConditions<>(recipes, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (cachedKeys.isEmpty()) {
        return false;
      }
      boolean value = true;
      boolean evaluated = false;
      for (final NamespacedKey key : cachedKeys) {
        value = value && type.apply(player.hasDiscoveredRecipe(key));
        if (!value) {
          return false;
        }
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public static class PluginConditions<S extends CommandSender> implements Condition<S> {

    private final Set<String> plugins;
    private final ConditionListType type;

    private PluginConditions(final Set<String> plugins, final ConditionListType type) {
      this.plugins = plugins == null ? new HashSet<>() : new HashSet<>(plugins);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<String> plugins) {
      if (plugins == null || plugins.isEmpty()) {
        return NeverCondition.of();
      }
      return new PluginConditions<>(plugins, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<String> plugins) {
      if (plugins == null || plugins.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new PluginConditions<>(plugins, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<String> plugins, final ConditionListType type) {
      if (plugins == null || plugins.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new PluginConditions<>(plugins, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null) {
        return false;
      }
      boolean value = true;
      boolean evaluated = false;
      for (final String plugin : plugins) {
        if (plugin == null || plugin.isEmpty())
          continue;
        final boolean isEnabled = ZenithProvider.instance().getPluginManager().isPluginEnabled(plugin);
        value = value && type.apply(isEnabled);
        if (!value) {
          return false;
        }
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public static class PermissionConditions<S extends CommandSender> implements Condition<S> {

    private final String[] permissions;
    private final ConditionListType type;

    private PermissionConditions(final Collection<String> permissions, final ConditionListType type) {
      this.permissions = permissions == null ? new String[0] : permissions.toArray(String[]::new);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    private PermissionConditions(final String[] permissions, final ConditionListType type) {
      this.permissions = permissions == null ? new String[0] : permissions;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final String... permissions) {
      if (permissions == null || permissions.length == 0) {
        return NeverCondition.of();
      }
      return new PermissionConditions<>(permissions, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> allowed(final Collection<String> permissions) {
      if (permissions == null || permissions.isEmpty()) {
        return NeverCondition.of();
      }
      return new PermissionConditions<>(permissions, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Collection<String> permissions) {
      if (permissions == null || permissions.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new PermissionConditions<>(permissions, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Collection<String> permissions,
        final ConditionListType type) {
      if (permissions == null || permissions.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new PermissionConditions<>(permissions, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || context.sender() == null) {
        return false;
      }
      final CommandSender sender = context.sender();
      boolean value = true;
      boolean evaluated = false;
      for (final String permission : permissions) {
        if (permission == null || permission.isEmpty())
          continue;
        final boolean hasPerm = sender.hasPermission(permission);
        value = value && type.apply(hasPerm);
        if (!value) {
          return false;
        }
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  // -------------------- New requested conditions --------------------

  public static class GameModeConditions<S extends CommandSender> implements Condition<S> {
    private final Set<GameMode> modes;
    private final ConditionListType type;

    private GameModeConditions(final Set<GameMode> modes, final ConditionListType type) {
      this.modes = modes == null ? new HashSet<>() : new HashSet<>(modes);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<GameMode> modes) {
      if (modes == null || modes.isEmpty()) {
        return NeverCondition.of();
      }
      return new GameModeConditions<>(modes, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<GameMode> modes) {
      if (modes == null || modes.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new GameModeConditions<>(modes, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<GameMode> modes, final ConditionListType type) {
      if (modes == null || modes.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new GameModeConditions<>(modes, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(modes.contains(player.getGameMode()));
    }
  }

  public static class HealthConditions<S extends CommandSender> implements Condition<S> {
    private final Double min;
    private final Double max;
    private final boolean percent;
    private final ConditionListType type;

    private HealthConditions(final Double min, final Double max, final boolean percent, final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.percent = percent;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> range(final double min, final double max) {
      return new HealthConditions<>(min, max, false, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> percentRange(final double min, final double max) {
      return new HealthConditions<>(min, max, true, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Double min, final Double max, final boolean percent,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new HealthConditions<>(min, max, percent, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      double value = context.player().getHealth();
      if (percent) {
        final AttributeInstance instance = context.player().getAttribute(Attribute.MAX_HEALTH);
        value = (value / Math.max(1.0, instance.getValue())) * 100.0;
      }
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class LevelConditions<S extends CommandSender> implements Condition<S> {
    private final Integer min;
    private final Integer max;
    private final ConditionListType type;

    private LevelConditions(final Integer min, final Integer max, final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> range(final int min, final int max) {
      return new LevelConditions<>(min, max, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> atLeast(final int min) {
      return new LevelConditions<>(min, null, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Integer min, final Integer max,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new LevelConditions<>(min, max, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final int value = player.getLevel();
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class FoodConditions<S extends CommandSender> implements Condition<S> {
    private final Integer min;
    private final Integer max;
    private final ConditionListType type;

    private FoodConditions(final Integer min, final Integer max, final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> range(final int min, final int max) {
      return new FoodConditions<>(min, max, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Integer min, final Integer max,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new FoodConditions<>(min, max, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final int value = player.getFoodLevel();
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class FlightConditions<S extends CommandSender> implements Condition<S> {
    private final Boolean allowFlight;
    private final Boolean flying;
    private final ConditionListType type;

    private FlightConditions(final Boolean allowFlight, final Boolean flying, final ConditionListType type) {
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Boolean allowFlight, final Boolean flying) {
      if (allowFlight == null && flying == null) {
        return NeverCondition.of();
      }
      return new FlightConditions<>(allowFlight, flying, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Boolean allowFlight, final Boolean flying) {
      if (allowFlight == null && flying == null) {
        return AlwaysCondition.of();
      }
      return new FlightConditions<>(allowFlight, flying, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Boolean allowFlight, final Boolean flying,
        final ConditionListType type) {
      if (allowFlight == null && flying == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new FlightConditions<>(allowFlight, flying, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      boolean match = true;
      if (allowFlight != null) {
        match = match && player.getAllowFlight() == allowFlight;
      }
      if (flying != null) {
        match = match && player.isFlying() == flying;
      }
      return type.apply(match);
    }
  }

  public static class InvulnerableConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    protected InvulnerableConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new InvulnerableConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final boolean expected) {
      return new InvulnerableConditions<>(expected, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new InvulnerableConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isInvulnerable() == expected);
    }
  }

  public static class SneakingConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    private SneakingConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new SneakingConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new SneakingConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isSneaking() == expected);
    }
  }

  public static class SprintingConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    private SprintingConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new SprintingConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new SprintingConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isSprinting() == expected);
    }
  }

  public static class SwimmingConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    private SwimmingConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new SwimmingConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new SwimmingConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isSwimming() == expected);
    }
  }

  public static class GlidingConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    private GlidingConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new GlidingConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new GlidingConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isGliding() == expected);
    }
  }

  public static class GameRulesConditions<S extends CommandSender> implements Condition<S> {
    private final Map<String, String> expectedRules;
    private final ConditionListType type;

    private GameRulesConditions(final Map<String, String> expectedRules, final ConditionListType type) {
      this.expectedRules = expectedRules == null ? Collections.emptyMap() : expectedRules;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Map<String, String> expectedRules) {
      if (expectedRules == null || expectedRules.isEmpty()) {
        return NeverCondition.of();
      }
      return new GameRulesConditions<>(expectedRules, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Map<String, String> expectedRules) {
      if (expectedRules == null || expectedRules.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new GameRulesConditions<>(expectedRules, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Map<String, String> expectedRules,
        final ConditionListType type) {
      if (expectedRules == null || expectedRules.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new GameRulesConditions<>(expectedRules, type);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      boolean value = true;
      boolean evaluated = false;
      for (final Map.Entry<String, String> entry : expectedRules.entrySet()) {
        if (entry == null || entry.getKey() == null) {
          continue;
        }
        final GameRule rule = GameRule.getByName(entry.getKey());
        if (rule == null) {
          continue;
        }
        final Object current = player.getWorld().getGameRuleValue(rule);
        final String currentStr = String.valueOf(current);
        final boolean matches = entry.getValue() == null ? current == null
            : entry.getValue().equalsIgnoreCase(currentStr);
        value = value && type.apply(matches);
        if (!value) {
          return false;
        }
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public enum HandSlot {
    MAIN, OFF
  }

  public static class ItemInHandConditions<S extends CommandSender> implements Condition<S> {
    private final Set<Material> materials;
    private final HandSlot slot;
    private final ConditionListType type;

    private ItemInHandConditions(final Set<Material> materials, final HandSlot slot, final ConditionListType type) {
      this.materials = materials == null ? new HashSet<>() : new HashSet<>(materials);
      this.slot = slot == null ? HandSlot.MAIN : slot;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<Material> materials, final HandSlot slot) {
      if (materials == null || materials.isEmpty()) {
        return NeverCondition.of();
      }
      return new ItemInHandConditions<>(materials, slot, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<Material> materials, final HandSlot slot) {
      if (materials == null || materials.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new ItemInHandConditions<>(materials, slot, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<Material> materials, final HandSlot slot,
        final ConditionListType type) {
      if (materials == null || materials.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new ItemInHandConditions<>(materials, slot, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getInventory() == null) {
        return false;
      }
      final ItemStack stack = slot == HandSlot.OFF ? player.getInventory().getItemInOffHand()
          : player.getInventory().getItemInMainHand();
      final Material mat = stack == null ? Material.AIR : stack.getType();
      return type.apply(materials.contains(mat));
    }
  }

  public static class ArmorConditions<S extends CommandSender> implements Condition<S> {
    private final Map<EquipmentSlot, Set<Material>> armor;
    private final ConditionListType type;

    private ArmorConditions(final Map<EquipmentSlot, Set<Material>> armor, final ConditionListType type) {
      this.armor = armor == null ? Collections.emptyMap() : armor;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Map<EquipmentSlot, Set<Material>> armor) {
      if (armor == null || armor.isEmpty()) {
        return NeverCondition.of();
      }
      return new ArmorConditions<>(armor, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Map<EquipmentSlot, Set<Material>> armor) {
      if (armor == null || armor.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new ArmorConditions<>(armor, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Map<EquipmentSlot, Set<Material>> armor,
        final ConditionListType type) {
      if (armor == null || armor.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new ArmorConditions<>(armor, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getEquipment() == null) {
        return false;
      }
      final EntityEquipment eq = player.getEquipment();
      boolean value = true;
      boolean evaluated = false;
      for (final Map.Entry<EquipmentSlot, Set<Material>> entry : armor.entrySet()) {
        if (entry == null || entry.getKey() == null || entry.getValue() == null || entry.getValue().isEmpty()) {
          continue;
        }
        final ItemStack item = eq.getItem(entry.getKey());
        final Material mat = item == null ? Material.AIR : item.getType();
        value = value && type.apply(entry.getValue().contains(mat));
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public static class EmptySlotConditions<S extends CommandSender> implements Condition<S> {
    private final Integer minEmptySlots;
    private final ConditionListType type;

    private EmptySlotConditions(final Integer minEmptySlots, final ConditionListType type) {
      this.minEmptySlots = minEmptySlots;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> atLeast(final int minEmptySlots) {
      return new EmptySlotConditions<>(minEmptySlots, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Integer minEmptySlots,
        final ConditionListType type) {
      if (minEmptySlots == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new EmptySlotConditions<>(minEmptySlots, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getInventory() == null) {
        return false;
      }
      int empty = 0;
      final ItemStack[] storage = player.getInventory().getStorageContents();
      if (storage != null) {
        for (final ItemStack item : storage) {
          if (item == null || item.getType() == Material.AIR) {
            empty++;
          }
        }
      }
      return type.apply(empty >= minEmptySlots);
    }
  }

  public static class InventoryContainsConditions<S extends CommandSender> implements Condition<S> {
    private final Material material;
    private final int minAmount;
    private final ConditionListType type;

    private InventoryContainsConditions(final Material material, final int minAmount, final ConditionListType type) {
      this.material = material;
      this.minAmount = Math.max(1, minAmount);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> atLeast(final Material material, final int minAmount) {
      if (material == null) {
        return NeverCondition.of();
      }
      return new InventoryContainsConditions<>(material, minAmount, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Material material, final int minAmount,
        final ConditionListType type) {
      if (material == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new InventoryContainsConditions<>(material, minAmount, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getInventory() == null) {
        return false;
      }
      return type.apply(player.getInventory().contains(material, minAmount));
    }
  }

  public static class RegionConditions<S extends CommandSender> implements Condition<S> {
    private final Set<Region> regions;
    private final ConditionListType type;

    private RegionConditions(final Set<Region> regions, final ConditionListType type) {
      this.regions = regions == null ? new HashSet<>() : new HashSet<>(regions);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<Region> regions) {
      if (regions == null || regions.isEmpty()) {
        return NeverCondition.of();
      }
      return new RegionConditions<>(regions, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<Region> regions) {
      if (regions == null || regions.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new RegionConditions<>(regions, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<Region> regions, final ConditionListType type) {
      if (regions == null || regions.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new RegionConditions<>(regions, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getLocation() == null) {
        return false;
      }
      boolean inside = false;
      for (final Region region : regions) {
        if (region == null) {
          continue;
        }
        try {
          if (region.inRegion(player.getLocation())) {
            inside = true;
            break;
          }
        } catch (final Throwable ignored) {
          // region world may be unloaded or mismatched
        }
      }
      return type.apply(inside);
    }
  }

  public static class LightLevelConditions<S extends CommandSender> implements Condition<S> {
    public enum LightType {
      BLOCK, SKY, MAX
    }

    private final Integer min;
    private final Integer max;
    private final LightType lightType;
    private final ConditionListType type;

    private LightLevelConditions(final Integer min, final Integer max, final LightType lightType,
        final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.lightType = lightType == null ? LightType.BLOCK : lightType;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> range(final int min, final int max,
        final LightType lightType) {
      return new LightLevelConditions<>(min, max, lightType, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Integer min, final Integer max,
        final LightType lightType,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new LightLevelConditions<>(min, max, lightType, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getLocation() == null || player.getLocation().getBlock() == null) {
        return false;
      }
      final int value;
      switch (lightType) {
        case SKY:
          value = player.getLocation().getBlock().getLightFromSky();
          break;
        case MAX:
          value = player.getLocation().getBlock().getLightLevel();
          break;
        case BLOCK:
        default:
          value = player.getLocation().getBlock().getLightFromBlocks();
          break;
      }
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class DimensionConditions<S extends CommandSender> implements Condition<S> {
    private final Set<World.Environment> environments;
    private final ConditionListType type;

    private DimensionConditions(final Set<World.Environment> environments, final ConditionListType type) {
      this.environments = environments == null ? new HashSet<>() : new HashSet<>(environments);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<World.Environment> environments) {
      if (environments == null || environments.isEmpty()) {
        return NeverCondition.of();
      }
      return new DimensionConditions<>(environments, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<World.Environment> environments) {
      if (environments == null || environments.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new DimensionConditions<>(environments, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<World.Environment> environments,
        final ConditionListType type) {
      if (environments == null || environments.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new DimensionConditions<>(environments, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getWorld() == null) {
        return false;
      }
      return type.apply(environments.contains(player.getWorld().getEnvironment()));
    }
  }

  public static class DifficultyConditions<S extends CommandSender> implements Condition<S> {
    private final Set<Difficulty> difficulties;
    private final ConditionListType type;

    private DifficultyConditions(final Set<Difficulty> difficulties, final ConditionListType type) {
      this.difficulties = difficulties == null ? new HashSet<>() : new HashSet<>(difficulties);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<Difficulty> difficulties) {
      if (difficulties == null || difficulties.isEmpty()) {
        return NeverCondition.of();
      }
      return new DifficultyConditions<>(difficulties, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<Difficulty> difficulties) {
      if (difficulties == null || difficulties.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new DifficultyConditions<>(difficulties, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<Difficulty> difficulties,
        final ConditionListType type) {
      if (difficulties == null || difficulties.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new DifficultyConditions<>(difficulties, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getWorld() == null) {
        return false;
      }
      return type.apply(difficulties.contains(player.getWorld().getDifficulty()));
    }
  }

  public static class StatisticConditions<S extends CommandSender> implements Condition<S> {
    public enum CompareOp {
      EQ, NE, GT, GE, LT, LE
    }

    private final Statistic statistic;
    private final Material material;
    private final EntityType entityType;
    private final int target;
    private final CompareOp op;
    private final ConditionListType type;

    private StatisticConditions(final Statistic statistic, final Material material, final EntityType entityType,
        final int target, final CompareOp op, final ConditionListType type) {
      this.statistic = statistic;
      this.material = material;
      this.entityType = entityType;
      this.target = target;
      this.op = op == null ? CompareOp.GE : op;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> of(final Statistic statistic, final int target,
        final CompareOp op) {
      if (statistic == null) {
        return NeverCondition.of();
      }
      return new StatisticConditions<>(statistic, null, null, target, op, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> of(final Statistic statistic, final Material material,
        final int target,
        final CompareOp op) {
      if (statistic == null) {
        return NeverCondition.of();
      }
      return new StatisticConditions<>(statistic, material, null, target, op, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> of(final Statistic statistic, final EntityType entityType,
        final int target,
        final CompareOp op) {
      if (statistic == null) {
        return NeverCondition.of();
      }
      return new StatisticConditions<>(statistic, null, entityType, target, op, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Statistic statistic, final Material material,
        final EntityType entityType, final int target, final CompareOp op, final ConditionListType type) {
      if (statistic == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new StatisticConditions<>(statistic, material, entityType, target, op, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      int value;
      try {
        if (material != null) {
          value = player.getStatistic(statistic, material);
        } else if (entityType != null) {
          value = player.getStatistic(statistic, entityType);
        } else {
          value = player.getStatistic(statistic);
        }
      } catch (final Throwable ignored) {
        return false;
      }
      final boolean matches;
      switch (op) {
        case EQ:
          matches = value == target;
          break;
        case NE:
          matches = value != target;
          break;
        case GT:
          matches = value > target;
          break;
        case GE:
          matches = value >= target;
          break;
        case LT:
          matches = value < target;
          break;
        case LE:
          matches = value <= target;
          break;
        default:
          matches = value >= target;
          break;
      }
      return type.apply(matches);
    }
  }

  public static class ObjectiveScoreConditions<S extends CommandSender> implements Condition<S> {
    private final String objectiveName;
    private final Integer min;
    private final Integer max;
    private final boolean usePlayerScoreboard;
    private final ConditionListType type;

    private ObjectiveScoreConditions(final String objectiveName, final Integer min, final Integer max,
        final boolean usePlayerScoreboard, final ConditionListType type) {
      this.objectiveName = objectiveName;
      this.min = min;
      this.max = max;
      this.usePlayerScoreboard = usePlayerScoreboard;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> range(final String objectiveName, final Integer min,
        final Integer max) {
      if (objectiveName == null || objectiveName.isEmpty() || (min == null && max == null)) {
        return NeverCondition.of();
      }
      return new ObjectiveScoreConditions<>(objectiveName, min, max, true, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final String objectiveName, final Integer min,
        final Integer max,
        final boolean usePlayerScoreboard, final ConditionListType type) {
      if (objectiveName == null || objectiveName.isEmpty() || (min == null && max == null)) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new ObjectiveScoreConditions<>(objectiveName, min, max, usePlayerScoreboard, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      try {
        final Scoreboard scoreboard = usePlayerScoreboard ? player.getScoreboard()
            : (Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard());
        if (scoreboard == null) {
          return false;
        }
        final Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
          return false;
        }
        final Score score = objective.getScore(player.getName());
        final int value = score == null ? 0 : score.getScore();
        boolean inRange = true;
        if (min != null) {
          inRange = inRange && value >= min;
        }
        if (max != null) {
          inRange = inRange && value <= max;
        }
        return type.apply(inRange);
      } catch (final Throwable ignored) {
        return false;
      }
    }
  }

  public static class CooldownConditions<S extends CommandSender> implements Condition<S> {
    private final CooldownStore<UUID> store;
    private final boolean requireOnCooldown;
    private final ConditionListType type;

    private CooldownConditions(final CooldownStore<UUID> store, final boolean requireOnCooldown,
        final ConditionListType type) {
      this.store = store;
      this.requireOnCooldown = requireOnCooldown;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> onCooldown(final CooldownStore<UUID> store) {
      if (store == null) {
        return NeverCondition.of();
      }
      return new CooldownConditions<>(store, true, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> notOnCooldown(final CooldownStore<UUID> store) {
      if (store == null) {
        return NeverCondition.of();
      }
      return new CooldownConditions<>(store, false, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final CooldownStore<UUID> store,
        final boolean requireOnCooldown,
        final ConditionListType type) {
      if (store == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new CooldownConditions<>(store, requireOnCooldown, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final boolean onCooldown = store.isOnCooldown(player.getUniqueId());
      return type.apply(onCooldown == requireOnCooldown);
    }
  }

  public static class CurrencyConditions<S extends CommandSender> implements Condition<S> {
    public enum CompareOp {
      EQ, NE, GT, GE, LT, LE
    }

    private final double target;
    private final CompareOp op;
    private final ConditionListType type;

    private CurrencyConditions(final double target, final CompareOp op, final ConditionListType type) {
      this.target = target;
      this.op = op == null ? CompareOp.GE : op;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> atLeast(final double amount) {
      return new CurrencyConditions<>(amount, CompareOp.GE, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final double target, final CompareOp op,
        final ConditionListType type) {
      return new CurrencyConditions<>(target, op, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final Double balance = getBalance(player);
      if (balance == null) {
        return false;
      }
      final boolean matches;
      switch (op) {
        case EQ:
          matches = balance == target;
          break;
        case NE:
          matches = balance != target;
          break;
        case GT:
          matches = balance > target;
          break;
        case GE:
          matches = balance >= target;
          break;
        case LT:
          matches = balance < target;
          break;
        case LE:
          matches = balance <= target;
          break;
        default:
          matches = balance >= target;
          break;
      }
      return type.apply(matches);
    }

    private static Double getBalance(final Player player) {
      try {
        final RegisteredServiceProvider<?> provider = Bukkit.getServicesManager()
            .getRegistration(Class.forName("net.milkbowl.vault.economy.Economy"));
        if (provider == null) {
          return null;
        }
        final Object economy = provider.getProvider();
        final Object result = economy.getClass()
            .getMethod("getBalance", Class.forName("org.bukkit.OfflinePlayer"))
            .invoke(economy, player);
        if (result instanceof Number) {
          return ((Number) result).doubleValue();
        }
      } catch (final Exception ignored) {
        // Vault economy is optional
      }
      return null;
    }
  }

  public static class PlaceholderConditions<S extends CommandSender> implements Condition<S> {
    public enum CompareOp {
      EQUALS, EQUALS_IGNORE_CASE, CONTAINS, STARTS_WITH, ENDS_WITH, REGEX, NOT_EQUALS, GT, GE, LT, LE
    }

    private static volatile java.lang.reflect.Method PAPI_SET_PLACEHOLDERS;

    private final String placeholder;
    private final String expected;
    private final CompareOp op;
    private final ConditionListType type;
    private final Pattern regexPattern;

    private PlaceholderConditions(final String placeholder, final String expected, final CompareOp op,
        final ConditionListType type) {
      this.placeholder = placeholder;
      this.expected = expected == null ? "" : expected;
      this.op = op == null ? CompareOp.EQUALS : op;
      this.type = type == null ? ConditionListType.WHITELIST : type;
      Pattern compiled = null;
      if (this.op == CompareOp.REGEX && this.expected != null && !this.expected.isEmpty()) {
        try {
          compiled = Pattern.compile(this.expected);
        } catch (final Exception e) {
          compiled = null;
        }
      }
      this.regexPattern = compiled;
    }

    public static <S extends CommandSender> Condition<S> equals(final String placeholder, final String expected) {
      if (placeholder == null || placeholder.isEmpty()) {
        return NeverCondition.of();
      }
      return new PlaceholderConditions<>(placeholder, expected, CompareOp.EQUALS, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final String placeholder, final String expected,
        final CompareOp op,
        final ConditionListType type) {
      if (placeholder == null || placeholder.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new PlaceholderConditions<>(placeholder, expected, op, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final String resolved = resolve(player, placeholder);
      boolean matches;
      switch (op) {
        case EQUALS_IGNORE_CASE:
          matches = resolved.equalsIgnoreCase(expected);
          break;
        case CONTAINS:
          matches = resolved.contains(expected);
          break;
        case STARTS_WITH:
          matches = resolved.startsWith(expected);
          break;
        case ENDS_WITH:
          matches = resolved.endsWith(expected);
          break;
        case REGEX:
          if (regexPattern == null) {
            matches = false;
          } else {
            try {
              matches = regexPattern.matcher(resolved).find();
            } catch (final Exception e) {
              matches = false;
            }
          }
          break;
        case NOT_EQUALS:
          matches = !resolved.equals(expected);
          break;
        case GT:
        case GE:
        case LT:
        case LE:
          matches = compareNumeric(resolved, expected, op);
          break;
        case EQUALS:
        default:
          matches = resolved.equals(expected);
          break;
      }
      return type.apply(matches);
    }

    private static boolean compareNumeric(final String left, final String right, final CompareOp op) {
      try {
        final double l = Double.parseDouble(left.trim());
        final double r = Double.parseDouble(right.trim());
        switch (op) {
          case GT:
            return l > r;
          case GE:
            return l >= r;
          case LT:
            return l < r;
          case LE:
            return l <= r;
          default:
            return false;
        }
      } catch (final Exception ignored) {
        return false;
      }
    }

    private static String resolve(final Player player, final String input) {
      if (input == null) {
        return "";
      }
      if (!Plugins.hasPlaceholderAPI() || !Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        return input;
      }
      try {
        java.lang.reflect.Method m = PAPI_SET_PLACEHOLDERS;
        if (m == null) {
          synchronized (PlaceholderConditions.class) {
            m = PAPI_SET_PLACEHOLDERS;
            if (m == null) {
              final Class<?> papi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
              m = papi.getMethod("setPlaceholders", OfflinePlayer.class, String.class);
              PAPI_SET_PLACEHOLDERS = m;
            }
          }
        }
        final Object out = m.invoke(null, player, input);
        return out == null ? "" : String.valueOf(out);
      } catch (final Throwable ignored) {
        return input;
      }
    }
  }

  public static class PotionEffectConditions<S extends CommandSender> implements Condition<S> {
    private final Set<PotionEffectType> effects;
    private final Integer minAmplifier;
    private final Integer minDuration;
    private final ConditionListType type;

    private PotionEffectConditions(final Set<PotionEffectType> effects, final Integer minAmplifier,
        final Integer minDuration, final ConditionListType type) {
      this.effects = effects == null ? new HashSet<>() : new HashSet<>(effects);
      this.minAmplifier = minAmplifier;
      this.minDuration = minDuration;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<PotionEffectType> effects) {
      if (effects == null || effects.isEmpty()) {
        return NeverCondition.of();
      }
      return new PotionEffectConditions<>(effects, null, null, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<PotionEffectType> effects) {
      if (effects == null || effects.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new PotionEffectConditions<>(effects, null, null, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<PotionEffectType> effects,
        final Integer minAmplifier,
        final Integer minDuration, final ConditionListType type) {
      final boolean empty = (effects == null || effects.isEmpty()) && minAmplifier == null && minDuration == null;
      if (empty) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new PotionEffectConditions<>(effects, minAmplifier, minDuration, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      boolean match = false;
      if (effects == null || effects.isEmpty()) {
        for (final PotionEffect effect : player.getActivePotionEffects()) {
          if (matches(effect)) {
            match = true;
            break;
          }
        }
      } else {
        for (final PotionEffectType effectType : effects) {
          if (effectType == null) {
            continue;
          }
          final PotionEffect effect = player.getPotionEffect(effectType);
          if (matches(effect)) {
            match = true;
            break;
          }
        }
      }
      return type.apply(match);
    }

    private boolean matches(final PotionEffect effect) {
      if (effect == null) {
        return false;
      }
      if (minAmplifier != null && effect.getAmplifier() < minAmplifier) {
        return false;
      }
      if (minDuration != null && effect.getDuration() < minDuration) {
        return false;
      }
      return true;
    }
  }

  public static class ExperienceConditions<S extends CommandSender> implements Condition<S> {
    public enum Mode {
      TOTAL, LEVEL_PROGRESS
    }

    private final Double min;
    private final Double max;
    private final Mode mode;
    private final ConditionListType type;

    private ExperienceConditions(final Double min, final Double max, final Mode mode, final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.mode = mode == null ? Mode.TOTAL : mode;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> totalRange(final double min, final double max) {
      return new ExperienceConditions<>(min, max, Mode.TOTAL, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> progressRange(final double min, final double max) {
      return new ExperienceConditions<>(min, max, Mode.LEVEL_PROGRESS, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Double min, final Double max, final Mode mode,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new ExperienceConditions<>(min, max, mode, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final double value = mode == Mode.LEVEL_PROGRESS ? player.getExp() : player.getTotalExperience();
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class AbsorptionConditions<S extends CommandSender> implements Condition<S> {
    private final Double min;
    private final Double max;
    private final ConditionListType type;

    private AbsorptionConditions(final Double min, final Double max, final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> range(final double min, final double max) {
      return new AbsorptionConditions<>(min, max, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> atLeast(final double min) {
      return new AbsorptionConditions<>(min, null, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Double min, final Double max,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new AbsorptionConditions<>(min, max, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final double value = player.getAbsorptionAmount();
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class SaturationConditions<S extends CommandSender> implements Condition<S> {
    private final Float min;
    private final Float max;
    private final ConditionListType type;

    private SaturationConditions(final Float min, final Float max, final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> range(final float min, final float max) {
      return new SaturationConditions<>(min, max, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Float min, final Float max,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new SaturationConditions<>(min, max, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final float value = player.getSaturation();
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class SleepingConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    private SleepingConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new SleepingConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new SleepingConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isSleeping() == expected);
    }
  }

  public static class RiptidingConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    private RiptidingConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new RiptidingConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new RiptidingConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isRiptiding() == expected);
    }
  }

  public static class BlockingConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expected;
    private final ConditionListType type;

    private BlockingConditions(final boolean expected, final ConditionListType type) {
      this.expected = expected;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final boolean expected) {
      return new BlockingConditions<>(expected, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expected, final ConditionListType type) {
      return new BlockingConditions<>(expected, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      return type.apply(player.isBlocking() == expected);
    }
  }

  public static class HeldItemDetailConditions<S extends CommandSender> implements Condition<S> {
    private final Set<Material> materials;
    private final HandSlot slot;
    private final String displayName;
    private final String displayNameContains;
    private final String loreContains;
    private final Integer customModelData;
    private final Map<Enchantment, Integer> minEnchantments;
    private final Integer minDurability;
    private final Integer maxDurability;
    private final ConditionListType type;

    private HeldItemDetailConditions(final Set<Material> materials, final HandSlot slot, final String displayName,
        final String displayNameContains, final String loreContains, final Integer customModelData,
        final Map<Enchantment, Integer> minEnchantments, final Integer minDurability, final Integer maxDurability,
        final ConditionListType type) {
      this.materials = materials == null ? new HashSet<>() : new HashSet<>(materials);
      this.slot = slot == null ? HandSlot.MAIN : slot;
      this.displayName = displayName;
      this.displayNameContains = displayNameContains;
      this.loreContains = loreContains;
      this.customModelData = customModelData;
      this.minEnchantments = minEnchantments == null ? Collections.emptyMap() : minEnchantments;
      this.minDurability = minDurability;
      this.maxDurability = maxDurability;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> from(final Set<Material> materials, final HandSlot slot,
        final String displayName, final String displayNameContains, final String loreContains,
        final Integer customModelData, final Map<Enchantment, Integer> minEnchantments, final Integer minDurability,
        final Integer maxDurability, final ConditionListType type) {
      final boolean empty = (materials == null || materials.isEmpty()) && displayName == null
          && displayNameContains == null && loreContains == null && customModelData == null
          && (minEnchantments == null || minEnchantments.isEmpty()) && minDurability == null && maxDurability == null;
      if (empty) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new HeldItemDetailConditions<>(materials, slot, displayName, displayNameContains, loreContains,
          customModelData, minEnchantments, minDurability, maxDurability, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getInventory() == null) {
        return false;
      }
      final ItemStack stack = slot == HandSlot.OFF ? player.getInventory().getItemInOffHand()
          : player.getInventory().getItemInMainHand();
      return type.apply(matches(stack));
    }

    @SuppressWarnings("deprecation")
    private boolean matches(final ItemStack stack) {
      if (stack == null || stack.getType() == Material.AIR) {
        return false;
      }
      if (materials != null && !materials.isEmpty() && !materials.contains(stack.getType())) {
        return false;
      }
      final ItemMeta meta = stack.getItemMeta();
      if (displayName != null) {
        final String name = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "";
        if (!name.equals(displayName)) {
          return false;
        }
      }
      if (displayNameContains != null) {
        final String name = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "";
        if (!name.contains(displayNameContains)) {
          return false;
        }
      }
      if (loreContains != null) {
        boolean found = false;
        if (meta != null && meta.hasLore() && meta.getLore() != null) {
          for (final String line : meta.getLore()) {
            if (line != null && line.contains(loreContains)) {
              found = true;
              break;
            }
          }
        }
        if (!found) {
          return false;
        }
      }
      if (customModelData != null) {
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != customModelData) {
          return false;
        }
      }
      if (minEnchantments != null && !minEnchantments.isEmpty()) {
        for (final Map.Entry<Enchantment, Integer> entry : minEnchantments.entrySet()) {
          if (entry == null || entry.getKey() == null) {
            continue;
          }
          final int level = stack.getEnchantmentLevel(entry.getKey());
          final int required = entry.getValue() == null ? 1 : entry.getValue();
          if (level < required) {
            return false;
          }
        }
      }
      if (minDurability != null || maxDurability != null) {
        final short maxType = stack.getType().getMaxDurability();
        if (maxType <= 0) {
          return false;
        }
        int damage = 0;
        final ItemMeta m = stack.getItemMeta();
        if (m instanceof Damageable) {
          damage = ((Damageable) m).getDamage();
        } else {
          try {
            damage = stack.getDurability();
          } catch (final Throwable ignored) {
            damage = 0;
          }
        }
        final int remaining = Math.max(0, maxType - damage);
        if (minDurability != null && remaining < minDurability) {
          return false;
        }
        if (maxDurability != null && remaining > maxDurability) {
          return false;
        }
      }
      return true;
    }
  }

  public static class SpecificSlotConditions<S extends CommandSender> implements Condition<S> {
    private final int slot;
    private final Set<Material> materials;
    private final boolean requireEmpty;
    private final ConditionListType type;

    private SpecificSlotConditions(final int slot, final Set<Material> materials, final boolean requireEmpty,
        final ConditionListType type) {
      this.slot = slot;
      this.materials = materials == null ? new HashSet<>() : new HashSet<>(materials);
      this.requireEmpty = requireEmpty;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> empty(final int slot) {
      return new SpecificSlotConditions<>(slot, Collections.emptySet(), true, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> materials(final int slot, final Set<Material> materials) {
      if (materials == null || materials.isEmpty()) {
        return NeverCondition.of();
      }
      return new SpecificSlotConditions<>(slot, materials, false, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final int slot, final Set<Material> materials,
        final boolean requireEmpty,
        final ConditionListType type) {
      if (!requireEmpty && (materials == null || materials.isEmpty())) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new SpecificSlotConditions<>(slot, materials, requireEmpty, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getInventory() == null) {
        return false;
      }
      if (slot >= player.getInventory().getSize()) {
        return false;
      }
      final ItemStack stack = player.getInventory().getItem(slot);
      final boolean empty = stack == null || stack.getType() == Material.AIR;
      if (requireEmpty) {
        return type.apply(empty);
      }
      return type.apply(!empty && materials.contains(stack.getType()));
    }
  }

  public static class InventoryFullConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expectedFull;
    private final ConditionListType type;

    private InventoryFullConditions(final boolean expectedFull, final ConditionListType type) {
      this.expectedFull = expectedFull;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> full() {
      return new InventoryFullConditions<>(true, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> notFull() {
      return new InventoryFullConditions<>(false, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expectedFull,
        final ConditionListType type) {
      return new InventoryFullConditions<>(expectedFull, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getInventory() == null) {
        return false;
      }
      boolean hasEmpty = false;
      final ItemStack[] storage = player.getInventory().getStorageContents();
      if (storage != null) {
        for (final ItemStack item : storage) {
          if (item == null || item.getType() == Material.AIR) {
            hasEmpty = true;
            break;
          }
        }
      }
      return type.apply(hasEmpty != expectedFull);
    }
  }

  public static class CoordinateConditions<S extends CommandSender> implements Condition<S> {
    private final Double minX;
    private final Double maxX;
    private final Double minY;
    private final Double maxY;
    private final Double minZ;
    private final Double maxZ;
    private final ConditionListType type;

    private CoordinateConditions(final Double minX, final Double maxX, final Double minY, final Double maxY,
        final Double minZ, final Double maxZ, final ConditionListType type) {
      this.minX = minX;
      this.maxX = maxX;
      this.minY = minY;
      this.maxY = maxY;
      this.minZ = minZ;
      this.maxZ = maxZ;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> from(final Double minX, final Double maxX, final Double minY,
        final Double maxY,
        final Double minZ, final Double maxZ, final ConditionListType type) {
      if (minX == null && maxX == null && minY == null && maxY == null && minZ == null && maxZ == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new CoordinateConditions<>(minX, maxX, minY, maxY, minZ, maxZ, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getLocation() == null) {
        return false;
      }
      final Location loc = player.getLocation();
      boolean inRange = true;
      inRange = inRange && inBound(loc.getX(), minX, maxX);
      inRange = inRange && inBound(loc.getY(), minY, maxY);
      inRange = inRange && inBound(loc.getZ(), minZ, maxZ);
      return type.apply(inRange);
    }

    private static boolean inBound(final double value, final Double min, final Double max) {
      if (min != null && value < min) {
        return false;
      }
      if (max != null && value > max) {
        return false;
      }
      return true;
    }
  }

  public static class DistanceConditions<S extends CommandSender> implements Condition<S> {
    private final Location origin;
    private final Double min;
    private final Double max;
    private final boolean sameWorld;
    private final boolean ignoreY;
    private final ConditionListType type;

    private DistanceConditions(final Location origin, final Double min, final Double max, final boolean sameWorld,
        final boolean ignoreY, final ConditionListType type) {
      this.origin = origin == null ? null : origin.clone();
      this.min = min;
      this.max = max;
      this.sameWorld = sameWorld;
      this.ignoreY = ignoreY;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> within(final Location origin, final double max) {
      if (origin == null) {
        return NeverCondition.of();
      }
      return new DistanceConditions<>(origin, null, max, true, false, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Location origin, final Double min, final Double max,
        final boolean sameWorld, final boolean ignoreY, final ConditionListType type) {
      if (origin == null || (min == null && max == null)) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new DistanceConditions<>(origin, min, max, sameWorld, ignoreY, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getLocation() == null) {
        return false;
      }
      final Location loc = player.getLocation();
      if (sameWorld) {
        if (origin.getWorld() == null || loc.getWorld() == null || !origin.getWorld().equals(loc.getWorld())) {
          return false;
        }
      }
      double dist;
      try {
        dist = ignoreY ? Math.hypot(loc.getX() - origin.getX(), loc.getZ() - origin.getZ())
            : loc.distance(origin);
      } catch (final IllegalArgumentException ex) {
        return false;
      }
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && dist >= min;
      }
      if (max != null) {
        inRange = inRange && dist <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class BlockAtLocationConditions<S extends CommandSender> implements Condition<S> {
    public enum Position {
      FEET, HEAD
    }

    private final Position position;
    private final Set<Material> materials;
    private final ConditionListType type;

    private BlockAtLocationConditions(final Position position, final Set<Material> materials,
        final ConditionListType type) {
      this.position = position == null ? Position.FEET : position;
      this.materials = materials == null ? new HashSet<>() : new HashSet<>(materials);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> feet(final Set<Material> materials) {
      if (materials == null || materials.isEmpty()) {
        return NeverCondition.of();
      }
      return new BlockAtLocationConditions<>(Position.FEET, materials, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> head(final Set<Material> materials) {
      if (materials == null || materials.isEmpty()) {
        return NeverCondition.of();
      }
      return new BlockAtLocationConditions<>(Position.HEAD, materials, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Position position, final Set<Material> materials,
        final ConditionListType type) {
      if (materials == null || materials.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new BlockAtLocationConditions<>(position, materials, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getLocation() == null) {
        return false;
      }
      final Location base = player.getLocation();
      final Location target = position == Position.HEAD ? base.clone().add(0, 1, 0) : base;
      final Block block = target.getBlock();
      if (block == null) {
        return false;
      }
      return type.apply(materials.contains(block.getType()));
    }
  }

  public static class StructureConditions<S extends CommandSender> implements Condition<S> {
    private final Set<String> structures;
    private final ConditionListType type;

    private StructureConditions(final Set<String> structures, final ConditionListType type) {
      this.structures = toLowerCaseSet(structures);
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> allowed(final Set<String> structures) {
      if (structures == null || structures.isEmpty()) {
        return NeverCondition.of();
      }
      return new StructureConditions<>(structures, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> denied(final Set<String> structures) {
      if (structures == null || structures.isEmpty()) {
        return AlwaysCondition.of();
      }
      return new StructureConditions<>(structures, ConditionListType.BLACKLIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Set<String> structures,
        final ConditionListType type) {
      if (structures == null || structures.isEmpty()) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new StructureConditions<>(structures, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getLocation() == null) {
        return false;
      }
      boolean match = false;
      try {
        final Set<String> present = getStructureKeysAt(player.getLocation());
        for (final String wanted : structures) {
          if (wanted != null && present.contains(wanted)) {
            match = true;
            break;
          }
        }
      } catch (final Throwable ignored) {
        return false;
      }
      return type.apply(match);
    }

    private static Set<String> getStructureKeysAt(final Location location) {
      final Set<String> result = new HashSet<>();
      if (location == null || location.getWorld() == null) {
        return result;
      }
      final World world = location.getWorld();
      final int x = location.getBlockX();
      final int y = location.getBlockY();
      final int z = location.getBlockZ();
      try {
        // Paper/modern: World#getStructures(int,int,int) or chunk-based APIs vary by
        // version.
        final Object chunk = world.getChunkAt(location);
        final java.lang.reflect.Method m = chunk.getClass().getMethod("getStructures");
        final Object col = m.invoke(chunk);
        if (col instanceof Collection) {
          for (final Object gs : (Collection<?>) col) {
            final Object structure = gs.getClass().getMethod("getStructure").invoke(gs);
            final Object key = structure.getClass().getMethod("getKey").invoke(structure);
            if (key != null) {
              result.add(key.toString().toLowerCase(Locale.ROOT));
              try {
                final Object k = key.getClass().getMethod("getKey").invoke(key);
                if (k != null) {
                  result.add(k.toString().toLowerCase(Locale.ROOT));
                }
              } catch (final Throwable ignored) {
              }
            }
          }
        }
      } catch (final Throwable ignored) {
        // API not available; fall through
      }
      try {
        final java.lang.reflect.Method m = world.getClass().getMethod("getStructures", int.class, int.class);
        final Object col = m.invoke(world, x >> 4, z >> 4);
        if (col instanceof Collection) {
          for (final Object gs : (Collection<?>) col) {
            try {
              final Object bb = gs.getClass().getMethod("getBoundingBox").invoke(gs);
              final java.lang.reflect.Method contains = bb.getClass().getMethod("contains", int.class, int.class,
                  int.class);
              final Object inside = contains.invoke(bb, x, y, z);
              if (!(inside instanceof Boolean) || !((Boolean) inside)) {
                continue;
              }
              final Object structure = gs.getClass().getMethod("getStructure").invoke(gs);
              final Object key = structure.getClass().getMethod("getKey").invoke(structure);
              if (key != null) {
                result.add(key.toString().toLowerCase(Locale.ROOT));
                try {
                  final Object k = key.getClass().getMethod("getKey").invoke(key);
                  if (k != null) {
                    result.add(k.toString().toLowerCase(Locale.ROOT));
                  }
                } catch (final Throwable ignored) {
                }
              }
            } catch (final Throwable ignored) {
            }
          }
        }
      } catch (final Throwable ignored) {
      }
      return result;
    }
  }

  public enum PlayerCountScope {
    SERVER, WORLD
  }

  public static class PlayerCountConditions<S extends CommandSender> implements Condition<S> {
    private final Integer min;
    private final Integer max;
    private final PlayerCountScope scope;
    private final ConditionListType type;

    private PlayerCountConditions(final Integer min, final Integer max, final PlayerCountScope scope,
        final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.scope = scope == null ? PlayerCountScope.SERVER : scope;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> server(final Integer min, final Integer max) {
      if (min == null && max == null) {
        return NeverCondition.of();
      }
      return new PlayerCountConditions<>(min, max, PlayerCountScope.SERVER, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> world(final Integer min, final Integer max) {
      if (min == null && max == null) {
        return NeverCondition.of();
      }
      return new PlayerCountConditions<>(min, max, PlayerCountScope.WORLD, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Integer min, final Integer max,
        final PlayerCountScope scope,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new PlayerCountConditions<>(min, max, scope, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null) {
        return false;
      }
      int count;
      if (scope == PlayerCountScope.WORLD) {
        if (!context.isPlayer() || context.player().getWorld() == null) {
          return false;
        }
        count = context.player().getWorld().getPlayers().size();
      } else {
        count = Bukkit.getOnlinePlayers().size();
      }
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && count >= min;
      }
      if (max != null) {
        inRange = inRange && count <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class NearbyEntitiesConditions<S extends CommandSender> implements Condition<S> {
    private final Double radius;
    private final Set<EntityType> entityTypes;
    private final Integer minCount;
    private final Integer maxCount;
    private final boolean includePlayers;
    private final boolean includeSelf;
    private final ConditionListType type;

    private NearbyEntitiesConditions(final Double radius, final Set<EntityType> entityTypes, final Integer minCount,
        final Integer maxCount, final boolean includePlayers, final boolean includeSelf,
        final ConditionListType type) {
      this.radius = radius;
      this.entityTypes = entityTypes == null ? new HashSet<>() : new HashSet<>(entityTypes);
      this.minCount = minCount;
      this.maxCount = maxCount;
      this.includePlayers = includePlayers;
      this.includeSelf = includeSelf;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> players(final double radius, final Integer minCount,
        final Integer maxCount) {
      if (minCount == null && maxCount == null) {
        return NeverCondition.of();
      }
      return new NearbyEntitiesConditions<>(radius, Collections.emptySet(), minCount, maxCount, true, false,
          ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Double radius, final Set<EntityType> entityTypes,
        final Integer minCount, final Integer maxCount, final boolean includePlayers, final boolean includeSelf,
        final ConditionListType type) {
      if (radius == null || (minCount == null && maxCount == null)) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new NearbyEntitiesConditions<>(radius, entityTypes, minCount, maxCount, includePlayers, includeSelf, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      if (player.getLocation() == null || player.getWorld() == null) {
        return false;
      }
      int count = 0;
      final Collection<Entity> nearby = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius,
          radius);
      for (final Entity entity : nearby) {
        if (entity == null) {
          continue;
        }
        if (!includeSelf && entity.getUniqueId().equals(player.getUniqueId())) {
          continue;
        }
        final boolean isPlayer = entity instanceof Player;
        if (isPlayer && !includePlayers) {
          continue;
        }
        if (!isPlayer && entityTypes != null && !entityTypes.isEmpty() && !entityTypes.contains(entity.getType())) {
          continue;
        }
        count++;
      }
      boolean inRange = true;
      if (minCount != null) {
        inRange = inRange && count >= minCount;
      }
      if (maxCount != null) {
        inRange = inRange && count <= maxCount;
      }
      return type.apply(inRange);
    }
  }

  public static class PlaytimeConditions<S extends CommandSender> implements Condition<S> {
    public enum Unit {
      TICKS, SECONDS, MINUTES
    }

    private final Long min;
    private final Long max;
    private final Unit unit;
    private final ConditionListType type;

    private PlaytimeConditions(final Long min, final Long max, final Unit unit, final ConditionListType type) {
      this.min = min;
      this.max = max;
      this.unit = unit == null ? Unit.TICKS : unit;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> atLeast(final long value, final Unit unit) {
      return new PlaytimeConditions<>(value, null, unit, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final Long min, final Long max, final Unit unit,
        final ConditionListType type) {
      if (min == null && max == null) {
        return type == ConditionListType.WHITELIST ? NeverCondition.of() : AlwaysCondition.of();
      }
      return new PlaytimeConditions<>(min, max, unit, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      long ticks;
      try {
        ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
      } catch (final Throwable t) {
        try {
          ticks = player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK"));
        } catch (final Throwable ignored) {
          return false;
        }
      }
      long value = ticks;
      if (unit == Unit.SECONDS) {
        value = ticks / 20L;
      } else if (unit == Unit.MINUTES) {
        value = ticks / (20L * 60L);
      }
      boolean inRange = true;
      if (min != null) {
        inRange = inRange && value >= min;
      }
      if (max != null) {
        inRange = inRange && value <= max;
      }
      return type.apply(inRange);
    }
  }

  public static class FirstJoinConditions<S extends CommandSender> implements Condition<S> {
    private final boolean expectedFirstJoin;
    private final ConditionListType type;

    private FirstJoinConditions(final boolean expectedFirstJoin, final ConditionListType type) {
      this.expectedFirstJoin = expectedFirstJoin;
      this.type = type == null ? ConditionListType.WHITELIST : type;
    }

    public static <S extends CommandSender> Condition<S> firstJoin() {
      return new FirstJoinConditions<>(true, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> returningPlayer() {
      return new FirstJoinConditions<>(false, ConditionListType.WHITELIST);
    }

    public static <S extends CommandSender> Condition<S> from(final boolean expectedFirstJoin,
        final ConditionListType type) {
      return new FirstJoinConditions<>(expectedFirstJoin, type);
    }

    @Override
    public boolean test(final ConditionContext<S> context) {
      if (context == null || !context.isPlayer()) {
        return false;
      }
      final Player player = context.player();
      final boolean isFirst = !player.hasPlayedBefore() || player.getFirstPlayed() == 0;
      return type.apply(isFirst == expectedFirstJoin);
    }
  }

  // -------------------- helpers --------------------

  private static Set<String> toLowerCaseSet(final Set<String> input) {
    if (input == null || input.isEmpty()) {
      return new HashSet<>();
    }
    final HashSet<String> out = new HashSet<>(input.size());
    for (final String s : input) {
      if (s != null) {
        out.add(s.toLowerCase(Locale.ROOT));
      }
    }
    return out;
  }
}
