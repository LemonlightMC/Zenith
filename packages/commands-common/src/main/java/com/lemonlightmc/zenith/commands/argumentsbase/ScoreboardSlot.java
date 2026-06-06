package com.lemonlightmc.zenith.commands.argumentsbase;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.scoreboard.DisplaySlot;

public enum ScoreboardSlot implements Keyed {

  PLAYER_LIST("list", 0x0),
  SIDEBAR("sidebar", 0x1),
  LEGACY_BELOW_NAME("belowName", 0x2), // Was replaced with below_name in 1.20.2
  BELOW_NAME("below_name", 0x2),
  SIDEBAR_TEAM_BLACK("sidebar.team.black", 0x0, ChatColor.BLACK),
  SIDEBAR_TEAM_DARK_BLUE("sidebar.team.dark_blue", 0x1, ChatColor.DARK_BLUE),
  SIDEBAR_TEAM_DARK_GREEN("sidebar.team.dark_green", 0x2, ChatColor.DARK_GREEN),
  SIDEBAR_TEAM_DARK_AQUA("sidebar.team.dark_aqua", 0x3, ChatColor.DARK_AQUA),
  SIDEBAR_TEAM_DARK_RED("sidebar.team.dark_red", 0x4, ChatColor.DARK_RED),
  SIDEBAR_TEAM_DARK_PURPLE("sidebar.team.dark_purple", 0x5, ChatColor.DARK_PURPLE),
  SIDEBAR_TEAM_GOLD("sidebar.team.gold", 0x6, ChatColor.GOLD),
  SIDEBAR_TEAM_GRAY("sidebar.team.gray", 0x7, ChatColor.GRAY),
  SIDEBAR_TEAM_DARK_GRAY("sidebar.team.dark_gray", 0x8, ChatColor.DARK_GRAY),
  SIDEBAR_TEAM_BLUE("sidebar.team.blue", 0x9, ChatColor.BLUE),
  SIDEBAR_TEAM_GREEN("sidebar.team.green", 0xA, ChatColor.GREEN),
  SIDEBAR_TEAM_AQUA("sidebar.team.aqua", 0xB, ChatColor.AQUA),
  SIDEBAR_TEAM_RED("sidebar.team.red", 0xC, ChatColor.RED),
  SIDEBAR_TEAM_LIGHT_PURPLE("sidebar.team.light_purple", 0xD, ChatColor.LIGHT_PURPLE),
  SIDEBAR_TEAM_YELLOW("sidebar.team.yellow", 0xE, ChatColor.YELLOW),
  SIDEBAR_TEAM_WHITE("sidebar.team.white", 0xF, ChatColor.WHITE);

  private final String key;
  private final int internal;
  private final ChatColor teamColor;

  ScoreboardSlot(final String key, final int internal) {
    this.key = key;
    this.internal = internal;
    this.teamColor = null;
  }

  ScoreboardSlot(final String key, final int internal, final ChatColor color) {
    this.key = key;
    this.internal = internal;
    this.teamColor = color;
  }

  private static boolean isLegacy() {
    return DisplaySlot.values().length == 3;
  }

  private static boolean isPaper() {
    for (final DisplaySlot slot : DisplaySlot.values()) {
      if (slot.name().startsWith("SIDEBAR_TEAM")) {
        return true;
      }
    }
    return false;
  }

  public static ScoreboardSlot ofMinecraft(final int i) {
    return switch (i) {
      case 0 -> ScoreboardSlot.PLAYER_LIST;
      case 1 -> ScoreboardSlot.SIDEBAR;
      case 2 -> ScoreboardSlot.BELOW_NAME;
      default -> {
        for (final ScoreboardSlot slot : ScoreboardSlot.values()) {
          if (i - 3 == slot.internal && slot.hasTeamColor()) {
            yield slot;
          }
        }
        throw new IllegalStateException("Invalid ScoreboardSlot with internal index " + i);
      }
    };
  }

  public static ScoreboardSlot of(final DisplaySlot slot) {
    if (isLegacy()) {
      // Legacy
      return switch (slot) {
        case PLAYER_LIST -> ScoreboardSlot.PLAYER_LIST;
        case SIDEBAR -> ScoreboardSlot.SIDEBAR;
        case BELOW_NAME -> ScoreboardSlot.BELOW_NAME;
        default -> throw new IllegalArgumentException("Unexpected value: " + slot);
      };
    } else {
      // Non-legacy
      return switch (slot) {
        case PLAYER_LIST -> ScoreboardSlot.PLAYER_LIST;
        case SIDEBAR -> ScoreboardSlot.SIDEBAR;
        case BELOW_NAME -> ScoreboardSlot.BELOW_NAME;
        default -> {
          if (isPaper()) {
            yield ScoreboardSlot.valueOf(slot.name());
          } else {
            yield ScoreboardSlot.valueOf("SIDEBAR_TEAM_" + slot.name().substring(8));
          }
        }
      };
    }
  }

  public static ScoreboardSlot of(final String str) {
    if (str == null || str.length() == 0) {
      return null;
    }
    for (final ScoreboardSlot slot : ScoreboardSlot.values()) {
      if (slot.toString().equals(str)) {
        return slot;
      }
    }
    return null;
  }

  public static ScoreboardSlot ofTeamColor(final ChatColor color) {
    for (final ScoreboardSlot slot : ScoreboardSlot.values()) {
      if (slot.hasTeamColor() && slot.teamColor.equals(color)) {
        return slot;
      }
    }
    return ScoreboardSlot.SIDEBAR;
  }

  public static Set<String> keys() {
    final HashSet<String> keys = new HashSet<>();
    for (final ScoreboardSlot slot : values()) {
      keys.add(slot.key);
    }
    return keys;
  }

  public DisplaySlot getDisplaySlot() {
    if (isLegacy()) {
      return switch (this) {
        case BELOW_NAME -> DisplaySlot.BELOW_NAME;
        case PLAYER_LIST -> DisplaySlot.PLAYER_LIST;
        case SIDEBAR -> DisplaySlot.SIDEBAR;
        default -> DisplaySlot.SIDEBAR;
      };
    } else {
      if (isPaper()) {
        return DisplaySlot.valueOf(this.name());
      } else {
        return DisplaySlot.valueOf(this.name().replace("TEAM_", ""));
      }
    }
  }

  public ChatColor getTeamColor() {
    return this.teamColor;
  }

  public boolean hasTeamColor() {
    return teamColor != null;
  }

  @Override
  public NamespacedKey getKey() {
    return NamespacedKey.fromString(this.key);
  }

  @Override
  public String toString() {
    return this.key;
  }
}
