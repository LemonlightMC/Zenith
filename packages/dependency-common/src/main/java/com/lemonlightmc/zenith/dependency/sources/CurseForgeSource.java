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
 * Dependency source for CurseForge (using the addons-ecs.forgesvc.net proxy
 * API).
 */
public final class CurseForgeSource implements DependencySource {

  private static final String API_BASE = "https://addons-ecs.forgesvc.net/api/v2/addon/";

  @Override
  public String type() {
    return "curseforge";
  }

  @Override
  public List<Version> fetchVersions(final Dependency dependency) throws DependencyException {
    final String id = dependency.identifier();
    try {
      final int addonId = Integer.parseInt(id);
      final String url = API_BASE + addonId + "/files";
      final String resp = HttpUtil.get(url);
      final JsonArray files = JsonUtil.toJsonArray(JsonUtil.fromJson(resp));
      if (files == null || files.size() == 0) {
        throw new DependencyException(id, "No files found on CurseForge");
      }
      final List<Version> versions = new ArrayList<>();
      for (final JsonElement el : files) {
        final JsonObject file = JsonUtil.toJsonObject(el);
        final String fileName = JsonUtil.getString(file, "fileName");
        if (fileName == null)
          continue;
        final Version v = Version.trySemver(fileName);
        if (v != null) {
          versions.add(v);
        }
      }
      if (versions.isEmpty()) {
        // try to use file IDs as last resort
        for (final JsonElement el : files) {
          final JsonObject file = JsonUtil.toJsonObject(el);
          final String displayName = JsonUtil.getString(file, "displayName");
          final Version v = displayName != null ? Version.trySemver(displayName) : null;
          if (v != null) {
            versions.add(v);
          }
        }
      }
      return versions;
    } catch (final NumberFormatException e) {
      throw new DependencyException(id, "CurseForge identifier must be numeric addon id");
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(id, "Failed to fetch versions from CurseForge: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(final Dependency dependency, final Version version) throws DependencyException {
    final String id = dependency.identifier();
    try {
      final int addonId = Integer.parseInt(id);
      final String url = API_BASE + addonId + "/files";
      final String resp = HttpUtil.get(url);
      final JsonArray files = JsonUtil.toJsonArray(JsonUtil.fromJson(resp));
      if (files == null || files.size() == 0) {
        throw new DependencyException(id, "No files found on CurseForge");
      }

      JsonObject target = null;
      for (final JsonElement el : files) {
        final JsonObject file = JsonUtil.toJsonObject(el);
        final String fileName = JsonUtil.getString(file, "fileName");
        if (fileName != null && (fileName.contains(version.toString()) || fileName.equals(version.toString()))) {
          target = file;
          break;
        }
        final String displayName = JsonUtil.getString(file, "displayName");
        if (displayName != null && displayName.contains(version.toString())) {
          target = file;
          break;
        }
      }

      if (target == null) {
        // fallback: choose first file
        target = JsonUtil.toJsonObject(files.get(0));
      }

      final String downloadUrl = JsonUtil.getString(target, "downloadUrl");
      final String fileName = JsonUtil.getString(target, "fileName");
      if (downloadUrl == null) {
        throw new DependencyException(id, "No download URL found for selected CurseForge file");
      }

      return new ResolvedDependency(dependency.name(), version, downloadUrl,
          dependency.fileName() != null ? dependency.fileName()
              : fileName != null ? fileName : (dependency.identifier() + "-" + version + ".jar"));
    } catch (final NumberFormatException e) {
      throw new DependencyException(id, "CurseForge identifier must be numeric addon id");
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(id, "Failed to resolve CurseForge addon: " + e.getMessage(), e);
    }
  }
}
