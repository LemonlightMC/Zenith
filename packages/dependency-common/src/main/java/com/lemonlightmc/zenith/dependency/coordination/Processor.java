package com.lemonlightmc.zenith.dependency.coordination;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencySource;
import com.lemonlightmc.zenith.dependency.DownloadResult;
import com.lemonlightmc.zenith.dependency.FailurePolicy;
import com.lemonlightmc.zenith.dependency.PluginYamlReader;
import com.lemonlightmc.zenith.dependency.sources.SourceType;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.messages.Logger;
import com.lemonlightmc.zenith.utils.Checksum;
import com.lemonlightmc.zenith.utils.HttpUtil;
import com.lemonlightmc.zenith.version.Version;
import com.lemonlightmc.zenith.version.VersionConstraint;

public class Processor {
  private final Path pluginsFolder;
  private final Path coordinationDir;
  private final Map<SourceType, DependencySource> sources;
  private final Map<String, Boolean> downloadedPlugins;

  public Processor(Path pluginsFolder) {
    this.pluginsFolder = pluginsFolder.resolve(".hopper");
    this.coordinationDir = pluginsFolder.resolve("/zenith");
    sources = new EnumMap<>(SourceType.class);
    downloadedPlugins = new ConcurrentHashMap<>();
  }

  public boolean isReady(String pluginName) {
    return downloadedPlugins.containsKey(pluginName);
  }

  /**
   * Download dependencies with coordination.
   */
  public DownloadResult downloadDependencies(String pluginName, List<Dependency> dependencies) {
    DownloadResult.Builder result = DownloadResult.builder();

    try {
      // Use file-based coordination
      try (HopperCoordinator coordinator = HopperCoordinator.acquire(coordinationDir)) {
        // Load or create registry
        LibraryRegistry registry = coordinator.loadRegistry();

        // Register this plugin's dependencies
        registry.registerPlugin(pluginName, dependencies);

        // Load lockfile
        Lockfile lockfile = coordinator.loadLockfile();

        // Process each dependency
        for (Dependency dep : dependencies) {
          try {
            processDependency(dep, registry, lockfile, result);
          } catch (DependencyException e) {
            handleFailure(dep, e.getMessage(), result);
          } catch (Exception e) {
            handleFailure(dep, e.getMessage(), result);
          }
        }

        // Save updated registry and lockfile
        coordinator.saveRegistry(registry);
        coordinator.saveLockfile(lockfile);
      }

      // Mark plugin as ready if successful
      if (result.isCurrentlySuccessful()) {
        downloadedPlugins.put(pluginName, true);
      }
    } catch (Exception e) {
      Logger.warn("Failed to process dependencies: " + e.getMessage());
    }
    return result.build();
  }

