package com.lemonlightmc.zenith.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.additive.Reflect;

public class Plugins {

  public static final String VAULT = "Vault";
  public static final String LUCK_PERMS = "LuckPerms";
  public static final String PLACEHOLDER_API = "PlaceholderAPI";
  public static final String FLOODGATE = "floodgate";
  public static final String PROTOCOL_LIB = "ProtocolLib";
  public static final String PACKET_EVENTS = "packetevents";
  public static final String DISCORD_SRV = "DiscordSRV";
  public static final String WORLD_GUARD = "WorldGuard";
  public static final String GRIEF_PREVENTION = "GriefPrevention";

  private static boolean hasPAPI;

  public static void detectPlugins() {
    hasPAPI = Reflect.hasClass("me.clip.placeholderapi.PlaceholderAPI");
  }

  public static boolean isInstalled(String pluginName) {
    Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
    return plugin != null;
  }

  public static boolean isLoaded(String pluginName) {
    Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
    return plugin != null && plugin.isEnabled();
  }

  public static boolean hasPacketLibrary() {
    return isInstalled(PACKET_EVENTS) || isInstalled(PROTOCOL_LIB);
  }

  public static boolean hasPlaceholderAPI() {
    return hasPAPI;
  }

  public static boolean hasVault() {
    return isInstalled(VAULT);
  }

  public static boolean hasFloodgate() {
    return isInstalled(FLOODGATE);
  }

  public static boolean hasDiscordSRV() {
    return isInstalled(DISCORD_SRV);
  }

  public static boolean hasWorldGuard() {
    return isInstalled(WORLD_GUARD);
  }

  public static boolean hasGriefPrevention() {
    return isInstalled(GRIEF_PREVENTION);
  }
}
