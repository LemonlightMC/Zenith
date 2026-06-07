package com.lemonlightmc.zenith.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lemonlightmc.zenith.math.NumberConversions;

public class SemverVersion extends Version {

  private static final Pattern PATTERN = Pattern.compile("^([a-zA-Z]*[-]?)?((?:[0-9]|[._])+)(([-.]+)([a-zA-Z0-9]+))?");
  private final int major;
  private final int minor;
  private final int patch;
  private final int build;
  private final String str;

  private final String qualifier;
  private final String prefix;

  public static final SemverVersion FIRST_VERSION = new SemverVersion(1, 0, 0, 0, null, null);

  public SemverVersion(
      final int major,
      final int minor,
      final int patch,
      final int build,
      final String prefix,
      final String qualifier) {
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
    this.prefix = prefix;
    this.qualifier = qualifier != null && qualifier.isEmpty() ? null : qualifier;
    this.str = toString();
  }

  public SemverVersion(
      final int major,
      final int minor,
      final int patch,
      final int build) {
    this(major, minor, patch, build, null, null);
  }

  public SemverVersion(final int major, final int minor, final int patch) {
    this(major, minor, patch, 0, null, null);
  }

  public SemverVersion(final int major, final int minor) {
    this(major, minor, 0, 0, null, null);
  }

  public SemverVersion(final int major) {
    this(major, 0, 0, 0, null, null);
  }

  public SemverVersion(final SemverVersion v) {
    this(v.major, v.minor, v.patch, v.build, v.prefix, v.qualifier);
  }

  public SemverVersion(final String raw) {
    if (raw == null) {
      throw new IllegalArgumentException("Version string cannot be empty");
    }
    this.str = raw.trim();
    if (str.isEmpty()) {
      throw new IllegalArgumentException("Version string cannot be empty");
    }

    final Matcher matcher = PATTERN.matcher(str);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Cannot parse version: " + str);
    }

    this.prefix = matcher.group(1);
    final String tempQualifier = matcher.group(3);
    this.qualifier = tempQualifier != null && tempQualifier.isEmpty() ? null : tempQualifier;

    final String numericPart = matcher.group(2);
    if (numericPart == null || numericPart.isEmpty()) {
      throw new IllegalArgumentException("Version string must contain numeric parts: " + str);
    }
    final String[] parts = numericPart.split("[._]");

    try {
      major = (parts.length > 0) ? parseIntSafe(parts[0]) : 0;
      minor = (parts.length > 1) ? parseIntSafe(parts[1]) : 0;
      patch = (parts.length > 2) ? parseIntSafe(parts[2]) : 0;
      build = (parts.length > 3) ? parseIntSafe(parts[3]) : 0;
    } catch (final NumberFormatException e) {
      throw new IllegalArgumentException("Invalid SemverVersion string: " + str, e);
    }
  }

  private static int parseIntSafe(final String str) {
    if (str == null || str.isEmpty()) {
      return 0;
    }
    try {
      return NumberConversions.parseInt(str, 10);
    } catch (final Exception ignored) {
      return 0;
    }
  }

  @Override
  public int major() {
    return major;
  }

  @Override
  public int minor() {
    return minor;
  }

  @Override
  public int patch() {
    return patch;
  }

  @Override
  public int build() {
    return build;
  }

  public String qualifier() {
    return qualifier;
  }

  public String prefix() {
    return prefix;
  }

  public int[] components() {
    return new int[] { major, minor, patch, build };
  }

  @Override
  public int compareTo(final Version v) {
    if (v == null) {
      return 1;
    }
    if (major > v.major())
      return 1;
    if (major < v.major())
      return -1;
    if (minor > v.minor())
      return 1;
    if (minor < v.minor())
      return -1;
    if (patch > v.patch())
      return 1;
    if (patch < v.patch())
      return -1;
    if (build > v.build())
      return 1;
    if (build < v.build())
      return -1;
    return 0;
  }

  @Override
  public String toString() {
    return str;
  }

  @Override
  public SemverVersion clone() {
    return new SemverVersion(major, minor, patch, build, prefix, qualifier);
  }

  @Override
  public int hashCode() {
    int result = 31 + major;
    result = 31 * result + minor;
    result = 31 * result + patch;
    result = 31 * result + build;
    result = 31 * result + ((prefix == null) ? 0 : prefix.hashCode());
    return 31 * result + qualifier.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SemverVersion other = (SemverVersion) obj;
    if (prefix == null) {
      if (other.prefix != null) {
        return false;
      }
    } else if (!prefix.equals(other.prefix)) {
      return false;
    }
    return major == other.major && minor == other.minor && patch == other.patch && build == other.build
        && qualifier == other.qualifier;
  }

}
