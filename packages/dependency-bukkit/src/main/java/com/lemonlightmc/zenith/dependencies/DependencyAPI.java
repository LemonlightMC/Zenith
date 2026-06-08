package com.lemonlightmc.zenith.dependencies;

import java.nio.file.Path;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.dependency.DependencyCollector;
import com.lemonlightmc.zenith.dependency.DependencyHandler;
import com.lemonlightmc.zenith.dependency.DownloadResult;

public class DependencyAPI extends DependencyHandler {

  public DependencyAPI() {
    super();
  }

  /**
   * Register dependencies for a Bukkit plugin.
   * Call this in your plugin constructor.
   *
   * @param plugin    the plugin instance (must have getName() method)
   * @param collector consumer that registers dependencies
   */
  public static void register(final Plugin plugin, final Consumer<DependencyCollector> collector) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    DependencyHandler.register(plugin.getName(), collector);
  }

  /**
   * Download all registered dependencies for a plugin.
   * Call this in your plugin's onLoad() method.
   *
   * @param plugin the plugin instance
   * @return download result
   */
  public static DownloadResult download(final Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    final Path pluginsFolder = plugin.getDataFolder().toPath().getParent();
    return DependencyHandler.download(plugin.getName(), pluginsFolder);
  }

  /**
   * Check if all dependencies for a plugin are ready.
   *
   * @param plugin the plugin instance
   * @return true if all dependencies are ready
   */
  public static boolean isReady(final Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    return DependencyHandler.isReady(plugin.getName());
  }
}
