package com.lemonlightmc.zenith.version;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class VersionConstraintsHolder {

  public static record TildeConstraint(Version baseVersion, String original) implements VersionConstraint2 {

    public TildeConstraint(final String versionStr) {
      this(Version.semver(versionStr), versionStr);
    }

    @Override
    public boolean satisfies(final Version version) {
      if (version.compareTo(baseVersion) < 0) {
        return false;
      }
      final Version upperBound = Version.semver(baseVersion.major(), baseVersion.minor() + 1, 0);
      return version.compareTo(upperBound) < 0;
    }

    @Override
    public List<String> getVersions() {
      return Collections.singletonList(
          ">=" + baseVersion + " <" + baseVersion.major() + "." + (baseVersion.minor() + 1) + ".0");
    }

    @Override
    public Version getLowVersion() {
      return baseVersion;
    }

    @Override
    public Version getMaxVersion() {
      return Version.semver(baseVersion.major(), baseVersion.minor() + 1, 0);
    }

    @Override
    public String original() {
      return original;
    }
  }

  public static record CaretConstraint(Version baseVersion, String original) implements VersionConstraint2 {

    public CaretConstraint(final String versionStr) {
      this(Version.semver(versionStr), versionStr);
    }

    @Override
    public boolean satisfies(final Version version) {
      return version.compareTo(baseVersion) >= 0 &&
          version.compareTo(Version.semver(baseVersion.major() + 1, 0, 0)) < 0;
    }

    @Override
    public String original() {
      return original;
    }

    @Override
    public List<String> getVersions() {
      return Collections.singletonList(">=" + baseVersion + " <" + (baseVersion.major() + 1) + ".0.0");
    }

    @Override
    public Version getLowVersion() {
      return baseVersion;
    }

    @Override
    public Version getMaxVersion() {
      return Version.semver(baseVersion.major() + 1, 0, 0);
    }
  }

  public static record ExactVersionConstraint(Version targetVersion, String original)
      implements VersionConstraint2 {

    public ExactVersionConstraint(final String versionStr) {
      this(Version.semver(versionStr), versionStr);
    }

    public ExactVersionConstraint(final Version targetVersion) {
      this(targetVersion, null);
    }

    @Override
    public boolean satisfies(final Version version) {
      return targetVersion.equals(version);
    }

    @Override
    public String original() {
      return original;
    }

    @Override
    public List<String> getVersions() {
      return Collections.singletonList(targetVersion.toString());
    }

    @Override
    public Version getLowVersion() {
      return targetVersion;
    }

    @Override
    public Version getMaxVersion() {
      return targetVersion;
    }
  }

  public static record OrConstraint(List<VersionConstraint2> constraints, String original)
      implements VersionConstraint2 {
    public OrConstraint(final List<VersionConstraint2> constraints, final String original) {
      if (constraints == null || constraints.isEmpty()) {
        throw new IllegalArgumentException("OrConstraint must have at least one constraint");
      }
      constraints.removeIf(v -> v == null);
      this.constraints = constraints;
      this.original = original == null ? constraints.stream().map(VersionConstraint2::original).filter(s -> s != null)
          .collect(Collectors.joining(" || ")) : original;
    }

    @Override
    public boolean satisfies(final Version version) {
      return constraints.stream().anyMatch(constraint -> constraint.satisfies(version));
    }

    @Override
    public List<String> getVersions() {
      final List<String> versions = new ArrayList<>();
      for (final VersionConstraint2 constraint : constraints) {
        versions.addAll(constraint.getVersions());
      }
      return versions;
    }

    @Override
    public Version getLowVersion() {
      return constraints.stream()
          .map(VersionConstraint2::getLowVersion)
          .min(Version::compareTo)
          .orElse(null);
    }

    @Override
    public Version getMaxVersion() {
      return constraints.stream()
          .map(VersionConstraint2::getMaxVersion)
          .max(Version::compareTo)
          .orElse(null);
    }
  }

  public record CompositeConstraint(List<VersionConstraint2> constraints, String original)
      implements VersionConstraint2 {
    public CompositeConstraint(final List<VersionConstraint2> constraints, final String original) {
      this.constraints = new ArrayList<>(constraints);
      this.original = original;
    }

    @Override
    public boolean satisfies(final Version version) {
      return constraints.stream().allMatch(constraint -> constraint.satisfies(version));
    }

    @Override
    public List<String> getVersions() {
      return constraints.stream()
          .flatMap(constraint -> constraint.getVersions().stream())
          .collect(Collectors.toList());
    }

    @Override
    public Version getLowVersion() {
      return constraints.stream()
          .map(VersionConstraint2::getLowVersion)
          .max(Version::compareTo)
          .orElse(null);
    }

    @Override
    public Version getMaxVersion() {
      return constraints.stream()
          .map(VersionConstraint2::getMaxVersion)
          .min(Version::compareTo)
          .orElse(null);
    }
  }

  public static record RangeConstraint(Version minVer, Version maxVer, boolean includeMin,
      boolean includeMax, String original) implements VersionConstraint2 {

    public RangeConstraint(final VersionRange range) {
      this(range.getMin(), range.getMax(), range.isMinInclusive(), range.isMaxInclusive(), range.toString());
    }

    public RangeConstraint(final Version minVer, final Version maxVer, final String original) {
      this(minVer, maxVer, true, true, original);
    }

    @Override
    public boolean satisfies(final Version version) {
      if (minVer != null) {
        final int cmp = version.compareTo(minVer);
        if ((includeMin && cmp < 0) || (!includeMin && cmp <= 0))
          return false;
      }

      if (maxVer != null) {
        final int cmp = version.compareTo(maxVer);
        return (!includeMax || cmp <= 0) && (includeMax || cmp < 0);
      }

      return true;
    }

    @Override
    public List<String> getVersions() {
      final StringBuilder builder = new StringBuilder();
      if (minVer != null) {
        builder.append(includeMin ? ">=" : ">").append(minVer);
      }
      if (maxVer != null) {
        if (!builder.isEmpty())
          builder.append(" ");
        builder.append(includeMax ? "<=" : "<").append(maxVer);
      }
      return Collections.singletonList(builder.toString());
    }

    @Override
    public Version getLowVersion() {
      return minVer != null ? minVer : null;
    }

    @Override
    public Version getMaxVersion() {
      return maxVer != null ? maxVer : null;
    }
  }

  public static class VersionConstraintParser {
    private static final String VERSION_PATTERN = "\\d+\\.\\d+\\.\\d+(?:-(?:pre|rc)\\d+)?|b\\d+\\.\\d+\\.\\d+|\\d{2}w\\d{2}[a-z](?:_or_[a-z])?|[\\w.-]+";

    private static final Pattern SIMPLE_VERSION_PATTERN = Pattern.compile("^(\\d+(?:\\.\\d+){1,2})$");
    private static final Pattern EXACT_PATTERN = Pattern.compile("^(" + VERSION_PATTERN + ")$");
    private static final Pattern RANGE_PATTERN = Pattern
        .compile("^(" + VERSION_PATTERN + ")-(" + VERSION_PATTERN + ")$");
    private static final Pattern EQUAL_PATTERN = Pattern.compile("^=(" + VERSION_PATTERN + ")$");
    private static final Pattern TILDE_PATTERN = Pattern.compile("^~(" + VERSION_PATTERN + ")$");
    private static final Pattern CARET_PATTERN = Pattern.compile("^\\^(" + VERSION_PATTERN + ")$");
    private static final Pattern COMPARISON_PATTERN = Pattern.compile("^(>=|<=|>|<)\\s*(" + VERSION_PATTERN + ")$");
    private static final Pattern MAVEN_RANGE_PATTERN = Pattern.compile("^[\\[(]([\\w.,-]+)[])]$");
    private static final Pattern COMPOSITE_PATTERN = Pattern.compile("^(.+?)\\s+(.+)$");

    public static VersionConstraint2 parse(final String constraintStr) throws IllegalArgumentException {
      if (constraintStr == null || constraintStr.isEmpty()) {
        throw new IllegalArgumentException("Version constraint cannot be null or empty");
      }
      final String trimmed = constraintStr.trim();
      if (trimmed.isEmpty()) {
        throw new IllegalArgumentException("Version constraint cannot be empty");
      }

      // Check for simple version pattern first (e.g., 1.12.2)
      final Matcher simpleMatcher = SIMPLE_VERSION_PATTERN.matcher(trimmed);
      if (simpleMatcher.matches()) {
        return new ExactVersionConstraint(simpleMatcher.group(1));
      }

      final Matcher equalMatcher = EQUAL_PATTERN.matcher(trimmed);
      if (equalMatcher.matches())
        return new ExactVersionConstraint(equalMatcher.group(1));

      final Matcher rangeMatcher = RANGE_PATTERN.matcher(trimmed);
      if (rangeMatcher.matches()) {
        final Version min = Version.semver(rangeMatcher.group(1));
        final Version max = Version.semver(rangeMatcher.group(2));
        return new RangeConstraint(min, max, true, true, trimmed);
      }

      final Matcher tildeMatcher = TILDE_PATTERN.matcher(trimmed);
      if (tildeMatcher.matches())
        return new TildeConstraint(trimmed);

      final Matcher caretMatcher = CARET_PATTERN.matcher(trimmed);
      if (caretMatcher.matches())
        return new CaretConstraint(trimmed);

      final Matcher comparisonMatcher = COMPARISON_PATTERN.matcher(trimmed);
      if (comparisonMatcher.matches()) {
        return parseComparison(comparisonMatcher.group(1), comparisonMatcher.group(2), trimmed);
      }

      final Matcher mavenRangeMatcher = MAVEN_RANGE_PATTERN.matcher(trimmed);
      if (mavenRangeMatcher.matches()) {
        return parseMavenRange(trimmed, mavenRangeMatcher.group(1));
      }

      final Matcher compositeMatcher = COMPOSITE_PATTERN.matcher(trimmed);
      if (compositeMatcher.matches()) {
        try {
          final VersionConstraint2 first = parse(compositeMatcher.group(1));
          final VersionConstraint2 second = parse(compositeMatcher.group(2));
          return new CompositeConstraint(List.of(first, second), trimmed);
        } catch (final Exception e) {
          throw new IllegalArgumentException("Unable to parse version constraint: " + constraintStr);
        }
      }

      // Fallback to generic EXACT_PATTERN for other cases
      final Matcher exactMatcher = EXACT_PATTERN.matcher(trimmed);
      if (exactMatcher.matches())
        return new ExactVersionConstraint(exactMatcher.group(1));

      throw new IllegalArgumentException("Unable to parse version constraint: " + constraintStr);
    }

    private static VersionConstraint2 parseComparison(final String operator, final String versionStr,
        final String original)
        throws IllegalArgumentException {
      final Version version = Version.semver(versionStr);
      return switch (operator) {
        case ">=" -> new RangeConstraint(version, null, true, false, original);
        case "<=" -> new RangeConstraint(null, version, false, true, original);
        case ">" -> new RangeConstraint(version, null, false, false, original);
        case "<" -> new RangeConstraint(null, version, false, false, original);
        default -> throw new IllegalArgumentException("Unknown comparison operator: " + operator);
      };
    }

    private static VersionConstraint2 parseMavenRange(final String original, final String content) {
      final boolean includeMin = original.startsWith("[");
      final boolean includeMax = original.endsWith("]");

      final String[] parts = content.split(",");
      switch (parts.length) {
        case 1:
          if (original.endsWith(",)")) {
            final Version min = Version.semver(parts[0]);
            return new RangeConstraint(min, null, includeMin, false, original);
          } else {
            return new ExactVersionConstraint(parts[0]);
          }

        case 2:
          final Version min = Version.semver(parts[0]);
          final Version max = parts[1].isEmpty() ? null : Version.semver(parts[1]);
          return new RangeConstraint(min, max, includeMin, includeMax, original);

        default:
          final List<VersionConstraint2> constraints = Arrays.stream(parts)
              .filter(s -> !s.isEmpty())
              .map(ExactVersionConstraint::new)
              .collect(Collectors.toList());
          return new OrConstraint(constraints, original);
      }
    }
  }
}
