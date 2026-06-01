package com.lemonlightmc.zenith.integrations.tab;

import java.util.UUID;

import com.lemonlightmc.zenith.integrations.TabApi;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.layout.Layout;

public class TabApiTabList {

  public static String getTablistCustomName(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getCustomName(player);
  }

  public static String getTablistCustomName(final UUID uuid) {
    return getTablistCustomName(TabApi.getPlayer(uuid));
  }

  public static void setTablistCustomName(final TabPlayer player, final String name) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getTabListFormatManager().setName(player, name);
  }

  public static String getTablistCustomPrefix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getCustomPrefix(player);
  }

  public static String getTablistCustomPrefix(final UUID uuid) {
    return getTablistCustomPrefix(TabApi.getPlayer(uuid));
  }

  public static void setTablistCustomPrefix(final TabPlayer player, final String prefix) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getTabListFormatManager().setPrefix(player, prefix);
  }

  public static String getTablistCustomSuffix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getCustomSuffix(player);
  }

  public static String getTablistCustomSuffix(final UUID uuid) {
    return getTablistCustomSuffix(TabApi.getPlayer(uuid));
  }

  public static void setTablistCustomSuffix(final TabPlayer player, final String suffix) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getTabListFormatManager().setSuffix(player, suffix);
  }

  public static String getTablistRawName(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getOriginalRawName(player);
  }

  public static String getTablistRawName(final UUID uuid) {
    return getTablistRawName(TabApi.getPlayer(uuid));
  }

  public static String getTablistRawPrefix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getOriginalRawPrefix(player);
  }

  public static String getTablistRawPrefix(final UUID uuid) {
    return getTablistRawPrefix(TabApi.getPlayer(uuid));
  }

  public static String getTablistRawSuffix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getOriginalRawSuffix(player);
  }

  public static String getTablistRawSuffix(final UUID uuid) {
    return getTablistRawSuffix(TabApi.getPlayer(uuid));
  }

  public static String getTablistReplacedName(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getOriginalReplacedName(player);
  }

  public static String getTablistReplacedName(final UUID uuid) {
    return getTablistReplacedName(TabApi.getPlayer(uuid));
  }

  public static String getTablistReplacedPrefix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getOriginalReplacedPrefix(player);
  }

  public static String getTablistReplacedPrefix(final UUID uuid) {
    return getTablistReplacedPrefix(TabApi.getPlayer(uuid));
  }

  public static String getTablistReplacedSuffix(final TabPlayer player) {
    if (player == null) {
      return null;
    }
    return TabAPI.getInstance().getTabListFormatManager().getOriginalReplacedSuffix(player);
  }

  public static String getTablistReplacedSuffix(final UUID uuid) {
    return getTablistReplacedSuffix(TabApi.getPlayer(uuid));
  }

  public static void setFooter(final TabPlayer player, final String footer) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getHeaderFooterManager().setFooter(player, footer);
  }

  public static void setFooter(final UUID uuid, final String footer) {
    setFooter(TabApi.getPlayer(uuid), footer);
  }

  public static void setHeader(final TabPlayer player, final String header) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getHeaderFooterManager().setHeader(player, header);
  }

  public static void setHeader(final UUID uuid, final String header) {
    setHeader(TabApi.getPlayer(uuid), header);
  }

  public static void setHeaderAndFooter(final TabPlayer player, final String footer, final String header) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getHeaderFooterManager().setHeaderAndFooter(player, footer, header);
  }

  public static void setHeaderAndFooter(final UUID uuid, final String footer, final String header) {
    setHeaderAndFooter(TabApi.getPlayer(uuid), footer, header);
  }

  public static Layout getLayout(final String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    return TabAPI.getInstance().getLayoutManager().getLayout(name);
  }

  public static Layout createLayout(final String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    return TabAPI.getInstance().getLayoutManager().createNewLayout(name);
  }

  public static void sendLayout(final TabPlayer player, final Layout layout) {
    if (player == null || layout == null) {
      return;
    }
    TabAPI.getInstance().getLayoutManager().sendLayout(player, layout);
  }

  public static void sendLayout(final UUID uuid, final Layout layout) {
    sendLayout(TabApi.getPlayer(uuid), layout);
  }

  public static void sendLayout(final TabPlayer player, final String layout) {
    sendLayout(player, getLayout(layout));
  }

  public static void sendLayout(final UUID uuid, final String layout) {
    sendLayout(TabApi.getPlayer(uuid), getLayout(layout));
  }

  public static void resetLayout(final TabPlayer player) {
    if (player == null) {
      return;
    }
    TabAPI.getInstance().getLayoutManager().resetLayout(player);
  }

  public static void resetLayout(final UUID uuid) {
    resetLayout(TabApi.getPlayer(uuid));
  }
}
