package com.lemonlightmc.zenith.apis.bossbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.additive.interfaces.Cloneable;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarChangedEvent;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarCreateEvent;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarNameChangedEvent;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarProgressEvent;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarVisibilityChangeEvent;

/**
 * Represents a Zenith-managed wrapper around a Bukkit {@link KeyedBossBar}.
 *
 * <p>The wrapper provides convenience methods for updating boss bar state, tracking players, and dispatching related
 * boss bar events when changes occur.
 */
public class BossBar implements Cloneable<BossBar> {
  /** The maximum allowed boss bar progress value. */
  public static final double MAX_PROGRESS = 1d;
  /** The minimum allowed boss bar progress value. */
  public static final double MIN_PROGRESS = 0d;
  /** The maximum distance used for location-based visibility checks. */
  public static final double MAX_DISTANCE = 30d;

  /** The default boss bar color. */
  public static final BarColor DEFAULT_COLOR = BarColor.PURPLE;
  /** The default boss bar style. */
  public static final BarStyle DEFAULT_STYLE = BarStyle.SOLID;
  /** The default boss bar flags. */
  public static final BarFlag[] DEFAULT_FLAGS = new BarFlag[] {};
  /** The default visibility state for a newly created boss bar. */
  public static final boolean DEFAULT_VISIBILITY = true;
  /** The default progress value for a newly created boss bar. */
  public static final double DEFAULT_PROGRESS = 1d;

  /** The underlying Bukkit keyed boss bar instance. */
  private final KeyedBossBar bar;
  /** The optional world location associated with this boss bar. */
  private Location location;
  /** A cached view of the currently active boss bar flags. */
  private Set<BarFlag> flagCache = null;
  /** The timer associated with this boss bar, if any. */
  @SuppressWarnings("unused")
  private BossBarTimer timer;

  /**
   * Creates a new boss bar with the specified properties.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @param color the boss bar color
   * @param style the boss bar style
   * @param flags the flags applied to the boss bar
   */
  public BossBar(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
      final BarFlag... flags) {
    this.bar = Bukkit.createBossBar(key, MessageFormatter.format(title, true, false), color,
        style,
        flags);
    new BossBarCreateEvent(this).call();
  }

  /**
   * Creates a new boss bar with the specified properties.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @param color the boss bar color
   * @param style the boss bar style
   * @param flags the flags applied to the boss bar
   */
  public BossBar(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
      final Set<BarFlag> flags) {
    this(key, title, color, style, flags.toArray(BarFlag[]::new));
  }

  /**
   * Creates a new boss bar with default flags.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @param color the boss bar color
   * @param style the boss bar style
   */
  public BossBar(final NamespacedKey key, final String title, final BarColor color, final BarStyle style) {
    this(key, title, color, style, DEFAULT_FLAGS);
  }

  /**
   * Creates a new boss bar with default style and flags.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @param color the boss bar color
   */
  public BossBar(final NamespacedKey key, final String title, final BarColor color) {
    this(key, title, color, DEFAULT_STYLE, DEFAULT_FLAGS);
  }

  /**
   * Creates a new boss bar with default color, style, and flags.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   */
  public BossBar(final NamespacedKey key, final String title) {
    this(key, title, DEFAULT_COLOR, DEFAULT_STYLE, DEFAULT_FLAGS);
  }

  /**
   * Creates a new wrapper view based on an existing boss bar.
   *
   * @param base the boss bar to copy state from
   */
  public BossBar(final BossBar base) {
    this.bar = base.bar;
    bar.setTitle(base.getTitle());
    bar.setColor(base.getColor());
    bar.setStyle(base.getStyle());
    bar.setVisible(base.getVisible());
    bar.setProgress(base.getProgress());
    for (final BarFlag flag : BarFlag.values()) {
      if (base.hasFlag(flag)) {
        bar.addFlag(flag);
      }
    }
    for (final Player p : base.getPlayers()) {
      bar.addPlayer(p);
    }
    final Location otherLocation = base.getLocation();
    this.location = otherLocation == null ? null : otherLocation.clone();
  }

  /**
   * Creates a new boss bar with the specified properties.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @param color the boss bar color
   * @param style the boss bar style
   * @param flags the flags applied to the boss bar
   * @return the created boss bar
   */
  public static BossBar from(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
      final BarFlag... flags) {
    return new BossBar(key, title, color,
        style,
        flags);
  }

  /**
   * Creates a new boss bar with the specified properties.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @param color the boss bar color
   * @param style the boss bar style
   * @return the created boss bar
   */
  public static BossBar from(final NamespacedKey key, final String title, final BarColor color,
      final BarStyle style) {
    return new BossBar(key, title, color, style, DEFAULT_FLAGS);
  }

  /**
   * Creates a new boss bar with default style and flags.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @param color the boss bar color
   * @return the created boss bar
   */
  public static BossBar from(final NamespacedKey key, final String title, final BarColor color) {
    return new BossBar(key, title, color, DEFAULT_STYLE,
        DEFAULT_FLAGS);
  }

