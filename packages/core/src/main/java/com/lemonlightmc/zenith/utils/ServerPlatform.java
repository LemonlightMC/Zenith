package com.lemonlightmc.zenith.utils;

import java.util.Locale;

public enum ServerPlatform {
  /**
   * Folia - Paper fork with regionized multithreading
   */
  FOLIA("folia", new String[] { "io.papermc.paper.threadedregions.scheduler.FoliaRegionScheduler",
      "io.papermc.paper.threadedregions.RegionizedServer", "ca.spottedleaf.moonrise.common.util.TickThread" }),

  /**
   * Paper - modern Performance-focused Spigot successor
   */
  PAPER("paper", new String[] { "com.destroystokyo.paper.PaperConfig", "io.papermc.paper.ServerBuildInfo" }),

  /**
   * Spigot - CraftBukkit fork with optimizations
   */
  SPIGOT("spigot", new String[] { "org.spigotmc.SpigotConfig", "org.bukkit.Server.Spigot" }),

  /**
   * Bukkit - Base server API
   */
  BUKKIT("bukkit", new String[] { "org.bukkit.Bukkit" }),

  /**
   * Purpur - Paper fork with extra features
   */
  PURPUR("purpur", "org.purpurmc.purpur.PurpurConfig"),

  /**
   * Velocity - Modern proxy server
   */
  VELOCITY("velocity", new String[] { "com.velocitypowered.api.proxy.ProxyServer" }),
  /**
   * Waterfall - BungeeCord fork by PaperMC
   */
  WATERFALL("waterfall", new String[] { "io.github.waterfallmc.waterfall.conf.WaterfallConfiguration" }),
  /**
   * BungeeCord - Proxy server
   */
  BUNGEECORD("bungeecord", new String[] { "net.md_5.bungee.api.ProxyServer" }),

  /**
   * Mock Bukkit - Mock API for Testing
   */
  MOCK_BUKKIT("mockbukkit", new String[] { "be.seeseemelk.mockbukkit.ServerMock",
      "org.mockbukkit.mockbukkit.ServerMock" }),

  /**
   * Sponge - API for Sponge servers
   */
  SPONGE("Sponge", new String[] { "org.spongepowered.api.Sponge", "org.spongepowered.api.Platform" }),
  /**
   * Nukkit - API for Nukkit servers
   */
  NUKKIT("Nukkit", new String[] { "cn.nukkit.Server", "cn.nukkit.utils.Utils" }),
  /**
   * Fabric - Lightweight modding API for Minecraft
   */
  FABRIC("Fabric", new String[] { "net.fabricmc.api.EnvType", "net.fabricmc.loader.api.FabricLoader" }),
  /**
   * NeoForge - modern Forge successor
   */
  NEOFORGE("NeoForge", new String[] { "org.neoforge.NeoForge", "org.neoforge.api.NeoForgeAPI" }),
  /**
   * Forge - Heavy Legacy modding API for Minecraft
   */
  FORGE("Forge", new String[] { "net.minecraftforge.fml.common.Mod", "net.minecraftforge.fml.common.Loader" }),
  SHREDDEDPAPER("ShreddedPaper", new String[] { "io.multipaper.shreddedpaper.threading.ShreddedPaperTickThread" }),
  QUILT("Quilt", new String[] { "org.quiltmc.loader.api.QuiltLoader" }),
  /**
   * Standalone - Running without a known server platform (e.g., as a library in
   * another application)
   */
  STANDALONE("Standalone", new String[] {}),
  /**
   * Unknown - No known platform detected
   */
  UNKNOWN("Unknown", new String[] {});

  private final String name;
  private final String[] checkClasses;

