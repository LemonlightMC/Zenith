package com.lemonlightmc.zenith.config.handlers;

import java.io.File;
import java.util.function.Function;

import com.lemonlightmc.zenith.config.FileHandler;

public enum ConfigHandlerType {
  YAML(YamlHandler::new, new String[] { "yaml", "yml" }),
  JSON(null, new String[] { "json" }),
  TOML(null, new String[] { "toml", "tml" }),
  PROPERTIES(null, new String[] { "properties" }),
  CONF(null, new String[] { "conf" });

  private final String[] fileExt;
  private final Function<ConfigOptions, ? extends FileHandler> factory;

  private ConfigHandlerType(final Function<ConfigOptions, ? extends FileHandler> factory, final String[] ext) {
    this.factory = factory;
    this.fileExt = ext;
  }

  public String[] getFileExtension() {
    return fileExt;
  }

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

  public FileHandler create() {
    return factory.apply(null);
  }

  public FileHandler create(final ConfigOptions options) {
    return factory.apply(options);
  }

  public static FileHandler create(final File path) {
    return create(path, null);
  }

  public static FileHandler create(final File path, final ConfigOptions options) {
    ConfigHandlerType type = detect(path);
    return type == null ? null : type.create(options);
  }

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
