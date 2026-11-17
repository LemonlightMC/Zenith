package com.lemonlightmc.zenith.apis.bossbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.apis.bossbar.events.BossBarAddEvent;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarRemoveEvent;

/**
 * Provides registry and management utilities for Zenith {@link BossBar} instances.
 *
 * <p>The API tracks boss bars globally and per player, and dispatches related boss bar events when bars are added or
 * removed.
 */
public class BossbarAPI {
  /** Registered boss bars indexed by their namespaced key. */
  private static final Map<NamespacedKey, BossBar> registry = new ConcurrentHashMap<>();
  /** Boss bars currently associated with each player. */
  private static final Map<UUID, Collection<BossBar>> barMap = new ConcurrentHashMap<>();

  /**
   * Creates a new boss bar builder for the given key.
   *
   * @param key the namespaced key for the boss bar
   * @return a new boss bar builder
   */
  public static BossBarBuilder builder(final NamespacedKey key) {
    return new BossBarBuilder(key);
  }

  /**
   * Creates a new boss bar builder without a preconfigured key.
   *
   * @return a new boss bar builder
   */
  public static BossBarBuilder builder() {
    return new BossBarBuilder();
  }

  /**
   * Returns an iterator over Bukkit's registered keyed boss bars.
   *
   * @return an iterator over registered keyed boss bars
   */
  public static Iterator<KeyedBossBar> getBossBars() {
    return Bukkit.getBossBars();
  }

  /**
   * Returns the registered Zenith boss bar for the given key.
   *
   * @param key the boss bar key
   * @return the registered boss bar, or {@code null} if none is registered
   */
  public static BossBar getBossBar(final NamespacedKey key) {
    return registry.get(key);
  }

  /**
   * Returns the boss bars currently associated with a player.
   *
   * @param player the player to inspect
   * @return the player's associated boss bars, or an empty collection if none are tracked
   */
  public static Collection<BossBar> getBossBars(final Player player) {
    if (player == null) {
      return List.of();
    }
    return barMap.get(player.getUniqueId());
  }

  /**
   * Returns whether a boss bar is registered for the given key.
   *
   * @param key the boss bar key
   * @return {@code true} if a boss bar is registered; {@code false} otherwise
   */
  public static boolean hasBar(final NamespacedKey key) {
    return registry.containsKey(key);
  }

  /**
   * Returns the boss bar associated with a player for the given key, if present.
   *
   * @param player the player to inspect
   * @param key the boss bar key
   * @return the matching boss bar, or {@code null} if not found
   */
  public static BossBar hasBar(final Player player, final NamespacedKey key) {
    if (player == null || key == null) {
      return null;
    }
    final Collection<BossBar> list = barMap.get(player.getUniqueId());
    if (list == null || list.isEmpty()) {
      return null;
    }
    for (final BossBar bossBar : list) {
      if (bossBar.getKey().equals(key)) {
        return bossBar;
      }
    }
    return null;
  }

  /**
   * Returns whether a player has any tracked boss bars.
   *
   * @param player the player to inspect
   * @return {@code true} if the player has tracked boss bars; {@code false} otherwise
   */
  public static boolean hasBars(final Player player) {
    if (player == null) {
      return false;
    }
    final Collection<BossBar> list = barMap.get(player.getUniqueId());
    return list != null && !list.isEmpty();
  }

  /**
   * Removes a registered boss bar globally by key.
   *
   * @param key the boss bar key to remove
   */
  public static void removeBar(final NamespacedKey key) {
    if (key == null) {
      return;
    }
    Bukkit.removeBossBar(key);
    final BossBar bar = registry.remove(key);
    if (bar != null) {
      new BossBarRemoveEvent(bar).call();
    }
    for (final Map.Entry<UUID, Collection<BossBar>> entry : barMap.entrySet()) {
      entry.getValue().remove(bar);
      if (entry.getValue().isEmpty()) {
        barMap.remove(entry.getKey());
      }
    }
  }

  /**
   * Removes a registered boss bar association for a specific player.
   *
   * @param player the player to remove the bar from
   * @param key the boss bar key to remove
   */
  public static void removeBar(final Player player, final NamespacedKey key) {
    if (key == null) {
      return;
    }
    Bukkit.removeBossBar(key);
    final BossBar bar = registry.remove(key);
    if (bar == null) {
      return;
    }
    removeBarForPlayer(player, bar);
    new BossBarRemoveEvent(bar, player).call();
  }

  /**
   * Removes all tracked boss bars from a player.
   *
   * @param player the player whose tracked boss bars should be removed
   */
  public static void removeAllBars(final Player player) {
    if (player == null) {
      return;
    }
    final Collection<BossBar> bars = barMap.get(player.getUniqueId());
    if (bars == null || bars.isEmpty()) {
      return;
    }
    for (final BossBar bossBar : new ArrayList<>(bars)) {
      removeBarForPlayer(player, bossBar);
      new BossBarRemoveEvent(bossBar, player).call();
    }
  }

  /**
   * Adds a boss bar to a player and tracks the association.
   *
   * @param player the player to receive the boss bar
   * @param bossBar the boss bar to add
   */
  public static void addBar(final Player player, final BossBar bossBar) {
    if (bossBar == null || player == null) {
      return;
    }
    addBarForPlayer(player, bossBar);
    new BossBarAddEvent(bossBar, player).call();
  }

  /**
   * Sets the progress of a registered boss bar.
   *
   * @param key the boss bar key
   * @param percentage the new progress value
   */
  public static void setProgress(final NamespacedKey key, final float percentage) {
    final BossBar bar = getBossBar(key);
    if (bar == null) {
      return;
    }
    bar.setProgress(percentage);
  }

  /**
   * Returns the progress of a registered boss bar.
   *
   * @param key the boss bar key
   * @return the boss bar progress, or {@code -1} if no matching bar is registered
   */
  public static double getProgress(final NamespacedKey key) {
    final BossBar bar = getBossBar(key);
    if (bar == null) {
      return -1;
    }
    return bar.getProgress();
  }

  /**
   * Adds a boss bar to a player's tracked collection.
   *
   * @param player the player receiving the boss bar
   * @param bossBar the boss bar to track
   */
  private static void addBarForPlayer(final Player player, final BossBar bossBar) {
    bossBar.addPlayers(player);
    barMap.compute(player.getUniqueId(), (uuid, collection) -> {
      if (collection == null) {
        collection = new ArrayList<>();
      }
      collection.add(bossBar);
      return collection;
    });
  }

  /**
   * Removes a boss bar from a player's tracked collection.
   *
   * @param player the player to update
   * @param bossBar the boss bar to remove
   */
  private static void removeBarForPlayer(final Player player, final BossBar bossBar) {
    bossBar.removePlayers(player);

    final Collection<BossBar> collection = barMap.get(player.getUniqueId());
    if (collection != null) {
      collection.remove(bossBar);
      if (!collection.isEmpty()) {
        barMap.put(player.getUniqueId(), collection);
      } else {
        barMap.remove(player.getUniqueId());
      }
    }
  }

}
