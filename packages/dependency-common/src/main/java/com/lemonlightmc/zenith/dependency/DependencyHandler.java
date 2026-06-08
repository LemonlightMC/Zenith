package com.lemonlightmc.zenith.dependency;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.lemonlightmc.zenith.dependency.coordination.Processor;
import com.lemonlightmc.zenith.messages.Logger;
import com.lemonlightmc.zenith.utils.Lazy;

public abstract class DependencyHandler {

  private static final Map<String, PluginRegistration> registrations = new ConcurrentHashMap<>();

  private static Lazy<Processor> processor = Lazy.from(() -> {
    Path pluginsFolder = null;
    // Auto-detect plugins folder if not provided
    if (pluginsFolder == null) {
      pluginsFolder = Path.of("plugins");
    }
    return new Processor(pluginsFolder);
  });

  private record PluginRegistration(String name, List<Dependency> dependencies) {
  }

  protected DependencyHandler() {
  }

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   *
   * @param key       the consumer name
   * @param collector consumer that registers dependencies
   */
  public static void register(String key, Consumer<DependencyCollector> collector) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("Plugin name cannot be null or empty");
    }
    if (collector == null) {
      throw new IllegalArgumentException("Collector cannot be null");
    }

    DependencyCollector deps = new DependencyCollector();
    collector.accept(deps);

    registrations.put(key, new PluginRegistration(key, deps.getDependencies()));
  }

  /**
   * Download all registered dependencies for a plugin.
   * Call this in your plugin's onLoad() method.
   *
   * @param key the consumer name
   * @return download result
   */
  public static DownloadResult download(String key) {
    return download(key, null);
  }

  /**
   * Download all registered dependencies for a plugin with explicit plugins
   * folder.
   *
   * @param key           the consumer name
   * @param pluginsFolder the plugins folder (null to auto-detect)
   * @return download result
   */
  public static DownloadResult download(String key, Path pluginsFolder) {
    PluginRegistration registration = registrations.get(key);
    if (registration == null) {
      // No dependencies registered - this can happen if:
      // 1. register() was never called
      // 2. Plugin name doesn't match (typo, different casing)
      // 3. Different classloader (shouldn't happen within same plugin)
      Logger.warn("No dependencies registered for plugin: " + key);
      if (!registrations.isEmpty()) {
        Logger.warn("Registered plugins: " + registrations.keySet());
      }
      return DownloadResult.empty();
    }

    Logger.info("Processing " + registration.dependencies().size() + " dependency(ies) for " + key);

    return processor.get().downloadDependencies(key, registration.dependencies());
  }

  /**
   * Check if all dependencies for a plugin are ready (downloaded and loaded).
   *
   * @param key the consumer name
   * @return true if all dependencies are ready
   */
  public static boolean isReady(String key) {
    return processor.isPresent() ? processor.get().isReady(key) : false;
  }
}
