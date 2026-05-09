package com.lemonlightmc.zenith.apis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.messages.MessageFormatter;

public class BossbarAPI {
  private static final Map<NamespacedKey, IBossBar> registry = new HashMap<>();

  public static final double MAX_PROGRESS = 1d;
  public static final double MIN_PROGRESS = 0d;
  public static final double MAX_DISTANCE = 30d;

  public static final BarColor DEFAULT_COLOR = BarColor.PURPLE;
  public static final BarStyle DEFAULT_STYLE = BarStyle.SOLID;
  public static final BarFlag[] DEFAULT_FLAGS = new BarFlag[] {};
  public static final boolean DEFAULT_VISIBILITY = true;
  public static final double DEFAULT_PROGRESS = 1d;

  public static IBossBar getBar(final NamespacedKey key) {
    return registry.get(key);
  }

  public static boolean hasBar(final NamespacedKey key) {
    return registry.containsKey(key);
  }

  public static void removeBar(final NamespacedKey key) {
    registry.remove(key);
    Bukkit.removeBossBar(key);
  }

  public static BossBarBuilder builder(final NamespacedKey key) {
    return new BossBarBuilder(key);
  }

  public static BossBarBuilder builder() {
    return new BossBarBuilder();
  }

  public static class BossBar implements IBossBar {
    private KeyedBossBar bar;
    private Set<IBossBarListener> listeners = new HashSet<>();
    private Location location;
    private Set<BarFlag> flagCache = null;

    public BossBar(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
        final BarFlag... flags) {
      this.bar = Bukkit.createBossBar(key, MessageFormatter.format(title, true, false), color,
          style,
          flags);
      registry.put(key, this);
    }

    public BossBar(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
        final Set<BarFlag> flags) {
      this(key, title, color, style, flags.toArray(BarFlag[]::new));
    }

    public BossBar(final NamespacedKey key, final String title, final BarColor color, final BarStyle style) {
      this(key, title, color, style, DEFAULT_FLAGS);
    }

    public BossBar(final NamespacedKey key, final String title, final BarColor color) {
      this(key, title, color, DEFAULT_STYLE, DEFAULT_FLAGS);
    }

    public BossBar(final NamespacedKey key, final String title) {
      this(key, title, DEFAULT_COLOR, DEFAULT_STYLE, DEFAULT_FLAGS);
    }

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
      this.listeners = base.getListeners();
      this.location = base.getLocation().clone();
    }

    public static BossBar from(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
        final BarFlag... flags) {
      return new BossBar(key, title, color,
          style,
          flags);
    }

    public static BossBar from(final NamespacedKey key, final String title, final BarColor color,
        final BarStyle style) {
      return new BossBar(key, title, color, style, DEFAULT_FLAGS);
    }

    public static BossBar from(final NamespacedKey key, final String title, final BarColor color) {
      return new BossBar(key, title, color, DEFAULT_STYLE,
          DEFAULT_FLAGS);
    }

    public static BossBar from(final NamespacedKey key, final String title) {
      return new BossBar(key, title, DEFAULT_COLOR, DEFAULT_STYLE,
          DEFAULT_FLAGS);
    }

    @Override
    public NamespacedKey getKey() {
      return bar.getKey();
    }

    @Override
    public String getTitle() {
      return bar.getTitle();
    }

    @Override
    public BossBar setTitle(final String title) {
      bar.setTitle(title);
      return this;
    }

    @Override
    public double getProgress() {
      return bar.getProgress();
    }

    @Override
    public BossBar setProgress(final double progress) {
      bar.setProgress(Math.max(0, Math.min(1, progress)));
      return this;
    }

    @Override
    public BarColor getColor() {
      return bar.getColor();
    }

    @Override
    public BossBar setColor(final BarColor color) {
      if (color == null) {
        throw new IllegalArgumentException("Bossbar Color cannot be null");
      }
      bar.setColor(color);
      return this;
    }

    @Override
    public BarStyle getStyle() {
      return bar.getStyle();
    }

    @Override
    public BossBar setStyle(final BarStyle style) {
      if (style == null) {
        throw new IllegalArgumentException("Bossbar Style cannot be null");
      }
      bar.setStyle(style);
      return this;
    }

    @Override
    public boolean hasFlag(final BarFlag flag) {
      return bar.hasFlag(flag);
    }

    @Override
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

