package com.lemonlightmc.zenith.config;

import java.io.File;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lemonlightmc.zenith.additive.files.ResourceUtils;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.config.handlers.ConfigHandlerType;
import com.lemonlightmc.zenith.config.handlers.ConfigOptions;
import com.lemonlightmc.zenith.config.handlers.YamlHandler;
import com.lemonlightmc.zenith.config.schema.BuildSchema;
import com.lemonlightmc.zenith.exceptions.ConfigHandlingException;

/**
 * Manages configuration registration, loading, saving, and reloading for the application.
 *
 * <p>Configurations are identified by name and backed by a {@link FileHandler} appropriate for their format.
 */
public class Configurate {

  private final File folder;
  private static Map<String, ConfigData> configs = new ConcurrentHashMap<>();
  private static EnumMap<ConfigHandlerType, FileHandler> handlers;
  private static final ConfigOptions options = new ConfigOptions();

  /**
   * Creates a new configurate instance rooted at the given folder.
   *
   * @param folder the folder used to resolve configuration files
   */
  public Configurate(final File folder) {
    this.folder = folder;
  }

  /**
   * Returns the global configuration options used by this configurate system.
   *
   * @return the global {@link ConfigOptions} instance
   */
  public static ConfigOptions options() {
    return options;
  }

  /**
   * Returns the folder backing this configurate instance.
   *
   * @return the configuration folder
   */
  public File getConfigFolder() {
    return folder;
  }

  /**
   * Returns the default configuration resource files for this folder.
   *
   * @return a list of default configuration resource files
   */
  public List<File> getDefaultConfigs() {
    return ResourceUtils.getResourcesFiles(folder.getPath());
  }

  /**
   * Creates missing default configuration files for all registered configurations.
   *
   * @throws ConfigHandlingException if a configuration handler fails to create defaults
   */
  public static void createDefaults() {
    try {
      for (final ConfigData data : configs.values()) {
        getHandler(data).createIfNotExists(data.filePath);
      }
    } catch (final ConfigHandlingException e) {
      throw e;
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to create default Configs");
      e.printStackTrace();
    }
  }

  /**
   * Returns the registered configuration with the given name.
   *
   * @param name the configuration name
   * @return the matching {@link ConfigData}, or {@code null} if not found or the name is invalid
   */
  public static ConfigData getConfig(final String name) {
    return name == null || name.isEmpty() ? null : configs.get(name);
  }

  /**
   * Checks whether a configuration with the given name is registered.
   *
   * @param name the configuration name
   * @return {@code true} if a configuration is registered for the name; {@code false} otherwise
   */
  public static boolean hasConfig(final String name) {
    return name == null || name.isEmpty() ? false : configs.containsKey(name);
  }

