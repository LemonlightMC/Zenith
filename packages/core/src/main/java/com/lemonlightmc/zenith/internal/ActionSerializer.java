package com.lemonlightmc.zenith.internal;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.block.data.BlockData;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.apis.TitleAPI.TitleInfo;
import com.lemonlightmc.zenith.math.MathOperation;
import com.lemonlightmc.zenith.utils.actions.Action;
import com.lemonlightmc.zenith.utils.actions.Condition;
import com.lemonlightmc.zenith.utils.actions.Actions.ActionbarAction;
import com.lemonlightmc.zenith.utils.actions.Actions.BlockBreakAction;
import com.lemonlightmc.zenith.utils.actions.Actions.BlockPlaceAction;
import com.lemonlightmc.zenith.utils.actions.Actions.BlockReplaceAction;
import com.lemonlightmc.zenith.apis.bossbar.BossBarAction;
import com.lemonlightmc.zenith.utils.actions.Actions.ConsoleCommandAction;
import com.lemonlightmc.zenith.utils.actions.Actions.DelayAction;
import com.lemonlightmc.zenith.utils.actions.Actions.ExperienceAction;
import com.lemonlightmc.zenith.utils.actions.Actions.ExplosionAction;
import com.lemonlightmc.zenith.utils.actions.Actions.FlyAction;
import com.lemonlightmc.zenith.utils.actions.Actions.FoodAction;
import com.lemonlightmc.zenith.utils.actions.Actions.GameModeAction;
import com.lemonlightmc.zenith.utils.actions.Actions.GiveItemAction;
import com.lemonlightmc.zenith.utils.actions.Actions.HealthAction;
import com.lemonlightmc.zenith.utils.actions.Actions.InvulnerableAction;
import com.lemonlightmc.zenith.utils.actions.Actions.LevelAction;
import com.lemonlightmc.zenith.utils.actions.Actions.LogAction;
import com.lemonlightmc.zenith.utils.actions.Actions.MessageAction;
import com.lemonlightmc.zenith.utils.actions.Actions.MovementAction;
import com.lemonlightmc.zenith.utils.actions.Actions.ParticleAction;
import com.lemonlightmc.zenith.utils.actions.Actions.PlayerCommandAction;
import com.lemonlightmc.zenith.utils.actions.Actions.RandomAction;
import com.lemonlightmc.zenith.utils.actions.Actions.RemoveItemAction;
import com.lemonlightmc.zenith.utils.actions.Actions.RepeatAction;
import com.lemonlightmc.zenith.utils.actions.Actions.SoundAction;
import com.lemonlightmc.zenith.utils.actions.Actions.StopAction;
import com.lemonlightmc.zenith.utils.actions.Actions.StopSoundAction;
import com.lemonlightmc.zenith.utils.actions.Actions.SummonAction;
import com.lemonlightmc.zenith.utils.actions.Actions.TeleportAction;
import com.lemonlightmc.zenith.utils.actions.Actions.TimeAction;
import com.lemonlightmc.zenith.utils.actions.Actions.TitleAction;
import com.lemonlightmc.zenith.utils.actions.Actions.WeatherAction;
import com.lemonlightmc.zenith.utils.actions.Actions.WeightedRandomAction;

import io.leangen.geantyref.TypeToken;

/**
 * Serializes and deserializes {@link Action} instances from configuration data.
 */
public final class ActionSerializer implements TypeSerializer<Action> {

  /**
   * Shared singleton instance of this serializer.
   */
  public static final ActionSerializer INSTANCE = new ActionSerializer();
  private static final TypeToken<Condition> CONDITION_TYPE = new TypeToken<Condition>() {
  };

  private ActionSerializer() {
  }

  /**
   * Deserializes an action from a configuration node.
   *
   * @param type the target type
   * @param node the configuration node to read
   * @return the deserialized action, or {@code null} if unavailable
   * @throws SerializationException if the action cannot be parsed
   */
  @Override
  public Action deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
    if (node == null || node.virtual() || node.isNull()) {
      return null;
    }
    if (!node.isMap()) {
      throw new SerializationException(node, Action.class, "Action must be a mapping with a 'type'");
    }