    @Override
    public BossBar setFlags(final Set<BarFlag> flags) {
      if (flags == null || flags.isEmpty()) {
        for (final BarFlag flag : BarFlag.values()) {
          bar.removeFlag(flag);
        }
        flagCache = null;
        return this;
      }
      for (final BarFlag flag : BarFlag.values()) {
        if (flags.contains(flag)) {
          bar.addFlag(flag);
        } else {
          bar.removeFlag(flag);
        }
      }
      flagCache = flags;
      return this;
    }

    @Override
    public BossBar addFlags(final BarFlag... flags) {
      if (flags == null || flags.length == 0) {
        return this;
      }
      for (final BarFlag barFlag : flags) {
        bar.addFlag(barFlag);
        if (flagCache != null) {
          flagCache.add(barFlag);
        }
      }
      return this;
    }

    @Override
    public BossBar addFlags(final Collection<BarFlag> flags) {
      if (flags == null || flags.isEmpty()) {
        return this;
      }
      for (final BarFlag flag : flags) {
        bar.addFlag(flag);
        if (flagCache != null) {
          flagCache.add(flag);
        }
      }
      flagCache = null;
      return this;
    }

    @Override
    public BossBar removeFlags(final BarFlag... flags) {
      if (flags == null || flags.length == 0) {
        return this;
      }
      for (final BarFlag barFlag : flags) {
        bar.removeFlag(barFlag);
        if (flagCache != null) {
          flagCache.remove(barFlag);
        }
      }
      flagCache = null;
      return this;
    }

    @Override
    public BossBar removeFlags(final Collection<BarFlag> flags) {
      if (flags == null || flags.isEmpty()) {
        return this;
      }
      for (final BarFlag flag : flags) {
        bar.removeFlag(flag);
        if (flagCache != null) {
          flagCache.remove(flag);
        }
      }
      flagCache = null;
      return this;
    }

    @Override
    public boolean getVisible() {
      return bar.isVisible();
    }

    @Override
    public BossBar setVisible(final Player player) {
      bar.addPlayer(player);
      return this;
    }

    @Override
    public BossBar setVisible(final Player player, final boolean visible) {
      if (visible) {
        bar.addPlayer(player);
      } else {
        bar.removePlayer(player);
      }
      return this;
    }

    @Override
    public BossBar setVisible(final boolean visible) {
      bar.setVisible(visible);
      return this;
    }

    @Override
    public BossBar show(final Player player) {
      bar.addPlayer(player);
      return this;
    }

    @Override
    public BossBar show() {
      bar.setVisible(true);
      return this;
    }

    @Override
    public BossBar hide(final Player player) {
      bar.removePlayer(player);
      return this;
    }

    @Override
    public BossBar hide() {
      bar.setVisible(false);
      return this;
    }

    @Override
    public Location getLocation() {
      return location;
    }

    @Override
    public BossBar setLocation(final Location location) {
      this.location = location;
      return this;
    }

    @Override
    public boolean inDistance(final Location location) {
      if (this.location == null || location == null || this.location.getWorld() == location.getWorld()) {
        return false;
      }
      final double dx = this.location.getX() - location.getX();
      final double dy = this.location.getY() - location.getY();
      final double dz = this.location.getZ() - location.getZ();
      return Math.sqrt(dx * dx + dy * dy + dz * dz) <= (MAX_DISTANCE * MAX_DISTANCE);
    }

    @Override
    public Set<IBossBarListener> getListeners() {
      return listeners;
    }

    @Override
    public BossBar clearListener() {
      this.listeners.clear();
      return this;
    }

    @Override
    public BossBar setListeners(final Set<IBossBarListener> listeners) {
      this.listeners = listeners;
      return this;
    }

    @Override
    public boolean hasListener(final IBossBarListener listener) {
      return listeners != null && this.listeners.contains(listener);
    }

    @Override
    public BossBar addListeners(final IBossBarListener... listener) {
      if (listener == null || listener.length == 0) {
        return this;
      }
      if (this.listeners == null) {
        this.listeners = new HashSet<>();
      }
      for (final IBossBarListener l : listener) {
        this.listeners.add(l);
      }
      return this;
    }

    @Override
    public BossBar addListeners(final Collection<IBossBarListener> listener) {
      if (listener == null || listener.isEmpty()) {
        return this;
      }
      if (this.listeners == null) {
        this.listeners = new HashSet<>();
      }
      for (final IBossBarListener l : listener) {
        this.listeners.add(l);
      }
      return this;
    }

    @Override
    public BossBar removeListeners(final IBossBarListener... listener) {
      if (listener == null || listener.length == 0 || this.listeners == null) {
        return this;
      }
      for (final IBossBarListener l : listener) {
        this.listeners.remove(l);
      }
      return this;
    }

