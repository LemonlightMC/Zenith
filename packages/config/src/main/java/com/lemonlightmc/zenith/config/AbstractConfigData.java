
package com.lemonlightmc.zenith.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.lemonlightmc.zenith.config.handlers.ConfigHandlerType;
import com.lemonlightmc.zenith.config.schema.BuildSchema;
import com.lemonlightmc.zenith.config.schema.SchemaPair;
import com.lemonlightmc.zenith.config.schema.SchemaType;

/**
 * Base implementation for configuration data containers backed by a schema and file path.
 */
public abstract class AbstractConfigData {

  /** The handler type associated with this configuration. */
  protected ConfigHandlerType type;
  /** The file path backing this configuration. */
  protected Path filePath;
  /** The file name derived from the configuration path. */
  protected String fileName;
  /** The schema used to parse and validate configuration values. */
  protected BuildSchema schema;

  /**
   * Creates a new configuration data container.
   *
   * @param schema the schema describing valid configuration structure and values
   * @param type the handler type associated with this configuration
   * @param filePath the path to the configuration file, or {@code null} if none is set
   */
  public AbstractConfigData(final BuildSchema schema, final ConfigHandlerType type, final Path filePath) {
    this.type = type;
    this.filePath = filePath;
    final Path fileName = filePath == null ? null : filePath.getFileName();
    this.fileName = fileName == null ? null : fileName.toString();
    this.schema = schema;
  }

  /**
   * Creates a new configuration data container without an associated file path.
   *
   * @param schema the schema describing valid configuration structure and values
   * @param type the handler type associated with this configuration
   */
  public AbstractConfigData(final BuildSchema schema, final ConfigHandlerType type) {
    this(schema, type, null);
  }

  /**
   * Returns the handler type associated with this configuration.
   *
   * @return the configuration handler type
   */
  public ConfigHandlerType getType() {
    return type;
  }

  /**
   * Returns the file path backing this configuration.
   *
   * @return the configuration file path, or {@code null} if none is set
   */
  public Path getFilePath() {
    return filePath;
  }

  /**
   * Returns the file name of this configuration.
   *
   * @return the configuration file name, or {@code null} if unavailable
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Returns the schema used by this configuration.
   *
   * @return the configuration schema
   */
  public BuildSchema getSchema() {
    return schema;
  }

  /**
   * Checks whether this configuration contains no entries.
   *
   * @return {@code true} if the configuration is empty; {@code false} otherwise
   */
  public abstract boolean isEmpty();

  /**
   * Returns the number of stored entries.
   *
   * @return the entry count
   */
  public abstract int size();

  /**
   * Checks whether a value exists at the given path.
   *
   * @param path the configuration path
   * @return {@code true} if the path is present; {@code false} otherwise
   */
  public abstract boolean containsKey(String path);

  /**
   * Checks whether the given schema pair value is present.
   *
   * @param value the schema pair value to check
   * @return {@code true} if the value is present; {@code false} otherwise
   */
  public abstract boolean containsValue(SchemaPair<?> value);

  /**
   * Sets the schema pair at the given path.
   *
   * @param path the configuration path
   * @param value the schema pair value to store
   */
  public abstract void set(String path, SchemaPair<?> value);

  /**
   * Removes the value at the given path.
   *
   * @param path the configuration path
   */
  public abstract void remove(String path);

  /**
   * Removes all stored entries.
   */
  public abstract void clear();

  /**
   * Returns all keys, including nested paths.
   *
   * @return a set of all keys
   */
  public abstract Set<String> keySetDeep();

  /**
   * Returns the top-level keys in this configuration.
   *
   * @return a set of top-level keys
   */
  public abstract Set<String> keySet();

  /**
   * Returns all entries, including nested paths.
   *
   * @return a set of all entries
   */
  public abstract Set<Map.Entry<String, SchemaPair<?>>> entrySetDeep();

  /**
   * Returns the top-level entries in this configuration.
   *
   * @return a set of top-level entries
   */
  public abstract Set<Map.Entry<String, SchemaPair<?>>> entrySet();

