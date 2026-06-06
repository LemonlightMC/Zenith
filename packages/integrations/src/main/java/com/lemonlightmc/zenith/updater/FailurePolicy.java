package com.lemonlightmc.zenith.updater;

/**
 * Defines how failures should be handled when resolving or downloading
 * dependencies.
 */
public enum FailurePolicy {
  /**
   * Throw an exception and abort. Use for required dependencies.
   */
  FAIL,

  /**
   * Log a warning and try to use the latest available version.
   * Useful when a version constraint cannot be satisfied but any version might
   * work.
   */
  WARN_USE_LATEST,

  /**
   * Log a warning and skip this dependency.
   * Use for optional dependencies.
   */
  WARN_SKIP
}