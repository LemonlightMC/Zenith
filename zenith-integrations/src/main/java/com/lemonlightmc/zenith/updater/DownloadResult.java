package com.lemonlightmc.zenith.updater;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.lemonlightmc.zenith.version.Version;

/**
 * Result of a download operation.
 */
public record DownloadResult(List<DownloadedDependency> downloaded,
    List<ExistingDependency> existing,
    List<SkippedDependency> skipped,
    List<FailedDependency> failed) {

  public static DownloadResult empty() {
    return new DownloadResult(null, null, null, null);
  }

  public static DownloadResult from(List<DownloadedDependency> downloaded,
      List<ExistingDependency> existing,
      List<SkippedDependency> skipped,
      List<FailedDependency> failed) {
    return new DownloadResult(
        downloaded == null ? List.of() : downloaded,
        existing == null ? List.of() : existing,
        skipped == null ? List.of() : skipped,
        failed == null ? List.of() : failed);
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * @return true if new dependencies were downloaded (requires server restart)
   */
  public boolean requiresRestart() {
    return !downloaded.isEmpty();
  }

  /**
   * @return true if all required dependencies are satisfied (existing or
   *         downloaded)
   */
  public boolean isSuccess() {
    return failed.isEmpty();
  }

  /**
   * @return list of newly downloaded dependencies
   */

  public List<DownloadedDependency> downloaded() {
    return downloaded;
  }

  /**
   * @return list of dependencies that were already present
   */

  public List<ExistingDependency> existing() {
    return existing;
  }

  /**
   * @return list of optional dependencies that were skipped
   */

  public List<SkippedDependency> skipped() {
    return skipped;
  }

  /**
   * @return list of dependencies that failed to resolve/download
   */

  public List<FailedDependency> failed() {
    return failed;
  }

  /**
   * A dependency that was newly downloaded.
   */
  public static record DownloadedDependency(
      String name,
      Version version,
      Path path) {
  }

  /**
   * A dependency that was already present on disk.
   */
  public static record ExistingDependency(
      String name,
      Version version,
      Path path) {
  }

  /**
   * A dependency that was skipped (optional with WARN_SKIP policy).
   */
  public static record SkippedDependency(
      String name,
      String reason) {
  }

  /**
   * A dependency that failed to resolve or download.
   */
  public static record FailedDependency(
      String name,
      String error,
      FailurePolicy policy) {
  }

  // Builder for constructing results
  public static class Builder {
    private final List<DownloadedDependency> downloaded = new ArrayList<>();
    private final List<ExistingDependency> existing = new ArrayList<>();
    private final List<SkippedDependency> skipped = new ArrayList<>();
    private final List<FailedDependency> failed = new ArrayList<>();

    public Builder addDownloaded(String name, Version version, Path path) {
      downloaded.add(new DownloadedDependency(name, version, path));
      return this;
    }

    public Builder addExisting(String name, Version version, Path path) {
      existing.add(new ExistingDependency(name, version, path));
      return this;
    }

    public Builder addSkipped(String name, String reason) {
      skipped.add(new SkippedDependency(name, reason));
      return this;
    }

    public Builder addFailed(String name, String error, FailurePolicy policy) {
      failed.add(new FailedDependency(name, error, policy));
      return this;
    }

    public boolean isCurrentlySuccessful() {
      return failed.isEmpty();
    }

    public DownloadResult build() {
      return new DownloadResult(downloaded, existing, skipped, failed);
    }
  }
}