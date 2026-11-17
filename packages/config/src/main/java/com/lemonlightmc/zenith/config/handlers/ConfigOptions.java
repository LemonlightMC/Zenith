package com.lemonlightmc.zenith.config.handlers;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.lemonlightmc.zenith.config.IConfigData;

/**
 * Extends {@link HandlerOptions} with configuration lifecycle and callback settings.
 */
public class ConfigOptions extends HandlerOptions {
  /** The separator character used for nested configuration paths. */
  protected char pathSeparator = '.';
  /** Whether missing default configuration files should be created automatically. */
  protected boolean createDefaults = true;

  /** Whether configurations should be loaded automatically. */
  protected boolean autoLoad = true;
  /** Whether configurations should be migrated automatically. */
  protected boolean autoMigrate = true;
  /** Whether configurations should be reloaded automatically. */
  protected boolean autoReload = true;
  /** Whether configurations should be saved automatically. */
  protected boolean autoSave = true;

  /** Callback invoked after a configuration is loaded. */
  protected Function<IConfigData, IConfigData> onLoad = null;
  /** Callback invoked after a configuration is migrated. */
  protected Function<IConfigData, IConfigData> onMigrate = null;
  /** Callback invoked after a configuration is reloaded. */
  protected Function<IConfigData, IConfigData> onReload = null;
  /** Callback invoked before or during a configuration save operation. */
  protected Consumer<IConfigData> onSave = null;

  /**
   * Creates configuration options with default values.
   */
  public ConfigOptions() {
  }

  /**
   * Creates configuration options copied from another instance.
   *
   * @param options the options to copy
   */
  public ConfigOptions(final ConfigOptions options) {
    super(options);
    this.pathSeparator = options.pathSeparator;
    this.createDefaults = options.createDefaults;

    this.autoMigrate = options.autoMigrate;
    this.autoLoad = options.autoLoad;
    this.autoReload = options.autoReload;
    this.autoSave = options.autoSave;

    this.onLoad = options.onLoad;
    this.onMigrate = options.onMigrate;
    this.onReload = options.onReload;
    this.onSave = options.onSave;
  }

