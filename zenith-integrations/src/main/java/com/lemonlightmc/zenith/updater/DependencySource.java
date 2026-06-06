package com.lemonlightmc.zenith.updater;

import java.util.List;

import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.updater.Checksum.ChecksumType;
import com.lemonlightmc.zenith.version.Version;

/**
 * Interface for plugin sources (Hangar, Modrinth, Spiget, GitHub, etc.).
 */
public interface DependencySource {

  /**
   * @return the source type identifier (e.g., "hangar", "modrinth", "github")
   */

  String type();

  /**
   * Fetch available versions from this source.
   *
   * @param dependency the dependency to fetch versions for
   * @return list of available versions, newest first
   * @throws DependencyException if fetching fails
   */

  List<Version> fetchVersions(Dependency dependency) throws DependencyException;

  /**
   * Resolve the download information for a specific version.
   *
   * @param dependency the dependency
   * @param version    the version to resolve
   * @return resolved download info
   * @throws DependencyException if resolution fails
   */

  ResolvedDependency resolve(Dependency dependency, Version version) throws DependencyException;

  /**
   * Resolved dependency with download URL and metadata.
   */
  record ResolvedDependency(
      String name,
      Version version,
      String downloadUrl,
      String checksum,
      ChecksumType checksumType,
      String fileName) {
    /**
     * Convenience constructor for no checksum.
     */
    public ResolvedDependency(
        String name,
        Version version,
        String downloadUrl,
        String fileName) {
      this(name, version, downloadUrl, null, null, fileName);
    }
  }

}