  /**
   * Checks whether the value at the given path is a string.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a string or absent; {@code false} otherwise
   */
  public boolean isString(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.STRING);
  }

  /**
   * Checks whether the value at the given path is an integer.
   *
   * @param path the configuration path
   * @return {@code true} if the value is an integer or absent; {@code false} otherwise
   */
  public boolean isInt(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.INT);
  }

  /**
   * Checks whether the value at the given path is a boolean.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a boolean or absent; {@code false} otherwise
   */
  public boolean isBoolean(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.BOOL);
  }

  /**
   * Checks whether the value at the given path is a double.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a double or absent; {@code false} otherwise
   */
  public boolean isDouble(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.DOUBLE);
  }

  /**
   * Checks whether the value at the given path is a long.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a long or absent; {@code false} otherwise
   */
  public boolean isLong(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.LONG);
  }

  /**
   * Checks whether the value at the given path is a float.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a float or absent; {@code false} otherwise
   */
  public boolean isFloat(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.FLOAT);
  }

  /**
   * Checks whether the value at the given path is a list.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a list or absent; {@code false} otherwise
   */
  public boolean isList(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.LIST);
  }

  /**
   * Checks whether the value at the given path is a map.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a map or absent; {@code false} otherwise
   */
  public boolean isMap(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.MAP);
  }

  /**
   * Checks whether the value at the given path is a section.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a section or absent; {@code false} otherwise
   */
  public boolean isSection(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.SECTION);
  }

  /**
   * Returns a section view for the given path.
   *
   * @param path the configuration path
   * @return the section view for the path
   */
  public abstract ConfigSection getSection(String path);

  /**
   * Returns the schema pair stored at the given path.
   *
   * @param path the configuration path
   * @return the schema pair, or {@code null} if not present
   */
  public abstract SchemaPair<?> get(String path);

  /**
   * Returns the string value at the given path.
   *
   * @param path the configuration path
   * @return the string value, or {@code null} if unavailable
   */
  public String getString(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.STRING).orElse(null);
  }

  /**
   * Returns the integer value at the given path.
   *
   * @param path the configuration path
   * @return the integer value, or {@code null} if unavailable
   */
  public Integer getInt(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.INT).orElse(null);
  }

  /**
   * Returns the boolean value at the given path.
   *
   * @param path the configuration path
   * @return the boolean value, or {@code null} if unavailable
   */
  public Boolean getBoolean(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.BOOL).orElse(null);
  }

  /**
   * Returns the double value at the given path.
   *
   * @param path the configuration path
   * @return the double value, or {@code null} if unavailable
   */
  public Double getDouble(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.DOUBLE).orElse(null);
  }

  /**
   * Returns the long value at the given path.
   *
   * @param path the configuration path
   * @return the long value, or {@code null} if unavailable
   */
  public Long getLong(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.LONG).orElse(null);
  }

  /**
   * Returns the float value at the given path.
   *
   * @param path the configuration path
   * @return the float value, or {@code null} if unavailable
   */
  public Float getFloat(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.FLOAT).orElse(null);
  }

  /**
   * Returns the list value at the given path.
   *
   * @param path the configuration path
   * @return the list value, or {@code null} if unavailable
   */
  public List<?> getList(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.LIST).orElse(null);
  }

  /**
   * Returns the string list at the given path.
   *
   * @param path the configuration path
   * @return the string list, or {@code null} if unavailable
   */
  public List<String> getStringList(final String path) {
    return getTypedList(path, SchemaType.STRING.parser);
  }

  /**
   * Returns the integer list at the given path.
   *
   * @param path the configuration path
   * @return the integer list, or {@code null} if unavailable
   */
  public List<Integer> getIntegerList(final String path) {
    return getTypedList(path, SchemaType.INT.parser);
  }

  /**
   * Returns the boolean list at the given path.
   *
   * @param path the configuration path
   * @return the boolean list, or {@code null} if unavailable
   */
  public List<Boolean> getBooleanList(final String path) {
    return getTypedList(path, SchemaType.BOOL.parser);
  }

  /**
   * Returns the double list at the given path.
   *
   * @param path the configuration path
   * @return the double list, or {@code null} if unavailable
   */
  public List<Double> getDoubleList(final String path) {
    return getTypedList(path, SchemaType.DOUBLE.parser);
  }

  /**
   * Returns the float list at the given path.
   *
   * @param path the configuration path
   * @return the float list, or {@code null} if unavailable
   */
  public List<Float> getFloatList(final String path) {
    return getTypedList(path, SchemaType.FLOAT.parser);
  }

  /**
   * Returns the long list at the given path.
   *
   * @param path the configuration path
   * @return the long list, or {@code null} if unavailable
   */
  public List<Long> getLongList(final String path) {
    return getTypedList(path, SchemaType.LONG.parser);
  }

  /**
   * Returns the byte list at the given path.
   *
   * @param path the configuration path
   * @return the byte list, or {@code null} if unavailable
   */
  public List<Byte> getByteList(final String path) {
    return getTypedList(path, SchemaType.BYTE.parser);
  }

  /**
   * Returns the character list at the given path.
   *
   * @param path the configuration path
   * @return the character list, or {@code null} if unavailable
   */
  public List<Character> getCharacterList(final String path) {
    return getTypedList(path, SchemaType.CHAR.parser);
  }

  /**
   * Returns the short list at the given path.
   *
   * @param path the configuration path
   * @return the short list, or {@code null} if unavailable
   */
  public List<Short> getShortList(final String path) {
    return getTypedList(path, SchemaType.SHORT.parser);
  }

  private <T> List<T> getTypedList(final String path, final Function<Object, T> mapper) {
    final SchemaPair<?> obj = get(path);
    if (obj == null) {
      return null;
    }
    final Optional<List<?>> optionalList = obj.toType(SchemaType.LIST);
    if (optionalList.isEmpty()) {
      return null;
    }
    final List<T> result = new ArrayList<T>();
    T v;
    for (final Object tempObj : optionalList.get()) {
      v = mapper.apply(tempObj);
      if (v != null) {
        result.add(v);
      }
    }
    return result;
  }

  /**
   * Returns the map list at the given path.
   *
   * @param path the configuration path
   * @return the map list, or {@code null} if unavailable
   */
  public List<Map<?, ?>> getMapList(final String path) {
    final List<?> list = getList(path);
    if (list == null) {
      return null;
    }
    final List<Map<?, ?>> result = new ArrayList<Map<?, ?>>();
    for (final Object obj : list) {
      if (obj instanceof Map) {
        result.add((Map<?, ?>) obj);
      }
    }
    return result;
  }

  /**
   * Returns the map value at the given path.
   *
   * @param path the configuration path
   * @return the map value, or {@code null} if unavailable
   */
  public Map<?, ?> getMap(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.MAP).orElse(null);
  }

  /**
   * Returns the map value at the given path as a string-keyed object map.
   *
   * @param path the configuration path
   * @return the object map, or {@code null} if unavailable
   */
  public Map<String, Object> getObjectMap(final String path) {
    final SchemaPair<?> obj = get(path);
    if (obj == null) {
      return null;
    }
    final Optional<Map<?, ?>> map = obj.toType(SchemaType.MAP);
    if (map.isEmpty()) {
      return null;
    }
    final java.util.Map<?, ?> mapContent = map.get();
    if (mapContent == null) {
      return Map.of();
    }
    final Map<String, Object> result = new HashMap<>();
    for (final Map.Entry<?, ?> entry : mapContent.entrySet()) {
      if (entry != null) {
        result.put((String) entry.getKey(), (Object) entry.getValue());
      }
    }
    return result;
  }

  /**
   * Returns the object value at the given path cast to the requested type.
   *
   * @param <T> the target object type
   * @param path the configuration path
   * @param clazz the class to cast the value to
   * @return the typed object value, or {@code null} if unavailable
   */
  @SuppressWarnings("unchecked")
  public <T extends Object> T getObject(final String path, final Class<T> clazz) {
    final SchemaPair<?> pair = get(path);
    if (pair == null || clazz == null) {
      return null;
    }
    final Optional<?> obj = pair.toType(SchemaType.CUSTOM);
    final T val = (obj.isPresent() && clazz.isInstance(obj.get())) ? clazz.cast(obj.get()) : (T) pair.def;
    return val;
  }

  /**
   * Returns the serializable object at the given path.
   *
   * @param <T> the serializable type
   * @param path the configuration path
   * @param clazz the serializable class to cast the value to
   * @return the serializable object, or {@code null} if unavailable
   */
  public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz) {
    return getObject(path, clazz);
  }

  /**
   * Removes comments from all schema pairs in this configuration.
   */
  public void stripComments() {
    for (Map.Entry<String, SchemaPair<?>> entry : entrySetDeep()) {
      entry.getValue().setComment(null);
    }
  }

  /**
   * Removes comments from schema pairs whose paths start with the given prefix.
   *
   * @param path the path prefix, or {@code null}/empty to strip all comments
   */
  public void stripComments(final String path) {
    if (path == null || path.isEmpty()) {
      stripComments();
      return;
    }
    for (Map.Entry<String, SchemaPair<?>> entry : entrySetDeep()) {
      if (entry.getKey().startsWith(path)) {
        entry.getValue().setComment(null);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 31 * (31 + type.hashCode()) + ((filePath == null) ? 0 : filePath.hashCode());
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
    final AbstractConfigData other = (AbstractConfigData) obj;
    if (filePath == null && other.filePath != null) {
      return false;
    }
    return type == other.type && filePath.equals(other.filePath);
  }
}