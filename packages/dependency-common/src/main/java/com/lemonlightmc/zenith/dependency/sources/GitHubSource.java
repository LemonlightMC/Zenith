package com.lemonlightmc.zenith.dependency.sources;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencySource;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.utils.GlobUtils;
import com.lemonlightmc.zenith.utils.HttpUtil;
import com.lemonlightmc.zenith.utils.JsonUtil;
import com.lemonlightmc.zenith.version.Version;

/**
 * Dependency source for GitHub Releases.
 * <p>
 * API: https://api.github.com
 */
public final class GitHubSource implements DependencySource {

  private static final String API_BASE = "https://api.github.com";

  @Override
  public String type() {
    return "github";
  }

  @Override
  public List<Version> fetchVersions(final Dependency dependency) throws DependencyException {
    final String repo = dependency.identifier();
    try {
      final String url = API_BASE + "/repos/" + repo + "/releases?per_page=25";
      final HttpRequest request = HttpUtil
          .requestBuilder(url, "GET", List.of("Accept", "application/vnd.github.v3+json"))
          .build();
      final JsonArray releases = JsonUtil.toJsonArray(JsonUtil.fromJson(HttpUtil.get(request)));

      if (releases == null || releases.size() == 0) {
        throw new DependencyException(repo, "No releases found on GitHub");
      }
      final List<Version> versions = new ArrayList<>();
      for (final JsonElement item : releases) {
        final JsonObject release = JsonUtil.toJsonObject(item);

        // Skip drafts and prereleases unless explicitly wanted
        final Boolean draft = JsonUtil.getBool(release, "draft");
        if (Boolean.TRUE.equals(draft)) {
          continue;
        }

        final String tagName = JsonUtil.getString(release, "tag_name");
        if (tagName != null) {
          // Remove common prefixes like "v" or "release-"
          final String cleanVersion = tagName.replaceFirst("^[vV]", "")
              .replaceFirst("^release[-_]?", "");
          Version v = Version.semver(cleanVersion);
          if (v == null) {
            v = Version.semver(tagName);
          }
          if (v != null) {
            versions.add(v);
          }
        }
      }

      return versions;
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(repo, "Failed to fetch releases from GitHub: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(final Dependency dependency, final Version version)
      throws DependencyException {
    final String repo = dependency.identifier();
    final String assetPattern = dependency.assetPattern();

    try {
      // Find the release with this version
      final String url = API_BASE + "/repos/" + repo + "/releases?per_page=50";
      final HttpRequest request = HttpUtil
          .requestBuilder(url, "GET", List.of("Accept", "application/vnd.github.v3+json"))
          .build();
      final JsonArray releases = JsonUtil.toJsonArray(JsonUtil.fromJson(HttpUtil.get(request)));

      if (releases == null) {
        throw new DependencyException(repo, "Failed to parse GitHub releases");
      }
      JsonObject targetRelease = null;
      for (final JsonElement item : releases) {
        final JsonObject release = JsonUtil.toJsonObject(item);
        final String tagName = JsonUtil.getString(release, "tag_name");

        if (tagName != null) {
          final String cleanVersion = tagName.replaceFirst("^[vV]", "")
              .replaceFirst("^release[-_]?", "");
          if (cleanVersion.equals(version.toString()) || tagName.equals(version.toString())) {
            targetRelease = release;
            break;
          }
        }
      }

      if (targetRelease == null) {
        throw new DependencyException(repo, "Release not found on GitHub: " + version);
      }
      // Find matching asset
      final JsonArray assets = JsonUtil.getJsonArray(targetRelease, "assets");
      if (assets == null || assets.size() == 0) {
        throw new DependencyException(repo, "No assets found for release " + version);
      }

      JsonObject targetAsset = null;

      if (assetPattern != null) {
        // Find asset matching pattern
        for (final JsonElement assetEl : assets) {
          final JsonObject asset = JsonUtil.toJsonObject(assetEl);
          final String name = JsonUtil.getString(asset, "name");
          if (name != null && GlobUtils.matches(name, assetPattern)) {
            targetAsset = asset;
            break;
          }
        }
      }

      if (targetAsset == null) {
        // Default: find first .jar file
        for (final JsonElement assetEl : assets) {
          final JsonObject asset = JsonUtil.toJsonObject(assetEl);
          final String name = JsonUtil.getString(asset, "name");
          if (name != null && name.endsWith(".jar")) {
            targetAsset = asset;
            break;
          }
        }
      }

      if (targetAsset == null) {
        // Fall back to first asset
        targetAsset = JsonUtil.toJsonObject(assets.get(0));
      }

      final String downloadUrl = JsonUtil.getString(targetAsset, "browser_download_url");
      final String fileName = JsonUtil.getString(targetAsset, "name");

      if (downloadUrl == null) {
        throw new DependencyException(repo, "No download URL for asset");
      }

      return new ResolvedDependency(
          dependency.name(),
          version,
          downloadUrl,
          dependency.fileName() != null ? dependency.fileName() : fileName);
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(repo, "Failed to resolve GitHub release: " + e.getMessage(), e);
    }
  }
}
