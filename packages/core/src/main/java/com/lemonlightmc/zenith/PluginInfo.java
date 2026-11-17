package com.lemonlightmc.zenith;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;

import com.lemonlightmc.zenith.additive.version.Version;

/**
 * Provides convenient access to plugin metadata derived from a Bukkit {@link PluginDescriptionFile}.
 *
 * <p>This wrapper exposes commonly used description values and pre-parsed version information for Zenith plugins.
 */
public class PluginInfo {

  /** The underlying Bukkit plugin description. */
  public final PluginDescriptionFile descriptionFile;

  /** The parsed plugin version. */
  private final Version version;

  /** The parsed target API version, if present. */
  private final Version apiVersion;

  /**
   * Creates plugin metadata from the given description file.
   *
   * @param descriptionFile the Bukkit plugin description to wrap
   */
  public PluginInfo(final PluginDescriptionFile descriptionFile) {
    this.descriptionFile = descriptionFile;
    this.version = Version.semver(descriptionFile.getVersion());
    this.apiVersion = Version.semver(descriptionFile.getAPIVersion());
  }

  /**
   * Returns the wrapped Bukkit plugin description file.
   *
   * @return the plugin description file
   */
  public PluginDescriptionFile getDescriptionFile() {
    return descriptionFile;
  }

  /**
   * Returns the plugin name declared in the description.
   *
   * @return the plugin name
   */
  public String getName() {
    return descriptionFile.getName();
  }

  /**
   * Returns a lowercase key derived from the plugin name.
   *
   * @return the normalized plugin key
   */
  public String getKey() {
    return descriptionFile.getName().toLowerCase(Locale.ROOT);
  }

  /**
   * Returns the plugin name combined with its formatted version.
   *
   * @return the full display name
   */
  public String getFullName() {
    return descriptionFile.getName() + " v" + version.formatted(true);
  }

  /**
   * Returns the parsed plugin version.
   *
   * @return the plugin version
   */
  public Version getVersion() {
    return version;
  }

  /**
   * Returns the formatted plugin version string.
   *
   * @return the formatted plugin version
   */
  public String getFormattedVersion() {
    return version.formatted(true);
  }

  /**
   * Returns the plugin prefix declared in the description.
   *
   * @return the plugin prefix
   */
  public String getPrefix() {
    return descriptionFile.getPrefix();
  }

  /**
   * Returns the plugin description text.
   *
   * @return the plugin description
   */
  public String getDescription() {
    return descriptionFile.getDescription();
  }

  /**
   * Returns the parsed Bukkit API version targeted by the plugin.
   *
   * @return the target API version
   */
  public Version getApiVersion() {
    return apiVersion;
  }

  /**
   * Returns the fully qualified main class name.
   *
   * @return the main class name
   */
  public String getMain() {
    return descriptionFile.getMain();
  }

  /**
   * Returns the list of declared authors.
   *
   * @return the plugin authors
   */
  public List<String> getAuthors() {
    return descriptionFile.getAuthors();
  }

  /**
   * Returns the list of declared contributors.
   *
   * @return the plugin contributors
   */
  public List<String> getContributors() {
    return descriptionFile.getContributors();
  }

  /**
   * Returns the plugin website URL, if configured.
   *
   * @return the plugin website
   */
  public String getWebsite() {
    return descriptionFile.getWebsite();
  }

  /**
   * Returns the hard dependency list declared by the plugin.
   *
   * @return the required plugin dependencies
   */
  public List<String> getDepend() {
    return descriptionFile.getDepend();
  }

  /**
   * Returns the soft dependency list declared by the plugin.
   *
   * @return the optional plugin dependencies
   */
  public List<String> getSoftDepend() {
    return descriptionFile.getSoftDepend();
  }

  /**
   * Returns the configured plugin load order.
   *
   * @return the load order
   */
  public PluginLoadOrder getLoadOrder() {
    return descriptionFile.getLoad();
  }

  /**
   * Returns plugins that should load before this plugin.
   *
   * @return the load-before declarations
   */
  public List<String> getLoadBefore() {
    return descriptionFile.getLoadBefore();
  }

  /**
   * Returns additional plugin names provided by this plugin.
   *
   * @return the provided plugin names
   */
  public List<String> getProvides() {
    return descriptionFile.getProvides();
  }

  /**
   * Returns libraries declared by the plugin description.
   *
   * @return the declared libraries
   */
  public List<String> getLibraries() {
    return descriptionFile.getLibraries();
  }

  /**
   * Returns the command names declared by the plugin.
   *
   * @return the declared command names
   */
  public Set<String> getCommands() {
    return descriptionFile.getCommands().keySet();
  }

  /**
   * Returns the plugin awareness flags declared by the plugin.
   *
   * @return the plugin awareness flags
   */
  public Set<PluginAwareness> getAwareness() {
    return descriptionFile.getAwareness();
  }

  /**
   * Returns the permissions declared by the plugin.
   *
   * @return the declared permissions
   */
  public List<Permission> getPermissions() {
    return descriptionFile.getPermissions();
  }

  /**
   * Returns the default permission value declared by the plugin.
   *
   * @return the default permission value
   */
  public PermissionDefault getPermissionDefault() {
    return descriptionFile.getPermissionDefault();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PluginDescription [getFullName()=" + getFullName() + ", getMain()=" + getMain() + ", getPrefix()="
        + getPrefix() + ", getDescription()=" + getDescription() + ", getApiVersion()=" + getApiVersion()
        + ", getAuthors()=" + getAuthors() + ", getDepend()=" + getDepend() + ", getSoftDepend()=" + getSoftDepend()
        + ", getProvides()=" + getProvides() + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 31 + getFullName().hashCode();
    result = 31 * result + getMain().hashCode();
    result = 31 * result + getClass().hashCode();
    return 31 * result + ((apiVersion == null) ? 0 : apiVersion.hashCode());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final PluginInfo other = (PluginInfo) obj;
    if (apiVersion == null || other.apiVersion != null) {
      return false;
    }
    return descriptionFile.equals(other.descriptionFile) && version.equals(other.version)
        && apiVersion.equals(other.apiVersion);
  }

}
