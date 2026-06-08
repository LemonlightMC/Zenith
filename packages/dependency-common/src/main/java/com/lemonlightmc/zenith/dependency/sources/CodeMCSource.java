package com.lemonlightmc.zenith.dependency.sources;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
 * Best-effort CodeMC integration.
 *
 * Attempts to use the CodeMC public API endpoints where available and falls
 * back to GitHub/Spiget strategies when applicable.
 */
public final class CodeMCSource implements DependencySource {

  private static final String API_V2 = "https://api.codemc.io/v2/projects/";
  private static final String API_V1 = "https://api.codemc.io/v1/projects/";

  @Override
  public String type() {
    return "codemc";
  }

  @Override
  public List<Version> fetchVersions(Dependency dependency) throws DependencyException {
    String id = dependency.identifier();
    try {
      // Try v2 endpoint which often returns a JSON array of versions or objects
      String url = API_V2 + URLEncoder.encode(id, StandardCharsets.UTF_8);
      String resp = HttpUtil.get(url);
      JsonElement root = JsonUtil.fromJson(resp);
      List<Version> versions = new ArrayList<>();
      if (root != null && root.isJsonArray()) {
        JsonArray arr = JsonUtil.toJsonArray(root);
        for (JsonElement el : arr) {
          String ver = el.isJsonPrimitive() ? el.getAsString() : JsonUtil.getString(JsonUtil.toJsonObject(el), "name");
          if (ver == null)
            continue;
          Version v = Version.trySemver(ver);
          if (v != null)
            versions.add(v);
        }
      }

      if (!versions.isEmpty())
        return versions;

      // Fallback: try v1 endpoint
      url = API_V1 + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/versions";
      resp = HttpUtil.get(url);
      JsonArray arr = JsonUtil.toJsonArray(JsonUtil.fromJson(resp));
      if (arr != null) {
        for (JsonElement el : arr) {
          String ver = el.isJsonPrimitive() ? el.getAsString()
              : JsonUtil.getString(JsonUtil.toJsonObject(el), "name");
          if (ver == null)
            continue;
          Version v = Version.trySemver(ver);
          if (v != null)
            versions.add(v);
        }
      }

      if (versions.isEmpty()) {
        throw new DependencyException(id, "No parseable versions found on CodeMC");
      }
      return versions;
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      // As a last resort, delegate to GitHub if identifier looks like owner/repo
      if (id.contains("/")) {
        try {
          GitHubSource gh = new GitHubSource();
          return gh.fetchVersions(dependency);
        } catch (Exception ignored) {
        }
      }
      throw new DependencyException(id, "Failed to fetch versions from CodeMC: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(Dependency dependency, Version version) throws DependencyException {
    String id = dependency.identifier();
    try {
      // Try v2 asset endpoint
      String url = API_V2 + URLEncoder.encode(id, StandardCharsets.UTF_8) + "/"
          + URLEncoder.encode(version.toString(), StandardCharsets.UTF_8);
      String resp = HttpUtil.get(url);
      JsonObject obj = JsonUtil.toJsonObject(JsonUtil.fromJson(resp));
      if (obj != null) {
        String download = JsonUtil.getString(obj, "downloadUrl");
        String fileName = JsonUtil.getString(obj, "fileName");
        if (download != null) {
          return new ResolvedDependency(dependency.name(), version, download,
              dependency.fileName() != null ? dependency.fileName()
                  : (fileName != null ? fileName : id + "-" + version + ".jar"));
        }
      }

      // Fallback: delegate to GitHub if looks like owner/repo
      if (id.contains("/")) {
        GitHubSource gh = new GitHubSource();
        return gh.resolve(dependency, version);
      }

      throw new DependencyException(id, "Failed to resolve CodeMC resource for version " + version);
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(id, "Failed to resolve CodeMC resource: " + e.getMessage(), e);
    }
  }
}
