package com.lemonlightmc.zenith.wrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.time.TimeRange;

public class Conditions {

  public static interface Condition<T> {
    public boolean isEmpty();

    public boolean test(T player);
  }

  public static enum ConditionValueType {
    WHITELIST(false),
    BLACKLIST(true);

    private boolean negate = false;

    private ConditionValueType(final boolean negate) {
      this.negate = negate;
    }

    public boolean apply(final boolean value) {
      return negate ? !value : value;
    }
  }

  public static enum ConditionTestType {
    AND((v1, v2) -> v1 && v2),
    NAND((v1, v2) -> !(v1 && v2)),
    OR((v1, v2) -> v1 || v2),
    NOR((v1, v2) -> !(v1 || v2)),
    XOR((v1, v2) -> (v1 && !v2) || (!v1 && v2)),
    XNOR((v1, v2) -> (!v1 || v2) && (v1 || !v2));

    private final BiPredicate<Boolean, Boolean> tester;

    private ConditionTestType(final BiPredicate<Boolean, Boolean> tester) {
      this.tester = tester;
    }

    public boolean merge(final boolean v1, final boolean v2) {
      return tester.test(v1, v2);
    }
  }

  public static class ConditionSet<T> implements Condition<T> {
    private Set<Condition<T>> conditions;
    private ConditionTestType type;

    public ConditionSet() {
      this(null, ConditionTestType.AND);
    }

    public ConditionSet(final Set<Condition<T>> conditions) {
      this(conditions, ConditionTestType.AND);
    }

    public ConditionSet(final Set<Condition<T>> conditions, final ConditionTestType type) {
      this.conditions = conditions == null ? new HashSet<>() : conditions;
      this.type = type == null ? ConditionTestType.AND : type;
    }

    public ConditionTestType getTestType() {
      return type;
    }

    public void setTestType(final ConditionTestType type) {
      if (type == null) {
        return;
      }
      this.type = type;
    }

    public void add(final Condition<T> condition) {
      if (condition == null || condition.isEmpty()) {
        return;
      }
      conditions.add(condition);
    }

    public boolean has(final Condition<T> condition) {
      if (condition == null) {
        return false;
      }
      return conditions.contains(condition);
    }

    public void remove(final Condition<T> condition) {
      if (condition == null) {
        return;
      }
      conditions.remove(condition);
    }

    public Set<Condition<T>> getConditions() {
      return conditions;
    }

    public void setConditions(final Set<Condition<T>> conditions) {
      this.conditions = conditions;
    }

    public void clear() {
      conditions.clear();
    }

    @Override
    public boolean isEmpty() {
      return conditions == null || conditions.isEmpty();
    }

    @Override
    public boolean test(final T player) {
      if (conditions.size() == 0) {
        return true;
      }
      if (player == null) {
        return false;
      }
      boolean value = true;
      for (final Condition<T> condition : conditions) {
        value = type.merge(value, condition.test(player));
      }
      return true;
    }

    @Override
    public int hashCode() {
      return 31 + conditions.hashCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final ConditionSet<T> other = (ConditionSet<T>) obj;
      return conditions.equals(other.conditions);
    }

    @Override
    public String toString() {
      return "ConditionSet [conditions=" + conditions + "]";
    }

  }

  public static class WorldConditions implements Condition<Player> {

    private final Set<String> worlds;
    private final ConditionValueType type;

    public WorldConditions(final Set<String> worlds, final ConditionValueType type) {
      this.worlds = worlds;
      this.type = type;
    }

    public static WorldConditions allowed(final Set<String> allowed_worlds) {
      return new WorldConditions(allowed_worlds, ConditionValueType.WHITELIST);
    }

    public static WorldConditions denied(final Set<String> denied_worlds) {
      return new WorldConditions(denied_worlds, ConditionValueType.BLACKLIST);
    }

    public static WorldConditions from(final Set<String> denied_worlds, final ConditionValueType type) {
      return new WorldConditions(denied_worlds, type);
    }

    @Override
    public boolean isEmpty() {
      return worlds == null || worlds.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      final String w = player.getWorld().getName().toLowerCase();
      return type.apply(worlds.contains(w));
    }
  }

  public static class BiomConditions implements Condition<Player> {

    private final Set<String> bioms;
    private final ConditionValueType type;

    public BiomConditions(final Set<String> bioms, final ConditionValueType type) {
      this.bioms = bioms;
      this.type = type;
    }

    public static BiomConditions allowed(final Set<String> allowed_bioms) {
      return new BiomConditions(allowed_bioms, ConditionValueType.WHITELIST);
    }

    public static BiomConditions denied(final Set<String> denied_bioms) {
      return new BiomConditions(denied_bioms, ConditionValueType.BLACKLIST);
    }

