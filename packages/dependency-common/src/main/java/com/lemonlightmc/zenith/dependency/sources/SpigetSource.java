package com.lemonlightmc.zenith.dependency.sources;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencySource;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.utils.HttpUtil;
import com.lemonlightmc.zenith.utils.JsonUtil;
import com.lemonlightmc.zenith.version.Version;

/**
 * Dependency source for SpigotMC via Spiget API.
 * <p>
 * API: https://api.spiget.org/v2
 * <p>
 * Note: Some resources may require manual download from SpigotMC (premium
 * resources).
 */
public final class SpigetSource implements DependencySource {

  private static final String API_BASE = "https://api.spiget.org/v2/resources/";

  @Override
  public String type() {
    return "spiget";
  }

  @Override
  public List<Version> fetchVersions(final Dependency dependency) throws DependencyException {
    final String resourceId = dependency.identifier();

    try {
      // Fetch resource info first to get name
      final JsonObject resourceJson = JsonUtil.toJsonObject(JsonUtil.fromJson(HttpUtil.get(API_BASE + resourceId)));
      if (resourceJson == null) {
        throw new DependencyException(resourceId, "Resource not found on Spiget");
      }

      // Fetch versions
      final String versionsUrl = API_BASE + resourceId + "/versions?size=25&sort=-releaseDate";
      final JsonArray versions = JsonUtil.toJsonArray(JsonUtil.fromJson(HttpUtil.get(versionsUrl)));

      // Fallback: try to get current version from resource info
      if (versions == null || versions.size() == 0) {
        final String currentVersion = JsonUtil.getString(resourceJson, "version");
        if (currentVersion == null) {
          throw new DependencyException(resourceId, "No versions found on Spiget");
        }
        final Version v = Version.trySemver(currentVersion);
        if (v != null) {
          return List.of(v);
        }
      }

      final List<Version> result = new ArrayList<>();
      for (final JsonElement item : versions) {
        final String name = JsonUtil.getString(JsonUtil.toJsonObject(item), "name");
        if (name == null) {
          continue;
        }
        final Version v = Version.trySemver(name);
        if (v != null) {
          result.add(v);
        }
      }

      // If no parseable versions, use current
      if (result.isEmpty()) {
        final String currentVersion = JsonUtil.getString(resourceJson, "version");
        if (currentVersion != null) {
          final Version v = Version.semver(currentVersion);
          if (v != null) {
            result.add(v);
          }
        }
      }

      return result;
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(resourceId, "Failed to fetch versions from Spiget: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(final Dependency dependency, final Version version)
      throws DependencyException {
    final String resourceId = dependency.identifier();

    try {
      // Fetch resource info
      final JsonObject resourceJson = JsonUtil.toJsonObject(JsonUtil.fromJson(HttpUtil.get(API_BASE + resourceId)));
      if (resourceJson == null) {
        throw new DependencyException(resourceId, "Resource not found on Spiget");
      }

      String name = JsonUtil.getString(resourceJson, "name");
      if (name == null) {
        name = "Resource-" + resourceId;
      }

      // Check if external URL is available
      final Boolean external = JsonUtil.getBool(resourceJson, "external");
      final JsonObject file = JsonUtil.getJsonObject(resourceJson, "file");
      final String externalUrl = file != null ? JsonUtil.getString(file, "externalUrl") : null;

      String downloadUrl;
      if (Boolean.TRUE.equals(external) && externalUrl != null && !externalUrl.isEmpty()) {
        downloadUrl = externalUrl;
      } else {
        // Use Spiget download endpoint
        downloadUrl = API_BASE + resourceId + "/download";
      }

      // Generate filename
      String fileName = dependency.fileName();
      if (fileName == null) {
        final String safeName = name.replaceAll("[^a-zA-Z0-9.-]", "_");
        fileName = safeName + "-" + version.toString() + ".jar";
      }

      return new ResolvedDependency(
          dependency.name() != null ? dependency.name() : name,
          version,
          downloadUrl,
          fileName);
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(resourceId, "Failed to resolve Spiget resource: " + e.getMessage(), e);
    }
  }
}