    @Override
    public BossBar removeListeners(final Collection<IBossBarListener> listener) {
      if (listener == null || listener.isEmpty() || this.listeners == null) {
        return this;
      }
      for (final IBossBarListener l : listener) {
        this.listeners.remove(l);
      }
      return this;
    }

    @Override
    public List<Player> getPlayers() {
      return bar.getPlayers();
    }

    @Override
    public boolean hasPlayer(final Player player) {
      return player != null && bar.getPlayers().contains(player);
    }

    @Override
    public BossBar clearPlayers() {
      bar.removeAll();
      return this;
    }

    @Override
    public BossBar addPlayers(final Player player) {
      if (player == null) {
        return this;
      }
      bar.addPlayer(player);
      return this;
    }

    @Override
    public BossBar addPlayers(final Player... players) {
      if (players == null || players.length == 0) {
        return this;
      }
      for (final Player p : players) {
        bar.addPlayer(p);
      }
      return this;
    }

    @Override
    public BossBar addPlayers(final Collection<Player> players) {
      if (players == null || players.isEmpty()) {
        return this;
      }
      for (final Player p : players) {
        bar.addPlayer(p);
      }
      return this;
    }

    @Override
    public BossBar removePlayers(final Player player) {
      if (player == null) {
        return this;
      }
      bar.removePlayer(player);
      return this;
    }

    @Override
    public BossBar removePlayers(final Player... players) {
      if (players == null || players.length == 0) {
        return this;
      }
      for (final Player p : players) {
        bar.addPlayer(p);
      }
      return this;
    }

    @Override
    public BossBar removePlayers(final Collection<Player> players) {
      if (players == null || players.isEmpty()) {
        return this;
      }
      for (final Player p : players) {
        bar.removePlayer(p);
      }
      return this;
    }

    @Override
    public BossBar clone() {
      return new BossBar(this);
    }

    @Override
    public int hashCode() {
      int result = 31 * (31 + bar.hashCode()) + ((listeners == null) ? 0 : listeners.hashCode());
      return 31 * result + ((location == null) ? 0 : location.hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final BossBar other = (BossBar) obj;
      if (bar == null && other.bar != null || listeners == null && other.listeners != null
          || location == null && other.location != null) {
        return false;
      }
      return bar.equals(other.bar) && listeners.equals(other.listeners)
          && location.equals(other.location);
    }

    @Override
    public String toString() {
      return "BossBar [getKey()=" + getKey() + ", title=" + getTitle() + ", progress="
          + getProgress() + ", color=" + getColor() + ", style=" + getStyle() + ", flags=" + getFlags()
          + ", visible=" + getVisible() + ", location=" + location + ", listeners=" + listeners
          + ", players=" + getPlayers() + "]";
    }
  }

  public static class BossBarBuilder implements Builder<BossBar> {
    private NamespacedKey key;
    private String title;
    private BarColor color = DEFAULT_COLOR;
    private BarStyle style = DEFAULT_STYLE;
    private boolean visible = DEFAULT_VISIBILITY;
    private double progress = DEFAULT_PROGRESS;
    private Set<BarFlag> flags = new HashSet<>();
    private List<Player> players = new ArrayList<>();

    public BossBarBuilder(final NamespacedKey key) {
      if (key == null) {
        throw new IllegalArgumentException("Bossbar Key cannot be null");
      }
      this.key = key;
    }

    public BossBarBuilder() {
    }

    public static BossBarBuilder create(final NamespacedKey key) {
      return new BossBarBuilder(key);
    }

    public static BossBarBuilder create() {
      return new BossBarBuilder();
    }

    public BossBarBuilder key(final NamespacedKey key) {
      if (key == null) {
        throw new IllegalArgumentException("Bossbar Key cannot be null");
      }
      this.key = key;
      return this;
    }

    public NamespacedKey key() {
      return key;
    }

    public BossBarBuilder title(final String title) {
      this.title = title;
      return this;
    }

    public String title() {
      return title;
    }

    public BossBarBuilder color(final BarColor color) {
      this.color = color;
      return this;
    }

    public BarColor color() {
      return color;
    }

    public BossBarBuilder style(final BarStyle style) {
      this.style = style;
      return this;
    }

    public BarStyle style() {
      return style;
    }

    public BossBarBuilder visible(final boolean visible) {
      this.visible = visible;
      return this;
    }

    public boolean visible() {
      return visible;
    }

