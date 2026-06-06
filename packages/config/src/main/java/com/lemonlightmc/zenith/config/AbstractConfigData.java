
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

public abstract class AbstractConfigData {

  protected ConfigHandlerType type;
  protected Path filePath;
  protected String fileName;
  protected BuildSchema schema;

  public AbstractConfigData(final BuildSchema schema, final ConfigHandlerType type, final Path filePath) {
    this.type = type;
    this.filePath = filePath;
    final Path fileName = filePath == null ? null : filePath.getFileName();
    this.fileName = fileName == null ? null : fileName.toString();
    this.schema = schema;
  }

  public AbstractConfigData(final BuildSchema schema, final ConfigHandlerType type) {
    this(schema, type, null);
  }

  public ConfigHandlerType getType() {
    return type;
  }

  public Path getFilePath() {
    return filePath;
  }

  public String getFileName() {
    return fileName;
  }

  public BuildSchema getSchema() {
    return schema;
  }

  public abstract boolean isEmpty();

  public abstract int size();

  public abstract boolean containsKey(String path);

  public abstract boolean containsValue(SchemaPair<?> value);

  public abstract void set(String path, SchemaPair<?> value);

  public abstract void remove(String path);

  public abstract void clear();

  public abstract Set<String> keySetDeep();

  public abstract Set<String> keySet();

  public abstract Set<Map.Entry<String, SchemaPair<?>>> entrySetDeep();

  public abstract Set<Map.Entry<String, SchemaPair<?>>> entrySet();

  public boolean isString(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.STRING);
  }

  public boolean isInt(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.INT);
  }

  public boolean isBoolean(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.BOOL);
  }

  public boolean isDouble(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.DOUBLE);
  }

  public boolean isLong(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.LONG);
  }

  public boolean isFloat(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.FLOAT);
  }

  public boolean isList(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.LIST);
  }

  public boolean isMap(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.MAP);
  }

  public boolean isSection(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null || obj.isType(SchemaType.SECTION);
  }

  public abstract ConfigSection getSection(String path);

  public abstract SchemaPair<?> get(String path);

  public String getString(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.STRING).orElse(null);
  }

  public Integer getInt(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.INT).orElse(null);
  }

  public Boolean getBoolean(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.BOOL).orElse(null);
  }

  public Double getDouble(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.DOUBLE).orElse(null);
  }

  public Long getLong(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.LONG).orElse(null);
  }

  public Float getFloat(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.FLOAT).orElse(null);
  }

  public List<?> getList(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.LIST).orElse(null);
  }

  public List<String> getStringList(final String path) {
    return getTypedList(path, SchemaType.STRING.parser);
  }

  public List<Integer> getIntegerList(final String path) {
    return getTypedList(path, SchemaType.INT.parser);
  }

  public List<Boolean> getBooleanList(final String path) {
    return getTypedList(path, SchemaType.BOOL.parser);
  }

  public List<Double> getDoubleList(final String path) {
    return getTypedList(path, SchemaType.DOUBLE.parser);
  }

  public List<Float> getFloatList(final String path) {
    return getTypedList(path, SchemaType.FLOAT.parser);
  }

  public List<Long> getLongList(final String path) {
    return getTypedList(path, SchemaType.LONG.parser);
  }

  public List<Byte> getByteList(final String path) {
    return getTypedList(path, SchemaType.BYTE.parser);
  }

  public List<Character> getCharacterList(final String path) {
    return getTypedList(path, SchemaType.CHAR.parser);
  }

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

  public Map<?, ?> getMap(final String path) {
    final SchemaPair<?> obj = get(path);
    return obj == null ? null : obj.toType(SchemaType.MAP).orElse(null);
  }

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

  public <T extends ConfigurationSerializable> T getSerializable(final String path, final Class<T> clazz) {
    return getObject(path, clazz);
  }

  public void stripComments() {
    for (Map.Entry<String, SchemaPair<?>> entry : entrySetDeep()) {
      entry.getValue().setComment(null);
    }
  }

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

  @Override
  public int hashCode() {
    return 31 * (31 + type.hashCode()) + ((filePath == null) ? 0 : filePath.hashCode());
  }

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