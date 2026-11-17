package com.lemonlightmc.zenith.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.lemonlightmc.zenith.config.handlers.ConfigHandlerType;
import com.lemonlightmc.zenith.config.schema.SchemaPair;

/**
 * Provides a path-scoped view over a {@link ConfigData} instance.
 */
public class ConfigSection implements IConfigData {
  private final String sectionPath;
  private final ConfigData data;

  /**
   * Creates a new configuration section view.
   *
   * @param path the section path, or {@code null} for the root
   * @param data the backing configuration data
   */
  public ConfigSection(final String path, final ConfigData data) {
    this.sectionPath = path == null ? "" : path;
    this.data = data;
  }

  /**
   * Returns the full path of this section.
   *
   * @return the section path
   */
  public String getPath() {
    return sectionPath;
  }

  /**
   * Returns the name of this section without its parent path.
   *
   * @return the section name
   */
  public String getSectionName() {
    return sectionPath.isEmpty() ? sectionPath : sectionPath.substring(sectionPath.lastIndexOf('.') + 1);
  }

  /**
   * Returns the parent path of this section.
   *
   * @return the parent path, or {@code null} if this is the root section
   */
  public String getParentPath() {
    return sectionPath.isEmpty() ? null : sectionPath.substring(0, sectionPath.lastIndexOf('.'));
  }

  /**
   * Resolves a child path relative to this section.
   *
   * @param child the child path segment
   * @return the resolved child path
   */
  public String getChildPath(final String child) {
    return sectionPath.isEmpty() ? child : sectionPath + "." + child;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 31 * (31 + sectionPath.hashCode()) + data.hashCode();
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
    final ConfigSection other = (ConfigSection) obj;
    return sectionPath.equals(other.sectionPath) && data.equals(other.data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "ConfigSection [sectionPath=" + sectionPath + ", data=" + data + "]";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigHandlerType getType() {
    return data.type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Path getFilePath() {
    return data.filePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getFileName() {
    return data.fileName;
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
  public boolean containsKey(final String path) {
    return data.containsKey(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsValue(final SchemaPair<?> value) {
    return data.containsValue(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void set(final String path, final SchemaPair<?> value) {
    data.set(path, value);
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
  public Set<String> keySet() {
    return data.keySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<String> keySetDeep() {
    return data.keySetDeep();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Entry<String, SchemaPair<?>>> entrySetDeep() {
    return data.entrySetDeep();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Entry<String, SchemaPair<?>>> entrySet() {
    return data.entrySet();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isString(final String path) {
    return data.isString(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isInt(final String path) {
    return data.isInt(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isBoolean(final String path) {
    return data.isBoolean(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDouble(final String path) {
    return data.isDouble(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLong(final String path) {
    return data.isLong(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFloat(final String path) {
    return data.isFloat(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isList(final String path) {
    return data.isList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMap(final String path) {
    return data.isMap(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSection(final String path) {
    return data.isSection(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigSection getSection(final String path) {
    return data.getSection(path);
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
  public String getString(final String path) {
    return data.getString(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getInt(final String path) {
    return data.getInt(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Boolean getBoolean(final String path) {
    return data.getBoolean(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double getDouble(final String path) {
    return data.getDouble(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getLong(final String path) {
    return data.getLong(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Float getFloat(final String path) {
    return data.getFloat(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<?> getList(final String path) {
    return data.getList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getStringList(final String path) {
    return data.getStringList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Integer> getIntegerList(final String path) {
    return data.getIntegerList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Boolean> getBooleanList(final String path) {
    return data.getBooleanList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Double> getDoubleList(final String path) {
    return data.getDoubleList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Float> getFloatList(final String path) {
    return data.getFloatList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Long> getLongList(final String path) {
    return data.getLongList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Byte> getByteList(final String path) {
    return data.getByteList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Character> getCharacterList(final String path) {
    return data.getCharacterList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Short> getShortList(final String path) {
    return data.getShortList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Map<?, ?>> getMapList(final String path) {
    return data.getMapList(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<?, ?> getMap(final String path) {
    return data.getMap(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> getObjectMap(final String path) {
    return data.getObjectMap(path);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T getObject(final String path, final Class<T> clazz) {
    return data.getObject(path, clazz);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz) {
    return data.getSerializable(path, clazz);
  }

}
