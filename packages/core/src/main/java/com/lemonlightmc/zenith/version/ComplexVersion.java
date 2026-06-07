package com.lemonlightmc.zenith.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lemonlightmc.zenith.math.NumberConversions;

/**
 * Flexible version parser that handles non-standard Minecraft plugin versions.
 * <p>
 * Supports formats like:
 * <ul>
 * <li>{@code 5.4.0} - Standard semver</li>
 * <li>{@code R4.0.9} - Prefixed (ModelEngine style)</li>
 * <li>{@code 5.4.0-SNAPSHOT} - With qualifier</li>
 * <li>{@code v2.11} - Simple prefix</li>
 * <li>{@code build-123} - Build number only</li>
 * <li>{@code 2024.12.20} - Calendar versioning</li>
 * </ul>
 */
public class ComplexVersion extends Version {

  private static final Pattern VERSION_PATTERN = Pattern.compile(
      "^([a-zA-Z]*[-]?)?" + // Optional prefix (R, v, build-, etc.)
          "([0-9]+(?:[._][0-9]+)*)" + // Numeric components (1.2.3 or 1_2_3 or 1.2_3)
          "(?:[-.]?([a-zA-Z][a-zA-Z0-9]*(?:[-._][a-zA-Z0-9]+)*))?" + // Optional qualifier
          "(?:[+](.+))?" + // Optional build metadata after +
          "$");

  private static final Pattern BUILD_NUMBER_PATTERN = Pattern.compile(
      "^(?:build[-.]?)?#?(\\d+)$",
      Pattern.CASE_INSENSITIVE);

  // Qualifier ordering: release (no qualifier) > RC > beta > alpha > SNAPSHOT
  private static final List<String> QUALIFIER_ORDER = List.of(
      "build", "snapshot", "dev", "patch", "alpha", "beta", "rc", "cr", "final", "prerelease", "ga", "release", "");

  private final String raw;
  private final String prefix;
  private final int[] components;
  private final String qualifier;
  private final Integer buildNumber;
  private final String buildMetadata;

  private ComplexVersion(final String raw, final String prefix, final int[] components,
      final String qualifier, final Integer buildNumber,
      final String buildMetadata) {
    if (raw == null || raw.isEmpty()) {
      throw new IllegalArgumentException("Version string cannot be empty");
    }
    if (components == null) {
      throw new IllegalArgumentException("Version components cannot be null");
    }
    for (final int i : components) {
      if (i < 0) {
        throw new IllegalArgumentException("Version numbers cannot be negative");
      }
    }
    this.raw = raw;
    this.prefix = prefix;
    this.components = components;
    this.qualifier = qualifier;
    this.buildNumber = buildNumber;
    this.buildMetadata = buildMetadata;
  }

  public ComplexVersion(final int major, final int minor, final int patch) {
    this(major + "." + minor + "." + patch, null, new int[] {
        major, minor, patch
    }, null, null, null);
  }

  public ComplexVersion(final String raw, final int[] components) {
    this(raw, null, components, null, null, null);
  }

  public ComplexVersion(final String raw) {
    this(parse(raw));
  }

  public ComplexVersion(final ComplexVersion version) {
    if (version == null) {
      throw new IllegalArgumentException("Version cannot be null");
    }
    this.raw = version.raw;
    this.prefix = version.prefix;
    this.components = version.components.clone();
    this.qualifier = version.qualifier;
    this.buildNumber = version.buildNumber;
    this.buildMetadata = version.buildMetadata;
  }

  public static ComplexVersion parse(String raw) {
    if (raw == null) {
      throw new IllegalArgumentException("Version string cannot be empty");
    }
    raw = raw.trim();
    if (raw.isEmpty()) {
      throw new IllegalArgumentException("Version string cannot be empty");
    }

    // Check for build-number-only format
    final Matcher buildMatcher = BUILD_NUMBER_PATTERN.matcher(raw);
    if (buildMatcher.matches()) {
      final int buildNum = Integer.parseInt(buildMatcher.group(1));
      return new ComplexVersion(raw, "build-", new int[0], null, buildNum, null);
    }

    final Matcher matcher = VERSION_PATTERN.matcher(raw);
    if (!matcher.matches()) {
      // Fallback: treat the whole string as a single component if it contains digits
      if (raw.matches(".*\\d+.*")) {
        final List<Integer> nums = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        for (final char c : raw.toCharArray()) {
          if (Character.isDigit(c)) {
            current.append(c);
          } else if (current.length() > 0) {
            nums.add(NumberConversions.parseInt(current.toString()));
            current.setLength(0);
          }
        }
        if (current.length() > 0) {
          nums.add(NumberConversions.parseInt(current.toString()));
        }
        if (!nums.isEmpty()) {
          return new ComplexVersion(raw, null, nums.stream().mapToInt(i -> i).toArray(), null, null, null);
        }
      }
      throw new IllegalArgumentException("Cannot parse version: " + raw);
    }

    String prefix = matcher.group(1);
    if (prefix != null && prefix.isEmpty()) {
      prefix = null;
    }

    final String numericPart = matcher.group(2);
    final String[] parts = numericPart.split("[._]");
    final int[] components = new int[parts.length];
    for (int i = 0; i < parts.length; i++) {
      components[i] = NumberConversions.parseInt(parts[i]);
    }

    final String qualifier = matcher.group(3);
    final String buildMetadata = matcher.group(4);

    // Extract build number from qualifier if present (e.g., "beta.2" or "RC1")
    Integer buildNumber = null;
    if (qualifier != null) {
      final Matcher qualBuildMatcher = Pattern.compile("(\\d+)$").matcher(qualifier);
      if (qualBuildMatcher.find()) {
        buildNumber = NumberConversions.parseInt(qualBuildMatcher.group(1));
      }
    }

    return new ComplexVersion(raw, prefix, components, qualifier, buildNumber, buildMetadata);
  }

