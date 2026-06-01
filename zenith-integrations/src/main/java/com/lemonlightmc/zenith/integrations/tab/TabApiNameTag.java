package com.lemonlightmc.zenith.integrations.tab;

import java.util.UUID;

import com.lemonlightmc.zenith.integrations.TabApi;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;

public class TabApiNameTag {

  public static void setPrefix(final TabPlayer player, final String prefix) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().setPrefix(player, prefix);
  }

  public static void setPrefix(final UUID uuid, final String prefix) {
    setPrefix(TabApi.getPlayer(uuid), prefix);
  }

  public static void setSuffix(final TabPlayer player, final String suffix) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().setSuffix(player, suffix);
  }

  public static void setSuffix(final UUID uuid, final String suffix) {
    setSuffix(TabApi.getPlayer(uuid), suffix);
  }

  public static String getCustomPrefix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getNameTagManager().getCustomPrefix(player);
  }

  public static String getCustomPrefix(final UUID uuid) {
    return getCustomPrefix(TabApi.getPlayer(uuid));
  }

  public static String getCustomSuffix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getNameTagManager().getCustomSuffix(player);
  }

  public static String getCustomSuffix(final UUID uuid) {
    return getCustomSuffix(TabApi.getPlayer(uuid));
  }

  public static String getRawPrefix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getNameTagManager().getOriginalRawPrefix(player);
  }

  public static String getRawPrefix(final UUID uuid) {
    return getRawPrefix(TabApi.getPlayer(uuid));
  }

  public static String getRawSuffix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getNameTagManager().getOriginalRawSuffix(player);
  }

  public static String getRawSuffix(final UUID uuid) {
    return getRawSuffix(TabApi.getPlayer(uuid));
  }

  public static String getReplacedPrefix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getNameTagManager().getOriginalReplacedPrefix(player);
  }

  public static String getReplacedPrefix(final UUID uuid) {
    return getReplacedPrefix(TabApi.getPlayer(uuid));
  }

  public static String getReplacedSuffix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getNameTagManager().getOriginalReplacedSuffix(player);
  }

  public static String getReplacedSuffix(final UUID uuid) {
    return getReplacedSuffix(TabApi.getPlayer(uuid));
  }

  public static boolean hasHiddenNameTag(final TabPlayer player) {
    if (player == null) {
      return false;
    }
    return TabAPI.getInstance().getNameTagManager().hasHiddenNameTag(player);
  }

  public static boolean hasHiddenNameTag(final UUID uuid) {
    return hasHiddenNameTag(TabApi.getPlayer(uuid));
  }

  public static boolean hasHiddenNameTag(final TabPlayer player, final TabPlayer viewer) {
    if (player == null || viewer == null) {
      return false;
    }
    return TabAPI.getInstance().getNameTagManager().hasHiddenNameTag(player, viewer);
  }

  public static void showNameTag(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().showNameTag(player);
  }

  public static void showNameTag(final UUID uuid) {
    showNameTag(TabApi.getPlayer(uuid));
  }

  public static void hideNameTag(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().hideNameTag(player);
  }

  public static void hideNameTag(final UUID uuid) {
    hideNameTag(TabApi.getPlayer(uuid));
  }

  public static void toggleNameTagVisibility(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().toggleNameTagVisibilityView(player, true);
  }

  public static void toggleNameTagVisibility(final TabPlayer player, final boolean message) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().toggleNameTagVisibilityView(player, message);
  }

  public static void setCollisionRule(final TabPlayer player, final boolean collision) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().setCollisionRule(player, collision);
  }

  public static void setCollisionRule(final UUID uuid, final boolean collision) {
    TabAPI.getInstance().getNameTagManager().setCollisionRule(TabApi.getPlayer(uuid), collision);
  }

  public static boolean hasCollisionRule(final TabPlayer player) {
    return player == null ? false : TabAPI.getInstance().getNameTagManager().getCollisionRule(player);
  }

  public static boolean hasCollisionRule(final UUID uuid) {
    return hasCollisionRule(TabApi.getPlayer(uuid));
  }

  public static void pauseTeamHandling(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().pauseTeamHandling(player);
  }

  public static void pauseTeamHandling(final UUID uuid) {
    pauseTeamHandling(TabApi.getPlayer(uuid));
  }

  public static void resumeTeamHandling(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getNameTagManager().resumeTeamHandling(player);
  }

  public static void resumeTeamHandling(final UUID uuid) {
    resumeTeamHandling(TabApi.getPlayer(uuid));
  }

  public static boolean hasTeamHandling(final TabPlayer player) {
    if (player == null) {
      return false;
    }
    return !TabAPI.getInstance().getNameTagManager().hasTeamHandlingPaused(player);
  }

  public static boolean hasTeamHandling(final UUID uuid) {
    return hasTeamHandling(TabApi.getPlayer(uuid));
  }
}
