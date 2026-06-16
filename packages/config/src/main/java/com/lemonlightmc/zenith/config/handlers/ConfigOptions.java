package com.lemonlightmc.zenith.config.handlers;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.lemonlightmc.zenith.config.IConfigData;

public class ConfigOptions extends HandlerOptions {
  protected char pathSeparator = '.';
  protected boolean createDefaults = true;

  protected boolean autoLoad = true;
  protected boolean autoMigrate = true;
  protected boolean autoReload = true;
  protected boolean autoSave = true;

  protected Function<IConfigData, IConfigData> onLoad = null;
  protected Function<IConfigData, IConfigData> onMigrate = null;
  protected Function<IConfigData, IConfigData> onReload = null;
  protected Consumer<IConfigData> onSave = null;

  public ConfigOptions() {
  }

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

  public static ConfigOptions create() {
    return new ConfigOptions();
  }

  @Override
  public ConfigOptions clone() {
    return new ConfigOptions(this);
  }

  @Override
  public String header() {
    return header;
  }

  @Override
  public ConfigOptions header(final List<String> header) {
    super.header(header);
    return this;
  }

  @Override
  public ConfigOptions header(final String header) {
    super.header(header);
    return this;
  }

  @Override
  public ConfigOptions footer(final List<String> footer) {
    super.footer(header);
    return this;
  }

  @Override
  public ConfigOptions footer(final String footer) {
    super.footer(header);
    return this;
  }

  @Override
  public String footer() {
    return footer;
  }

  @Override
  public boolean parseComments() {
    return parseComments;
  }

  @Override
  public ConfigOptions parseComments(final boolean parseComments) {
    this.parseComments = parseComments;
    return this;
  }

  public boolean createDefaults() {
    return createDefaults;
  }

  public ConfigOptions createDefaults(final boolean value) {
    this.createDefaults = value;
    return this;
  }

  public char pathSeparator() {
    return pathSeparator;
  }

  public ConfigOptions pathSeparator(final char value) {
    this.pathSeparator = value;
    return this;
  }

  @Override
  public boolean convertTabs() {
    return convertTabs;
  }

  @Override
  public ConfigOptions convertTabs(final boolean value) {
    this.convertTabs = value;
    return this;
  }

  @Override
  public int tabSpaces() {
    return tabSpaces;
  }

  @Override
  public ConfigOptions tabSpaces(final int value) {
    super.tabSpaces(value);
    return this;
  }

  @Override
  public boolean replaceQuotes() {
    return replaceQuotes;
  }

  @Override
  public ConfigOptions replaceQuotes(final boolean value) {
    this.replaceQuotes = value;
    return this;
  }

  @Override
  public boolean preferDoubleQuotes() {
    return preferDoubleQuotes;
  }

  @Override
  public ConfigOptions preferDoubleQuotes(final boolean value) {
    this.preferDoubleQuotes = value;
    return this;
  }

  public boolean autoMigrate() {
    return autoMigrate;
  }

  public ConfigOptions autoMigrate(final boolean value) {
    this.autoMigrate = value;
    return this;
  }

  public boolean autoLoad() {
    return autoLoad;
  }

  public ConfigOptions autoLoad(final boolean value) {
    this.autoLoad = value;
    return this;
  }

  public boolean autoReload() {
    return autoReload;
  }

  public ConfigOptions autoReload(final boolean value) {
    this.autoReload = value;
    return this;
  }

  public boolean autoSave() {
    return autoSave;
  }

  public ConfigOptions autoSave(final boolean value) {
    this.autoSave = value;
    return this;
  }

  public Function<IConfigData, IConfigData> onLoad() {
    return onLoad;
  }

  public ConfigOptions onLoad(final Consumer<IConfigData> consumer) {
    this.onLoad = data -> {
      consumer.accept(data);
      return data;
    };
    return this;
  }

  public ConfigOptions onLoad(final Function<IConfigData, IConfigData> function) {
    this.onLoad = function;
    return this;
  }

  public Function<IConfigData, IConfigData> onMigrate() {
    return onMigrate;
  }

  public ConfigOptions onMigrate(final Consumer<IConfigData> consumer) {
    this.onMigrate = data -> {
      consumer.accept(data);
      return data;
    };
    return this;
  }

  public ConfigOptions onMigrate(final Function<IConfigData, IConfigData> function) {
    this.onMigrate = function;
    return this;
  }

  public Function<IConfigData, IConfigData> onReload() {
    return onReload;
  }

  public ConfigOptions onReload(final Consumer<IConfigData> consumer) {
    this.onReload = data -> {
      consumer.accept(data);
      return data;
    };
    return this;
  }

  public ConfigOptions onReload(final Function<IConfigData, IConfigData> function) {
    this.onReload = function;
    return this;
  }

  public Consumer<IConfigData> onSave() {
    return onSave;
  }

  public ConfigOptions onSave(final Consumer<IConfigData> consumer) {
    this.onSave = consumer;
    return this;
  }

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