  /**
   * Creates a new boss bar with default color, style, and flags.
   *
   * @param key the unique namespaced key for the boss bar
   * @param title the title displayed to viewers
   * @return the created boss bar
   */
  public static BossBar from(final NamespacedKey key, final String title) {
    return new BossBar(key, title, DEFAULT_COLOR, DEFAULT_STYLE,
        DEFAULT_FLAGS);
  }

  /**
   * Returns the namespaced key identifying this boss bar.
   *
   * @return the boss bar key
   */
  public NamespacedKey getKey() {
    return bar.getKey();
  }

  /**
   * Returns the current boss bar title.
   *
   * @return the boss bar title
   */
  public String getTitle() {
    return bar.getTitle();
  }

  /**
   * Sets the boss bar title and emits a change event when the value changes.
   *
   * @param title the new title to display
   * @return this boss bar
   */
  public BossBar setTitle(String title) {
    title = MessageFormatter.format(title, true, false);

    final String oldTitle = bar.getTitle();
    if ((oldTitle == null && title != null) || (oldTitle != null && !oldTitle.equals(title))) {
      bar.setTitle(title);
      new BossBarNameChangedEvent(this, oldTitle, title).call();
    }
    return this;
  }

  public double getProgress() {
    return bar.getProgress();
  }

  public BossBar setProgress(double progress) {
    final double oldProgress = bar.getProgress();
    progress = Math.max(0, Math.min(1, progress));
    bar.setProgress(progress);
    if (Double.compare(oldProgress, progress) != 0) {
      new BossBarProgressEvent(this, oldProgress, progress).call();
    }
    return this;
  }

  public BarColor getColor() {
    return bar.getColor();
  }

  public BossBar setColor(final BarColor color) {
    if (color == null) {
      throw new IllegalArgumentException("Bossbar Color cannot be null");
    }
    if (bar.getColor() != color) {
      bar.setColor(color);
      new BossBarChangedEvent(this).call();
    }
    return this;
  }

  public BarStyle getStyle() {
    return bar.getStyle();
  }

  public BossBar setStyle(final BarStyle style) {
    if (style == null) {
      throw new IllegalArgumentException("Bossbar Style cannot be null");
    }
    if (bar.getStyle() != style) {
      bar.setStyle(style);
      new BossBarChangedEvent(this).call();
    }
    return this;
  }

  public boolean hasFlag(final BarFlag flag) {
    return bar.hasFlag(flag);
  }

  public Set<BarFlag> getFlags() {
    if (flagCache != null) {
      return flagCache;
    }
    final HashSet<BarFlag> flags = new HashSet<>();
    for (final BarFlag flag : BarFlag.values()) {
      if (bar.hasFlag(flag)) {
        flags.add(flag);
      }
    }
    return (flagCache = flags);
  }

  public BossBar setFlags(final Set<BarFlag> flags) {
    if (flags == null) {
      return this;
    }
    if (flags.isEmpty()) {
      for (final BarFlag flag : BarFlag.values()) {
        bar.removeFlag(flag);
      }
      flagCache = Set.of();
      new BossBarChangedEvent(this).call();
      return this;
    }
    boolean changed = false;
    for (final BarFlag flag : BarFlag.values()) {
      if (flags.contains(flag)) {
        if (!bar.hasFlag(flag)) {
          changed = true;
        }
        bar.addFlag(flag);
      } else {
        if (bar.hasFlag(flag)) {
          changed = true;
        }
        bar.removeFlag(flag);
      }
    }
    flagCache = Set.of(flags.toArray(BarFlag[]::new));
    if (changed) {
      new BossBarChangedEvent(this).call();
    }
    return this;
  }

  public BossBar addFlags(final BarFlag... flags) {
    if (flags == null || flags.length == 0) {
      return this;
    }
    boolean changed = false;
    for (final BarFlag barFlag : flags) {
      if (barFlag != null && !bar.hasFlag(barFlag)) {
        bar.addFlag(barFlag);
        changed = true;
        if (flagCache != null) {
          flagCache.add(barFlag);
        }
      }
    }
    if (changed) {
      new BossBarChangedEvent(this).call();
    }
    return this;
  }

  public BossBar addFlags(final Collection<BarFlag> flags) {
    if (flags == null || flags.isEmpty()) {
      return this;
    }
    boolean changed = false;
    for (final BarFlag flag : flags) {
      if (flag != null && !bar.hasFlag(flag)) {
        bar.addFlag(flag);
        changed = true;
        if (flagCache != null) {
          flagCache.add(flag);
        }
      }
    }
    if (changed) {
      flagCache = null;
      new BossBarChangedEvent(this).call();
    }
    return this;
  }

  public BossBar removeFlags(final BarFlag... flags) {
    if (flags == null || flags.length == 0) {
      return this;
    }
    boolean changed = false;
    for (final BarFlag barFlag : flags) {
      if (barFlag != null && bar.hasFlag(barFlag)) {
        bar.removeFlag(barFlag);
        changed = true;
        if (flagCache != null) {
          flagCache.remove(barFlag);
        }
      }
    }
    if (changed) {
      flagCache = null;
      new BossBarChangedEvent(this).call();
    }
    return this;
  }

