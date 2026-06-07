package com.lemonlightmc.zenith.updater.coordination;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.lemonlightmc.zenith.updater.DependencySource;
import com.lemonlightmc.zenith.utils.JsonUtil;
import com.lemonlightmc.zenith.utils.Checksum.ChecksumType;

/**
 * Records exact resolved versions for reproducibility.
 * <p>
 * Stored in {@code plugins/zenith/deps.lock}
 */
public final class Lockfile {

  private final int version = 1;
  private Instant timestamp;
  private final Map<String, Entry> entries = new LinkedHashMap<>();

  public Lockfile() {
  }

  public static Lockfile fromJson(String json) {
    return JsonUtil.fromJson(json, Lockfile.class);
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
   * Get the lockfile entry for a dependency.
   *
   * @param dependencyName the dependency name
   * @return the entry, or null if not found
   */
  public Entry getEntry(String dependencyName) {
    return entries.get(dependencyName);
  }

  /**
   * Update or create an entry for a resolved dependency.
   */
  public void updateEntry(String dependencyName, DependencySource.ResolvedDependency resolved) {
    entries.put(dependencyName, new Entry(
        resolved.name(),
        resolved.version().toString(),
        resolved.checksum(),
        resolved.checksumType(),
        resolved.downloadUrl(),
        resolved.fileName()));
  }

  /**
   * Remove an entry.
   */
  public void removeEntry(String dependencyName) {
    entries.remove(dependencyName);
  }

  /**
   * A lockfile entry for a single dependency.
   */
  public static record Entry(
      String name,
      String resolvedVersion,
      String checksum,
      ChecksumType checksumType,
      String downloadUrl,
      String fileName) {
  }

}
