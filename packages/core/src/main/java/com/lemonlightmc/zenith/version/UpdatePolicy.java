package com.lemonlightmc.zenith.version;

/**
 * Defines how aggressively Hopper should update dependencies.
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
}