  ServerPlatform(final String name, final String checkClass) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("ServerPlatform Name cannot be null or empty");
    }
    this.name = name;
    this.checkClasses = new String[] { checkClass };
  }

  ServerPlatform(final String name, final String[] checkClasses) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("ServerPlatform Name cannot be null or empty");
    }
    this.name = name;
    this.checkClasses = checkClasses;
  }

  /**
   * @return the friendly name of the platform (e.g., "paper", "spigot")
   */
  public String friendlyName() {
    return name;
  }

  /**
   * @return the platform identifier (e.g., "paper", "spigot")
   */
  public String id() {
    return name.toLowerCase(Locale.ROOT);
  }

  /**
   * Get the Hangar platform name.
   */
  public String hangarPlatform() {
    return switch (this) {
      case FOLIA, PAPER, PURPUR -> "PAPER";
      case SPIGOT, BUKKIT, MOCK_BUKKIT, SHREDDEDPAPER -> "PAPER"; // Hangar doesn't have separate Spigot, Paper works
      case VELOCITY -> "VELOCITY";
      case BUNGEECORD, WATERFALL -> "WATERFALL";
      case SPONGE -> "SPONGE";
      case NUKKIT -> "NUKKIT";
      case FABRIC -> "FABRIC";
      case NEOFORGE, FORGE -> "FORGE";
      case QUILT -> "QUILT";
      case STANDALONE -> "STANDALONE";
      case UNKNOWN -> "STANDALONE";
    };
  }

  /**
   * Get compatible Modrinth loaders for this platform.
   */
  public String[] modrinthLoaders() {
    return switch (this) {
      case FOLIA -> new String[] { "folia", "paper", "spigot", "bukkit" };
      case PAPER, SHREDDEDPAPER -> new String[] { "paper", "spigot", "bukkit" };
      case PURPUR -> new String[] { "purpur", "paper", "spigot", "bukkit" };
      case SPIGOT, BUKKIT, MOCK_BUKKIT -> new String[] { "spigot", "bukkit" };
      case VELOCITY -> new String[] { "velocity" };
      case BUNGEECORD -> new String[] { "bungeecord" };
      case WATERFALL -> new String[] { "waterfall", "bungeecord" };
      case SPONGE -> new String[] { "sponge" };
      case NUKKIT -> new String[] { "nukkit" };
      case FABRIC -> new String[] { "fabric" };
      case NEOFORGE -> new String[] { "neoforge" };
      case FORGE -> new String[] { "forge" };
      case QUILT -> new String[] { "quilt" };
      case STANDALONE -> new String[] {};
      case UNKNOWN -> new String[] {};
    };
  }

  // Cached detected platform
  private static volatile ServerPlatform detected = null;

  /**
   * Detect the current server platform.
   * Result is cached after first detection.
   *
   * @return the detected platform, or BUKKIT if detection fails
   */
  public static ServerPlatform detect() {
    if (detected != null) {
      return detected;
    }

    synchronized (ServerPlatform.class) {
      if (detected != null) {
        return detected;
      }

      detected = doDetect();
      return detected;
    }
  }

  public static boolean isFolia() {
    return detect() == FOLIA;
  }

  public static boolean isPaper() {
    return detect() == PAPER;
  }

  public static boolean isBukkit() {
    return detect() == BUKKIT;
  }

  public static boolean isPaperFork() {
    ServerPlatform platform = detect();
    return platform == FOLIA || platform == PAPER || platform == PURPUR;
  }

  public static boolean isMocked() {
    return detect() == MOCK_BUKKIT;
  }

  private static ServerPlatform doDetect() {

    // Check for Folia first (most specific)
    if (check(FOLIA)) {
      return FOLIA;
    }

    // Check for Purpur
    if (check(PURPUR)) {
      return PURPUR;
    }

    // Check for Paper
    if (check(PAPER)) {
      return PAPER;
    }

    // Check for older Paper versions
    if (check(PAPER)) {
      return PAPER;
    }

    // Check for Spigot
    if (check(SPIGOT)) {
      return SPIGOT;
    }

    // Check for Velocity
    if (check(VELOCITY)) {
      return VELOCITY;
    }

    // Check for Waterfall
    if (check(WATERFALL)) {
      return WATERFALL;
    }

    // Check for BungeeCord
    if (check(BUNGEECORD)) {
      return BUNGEECORD;
    }

    // Check for BungeeCord
    if (check(MOCK_BUKKIT)) {
      return MOCK_BUKKIT;
    }

    // Default to Bukkit
    return BUKKIT;
  }

  private static boolean check(ServerPlatform platform) {
    for (String cls : platform.checkClasses) {
      if (classExists(cls)) {
        return true;
      }
    }
    return false;
  }

  private static boolean classExists(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}