package com.lemonlightmc.zenith.dependency;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.lemonlightmc.zenith.dependency.coordination.Processor;
import com.lemonlightmc.zenith.messages.Logger;

public abstract class DependencyHandler<K> {

  private final Map<String, PluginRegistration> registrations = new ConcurrentHashMap<>();

  private static Processor processor = new Processor();

  private record PluginRegistration(String name, Dependency[] dependencies) {
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
      Logger.warn("No dependencies registered for plugin: " + key);
      if (!registrations.isEmpty()) {
        Logger.warn("Registered plugins: " + registrations.keySet());
      }
      return DownloadResult.empty();
    }

    Logger.info("Processing " + registration.dependencies().length + " dependency(ies) for " + key);
    return processor.downloadDependencies(key, registration.dependencies());
  }

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
    return processor.isReady(key);
  }

  /**
   * Check if all dependencies for a plugin are ready (downloaded and loaded).
   *
   * @param plugin the plugin instance
   * @return true if all dependencies are ready
   */
  public abstract boolean isReady(final K key);

}
