package com.lemonlightmc.zenith.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.lemonlightmc.zenith.config.handlers.ConfigHandlerType;
import com.lemonlightmc.zenith.config.schema.SchemaPair;

public class ConfigSection implements IConfigData {
  private final String sectionPath;
  private final ConfigData data;

  public ConfigSection(final String path, final ConfigData data) {
    this.sectionPath = path == null ? "" : path;
    this.data = data;
  }

  public String getPath() {
    return sectionPath;
  }

  public String getSectionName() {
    return sectionPath.isEmpty() ? sectionPath : sectionPath.substring(sectionPath.lastIndexOf('.') + 1);
  }

  public String getParentPath() {
    return sectionPath.isEmpty() ? null : sectionPath.substring(0, sectionPath.lastIndexOf('.'));
  }

  public String getChildPath(final String child) {
    return sectionPath.isEmpty() ? child : sectionPath + "." + child;
  }

  @Override
  public int hashCode() {
    return 31 * (31 + sectionPath.hashCode()) + data.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ConfigSection other = (ConfigSection) obj;
    return sectionPath.equals(other.sectionPath) && data.equals(other.data);
  }

  @Override
  public String toString() {
    return "ConfigSection [sectionPath=" + sectionPath + ", data=" + data + "]";
  }

  @Override
  public ConfigHandlerType getType() {
    return data.type;
  }

  @Override
  public Path getFilePath() {
    return data.filePath;
  }

  @Override
  public String getFileName() {
    return data.fileName;
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
  public boolean containsKey(final String path) {
    return data.containsKey(path);
  }

  @Override
  public boolean containsValue(final SchemaPair<?> value) {
    return data.containsValue(value);
  }

  @Override
  public void set(final String path, final SchemaPair<?> value) {
    data.set(path, value);
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
  public Set<String> keySet() {
    return data.keySet();
  }

  @Override
  public Set<String> keySetDeep() {
    return data.keySetDeep();
  }

  @Override
  public Set<Entry<String, SchemaPair<?>>> entrySetDeep() {
    return data.entrySetDeep();
  }

  @Override
  public Set<Entry<String, SchemaPair<?>>> entrySet() {
    return data.entrySet();
  }

  @Override
  public boolean isString(final String path) {
    return data.isString(path);
  }

  @Override
  public boolean isInt(final String path) {
    return data.isInt(path);
  }

  @Override
  public boolean isBoolean(final String path) {
    return data.isBoolean(path);
  }

  @Override
  public boolean isDouble(final String path) {
    return data.isDouble(path);
  }

  @Override
  public boolean isLong(final String path) {
    return data.isLong(path);
  }

  @Override
  public boolean isFloat(final String path) {
    return data.isFloat(path);
  }

  @Override
  public boolean isList(final String path) {
    return data.isList(path);
  }

  @Override
  public boolean isMap(final String path) {
    return data.isMap(path);
  }

  @Override
  public boolean isSection(final String path) {
    return data.isSection(path);
  }

  @Override
  public ConfigSection getSection(final String path) {
    return data.getSection(path);
  }

  @Override
  public SchemaPair<?> get(final String path) {
    return data.get(path);
  }

  @Override
  public String getString(final String path) {
    return data.getString(path);
  }

  @Override
  public Integer getInt(final String path) {
    return data.getInt(path);
  }

  @Override
  public Boolean getBoolean(final String path) {
    return data.getBoolean(path);
  }

  @Override
  public Double getDouble(final String path) {
    return data.getDouble(path);
  }

  @Override
  public Long getLong(final String path) {
    return data.getLong(path);
  }

  @Override
  public Float getFloat(final String path) {
    return data.getFloat(path);
  }

  @Override
  public List<?> getList(final String path) {
    return data.getList(path);
  }

  @Override
  public List<String> getStringList(final String path) {
    return data.getStringList(path);
  }

  @Override
  public List<Integer> getIntegerList(final String path) {
    return data.getIntegerList(path);
  }

  @Override
  public List<Boolean> getBooleanList(final String path) {
    return data.getBooleanList(path);
  }

  @Override
  public List<Double> getDoubleList(final String path) {
    return data.getDoubleList(path);
  }

  @Override
  public List<Float> getFloatList(final String path) {
    return data.getFloatList(path);
  }

  @Override
  public List<Long> getLongList(final String path) {
    return data.getLongList(path);
  }

  @Override
  public List<Byte> getByteList(final String path) {
    return data.getByteList(path);
  }

  @Override
  public List<Character> getCharacterList(final String path) {
    return data.getCharacterList(path);
  }

  @Override
  public List<Short> getShortList(final String path) {
    return data.getShortList(path);
  }

  @Override
  public List<Map<?, ?>> getMapList(final String path) {
    return data.getMapList(path);
  }

  @Override
  public Map<?, ?> getMap(final String path) {
    return data.getMap(path);
  }

  @Override
  public Map<String, Object> getObjectMap(final String path) {
    return data.getObjectMap(path);
  }

  @Override
  public <T> T getObject(final String path, final Class<T> clazz) {
    return data.getObject(path, clazz);
  }

  @Override
  public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz) {
    return data.getSerializable(path, clazz);
  }

}
