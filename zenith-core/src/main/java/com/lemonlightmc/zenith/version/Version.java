package com.lemonlightmc.zenith.version;

import java.util.ArrayList;
import java.util.List;

public class Version implements IVersion {

  public static final Version FIRST_VERSION = new Version(1, 0, 0, 0, null);
  private final int major;
  private final int minor;
  private final int patch;
  private final int build;

  private VersionModifier modifier;

  public static enum VersionModifier {
    NON(""),
    RELEASE("release"),
    ALPHA("alpha"),
    BETA("beta"),
    PATCH("patch"),
    BUILD("build"),
    RC("rc");

    private final String name;

    private VersionModifier(final String name) {
      this.name = name;
    }

    public String toString() {
      return name;
    }

    public static VersionModifier from(final String str) {
      if (str == null || str.length() == 0) {
        return null;
      }
      return valueOf(str.toUpperCase(java.util.Locale.ENGLISH));
    }
  }

  public Version(
      final int major,
      final int minor,
      final int patch,
      final int build,
      final VersionModifier modifier) {
    if (major < 0 || minor < 0 || patch < 0 || build < 0) {
      throw new IllegalArgumentException("Version numbers cannot be negative");
    }
    if (major > Integer.MAX_VALUE ||
        minor > Integer.MAX_VALUE ||
        patch > Integer.MAX_VALUE ||
        build > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Version numbers cannot be greater than Integer.MAX_VALUE");
    }
    if (major < Integer.MIN_VALUE ||
        minor < Integer.MIN_VALUE ||
        patch < Integer.MIN_VALUE ||
        build < Integer.MIN_VALUE) {
      throw new IllegalArgumentException("Version numbers cannot be less than Integer.MIN_VALUE");
    }
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.build = build;
    this.modifier = modifier;
  }

  public Version(
      final int major,
      final int minor,
      final int patch,
      final int build) {
    this(major, minor, patch, build, null);
  }

  public Version(final int major, final int minor, final int patch) {
    this(major, minor, patch, 0, null);
  }

  public Version(final int major, final int minor) {
    this(major, minor, 0, 0, null);
  }

  public Version(final int major) {
    this(major, 0, 0, 0, null);
  }

  public Version(final Version v) {
    this(v.major, v.minor, v.patch, v.build, v.modifier);
  }

  public Version(final String str) {
    String s = str.trim();
    if (s.startsWith("v") || s.startsWith("V")) {
      s = s.substring(1);
    }
    final String[] parts = s.split("[\\\\.|_|-]");
    if (parts.length < 1) {
      throw new IllegalArgumentException("Invalid version string: " + str);
    }
    try {
      // parses major, minor, patch, build and modifier
      final List<Integer> parsedParts = new ArrayList<>(4);
      for (final String part : parts) {
        if (VersionModifier.from(parts[0]) != null) {
          modifier = VersionModifier.from(part);
        } else {
          parsedParts.add(parseIntSafe(part));
        }
      }
      major = (parsedParts.size() > 0) ? parsedParts.get(0) : 0;
      minor = (parsedParts.size() > 1) ? parsedParts.get(1) : 0;
      patch = (parsedParts.size() > 2) ? parsedParts.get(2) : 0;
      build = (parsedParts.size() > 3) ? parsedParts.get(3) : 0;
    } catch (final NumberFormatException e) {
      throw new IllegalArgumentException("Invalid version string: " + str, e);
    }
  }

  private static int parseIntSafe(final String str) {
    try {
      return Integer.parseInt(str, 10);
    } catch (final Exception ignored) {
      return 0;
    }
  }

  @Override
  public int getMajor() {
    return major;
  }

  @Override
  public int getMinor() {
    return minor;
  }

  @Override
  public int getPatch() {
    return patch;
  }

  @Override
  public int getBuild() {
    return build;
  }

  public VersionModifier getModifier() {
    return modifier;
  }

  @Override
  public boolean isMajor(final IVersion version) {
    checkNotNull(version);
    return major == version.getMajor();
  }

  @Override
  public boolean isMinor(final IVersion version) {
    checkNotNull(version);
    return minor == version.getMinor();
  }

  @Override
  public boolean isPatch(final IVersion version) {
    checkNotNull(version);
    return patch == version.getPatch();
  }

