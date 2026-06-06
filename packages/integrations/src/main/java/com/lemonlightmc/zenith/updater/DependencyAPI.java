package com.lemonlightmc.zenith.updater;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.messages.Logger;
import com.lemonlightmc.zenith.updater.coordination.Processor;
import com.lemonlightmc.zenith.utils.Lazy;

public class DependencyAPI {

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

  private DependencyAPI() {
  }

  /**
   * Register dependencies for a plugin.
   * Call this in your plugin constructor or static initializer.
   *
   * @param pluginName the plugin name
   * @param collector  consumer that registers dependencies
   */
  public static void register(String pluginName, Consumer<DependencyCollector> collector) {
    if (pluginName == null || pluginName.isEmpty()) {
      throw new IllegalArgumentException("Plugin name cannot be null or empty");
    }
    if (collector == null) {
      throw new IllegalArgumentException("Collector cannot be null");
    }

    DependencyCollector deps = new DependencyCollector();
    collector.accept(deps);

    registrations.put(pluginName, new PluginRegistration(pluginName, deps.getDependencies()));
  }

  /**
   * Register dependencies for a Bukkit plugin.
   * Call this in your plugin constructor.
   *
   * @param plugin    the plugin instance (must have getName() method)
   * @param collector consumer that registers dependencies
   */
  public static void register(Plugin plugin, Consumer<DependencyCollector> collector) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    register(plugin.getName(), collector);
  }

  /**
   * Download all registered dependencies for a plugin.
   * Call this in your plugin's onLoad() method.
   *
   * @param pluginName the plugin name
   * @return download result
   */
  public static DownloadResult download(String pluginName) {
    return download(pluginName, null);
  }

  /**
   * Download all registered dependencies for a plugin.
   * Call this in your plugin's onLoad() method.
   *
   * @param plugin the plugin instance
   * @return download result
   */
  public static DownloadResult download(Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    Path pluginsFolder = plugin.getDataFolder().toPath().getParent();
    return download(plugin.getName(), pluginsFolder);
  }

  /**
   * Download all registered dependencies for a plugin with explicit plugins
   * folder.
   *
   * @param pluginName    the plugin name
   * @param pluginsFolder the plugins folder (null to auto-detect)
   * @return download result
   */
  public static DownloadResult download(String pluginName, Path pluginsFolder) {
    PluginRegistration registration = registrations.get(pluginName);
    if (registration == null) {
      // No dependencies registered - this can happen if:
      // 1. register() was never called
      // 2. Plugin name doesn't match (typo, different casing)
      // 3. Different classloader (shouldn't happen within same plugin)
      Logger.warn("No dependencies registered for plugin: " + pluginName);
      if (!registrations.isEmpty()) {
        Logger.warn("Registered plugins: " + registrations.keySet());
      }
      return DownloadResult.empty();
    }

    Logger.info("Processing " + registration.dependencies().size() + " dependency(ies) for " + pluginName);

    return processor.get().downloadDependencies(pluginName, registration.dependencies());
  }

  /**
   * Check if all dependencies for a plugin are ready (downloaded and loaded).
   *
   * @param pluginName the plugin name
   * @return true if all dependencies are ready
   */
  public static boolean isReady(String pluginName) {
    return processor.isPresent() ? processor.get().isReady(pluginName) : false;
  }

  /**
   * Check if all dependencies for a plugin are ready.
   *
   * @param plugin the plugin instance
   * @return true if all dependencies are ready
   */
  public static boolean isReady(Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    return processor.isPresent() ? processor.get().isReady(plugin.getName()) : false;
  }
}
