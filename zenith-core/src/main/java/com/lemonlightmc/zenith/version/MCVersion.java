package com.lemonlightmc.zenith.version;

import org.bukkit.Bukkit;

import com.lemonlightmc.zenith.exceptions.PlatformException;

public class MCVersion extends SemverVersion {

  public static final MCVersion v1_8_0 = new MCVersion(8, 0);
  public static final MCVersion v1_8_1 = new MCVersion(8, 1);
  public static final MCVersion v1_8_2 = new MCVersion(8, 2);
  public static final MCVersion v1_8_3 = new MCVersion(8, 3);
  public static final MCVersion v1_8_4 = new MCVersion(8, 4);
  public static final MCVersion v1_8_5 = new MCVersion(8, 5);
  public static final MCVersion v1_8_6 = new MCVersion(8, 6);
  public static final MCVersion v1_8_7 = new MCVersion(8, 7);
  public static final MCVersion v1_8_8 = new MCVersion(8, 8);
  public static final MCVersion v1_9_0 = new MCVersion(9, 0);
  public static final MCVersion v1_9_1 = new MCVersion(9, 1);
  public static final MCVersion v1_9_2 = new MCVersion(9, 2);
  public static final MCVersion v1_9_3 = new MCVersion(9, 3);
  public static final MCVersion v1_9_4 = new MCVersion(9, 4);
  public static final MCVersion v1_10_0 = new MCVersion(10, 0);
  public static final MCVersion v1_10_1 = new MCVersion(10, 1);
  public static final MCVersion v1_10_2 = new MCVersion(10, 2);
  public static final MCVersion v1_11_0 = new MCVersion(11, 0);
  public static final MCVersion v1_11_1 = new MCVersion(11, 1);
  public static final MCVersion v1_11_2 = new MCVersion(11, 2);
  public static final MCVersion v1_12_0 = new MCVersion(12, 0);
  public static final MCVersion v1_12_1 = new MCVersion(12, 1);
  public static final MCVersion v1_12_2 = new MCVersion(12, 2);
  public static final MCVersion v1_13_0 = new MCVersion(13, 0);
  public static final MCVersion v1_13_1 = new MCVersion(13, 1);
  public static final MCVersion v1_13_2 = new MCVersion(13, 2);
  public static final MCVersion v1_14_0 = new MCVersion(14, 0);
  public static final MCVersion v1_14_1 = new MCVersion(14, 1);
  public static final MCVersion v1_14_2 = new MCVersion(14, 2);
  public static final MCVersion v1_14_3 = new MCVersion(14, 3);
  public static final MCVersion v1_14_4 = new MCVersion(14, 4);
  public static final MCVersion v1_15_0 = new MCVersion(15, 0);
  public static final MCVersion v1_15_1 = new MCVersion(15, 1);
  public static final MCVersion v1_15_2 = new MCVersion(15, 2);
  public static final MCVersion v1_16_0 = new MCVersion(16, 0);
  public static final MCVersion v1_16_1 = new MCVersion(16, 1);
  public static final MCVersion v1_16_2 = new MCVersion(16, 2);
  public static final MCVersion v1_16_3 = new MCVersion(16, 3);
  public static final MCVersion v1_16_4 = new MCVersion(16, 4);
  public static final MCVersion v1_16_5 = new MCVersion(16, 5);
  public static final MCVersion v1_17_0 = new MCVersion(17, 0);
  public static final MCVersion v1_17_1 = new MCVersion(17, 1);
  public static final MCVersion v1_18_0 = new MCVersion(18, 0);
  public static final MCVersion v1_18_1 = new MCVersion(18, 1);
  public static final MCVersion v1_18_2 = new MCVersion(18, 2);
  public static final MCVersion v1_19_0 = new MCVersion(19, 0);
  public static final MCVersion v1_19_1 = new MCVersion(19, 1);
  public static final MCVersion v1_19_2 = new MCVersion(19, 2);
  public static final MCVersion v1_19_3 = new MCVersion(19, 3);
  public static final MCVersion v1_19_4 = new MCVersion(19, 4);
  public static final MCVersion v1_20_0 = new MCVersion(20, 0);
  public static final MCVersion v1_20_1 = new MCVersion(20, 1);
  public static final MCVersion v1_20_2 = new MCVersion(20, 2);
  public static final MCVersion v1_20_3 = new MCVersion(20, 3);
  public static final MCVersion v1_20_4 = new MCVersion(20, 4);
  public static final MCVersion v1_20_5 = new MCVersion(20, 5);
  public static final MCVersion v1_20_6 = new MCVersion(20, 6);
  public static final MCVersion v1_21_0 = new MCVersion(21, 0);
  public static final MCVersion v1_21_1 = new MCVersion(21, 1);
  public static final MCVersion v1_21_2 = new MCVersion(21, 2);
  public static final MCVersion v1_21_3 = new MCVersion(21, 3);
  public static final MCVersion v1_21_4 = new MCVersion(21, 4);
  public static final MCVersion v1_21_5 = new MCVersion(21, 5);
  public static final MCVersion v1_21_6 = new MCVersion(21, 6);
  public static final MCVersion v1_21_7 = new MCVersion(21, 7);
  public static final MCVersion v1_21_8 = new MCVersion(21, 8);
  public static final MCVersion v1_21_9 = new MCVersion(21, 9);
  public static final MCVersion v1_21_10 = new MCVersion(21, 10);
  public static final MCVersion v1_21_11 = new MCVersion(21, 11);

  private static final MCVersion currentVersion;

  static {
    String versionStr = null;
    if (ServerPlatform.isPaper()) {
      try {
        versionStr = (String) Bukkit
            .getServer()
            .getClass()
            .getMethod("getMinecraftVersion")
            .invoke(Bukkit.getServer());
      } catch (final Exception ignored) {
      }
    } else {
      versionStr = Bukkit.getVersion();
    }
    if (versionStr == null || versionStr.isEmpty()) {
      throw new PlatformException("Failed to detect Minecraft Version!");
    }

    versionStr = versionStr.substring(0, versionStr.indexOf('-')); // Legacy-wise this is enough
    if (versionStr.contains("build")) {
      // Paper new 26.1+ versioning system; Ex. 26.1.2.build.51-beta
      versionStr = versionStr.substring(0, versionStr.indexOf(".build"));
    }
    currentVersion = new MCVersion(versionStr);
  }

  public MCVersion(final int minor, final int patch) {
    super(1, minor, patch);
  }

  public MCVersion(final String str) {
    super(str);
  }

  public static MCVersion getCurrent() {
    return currentVersion;
  }

  public static boolean isNew() {
    return currentVersion.major() == 26;
  }

  public static boolean isOld() {
    return currentVersion.major() == 1;
  }

  public static boolean isLegacy() {
    return currentVersion.major() == 1 && currentVersion.minor() < 13;
  }

  public static boolean isNewerThan(final MCVersion version) {
    return currentVersion.isNewerThan((Version) version);
  }

  public static boolean isOlderThan(final MCVersion version) {
    return currentVersion.isOlderThan((Version) version);
  }

  public static boolean isAtLeast(final MCVersion version) {
    return currentVersion.isAtLeast((Version) version);
  }

  public static boolean isBetween(final MCVersion version1, final MCVersion version2) {
    return currentVersion.isNewerThan((Version) version1) && currentVersion.isOlderThan((Version) version2);
  }

  @Deprecated
  public static String getCraftBukkitVersion() {
    final String[] pckg = Bukkit
        .getServer()
        .getClass()
        .getPackage()
        .getName()
        .split("\\.");
    return (pckg.length >= 4) ? pckg[3] : null;
  }
}
