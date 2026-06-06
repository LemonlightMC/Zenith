package com.lemonlightmc.zenith.updater.coordination;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.lemonlightmc.zenith.updater.Dependency;
import com.lemonlightmc.zenith.utils.JsonUtil;
import com.lemonlightmc.zenith.version.VersionConstraint;

/**
 * Tracks which plugins registered which dependencies and merges their
 * constraints.
 * <p>
 * Stored in {@code plugins/zenith/deps-registry.json}
 */
public final class LibraryRegistry {

  private final int version = 1;
  private Instant timestamp;
  private final Map<String, DependencyRegistration> registrations = new LinkedHashMap<>();

  public LibraryRegistry() {
    super();
  }

  public static LibraryRegistry fromJson(String json) {
    return JsonUtil.fromJson(json, LibraryRegistry.class);
  }

  public String toJson() {
    this.timestamp = Instant.now();
    return JsonUtil.toJson(this);
  }

  public int version() {
    return version;
  }

  public Instant timestamp() {
    return timestamp;
  }

  /**
   * Register a plugin's dependencies, merging with existing registrations.
   */
  public void registerPlugin(String pluginName, List<Dependency> dependencies) {
    for (Dependency dep : dependencies) {
      String depName = dep.name();
      DependencyRegistration reg = registrations.get(depName);
      if (reg == null) {
        reg = new DependencyRegistration(depName);
        registrations.put(depName, reg);
      }
      reg.addConstraint(pluginName, dep);
    }
  }

  /**
   * Get the merged constraint for a dependency (intersection of all plugins'
   * constraints).
   *
   * @param dependencyName the dependency name
   * @return merged constraint, or null if not registered
   */
  public VersionConstraint getMergedConstraint(String dependencyName) {
    DependencyRegistration reg = registrations.get(dependencyName);
    return reg != null ? reg.getMergedConstraint() : null;
  }

  /**
   * Get all plugins that requested a dependency.
   */
  public List<String> getRequestingPlugins(String dependencyName) {
    DependencyRegistration reg = registrations.get(dependencyName);
    return reg != null ? new ArrayList<>(reg.requestedBy) : Collections.emptyList();
  }
}
