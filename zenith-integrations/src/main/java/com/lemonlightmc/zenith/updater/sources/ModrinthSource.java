package com.lemonlightmc.zenith.updater.sources;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.updater.Checksum.ChecksumType;
import com.lemonlightmc.zenith.updater.Dependency;
import com.lemonlightmc.zenith.updater.DependencySource;
import com.lemonlightmc.zenith.utils.HttpUtil;
import com.lemonlightmc.zenith.utils.JsonUtil;
import com.lemonlightmc.zenith.version.ServerPlatform;
import com.lemonlightmc.zenith.version.Version;

/**
 * Dependency source for Modrinth.
 * <p>
 * API: https://api.modrinth.com/v2
 */
public final class ModrinthSource implements DependencySource {

  private static final String API_BASE = "https://api.modrinth.com/v2";

  @Override
  public String type() {
    return "modrinth";
  }

  @Override
  public List<Version> fetchVersions(Dependency dependency) throws DependencyException {
    String slug = dependency.identifier();
    ServerPlatform platform = dependency.platform();

    try {
      // Build URL with query parameters
      StringBuilder url = new StringBuilder(API_BASE)
          .append("/project/").append(slug).append("/version");

      // Add loader filter based on platform
      String[] loaders = platform.modrinthLoaders();
      StringBuilder loadersJson = new StringBuilder("[");
      for (int i = 0; i < loaders.length; i++) {
        if (i > 0)
          loadersJson.append(",");
        loadersJson.append("\"").append(loaders[i]).append("\"");
      }
      loadersJson.append("]");
      url.append("?loaders=").append(URLEncoder.encode(loadersJson.toString(), StandardCharsets.UTF_8));

      url.append("&game_versions=")
          .append(URLEncoder.encode("[\"" + dependency.effectiveMinecraftVersion().toString() + "\"]",
              StandardCharsets.UTF_8));

      JsonArray versions = JsonUtil.toJsonArray(JsonUtil.fromJson((HttpUtil.get(url.toString()))));
      if (versions == null || versions.size() == 0) {
        throw new DependencyException(slug, "No versions found on Modrinth");
      }

      List<Version> result = new ArrayList<>();
      for (JsonElement item : versions) {
        String versionNumber = JsonUtil.getString(JsonUtil.toJsonObject(item), "version_number");
        if (versionNumber == null) {
          continue;
        }
        Version v = Version.semver(versionNumber);
        if (v != null) {
          result.add(v);
        }
      }
      return result;
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(slug, "Failed to fetch versions from Modrinth: " + e.getMessage(), e);
    }
  }

  @Override
  public ResolvedDependency resolve(Dependency dependency, Version version)
      throws DependencyException {
    String slug = dependency.identifier();
    ServerPlatform platform = dependency.platform();

    try {
      // Fetch all versions and find the matching one
      String url = API_BASE + "/project/" + slug + "/version";
      JsonArray versions = JsonUtil.toJsonArray(JsonUtil.fromJson(HttpUtil.get(url)));
      if (versions == null) {
        throw new DependencyException(slug, "Failed to parse Modrinth response");
      }

      // Find matching version
      JsonObject matchingVersion = null;
      for (JsonElement item : versions) {
        JsonObject versionData = JsonUtil.toJsonObject(item);
        String versionNumber = JsonUtil.getString(versionData, "version_number");
        if (versionNumber != null && versionNumber.equals(version.toString())) {
          matchingVersion = versionData;
          break;
        }
      }
      if (matchingVersion == null) {
        throw new DependencyException(slug, "Version not found on Modrinth: " + version);
      }

      // Get files list
      JsonArray files = JsonUtil.getJsonArray(matchingVersion, "files");
      if (files == null || files.size() == 0) {
        throw new DependencyException(slug, "No files found for version " + version);
      }

      // Select the best file based on platform
      JsonObject selectedFile = selectFileForPlatform(files, platform);
      String downloadUrl = JsonUtil.getString(selectedFile, "url");
      String fileName = JsonUtil.getString(selectedFile, "filename");
      JsonObject hashes = JsonUtil.getJsonObject(selectedFile, "hashes");
      String sha512 = hashes != null ? JsonUtil.getString(hashes, "sha512") : null;

      if (downloadUrl == null || fileName == null) {
        throw new DependencyException(slug, "Invalid file info for version " + version);
      }

      return new ResolvedDependency(
          dependency.name(),
          version,
          downloadUrl,
          sha512,
          sha512 != null ? ChecksumType.SHA512 : null,
          dependency.fileName() != null ? dependency.fileName() : fileName);
    } catch (DependencyException e) {
      throw e;
    } catch (Exception e) {
      throw new DependencyException(slug, "Failed to resolve Modrinth version: " + e.getMessage(), e);
    }
  }