  @Override
  public boolean isBuild(final IVersion version) {
    checkNotNull(version);
    return build == version.getBuild();
  }

  public boolean isModifier(final Version version) {
    checkNotNull(version);
    return modifier == version.getModifier();
  }

  @Override
  public boolean isSame(final IVersion version) {
    checkNotNull(version);
    return (major == version.getMajor() &&
        minor == version.getMajor() &&
        patch == version.getPatch() &&
        build == version.getBuild());
  }

  @Override
  public boolean isNewerThan(final IVersion version) {
    checkNotNull(version);
    return ((major > version.getMajor()) ||
        (major == version.getMajor() && minor > version.getMinor()) ||
        (major == version.getMajor() &&
            minor == version.getMinor() &&
            patch > version.getPatch())
        ||
        (major == version.getMajor() &&
            minor == version.getMinor() &&
            patch == version.getPatch() &&
            build > version.getBuild()));
  }

  @Override
  public boolean isOlderThan(final IVersion version) {
    checkNotNull(version);
    return ((major < version.getMajor()) ||
        (major == version.getMajor() && minor < version.getMinor()) ||
        (major == version.getMajor() &&
            minor == version.getMinor() &&
            patch < version.getPatch())
        ||
        (major == version.getMajor() &&
            minor == version.getMinor() &&
            patch == version.getPatch() &&
            build < version.getBuild()));
  }

  @Override
  public boolean isAtLeast(final IVersion version) {
    checkNotNull(version);
    return ((major >= version.getMajor()) ||
        (major == version.getMajor() && minor >= version.getMinor()) ||
        (major == version.getMajor() &&
            minor == version.getMinor() &&
            patch >= version.getPatch())
        ||
        (major == version.getMajor() &&
            minor == version.getMinor() &&
            patch == version.getPatch() &&
            build >= version.getBuild()));
  }

  @Override
  public boolean isDifferent(IVersion version) {
    checkNotNull(version);
    return !isSame(version);
  }

  @Override
  public boolean isBetween(IVersion minVersion, IVersion maxVersion) {
    checkNotNull(minVersion);
    checkNotNull(maxVersion);
    return isNewerThan(minVersion) && isOlderThan(maxVersion);
  }

  @Override
  public boolean isOutside(IVersion minVersion, IVersion maxVersion) {
    checkNotNull(minVersion);
    checkNotNull(maxVersion);
    return isOlderThan(minVersion) || isNewerThan(maxVersion);
  }

  @Override
  public int compareTo(final IVersion v) {
    checkNotNull(v);
    if (major > v.getMajor())
      return 1;
    if (major < v.getMajor())
      return -1;
    if (minor > v.getMinor())
      return 1;
    if (minor < v.getMinor())
      return -1;
    if (patch > v.getPatch())
      return 1;
    if (patch < v.getPatch())
      return -1;
    if (build > v.getBuild())
      return 1;
    if (build < v.getBuild())
      return -1;
    return 0;
  }

  private void checkNotNull(final IVersion version) {
    if (version == null) {
      throw new IllegalArgumentException("Version cannot be null");
    }
  }

  @Override
  public String formatted() {
    return formatted(false);
  }

  @Override
  public String formatted(final boolean includeEmpty) {
    return (major +
        ((minor == 0 && !includeEmpty) ? "" : "." + minor) +
        ((patch == 0 && !includeEmpty) ? "" : "." + patch) +
        ((build == 0 && !includeEmpty) ? "" : "." + build) +
        ((modifier == null && !includeEmpty) ? "" : "-" + modifier.toString()));
  }

  @Override
  public String toString() {
    return "Version [major=" + major + ", minor=" + minor + ", patch=" + patch + ", build=" + build + ", modifier="
        + modifier + "]";
  }

  @Override
  public Version clone() {
    return new Version(major, minor, patch, build, modifier);
  }

  @Override
  public int hashCode() {
    int result = 31 + major;
    result = 31 * result + minor;
    result = 31 * result + patch;
    result = 31 * result + build;
    return 31 * result + ((modifier == null) ? 0 : modifier.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Version other = (Version) obj;
    return major == other.major && minor == other.minor && patch == other.patch && build == other.build
        && modifier == other.modifier;
  }

}
