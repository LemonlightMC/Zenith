package com.lemonlightmc.zenith.utils.actions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.time.TimeRange;
import com.lemonlightmc.zenith.math.Region;
import com.lemonlightmc.zenith.utils.CooldownStore;
import com.lemonlightmc.zenith.utils.Plugins;

public class Conditions {

  public static enum ConditionValueType {
    WHITELIST(false),
    BLACKLIST(true);

    private final boolean negate;

    private ConditionValueType(final boolean negate) {
      this.negate = negate;
    }

    public boolean apply(final boolean value) {
      return negate ? !value : value;
    }
  }

  // -------------------- Existing conditions

  public static class WorldConditions implements Condition<Player> {

    private final Set<String> worlds;
    private final ConditionValueType type;

    public WorldConditions(final Set<String> worlds, final ConditionValueType type) {
      this.worlds = toLowerCaseSet(worlds);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static WorldConditions allowed(final Set<String> allowed_worlds) {
      return new WorldConditions(allowed_worlds, ConditionValueType.WHITELIST);
    }

    public static WorldConditions denied(final Set<String> denied_worlds) {
      return new WorldConditions(denied_worlds, ConditionValueType.BLACKLIST);
    }

    public static WorldConditions from(final Set<String> worlds, final ConditionValueType type) {
      return new WorldConditions(worlds, type);
    }

    @Override
    public boolean isEmpty() {
      return worlds == null || worlds.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || isEmpty()) {
        return false;
      }
      final String w = player.getWorld().getName().toLowerCase();
      return type.apply(worlds.contains(w));
    }
  }

  public static class BiomeConditions implements Condition<Player> {

    private final Set<String> biomes;
    private final ConditionValueType type;

    public BiomeConditions(final Set<String> biomes, final ConditionValueType type) {
      this.biomes = toLowerCaseSet(biomes);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static BiomeConditions allowed(final Set<String> allowed_biomes) {
      return new BiomeConditions(allowed_biomes, ConditionValueType.WHITELIST);
    }

    public static BiomeConditions denied(final Set<String> denied_biomes) {
      return new BiomeConditions(denied_biomes, ConditionValueType.BLACKLIST);
    }

    public static BiomeConditions from(final Set<String> biomes, final ConditionValueType type) {
      return new BiomeConditions(biomes, type);
    }

    @Override
    public boolean isEmpty() {
      return biomes == null || biomes.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || player.getLocation() == null || isEmpty()) {
        return false;
      }
      try {
        final Biome biome = player.getWorld().getBiome(player.getLocation());
        final String b = biome == null ? "" : biome.toString().toLowerCase();
        final String key = biome != null && biome.getKey() != null ? biome.getKey().toString().toLowerCase() : "";
        final String keyOnly = biome != null && biome.getKey() != null ? biome.getKey().getKey().toLowerCase() : "";
        return type.apply(biomes.contains(b) || biomes.contains(key) || biomes.contains(keyOnly));
      } catch (final Throwable ignored) {
        return false;
      }
    }
  }

  public static class WeatherConditions implements Condition<Player> {

    private final Set<String> weathers;
    private final ConditionValueType type;

    public WeatherConditions(final Set<String> weathers, final ConditionValueType type) {
      this.weathers = toLowerCaseSet(weathers);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static WeatherConditions allowed(final Set<String> allowed_weathers) {
      return new WeatherConditions(allowed_weathers, ConditionValueType.WHITELIST);
    }

    public static WeatherConditions denied(final Set<String> denied_weathers) {
      return new WeatherConditions(denied_weathers, ConditionValueType.BLACKLIST);
    }

    public static WeatherConditions from(final Set<String> weathers, final ConditionValueType type) {
      return new WeatherConditions(weathers, type);
    }

    @Override
    public boolean isEmpty() {
      return weathers == null || weathers.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || isEmpty()) {
        return false;
      }
      final World world = player.getWorld();
      final String now = world.isThundering() ? "thunder" : (world.hasStorm() ? "rain" : "clear");
      return type.apply(weathers.contains(now));
    }
  }

  public static class MoonConditions implements Condition<Player> {

    private final Set<Integer> moonPhases;
    private final ConditionValueType type;

