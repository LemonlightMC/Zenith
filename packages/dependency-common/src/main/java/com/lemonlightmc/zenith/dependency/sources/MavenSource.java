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
 * Dependency source for Maven Central.
 */
public final class MavenSource implements DependencySource {

  private static final String SEARCH_API = "https://search.maven.org/solrsearch/select";

  @Override
  public String type() {
    return "maven";
  }

  @Override
  public List<Version> fetchVersions(final Dependency dependency) throws DependencyException {
    final String id = dependency.identifier();
    // Expect group:artifact
    final String[] parts = id.split(":", 2);
    if (parts.length < 2) {
      throw new DependencyException(id, "Maven identifier must be in 'group:artifact' format");
    }
    final String group = parts[0];
    final String artifact = parts[1];

    try {
      final String q = "g:\"" + group + "\" AND a:\"" + artifact + "\"";
      final String url = SEARCH_API + "?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8) + "&rows=200&core=gav&wt=json";
      final String resp = HttpUtil.get(url);
      final JsonObject root = JsonUtil.toJsonObject(JsonUtil.fromJson(resp));
      if (root == null) {
        throw new DependencyException(id, "Failed to parse Maven search response");
      }
      final JsonObject response = JsonUtil.getJsonObject(root, "response");
      final JsonArray docs = JsonUtil.getJsonArray(response, "docs");
      if (docs == null || docs.size() == 0) {
        throw new DependencyException(id, "No versions found on Maven Central");
      }

      final List<Version> versions = new ArrayList<>();
      for (final JsonElement el : docs) {
        final JsonObject doc = JsonUtil.toJsonObject(el);
        final String v = JsonUtil.getString(doc, "v");
        if (v == null)
          continue;
        final Version ver = Version.trySemver(v);
        if (ver != null) {
          versions.add(ver);
        }
      }
      return versions;
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(id, "Failed to fetch versions from Maven Central: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(final Dependency dependency, final Version version) throws DependencyException {
    final String id = dependency.identifier();
    final String[] parts = id.split(":", 2);
    if (parts.length < 2) {
      throw new DependencyException(id, "Maven identifier must be in 'group:artifact' format");
    }
    final String group = parts[0];
    final String artifact = parts[1];

    try {
      final String groupPath = group.replace('.', '/');
      final String fileName = dependency.fileName() != null ? dependency.fileName()
          : artifact + "-" + version.toString() + ".jar";
      final String downloadUrl = "https://repo1.maven.org/maven2/" + groupPath + "/" + artifact + "/"
          + version.toString() + "/" + fileName;

      return new ResolvedDependency(dependency.name(), version, downloadUrl, fileName);
    } catch (final DependencyException e) {
      throw e;
    } catch (final Exception e) {
      throw new DependencyException(id, "Failed to resolve Maven artifact: " + e.getMessage(), e);
    }
  }
}
