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
 * Defines read and write access to configuration data stored as schema-backed values.
 */
public interface IConfigData {
  /**
   * Returns the handler type associated with this configuration.
   *
   * @return the configuration handler type
   */
  ConfigHandlerType getType();

  /**
   * Returns the file path backing this configuration.
   *
   * @return the configuration file path, or {@code null} if none is set
   */
  Path getFilePath();

  /**
   * Returns the file name of this configuration.
   *
   * @return the configuration file name, or {@code null} if unavailable
   */
  String getFileName();

  /**
   * Checks whether this configuration contains no entries.
   *
   * @return {@code true} if the configuration is empty; {@code false} otherwise
   */
  boolean isEmpty();

  /**
   * Returns the number of stored entries.
   *
   * @return the entry count
   */
  int size();

  /**
   * Checks whether a value exists at the given path.
   *
   * @param path the configuration path
   * @return {@code true} if the path is present; {@code false} otherwise
   */
  boolean containsKey(String path);

  /**
   * Checks whether the given schema pair value is present.
   *
   * @param value the schema pair value to check
   * @return {@code true} if the value is present; {@code false} otherwise
   */
  boolean containsValue(SchemaPair<?> value);

  /**
   * Sets the schema pair at the given path.
   *
   * @param path the configuration path
   * @param value the schema pair value to store
   */
  void set(String path, SchemaPair<?> value);

  /**
   * Removes the value at the given path.
   *
   * @param path the configuration path
   */
  void remove(String path);

  /**
   * Removes all stored entries.
   */
  void clear();

  /**
   * Returns the top-level keys in this configuration.
   *
   * @return a set of top-level keys
   */
  Set<String> keySet();

  /**
   * Returns all keys, including nested paths.
   *
   * @return a set of all keys
   */
  Set<String> keySetDeep();

  /**
   * Returns all entries, including nested paths.
   *
   * @return a set of all entries
   */
  Set<Entry<String, SchemaPair<?>>> entrySetDeep();

  /**
   * Returns the top-level entries in this configuration.
   *
   * @return a set of top-level entries
   */
  Set<Entry<String, SchemaPair<?>>> entrySet();

  /**
   * Checks whether the value at the given path is a string.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a string; {@code false} otherwise
   */
  boolean isString(String path);

  /**
   * Checks whether the value at the given path is an integer.
   *
   * @param path the configuration path
   * @return {@code true} if the value is an integer; {@code false} otherwise
   */
  boolean isInt(String path);

  /**
   * Checks whether the value at the given path is a boolean.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a boolean; {@code false} otherwise
   */
  boolean isBoolean(String path);

  /**
   * Checks whether the value at the given path is a double.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a double; {@code false} otherwise
   */
  boolean isDouble(String path);

  /**
   * Checks whether the value at the given path is a long.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a long; {@code false} otherwise
   */
  boolean isLong(String path);

  /**
   * Checks whether the value at the given path is a float.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a float; {@code false} otherwise
   */
  boolean isFloat(String path);

  /**
   * Checks whether the value at the given path is a list.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a list; {@code false} otherwise
   */
  boolean isList(String path);

  /**
   * Checks whether the value at the given path is a map.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a map; {@code false} otherwise
   */
  boolean isMap(String path);

  /**
   * Checks whether the value at the given path is a section.
   *
   * @param path the configuration path
   * @return {@code true} if the value is a section; {@code false} otherwise
   */
  boolean isSection(String path);

  /**
   * Returns a section view for the given path.
   *
   * @param path the configuration path
   * @return the section view for the path
   */
  ConfigSection getSection(String path);

  /**
   * Returns the schema pair stored at the given path.
   *
   * @param path the configuration path
   * @return the schema pair, or {@code null} if not present
   */
  SchemaPair<?> get(String path);

  /**
   * Returns the string value at the given path.
   *
   * @param path the configuration path
   * @return the string value, or {@code null} if unavailable
   */
  String getString(String path);

  /**
   * Returns the integer value at the given path.
   *
   * @param path the configuration path
   * @return the integer value, or {@code null} if unavailable
   */
  Integer getInt(String path);

  /**
   * Returns the boolean value at the given path.
   *
   * @param path the configuration path
   * @return the boolean value, or {@code null} if unavailable
   */
  Boolean getBoolean(String path);

  /**
   * Returns the double value at the given path.
   *
   * @param path the configuration path
   * @return the double value, or {@code null} if unavailable
   */
  Double getDouble(String path);

  /**
   * Returns the long value at the given path.
   *
   * @param path the configuration path
   * @return the long value, or {@code null} if unavailable
   */
  Long getLong(String path);

  /**
   * Returns the float value at the given path.
   *
   * @param path the configuration path
   * @return the float value, or {@code null} if unavailable
   */
  Float getFloat(String path);

  /**
   * Returns the list value at the given path.
   *
   * @param path the configuration path
   * @return the list value, or {@code null} if unavailable
   */
  List<?> getList(String path);

  /**
   * Returns the string list at the given path.
   *
   * @param path the configuration path
   * @return the string list, or {@code null} if unavailable
   */
  List<String> getStringList(String path);

  /**
   * Returns the integer list at the given path.
   *
   * @param path the configuration path
   * @return the integer list, or {@code null} if unavailable
   */
  List<Integer> getIntegerList(String path);

  /**
   * Returns the boolean list at the given path.
   *
   * @param path the configuration path
   * @return the boolean list, or {@code null} if unavailable
   */
  List<Boolean> getBooleanList(String path);

  /**
   * Returns the double list at the given path.
   *
   * @param path the configuration path
   * @return the double list, or {@code null} if unavailable
   */
  List<Double> getDoubleList(String path);

  /**
   * Returns the float list at the given path.
   *
   * @param path the configuration path
   * @return the float list, or {@code null} if unavailable
   */
  List<Float> getFloatList(String path);

  /**
   * Returns the long list at the given path.
   *
   * @param path the configuration path
   * @return the long list, or {@code null} if unavailable
   */
  List<Long> getLongList(String path);

  /**
   * Returns the byte list at the given path.
   *
   * @param path the configuration path
   * @return the byte list, or {@code null} if unavailable
   */
  List<Byte> getByteList(String path);

  /**
   * Returns the character list at the given path.
   *
   * @param path the configuration path
   * @return the character list, or {@code null} if unavailable
   */
  List<Character> getCharacterList(String path);

  /**
   * Returns the short list at the given path.
   *
   * @param path the configuration path
   * @return the short list, or {@code null} if unavailable
   */
  List<Short> getShortList(String path);

  /**
   * Returns the map list at the given path.
   *
   * @param path the configuration path
   * @return the map list, or {@code null} if unavailable
   */
  List<Map<?, ?>> getMapList(String path);

  /**
   * Returns the map value at the given path.
   *
   * @param path the configuration path
   * @return the map value, or {@code null} if unavailable
   */
  Map<?, ?> getMap(String path);

  /**
   * Returns the map value at the given path as a string-keyed object map.
   *
   * @param path the configuration path
   * @return the object map, or {@code null} if unavailable
   */
  Map<String, Object> getObjectMap(String path);

  /**
   * Returns the object value at the given path cast to the requested type.
   *
   * @param <T> the target object type
   * @param path the configuration path
   * @param clazz the class to cast the value to
   * @return the typed object value, or {@code null} if unavailable
   */
  <T> T getObject(String path, Class<T> clazz);

  /**
   * Returns the serializable object at the given path.
   *
   * @param <T> the serializable type
   * @param path the configuration path
   * @param clazz the serializable class to cast the value to
   * @return the serializable object, or {@code null} if unavailable
   */
  <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz);
}