  public BossBar removeFlags(final Collection<BarFlag> flags) {
    if (flags == null || flags.isEmpty()) {
      return this;
    }
    boolean changed = false;
    for (final BarFlag flag : flags) {
      if (flag != null && bar.hasFlag(flag)) {
        bar.removeFlag(flag);
        changed = true;
        if (flagCache != null) {
          flagCache.remove(flag);
        }
      }
    }
    if (changed) {
      flagCache = null;
      new BossBarChangedEvent(this).call();
    }
    return this;
  }

  public boolean getVisible() {
    return bar.isVisible();
  }

  public BossBar setVisible(final Player player) {
    if (player == null) {
      return this;
    }
    if (!bar.getPlayers().contains(player)) {
      bar.addPlayer(player);
      new BossBarVisibilityChangeEvent(this, true, player).call();
    }
    return this;
  }

  public BossBar setVisible(final Player player, final boolean visible) {
    if (player == null) {
      return this;
    }

    if (bar.getPlayers().contains(player) != visible) {
      if (visible) {
        bar.addPlayer(player);
      } else {
        bar.removePlayer(player);
      }
      new BossBarVisibilityChangeEvent(this, visible, player).call();
    }
    return this;
  }

  public BossBar setVisible(final boolean visible) {
    if (bar.isVisible() != visible) {
      bar.setVisible(visible);
      new BossBarVisibilityChangeEvent(this, visible).call();
    }
    return this;
  }

  public BossBar show(final Player player) {
    return setVisible(player);
  }

  public BossBar show() {
    return setVisible(true);
  }

  public BossBar hide(final Player player) {
    return setVisible(player, false);
  }

  public BossBar hide() {
    return setVisible(false);
  }

  public Location getLocation() {
    return location;
  }

  public BossBar setLocation(final Location location) {
    this.location = location;
    return this;
  }

  public boolean inDistance(final Location location) {
    if (this.location == null || location == null || this.location.getWorld() == location.getWorld()) {
      return false;
    }
    final double dx = this.location.getX() - location.getX();
    final double dy = this.location.getY() - location.getY();
    final double dz = this.location.getZ() - location.getZ();
    return Math.sqrt(dx * dx + dy * dy + dz * dz) <= (MAX_DISTANCE * MAX_DISTANCE);
  }

  public List<Player> getPlayers() {
    return bar.getPlayers();
  }

  public boolean hasPlayer(final Player player) {
    return player != null && bar.getPlayers().contains(player);
  }

  public BossBar clearPlayers() {
    for (final Player player : new ArrayList<>(bar.getPlayers())) {
      removePlayers(player);
    }
    return this;
  }

  public BossBar addPlayers(final Player player) {
    if (player == null) {
      return this;
    }
    if (!bar.getPlayers().contains(player)) {
      bar.addPlayer(player);
      new BossBarVisibilityChangeEvent(this, true, player).call();
    }
    return this;
  }

  public BossBar addPlayers(final Player... players) {
    if (players == null || players.length == 0) {
      return this;
    }
    for (final Player p : players) {
      addPlayers(p);
    }
    return this;
  }

  public BossBar addPlayers(final Collection<Player> players) {
    if (players == null || players.isEmpty()) {
      return this;
    }
    for (final Player p : players) {
      addPlayers(p);
    }
    return this;
  }

  public BossBar removePlayers(final Player player) {
    if (player == null) {
      return this;
    }
    if (bar.getPlayers().contains(player)) {
      bar.removePlayer(player);
      new BossBarVisibilityChangeEvent(this, false, player).call();
    }
    return this;
  }

  public BossBar removePlayers(final Player... players) {
    if (players == null || players.length == 0) {
      return this;
    }
    for (final Player p : players) {
      removePlayers(p);
    }
    return this;
  }

  public BossBar removePlayers(final Collection<Player> players) {
    if (players == null || players.isEmpty()) {
      return this;
    }
    for (final Player p : players) {
      removePlayers(p);
    }
    return this;
  }

  void appendTimer(final BossBarTimer timer) {
    this.timer = timer;
  }

  @Override
  public BossBar clone() {
    return new BossBar(this);
  }

  public int hashCode() {
    final int result = 31 * (31 + bar.hashCode());
    return 31 * result + ((location == null) ? 0 : location.hashCode());
  }

  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final BossBar other = (BossBar) obj;
    if (bar == null && other.bar != null || location == null && other.location != null) {
      return false;
    }
    return bar.equals(other.bar)
        && location.equals(other.location);
  }

  public String toString() {
    return "BossBar [getKey()=" + getKey() + ", title=" + getTitle() + ", progress="
        + getProgress() + ", color=" + getColor() + ", style=" + getStyle() + ", flags=" + getFlags()
        + ", visible=" + getVisible() + ", location=" + location
        + ", players=" + getPlayers() + "]";
  }

}