  /**
   * Try to parse a version string, returning null if parsing fails.
   */
  public static ComplexVersion tryParse(final String raw) {
    try {
      return parse(raw);
    } catch (final IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * @return the original version string
   */
  public String raw() {
    return raw;
  }

  /**
   * @return the version prefix (e.g., "R", "v", "build-"), or null if none
   */
  public String prefix() {
    return prefix;
  }

  /**
   * @return the numeric version components
   */
  @Override
  public int[] components() {
    return components.clone();
  }

  /**
   * @return the major version (first component), or 0 if no components
   */
  public int major() {
    return components.length > 0 ? components[0] : 0;
  }

  /**
   * @return the minor version (second component), or 0 if not present
   */
  @Override
  public int minor() {
    return components.length > 1 ? components[1] : 0;
  }

  /**
   * @return the patch version (third component), or 0 if not present
   */
  @Override
  public int patch() {
    return components.length > 2 ? components[2] : 0;
  }

  @Override
  public int build() {
    return buildNumber;
  }

  /**
   * @return the version qualifier (e.g., "SNAPSHOT", "beta"), or null if release
   */
  public String qualifier() {
    return qualifier;
  }

  /**
   * @return true if this is a pre-release version (has qualifier like SNAPSHOT,
   *         beta, etc.)
   */
  public boolean isPreRelease() {
    if (qualifier == null) {
      return false;
    }
    final String lower = qualifier.toLowerCase();
    return lower.contains("snapshot") || lower.contains("alpha") ||
        lower.contains("beta") || lower.contains("dev") ||
        lower.contains("rc") || lower.contains("cr") || lower.contains("prerelease") || lower.contains("build");
  }

  /**
   * @return the build number if present
   */
  public Integer buildNumber() {
    return buildNumber;
  }

  @Override
  public int compareTo(final Version v) {
    if (v == null) {
      return 1;
    }
    if (!(v instanceof ComplexVersion)) {
      throw new IllegalArgumentException("Can only compare with another Version Implementation");
    }
    final ComplexVersion other = (ComplexVersion) v;

    // Compare numeric components
    final int maxLen = Math.max(this.components.length, other.components.length);
    for (int i = 0; i < maxLen; i++) {
      final int thisComp = i < this.components.length ? this.components[i] : 0;
      final int otherComp = i < other.components.length ? other.components[i] : 0;
      if (thisComp != otherComp) {
        return Integer.compare(thisComp, otherComp);
      }
    }

    // If components are equal, compare qualifiers
    // No qualifier (release) > any qualifier
    if (this.qualifier == null && other.qualifier == null) {
      // Compare build numbers if both have them
      if (this.buildNumber != null && other.buildNumber != null) {
        return Integer.compare(this.buildNumber, other.buildNumber);
      }
      return 0;
    }
    if (this.qualifier == null) {
      return 1; // Release > pre-release
    }
    if (other.qualifier == null) {
      return -1;
    }

    // Compare qualifier ordering
    final String thisQualLower = this.qualifier.toLowerCase().replaceAll("[^a-z]", "");
    final String otherQualLower = other.qualifier.toLowerCase().replaceAll("[^a-z]", "");

    final int thisQualIdx = findQualifierIndex(thisQualLower);
    final int otherQualIdx = findQualifierIndex(otherQualLower);

    if (thisQualIdx != otherQualIdx) {
      return Integer.compare(thisQualIdx, otherQualIdx);
    }

    // Same qualifier type, compare build numbers
    if (this.buildNumber != null && other.buildNumber != null) {
      return Integer.compare(this.buildNumber, other.buildNumber);
    }

    // Fallback to string comparison
    return this.qualifier.compareToIgnoreCase(other.qualifier);
  }

  private static int findQualifierIndex(final String qual) {
    for (int i = 0; i < QUALIFIER_ORDER.size(); i++) {
      if (qual.contains(QUALIFIER_ORDER.get(i))) {
        return i;
      }
    }
    return QUALIFIER_ORDER.size() / 2; // Unknown qualifiers go in the middle
  }

  @Override
  public ComplexVersion clone() {
    return new ComplexVersion(this);
  }

  @Override
  public int hashCode() {
    int result = 31 + raw.hashCode();
    result = 31 * result + ((prefix == null) ? 0 : prefix.hashCode());
    result = 31 * result + Arrays.hashCode(components);
    result = 31 * result + ((qualifier == null) ? 0 : qualifier.hashCode());
    result = 31 * result + ((buildNumber == null) ? 0 : buildNumber.hashCode());
    result = 31 * result + ((buildMetadata == null) ? 0 : buildMetadata.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ComplexVersion other = (ComplexVersion) obj;
    if (prefix == null) {
      if (other.prefix != null) {
        return false;
      }
    } else if (!prefix.equals(other.prefix)) {
      return false;
    }
    if (qualifier == null) {
      if (other.qualifier != null) {
        return false;
      }
    } else if (!qualifier.equals(other.qualifier)) {
      return false;
    }
    if (buildNumber == null) {
      if (other.buildNumber != null) {
        return false;
      }
    } else if (!buildNumber.equals(other.buildNumber)) {
      return false;
    }
    if (buildMetadata == null) {
      if (other.buildMetadata != null) {
        return false;
      }
    } else if (!buildMetadata.equals(other.buildMetadata)) {
      return false;
    }
    return raw.equals(other.raw) && Arrays.equals(components, other.components);
  }

  @Override
  public String toString() {
    return raw;
  }
}