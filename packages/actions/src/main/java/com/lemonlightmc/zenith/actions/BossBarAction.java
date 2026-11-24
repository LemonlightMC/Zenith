package com.lemonlightmc.zenith.actions;

import java.lang.reflect.Type;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.apis.bossbar.BossBar;
import com.lemonlightmc.zenith.apis.bossbar.BossbarAPI;

/**
 * Action implementation that creates and displays a boss bar for players.
 *
 * <p>
 * The action can target a specific player context or broadcast to all online
 * players, and optionally remove the
 * created bar after a configured duration.
 */
public class BossBarAction extends Action<CommandSender> {

  /** Serializer for boss bar action configuration nodes. */
  public static final BossBarActionSerializer SERIALIZER = new BossBarActionSerializer();

  /** The explicit key used for the created boss bar, if provided. */
  private final NamespacedKey key;
  /** The title displayed by the boss bar. */
  private final String title;
  /** The color applied to the boss bar. */
  private final BarColor color;
  /** The style applied to the boss bar. */
  private final BarStyle style;
  /** The initial progress value of the boss bar. */
  private final double progress;
  /** The duration in ticks before automatic removal, if positive. */
  private final int durationTicks;
  /** Whether the boss bar should be shown to all online players. */
  private final boolean broadcast;

  /**
   * Creates a boss bar action with default targeting and conditions.
   *
   * @param key           the boss bar key, or {@code null} to derive one from the
   *                      title
   * @param title         the boss bar title
   * @param color         the boss bar color
   * @param style         the boss bar style
   * @param progress      the initial progress value
   * @param durationTicks the display duration in ticks, or a non-positive value
   *                      to keep the bar
   */
  public BossBarAction(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
      final double progress, final int durationTicks) {
    this(key, title, color, style, progress, durationTicks, false, null);
  }

  /**
   * Creates a boss bar action with broadcast and condition settings.
   *
   * @param key           the boss bar key, or {@code null} to derive one from the
   *                      title
   * @param title         the boss bar title
   * @param color         the boss bar color
   * @param style         the boss bar style
   * @param progress      the initial progress value
   * @param durationTicks the display duration in ticks, or a non-positive value
   *                      to keep the bar
   * @param broadcast     whether the boss bar should be shown to all online
   *                      players
   * @param conditions    the conditions required for execution
   */
  public BossBarAction(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
      final double progress, final int durationTicks, final boolean broadcast,
      final Condition<CommandSender> conditions) {
    super(conditions);
    this.key = key;
    this.title = title == null ? "" : title;
    this.color = color == null ? BarColor.PURPLE : color;
    this.style = style == null ? BarStyle.SOLID : style;
    this.progress = Math.max(0.0, Math.min(1.0, progress));
    this.durationTicks = durationTicks;
    this.broadcast = broadcast;
  }

  /**
   * Creates a boss bar action with broadcast, condition, and synchronization
   * settings.
   *
   * @param key           the boss bar key, or {@code null} to derive one from the
   *                      title
   * @param title         the boss bar title
   * @param color         the boss bar color
   * @param style         the boss bar style
   * @param progress      the initial progress value
   * @param durationTicks the display duration in ticks, or a non-positive value
   *                      to keep the bar
   * @param broadcast     whether the boss bar should be shown to all online
   *                      players
   * @param conditions    the conditions required for execution
   * @param useSync       whether execution should be synchronized
   */
  public BossBarAction(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
      final double progress, final int durationTicks, final boolean broadcast,
      final Condition<CommandSender> conditions, final boolean useSync) {
    super(conditions, useSync);
    this.key = key;
    this.title = title == null ? "" : title;
    this.color = color == null ? BarColor.PURPLE : color;
    this.style = style == null ? BarStyle.SOLID : style;
    this.progress = Math.max(0.0, Math.min(1.0, progress));
    this.durationTicks = durationTicks;
    this.broadcast = broadcast;
  }

