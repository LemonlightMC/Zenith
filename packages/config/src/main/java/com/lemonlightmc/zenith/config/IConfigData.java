package com.lemonlightmc.zenith.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.lemonlightmc.zenith.config.handlers.ConfigHandlerType;
import com.lemonlightmc.zenith.config.schema.SchemaPair;

public interface IConfigData {
  public ConfigHandlerType getType();

  Path getFilePath();

  String getFileName();

  boolean isEmpty();

  int size();

  boolean containsKey(String path);

  boolean containsValue(SchemaPair<?> value);

  void set(String path, SchemaPair<?> value);

  void remove(String path);

  void clear();

  Set<String> keySet();

  Set<String> keySetDeep();

  Set<Entry<String, SchemaPair<?>>> entrySetDeep();

  Set<Entry<String, SchemaPair<?>>> entrySet();

  boolean isString(String path);

  boolean isInt(String path);

  boolean isBoolean(String path);

  boolean isDouble(String path);

  boolean isLong(String path);

  boolean isFloat(String path);

  boolean isList(String path);

  boolean isMap(String path);

  boolean isSection(String path);

  ConfigSection getSection(String path);

  SchemaPair<?> get(String path);

  String getString(String path);

  Integer getInt(String path);

  Boolean getBoolean(String path);

  Double getDouble(String path);

  Long getLong(String path);

  Float getFloat(String path);

  List<?> getList(String path);

  List<String> getStringList(String path);

  List<Integer> getIntegerList(String path);

  List<Boolean> getBooleanList(String path);

  List<Double> getDoubleList(String path);

  List<Float> getFloatList(String path);

  List<Long> getLongList(String path);

  List<Byte> getByteList(String path);

  List<Character> getCharacterList(String path);

  List<Short> getShortList(String path);

  List<Map<?, ?>> getMapList(String path);

  Map<?, ?> getMap(String path);

  Map<String, Object> getObjectMap(String path);

  <T> T getObject(String path, Class<T> clazz);

  <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz);
}