    public BossBarBuilder progress(final double progress) {
      this.progress = progress;
      return this;
    }

    public double progress() {
      return progress;
    }

    public Set<BarFlag> flags() {
      return flags;
    }

    public BossBarBuilder flags(final BarFlag... flag) {
      this.flags.addAll(List.of(flag));
      return this;
    }

    public BossBarBuilder flags(final BarFlag flag) {
      this.flags.add(flag);
      return this;
    }

    public BossBarBuilder removeFlag(final BarFlag flag) {
      this.flags.remove(flag);
      return this;
    }

    public BossBarBuilder setFlags(final Set<BarFlag> flags) {
      this.flags = flags;
      return this;
    }

    public BossBarBuilder clearFlags() {
      this.flags.clear();
      return this;
    }

    public List<Player> players() {
      return players;
    }

    public BossBarBuilder players(final Player... players) {
      this.players.addAll(List.of(players));
      return this;
    }

    public BossBarBuilder players(final Player player) {
      this.players.add(player);
      return this;
    }

    public BossBarBuilder removePlayer(final Player player) {
      this.players.remove(player);
      return this;
    }

    public BossBarBuilder setPlayers(final List<Player> players) {
      this.players = players;
      return this;
    }

    public BossBar build() {
      BossBar bar = new BossBar(key, title, color, style, flags);
      bar.setProgress(this.progress);
      bar.setVisible(visible);
      bar.addPlayers(players);
      return bar;
    }
  }

  public static interface IBossBar extends IBossBarListener, Cloneable {

    public NamespacedKey getKey();

    public String getTitle();

    public IBossBar setTitle(final String title);

    public double getProgress();

    public IBossBar setProgress(final double progress);

    public BarColor getColor();

    public IBossBar setColor(final BarColor color);

    public BarStyle getStyle();

    public IBossBar setStyle(final BarStyle style);

    public Set<BarFlag> getFlags();

    public IBossBar setFlags(final Set<BarFlag> flags);

    public boolean hasFlag(final BarFlag flag);

    public IBossBar addFlags(final BarFlag... flags);

    public IBossBar addFlags(final Collection<BarFlag> flags);

    public IBossBar removeFlags(final BarFlag... flags);

    public IBossBar removeFlags(final Collection<BarFlag> flags);

    public boolean getVisible();

    public IBossBar setVisible(Player player);

    public IBossBar setVisible(Player player, boolean visible);

    public IBossBar setVisible(boolean visible);

    public IBossBar show(Player player);

    public IBossBar hide(Player player);

    public IBossBar hide();

    public IBossBar show();

    public Location getLocation();

    public IBossBar setLocation(Location location);

    public boolean inDistance(Location location);

    public Set<IBossBarListener> getListeners();

    public IBossBar clearListener();

    public IBossBar setListeners(final Set<IBossBarListener> listener);

    public boolean hasListener(final IBossBarListener listener);

    public IBossBar addListeners(final IBossBarListener... listener);

    public IBossBar addListeners(final Collection<IBossBarListener> listener);

    public IBossBar removeListeners(final IBossBarListener... listener);

    public IBossBar removeListeners(final Collection<IBossBarListener> listener);

    public List<Player> getPlayers();

    public boolean hasPlayer(Player player);

    public IBossBar addPlayers(Player player);

    public IBossBar addPlayers(Player... players);

    public IBossBar addPlayers(Collection<Player> players);

    public IBossBar removePlayers(Player... players);

    public IBossBar removePlayers(Player player);

    public IBossBar removePlayers(Collection<Player> players);

    public IBossBar clearPlayers();

    public IBossBar clone();

    public String toString();

    public boolean equals(final Object obj);

    public int hashCode();
  }

  public static interface IBossBarListener {

    default void bossBarNameChanged(final IBossBar bar, final String oldName, final String newName) {
    }

    default void bossBarProgressChanged(final IBossBar bar, final float oldProgress, final float newProgress) {
      this.bossBarPercentChanged(bar, oldProgress, newProgress);
    }

    default void bossBarPercentChanged(final IBossBar bar, final float oldProgress, final float newProgress) {
    }

    default void bossBarColorChanged(final IBossBar bar, final BarColor oldColor,
        final BarColor newColor) {
    }

    default void bossBarOverlayChanged(final IBossBar bar, final BarStyle oldOverlay,
        final BarStyle newOverlay) {
    }

    default void bossBarFlagsChanged(final IBossBar bar, final Set<BarFlag> flagsAdded,
        final Set<BarFlag> flagsRemoved) {
    }
  }
}
