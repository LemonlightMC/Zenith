package com.lemonlightmc.zenith.dependency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;

import com.lemonlightmc.zenith.dependency.coordination.HopperCoordinator;
import com.lemonlightmc.zenith.dependency.coordination.LibraryRegistry;
import com.lemonlightmc.zenith.dependency.coordination.Lockfile;
import com.lemonlightmc.zenith.dependency.sources.SourceType;
import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.utils.Checksum;
import com.lemonlightmc.zenith.utils.HttpUtil;
import com.lemonlightmc.zenith.version.Version;
import com.lemonlightmc.zenith.version.VersionConstraint;

public abstract class DependencyHandler<K> {

  // Tracks which plugins have had their dependencies processed successfully.
  private final Map<String, Boolean> readyPlugins = new ConcurrentHashMap<>();
  private final Map<String, PluginRegistration> registrations = new ConcurrentHashMap<>();
  private final Map<SourceType, DependencySource> sources;

  private final Path coordinationDir;
  private final Path pluginsDir;
  protected final Logger logger;

  private record PluginRegistration(String name, Dependency[] dependencies) {
  }

  protected DependencyHandler(final Logger logger, final Path coordinationDir, final Path pluginsDir) {
    this.logger = logger;
    this.coordinationDir = coordinationDir;
    this.pluginsDir = pluginsDir;
    sources = new EnumMap<>(SourceType.class);
  }

  public Path getCoordinationFolder() {
    return coordinationDir;
  }

