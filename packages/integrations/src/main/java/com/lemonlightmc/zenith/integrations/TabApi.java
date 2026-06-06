package com.lemonlightmc.zenith.integrations;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.placeholder.Placeholder;

public class TabApi {

  public static TabAPI get() {
    return TabAPI.getInstance();
  }

  public static TabAPI getOrNull() {
    try {
      return TabAPI.getInstance();
    } catch (final Exception e) {
      return null;
    }
  }

  public static TabAPI getOrThrow() {
    return TabAPI.getInstance();
  }

  public static boolean isLoaded() {
    try {
      return TabAPI.getInstance() != null;
    } catch (final Exception e) {
      return false;
    }
  }

  // Player
  public static TabPlayer getPlayer(final String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    return TabAPI.getInstance().getPlayer(name);
  }

  public static TabPlayer getPlayer(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return TabAPI.getInstance().getPlayer(uuid);
  }

  public static TabPlayer[] getOnlinePlayers() {
    return TabAPI.getInstance().getOnlinePlayers();
  }

  // Team
  public static void forceTeamName(final TabPlayer player, final String name) {
    if (player == null || name == null || name.isEmpty()) {
      return;
    }
    TabAPI.getInstance().getSortingManager().forceTeamName(player, name);
  }

  public static void forceTeamName(final UUID uuid, final String name) {
    forceTeamName(getPlayer(uuid), name);
  }

  public static String getOriginalTeamName(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getSortingManager().getOriginalTeamName(player);
  }

  public static String getOriginalTeamName(final UUID uuid) {
    return getOriginalTeamName(getPlayer(uuid));
  }

  public static String getForcedTeamName(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getSortingManager().getForcedTeamName(player);
  }

  public static String getForcedTeamName(final UUID uuid) {
    return getForcedTeamName(getPlayer(uuid));
  }

  // Placeholders
  public static Placeholder getPlaceholder(final String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    return TabAPI.getInstance().getPlaceholderManager().getPlaceholder(name);
  }

  public static void registerServerPlaceholder(final String name, final int refresh, final Supplier<String> supplier) {
    if (name == null || name.isEmpty() || supplier == null) {
      return;
    }
    if (refresh < 0 || refresh > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Refresh must be between 0 and " + Integer.MAX_VALUE);
    }
    TabAPI.getInstance().getPlaceholderManager().registerServerPlaceholder(name, refresh, supplier);
  }

  public static void registerPlayerPlaceholder(final String name, final int refresh,
      final Function<TabPlayer, String> function) {
    if (name == null || name.isEmpty() || function == null) {
      return;
    }
    if (refresh < 0 || refresh > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Refresh must be between 0 and " + Integer.MAX_VALUE);
    }
    TabAPI.getInstance().getPlaceholderManager().registerPlayerPlaceholder(name, refresh, function);
  }

  public static void registerPlayerPlaceholder(final String name, final int refresh,
      final BiFunction<TabPlayer, TabPlayer, String> function) {
    if (name == null || name.isEmpty() || function == null) {
      return;
    }
    if (refresh < 0 || refresh > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Refresh must be between 0 and " + Integer.MAX_VALUE);
    }
    TabAPI.getInstance().getPlaceholderManager().registerRelationalPlaceholder(name, refresh, function);
  }

  public static void unregisterPlaceholder(final Placeholder placeholder) {
    if (placeholder == null) {
      return;
    }
    TabAPI.getInstance().getPlaceholderManager().unregisterPlaceholder(placeholder);
  }

  public static void unregisterPlaceholder(final String placeholder) {
    if (placeholder == null || placeholder.isEmpty()) {
      return;
    }
    TabAPI.getInstance().getPlaceholderManager().unregisterPlaceholder(placeholder);
  }
}
