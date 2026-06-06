package com.lemonlightmc.zenith.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerUtils {

  public static OfflinePlayer getOfflinePlayer(final String name) {
    final UUID uuid = UUIDUtils.toUUID(name);
    return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
  }

  public static OfflinePlayer getOfflinePlayer(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return Bukkit.getOfflinePlayer(uuid);
  }

  public static Player getPlayer(final String name) {
    if (name == null || name.length() == 0) {
      return null;
    }
    return Bukkit.getPlayerExact(UUIDUtils.toName(name));
  }

  public static OfflinePlayer getPlayer(final String name, final boolean allowOffline) {
    final Player p = getPlayer(name);
    if (p != null || !allowOffline) {
      return p;
    }
    return getOfflinePlayer(name);
  }

  public static List<String> getOnlinePlayerNames() {
    return Bukkit.getOnlinePlayers().stream().filter(p -> p == null).map(p -> p.getName()).toList();
  }

  public static Player[] getOnlinePlayers() {
    return Bukkit.getOnlinePlayers().toArray(Player[]::new);
  }

  public static Stream<? extends Player> streamOnline() {
    return Bukkit.getOnlinePlayers().stream();
  }

  public static void forEachOnline(final Consumer<Player> consumer) {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      consumer.accept(player);
    }
  }

  public static OfflinePlayer[] getOfflinePlayers() {
    return Bukkit.getOfflinePlayers().clone();
  }

  public static Stream<OfflinePlayer> streamOffline() {
    return Arrays.stream(Bukkit.getOfflinePlayers());
  }

  public static void forEachOffline(final Consumer<OfflinePlayer> consumer) {
    for (final OfflinePlayer player : Bukkit.getOfflinePlayers()) {
      consumer.accept(player);
    }
  }

  public static Player[] getPlayersInRadius(final Player player, final int size) {
    final List<Entity> players = player.getNearbyEntities(size, size, size);
    players.removeIf(e -> !(e instanceof Player));
    return players.toArray(Player[]::new);
  }

  public static boolean isValidPlayer(final OfflinePlayer player) {
    if (player == null || player.getName() == null || player.getName().length() == 0) {
      return false;
    }
    final String name = UUIDUtils.fetchName(player.getUniqueId().toString());
    return name != null && name.length() != 0 && name == player.getName();
  }

  public static boolean isValidPlayerName(final String playerName) {
    return UUIDUtils.toName(playerName) != null;
  }

  public static boolean isValidPlayerUUID(final String uuidStr) {
    return UUIDUtils.isUUID(uuidStr);
  }

  public static boolean isValidPlayerUUID(final UUID uuid) {
    return UUIDUtils.isUUID(uuid);
  }

  public static Locale getPlayerLocale(final Player player) {
    if (player == null) {
      return null;
    }
    return StringUtils.parseLocale(player.getLocale());
  }

}