  private void processDependency(Dependency dep, LibraryRegistry registry, Lockfile lockfile,
      DownloadResult.Builder result) {
    String depName = dep.name();
    Logger.info("Processing dependency: " + depName);

    // Get merged constraint from registry (combines all plugins' constraints)
    VersionConstraint mergedConstraint = registry.getMergedConstraint(depName);
    if (mergedConstraint == null) {
      mergedConstraint = dep.constraint();
    }

    // Check lockfile first
    Lockfile.Entry lockedEntry = lockfile.getEntry(depName);
    if (lockedEntry != null) {
      // Check if locked version still satisfies constraint
      Version lockedVersion = Version.trySemver(lockedEntry.resolvedVersion());
      if (lockedVersion != null && mergedConstraint.isSatisfiedBy(lockedVersion)) {
        // Check if file exists
        Path lockedPath = pluginsFolder.resolve(lockedEntry.fileName());
        if (Files.exists(lockedPath)) {
          Logger.info("  Using locked version: " + lockedVersion);
          result.addExisting(depName, lockedVersion, lockedPath);
          return;
        }
      }
    }

    // Respect any jar the user (or another plugin) has already installed under
    // a different filename. Exclude the lockfile-tracked jar so Hopper's own
    // stale download doesn't block a legitimate upgrade to a new constraint.
    List<PluginYamlReader.Match> installed = PluginYamlReader.findAll(pluginsFolder, depName);
    if (lockedEntry != null) {
      String lockedFile = lockedEntry.fileName();
      installed.removeIf(m -> m.path().getFileName().toString().equals(lockedFile));
    }
    if (!installed.isEmpty()) {
      PluginYamlReader.Match chosen = selectHighestVersion(installed);
      String detectedVersion = chosen.descriptor().version();
      Version installedVersion = detectedVersion != null ? Version.trySemver(detectedVersion) : null;
      if (installedVersion == null) {
        installedVersion = Version.trySemver("0.0.0");
      }
      Logger.info("[Hopper] " + depName + " already installed as "
          + chosen.path().getFileName()
          + (detectedVersion != null ? " v" + detectedVersion : "")
          + " - skipping download");
      result.addExisting(depName, installedVersion, chosen.path());
      return;
    }

    // Need to resolve version
    DependencySource source = retrieveSource(dep.sourceType(), depName);

    // Fetch available versions
    Logger.info("  Fetching versions from " + dep.sourceType() + "...");
    List<Version> versions = source.fetchVersions(dep);
    if (versions.isEmpty()) {
      throw new DependencyException(depName, "No versions found");
    }

    // Select best version
    Version selected = mergedConstraint.selectBest(versions);
    if (selected == null) {
      // Constraint couldn't be satisfied
      if (dep.failurePolicy() == FailurePolicy.WARN_USE_LATEST) {
        Logger.warn("  Constraint not satisfied, using latest: " + versions.get(0));
        selected = versions.get(0);
      } else {
        throw new DependencyException(depName,
            "No version satisfies constraint: " + mergedConstraint);
      }
    }

    Logger.info("  Selected version: " + selected);

    // Check if already downloaded
    DependencySource.ResolvedDependency resolved = source.resolve(dep, selected);
    Path targetPath = pluginsFolder.resolve(resolved.fileName());

    if (Files.exists(targetPath)) {
      // Verify checksum if provided
      if (resolved.checksum() != null && resolved.checksumType() != null) {
        if (Checksum.verify(targetPath, resolved.checksum(), resolved.checksumType())) {
          Logger.info("  Already downloaded with matching checksum");
          result.addExisting(depName, selected, targetPath);
          lockfile.updateEntry(depName, resolved);
          return;
        } else {
          Logger.info("  Checksum mismatch, re-downloading");
          try {
            Files.deleteIfExists(targetPath);
          } catch (IOException e) {
            throw new DependencyException(depName, "Failed to delete file: " + e.getMessage(), e);
          }
        }
      } else {
        Logger.info("  Already downloaded");
        result.addExisting(depName, selected, targetPath);
        lockfile.updateEntry(depName, resolved);
        return;
      }
    }

    // Download
    Logger.info("[Hopper] Downloading " + depName + " " + selected + "...");
    Logger.info("  Downloading from: " + resolved.downloadUrl());
    HttpUtil.download(resolved.downloadUrl(), targetPath);

    // Verify checksum
    if (resolved.checksum() != null && resolved.checksumType() != null) {
      if (!Checksum.verify(targetPath, resolved.checksum(), resolved.checksumType())) {
        try {
          Files.deleteIfExists(targetPath);
        } catch (IOException ignored) {
          // Best effort cleanup
        }
        throw new DependencyException(depName, "Checksum verification failed (" +
            resolved.checksumType().algorithm() + ")");
      }
    }

    Logger.info("  Downloaded successfully: " + resolved.fileName());
    result.addDownloaded(depName, selected, targetPath);
    lockfile.updateEntry(depName, resolved);
  }

  /**
   * Deterministically pick the match with the highest parsable version; if
   * none of the matches have parsable versions, fall back to the jar whose
   * filename sorts first. Keeps behavior stable across restarts when admins
   * leave multiple jars with the same plugin name in place.
   */
  private static PluginYamlReader.Match selectHighestVersion(
      List<PluginYamlReader.Match> matches) {
    PluginYamlReader.Match bestWithVersion = null;
    Version bestVersion = null;
    PluginYamlReader.Match fallback = null;
    for (PluginYamlReader.Match m : matches) {
      if (fallback == null || m.path().getFileName().toString()
          .compareTo(fallback.path().getFileName().toString()) < 0) {
        fallback = m;
      }
      String raw = m.descriptor().version();
      if (raw == null)
        continue;
      Version v = Version.trySemver(raw);
      if (v == null)
        continue;
      if (bestVersion == null || v.compareTo(bestVersion) > 0) {
        bestVersion = v;
        bestWithVersion = m;
      }
    }
    return bestWithVersion != null ? bestWithVersion : fallback;
  }

  private void handleFailure(Dependency dep, String error, DownloadResult.Builder result) {
    String depName = dep.name();
    FailurePolicy policy = dep.failurePolicy();

    switch (policy) {
      case FAIL:
        result.addFailed(depName, error, policy);
        // Errors are always logged (they're important regardless of verbosity)
        Logger.error("[Hopper] " + depName + " FAILED: " + error);
        break;
      case WARN_USE_LATEST:
        result.addFailed(depName, error, policy);
        Logger.warn("[Hopper] " + depName + " WARNING: " + error);
        break;
      case WARN_SKIP:
        result.addSkipped(depName, error);
        Logger.warn("[Hopper] " + depName + " SKIPPED: " + error);
        break;
    }
  }

  private DependencySource retrieveSource(SourceType type, String depName) {
    DependencySource source = sources.get(type);
    if (source == null) {
      source = (DependencySource) type.create();
    }
    if (source == null) {
      throw new DependencyException(depName, "Unknown source type: " + type);
    }
    return source;
  }
}
