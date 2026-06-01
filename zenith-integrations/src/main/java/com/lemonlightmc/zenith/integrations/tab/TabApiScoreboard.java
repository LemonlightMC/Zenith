package com.lemonlightmc.zenith.integrations.tab;

import java.util.UUID;

import com.lemonlightmc.zenith.integrations.TabApi;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.scoreboard.Scoreboard;

public class TabApiScoreboard {
  public static boolean hasScoreboardVisible(final TabPlayer player) {
    if (player == null) {
      return false;
    }
    return TabAPI.getInstance().getScoreboardManager().hasScoreboardVisible(player);
  }

  public static boolean hasScoreboardVisible(final UUID uuid) {
    return hasScoreboardVisible(TabApi.getPlayer(uuid));
  }

  public static void setScoreboardVisible(final TabPlayer player, final boolean visible, final boolean message) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getScoreboardManager().setScoreboardVisible(player, visible, message);
  }

  public static void setScoreboardVisible(final TabPlayer player, final boolean visible) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getScoreboardManager().setScoreboardVisible(player, visible, true);
  }

  public static void setScoreboardVisible(final UUID uuid, final boolean visible, final boolean message) {
    setScoreboardVisible(TabApi.getPlayer(uuid), visible, message);
  }

  public static void setScoreboardVisible(final UUID uuid, final boolean visible) {
    setScoreboardVisible(TabApi.getPlayer(uuid), visible, true);
  }

  public static void toggleScoreboard(final TabPlayer player, final boolean message) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getScoreboardManager().toggleScoreboard(player, message);
  }

  public static void toggleScoreboard(final UUID uuid, final boolean message) {
    toggleScoreboard(TabApi.getPlayer(uuid), message);
  }

  public static void toggleScoreboard(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getScoreboardManager().toggleScoreboard(player, true);
  }

  public static void toggleScoreboard(final UUID uuid) {
    toggleScoreboard(TabApi.getPlayer(uuid), true);
  }

  public static void resetScoreboard(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getScoreboardManager().resetScoreboard(player);
  }

  public static void resetScoreboard(final UUID uuid) {
    resetScoreboard(TabApi.getPlayer(uuid));
  }

  public static void removeScoreboard(final Scoreboard board) {
    if (board == null) {
      return;
    }
    TabAPI.getInstance().getScoreboardManager().removeScoreboard(board);
  }

  public static void showScoreboard(final TabPlayer player, final Scoreboard board) {
    if (player == null || board == null) {
      return;
    }
    TabAPI.getInstance().getScoreboardManager().showScoreboard(player, board);
  }

  public static void showScoreboard(final UUID uuid, final Scoreboard board) {
    showScoreboard(TabApi.getPlayer(uuid), board);
  }

  public static boolean hasCustomScoreboard(final TabPlayer player) {
    if (player == null) {
      return false;
    }
    return TabAPI.getInstance().getScoreboardManager().hasCustomScoreboard(player);
  }

  public static boolean hasCustomScoreboard(final UUID uuid) {
    return hasScoreboardVisible(TabApi.getPlayer(uuid));
  }

  public static Scoreboard getActiveScoreboard(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getScoreboardManager().getActiveScoreboard(player);
  }

  public static Scoreboard getActiveScoreboard(final UUID uuid) {
    return getActiveScoreboard(TabApi.getPlayer(uuid));
  }
}
