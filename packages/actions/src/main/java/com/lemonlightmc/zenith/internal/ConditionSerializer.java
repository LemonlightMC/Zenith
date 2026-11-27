package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.additive.time.TimeRange;
import com.lemonlightmc.zenith.math.Region;

import io.leangen.geantyref.TypeToken;

import com.lemonlightmc.zenith.actions.Condition;
import com.lemonlightmc.zenith.actions.Conditions;
import com.lemonlightmc.zenith.actions.Condition.AlwaysCondition;
import com.lemonlightmc.zenith.actions.Condition.AndCondition;
import com.lemonlightmc.zenith.actions.Condition.ConditionSet;
import com.lemonlightmc.zenith.actions.Condition.NeverCondition;
import com.lemonlightmc.zenith.actions.Condition.NotCondition;
import com.lemonlightmc.zenith.actions.Condition.OrCondition;
import com.lemonlightmc.zenith.actions.Condition.XorCondition;
import com.lemonlightmc.zenith.actions.Conditions.*;
import com.lemonlightmc.zenith.actions.Conditions.LightLevelConditions.LightType;

/**
 * Serializes and deserializes player {@link Condition} instances from
 * configuration data.
 */
public final class ConditionSerializer implements TypeSerializer<Condition> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final ConditionSerializer INSTANCE = new ConditionSerializer();

  private ConditionSerializer() {
  }

  /**
   * Deserializes a condition from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the deserialized condition, or {@code null} if unavailable
   * @throws SerializationException if the condition cannot be parsed
   */
  @Override
  public Condition deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }

    // Shorthand scalars: "always" / "never"
    if (!node.isMap() && !node.isList()) {
      final String scalar = node.getString();
      if (scalar == null) {
        return null;
      }
      switch (normalizeId(scalar)) {
        case "always":
        case "true":
          return AlwaysCondition.of();
        case "never":
        case "false":
          return NeverCondition.of();
        default:
          throw new SerializationException(node, Condition.class, "Unknown condition shorthand: " + scalar);
      }
    }

    final ConfigurationNode typeNode = ActionSerializer.firstNonEmpty(node, "type", "id");
    final String rawType = typeNode == null ? null : typeNode.getString();
    if (rawType == null || rawType.isEmpty()) {
      throw new SerializationException(node, Condition.class, "Condition requires a 'type'");
    }

    switch (normalizeId(rawType)) {
      case "always":
        return AlwaysCondition.of();
      case "never":
        return NeverCondition.of();
      case "not":
      case "negate":
        return NotCondition.of(readSingleChild(node, "condition", "value", "child"));
      case "all":
      case "and":
      case "set":
        return buildAll(node);
      case "any":
      case "or":
        return buildAny(node);
      case "xor":
        return buildXor(node);

      // Leaves
      case "world":
      case "worlds":
        return Conditions.WorldConditions.from(stringSet(node), mode(node));
      case "biome":
      case "biomes":
        return Conditions.BiomeConditions.from(stringSet(node), mode(node));
      case "weather":
        return Conditions.WeatherConditions.from(stringSet(node), mode(node));
      case "moon-phase":
      case "moon-phases":
        return Conditions.MoonConditions.from(intSet(node), mode(node));
      case "time":
      case "time-range":
      case "time-ranges":
        return Conditions.TimeRangeConditions.from(timeRanges(node), mode(node));
      case "advancement":
      case "advancements":
        return Conditions.AdvancementConditions.from(stringSet(node), mode(node));
      case "recipe":
      case "recipes":
        return Conditions.RecipeConditions.from(stringSet(node), mode(node));
      case "plugin":
      case "plugins":
        return adaptSenderCondition(Conditions.PluginConditions.from(stringSet(node), mode(node)));
      case "permission":
      case "permissions":
        return adaptSenderCondition(Conditions.PermissionConditions.from(stringSet(node), mode(node)));
      case "gamemode":
      case "gamemodes":
        return Conditions.GameModeConditions.from(enumSet(node, GameMode.class), mode(node));
      case "health":
        return Conditions.HealthConditions.from(
            optDouble(node.node("min")), optDouble(node.node("max")),
            node.node("percent").getBoolean(false), mode(node));
      case "level":
        return Conditions.LevelConditions.from(
            optInt(node.node("min")), optInt(node.node("max")), mode(node));
      case "food":
      case "hunger":
      case "food-level":
        return Conditions.FoodConditions.from(
            optInt(node.node("min")), optInt(node.node("max")), mode(node));
      case "flight":
      case "fly":
        return Conditions.FlightConditions.from(
            optBool(node.node("allow-flight"), node.node("allow")),
            optBool(node.node("flying"), node.node("is-flying")),
            mode(node));
      case "invulnerable":
      case "invulnerability":
        return Conditions.InvulnerableConditions.from(
            node.node("value").getBoolean(node.node("expected").getBoolean(true)), mode(node));
      case "sneaking":
      case "sneak":
        return Conditions.SneakingConditions.from(boolExpected(node), mode(node));
      case "sprinting":
      case "sprint":
        return Conditions.SprintingConditions.from(boolExpected(node), mode(node));
      case "swimming":
      case "swim":
        return Conditions.SwimmingConditions.from(boolExpected(node), mode(node));
      case "gliding":
      case "glide":
      case "elytra":
        return Conditions.GlidingConditions.from(boolExpected(node), mode(node));
      case "game-rules":
      case "gamerules":
      case "gamerule":
        return Conditions.GameRulesConditions.from(stringMap(node, "rules", "values", "gamerules"), mode(node));
      case "item-in-hand":
      case "hand-item":
      case "holding":
        return Conditions.ItemInHandConditions.from(
            enumSet(node, Material.class), handSlot(node), mode(node));
      case "armor":
      case "armour":
        return Conditions.ArmorConditions.from(armorMap(node), mode(node));
      case "empty-slots":
      case "empty-slot":
        return Conditions.EmptySlotConditions.from(
            optInt(node.node("min"), node.node("min-empty-slots"), node.node("value")), mode(node));
      case "inventory-contains":
      case "has-item":
      case "contains-item":
        return Conditions.InventoryContainsConditions.from(
            parseEnum(node.node("material").getString(node.node("item").getString()), Material.class, null),
            node.node("amount").getInt(node.node("min-amount").getInt(1)),
            mode(node));
      case "region":
      case "regions":
        return Conditions.RegionConditions.from(parseRegions(node), mode(node));
      case "light":
      case "light-level":
        return Conditions.LightLevelConditions.from(
            optInt(node.node("min")), optInt(node.node("max")),
            parseEnum(node.node("light-type").getString(node.node("light").getString()), LightType.class,
                LightType.BLOCK),
            mode(node));
      case "dimension":
      case "dimensions":
      case "environment":
        return Conditions.DimensionConditions.from(enumSet(node, World.Environment.class), mode(node));
      case "difficulty":
      case "difficulties":
        return Conditions.DifficultyConditions.from(enumSet(node, Difficulty.class), mode(node));
      case "statistic":
      case "stat":
        return parseStatistic(node);
      case "objective":
      case "objective-score":
      case "score":
        return Conditions.ObjectiveScoreConditions.from(
            node.node("objective").getString(node.node("name").getString()),
            optInt(node.node("min")), optInt(node.node("max")),
            node.node("player-scoreboard").getBoolean(node.node("use-player-scoreboard").getBoolean(true)),
            mode(node));
      case "placeholder":
      case "papi":
        return Conditions.PlaceholderConditions.from(
            node.node("placeholder").getString(node.node("name").getString()),
            node.node("expected").getString(node.node("value").getString("")),
            parseEnum(node.node("op").getString(node.node("compare").getString(node.node("operator").getString())),
                Conditions.PlaceholderConditions.CompareOp.class, Conditions.PlaceholderConditions.CompareOp.EQUALS),
            mode(node));
      case "experience":
      case "exp":
      case "xp":
        return Conditions.ExperienceConditions.from(
            optDouble(node.node("min")), optDouble(node.node("max")),
            parseEnum(node.node("xp-mode").getString(node.node("mode-type").getString()),
                Conditions.ExperienceConditions.Mode.class, Conditions.ExperienceConditions.Mode.TOTAL),
            mode(node));
      case "absorption":
        return Conditions.AbsorptionConditions.from(
            optDouble(node.node("min")), optDouble(node.node("max")), mode(node));
      case "saturation":
        return Conditions.SaturationConditions.from(
            optFloat(node.node("min")), optFloat(node.node("max")), mode(node));
      case "sleeping":
      case "sleep":
        return Conditions.SleepingConditions.from(boolExpected(node), mode(node));
      case "riptiding":
      case "riptide":
        return Conditions.RiptidingConditions.from(boolExpected(node), mode(node));
      case "blocking":
      case "block":
        return Conditions.BlockingConditions.from(boolExpected(node), mode(node));
      case "held-item-detail":
      case "held-item":
      case "item-detail":
        return parseHeldItemDetail(node);
      case "specific-slot":
      case "slot":
        return parseSpecificSlot(node);
      case "inventory-full":
      case "inv-full":
        return Conditions.InventoryFullConditions.from(
            node.node("value").getBoolean(node.node("full").getBoolean(true)), mode(node));
      case "coordinates":
      case "coordinate":
      case "coords":
      case "position":
        return Conditions.CoordinateConditions.from(
            optDouble(node.node("min-x"), node.node("x-min")),
            optDouble(node.node("max-x"), node.node("x-max")),
            optDouble(node.node("min-y"), node.node("y-min")),
            optDouble(node.node("max-y"), node.node("y-max")),
            optDouble(node.node("min-z"), node.node("z-min")),
            optDouble(node.node("max-z"), node.node("z-max")),
            mode(node));
      case "distance": {
        final ConfigurationNode originNode = ActionSerializer.firstNonEmpty(node, "origin", "location");
        final org.bukkit.Location origin = originNode == null || originNode.virtual() ? null
            : originNode.get(org.bukkit.Location.class);
        return Conditions.DistanceConditions.from(
            origin,
            optDouble(node.node("min")), optDouble(node.node("max")),
            node.node("same-world").getBoolean(true),
            node.node("ignore-y").getBoolean(false),
            mode(node));
      }
      case "block-at":
      case "block-at-location":
      case "standing-on":
        return parseBlockAt(node);
      case "player-count":
      case "players":
      case "online-players":
        return Conditions.PlayerCountConditions.from(
            optInt(node.node("min")), optInt(node.node("max")),
            parseEnum(node.node("scope").getString(), PlayerCountScope.class, PlayerCountScope.SERVER),
            mode(node));
      case "nearby-entities":
      case "nearby":
        return Conditions.NearbyEntitiesConditions.from(
            optDouble(node.node("radius")),
            enumSet(node, EntityType.class, "entity-types", "entities", "types"),
            optInt(node.node("min"), node.node("min-count")),
            optInt(node.node("max"), node.node("max-count")),
            node.node("include-players").getBoolean(false),
            node.node("include-self").getBoolean(false),
            mode(node));
      case "playtime":
        return Conditions.PlaytimeConditions.from(
            optLong(node.node("min")), optLong(node.node("max")),
            parseEnum(node.node("unit").getString(), Conditions.PlaytimeConditions.Unit.class,
                Conditions.PlaytimeConditions.Unit.TICKS),
            mode(node));
      case "first-join":
      case "first-join-condition":
        return Conditions.FirstJoinConditions.from(
            node.node("value").getBoolean(node.node("first-join").getBoolean(true)), mode(node));
      case "cooldown":
        throw new SerializationException(node, Condition.class,
            "Cooldown conditions require a runtime CooldownStore and cannot be deserialized from config");
      case "currency":
      case "balance":
      case "money":
        return Conditions.CurrencyConditions.from(
            node.node("amount").getDouble(node.node("target").getDouble(node.node("value").getDouble(0.0))),
            parseEnum(node.node("op").getString(node.node("compare").getString()),
                Conditions.CurrencyConditions.CompareOp.class, Conditions.CurrencyConditions.CompareOp.GE),
            mode(node));
      case "structure":
      case "structures":
        return Conditions.StructureConditions.from(stringSet(node), mode(node));

      default:
        throw new SerializationException(node, Condition.class, "Unknown condition type: " + rawType);
    }
  }

  /**
   * Serializes a condition to a configuration node.
   *
   * @param type the source type
   * @param obj  the condition to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails or is unsupported for
   *                                the condition type
   */
  @SuppressWarnings("unchecked")
  @Override
  public void serialize(final Type type, final Condition obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    // Best-effort serialization for common/composite cases; many leaf types lack
    // getters.
    if (obj instanceof AlwaysCondition) {
      node.set("always");
      return;
    }
    if (obj instanceof NeverCondition) {
      node.set("never");
      return;
    }
    if (obj instanceof NotCondition) {
      final NotCondition n = (NotCondition) obj;
      node.node("type").set("not");
      node.node("condition").set(new TypeToken<Condition>() {
      }, n.delegate());
      return;
    }
    if (obj instanceof final ConditionSet set) {
      node.node("type").set("all");
      final ConfigurationNode arr = node.node("conditions");
      for (final Condition child : set.children()) {
        arr.appendListNode().set(new TypeToken<Condition>() {
        }, (Condition) child);
      }
      return;
    }
    if (obj instanceof final AndCondition c) {
      node.node("type").set("all");
      node.node("conditions").appendListNode().set(new TypeToken<Condition>() {
      }, (Condition) c.left());
      node.node("conditions").appendListNode().set(new TypeToken<Condition>() {
      }, (Condition) c.right());
      return;
    }
    if (obj instanceof final OrCondition c) {
      node.node("type").set("any");
      node.node("conditions").appendListNode().set(new TypeToken<Condition>() {
      }, (Condition) c.left());
      node.node("conditions").appendListNode().set(new TypeToken<Condition>() {
      }, (Condition) c.right());
      return;
    }
    if (obj instanceof final XorCondition c) {
      node.node("type").set("xor");
      node.node("left").set(new TypeToken<Condition>() {
      }, (Condition) c.left());
      node.node("right").set(new TypeToken<Condition>() {
      }, (Condition) c.right());
      return;
    }
    throw new SerializationException(node, Condition.class,
        "Cannot serialize condition of type " + obj.getClass().getName()
            + " (no exposed state). Deserialization is supported for configured conditions.");
  }

  // ----------------- builders/helpers -----------------

  private Condition buildAll(final ConfigurationNode node) throws SerializationException {
    final List<Condition> children = readChildren(node);
    if (children.size() == 1) {
      return children.get(0);
    }
    final ConditionSet set = ConditionSet.of();
    for (final Condition c : children) {
      set.add(c);
    }
    return set;
  }

  private Condition buildAny(final ConfigurationNode node) throws SerializationException {
    final List<Condition> children = readChildren(node);
    if (children.isEmpty()) {
      return NeverCondition.of();
    }
    Condition acc = children.get(0);
    for (int i = 1; i < children.size(); i++) {
      acc = OrCondition.of(acc, children.get(i));
    }
    return acc;
  }

  private Condition buildXor(final ConfigurationNode node) throws SerializationException {
    final ConfigurationNode leftN = node.node("left");
    final ConfigurationNode rightN = node.node("right");
    if (!leftN.virtual() && !rightN.virtual()) {
      return XorCondition.of(deserialize((Type) null, leftN), deserialize((Type) null, rightN));
    }
    final List<Condition> children = readChildren(node);
    if (children.size() == 2) {
      return XorCondition.of(children.get(0), children.get(1));
    }
    throw new SerializationException(node, Condition.class,
        "xor condition requires 'left'/'right' or exactly 2 children");
  }

  private Condition readSingleChild(final ConfigurationNode node, final String... keys)
      throws SerializationException {
    for (final String k : keys) {
      final ConfigurationNode c = node.node(k);
      if (!c.virtual()) {
        return deserialize((Type) null, c);
      }
    }
    final List<Condition> children = readChildren(node);
    if (children.size() == 1) {
      return children.get(0);
    }
    throw new SerializationException(node, Condition.class, "Expected a single child condition");
  }

  private List<Condition> readChildren(final ConfigurationNode node) throws SerializationException {
    ConfigurationNode listNode = node.node("conditions");
    if (listNode.virtual()) {
      listNode = node.node("children");
    }
    if (listNode.virtual()) {
      listNode = node.node("values");
    }
    final List<Condition> out = new ArrayList<>();
    if (!listNode.virtual()) {
      if (listNode.isList()) {
        for (final ConfigurationNode child : listNode.childrenList()) {
          final Condition c = deserialize((Type) null, child);
          if (c != null) {
            out.add(c);
          }
        }
      } else if (listNode.isMap() || !listNode.isNull()) {
        final Condition c = deserialize((Type) null, listNode);
        if (c != null) {
          out.add(c);
        }
      }
    }
    return out;
  }

  private static Condition adaptSenderCondition(final Condition condition) {
    return condition;
  }

  private static ConditionListType mode(final ConfigurationNode node) {
    final ConfigurationNode modeNode = ActionSerializer.firstNonEmpty(node,
        "mode",
        "list-type",
        "list");
    final String raw = modeNode == null ? null : modeNode.getString();
    if (raw == null) {
      return ConditionListType.WHITELIST; // default
    }
    switch (normalizeId(raw)) {
      case "blacklist":
      case "black":
      case "deny":
      case "denied":
      case "exclude":
        return ConditionListType.BLACKLIST;
      case "whitelist":
      case "white":
      case "allow":
      case "allowed":
      case "include":
      default:
        return ConditionListType.WHITELIST;
    }
  }

  private static Set<String> stringSet(final ConfigurationNode node) throws SerializationException {
    return stringSet(node, "values", "value");
  }

  private static Set<String> stringSet(final ConfigurationNode node, final String... keys)
      throws SerializationException {
    final ConfigurationNode n = firstPresent(node, keys);
    return toStringSet(n);
  }

  private static Set<String> toStringSet(final ConfigurationNode n) {
    if (n == null || n.virtual() || n.isNull()) {
      return new HashSet<>();
    }
    final LinkedHashSet<String> out = new LinkedHashSet<>();
    if (n.isList()) {
      for (final ConfigurationNode c : n.childrenList()) {
        final String v = c.getString();
        if (v != null) {
          out.add(v);
        }
      }
    } else {
      final String v = n.getString();
      if (v != null) {
        out.add(v);
      } else {
        // try map-as-set keys
        for (final Map.Entry<Object, ? extends ConfigurationNode> e : n.childrenMap().entrySet()) {
          if (e.getKey() != null) {
            out.add(String.valueOf(e.getKey()));
          }
        }
      }
    }
    return out;
  }

  private static Set<Integer> intSet(final ConfigurationNode node) throws SerializationException {
    final ConfigurationNode n = firstPresent(node, "values", "value");
    if (n == null || n.virtual() || n.isNull()) {
      return new HashSet<>();
    }
    final Set<Integer> out = new LinkedHashSet<>();
    if (n.isList()) {
      for (final ConfigurationNode c : n.childrenList()) {
        out.add(c.getInt());
      }
    } else {
      out.add(n.getInt());
    }
    return out;
  }

  private static Set<TimeRange> timeRanges(final ConfigurationNode node) throws SerializationException {
    final ConfigurationNode n = firstPresent(node, "values", "value", "ranges", "range");
    if (n == null || n.virtual() || n.isNull()) {
      return new HashSet<>();
    }
    final Set<TimeRange> out = new LinkedHashSet<>();
    if (n.isList()) {
      for (final ConfigurationNode c : n.childrenList()) {
        final TimeRange r = parseTimeRange(c);
        if (r != null) {
          out.add(r);
        }
      }
    } else {
      final TimeRange r = parseTimeRange(n);
      if (r != null) {
        out.add(r);
      }
    }
    return out;
  }

  private static TimeRange parseTimeRange(final ConfigurationNode n) throws SerializationException {
    if (n == null || n.virtual() || n.isNull()) {
      return null;
    }
    if (n.isMap()) {
      final int min = n.node("min").getInt(n.node("start").getInt(n.node("from").getInt(0)));
      final int max = n.node("max").getInt(n.node("end").getInt(n.node("to").getInt(min)));
      return new TimeRange(min, max);
    }
    final String s = n.getString();
    if (s == null || s.isEmpty()) {
      return new TimeRange(n.getInt(), n.getInt());
    }
    final String[] parts = s.split("-", 2);
    try {
      if (parts.length == 2) {
        return new TimeRange(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
      }
      final int v = Integer.parseInt(s.trim());
      return new TimeRange(v, v);
    } catch (final NumberFormatException e) {
      throw new SerializationException(n, TimeRange.class, "Invalid time range: " + s);
    }
  }

  private static <E extends Enum<E>> Set<E> enumSet(final ConfigurationNode node, final Class<E> type)
      throws SerializationException {
    return enumSet(node, type, "values", "value");
  }

  private static <E extends Enum<E>> Set<E> enumSet(final ConfigurationNode node, final Class<E> type,
      final String... keys) throws SerializationException {
    final ConfigurationNode n = firstPresent(node, keys);
    if (n == null || n.virtual() || n.isNull()) {
      return new HashSet<>();
    }
    final Set<E> out = EnumSet.noneOf(type);
    if (n.isList()) {
      for (final ConfigurationNode c : n.childrenList()) {
        final E e = parseEnum(c.getString(), type, null);
        if (e != null) {
          out.add(e);
        }
      }
    } else {
      final E e = parseEnum(n.getString(), type, null);
      if (e != null) {
        out.add(e);
      }
    }
    return out.isEmpty() ? new HashSet<>() : out;
  }

  private static Map<String, String> stringMap(final ConfigurationNode node, final String... keys) {
    final ConfigurationNode n = firstPresent(node, keys);
    if (n == null || n.virtual() || n.isNull() || !n.isMap()) {
      return Collections.emptyMap();
    }
    final Map<String, String> out = new HashMap<>();
    for (final Map.Entry<Object, ? extends ConfigurationNode> e : n.childrenMap().entrySet()) {
      if (e.getKey() != null) {
        out.put(String.valueOf(e.getKey()), e.getValue().getString(""));
      }
    }
    return out;
  }

  private static HandSlot handSlot(final ConfigurationNode node) {
    final String s = node.node("slot").getString(node.node("hand").getString("MAIN"));
    if (s != null && normalizeId(s).equals("off")) {
      return HandSlot.OFF;
    }
    return HandSlot.MAIN;
  }

  private static Map<EquipmentSlot, Set<Material>> armorMap(final ConfigurationNode node)
      throws SerializationException {
    ConfigurationNode n = node.node("armor");
    if (n.virtual()) {
      n = node.node("slots");
    }
    if (n.virtual()) {
      n = node.node("values");
    }
    if (n.virtual() || n.isNull() || !n.isMap()) {
      return Collections.emptyMap();
    }
    final Map<EquipmentSlot, Set<Material>> out = new HashMap<>();
    for (final Map.Entry<Object, ? extends ConfigurationNode> e : n.childrenMap().entrySet()) {
      final EquipmentSlot slot = parseEnum(String.valueOf(e.getKey()), EquipmentSlot.class, null);
      if (slot == null) {
        continue;
      }
      final Set<Material> mats = enumSetFromNode(e.getValue(), Material.class);
      if (!mats.isEmpty()) {
        out.put(slot, mats);
      }
    }
    return out;
  }

  private static <E extends Enum<E>> Set<E> enumSetFromNode(final ConfigurationNode n, final Class<E> type) {
    if (n == null || n.virtual() || n.isNull()) {
      return new HashSet<>();
    }
    final Set<E> out = EnumSet.noneOf(type);
    if (n.isList()) {
      for (final ConfigurationNode c : n.childrenList()) {
        final E e = parseEnum(c.getString(), type, null);
        if (e != null)
          out.add(e);
      }
    } else {
      final E e = parseEnum(n.getString(), type, null);
      if (e != null)
        out.add(e);
    }
    return out.isEmpty() ? new HashSet<>() : out;
  }

  private static Set<Region> parseRegions(final ConfigurationNode node) throws SerializationException {
    final ConfigurationNode n = firstPresent(node, "values", "value", "regions", "region");
    if (n == null || n.virtual() || n.isNull()) {
      return new HashSet<>();
    }
    final Set<Region> out = new LinkedHashSet<>();
    if (n.isList()) {
      for (final ConfigurationNode c : n.childrenList()) {
        final Region r = c.get(Region.class);
        if (r != null) {
          out.add(r);
        }
      }
    } else {
      final Region r = n.get(Region.class);
      if (r != null) {
        out.add(r);
      }
    }
    return out;
  }

  private static Condition parseStatistic(final ConfigurationNode node) {
    final Statistic stat = parseEnum(node.node("statistic").getString(node.node("stat").getString()), Statistic.class,
        null);
    final Material material = parseEnum(node.node("material").getString(), Material.class, null);
    final EntityType entity = parseEnum(node.node("entity").getString(node.node("entity-type").getString()),
        EntityType.class, null);
    final int target = node.node("target").getInt(node.node("value").getInt(0));
    final Conditions.StatisticConditions.CompareOp op = parseEnum(
        node.node("op").getString(node.node("compare").getString()),
        Conditions.StatisticConditions.CompareOp.class, Conditions.StatisticConditions.CompareOp.GE);
    return Conditions.StatisticConditions.from(stat, material, entity, target, op, mode(node));
  }

  private static Condition parseHeldItemDetail(final ConfigurationNode node) throws SerializationException {
    final Set<Material> materials = enumSet(node, Material.class, "materials", "material", "values", "value");
    final HandSlot slot = handSlot(node);
    final String displayName = node.node("display-name").getString(node.node("name").getString());
    final String displayNameContains = node.node("display-name-contains")
        .getString(node.node("name-contains").getString());
    final String loreContains = node.node("lore-contains").getString(node.node("lore").getString());
    final Integer cmd = optInt(node.node("custom-model-data"), node.node("model-data"));
    final Map<Enchantment, Integer> ench = enchantments(node.node("enchantments"));
    final Integer minDur = optInt(node.node("min-durability"));
    final Integer maxDur = optInt(node.node("max-durability"));
    return Conditions.HeldItemDetailConditions.from(materials, slot, displayName, displayNameContains, loreContains,
        cmd, ench, minDur, maxDur, mode(node));
  }

  @SuppressWarnings("deprecation")
  private static Map<Enchantment, Integer> enchantments(final ConfigurationNode n) {
    if (n == null || n.virtual() || n.isNull() || !n.isMap()) {
      return Collections.emptyMap();
    }
    final Map<Enchantment, Integer> out = new HashMap<>();
    for (final Map.Entry<Object, ? extends ConfigurationNode> e : n.childrenMap().entrySet()) {
      final Enchantment ench = Enchantment.getByName(String.valueOf(e.getKey()).toUpperCase(Locale.ROOT));
      if (ench == null) {
        continue;
      }
      out.put(ench, e.getValue().getInt(1));
    }
    return out;
  }

  private static Condition parseSpecificSlot(final ConfigurationNode node) throws SerializationException {
    final int slot = node.node("slot").getInt(node.node("index").getInt(0));
    final boolean requireEmpty = node.node("empty").getBoolean(node.node("require-empty").getBoolean(false));
    final Set<Material> materials = enumSet(node, Material.class, "materials", "material", "values", "value");
    return Conditions.SpecificSlotConditions.from(slot, materials, requireEmpty, mode(node));
  }

  private static Condition parseBlockAt(final ConfigurationNode node) throws SerializationException {
    final Conditions.BlockAtLocationConditions.Position pos = parseEnum(
        node.node("position").getString(node.node("at").getString()),
        Conditions.BlockAtLocationConditions.Position.class, Conditions.BlockAtLocationConditions.Position.FEET);
    final Set<Material> materials = enumSet(node, Material.class, "materials", "material", "values", "value");
    return Conditions.BlockAtLocationConditions.from(pos, materials, mode(node));
  }

  // ----------------- primitives -----------------

  private static boolean boolExpected(final ConfigurationNode node) {
    return node.node("value").getBoolean(node.node("expected").getBoolean(true));
  }

  private static Integer optInt(final ConfigurationNode... nodes) {
    for (final ConfigurationNode n : nodes) {
      if (n != null && !n.virtual()) {
        try {
          return n.getInt();
        } catch (final Exception ignored) {
        }
      }
    }
    return null;
  }

  private static Long optLong(final ConfigurationNode... nodes) {
    for (final ConfigurationNode n : nodes) {
      if (n != null && !n.virtual()) {
        try {
          return n.getLong();
        } catch (final Exception ignored) {
        }
      }
    }
    return null;
  }

  private static Double optDouble(final ConfigurationNode... nodes) {
    for (final ConfigurationNode n : nodes) {
      if (n != null && !n.virtual()) {
        try {
          return n.getDouble();
        } catch (final Exception ignored) {
        }
      }
    }
    return null;
  }

  private static Float optFloat(final ConfigurationNode... nodes) {
    final Double d = optDouble(nodes);
    return d == null ? null : d.floatValue();
  }

  private static Boolean optBool(final ConfigurationNode... nodes) {
    for (final ConfigurationNode n : nodes) {
      if (n != null && !n.virtual()) {
        try {
          return n.getBoolean();
        } catch (final Exception ignored) {
        }
      }
    }
    return null;
  }

  private static ConfigurationNode firstPresent(final ConfigurationNode node, final String... keys) {
    for (final String k : keys) {
      final ConfigurationNode n = node.node(k);
      if (!n.virtual()) {
        return n;
      }
    }
    return null;
  }

  private static String normalizeId(final String s) {
    return s == null ? "" : s.trim().toLowerCase(Locale.ROOT).replace('_', '-');
  }

  private static <E extends Enum<E>> E parseEnum(final String value, final Class<E> type, final E def) {
    if (value == null || value.isEmpty()) {
      return def;
    }
    try {
      return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace('.', '_'));
    } catch (final Exception ex) {
      return def;
    }
  }
}