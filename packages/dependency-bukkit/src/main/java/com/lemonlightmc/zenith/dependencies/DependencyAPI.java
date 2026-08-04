package com.lemonlightmc.zenith.dependencies;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.dependencies.BukkitDependencyHandler.LoadResult;
import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencyCollector;
import com.lemonlightmc.zenith.dependency.DownloadResult;

public class DependencyAPI {
  private static Logger logger = ZenithProvider.zenithLogger("dependency");
  private static BukkitDependencyHandler handler = new BukkitDependencyHandler(logger,
      ZenithProvider.zenithFolder().resolve(".deps"), ZenithProvider.pluginsFolder());

  public static Path getCoordinationDir() {
    return handler.getCoordinationFolder();
  }

  public static Path getPluginsFolder() {
    return handler.getPluginsFolder();
  }

  /**
   * @InheritDoc
   */
  public static void register(final String key, final Dependency... dependencies) {
    handler.register(key, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final String key, final List<Dependency> dependencies) {
    handler.register(key, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final String key, final Consumer<DependencyCollector> collector) {
    handler.register(key, collector);
  }

  /**
   * @InheritDoc
   */
  public static void register(final Plugin plugin, final Dependency... dependencies) {
    handler.register(plugin, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final Plugin plugin, final List<Dependency> dependencies) {
    handler.register(plugin, dependencies);
  }

  /**
   * @InheritDoc
   */
  public static void register(final Plugin plugin, final Consumer<DependencyCollector> collector) {
    handler.register(plugin, collector);
  }

  /**
   * @InheritDoc
   */
  public static DownloadResult download(final Plugin plugin) {
    return handler.download(plugin);
  }

  /**
   * @InheritDoc
   */
  public static DownloadResult download(final String key) {
    return handler.download(key);
  }

  public static LoadResult load(final String key, final Plugin plugin) {
    return handler.load(key, plugin);
  }

  /**
   * @InheritDoc
   */
  public static boolean isReady(final String key) {
    return handler.isReady(key);
  }

  /**
   * @InheritDoc
   */
  public static boolean isReady(final Plugin plugin) {
    return handler.isReady(plugin);
  }
}
