package com.lemonlightmc.zenith.dependency.sources;

import java.net.URI;
import java.util.List;

import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencySource;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.utils.Checksum.ChecksumType;
import com.lemonlightmc.zenith.version.Version;

/**
 * Dependency source for direct URL downloads.
 * <p>
 * This source doesn't support version resolution - it just downloads from the
 * provided URL.
 */
public final class UrlSource implements DependencySource {

  @Override
  public String type() {
    return "url";
  }

  @Override
  public List<Version> fetchVersions(final Dependency dependency) throws DependencyException {
    // URL source doesn't support version listing
    // Return the constraint version if specified, or a dummy version
    if (dependency.constraint() != null && dependency.constraint().isExact()) {
      return List.of(dependency.constraint().asExact().version());
    }

    // Return a dummy version for latest
    return List.of(Version.semver("1.0.0"));
  }

  @Override
  public ResolvedDependency resolve(final Dependency dependency, final Version version)
      throws DependencyException {
    final String url = dependency.identifier();
    String fileName = dependency.fileName();

    try {
      // Extract filename from URL
      if (fileName == null) {
        final URI uri = URI.create(url);
        final String path = uri.getPath();
        if (path != null && !path.isEmpty()) {
          final int lastSlash = path.lastIndexOf('/');
          fileName = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;

          // Clean up query strings
          final int queryStart = fileName.indexOf('?');
          if (queryStart >= 0) {
            fileName = fileName.substring(0, queryStart);
          }
        }

        if (fileName == null || fileName.isEmpty()) {
          fileName = "download-" + System.currentTimeMillis() + ".jar";
        }
      }

      final String sha256 = dependency.sha256();
      return new ResolvedDependency(
          dependency.name(),
          version,
          url,
          sha256,
          sha256 != null ? ChecksumType.SHA256 : null,
          fileName);
    } catch (final Exception e) {
      throw new DependencyException(url, "Failed to resolve URL: " + e.getMessage(), e);
    }
  }
}