    final String rawType = firstNonEmpty(node.node("type").getString(), node.node("id").getString());
    if (rawType == null || rawType.isEmpty()) {
      throw new SerializationException(node, Action.class, "Action requires a 'type'");
    }

    final Condition condition = readCondition(node);
    // useSync defaults to true for most actions; Delay/Stop manage their own
    // semantics
    final boolean useSync = node.node("sync").getBoolean(node.node("use-sync").getBoolean(true));
    final boolean broadcast = node.node("broadcast").getBoolean(node.node("all").getBoolean(false));

    switch (normalizeId(rawType)) {
      case "stop":
        return new StopAction(condition);

      case "console-command":
      case "console":
      case "console-cmd":
        return new ConsoleCommandAction(str(node, "command", "cmd", "value"), condition, useSync);

      case "player-command":
      case "player":
      case "player-cmd":
      case "command":
      case "cmd":
        return new PlayerCommandAction(str(node, "command", "cmd", "value"), condition, useSync);

      case "message":
      case "chat":
      case "msg": {
        final String message = str(node, "message", "text", "msg", "value");
        return new MessageAction(message, broadcast, condition, useSync);
      }

      case "title":
      case "subtitle": {
        return new TitleAction(node.get(TitleInfo.class), broadcast, condition, useSync);
      }

      case "actionbar":
      case "action-bar": {
        final String message = str(node, "message", "text", "msg", "value");
        return new ActionbarAction(message, broadcast, condition, useSync);
      }

      case "teleport":
      case "tp": {
        final Location loc = (node.node("location").virtual() ? node : node.node("location")).get(Location.class);
        return new TeleportAction(loc, broadcast, condition, useSync);
      }

      case "sound":
      case "play-sound":
      case "playsound": {
        final String sound = str(node, "sound", "value", "name");
        final float volume = (float) node.node("volume").getDouble(1.0);
        final float pitch = (float) node.node("pitch").getDouble(1.0);
        return new SoundAction(sound, volume, pitch, broadcast, condition, useSync);
      }

      case "bossbar":
      case "boss-bar":
        return BossBarAction.SERIALIZER.deserialize(type, node, condition, useSync, broadcast);

      case "particle":
      case "particles": {
        final Particle particle = parseEnum(str(node, "particle", "type", "value"), Particle.class, null);
        final int count = firstNonEmpty(node, "amount", "count").getInt(1);
        final ConfigurationNode off = node.node("offset");
        final double ox = !off.virtual() ? off.node("x").getDouble(0.5) : node.node("offset-x").getDouble(0.5);
        final double oy = !off.virtual() ? off.node("y").getDouble(0.5) : node.node("offset-y").getDouble(0.5);
        final double oz = !off.virtual() ? off.node("z").getDouble(0.5) : node.node("offset-z").getDouble(0.5);
        final double speed = node.node("speed").getDouble(0.0);
        return new ParticleAction(particle, count, ox, oy, oz, speed, broadcast, condition, useSync);
      }

      case "give-item":
      case "item":
      case "give": {
        return new GiveItemAction(readItem(node), condition, useSync);
      }

      case "remove-item":
      case "removeitem":
      case "take-item": {
        final Material material = parseEnum(str(node, "material", "item", "type"), Material.class, null);
        final int amount = firstNonEmpty(node, "amount", "count").getInt(1);
        return new RemoveItemAction(material, amount, condition, useSync);
      }

      case "experience":
      case "exp":
      case "xp":
        return new ExperienceAction(firstNonEmpty(node, "amount", "value", "xp", "experience").getInt(0), condition,
            useSync);

      case "level":
      case "levels":
        return new LevelAction(firstNonEmpty(node, "amount", "level", "levels", "value").getInt(0), condition, useSync);

      case "health":
      case "heal":
      case "damage":
        return new HealthAction(firstNonEmpty(node, "amount", "health", "value").getDouble(0.0), condition, useSync);

      case "food":
      case "hunger":
      case "feed":
        return new FoodAction(
            firstNonEmpty(node, "amount", "food", "value").getInt(0),
            (float) node.node("saturation").getDouble(0.0),
            condition, useSync);

      case "delay":
      case "wait":
      case "sleep": {
        return new DelayAction(firstNonEmpty(node, "ticks", "delay", "duration").get(Duration.class), condition);
      }

      case "stop-sound":
      case "stopsound": {
        final SoundCategory cat = parseEnum(str(node, "category"), SoundCategory.class, null);
        return new StopSoundAction(str(node, "sound", "value", "name"), cat, broadcast, condition, useSync);
      }

      case "movement":
      case "velocity":
      case "motion": {
        return new MovementAction(
            node.get(Vector.class),
            node.node("operation").get(MathOperation.class),
            node.node("use-look-direction").getBoolean(false),
            broadcast, condition, useSync);
      }

      case "gamemode":
      case "game-mode": {
        return new GameModeAction(parseEnum(str(node, "value"), GameMode.class, null), broadcast, condition, useSync);
      }

      case "fly":
      case "flight": {
        final boolean allow = node.node("allow").getBoolean(true);
        return new FlyAction(allow, node.node("flying").getBoolean(allow), broadcast, condition, useSync);
      }

      case "invulnerable":
      case "invulnerability":
      case "god":
      case "invurable": {
        return new InvulnerableAction(
            node.node("value").getBoolean(true),
            node.node("duration").get(Duration.class),
            broadcast,
            condition, useSync);
      }

      case "summon":
      case "spawn":
      case "spawn-entity": {
        final EntityType entityType = parseEnum(str(node, "entity", "entity-type", "type", "mob"), EntityType.class,
            null);
        final int amount = firstNonEmpty(node, "amount", "count").getInt(1);
        final boolean atPlayer = firstNonEmpty(node, "at-player", "player-location").getBoolean(true);
        final Location loc = atPlayer ? null : node.node("location").get(Location.class);
        if (node.hasChild("customizer")) {
          throw new SerializationException(node, Action.class,
              "SummonAction 'customizer' is not serializable from config");
        }
        return new SummonAction(entityType, amount, loc, atPlayer, null, condition);
      }

      case "weather": {
        final WeatherAction.WeatherType wt = parseEnum(str(node, "weather", "type", "value"),
            WeatherAction.WeatherType.class, WeatherAction.WeatherType.CLEAR);
        final int duration = node.node("duration-ticks").getInt(node.node("duration").getInt(0));
        return new WeatherAction(wt, str(node, "world"), duration, condition, useSync);
      }

      case "time": {
        final long time = firstNonEmpty(node, "time", "value").getLong(0L);
        final boolean add = firstNonEmpty(node, "add", "relative").getBoolean(false);
        return new TimeAction(time, add, str(node, "world"), condition, useSync);
      }

      case "block-place":
      case "place-block":
      case "set-block": {
        final boolean atPlayer = firstNonEmpty(node, "at-player", "player-location").getBoolean(false);
        final Location loc = atPlayer ? null
            : (node.node("location").virtual() ? node : node.node("location")).get(Location.class);
        final Material mat = parseEnum(str(node, "material", "block", "type"), Material.class, null);
        final BlockData data = readBlockData(node.node("block-data").getString(node.node("data").getString()));
        final boolean physics = firstNonEmpty(node, "physics", "apply-physics").getBoolean(true);
        return new BlockPlaceAction(loc, atPlayer, mat, data, physics, condition, useSync);
      }

      case "block-break":
      case "break-block": {
        final boolean atPlayer = firstNonEmpty(node, "at-player", "player-location").getBoolean(false);
        final Location loc = atPlayer ? null
            : (node.node("location").virtual() ? node : node.node("location")).get(Location.class);
        final boolean drop = node.node("drop-items").getBoolean(node.node("drop").getBoolean(false));
        final boolean physics = firstNonEmpty(node, "physics", "apply-physics").getBoolean(true);
        return new BlockBreakAction(loc, atPlayer, drop, physics, condition, useSync);
      }

      case "block-replace":
      case "replace-block": {
        final boolean atPlayer = firstNonEmpty(node, "at-player", "player-location").getBoolean(false);
        final Location loc = atPlayer ? null
            : (node.node("location").virtual()
                ? node
                : node.node("location")).get(Location.class);
        final Material from = parseEnum(str(node, "from", "from-material"), Material.class, null);
        final Material to = parseEnum(str(node, "to", "to-material", "material", "block", "type"), Material.class,
            null);
        final BlockData data = readBlockData(node.node("block-data").getString(node.node("to-data").getString()));
        final boolean physics = firstNonEmpty(node, "physics", "apply-physics").getBoolean(true);
        return new BlockReplaceAction(loc, atPlayer, from, to, data, physics, condition, useSync);
      }

      case "explosion":
      case "explode": {
        final boolean atPlayer = firstNonEmpty(node, "at-player", "player-location").getBoolean(false);
        final Location loc = atPlayer ? null : node.node("location").get(Location.class);
        final float power = (float) node.node("power").getDouble(4.0);
        final boolean fire = node.node("fire").getBoolean(false);
        final boolean breakBlocks = firstNonEmpty(node, "break-blocks", "break").getBoolean(true);
        return new ExplosionAction(loc, atPlayer, power, fire, breakBlocks, condition, useSync);
      }

      case "log": {
        final String message = str(node, "message", "text", "msg", "value");
        final java.util.logging.Level level = parseLogLevel(str(node, "level"));
        return new LogAction(message, level, str(node, "logger", "logger-name"), condition, useSync);
      }

      case "random": {
        return new RandomAction(readActionList(node), condition, useSync);
      }

      case "weighted-random":
      case "weighted": {
        return new WeightedRandomAction(readWeightedActions(node), condition, useSync);
      }

      case "repeat":
      case "loop": {
        final List<Action> actions = readActionList(node);
        if (actions.isEmpty()) {
          final Action single = readSingleAction(node, "action", "then", "do");
          if (single != null) {
            actions.add(single);
          }
        }
        return new RepeatAction(
            actions,
            firstNonEmpty(node, "times", "count", "repeat").getInt(1),
            firstNonEmpty(node, "initial-delay", "delay").getLong(0L),
            node.node("interval").getLong(0L),
            condition, useSync);
      }

      default:
        throw new SerializationException(node, Action.class, "Unknown action type: " + rawType);
    }
  }

  /**
   * Serializes an action to a configuration node.
   *
   * @param type the source type
   * @param obj the action to serialize
   * @param node the configuration node to write
   * @throws SerializationException if serialization fails or is unsupported for the action type
   */
  @Override
  public void serialize(final Type type, final @Nullable Action obj, final ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      node.set(null);
      return;
    }
    // Serialize common fields where possible
    if (obj.condition() != null) {
      try {
        node.node("condition").set(CONDITION_TYPE, obj.condition());
      } catch (final SerializationException e) {
        // condition may not be serializable; omit
      }
    }
    if (!obj.isUseSync()) {
      node.node("sync").set(false);
    }

    if (obj instanceof StopAction) {
      node.node("type").set("stop");
      return;
    }
    if (obj instanceof ConsoleCommandAction) {
      node.node("type").set("console-command");
      // no getter; cannot round-trip command text
      throw new SerializationException(node, Action.class,
          "ConsoleCommandAction serialization requires exposed fields; deserialization is supported.");
    }
    if (obj instanceof DelayAction) {
      node.node("type").set("delay");
      node.node("ticks").set(((DelayAction) obj).getDelayTicks());
      return;
    }
    // Many actions lack getters; support deserialization-first with best-effort for
    // known cases
    throw new SerializationException(node, Action.class,
        "Cannot serialize action of type " + obj.getClass().getName()
            + " (missing getters). Deserialization is supported for configured actions.");
  }

  // ----------------- helpers -----------------

  private Condition readCondition(final ConfigurationNode node) throws SerializationException {
    ConfigurationNode c = node.node("condition");
    if (!c.virtual() && !c.isNull()) {
      return c.get(CONDITION_TYPE);
    }
    c = node.node("conditions");
    if (!c.virtual() && !c.isNull()) {
      return c.get(CONDITION_TYPE);
    }
    return null;
  }

  private static List<Action> readActionList(final ConfigurationNode node) throws SerializationException {
    ConfigurationNode n = node.node("actions");
    if (n.virtual()) {
      n = node.node("children");
    }
    if (n.virtual()) {
      n = node.node("then");
    }
    final List<Action> out = new ArrayList<>();
    if (!n.virtual() && n.isList()) {
      for (final ConfigurationNode c : n.childrenList()) {
        final Action a = INSTANCE.deserialize((Type) null, c);
        if (a != null) {
          out.add(a);
        }
      }
    }
    return out;
  }

  private static Action readSingleAction(final ConfigurationNode node, final String... keys)
      throws SerializationException {
    for (final String k : keys) {
      final ConfigurationNode c = node.node(k);
      if (!c.virtual() && c.isMap()) {
        return INSTANCE.deserialize((Type) null, c);
      }
    }
    return null;
  }

  private static List<WeightedRandomAction.Entry> readWeightedActions(final ConfigurationNode node)
      throws SerializationException {
    ConfigurationNode n = node.node("actions");
    if (n.virtual()) {
      n = node.node("entries");
    }
    final List<WeightedRandomAction.Entry> out = new ArrayList<>();
    if (n.virtual() || !n.isList()) {
      return out;
    }
    for (final ConfigurationNode c : n.childrenList()) {
      final double weight = c.node("weight").getDouble(c.node("chance").getDouble(1.0));
      ConfigurationNode aNode = c.node("action");
      if (aNode.virtual()) {
        // allow inline action fields alongside weight
        aNode = c;
      }
      final Action a = INSTANCE.deserialize((Type) null, aNode);
      if (a != null) {
        out.add(new WeightedRandomAction.Entry(a, weight));
      }
    }
    return out;
  }

  private static ItemStack readItem(final ConfigurationNode node) throws SerializationException {
    ConfigurationNode n = node.node("item");
    if (n.virtual()) {
      n = node;
    }
    final Material mat = parseEnum(n.node("material").getString(n.node("type").getString()), Material.class, null);
    if (mat == null) {
      return null;
    }
    final int amount = n.node("amount").getInt(n.node("count").getInt(1));
    return new ItemStack(mat, Math.max(1, amount));
  }

  private static BlockData readBlockData(final String data) {
    if (data == null || data.isEmpty()) {
      return null;
    }
    try {
      return Bukkit.createBlockData(data);
    } catch (final Exception e) {
      return null;
    }
  }

  private static java.util.logging.Level parseLogLevel(final String s) {
    if (s == null) {
      return java.util.logging.Level.INFO;
    }
    try {
      return java.util.logging.Level.parse(s.toUpperCase(Locale.ROOT));
    } catch (final Exception e) {
      return java.util.logging.Level.INFO;
    }
  }

  private static String str(final ConfigurationNode node, final String... keys) {
    for (final String k : keys) {
      final ConfigurationNode n = node.node(k);
      if (!n.virtual()) {
        final String v = n.getString();
        if (v != null) {
          return v;
        }
      }
    }
    return null;
  }

  static ConfigurationNode firstNonEmpty(final ConfigurationNode node, final String... keys) {
    for (final String key : keys) {
      final ConfigurationNode n = node.node(key);
      if (!n.virtual() && !n.isNull()) {
        return n;
      }
    }
    return null;
  }

  static String firstNonEmpty(final String... values) {
    for (final String v : values) {
      if (v != null && !v.isEmpty()) {
        return v;
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