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

public class ConfigData extends AbstractConfigData implements IConfigData {

  private Map<String, SchemaPair<?>> data;

  public ConfigData(final BuildSchema schema, final Path filePath) {
    super(schema, ConfigHandlerType.YAML, filePath);
  }

  public ConfigData(final BuildSchema schema, final Path filePath, final Map<String, SchemaPair<?>> data) {
    super(schema, ConfigHandlerType.YAML, filePath);
    this.data = data;
  }

  void setRawData(final Map<String, SchemaPair<?>> data) {
    this.data = data;
  }

  public Map<String, SchemaPair<?>> getRawData() {
    return data;
  }

  @Override
  public ConfigSection getSection(final String path) {
    return new ConfigSection(path, this);
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public SchemaPair<?> get(final String path) {
    return data.get(path);
  }

  @Override
  public boolean containsKey(final String path) {
    return path == null || path.isEmpty() ? false : data.containsKey(path);
  }

  @Override
  public boolean containsValue(final SchemaPair<?> value) {
    return value == null ? false : data.containsValue(value);
  }

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

  @Override
  public void remove(final String path) {
    data.remove(path);
  }

  @Override
  public void clear() {
    data.clear();
  }

  @Override
  public Set<String> keySetDeep() {
    return data.keySet();
  }

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

  @Override
  public Set<Map.Entry<String, SchemaPair<?>>> entrySetDeep() {
    return data.entrySet();
  }

  @Override
  public Set<Map.Entry<String, SchemaPair<?>>> entrySet() {
    final Map<String, SchemaPair<?>> values = new HashMap<>();
    for (final String key : keySet()) {
      values.put(key, data.get(key));
    }
    return values.entrySet();
  }

  @Override
  public int hashCode() {
    return 31 * (31 + super.hashCode()) + data.hashCode();
  }

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

  @Override
  public String toString() {
    return "YamlConfigData [type=" + type + ", data=" + data + "]";
  }

}