    public static BiomConditions from(final Set<String> denied_bioms, final ConditionValueType type) {
      return new BiomConditions(denied_bioms, type);
    }

    @Override
    public boolean isEmpty() {
      return bioms == null || bioms.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      final String b = player.getWorld().getBiome(player.getLocation()).toString().toLowerCase();
      return type.apply(bioms.contains(b));
    }
  }

  public static class WeatherConditions implements Condition<Player> {

    private final Set<String> weathers;
    private final ConditionValueType type;

    public WeatherConditions(final Set<String> weathers, final ConditionValueType type) {
      this.weathers = weathers;
      this.type = type;
    }

    public static BiomConditions allowed(final Set<String> allowed_worlds) {
      return new BiomConditions(allowed_worlds, ConditionValueType.WHITELIST);
    }

    public static BiomConditions denied(final Set<String> denied_worlds) {
      return new BiomConditions(denied_worlds, ConditionValueType.BLACKLIST);
    }

    public static BiomConditions from(final Set<String> denied_worlds, final ConditionValueType type) {
      return new BiomConditions(denied_worlds, type);
    }

    @Override
    public boolean isEmpty() {
      return weathers == null || weathers.isEmpty();
    }

    @Override
    public boolean test(final Player player) {
      final String now = player.getWorld().isThundering() ? "thunder"
          : (player.getWorld().hasStorm() ? "rain" : "clear");
      return type.apply(weathers.contains(now));
    }
  }

  public static class MoonConditions implements Condition<Player> {

    private final Set<Integer> moonPhases;
    private final ConditionValueType type;

    public MoonConditions(final Set<Integer> moonPhases, final ConditionValueType type) {
      this.moonPhases = moonPhases;
      this.type = type;
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
      final int value = Integer.valueOf(getMoonPhase(player));
      return type.apply(moonPhases.contains(value));
    }

    private static int getMoonPhase(final Player player) {
      final long day = player.getWorld().getFullTime() / 24000L; // Get the in-game day
      return (int) (day % 8); // Calculate the moon phase (0-7)
    }
  }

  public static class TimeRangeConditions implements Condition<Player> {

    private final Set<TimeRange> timeRanges;
    private final ConditionValueType type;

    public TimeRangeConditions(final Set<TimeRange> timeRanges, final ConditionValueType type) {
      this.timeRanges = timeRanges;
      this.type = type;
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
      return timeRanges == null;
    }

    @Override
    public boolean test(final Player player) {
      boolean value = true;
      final int t = (int) (player.getWorld().getFullTime() % 24000);
      for (final TimeRange timeRange : timeRanges) {
        if (timeRange == null) {
          continue;
        }
        value = value && type.apply(timeRange.contains(t));
      }
      return value;
    }
  }

  public static class AdvancementConditions implements Condition<Player> {

    private final Set<String> advancements;
    private final ConditionValueType type;

    public AdvancementConditions(final Set<String> advancements, final ConditionValueType type) {
      this.advancements = advancements;
      this.type = type;
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
      boolean value = true;
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
      }
      return value;
    }
  }

  public static class RecipeConditions implements Condition<Player> {

    private final Set<String> recipes;
    private final ConditionValueType type;

    public RecipeConditions(final Set<String> recipes, final ConditionValueType type) {
      this.recipes = recipes;
      this.type = type;
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
      boolean value = true;
      for (final String recipe : recipes) {
        if (recipe == null || recipe.isEmpty())
          continue;
        final NamespacedKey key = recipe.indexOf(':') >= 0 ? NamespacedKey.fromString(recipe)
            : NamespacedKey.minecraft(recipe);
        if (key == null)
          continue;
        value = value && type.apply(player.hasDiscoveredRecipe(key));
      }
      return value;
    }
  }

  public static class PluginConditions implements Condition<CommandSender> {

    private final Set<String> plugins;
    private final ConditionValueType type;

    public PluginConditions(final Set<String> plugins, final ConditionValueType type) {
      this.plugins = plugins;
      this.type = type;
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
      boolean value = true;
      for (final String plugin : plugins) {
        if (plugin == null || plugin.isEmpty())
          continue;
        final boolean isEnabled = ZenithProvider.instance().getPluginManager().isPluginEnabled(plugin);
        value = value && type.apply(isEnabled);
      }
      return value;
    }
  }

  public static class PermissionConditions implements Condition<CommandSender> {

    private final Set<String> permissions;
    private final ConditionValueType type;

    public PermissionConditions(final Set<String> permissions, final ConditionValueType type) {
      this.permissions = permissions;
      this.type = type;
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
      boolean value = true;
      for (final String permission : permissions) {
        if (permission == null || permission.isEmpty())
          continue;
        final boolean hasPerm = sender.hasPermission(permission);
        value = value && type.apply(hasPerm);
      }
      return value;
    }
  }
}
