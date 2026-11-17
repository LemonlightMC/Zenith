package com.lemonlightmc.zenith.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.lemonlightmc.zenith.config.handlers.ConfigHandlerType;
import com.lemonlightmc.zenith.config.schema.BuildSchema;
import com.lemonlightmc.zenith.config.schema.SchemaPair;

//import org.bukkit.configuration.MemorySection;
//import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Stores parsed configuration values for a single configuration file.
 */
public class ConfigData extends AbstractConfigData implements IConfigData {

  private Map<String, SchemaPair<?>> data;

  /**
   * Creates a new configuration data container for a YAML-backed file.
   *
   * @param schema the schema describing valid configuration structure and values
   * @param filePath the path to the configuration file
   */
  public ConfigData(final BuildSchema schema, final Path filePath) {
    super(schema, ConfigHandlerType.YAML, filePath);
  }

  /**
   * Creates a new configuration data container with preloaded data.
   *
   * @param schema the schema describing valid configuration structure and values
   * @param filePath the path to the configuration file
   * @param data the initial parsed configuration data
   */
  public ConfigData(final BuildSchema schema, final Path filePath, final Map<String, SchemaPair<?>> data) {
    super(schema, ConfigHandlerType.YAML, filePath);
    this.data = data;
  }

  /**
   * Replaces the raw parsed data stored in this configuration.
   *
   * @param data the parsed configuration data to store
   */
  void setRawData(final Map<String, SchemaPair<?>> data) {
    this.data = data;
  }

  /**
   * Returns the raw parsed data stored in this configuration.
   *
   * @return the raw parsed configuration data
   */
  public Map<String, SchemaPair<?>> getRawData() {
    return data;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigSection getSection(final String path) {
    return new ConfigSection(path, this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return data.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SchemaPair<?> get(final String path) {
    return data.get(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsKey(final String path) {
    return path == null || path.isEmpty() ? false : data.containsKey(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsValue(final SchemaPair<?> value) {
    return value == null ? false : data.containsValue(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void set(final String path, final SchemaPair<?> value) {
    if (path == null || path.isEmpty()) {
      return;
    }
    if (value == null) {
      data.remove(path);
    } else {
      data.put(path, value);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove(final String path) {
    data.remove(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    data.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> keySetDeep() {
    return data.keySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> keySet() {
    final Set<String> newKeys = new HashSet<>();
    int idx;
    for (final String key : data.keySet()) {
      idx = key.indexOf(".");
      newKeys.add(key.substring(0, idx == -1 ? key.length() : idx));
    }
    return newKeys;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Map.Entry<String, SchemaPair<?>>> entrySetDeep() {
    return data.entrySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Map.Entry<String, SchemaPair<?>>> entrySet() {
    final Map<String, SchemaPair<?>> values = new HashMap<>();
    for (final String key : keySet()) {
      values.put(key, data.get(key));
    }
    return values.entrySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 31 * (31 + super.hashCode()) + data.hashCode();
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
    final ConfigData other = (ConfigData) obj;
    return data.equals(other.data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "YamlConfigData [type=" + type + ", data=" + data + "]";
  }

}
