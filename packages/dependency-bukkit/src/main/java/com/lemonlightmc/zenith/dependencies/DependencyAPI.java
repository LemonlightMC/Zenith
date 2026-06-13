package com.lemonlightmc.zenith.dependencies;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencyCollector;
import com.lemonlightmc.zenith.dependency.DownloadResult;
import com.lemonlightmc.zenith.utils.Lazy;

public class DependencyAPI {
  private static Lazy<BukkitDependencyHandler> handler = Lazy.from(() -> {
    return new BukkitDependencyHandler();
  });

  /**
   * @InheritDoc
   */
  public static void register(final String key, final Dependency... dependencies) {
    handler.get().register(key, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final String key, final List<Dependency> dependencies) {
    handler.get().register(key, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final String key, final Consumer<DependencyCollector> collector) {
    handler.get().register(key, collector);
  }

  /**
   * @InheritDoc
   */
  public static void register(final Plugin plugin, final Dependency... dependencies) {
    handler.get().register(plugin, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final Plugin plugin, final List<Dependency> dependencies) {
    handler.get().register(plugin, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final Plugin plugin, final Consumer<DependencyCollector> collector) {
    handler.get().register(plugin, collector);
  }

  /**
   * @InheritDoc
   */
  public static DownloadResult download(final Plugin plugin) {
    return handler.get().download(plugin);
  }

  /**
   * @InheritDoc
   */
  public static DownloadResult download(final String key) {
    return handler.get().download(key);
  }

  /**
   * @InheritDoc
   */
  public static boolean isReady(final String key) {
    return handler.get().isReady(key);
  }

  /**
   * @InheritDoc
   */
  public static boolean isReady(final Plugin plugin) {
    return handler.get().isReady(plugin);
  }
}