  /**
   * Executes the boss bar action for the given context.
   *
   * @param context the action execution context
   * @return {@code true} after the action has been applied
   */
  @Override
  protected boolean run(final ExecutionContext<CommandSender> context) {
    final NamespacedKey effectiveKey = key != null ? key
        : NamespacedKey.minecraft("zenith_action_" + Math.abs(title.hashCode()));
    BossbarAPI.removeBar(effectiveKey);
    final BossBar bar = new BossBar(effectiveKey, title, color, style).setProgress(progress).setVisible(true);
    if (broadcast) {
      for (final Player p : Bukkit.getOnlinePlayers()) {
        BossbarAPI.addBar(p, bar);
      }
    } else if (context.player() != null) {
      final Player player = context.player();
      BossbarAPI.addBar(player, bar);
    }
    if (durationTicks > 0) {
      GlobalScheduler.runLater(() -> BossbarAPI.removeBar(effectiveKey), durationTicks);
    }
    return true;
  }

  /**
   * Configurate serializer for {@link BossBarAction} instances.
   */
  public static final class BossBarActionSerializer implements TypeSerializer<BossBarAction> {

    /**
     * Creates a new serializer instance.
     */
    private BossBarActionSerializer() {
    }

    /**
     * Deserializes a boss bar action with explicit execution settings.
     *
     * @param type      the target type
     * @param node      the source configuration node
     * @param condition the condition applied to the action
     * @param useSync   whether the action should execute synchronously
     * @param broadcast whether the action should broadcast to all players
     * @return the deserialized boss bar action
     * @throws SerializationException if deserialization fails
     */
    public BossBarAction deserialize(final Type type, final ConfigurationNode node,
        final Condition<CommandSender> condition, final boolean useSync, final boolean broadcast)
        throws SerializationException {
      final ConfigurationNode keyNode = firstNonEmpty(node, "key", "id", "name");
      final String title = str(node, "title", "text", "message");
      final BarColor color = parseEnum(str(node, "color"), BarColor.class, BarColor.PURPLE);
      final BarStyle style = parseEnum(str(node, "style"), BarStyle.class, BarStyle.SOLID);
      final double progress = node.node("progress").getDouble(1.0);
      final int duration = node.node("duration-ticks").getInt(node.node("duration").getInt(-1));
      return new BossBarAction(keyNode == null ? null : keyNode.get(NamespacedKey.class), title, color, style,
          progress, duration, broadcast, condition, useSync);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BossBarAction deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
      return deserialize(type, node, null, true, node.node("broadcast").getBoolean(node.node("all").getBoolean(false)));
    }

    /**
     * {@inheritDoc}
     *
     * @throws SerializationException always, because serialization is not supported
     */
    @Override
    public void serialize(final Type type, final @Nullable BossBarAction obj, final ConfigurationNode node)
        throws SerializationException {
      if (obj == null) {
        node.set(null);
        return;
      }
      throw new SerializationException(node, BossBarAction.class,
          "BossBarAction serialization is not supported");
    }

    /**
     * Returns the first non-null string value found for the given keys.
     *
     * @param node the source configuration node
     * @param keys the candidate child keys
     * @return the resolved string value, or {@code null} if none is found
     */
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

    /**
     * Returns the first non-virtual, non-null child node found for the given keys.
     *
     * @param node the source configuration node
     * @param keys the candidate child keys
     * @return the resolved configuration node, or {@code null} if none is found
     */
    private static ConfigurationNode firstNonEmpty(final ConfigurationNode node, final String... keys) {
      for (final String key : keys) {
        final ConfigurationNode n = node.node(key);
        if (!n.virtual() && !n.isNull()) {
          return n;
        }
      }
      return null;
    }

    /**
     * Parses an enum value from configuration input using a case-insensitive
     * fallback strategy.
     *
     * @param <E>   the enum type
     * @param value the raw configuration value
     * @param type  the enum class to parse
     * @param def   the default value to return when parsing fails
     * @return the parsed enum value, or {@code def} if parsing fails
     */
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
}