  /**
   * Registers and loads a YAML configuration from the given name.
   *
   * @param name the configuration name or path string
   * @param schema the schema used to parse and validate configuration values
   * @param options the handler options to apply, or {@code null} for defaults
   * @return the loaded {@link ConfigData}
   * @throws IllegalArgumentException if the name is null or empty
   */
  public static ConfigData yaml(final String name, final BuildSchema schema, final ConfigOptions options) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Config name cannot be null or empty");
    }
    return yaml(Path.of(name), schema, options);
  }

  /**
   * Registers and loads a YAML configuration from the given path using default options.
   *
   * @param path the configuration file path
   * @param schema the schema used to parse and validate configuration values
   * @return the loaded {@link ConfigData}
   */
  public static ConfigData yaml(final Path path, final BuildSchema schema) {
    return yaml(path, schema, null);
  }

  /**
   * Registers and loads a YAML configuration from the given path.
   *
   * @param path the configuration file path
   * @param schema the schema used to parse and validate configuration values
   * @param options the handler options to apply, or {@code null} for defaults
   * @return the loaded {@link ConfigData}
   * @throws IllegalArgumentException if the path is null or a configuration with the same file name already exists
   */
  public static ConfigData yaml(final Path path, final BuildSchema schema, final ConfigOptions options) {
    if (path == null) {
      throw new IllegalArgumentException("Config path cannot be null");
    }
    if (configs.containsKey(path.getFileName().toString())) {
      throw new IllegalArgumentException("Config with name " + path.getFileName() + " already exists");
    }
    final YamlHandler handler = YamlHandler.from(options);
    handlers.put(ConfigHandlerType.YAML, handler);
    final ConfigData data = new ConfigData(schema, path);
    handler.load(path, data);
    configs.put(path.getFileName().toString(), data);
    return data;
  }

  /**
   * Returns the file handler associated with the registered configuration name.
   *
   * @param name the configuration name
   * @return the associated {@link FileHandler}, or {@code null} if unavailable
   */
  public static FileHandler getHandler(final String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    final ConfigData data = configs.get(name);
    return data == null ? null : getHandler(data.type);
  }

  private static FileHandler getHandler(final ConfigData data) {
    return data == null ? null : getHandler(data.type);
  }

  private static FileHandler getHandler(final ConfigHandlerType type) {
    FileHandler handler = handlers.get(type);
    if (handler == null) {
      handler = type.create();
      handlers.put(type, handler);
    }
    return handler;
  }

  /**
   * Reloads all registered configurations from disk.
   */
  public static void reloadAll() {
    for (final ConfigData data : configs.values()) {
      getHandler(data.type).load(data.filePath, data);
    }
  }

  /**
   * Loads all registered configurations from disk.
   */
  public static void loadAll() {
    for (final ConfigData data : configs.values()) {
      getHandler(data.type).load(data.filePath, data);
    }
  }

  /**
   * Saves all registered configurations to disk.
   */
  public static void saveAll() {
    for (final ConfigData data : configs.values()) {
      getHandler(data.type).save(data.filePath, data);
    }
  }

  /**
   * Reloads the registered configuration with the given name.
   *
   * @param name the configuration name
   */
  public static void reload(final String name) {
    final ConfigData data = getConfig(name);
    if (data == null) {
      return;
    }
    getHandler(data.type).reload(data);
  }

  /**
   * Reloads the given configuration from its current file path.
   *
   * @param data the configuration data to reload
   */
  public static void reload(final ConfigData data) {
    if (data == null) {
      return;
    }
    getHandler(data.type).reload(data);
  }

  /**
   * Loads the registered configuration with the given name.
   *
   * @param name the configuration name
   */
  public static void load(final String name) {
    final ConfigData data = getConfig(name);
    if (data == null) {
      return;
    }
    getHandler(data.type).load(data);
  }

  /**
   * Loads the given configuration from its current file path.
   *
   * @param data the configuration data to load
   */
  public static void load(final ConfigData data) {
    if (data == null) {
      return;
    }
    getHandler(data.type).load(data);
  }

  /**
   * Loads the given configuration from the specified path.
   *
   * @param path the path to load from, or {@code null} to use the configuration's current path
   * @param data the configuration data to populate
   */
  public static void load(final Path path, final ConfigData data) {
    if (data == null) {
      return;
    }
    getHandler(data.type).load(path == null ? data.getFilePath() : path, data);
  }

  /**
   * Saves the registered configuration with the given name.
   *
   * @param name the configuration name
   */
  public static void save(final String name) {
    final ConfigData data = getConfig(name);
    if (data == null) {
      return;
    }
    getHandler(data.type).save(data);
  }

  /**
   * Saves the given configuration to its current file path.
   *
   * @param data the configuration data to save
   */
  public static void save(final ConfigData data) {
    if (data == null) {
      return;
    }
    getHandler(data.type).save(data);
  }

  /**
   * Saves the given configuration to the specified path.
   *
   * @param path the path to save to, or {@code null} to use the configuration's current path
   * @param data the configuration data to save
   */
  public static void save(final Path path, final ConfigData data) {
    if (data == null) {
      return;
    }
    getHandler(data.type).save(path == null ? data.getFilePath() : path, data);
  }
}
