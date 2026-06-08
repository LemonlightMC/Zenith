package com.lemonlightmc.zenith.dependency.sources;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencySource;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.utils.Checksum.ChecksumType;
import com.lemonlightmc.zenith.utils.HttpUtil;
import com.lemonlightmc.zenith.utils.JsonUtil;
import com.lemonlightmc.zenith.utils.ServerPlatform;
import com.lemonlightmc.zenith.version.Version;

/**
 * Dependency source for Hangar (PaperMC's plugin repository).
 * <p>
 * API: https://hangar.papermc.io/api/v1
 */
public final class HangarSource implements DependencySource {

  private static final String API_BASE = "https://hangar.papermc.io/api/v1/projects/";

  @Override
  public String type() {
    return "hangar";
  }

  private static Iterable<String> platformDownloadKeySet(JsonObject downloads) {
    List<String> keys = new ArrayList<>();
    if (downloads == null)
      return keys;
    for (java.util.Map.Entry<String, JsonElement> e : downloads.entrySet()) {
      keys.add(e.getKey());
    }
    return keys;
  }

  @Override
  public List<Version> fetchVersions(Dependency dependency) throws DependencyException {
    String slug = dependency.identifier();
    ServerPlatform platform = dependency.platform();

    try {
      // Build URL with query parameters
      StringBuilder url = new StringBuilder(API_BASE)
          .append("/projects/").append(slug).append("/versions")
          .append("?limit=25&platform=").append(platform.hangarPlatform());
      url.append("&platformVersion=").append(dependency.effectiveMinecraftVersion().toString());

      JsonObject json = JsonUtil.toJsonObject(JsonUtil.fromJson(HttpUtil.get(url.toString())));
      if (json == null) {
        throw new DependencyException(slug, "Failed to parse Hangar response");
      }

      JsonArray result = JsonUtil.getJsonArray(json, "result");
      if (result == null || result.size() == 0) {
        throw new DependencyException(slug, "No versions found on Hangar");
      }

      List<Version> versions = new ArrayList<>();
      for (JsonElement item : result) {
        String name = JsonUtil.getString(JsonUtil.toJsonObject(item), "name");
        if (name == null) {
          continue;
        }
        Version v = Version.semver(name);
        if (v != null) {
          versions.add(v);
        }
      }
      return versions;
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(slug, "Failed to fetch versions from Hangar: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(Dependency dependency, Version version)
      throws DependencyException {
    String slug = dependency.identifier();
    ServerPlatform platform = dependency.platform();
    String hangarPlatform = platform.hangarPlatform();

    try {
      // Fetch version details
      String url = API_BASE + slug + "/versions/" + version.toString();
      JsonObject json = JsonUtil.toJsonObject(JsonUtil.fromJson(HttpUtil.get(url)));
      if (json == null) {
        throw new DependencyException(slug, "Failed to parse Hangar version response");
      }

      // Get download info
      JsonObject downloads = JsonUtil.getJsonObject(json, "downloads");
      if (downloads == null) {
        throw new DependencyException(slug, "No downloads found for version " + version);
      }
      // Try platform-specific download, fall back to first available
      JsonObject platformDownload = JsonUtil.getJsonObject(downloads, hangarPlatform);
      if (platformDownload == null) {
        // Fall back to first available
        // iterate members of downloads JsonObject
        for (String key : platformDownloadKeySet(downloads)) {
          platformDownload = JsonUtil.getJsonObject(downloads, key);
          if (platformDownload != null)
            break;
        }
      }
      if (platformDownload == null) {
        throw new DependencyException(slug,
            "No downloads found for version " + version + " and Platform " + hangarPlatform);
      }

      JsonObject fileInfo = JsonUtil.getJsonObject(platformDownload, "fileInfo");
      String fileName = fileInfo != null ? JsonUtil.getString(fileInfo, "name") : null;
      String sha256 = fileInfo != null ? JsonUtil.getString(fileInfo, "sha256Hash") : null;

      // Build download URL
      String downloadUrl = JsonUtil.getString(platformDownload, "downloadUrl");
      if (downloadUrl == null) {
        downloadUrl = API_BASE + slug + "/versions/" + version.toString() + "/" + hangarPlatform
            + "/download";
      } else if (!downloadUrl.startsWith("http")) {
        downloadUrl = "https://hangar.papermc.io" + downloadUrl;
      }

      if (fileName == null) {
        fileName = slug + "-" + version.toString() + ".jar";
      }

      return new ResolvedDependency(
          dependency.name(),
          version,
          downloadUrl,
          sha256,
          sha256 != null ? ChecksumType.SHA256 : null,
          dependency.fileName() != null ? dependency.fileName() : fileName);
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(slug, "Failed to resolve Hangar version: " + e.getMessage(), e);
    }
  }
}