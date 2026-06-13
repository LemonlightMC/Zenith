package com.lemonlightmc.zenith.dependency;

import com.lemonlightmc.zenith.version.Version;
import com.lemonlightmc.zenith.version.VersionConstraint;

/**
 * Defines how aggressively DependencyAPI should update dependencies.
 */
public enum UpdatePolicy {
  /**
   * Pinned to exact version from lockfile. No updates allowed.
   */
  NONE,

  /**
   * Only patch version updates allowed (e.g., 5.4.X).
   * Example: 5.4.0 -> 5.4.1, 5.4.2, but NOT 5.5.0
   */
  PATCH,

  /**
   * Minor and patch updates allowed (e.g., 5.X.X).
   * Example: 5.4.0 -> 5.4.1, 5.5.0, 5.9.0, but NOT 6.0.0
   */
  MINOR,

  /**
   * Any newer version allowed (e.g., X.X.X).
   * Example: 5.4.0 -> 5.4.1, 5.5.0, 6.0.0, 7.0.0
   */
  MAJOR;

  /**
   * Returns the more restrictive of two policies.
   */
  public UpdatePolicy merge(final UpdatePolicy other) {
    // NONE < PATCH < MINOR < MAJOR (in terms of restrictiveness, NONE is most
    // restrictive)
    return this.ordinal() < other.ordinal() ? this : other;
  }

  /**
   * Check if this version is compatible with an update policy relative to a
   * baseline.
   *
   * @param baseline the baseline version
   * @param policy   the update policy
   * @return true if this version is allowed by the policy
   */
  public static boolean isAllowedBy(final Version version, final Version baseline, final UpdatePolicy policy) {
    if (policy == UpdatePolicy.NONE) {
      return version.equals(baseline);
    }

    // Must be >= baseline
    if (version.compareTo(baseline) < 0) {
      return false;
    }

    switch (policy) {
      case PATCH:
        return version.major() == baseline.major() && version.minor() == baseline.minor();
      case MINOR:
        return version.major() == baseline.major();
      case MAJOR:
        return true;
      default:
        return false;
    }
  }

  public static VersionConstraint fromPolicy(final Version baseline, final UpdatePolicy policy) {
    switch (policy) {
      case NONE:
        return VersionConstraint.exact(baseline);
      case PATCH:
        // Allow X.Y.* where X.Y matches baseline
        final Version patchMax = Version.semver(baseline.major() + "." + (baseline.minor() + 1) + ".0");
        return VersionConstraint.range(baseline, true, patchMax, false);
      case MINOR:
        // Allow X.*.* where X matches baseline
        final Version minorMax = Version.semver((baseline.major() + 1) + ".0.0");
        return VersionConstraint.range(baseline, true, minorMax, false);
      case MAJOR:
      default:
        return VersionConstraint.atleast(baseline);
    }
  }
}