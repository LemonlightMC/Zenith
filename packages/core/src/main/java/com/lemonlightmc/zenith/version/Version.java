package com.lemonlightmc.zenith.version;

import com.lemonlightmc.zenith.additive.Cloneable;

public abstract class Version implements Cloneable<Version>, Comparable<Version> {

  public static final SemverVersion FIRST_VERSION = new SemverVersion(1, 0, 0, 0, null, null);
  public static final SemverVersion MIN_VALUE = new SemverVersion(0, 0, 0, 0, null, null);
  public static final SemverVersion MAX_VALUE = new SemverVersion(Integer.MAX_VALUE, Integer.MAX_VALUE,
      Integer.MAX_VALUE, Integer.MAX_VALUE, null, null);

  public static SemverVersion trySemver(final String raw) {
    try {
      return new SemverVersion(raw);
    } catch (Exception e) {
      return null;
    }
  }

  public static SemverVersion semver(final String raw) {
    return new SemverVersion(raw);
  }

  public static SemverVersion semver(final SemverVersion version) {
    return new SemverVersion(version);
  }

  public static SemverVersion semver(final int major,
      final int minor,
      final int patch,
      final int build,
      final String prefix,
      final String qualifier) {
    return new SemverVersion(major, minor, patch, build, prefix, qualifier);
  }

  public static SemverVersion semver(final int major,
      final int minor,
      final int patch,
      final int build) {
    return new SemverVersion(major, minor, patch, build, null, null);
  }

  public static SemverVersion semver(final int major,
      final int minor,
      final int patch) {
    return new SemverVersion(major, minor, patch, 0, null, null);
  }

  public static SemverVersion semver(final int major,
      final int minor) {
    return new SemverVersion(major, minor, 0, 0, null, null);
  }

  public static ComplexVersion complex(final String raw) {
    return new ComplexVersion(raw);
  }

  public static ComplexVersion complex(final ComplexVersion version) {
    return new ComplexVersion(version);
  }

  public static ComplexVersion complex(final String raw, final int[] components) {
    return new ComplexVersion(raw, components);
  }

  public static ComplexVersion complex(final int major,
      final int minor,
      final int patch) {
    return new ComplexVersion(major, minor, patch);
  }

  public static ComplexVersion complex(final int major, final int minor) {
    return new ComplexVersion(major, minor, 0);
  }

  public static int compare(final Version v1, final Version v2) {
    if (v1 == null && v2 == null) {
      return 0;
    } else if (v1 == null) {
      return -1;
    } else if (v2 == null) {
      return 1;
    }
    return v1.compareTo(v2);
  }

  private String str;

  public abstract int major();

  public abstract int minor();

  public abstract int patch();

  public abstract int build();

  public abstract int[] components();

  boolean isSame(final Version version) {
    return compareTo(version) == 0;
  }

  public boolean isNewerThan(final Version other) {
    return compareTo(other) == 1;
  }

  public boolean isOlderThan(final Version other) {
    return compareTo(other) == -1;
  }

  public boolean isAtLeast(final Version other) {
    return compareTo(other) == 1 || compareTo(other) == 0;
  }

  public boolean isBetween(final Version minVersion, final Version maxVersion) {
    return (minVersion == null || isNewerThan(minVersion)) && (maxVersion == null || isOlderThan(maxVersion));
  }

  public boolean isOutside(final Version minVersion, final Version maxVersion) {
    return minVersion == null || isOlderThan(minVersion) || maxVersion == null || isNewerThan(maxVersion);
  }

  public boolean isSameMajor(final Version version) {
    return major() == version.major();
  }

  public boolean isSameMinor(final Version version) {
    return minor() == version.minor();

  }

  public boolean isSamePatch(final Version version) {
    return patch() == version.patch();
  }

  public boolean isSameBuild(final Version version) {
    return build() == version.build();

  }

  public boolean isDifferent(final Version version) {
    return compareTo(version) != 0;
  }

  public abstract int compareTo(Version v);

  public abstract Version clone();

  public abstract int hashCode();

  public abstract boolean equals(Object obj);

  public String formatted() {
    return formatted(null, true);
  }

  public String formatted(final String prefix) {
    return formatted(prefix, true);
  }

  public String formatted(final boolean includeEmpty) {
    return formatted(null, includeEmpty);
  }

  public String formatted(final String prefix, final boolean includeEmpty) {
    final StringBuilder builder = new StringBuilder(prefix == null ? "" : prefix);
    builder.append(major());
    final int[] components = components();
    for (int i = 1; i < components.length; i++) {
      if (components[i] == 0 && !includeEmpty) {
        break;
      }
      builder.append(".");
      builder.append(components[i]);
    }
    return builder.toString();
  }

  public String toString() {
    if (str == null) {
      final StringBuilder builder = new StringBuilder("v");
      builder.append(major());
      final int[] components = components();
      for (int i = 1; i < components.length; i++) {
        builder.append(".");
        builder.append(components[i]);
      }
      str = builder.toString();
    }
    return str;
  }

}