    public MoonConditions(final Set<Integer> moonPhases, final ConditionValueType type) {
      this.moonPhases = moonPhases == null ? new HashSet<>() : new HashSet<>(moonPhases);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static MoonConditions allowed(final Set<Integer> moonPhases) {
      return new MoonConditions(moonPhases, ConditionValueType.WHITELIST);
    }

    public static MoonConditions denied(final Set<Integer> moonPhases) {
      return new MoonConditions(moonPhases, ConditionValueType.BLACKLIST);
    }

    public static MoonConditions from(final Set<Integer> moonPhases, final ConditionValueType type) {
      return new MoonConditions(moonPhases, type);
    }

    @Override
    public boolean isEmpty() {
      return moonPhases == null || moonPhases.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || isEmpty()) {
        return false;
      }
      final int value = getMoonPhase(player);
      return type.apply(moonPhases.contains(value));
    }

    private static int getMoonPhase(final Player player) {
      final long day = player.getWorld().getFullTime() / 24000L;
      return (int) Math.floorMod(day, 8);
    }
  }

  public static class TimeRangeConditions implements Condition<Player> {

    private final Set<TimeRange> timeRanges;
    private final ConditionValueType type;

    public TimeRangeConditions(final Set<TimeRange> timeRanges, final ConditionValueType type) {
      this.timeRanges = timeRanges == null ? new HashSet<>() : new HashSet<>(timeRanges);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static TimeRangeConditions allowed(final Set<TimeRange> timeRanges) {
      return new TimeRangeConditions(timeRanges, ConditionValueType.WHITELIST);
    }

    public static TimeRangeConditions denied(final Set<TimeRange> timeRanges) {
      return new TimeRangeConditions(timeRanges, ConditionValueType.BLACKLIST);
    }

    public static TimeRangeConditions from(final Set<TimeRange> timeRanges, final ConditionValueType type) {
      return new TimeRangeConditions(timeRanges, type);
    }

    @Override
    public boolean isEmpty() {
      return timeRanges == null || timeRanges.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || isEmpty()) {
        return false;
      }
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

  public static class AdvancementConditions implements Condition<Player> {

    private final Set<String> advancements;
    private final ConditionValueType type;

    public AdvancementConditions(final Set<String> advancements, final ConditionValueType type) {
      this.advancements = advancements == null ? new HashSet<>() : new HashSet<>(advancements);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static AdvancementConditions allowed(final Set<String> advancements) {
      return new AdvancementConditions(advancements, ConditionValueType.WHITELIST);
    }

    public static AdvancementConditions denied(final Set<String> advancements) {
      return new AdvancementConditions(advancements, ConditionValueType.BLACKLIST);
    }

    public static AdvancementConditions from(final Set<String> advancements, final ConditionValueType type) {
      return new AdvancementConditions(advancements, type);
    }

    @Override
    public boolean isEmpty() {
      return advancements == null || advancements.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
      boolean value = true;
      boolean evaluated = false;
      for (final String adv : advancements) {
        if (adv == null || adv.isEmpty())
          continue;
        final NamespacedKey key = adv.indexOf(':') >= 0 ? NamespacedKey.fromString(adv)
            : NamespacedKey.minecraft(adv);
        if (key == null)
          continue;
        final Advancement a = Bukkit.getAdvancement(key);
        if (a == null)
          continue;
        final AdvancementProgress prog = player.getAdvancementProgress(a);
        value = value && type.apply(prog.isDone());
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public static class RecipeConditions implements Condition<Player> {

    private final Set<String> recipes;
    private final ConditionValueType type;

    public RecipeConditions(final Set<String> recipes, final ConditionValueType type) {
      this.recipes = recipes == null ? new HashSet<>() : new HashSet<>(recipes);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static RecipeConditions allowed(final Set<String> recipes) {
      return new RecipeConditions(recipes, ConditionValueType.WHITELIST);
    }

    public static RecipeConditions denied(final Set<String> recipes) {
      return new RecipeConditions(recipes, ConditionValueType.BLACKLIST);
    }

    public static RecipeConditions from(final Set<String> recipes, final ConditionValueType type) {
      return new RecipeConditions(recipes, type);
    }

    @Override
    public boolean isEmpty() {
      return recipes == null || recipes.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
      boolean value = true;
      boolean evaluated = false;
      for (final String recipe : recipes) {
        if (recipe == null || recipe.isEmpty())
          continue;
        final NamespacedKey key = recipe.indexOf(':') >= 0 ? NamespacedKey.fromString(recipe)
            : NamespacedKey.minecraft(recipe);
        if (key == null)
          continue;
        value = value && type.apply(player.hasDiscoveredRecipe(key));
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public static class PluginConditions implements Condition<CommandSender> {

    private final Set<String> plugins;
    private final ConditionValueType type;

    public PluginConditions(final Set<String> plugins, final ConditionValueType type) {
      this.plugins = plugins == null ? new HashSet<>() : new HashSet<>(plugins);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static PluginConditions allowed(final Set<String> plugins) {
      return new PluginConditions(plugins, ConditionValueType.WHITELIST);
    }

    public static PluginConditions denied(final Set<String> plugins) {
      return new PluginConditions(plugins, ConditionValueType.BLACKLIST);
    }

    public static PluginConditions from(final Set<String> plugins, final ConditionValueType type) {
      return new PluginConditions(plugins, type);
    }

    @Override
    public boolean isEmpty() {
      return plugins == null || plugins.isEmpty();
    }

    @Override
    public boolean test(final CommandSender sender) {
      if (isEmpty()) {
        return false;
      }
      boolean value = true;
      boolean evaluated = false;
      for (final String plugin : plugins) {
        if (plugin == null || plugin.isEmpty())
          continue;
        final boolean isEnabled = ZenithProvider.instance().getPluginManager().isPluginEnabled(plugin);
        value = value && type.apply(isEnabled);
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public static class PermissionConditions implements Condition<CommandSender> {

    private final Set<String> permissions;
    private final ConditionValueType type;

    public PermissionConditions(final Set<String> permissions, final ConditionValueType type) {
      this.permissions = permissions == null ? new HashSet<>() : new HashSet<>(permissions);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static PermissionConditions allowed(final Set<String> permissions) {
      return new PermissionConditions(permissions, ConditionValueType.WHITELIST);
    }

    public static PermissionConditions denied(final Set<String> permissions) {
      return new PermissionConditions(permissions, ConditionValueType.BLACKLIST);
    }

    public static PermissionConditions from(final Set<String> permissions, final ConditionValueType type) {
      return new PermissionConditions(permissions, type);
    }

    @Override
    public boolean isEmpty() {
      return permissions == null || permissions.isEmpty();
    }

    @Override
    public boolean test(final CommandSender sender) {
      if (sender == null || isEmpty()) {
        return false;
      }
      boolean value = true;
      boolean evaluated = false;
      for (final String permission : permissions) {
        if (permission == null || permission.isEmpty())
          continue;
        final boolean hasPerm = sender.hasPermission(permission);
        value = value && type.apply(hasPerm);
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  // -------------------- New requested conditions --------------------

  public static class GameModeConditions implements Condition<Player> {
    private final Set<GameMode> modes;
    private final ConditionValueType type;

    public GameModeConditions(final Set<GameMode> modes, final ConditionValueType type) {
      this.modes = modes == null ? new HashSet<>() : new HashSet<>(modes);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static GameModeConditions allowed(final Set<GameMode> modes) {
      return new GameModeConditions(modes, ConditionValueType.WHITELIST);
    }

    public static GameModeConditions denied(final Set<GameMode> modes) {
      return new GameModeConditions(modes, ConditionValueType.BLACKLIST);
    }

    public static GameModeConditions from(final Set<GameMode> modes, final ConditionValueType type) {
      return new GameModeConditions(modes, type);
    }

    @Override
    public boolean isEmpty() {
      return modes == null || modes.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
      return type.apply(modes.contains(player.getGameMode()));
    }
  }

  public static class HealthConditions implements Condition<Player> {
    private final Double min;
    private final Double max;
    private final boolean percent;
    private final ConditionValueType type;

    public HealthConditions(final Double min, final Double max, final boolean percent, final ConditionValueType type) {
      this.min = min;
      this.max = max;
      this.percent = percent;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static HealthConditions range(final double min, final double max) {
      return new HealthConditions(min, max, false, ConditionValueType.WHITELIST);
    }

    public static HealthConditions percentRange(final double min, final double max) {
      return new HealthConditions(min, max, true, ConditionValueType.WHITELIST);
    }

    public static HealthConditions from(final Double min, final Double max, final boolean percent,
        final ConditionValueType type) {
      return new HealthConditions(min, max, percent, type);
    }

    @Override
    public boolean isEmpty() {
      return min == null && max == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
      double value = player.getHealth();
      if (percent) {
        final double maxHealth = Math.max(1.0, player.getMaxHealth());
        value = (value / maxHealth) * 100.0;
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

  public static class LevelConditions implements Condition<Player> {
    private final Integer min;
    private final Integer max;
    private final ConditionValueType type;

    public LevelConditions(final Integer min, final Integer max, final ConditionValueType type) {
      this.min = min;
      this.max = max;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static LevelConditions range(final int min, final int max) {
      return new LevelConditions(min, max, ConditionValueType.WHITELIST);
    }

    public static LevelConditions atLeast(final int min) {
      return new LevelConditions(min, null, ConditionValueType.WHITELIST);
    }

    public static LevelConditions from(final Integer min, final Integer max, final ConditionValueType type) {
      return new LevelConditions(min, max, type);
    }

    @Override
    public boolean isEmpty() {
      return min == null && max == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
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

  public static class FoodConditions implements Condition<Player> {
    private final Integer min;
    private final Integer max;
    private final ConditionValueType type;

    public FoodConditions(final Integer min, final Integer max, final ConditionValueType type) {
      this.min = min;
      this.max = max;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static FoodConditions range(final int min, final int max) {
      return new FoodConditions(min, max, ConditionValueType.WHITELIST);
    }

    public static FoodConditions from(final Integer min, final Integer max, final ConditionValueType type) {
      return new FoodConditions(min, max, type);
    }

    @Override
    public boolean isEmpty() {
      return min == null && max == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
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

  public static class FlightConditions implements Condition<Player> {
    private final Boolean allowFlight;
    private final Boolean flying;
    private final ConditionValueType type;

    public FlightConditions(final Boolean allowFlight, final Boolean flying, final ConditionValueType type) {
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static FlightConditions allowed(final Boolean allowFlight, final Boolean flying) {
      return new FlightConditions(allowFlight, flying, ConditionValueType.WHITELIST);
    }

    public static FlightConditions denied(final Boolean allowFlight, final Boolean flying) {
      return new FlightConditions(allowFlight, flying, ConditionValueType.BLACKLIST);
    }

    public static FlightConditions from(final Boolean allowFlight, final Boolean flying,
        final ConditionValueType type) {
      return new FlightConditions(allowFlight, flying, type);
    }

    @Override
    public boolean isEmpty() {
      return allowFlight == null && flying == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
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

  public static class InvulnerableConditions implements Condition<Player> {
    private final boolean expected;
    private final ConditionValueType type;

    public InvulnerableConditions(final boolean expected, final ConditionValueType type) {
      this.expected = expected;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static InvulnerableConditions allowed(final boolean expected) {
      return new InvulnerableConditions(expected, ConditionValueType.WHITELIST);
    }

    public static InvulnerableConditions denied(final boolean expected) {
      return new InvulnerableConditions(expected, ConditionValueType.BLACKLIST);
    }

    public static InvulnerableConditions from(final boolean expected, final ConditionValueType type) {
      return new InvulnerableConditions(expected, type);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null) {
        return false;
      }
      return type.apply(player.isInvulnerable() == expected);
    }
  }

  /** Alias for common misspelling. */
  public static class InvurableConditions extends InvulnerableConditions {
    public InvurableConditions(final boolean expected, final ConditionValueType type) {
      super(expected, type);
    }
  }

  public static class SneakingConditions implements Condition<Player> {
    private final boolean expected;
    private final ConditionValueType type;

    public SneakingConditions(final boolean expected, final ConditionValueType type) {
      this.expected = expected;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static SneakingConditions allowed(final boolean expected) {
      return new SneakingConditions(expected, ConditionValueType.WHITELIST);
    }

    public static SneakingConditions from(final boolean expected, final ConditionValueType type) {
      return new SneakingConditions(expected, type);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null) {
        return false;
      }
      return type.apply(player.isSneaking() == expected);
    }
  }

  public static class SprintingConditions implements Condition<Player> {
    private final boolean expected;
    private final ConditionValueType type;

    public SprintingConditions(final boolean expected, final ConditionValueType type) {
      this.expected = expected;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static SprintingConditions allowed(final boolean expected) {
      return new SprintingConditions(expected, ConditionValueType.WHITELIST);
    }

    public static SprintingConditions from(final boolean expected, final ConditionValueType type) {
      return new SprintingConditions(expected, type);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null) {
        return false;
      }
      return type.apply(player.isSprinting() == expected);
    }
  }

  public static class SwimmingConditions implements Condition<Player> {
    private final boolean expected;
    private final ConditionValueType type;

    public SwimmingConditions(final boolean expected, final ConditionValueType type) {
      this.expected = expected;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static SwimmingConditions allowed(final boolean expected) {
      return new SwimmingConditions(expected, ConditionValueType.WHITELIST);
    }

    public static SwimmingConditions from(final boolean expected, final ConditionValueType type) {
      return new SwimmingConditions(expected, type);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null) {
        return false;
      }
      return type.apply(player.isSwimming() == expected);
    }
  }

  public static class GlidingConditions implements Condition<Player> {
    private final boolean expected;
    private final ConditionValueType type;

    public GlidingConditions(final boolean expected, final ConditionValueType type) {
      this.expected = expected;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static GlidingConditions allowed(final boolean expected) {
      return new GlidingConditions(expected, ConditionValueType.WHITELIST);
    }

    public static GlidingConditions from(final boolean expected, final ConditionValueType type) {
      return new GlidingConditions(expected, type);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null) {
        return false;
      }
      return type.apply(player.isGliding() == expected);
    }
  }

  public static class GameRulesConditions implements Condition<Player> {
    private final Map<String, String> expectedRules;
    private final ConditionValueType type;

    public GameRulesConditions(final Map<String, String> expectedRules, final ConditionValueType type) {
      this.expectedRules = expectedRules == null ? Collections.emptyMap() : expectedRules;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static GameRulesConditions allowed(final Map<String, String> expectedRules) {
      return new GameRulesConditions(expectedRules, ConditionValueType.WHITELIST);
    }

    public static GameRulesConditions denied(final Map<String, String> expectedRules) {
      return new GameRulesConditions(expectedRules, ConditionValueType.BLACKLIST);
    }

    public static GameRulesConditions from(final Map<String, String> expectedRules, final ConditionValueType type) {
      return new GameRulesConditions(expectedRules, type);
    }

    @Override
    public boolean isEmpty() {
      return expectedRules == null || expectedRules.isEmpty();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || isEmpty()) {
        return false;
      }
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
        evaluated = true;
      }
      return evaluated && value;
    }
  }

  public enum HandSlot {
    MAIN, OFF
  }

  public static class ItemInHandConditions implements Condition<Player> {
    private final Set<Material> materials;
    private final HandSlot slot;
    private final ConditionValueType type;

    public ItemInHandConditions(final Set<Material> materials, final HandSlot slot, final ConditionValueType type) {
      this.materials = materials == null ? new HashSet<>() : new HashSet<>(materials);
      this.slot = slot == null ? HandSlot.MAIN : slot;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static ItemInHandConditions allowed(final Set<Material> materials, final HandSlot slot) {
      return new ItemInHandConditions(materials, slot, ConditionValueType.WHITELIST);
    }

    public static ItemInHandConditions denied(final Set<Material> materials, final HandSlot slot) {
      return new ItemInHandConditions(materials, slot, ConditionValueType.BLACKLIST);
    }

    public static ItemInHandConditions from(final Set<Material> materials, final HandSlot slot,
        final ConditionValueType type) {
      return new ItemInHandConditions(materials, slot, type);
    }

    @Override
    public boolean isEmpty() {
      return materials == null || materials.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getInventory() == null || isEmpty()) {
        return false;
      }
      final ItemStack stack = slot == HandSlot.OFF ? player.getInventory().getItemInOffHand()
          : player.getInventory().getItemInMainHand();
      final Material mat = stack == null ? Material.AIR : stack.getType();
      return type.apply(materials.contains(mat));
    }
  }

  public static class ArmorConditions implements Condition<Player> {
    private final Map<EquipmentSlot, Set<Material>> armor;
    private final ConditionValueType type;

    public ArmorConditions(final Map<EquipmentSlot, Set<Material>> armor, final ConditionValueType type) {
      this.armor = armor == null ? Collections.emptyMap() : armor;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static ArmorConditions allowed(final Map<EquipmentSlot, Set<Material>> armor) {
      return new ArmorConditions(armor, ConditionValueType.WHITELIST);
    }

    public static ArmorConditions denied(final Map<EquipmentSlot, Set<Material>> armor) {
      return new ArmorConditions(armor, ConditionValueType.BLACKLIST);
    }

    public static ArmorConditions from(final Map<EquipmentSlot, Set<Material>> armor, final ConditionValueType type) {
      return new ArmorConditions(armor, type);
    }

    @Override
    public boolean isEmpty() {
      return armor == null || armor.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getEquipment() == null || isEmpty()) {
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

  public static class EmptySlotConditions implements Condition<Player> {
    private final Integer minEmptySlots;
    private final ConditionValueType type;

    public EmptySlotConditions(final Integer minEmptySlots, final ConditionValueType type) {
      this.minEmptySlots = minEmptySlots;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static EmptySlotConditions atLeast(final int minEmptySlots) {
      return new EmptySlotConditions(minEmptySlots, ConditionValueType.WHITELIST);
    }

    public static EmptySlotConditions from(final Integer minEmptySlots, final ConditionValueType type) {
      return new EmptySlotConditions(minEmptySlots, type);
    }

    @Override
    public boolean isEmpty() {
      return minEmptySlots == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getInventory() == null || isEmpty()) {
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

  public static class InventoryContainsConditions implements Condition<Player> {
    private final Material material;
    private final int minAmount;
    private final ConditionValueType type;

    public InventoryContainsConditions(final Material material, final int minAmount, final ConditionValueType type) {
      this.material = material;
      this.minAmount = Math.max(1, minAmount);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static InventoryContainsConditions atLeast(final Material material, final int minAmount) {
      return new InventoryContainsConditions(material, minAmount, ConditionValueType.WHITELIST);
    }

    public static InventoryContainsConditions from(final Material material, final int minAmount,
        final ConditionValueType type) {
      return new InventoryContainsConditions(material, minAmount, type);
    }

    @Override
    public boolean isEmpty() {
      return material == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getInventory() == null || isEmpty()) {
        return false;
      }
      return type.apply(player.getInventory().contains(material, minAmount));
    }
  }

  public static class RegionConditions implements Condition<Player> {
    private final Set<Region> regions;
    private final ConditionValueType type;

    public RegionConditions(final Set<Region> regions, final ConditionValueType type) {
      this.regions = regions == null ? new HashSet<>() : new HashSet<>(regions);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static RegionConditions allowed(final Set<Region> regions) {
      return new RegionConditions(regions, ConditionValueType.WHITELIST);
    }

    public static RegionConditions denied(final Set<Region> regions) {
      return new RegionConditions(regions, ConditionValueType.BLACKLIST);
    }

    public static RegionConditions from(final Set<Region> regions, final ConditionValueType type) {
      return new RegionConditions(regions, type);
    }

    @Override
    public boolean isEmpty() {
      return regions == null || regions.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getLocation() == null || isEmpty()) {
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

  public static class LightLevelConditions implements Condition<Player> {
    public enum LightType {
      BLOCK, SKY, MAX
    }

    private final Integer min;
    private final Integer max;
    private final LightType lightType;
    private final ConditionValueType type;

    public LightLevelConditions(final Integer min, final Integer max, final LightType lightType,
        final ConditionValueType type) {
      this.min = min;
      this.max = max;
      this.lightType = lightType == null ? LightType.BLOCK : lightType;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static LightLevelConditions range(final int min, final int max, final LightType lightType) {
      return new LightLevelConditions(min, max, lightType, ConditionValueType.WHITELIST);
    }

    public static LightLevelConditions from(final Integer min, final Integer max, final LightType lightType,
        final ConditionValueType type) {
      return new LightLevelConditions(min, max, lightType, type);
    }

    @Override
    public boolean isEmpty() {
      return min == null && max == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getLocation() == null || player.getLocation().getBlock() == null || isEmpty()) {
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

  public static class DimensionConditions implements Condition<Player> {
    private final Set<World.Environment> environments;
    private final ConditionValueType type;

    public DimensionConditions(final Set<World.Environment> environments, final ConditionValueType type) {
      this.environments = environments == null ? new HashSet<>() : new HashSet<>(environments);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static DimensionConditions allowed(final Set<World.Environment> environments) {
      return new DimensionConditions(environments, ConditionValueType.WHITELIST);
    }

    public static DimensionConditions denied(final Set<World.Environment> environments) {
      return new DimensionConditions(environments, ConditionValueType.BLACKLIST);
    }

    public static DimensionConditions from(final Set<World.Environment> environments, final ConditionValueType type) {
      return new DimensionConditions(environments, type);
    }

    @Override
    public boolean isEmpty() {
      return environments == null || environments.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || isEmpty()) {
        return false;
      }
      return type.apply(environments.contains(player.getWorld().getEnvironment()));
    }
  }

  public static class DifficultyConditions implements Condition<Player> {
    private final Set<Difficulty> difficulties;
    private final ConditionValueType type;

    public DifficultyConditions(final Set<Difficulty> difficulties, final ConditionValueType type) {
      this.difficulties = difficulties == null ? new HashSet<>() : new HashSet<>(difficulties);
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static DifficultyConditions allowed(final Set<Difficulty> difficulties) {
      return new DifficultyConditions(difficulties, ConditionValueType.WHITELIST);
    }

    public static DifficultyConditions denied(final Set<Difficulty> difficulties) {
      return new DifficultyConditions(difficulties, ConditionValueType.BLACKLIST);
    }

    public static DifficultyConditions from(final Set<Difficulty> difficulties, final ConditionValueType type) {
      return new DifficultyConditions(difficulties, type);
    }

    @Override
    public boolean isEmpty() {
      return difficulties == null || difficulties.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || player.getWorld() == null || isEmpty()) {
        return false;
      }
      return type.apply(difficulties.contains(player.getWorld().getDifficulty()));
    }
  }

  public static class StatisticConditions implements Condition<Player> {
    public enum CompareOp {
      EQ, NE, GT, GE, LT, LE
    }

    private final Statistic statistic;
    private final Material material;
    private final EntityType entityType;
    private final int target;
    private final CompareOp op;
    private final ConditionValueType type;

    public StatisticConditions(final Statistic statistic, final Material material, final EntityType entityType,
        final int target, final CompareOp op, final ConditionValueType type) {
      this.statistic = statistic;
      this.material = material;
      this.entityType = entityType;
      this.target = target;
      this.op = op == null ? CompareOp.GE : op;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static StatisticConditions of(final Statistic statistic, final int target, final CompareOp op) {
      return new StatisticConditions(statistic, null, null, target, op, ConditionValueType.WHITELIST);
    }

    public static StatisticConditions of(final Statistic statistic, final Material material, final int target,
        final CompareOp op) {
      return new StatisticConditions(statistic, material, null, target, op, ConditionValueType.WHITELIST);
    }

    public static StatisticConditions of(final Statistic statistic, final EntityType entityType, final int target,
        final CompareOp op) {
      return new StatisticConditions(statistic, null, entityType, target, op, ConditionValueType.WHITELIST);
    }

    public static StatisticConditions from(final Statistic statistic, final Material material,
        final EntityType entityType, final int target, final CompareOp op, final ConditionValueType type) {
      return new StatisticConditions(statistic, material, entityType, target, op, type);
    }

    @Override
    public boolean isEmpty() {
      return statistic == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
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

  public static class ObjectiveScoreConditions implements Condition<Player> {
    private final String objectiveName;
    private final Integer min;
    private final Integer max;
    private final boolean usePlayerScoreboard;
    private final ConditionValueType type;

    public ObjectiveScoreConditions(final String objectiveName, final Integer min, final Integer max,
        final boolean usePlayerScoreboard, final ConditionValueType type) {
      this.objectiveName = objectiveName;
      this.min = min;
      this.max = max;
      this.usePlayerScoreboard = usePlayerScoreboard;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static ObjectiveScoreConditions range(final String objectiveName, final Integer min, final Integer max) {
      return new ObjectiveScoreConditions(objectiveName, min, max, true, ConditionValueType.WHITELIST);
    }

    public static ObjectiveScoreConditions from(final String objectiveName, final Integer min, final Integer max,
        final boolean usePlayerScoreboard, final ConditionValueType type) {
      return new ObjectiveScoreConditions(objectiveName, min, max, usePlayerScoreboard, type);
    }

    @Override
    public boolean isEmpty() {
      return objectiveName == null || objectiveName.isEmpty() || (min == null && max == null);
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
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

  public static class CooldownConditions implements Condition<Player> {
    private final CooldownStore<UUID> store;
    private final boolean requireOnCooldown;
    private final ConditionValueType type;

    public CooldownConditions(final CooldownStore<UUID> store, final boolean requireOnCooldown,
        final ConditionValueType type) {
      this.store = store;
      this.requireOnCooldown = requireOnCooldown;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static CooldownConditions onCooldown(final CooldownStore<UUID> store) {
      return new CooldownConditions(store, true, ConditionValueType.WHITELIST);
    }

    public static CooldownConditions notOnCooldown(final CooldownStore<UUID> store) {
      return new CooldownConditions(store, false, ConditionValueType.WHITELIST);
    }

    public static CooldownConditions from(final CooldownStore<UUID> store, final boolean requireOnCooldown,
        final ConditionValueType type) {
      return new CooldownConditions(store, requireOnCooldown, type);
    }

    @Override
    public boolean isEmpty() {
      return store == null;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
      final boolean onCooldown = store.isOnCooldown(player.getUniqueId());
      return type.apply(onCooldown == requireOnCooldown);
    }
  }

  public static class CurrencyConditions implements Condition<Player> {
    public enum CompareOp {
      EQ, NE, GT, GE, LT, LE
    }

    private final double target;
    private final CompareOp op;
    private final ConditionValueType type;

    public CurrencyConditions(final double target, final CompareOp op, final ConditionValueType type) {
      this.target = target;
      this.op = op == null ? CompareOp.GE : op;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static CurrencyConditions atLeast(final double amount) {
      return new CurrencyConditions(amount, CompareOp.GE, ConditionValueType.WHITELIST);
    }

    public static CurrencyConditions from(final double target, final CompareOp op, final ConditionValueType type) {
      return new CurrencyConditions(target, op, type);
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean test(final Player player) {
      if (player == null) {
        return false;
      }
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

  public static class PlaceholderConditions implements Condition<Player> {
    public enum CompareOp {
      EQUALS, EQUALS_IGNORE_CASE, CONTAINS, STARTS_WITH, ENDS_WITH, REGEX, NOT_EQUALS, GT, GE, LT, LE
    }

    private final String placeholder;
    private final String expected;
    private final CompareOp op;
    private final ConditionValueType type;

    public PlaceholderConditions(final String placeholder, final String expected, final CompareOp op,
        final ConditionValueType type) {
      this.placeholder = placeholder;
      this.expected = expected == null ? "" : expected;
      this.op = op == null ? CompareOp.EQUALS : op;
      this.type = type == null ? ConditionValueType.WHITELIST : type;
    }

    public static PlaceholderConditions equals(final String placeholder, final String expected) {
      return new PlaceholderConditions(placeholder, expected, CompareOp.EQUALS, ConditionValueType.WHITELIST);
    }

    public static PlaceholderConditions from(final String placeholder, final String expected, final CompareOp op,
        final ConditionValueType type) {
      return new PlaceholderConditions(placeholder, expected, op, type);
    }

    @Override
    public boolean isEmpty() {
      return placeholder == null || placeholder.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      if (player == null || isEmpty()) {
        return false;
      }
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
          try {
            matches = Pattern.compile(expected).matcher(resolved).find();
          } catch (final Exception e) {
            matches = false;
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
        final Class<?> papi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
        final Object out = papi.getMethod("setPlaceholders", OfflinePlayer.class, String.class).invoke(null, player,
            input);
        return out == null ? "" : String.valueOf(out);
      } catch (final Throwable ignored) {
        return input;
      }
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
        out.add(s.toLowerCase());
      }
    }
    return out;
  }
}
