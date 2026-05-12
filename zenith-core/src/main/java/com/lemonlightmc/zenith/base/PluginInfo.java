package com.lemonlightmc.zenith.base;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoadOrder;

import com.lemonlightmc.zenith.version.Version;

public class PluginInfo {

  public final PluginDescriptionFile descriptionFile;
  private final Version version;
  private final Version apiVersion;

  PluginInfo(final PluginDescriptionFile descriptionFile) {
    this.descriptionFile = descriptionFile;
    this.version = new Version(descriptionFile.getVersion());
    this.apiVersion = new Version(descriptionFile.getAPIVersion());
  }

  public PluginDescriptionFile getDescriptionFile() {
    return descriptionFile;
  }

  public String getName() {
    return descriptionFile.getName();
  }

  public String getKey() {
    return descriptionFile.getName().toLowerCase(Locale.ROOT);
  }

  public String getFullName() {
    return descriptionFile.getName() + " v" + version.formatted(true);
  }

  public Version getVersion() {
    return version;
  }

  public String getFormattedVersion() {
    return version.formatted(true);
  }

  public String getPrefix() {
    return descriptionFile.getPrefix();
  }

  public String getDescription() {
    return descriptionFile.getDescription();
  }

  public Version getApiVersion() {
    return apiVersion;
  }

  public String getMain() {
    return descriptionFile.getMain();
  }

  public List<String> getAuthors() {
    return descriptionFile.getAuthors();
  }

  public List<String> getContributors() {
    return descriptionFile.getContributors();
  }

  public String getWebsite() {
    return descriptionFile.getWebsite();
  }

  public List<String> getDepend() {
    return descriptionFile.getDepend();
  }

  public List<String> getSoftDepend() {
    return descriptionFile.getSoftDepend();
  }

  public PluginLoadOrder getLoadOrder() {
    return descriptionFile.getLoad();
  }

  public List<String> getLoadBefore() {
    return descriptionFile.getLoadBefore();
  }

  public List<String> getProvides() {
    return descriptionFile.getProvides();
  }

  public List<String> getLibraries() {
    return descriptionFile.getLibraries();
  }

  public Set<String> getCommands() {
    return descriptionFile.getCommands().keySet();
  }

  public Set<PluginAwareness> getAwareness() {
    return descriptionFile.getAwareness();
  }

  public List<Permission> getPermissions() {
    return descriptionFile.getPermissions();
  }

  public PermissionDefault getPermissionDefault() {
    return descriptionFile.getPermissionDefault();
  }

  @Override
  public String toString() {
    return "PluginDescription [getFullName()=" + getFullName() + ", getMain()=" + getMain() + ", getPrefix()="
        + getPrefix() + ", getDescription()=" + getDescription() + ", getApiVersion()=" + getApiVersion()
        + ", getAuthors()=" + getAuthors() + ", getDepend()=" + getDepend() + ", getSoftDepend()=" + getSoftDepend()
        + ", getProvides()=" + getProvides() + "]";
  }

  @Override
  public int hashCode() {
    int result = 31 + getFullName().hashCode();
    result = 31 * result + getMain().hashCode();
    result = 31 * result + getClass().hashCode();
    return 31 * result + ((apiVersion == null) ? 0 : apiVersion.hashCode());
  }

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