  /**
   * Creates a new configuration options instance with default values.
   *
   * @return a new {@link ConfigOptions} instance
   */
  public static ConfigOptions create() {
    return new ConfigOptions();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions clone() {
    return new ConfigOptions(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String header() {
    return header;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions header(final List<String> header) {
    super.header(header);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions header(final String header) {
    super.header(header);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions footer(final List<String> footer) {
    super.footer(footer);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions footer(final String footer) {
    super.footer(footer);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String footer() {
    return footer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean parseComments() {
    return parseComments;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions parseComments(final boolean parseComments) {
    this.parseComments = parseComments;
    return this;
  }

  /**
   * Returns whether missing default configuration files should be created automatically.
   *
   * @return {@code true} if defaults should be created; {@code false} otherwise
   */
  public boolean createDefaults() {
    return createDefaults;
  }

  /**
   * Sets whether missing default configuration files should be created automatically.
   *
   * @param value {@code true} to create defaults; {@code false} otherwise
   * @return this options instance for chaining
   */
  public ConfigOptions createDefaults(final boolean value) {
    this.createDefaults = value;
    return this;
  }

  /**
   * Returns the separator character used for nested configuration paths.
   *
   * @return the path separator character
   */
  public char pathSeparator() {
    return pathSeparator;
  }

  /**
   * Sets the separator character used for nested configuration paths.
   *
   * @param value the path separator character
   * @return this options instance for chaining
   */
  public ConfigOptions pathSeparator(final char value) {
    this.pathSeparator = value;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean convertTabs() {
    return convertTabs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions convertTabs(final boolean value) {
    this.convertTabs = value;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int tabSpaces() {
    return tabSpaces;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions tabSpaces(final int value) {
    super.tabSpaces(value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean replaceQuotes() {
    return replaceQuotes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions replaceQuotes(final boolean value) {
    this.replaceQuotes = value;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean preferDoubleQuotes() {
    return preferDoubleQuotes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConfigOptions preferDoubleQuotes(final boolean value) {
    this.preferDoubleQuotes = value;
    return this;
  }

  /**
   * Returns whether configurations should be migrated automatically.
   *
   * @return {@code true} if automatic migration is enabled; {@code false} otherwise
   */
  public boolean autoMigrate() {
    return autoMigrate;
  }

  /**
   * Sets whether configurations should be migrated automatically.
   *
   * @param value {@code true} to enable automatic migration; {@code false} otherwise
   * @return this options instance for chaining
   */
  public ConfigOptions autoMigrate(final boolean value) {
    this.autoMigrate = value;
    return this;
  }

  /**
   * Returns whether configurations should be loaded automatically.
   *
   * @return {@code true} if automatic loading is enabled; {@code false} otherwise
   */
  public boolean autoLoad() {
    return autoLoad;
  }

  /**
   * Sets whether configurations should be loaded automatically.
   *
   * @param value {@code true} to enable automatic loading; {@code false} otherwise
   * @return this options instance for chaining
   */
  public ConfigOptions autoLoad(final boolean value) {
    this.autoLoad = value;
    return this;
  }

  /**
   * Returns whether configurations should be reloaded automatically.
   *
   * @return {@code true} if automatic reloading is enabled; {@code false} otherwise
   */
  public boolean autoReload() {
    return autoReload;
  }

  /**
   * Sets whether configurations should be reloaded automatically.
   *
   * @param value {@code true} to enable automatic reloading; {@code false} otherwise
   * @return this options instance for chaining
   */
  public ConfigOptions autoReload(final boolean value) {
    this.autoReload = value;
    return this;
  }

  /**
   * Returns whether configurations should be saved automatically.
   *
   * @return {@code true} if automatic saving is enabled; {@code false} otherwise
   */
  public boolean autoSave() {
    return autoSave;
  }

  /**
   * Sets whether configurations should be saved automatically.
   *
   * @param value {@code true} to enable automatic saving; {@code false} otherwise
   * @return this options instance for chaining
   */
  public ConfigOptions autoSave(final boolean value) {
    this.autoSave = value;
    return this;
  }

  /**
   * Returns the callback invoked after a configuration is loaded.
   *
   * @return the load callback, or {@code null} if none is configured
   */
  public Function<IConfigData, IConfigData> onLoad() {
    return onLoad;
  }

  /**
   * Sets the callback invoked after a configuration is loaded.
   *
   * @param consumer the callback to invoke
   * @return this options instance for chaining
   */
  public ConfigOptions onLoad(final Consumer<IConfigData> consumer) {
    this.onLoad = data -> {
      consumer.accept(data);
      return data;
    };
    return this;
  }

  /**
   * Sets the callback invoked after a configuration is loaded.
   *
   * @param function the callback to invoke
   * @return this options instance for chaining
   */
  public ConfigOptions onLoad(final Function<IConfigData, IConfigData> function) {
    this.onLoad = function;
    return this;
  }

  /**
   * Returns the callback invoked after a configuration is migrated.
   *
   * @return the migration callback, or {@code null} if none is configured
   */
  public Function<IConfigData, IConfigData> onMigrate() {
    return onMigrate;
  }

  /**
   * Sets the callback invoked after a configuration is migrated.
   *
   * @param consumer the callback to invoke
   * @return this options instance for chaining
   */
  public ConfigOptions onMigrate(final Consumer<IConfigData> consumer) {
    this.onMigrate = data -> {
      consumer.accept(data);
      return data;
    };
    return this;
  }

  /**
   * Sets the callback invoked after a configuration is migrated.
   *
   * @param function the callback to invoke
   * @return this options instance for chaining
   */
  public ConfigOptions onMigrate(final Function<IConfigData, IConfigData> function) {
    this.onMigrate = function;
    return this;
  }

  /**
   * Returns the callback invoked after a configuration is reloaded.
   *
   * @return the reload callback, or {@code null} if none is configured
   */
  public Function<IConfigData, IConfigData> onReload() {
    return onReload;
  }

  /**
   * Sets the callback invoked after a configuration is reloaded.
   *
   * @param consumer the callback to invoke
   * @return this options instance for chaining
   */
  public ConfigOptions onReload(final Consumer<IConfigData> consumer) {
    this.onReload = data -> {
      consumer.accept(data);
      return data;
    };
    return this;
  }

  /**
   * Sets the callback invoked after a configuration is reloaded.
   *
   * @param function the callback to invoke
   * @return this options instance for chaining
   */
  public ConfigOptions onReload(final Function<IConfigData, IConfigData> function) {
    this.onReload = function;
    return this;
  }

  /**
   * Returns the callback invoked during a configuration save operation.
   *
   * @return the save callback, or {@code null} if none is configured
   */
  public Consumer<IConfigData> onSave() {
    return onSave;
  }

  /**
   * Sets the callback invoked during a configuration save operation.
   *
   * @param consumer the callback to invoke
   * @return this options instance for chaining
   */
  public ConfigOptions onSave(final Consumer<IConfigData> consumer) {
    this.onSave = consumer;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 31 * super.hashCode() + pathSeparator;
    result = 31 * result + (createDefaults ? 1231 : 1237);
    result = 31 * result + (autoLoad ? 1231 : 1237);
    result = 31 * result + (autoMigrate ? 1231 : 1237);
    result = 31 * result + (autoReload ? 1231 : 1237);
    result = 31 * result + (autoSave ? 1231 : 1237);
    result = 31 * result + ((onLoad == null) ? 0 : onLoad.hashCode());
    result = 31 * result + ((onMigrate == null) ? 0 : onMigrate.hashCode());
    result = 31 * result + ((onReload == null) ? 0 : onReload.hashCode());
    result = 31 * result + ((onSave == null) ? 0 : onSave.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    final ConfigOptions other = (ConfigOptions) obj;
    return pathSeparator == other.pathSeparator
        && createDefaults == other.createDefaults
        && autoLoad == other.autoLoad
        && autoMigrate == other.autoMigrate
        && autoReload == other.autoReload
        && autoSave == other.autoSave
        && Objects.equals(onLoad, other.onLoad)
        && Objects.equals(onMigrate, other.onMigrate)
        && Objects.equals(onReload, other.onReload)
        && Objects.equals(onSave, other.onSave);
  }

}
