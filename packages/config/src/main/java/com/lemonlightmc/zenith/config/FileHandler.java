package com.lemonlightmc.zenith.config;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import com.lemonlightmc.zenith.additive.files.FileUtils;
import com.lemonlightmc.zenith.additive.files.ResourceUtils;
import com.lemonlightmc.zenith.config.handlers.ConfigHandlerType;
import com.lemonlightmc.zenith.config.handlers.ConfigOptions;
import com.lemonlightmc.zenith.config.schema.SchemaPair;
import com.lemonlightmc.zenith.exceptions.ConfigHandlingException;

/**
 * Handles serialization, deserialization, and file I/O for a configuration format.
 */
public abstract class FileHandler {
  private ConfigOptions options;

  /**
   * Creates a new file handler with the given options.
   *
   * @param options the handler options, or {@code null} to use defaults lazily
   */
  public FileHandler(final ConfigOptions options) {
    this.options = options;
  }

  /**
   * Serializes configuration data to a string representation.
   *
   * @param data the configuration data to serialize
   * @return the serialized configuration content
   */
  public abstract String saveToString(final Map<String, SchemaPair<?>> data);

  /**
   * Parses raw configuration content into a map structure.
   *
   * @param raw the raw configuration content
   * @return the parsed configuration data as a map
   */
  public abstract Map<String, Object> loadFromString(final String raw);

  /**
   * Returns the options used by this handler, creating defaults if needed.
   *
   * @return the handler options
   */
  public ConfigOptions options() {
    if (options == null) {
      options = new ConfigOptions();
    }
    return options;
  }

  /**
   * Detects the handler type for the given file.
   *
   * @param path the file to inspect
   * @return the detected handler type, or {@code null} if none matches
   */
  public static ConfigHandlerType detect(final File path) {
    return ConfigHandlerType.detect(path);
  }

  /**
   * Creates the configuration file from bundled resources if it does not already exist.
   *
   * @param path the destination configuration file path
   */
  public void createIfNotExists(final Path path) {
    if (FileUtils.exists(path)) {
      return;
    }
    final File ressource = ResourceUtils.getResourceFile(path.toString());
    if (ressource == null) {
      return;
    }
    ResourceUtils.saveResource(ressource, path.toFile());
  }

  /**
   * Reloads the given configuration from disk.
   *
   * @param data the configuration data to reload
   * @return the reloaded configuration data, or {@code null} if the input was {@code null}
   */
  public ConfigData reload(final ConfigData data) {
    if (data == null) {
      return null;
    }
    load(data);
    return data;
  }

  /**
   * Saves the given configuration to its configured file path.
   *
   * @param data the configuration data to save
   * @throws IllegalArgumentException if the configuration data or its file path is missing
   */
  public void save(final ConfigData data) {
    if (data == null) {
      throw new IllegalArgumentException("ConfigData cannot be null");
    }
    if (data.getFilePath() == null) {
      throw new IllegalArgumentException("ConfigData file path is not set");
    }
    save(data.getFilePath(), data);
  }

  /**
   * Saves the given configuration to the specified path.
   *
   * @param path the destination file path
   * @param data the configuration data to save
   * @throws ConfigHandlingException if the configuration cannot be written
   */
  public void save(final Path path, final ConfigData data) {
    FileUtils.mkdirs(path).throwIfFailed(ConfigHandlingException.class);
    try (BufferedWriter writer = FileUtils.createWriter(path.toFile())) {
      final String str = saveToString(data.getRawData());
      writer.write(str);
    } catch (final Exception e) {
      throw new ConfigHandlingException("Failed to save Config to " + path, e);
    }
  }

  /**
   * Loads the given configuration from its configured file path.
   *
   * @param data the configuration data to populate
   */
  public void load(final ConfigData data) {
    load(data.getFilePath(), data);
  }

  /**
   * Loads the given configuration from the specified path.
   *
   * @param path the source file path
   * @param data the configuration data to populate
   * @throws IllegalArgumentException if the path or configuration data is {@code null}
   */
  public void load(final Path path, final ConfigData data) {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null");
    }
    if (data == null) {
      throw new IllegalArgumentException("ConfigData cannot be null");
    }
    final Map<String, Object> raw = loadFromString(FileUtils.readString(path));
    data.setRawData(data.getSchema().parse(raw));
  }
}
