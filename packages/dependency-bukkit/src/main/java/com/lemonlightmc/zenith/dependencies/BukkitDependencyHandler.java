package com.lemonlightmc.zenith.dependencies;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.DependencyCollector;
import com.lemonlightmc.zenith.dependency.DependencyHandler;
import com.lemonlightmc.zenith.dependency.DownloadResult;

public class BukkitDependencyHandler extends DependencyHandler<Plugin> {

  public BukkitDependencyHandler() {
    super();
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
  public void register(Plugin plugin, List<Dependency> dependencies) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    register(plugin.getName(), dependencies);
  }

  /**
   * @InheritDoc
   */
  @Override
  public void register(Plugin plugin, Dependency... dependencies) {
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

}
