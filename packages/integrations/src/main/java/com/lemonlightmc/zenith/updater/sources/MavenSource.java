package com.lemonlightmc.zenith.updater.sources;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
 * Dependency source for Maven Central.
 */
public final class MavenSource implements DependencySource {

  private static final String SEARCH_API = "https://search.maven.org/solrsearch/select";

  @Override
  public String type() {
    return "maven";
  }

  @Override
  public List<Version> fetchVersions(Dependency dependency) throws DependencyException {
    String id = dependency.identifier();
    // Expect group:artifact
    String[] parts = id.split(":", 2);
    if (parts.length < 2) {
      throw new DependencyException(id, "Maven identifier must be in 'group:artifact' format");
    }
    String group = parts[0];
    String artifact = parts[1];

    try {
      String q = "g:\"" + group + "\" AND a:\"" + artifact + "\"";
      String url = SEARCH_API + "?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8) + "&rows=200&core=gav&wt=json";
      String resp = HttpUtil.get(url);
      JsonObject root = JsonUtil.toJsonObject(JsonUtil.fromJson(resp));
      if (root == null) {
        throw new DependencyException(id, "Failed to parse Maven search response");
      }
      JsonObject response = JsonUtil.getJsonObject(root, "response");
      JsonArray docs = JsonUtil.getJsonArray(response, "docs");
      if (docs == null || docs.size() == 0) {
        throw new DependencyException(id, "No versions found on Maven Central");
      }

      List<Version> versions = new ArrayList<>();
      for (JsonElement el : docs) {
        JsonObject doc = JsonUtil.toJsonObject(el);
        String v = JsonUtil.getString(doc, "v");
        if (v == null)
          continue;
        Version ver = Version.trySemver(v);
        if (ver != null) {
          versions.add(ver);
        }
      }
      return versions;
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(id, "Failed to fetch versions from Maven Central: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(Dependency dependency, Version version) throws DependencyException {
    String id = dependency.identifier();
    String[] parts = id.split(":", 2);
    if (parts.length < 2) {
      throw new DependencyException(id, "Maven identifier must be in 'group:artifact' format");
    }
    String group = parts[0];
    String artifact = parts[1];

    try {
      String groupPath = group.replace('.', '/');
      String fileName = dependency.fileName() != null ? dependency.fileName()
          : artifact + "-" + version.toString() + ".jar";
      String downloadUrl = "https://repo1.maven.org/maven2/" + groupPath + "/" + artifact + "/"
          + version.toString() + "/" + fileName;

      return new ResolvedDependency(dependency.name(), version, downloadUrl, fileName);
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(id, "Failed to resolve Maven artifact: " + e.getMessage(), e);
    }
  }
}