  public Path getPluginsFolder() {
    return pluginsDir;
  }

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   *
   * @param key       the consumer name
   * @param collector consumer that registers dependencies
   */
  public void register(final String key, final Consumer<DependencyCollector> collector) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("Plugin name cannot be null or empty");
    }
    if (collector == null) {
      throw new IllegalArgumentException("Collector cannot be null");
    }

    final DependencyCollector deps = new DependencyCollector();
    collector.accept(deps);

    registrations.put(key, new PluginRegistration(key, deps.getDependencies().toArray(Dependency[]::new)));
  }

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   *
   * @param plugin    the plugin instance
   * @param collector consumer that registers dependencies
   */
  public abstract void register(K key, Consumer<DependencyCollector> collector);

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   * 
   * @param key          the consumer name
   * @param dependencies the dependencies to register
   */
  public void register(final String key, final List<Dependency> dependencies) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("Plugin name cannot be null or empty");
    }
    if (dependencies == null || dependencies.isEmpty()) {
      throw new IllegalArgumentException("Dependencies cannot be null or empty");
    }

    registrations.put(key, new PluginRegistration(key, dependencies.toArray(Dependency[]::new)));
  }

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   * 
   * @param plugin       the plugin instance
   * @param dependencies the dependencies to register
   */
  public abstract void register(K key, List<Dependency> dependencies);

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   * 
   * @param key          the consumer name
   * @param dependencies the dependencies to register
   */
  public void register(final String key, final Dependency... dependencies) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("Plugin name cannot be null or empty");
    }
    if (dependencies == null || dependencies.length == 0) {
      throw new IllegalArgumentException("Dependencies cannot be null or empty");
    }

    registrations.put(key, new PluginRegistration(key, dependencies));
  }

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   * 
   * @param plugin       the plugin instance
   * @param dependencies the dependencies to register
   */
  public abstract void register(K key, Dependency... dependencies);

  /**
   * Download all registered dependencies for a plugin.
   * Call this in your plugin's onLoad() method.
   *
   * @param plugin the plugin instance
   * @return download result
   */
  public abstract DownloadResult download(K key);

  /**
   * Check if all dependencies for a plugin are ready (downloaded and loaded).
   *
   * @param key the consumer name
   * @return true if all dependencies are ready
   */
  public boolean isReady(final String key) {
    return readyPlugins.containsKey(key);
  }

  /**
   * Check if all dependencies for a plugin are ready (downloaded and loaded).
   *
   * @param plugin the plugin instance
   * @return true if all dependencies are ready
   */
  public abstract boolean isReady(final K key);

  /**
   * Download all registered dependencies for a plugin.
   * Call this in your plugin's onLoad() method.
   *
   * @param key the consumer name
   * @return download result
   */
  public DownloadResult download(final String key) {
    final PluginRegistration registration = registrations.get(key);
    if (registration == null) {
      // No dependencies registered - this can happen if:
      // 1. register() was never called
      // 2. Plugin name doesn't match (typo, different casing)
      // 3. Different classloader (shouldn't happen within same plugin)
      logger.warn("No dependencies registered for plugin: " + key);
      if (!registrations.isEmpty()) {
        logger.warn("Registered plugins: " + registrations.keySet());
      }
      return DownloadResult.empty();
    }

    logger.info("Processing " + registration.dependencies().length + " dependency(ies) for " + key);

    final DownloadResult.Builder result = DownloadResult.builder();

    // Use file-based coordination
    try (HopperCoordinator coordinator = HopperCoordinator.acquire(coordinationDir)) {
      // Load or create registry
      final LibraryRegistry registry = coordinator.loadRegistry();

      // Register this plugin's dependencies
      registry.registerPlugin(key, registration.dependencies());

      // Load lockfile
      final Lockfile lockfile = coordinator.loadLockfile();

      // Process each dependency
      for (final Dependency dep : registration.dependencies()) {
        try {
          processDependency(dep, registry, lockfile, result);
        } catch (final DependencyException e) {
          handleFailure(dep, e.getMessage(), result);
        } catch (final Exception e) {
          handleFailure(dep, e.getMessage(), result);
        }
      }

      // Save updated registry and lockfile
      coordinator.saveRegistry(registry);
      coordinator.saveLockfile(lockfile);

      // Mark plugin as ready if successful
      if (result.isCurrentlySuccessful()) {
        readyPlugins.put(key, true);
      }
    } catch (final IOException e) {
      logger.warn("Failed to process dependencies (Aquiring Coordinator): " + e.getMessage());
    } catch (final Exception e) {
      logger.warn("Failed to process dependencies: " + e.getMessage());
    }
    return result.build();
  }

  private void processDependency(final Dependency dep, final LibraryRegistry registry, final Lockfile lockfile,
      final DownloadResult.Builder result) {
    final String depName = dep.name();
    logger.info("Processing dependency: " + depName);

    // Get merged constraint from registry (combines all plugins' constraints)
    VersionConstraint mergedConstraint = registry.getMergedConstraint(depName);
    if (mergedConstraint == null) {
      mergedConstraint = dep.constraint();
    }

    // Check lockfile first
    final Lockfile.Entry lockedEntry = lockfile.getEntry(depName);
    if (lockedEntry != null) {
      // Check if locked version still satisfies constraint
      final Version lockedVersion = Version.trySemver(lockedEntry.resolvedVersion());
      if (lockedVersion != null && mergedConstraint.isSatisfiedBy(lockedVersion)) {
        // Check if file exists
        final Path lockedPath = pluginsDir.resolve(lockedEntry.fileName());
        if (Files.exists(lockedPath)) {
          logger.info("  Using locked version: " + lockedVersion);
          result.addExisting(depName, lockedVersion, lockedPath);
          return;
        }
      }
    }

    // Respect any jar the user (or another plugin) has already installed under
    // a different filename. Exclude the lockfile-tracked jar so DependencyAPI's own
    // stale download doesn't block a legitimate upgrade to a new constraint.
    final List<PluginYamlReader.Match> installed = PluginYamlReader.findAll(pluginsDir, depName);
    if (lockedEntry != null) {
      final String lockedFile = lockedEntry.fileName();
      installed.removeIf(m -> m.path().getFileName().toString().equals(lockedFile));
    }
    if (!installed.isEmpty()) {
      final PluginYamlReader.Match chosen = selectHighestVersion(installed);
      final String detectedVersion = chosen.descriptor().version();
      Version installedVersion = detectedVersion != null ? Version.trySemver(detectedVersion) : null;
      if (installedVersion == null) {
        installedVersion = Version.trySemver("0.0.0");
      }
      logger.info(depName + " already installed as "
          + chosen.path().getFileName()
          + (detectedVersion != null ? " v" + detectedVersion : "")
          + " - skipping download");
      result.addExisting(depName, installedVersion, chosen.path());
      return;
    }

    // Need to resolve version
    final DependencySource source = retrieveSource(dep.sourceType(), depName);

    // Fetch available versions
    logger.info("  Fetching versions from " + dep.sourceType() + "...");
    final List<Version> versions = source.fetchVersions(dep);
    if (versions.isEmpty()) {
      throw new DependencyException(depName, "No versions found");
    }

    // Select best version
    Version selected = mergedConstraint.selectBest(versions);
    if (selected == null) {
      // Constraint couldn't be satisfied
      if (dep.failurePolicy() == FailurePolicy.WARN_USE_LATEST) {
        logger.warn("  Constraint not satisfied, using latest: " + versions.get(0));
        selected = versions.get(0);
      } else {
        throw new DependencyException(depName,
            "No version satisfies constraint: " + mergedConstraint);
      }
    }

    logger.info("  Selected version: " + selected);

    // Check if already downloaded
    final DependencySource.ResolvedDependency resolved = source.resolve(dep, selected);
    final Path targetPath = pluginsDir.resolve(resolved.fileName());

    if (Files.exists(targetPath)) {
      // Verify checksum if provided
      if (resolved.checksum() != null && resolved.checksumType() != null) {
        if (Checksum.verify(targetPath, resolved.checksum(), resolved.checksumType())) {
          logger.info("  Already downloaded with matching checksum");
          result.addExisting(depName, selected, targetPath);
          lockfile.updateEntry(depName, resolved);
          return;
        } else {
          logger.info("  Checksum mismatch, re-downloading");
          try {
            Files.deleteIfExists(targetPath);
          } catch (final IOException e) {
            throw new DependencyException(depName, "Failed to delete file: " + e.getMessage(), e);
          }
        }
      } else {
        logger.info("  Already downloaded");
        result.addExisting(depName, selected, targetPath);
        lockfile.updateEntry(depName, resolved);
        return;
      }
    }

    // Download
    logger.info("[DependencyAPI] Downloading " + depName + " " + selected + "...");
    logger.info("  Downloading from: " + resolved.downloadUrl());
    HttpUtil.download(resolved.downloadUrl(), targetPath);

    // Verify checksum
    if (resolved.checksum() != null && resolved.checksumType() != null) {
      if (!Checksum.verify(targetPath, resolved.checksum(), resolved.checksumType())) {
        try {
          Files.deleteIfExists(targetPath);
        } catch (final IOException ignored) {
          // Best effort cleanup
        }
        throw new DependencyException(depName, "Checksum verification failed (" +
            resolved.checksumType().algorithm() + ")");
      }
    }

    logger.info("  Downloaded successfully: " + resolved.fileName());
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
      final List<PluginYamlReader.Match> matches) {
    PluginYamlReader.Match bestWithVersion = null;
    Version bestVersion = null;
    PluginYamlReader.Match fallback = null;
    for (final PluginYamlReader.Match m : matches) {
      if (fallback == null || m.path().getFileName().toString()
          .compareTo(fallback.path().getFileName().toString()) < 0) {
        fallback = m;
      }
      final String raw = m.descriptor().version();
      if (raw == null)
        continue;
      final Version v = Version.trySemver(raw);
      if (v == null)
        continue;
      if (bestVersion == null || v.compareTo(bestVersion) > 0) {
        bestVersion = v;
        bestWithVersion = m;
      }
    }
    return bestWithVersion != null ? bestWithVersion : fallback;
  }

  private void handleFailure(final Dependency dep, final String error, final DownloadResult.Builder result) {
    final String depName = dep.name();
    final FailurePolicy policy = dep.failurePolicy();

    switch (policy) {
      case FAIL:
        result.addFailed(depName, error, policy);
        // Errors are always logged (they're important regardless of verbosity)
        logger.error("[DependencyAPI] " + depName + " FAILED: " + error);
        break;
      case WARN_USE_LATEST:
        result.addFailed(depName, error, policy);
        logger.warn("[DependencyAPI] " + depName + " WARNING: " + error);
        break;
      case WARN_SKIP:
        result.addSkipped(depName, error);
        logger.warn("[DependencyAPI] " + depName + " SKIPPED: " + error);
        break;
    }
  }

  private DependencySource retrieveSource(final SourceType type, final String depName) {
    DependencySource source = sources.get(type);
    if (source != null) {
      return source;
    }
    source = (DependencySource) type.create();
    if (source == null) {
      throw new DependencyException(depName, "Unknown source type: " + type);
    }
    sources.put(type, source);
    return source;
  }
}
