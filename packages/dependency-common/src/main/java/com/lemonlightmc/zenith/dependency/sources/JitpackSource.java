package com.lemonlightmc.zenith.dependency.sources;

import java.util.List;

import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencySource;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.version.Version;

/**
 * Dependency source for JitPack-built artifacts.
 * <p>
 * This implementation supports GitHub-style identifiers like "owner/repo" and
 * uses GitHub releases for listing versions. Resolution constructs a typical
 * JitPack artifact URL.
 */
public final class JitpackSource implements DependencySource {

  @Override
  public String type() {
    return "jitpack";
  }

  @Override
  public List<Version> fetchVersions(final Dependency dependency) throws DependencyException {
    final String id = dependency.identifier();
    // Delegate to GitHub when identifier is owner/repo
    if (id.contains("/")) {
      try {
        final GitHubSource gh = new GitHubSource();
        return gh.fetchVersions(dependency);
      } catch (final DependencyException e) {
        throw e;
      } catch (final Exception e) {
        throw new DependencyException(id, "Failed to fetch versions from JitPack/GitHub: " + e.getMessage(), e);
      }
    }
    throw new DependencyException(id, "JitPack identifier must be in 'owner/repo' format");
  }

  @Override
  public ResolvedDependency resolve(final Dependency dependency, final Version version) throws DependencyException {
    final String id = dependency.identifier();
    if (!id.contains("/")) {
      throw new DependencyException(id, "JitPack identifier must be in 'owner/repo' format");
    }
    final String[] parts = id.split("/", 2);
    final String owner = parts[0];
    final String repo = parts[1];

    try {
      final String fileName = dependency.fileName() != null ? dependency.fileName()
          : repo + "-" + version.toString() + ".jar";
      final String downloadUrl = "https://jitpack.io/com/github/" + owner + "/" + repo + "/" + version.toString() + "/"
          + fileName;

      return new ResolvedDependency(dependency.name(), version, downloadUrl, fileName);
    } catch (final Exception e) {
      throw new DependencyException(id, "Failed to resolve JitPack artifact: " + e.getMessage(), e);
    }
  }
}
