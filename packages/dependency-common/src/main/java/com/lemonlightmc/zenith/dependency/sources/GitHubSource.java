package com.lemonlightmc.zenith.dependency.sources;

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
  public List<Version> fetchVersions(Dependency dependency) throws DependencyException {
    String repo = dependency.identifier();
    try {
      String url = API_BASE + "/repos/" + repo + "/releases?per_page=25";
      String response = HttpUtil.get(url, "Accept", "application/vnd.github.v3+json");
      JsonArray releases = JsonUtil.toJsonArray(JsonUtil.fromJson(response));

      if (releases == null || releases.size() == 0) {
        throw new DependencyException(repo, "No releases found on GitHub");
      }
      List<Version> versions = new ArrayList<>();
      for (JsonElement item : releases) {
        JsonObject release = JsonUtil.toJsonObject(item);

        // Skip drafts and prereleases unless explicitly wanted
        Boolean draft = JsonUtil.getBool(release, "draft");
        if (Boolean.TRUE.equals(draft)) {
          continue;
        }

        String tagName = JsonUtil.getString(release, "tag_name");
        if (tagName != null) {
          // Remove common prefixes like "v" or "release-"
          String cleanVersion = tagName.replaceFirst("^[vV]", "")
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
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(repo, "Failed to fetch releases from GitHub: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(Dependency dependency, Version version)
      throws DependencyException {
    String repo = dependency.identifier();
    String assetPattern = dependency.assetPattern();

    try {
      // Find the release with this version
      String releasesUrl = API_BASE + "/repos/" + repo + "/releases?per_page=50";
      String response = HttpUtil.get(releasesUrl, "Accept", "application/vnd.github.v3+json");
      JsonArray releases = JsonUtil.toJsonArray(JsonUtil.fromJson(response));

      if (releases == null) {
        throw new DependencyException(repo, "Failed to parse GitHub releases");
      }
      JsonObject targetRelease = null;
      for (JsonElement item : releases) {
        JsonObject release = JsonUtil.toJsonObject(item);
        String tagName = JsonUtil.getString(release, "tag_name");

        if (tagName != null) {
          String cleanVersion = tagName.replaceFirst("^[vV]", "")
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
      JsonArray assets = JsonUtil.getJsonArray(targetRelease, "assets");
      if (assets == null || assets.size() == 0) {
        throw new DependencyException(repo, "No assets found for release " + version);
      }

      JsonObject targetAsset = null;

      if (assetPattern != null) {
        // Find asset matching pattern
        for (JsonElement assetEl : assets) {
          JsonObject asset = JsonUtil.toJsonObject(assetEl);
          String name = JsonUtil.getString(asset, "name");
          if (name != null && GlobUtils.matches(name, assetPattern)) {
            targetAsset = asset;
            break;
          }
        }
      }

      if (targetAsset == null) {
        // Default: find first .jar file
        for (JsonElement assetEl : assets) {
          JsonObject asset = JsonUtil.toJsonObject(assetEl);
          String name = JsonUtil.getString(asset, "name");
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

      String downloadUrl = JsonUtil.getString(targetAsset, "browser_download_url");
      String fileName = JsonUtil.getString(targetAsset, "name");

      if (downloadUrl == null) {
        throw new DependencyException(repo, "No download URL for asset");
      }

      return new ResolvedDependency(
          dependency.name(),
          version,
          downloadUrl,
          dependency.fileName() != null ? dependency.fileName() : fileName);
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(repo, "Failed to resolve GitHub release: " + e.getMessage(), e);
    }
  }
}
