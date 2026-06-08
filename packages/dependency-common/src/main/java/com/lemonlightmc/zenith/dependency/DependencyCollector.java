package com.lemonlightmc.zenith.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects dependencies during plugin registration.
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * DependencyAPI.register(this, deps -> {
 *   deps.require(Dependency.hangar("ProtocolLib").minVersion("5.0.0").build());
 *   deps.require(Dependency.modrinth("packetevents").latest().build());
 * });
 * }</pre>
 */
public final class DependencyCollector {

  private final List<Dependency> dependencies = new ArrayList<>();

  /**
   * Create a new dependency collector.
   */
  public DependencyCollector() {
  }

  /**
   * Add a required dependency.
   *
   * @param dependency the dependency to require
   * @return this collector for chaining
   */
  public DependencyCollector require(Dependency dependency) {
    if (dependency == null) {
      throw new IllegalArgumentException("Dependency cannot be null");
    }
    dependencies.add(dependency);
    return this;
  }

  /**
   * Add multiple dependencies.
   *
   * @param deps the dependencies to add
   * @return this collector for chaining
   */
  public DependencyCollector require(Dependency... deps) {
    for (Dependency dep : deps) {
      require(dep);
    }
    return this;
  }

  /**
   * @return an unmodifiable view of the collected dependencies
   */

  public List<Dependency> getDependencies() {
    return Collections.unmodifiableList(dependencies);
  }
}