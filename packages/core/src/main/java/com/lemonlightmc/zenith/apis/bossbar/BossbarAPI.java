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

import com.lemonlightmc.zenith.apis.bossbar.BossBar.BossBarBuilder;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarAddEvent;
import com.lemonlightmc.zenith.apis.bossbar.events.BossBarRemoveEvent;

public class BossbarAPI {
  private static final Map<NamespacedKey, BossBar> registry = new ConcurrentHashMap<>();
  private static final Map<UUID, Collection<BossBar>> barMap = new ConcurrentHashMap<>();

  public static BossBarBuilder builder(final NamespacedKey key) {
    return new BossBarBuilder(key);
  }

  public static BossBarBuilder builder() {
    return new BossBarBuilder();
  }

  public static Iterator<KeyedBossBar> getBossBars() {
    return Bukkit.getBossBars();
  }

  public static BossBar getBossBar(final NamespacedKey key) {
    return registry.get(key);
  }

  public static Collection<BossBar> getBossBars(final Player player) {
    if (player == null) {
      return List.of();
    }
    return barMap.get(player.getUniqueId());
  }

  public static boolean hasBar(final NamespacedKey key) {
    return registry.containsKey(key);
  }

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

  public static boolean hasBars(final Player player) {
    if (player == null) {
      return false;
    }
    final Collection<BossBar> list = barMap.get(player.getUniqueId());
    return list != null && !list.isEmpty();
  }

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

  public static void addBar(final Player player, final BossBar bossBar) {
    if (bossBar == null || player == null) {
      return;
    }
    addBarForPlayer(player, bossBar);
    new BossBarAddEvent(bossBar, player).call();
  }

  public static void setProgress(final NamespacedKey key, final float percentage) {
    final BossBar bar = getBossBar(key);
    if (bar == null) {
      return;
    }
    bar.setProgress(percentage);
  }

  /**
   * @param player {@link Player}
   * @return The progress of the player's bar
   */
  public static double getProgress(final NamespacedKey key) {
    final BossBar bar = getBossBar(key);
    if (bar == null) {
      return -1;
    }
    return bar.getProgress();
  }

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