  /**
   * Select the best file from a list based on the target platform.
   * <p>
   * For multi-file versions (e.g., CommandAPI with Paper and Spigot variants),
   * this selects the file matching the platform. Falls back to primary file
   * or first file if no platform-specific match is found.
   *
   * @param files    list of file objects from Modrinth API
   * @param platform the target platform
   * @return the best matching file
   */
  private static JsonObject selectFileForPlatform(JsonArray files, ServerPlatform platform) {

    if (files.size() == 1) {
      return JsonUtil.toJsonObject(files.get(0));
    }

    // Platform-specific filename patterns (case-insensitive)
    String[] preferredPatterns = getPlatformFilePatterns(platform);

    // First pass: look for platform-specific file
    for (String pattern : preferredPatterns) {
      for (JsonElement fileEl : files) {
        JsonObject file = JsonUtil.toJsonObject(fileEl);
        String filename = JsonUtil.getString(file, "filename");
        if (filename != null && filename.toLowerCase().contains(pattern.toLowerCase())) {
          return file;
        }
      }
    }

    // Second pass: avoid files for incompatible platforms
    String[] avoidPatterns = getIncompatibleFilePatterns(platform);
    for (JsonElement fileEl : files) {
      JsonObject file = JsonUtil.toJsonObject(fileEl);
      String filename = JsonUtil.getString(file, "filename");
      if (filename == null) {
        continue;
      }

      boolean shouldAvoid = false;
      for (String pattern : avoidPatterns) {
        if (filename.toLowerCase().contains(pattern.toLowerCase())) {
          shouldAvoid = true;
          break;
        }
      }
      if (!shouldAvoid) {
        return file;
      }
    }

    // Fall back to primary file or first file
    for (JsonElement fileEl : files) {
      JsonObject file = JsonUtil.toJsonObject(fileEl);
      Boolean primary = JsonUtil.getBool(file, "primary");
      if (Boolean.TRUE.equals(primary)) {
        return file;
      }
    }
    return JsonUtil.toJsonObject(files.get(0));
  }

  /**
   * Get filename patterns to prefer for this platform.
   */
  private static String[] getPlatformFilePatterns(ServerPlatform platform) {
    return switch (platform) {
      case FOLIA -> new String[] { "folia", "paper" };
      case PURPUR -> new String[] { "purpur", "paper" };
      case PAPER -> new String[] { "paper" };
      case SPIGOT -> new String[] { "spigot" };
      case BUKKIT -> new String[] { "bukkit", "spigot" };
      case VELOCITY -> new String[] { "velocity" };
      case BUNGEECORD -> new String[] { "bungeecord", "bungee" };
      case WATERFALL -> new String[] { "waterfall", "bungeecord", "bungee" };
      case SPONGE -> new String[] { "sponge" };
      case NUKKIT -> new String[] { "nukkit" };
      case FABRIC -> new String[] { "fabric" };
      case NEOFORGE -> new String[] { "neoforge" };
      case FORGE -> new String[] { "forge" };
      case MOCK_BUKKIT -> new String[] { "mockbukkit", "bukkit", "spigot" };
      case STANDALONE -> new String[] {};
    };
  }

  /**
   * Get filename patterns to avoid for this platform.
   */
  private static String[] getIncompatibleFilePatterns(ServerPlatform platform) {
    return switch (platform) {
      case SPIGOT, BUKKIT -> new String[] { "paper", "folia", "purpur", "velocity", "bungee" };
      case PAPER -> new String[] { "folia", "velocity", "bungee" };
      case FOLIA -> new String[] { "velocity", "bungee" };
      case PURPUR -> new String[] { "folia", "velocity", "bungee" };
      case VELOCITY -> new String[] { "paper", "spigot", "bukkit", "folia", "bungee" };
      case BUNGEECORD, WATERFALL -> new String[] { "paper", "spigot", "bukkit", "folia", "velocity" };
      case SPONGE -> new String[] { "paper", "spigot", "bukkit", "folia", "velocity", "bungee" };
      case NUKKIT -> new String[] { "paper", "spigot", "bukkit", "folia", "velocity", "bungee" };
      case FABRIC -> new String[] { "paper", "spigot", "bukkit", "folia", "velocity", "bungee" };
      case NEOFORGE -> new String[] { "paper", "spigot", "bukkit", "folia", "velocity", "bungee" };
      case FORGE -> new String[] { "paper", "spigot", "bukkit", "folia", "velocity", "bungee" };
      case STANDALONE -> new String[] {};
      case MOCK_BUKKIT -> new String[] { "paper", "folia", "velocity", "bungee", "fabric", "neoforge", "forge" };
    };
  }
}