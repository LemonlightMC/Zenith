package com.lemonlightmc.zenith.updater.sources;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.updater.Dependency;
import com.lemonlightmc.zenith.updater.DependencySource;
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
  public List<Version> fetchVersions(Dependency dependency) throws DependencyException {
    String id = dependency.identifier();
    try {
      int addonId = Integer.parseInt(id);
      String url = API_BASE + addonId + "/files";
      String resp = HttpUtil.get(url);
      JsonArray files = JsonUtil.toJsonArray(JsonUtil.fromJson(resp));
      if (files == null || files.size() == 0) {
        throw new DependencyException(id, "No files found on CurseForge");
      }
      List<Version> versions = new ArrayList<>();
      for (JsonElement el : files) {
        JsonObject file = JsonUtil.toJsonObject(el);
        String fileName = JsonUtil.getString(file, "fileName");
        if (fileName == null)
          continue;
        Version v = Version.trySemver(fileName);
        if (v != null) {
          versions.add(v);
        }
      }
      if (versions.isEmpty()) {
        // try to use file IDs as last resort
        for (JsonElement el : files) {
          JsonObject file = JsonUtil.toJsonObject(el);
          String displayName = JsonUtil.getString(file, "displayName");
          Version v = displayName != null ? Version.trySemver(displayName) : null;
          if (v != null) {
            versions.add(v);
          }
        }
      }
      return versions;
    } catch (NumberFormatException e) {
      throw new DependencyException(id, "CurseForge identifier must be numeric addon id");
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(id, "Failed to fetch versions from CurseForge: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(Dependency dependency, Version version) throws DependencyException {
    String id = dependency.identifier();
    try {
      int addonId = Integer.parseInt(id);
      String url = API_BASE + addonId + "/files";
      String resp = HttpUtil.get(url);
      JsonArray files = JsonUtil.toJsonArray(JsonUtil.fromJson(resp));
      if (files == null || files.size() == 0) {
        throw new DependencyException(id, "No files found on CurseForge");
      }

      JsonObject target = null;
      for (JsonElement el : files) {
        JsonObject file = JsonUtil.toJsonObject(el);
        String fileName = JsonUtil.getString(file, "fileName");
        if (fileName != null && (fileName.contains(version.toString()) || fileName.equals(version.toString()))) {
          target = file;
          break;
        }
        String displayName = JsonUtil.getString(file, "displayName");
        if (displayName != null && displayName.contains(version.toString())) {
          target = file;
          break;
        }
      }

      if (target == null) {
        // fallback: choose first file
        target = JsonUtil.toJsonObject(files.get(0));
      }

      String downloadUrl = JsonUtil.getString(target, "downloadUrl");
      String fileName = JsonUtil.getString(target, "fileName");
      if (downloadUrl == null) {
        throw new DependencyException(id, "No download URL found for selected CurseForge file");
      }

      return new ResolvedDependency(dependency.name(), version, downloadUrl,
          dependency.fileName() != null ? dependency.fileName()
              : fileName != null ? fileName : (dependency.identifier() + "-" + version + ".jar"));
    } catch (NumberFormatException e) {
      throw new DependencyException(id, "CurseForge identifier must be numeric addon id");
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(id, "Failed to resolve CurseForge addon: " + e.getMessage(), e);
    }
  }
}
