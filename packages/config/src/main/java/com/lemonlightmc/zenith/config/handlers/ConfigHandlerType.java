package com.lemonlightmc.zenith.config.handlers;

import java.io.File;
import java.util.function.Function;

import com.lemonlightmc.zenith.config.FileHandler;

/**
 * Identifies supported configuration file formats and their associated handlers.
 */
public enum ConfigHandlerType {
  /** YAML configuration format. */
  YAML(YamlHandler::new, new String[] { "yaml", "yml" }),
  /** JSON configuration format. */
  JSON(null, new String[] { "json" }),
  /** TOML configuration format. */
  TOML(null, new String[] { "toml", "tml" }),
  /** Java properties configuration format. */
  PROPERTIES(null, new String[] { "properties" }),
  /** Generic configuration format. */
  CONF(null, new String[] { "conf" });

  private final String[] fileExt;
  private final Function<ConfigOptions, ? extends FileHandler> factory;

  private ConfigHandlerType(final Function<ConfigOptions, ? extends FileHandler> factory, final String[] ext) {
    this.factory = factory;
    this.fileExt = ext;
  }

  /**
   * Returns the file extensions associated with this handler type.
   *
   * @return the supported file extensions
   */
  public String[] getFileExtension() {
    return fileExt;
  }

  /**
   * Checks whether this handler type applies to the given file.
   *
   * @param path the file to inspect
   * @return {@code true} if the file extension matches this handler type; {@code false} otherwise
   */
  public boolean isApplicable(final File path) {
    if (path == null) {
      return false;
    }
    for (final String ext : fileExt) {
      if (path.getPath().endsWith(ext)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates a new handler instance using default options.
   *
   * @return a new {@link FileHandler} instance
   */
  public FileHandler create() {
    return factory.apply(null);
  }

  /**
   * Creates a new handler instance using the given options.
   *
   * @param options the handler options to apply
   * @return a new {@link FileHandler} instance
   */
  public FileHandler create(final ConfigOptions options) {
    return factory.apply(options);
  }

  /**
   * Creates a handler for the given file using default options.
   *
   * @param path the file to create a handler for
   * @return a matching {@link FileHandler}, or {@code null} if no handler type is detected
   */
  public static FileHandler create(final File path) {
    return create(path, null);
  }

  /**
   * Creates a handler for the given file using the provided options.
   *
   * @param path the file to create a handler for
   * @param options the handler options to apply
   * @return a matching {@link FileHandler}, or {@code null} if no handler type is detected
   */
  public static FileHandler create(final File path, final ConfigOptions options) {
    ConfigHandlerType type = detect(path);
    return type == null ? null : type.create(options);
  }

  /**
   * Detects the handler type for the given file.
   *
   * @param path the file to inspect
   * @return the detected handler type, or {@code null} if none matches
   */
  public static ConfigHandlerType detect(final File path) {
    if (path == null) {
      return null;
    }
    for (final ConfigHandlerType handler : values()) {
      if (handler.isApplicable(path)) {
        return handler;
      }
    }
    return null;
  }
}
