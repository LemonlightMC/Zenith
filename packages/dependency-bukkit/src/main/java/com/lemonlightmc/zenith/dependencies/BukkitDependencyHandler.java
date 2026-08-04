package com.lemonlightmc.zenith.dependencies;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencyCollector;
import com.lemonlightmc.zenith.dependency.DependencyHandler;
import com.lemonlightmc.zenith.dependency.DownloadResult;
import com.lemonlightmc.zenith.dependency.coordination.HopperCoordinator;

class BukkitDependencyHandler extends DependencyHandler<Plugin> {

  public BukkitDependencyHandler(final Logger logger, final Path coordinationDir, final Path pluginsDir) {
    super(logger, coordinationDir, pluginsDir);
  }

  @Override
  public void register(final Plugin plugin, final Consumer<DependencyCollector> collector) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    register(plugin.getName(), collector);
  }

  /**
   * @InheritDoc
   */
  @Override
  public void register(final Plugin plugin, final List<Dependency> dependencies) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    register(plugin.getName(), dependencies);
  }

  /**
   * {@InheritDoc}
   */
  @Override
  public void register(final Plugin plugin, final Dependency... dependencies) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    register(plugin.getName(), dependencies);
  }

  /**
   * @InheritDoc
   */
  @Override
  public DownloadResult download(final Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    return download(plugin.getName());
  }

  /**
   * @InheritDoc
   */
  @Override
  public boolean isReady(final Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    return isReady(plugin.getName());
  }

  /**
   * Result of a plugin load operation.
   */
  public record LoadResult(
      List<LoadedPlugin> loaded,
      List<FailedPlugin> failed) {
    /**
     * @return true if all plugins were loaded successfully
     */
    public boolean isSuccess() {
      return failed.isEmpty();
    }

    /**
     * @return true if any plugins were loaded
     */
    public boolean hasLoaded() {
      return !loaded.isEmpty();
    }
  }

  /**
   * A plugin that was successfully loaded.
   */
  public record LoadedPlugin(
      String name,
      String version,
      Path path) {
  }

  /**
   * A plugin that failed to load.
   */
  public record FailedPlugin(
      Path path,
      String error) {
  }

  /**
   * A plugin to load, with its expected name and path.
   */
  public record PluginToLoad(
      String name,
      Path path) {
  }

  /**
   * Result of a combined download and load operation.
   */
  public record DownloadAndLoadResult(
      DownloadResult downloadResult,
      LoadResult loadResult) {
    /**
     * @return true if all downloads succeeded and all plugins were loaded
     */
    public boolean isFullySuccessful() {
      return downloadResult.isSuccess() && loadResult.isSuccess();
    }

    /**
     * @return true if no restart is required (nothing was downloaded, or all
     *         downloaded plugins were loaded)
     */
    public boolean noRestartRequired() {
      return !downloadResult.requiresRestart() || loadResult.isSuccess();
    }
  }

  public DownloadAndLoadResult load(final Plugin plugin) {
    // First, download all dependencies
    final DownloadResult downloadResult = download(plugin);

    // If nothing was downloaded, return early with empty load result
    if (!downloadResult.requiresRestart()) {
      return new DownloadAndLoadResult(
          downloadResult,
          new LoadResult(List.of(), List.of()));
    }

    // Collect plugins to load with their names (for better duplicate detection)
    final List<PluginToLoad> pluginsToLoad = new ArrayList<>();
    for (final DownloadResult.DownloadedDependency dep : downloadResult.downloaded()) {
      pluginsToLoad.add(new PluginToLoad(dep.name(), dep.path()));
    }

    // Log what we're about to do (only in VERBOSE mode for the count message)
    logger.debug("[Hopper] Auto-loading " + pluginsToLoad.size() + " downloaded plugin(s)...");

    // Load the downloaded plugins (with coordination lock and name-based duplicate
    // detection)
    final LoadResult loadResult = loadAllWithNames(pluginsToLoad);

    // Log the final result
    logDownloadAndLoadResult(plugin, downloadResult, loadResult);

    return new DownloadAndLoadResult(downloadResult, loadResult);
  }

  /**
   * Load a single plugin JAR file.
   *
   * @param pluginPath the path to the plugin JAR
   * @return the loaded Plugin instance, or null if loading failed
   */

  public Plugin load(final Path pluginPath) {
    return load(pluginPath, null);
  }

  /**
   * Load a single plugin JAR file with expected name.
   * <p>
   * If the expected plugin name is provided and a plugin with that name is
   * already
   * loaded, returns the existing plugin without attempting to load again.
   *
   * @param pluginPath   the path to the plugin JAR
   * @param expectedName the expected plugin name (for duplicate detection), or
   *                     null
   * @return the loaded Plugin instance, or null if loading failed
   */

  public Plugin load(final Path pluginPath, final String expectedName) {
    final File pluginFile = pluginPath.toFile();

    if (!pluginFile.exists()) {
      logger.warn("Plugin file not found: " + pluginPath);
      return null;
    }

    if (!pluginFile.getName().endsWith(".jar")) {
      logger.warn("Not a JAR file: " + pluginPath);
      return null;
    }

    final PluginManager pm = Bukkit.getPluginManager();

    // Check if plugin is already loaded by name (most reliable check)
    if (expectedName != null) {
      final Plugin existing = pm.getPlugin(expectedName);
      if (existing != null) {
        logger.debug("Plugin already loaded: " + existing.getName() + " v" + existing.getDescription().getVersion());
        return existing;
      }
    }

    // Also check by file path as fallback
    try {
      for (final Plugin p : pm.getPlugins()) {
        final File pFile = getPluginFile(p);
        if (pFile != null && pFile.getAbsolutePath().equals(pluginFile.getAbsolutePath())) {
          logger.debug("Plugin already loaded: " + p.getName());
          return p;
        }
      }
    } catch (final Exception ignored) {
      // Continue with loading attempt
    }

    try {
      logger.debug("Loading plugin: " + pluginFile.getName());

      final Plugin plugin = pm.loadPlugin(pluginFile);
      if (plugin != null) {
        // Call onLoad
        plugin.onLoad();

        // Enable the plugin
        pm.enablePlugin(plugin);

        logger.debug("Successfully loaded: " + plugin.getName() + " v" + plugin.getDescription().getVersion());
        return plugin;
      }
    } catch (final InvalidPluginException e) {
      if (logger != null) {
        logger.error("Invalid plugin: " + pluginFile.getName(), e);
      }
    } catch (final InvalidDescriptionException e) {
      if (logger != null) {
        logger.error("Invalid plugin description: " + pluginFile.getName(), e);
      }
    } catch (final Exception e) {
      if (logger != null) {
        logger.error("Failed to load plugin: " + pluginFile.getName(), e);
      }
    }

    return null;
  }

  /**
   * Load multiple plugin JAR files.
   *
   * @param pluginPaths the paths to the plugin JARs
   * @return the load result containing loaded and failed plugins
   */
  public LoadResult loadAll(final List<Path> pluginPaths) {
    final List<LoadedPlugin> loaded = new ArrayList<>();
    final List<FailedPlugin> failed = new ArrayList<>();

    for (final Path path : pluginPaths) {
      final Plugin plugin = load(path, null);
      if (plugin != null) {
        loaded.add(new LoadedPlugin(
            plugin.getName(),
            plugin.getDescription().getVersion(),
            path));
      } else {
        failed.add(new FailedPlugin(path, "Failed to load plugin"));
      }
    }

    return new LoadResult(loaded, failed);
  }

  /**
   * Load multiple plugins with their expected names.
   * <p>
   * This method is preferred over {@link #loadAll(List)} when the
   * expected
   * plugin names are known, as it provides better duplicate detection.
   *
   * @param plugins the plugins to load with their names and paths
   * @return the load result containing loaded and failed plugins
   */

  public LoadResult loadAllWithNames(final List<PluginToLoad> plugins) {
    // Use file-based coordination to prevent race conditions
    try (HopperCoordinator coordinator = HopperCoordinator.acquire(getCoordinationFolder())) {
      return loadAllWithNamesInternal(plugins);
    } catch (final Exception e) {
      ZenithProvider.zenithLogger().debug("Failed to acquire coordination lock, proceeding without lock");
      // Fall back to uncoordinated loading
      return loadAllWithNamesInternal(plugins);
    }
  }

  private LoadResult loadAllWithNamesInternal(final List<PluginToLoad> plugins) {
    final List<LoadedPlugin> loaded = new ArrayList<>();
    final List<FailedPlugin> failed = new ArrayList<>();

    for (final PluginToLoad toLoad : plugins) {
      final Plugin plugin = load(toLoad.path(), toLoad.name());
      if (plugin != null) {
        loaded.add(new LoadedPlugin(
            plugin.getName(),
            plugin.getDescription().getVersion(),
            toLoad.path()));
      } else {
        failed.add(new FailedPlugin(toLoad.path(), "Failed to load plugin"));
      }
    }

    return new LoadResult(loaded, failed);
  }

  /**
   * Check if a plugin with the given name is currently loaded.
   *
   * @param pluginName the plugin name
   * @return true if the plugin is loaded
   */
  public boolean isLoaded(final String pluginName) {
    return Bukkit.getPluginManager().getPlugin(pluginName) != null;
  }

  /**
   * Get the JAR file for a loaded plugin.
   */

  private File getPluginFile(final Plugin plugin) {
    try {
      // Try to get the plugin file via reflection
      // JavaPlugin.getFile() is protected, so we must use getDeclaredMethod
      final java.lang.reflect.Method getFile = JavaPlugin.class.getDeclaredMethod("getFile");
      getFile.setAccessible(true);
      final Object result = getFile.invoke(plugin);
      if (result instanceof File) {
        return (File) result;
      }
    } catch (final Exception ignored) {
      // Method may not exist or not be accessible
    }

    // Fallback: try to get from the plugin's data folder
    final File dataFolder = plugin.getDataFolder();
    if (dataFolder != null) {
      final File pluginsDir = dataFolder.getParentFile();
      if (pluginsDir != null) {
        final File jarFile = new File(pluginsDir, plugin.getName() + ".jar");
        if (jarFile.exists()) {
          return jarFile;
        }
      }
    }

    return null;
  }

  public void logDownloadAndLoadResult(
      final Plugin plugin,
      final DownloadResult downloadResult,
      final LoadResult loadResult) {
    if (loadResult.hasLoaded()) {
      // QUIET mode: Just show the summary line
      if (logLevel == LogLevel.QUIET) {
        final StringBuilder summary = new StringBuilder("[Hopper] Loaded: ");
        final List<LoadedPlugin> loaded = loadResult.loaded();
        for (int i = 0; i < loaded.size(); i++) {
          if (i > 0)
            summary.append(", ");
          summary.append(loaded.get(i).name()).append(" ").append(loaded.get(i).version());
        }
        logger.info(summary.toString());
      } else {
        // NORMAL and VERBOSE: Show the formatted box
        logger.info("========================================");
        logger.info("  HOPPER - Plugins Auto-Loaded");
        logger.info("========================================");

        for (final LoadedPlugin loaded : loadResult.loaded()) {
          logger.info("  ✓ " + loaded.name() + " v" + loaded.version());
        }

        if (!loadResult.failed().isEmpty()) {
          logger.warn("");
          logger.warn("  Failed to auto-load:");
          for (final FailedPlugin failed : loadResult.failed()) {
            logger.warn("  ✗ " + failed.path().getFileName() + ": " + failed.error());
          }
          logger.warn("");
          logger.warn("  These may require a server RESTART.");
        }

        logger.info("========================================");
      }
    } else if (!downloadResult.downloaded().isEmpty()) {
      // Downloaded but none could be loaded - always show this error
      logger.error("========================================");
      logger.error("  HOPPER - Auto-Load Failed");
      logger.error("========================================");
      logger.error("  Downloaded plugins could not be auto-loaded.");
      logger.error("  Please RESTART the server.");
      logger.error("========================================");
    }

    // Also log any download failures (errors are always shown)
    if (!downloadResult.failed().isEmpty()) {
      logger.error("[Hopper] Failed to download:");
      for (final DownloadResult.FailedDependency dep : downloadResult.failed()) {
        logger.error("  - " + dep.name() + ": " + dep.error());
      }
    }
  }
}
