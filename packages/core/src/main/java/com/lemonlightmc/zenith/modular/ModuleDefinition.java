package com.lemonlightmc.zenith.modular;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public record ModuleDefinition<T extends Module>(String key, String name, String prefix, String[] depends,
    String[] softDepends,
    ModuleFactory<T> factory, Supplier<LoadCondition> condition) {

  public static <T extends Module> ModuleDefinitionBuilder<T> builder() {
    return new ModuleDefinitionBuilder<>();
  }

  public static <T extends Module> ModuleDefinitionBuilder<T> builder(String key, final String name,
      final ModuleFactory<T> factory) {
    return new ModuleDefinitionBuilder<>(key, name, factory);
  }

  public static <T extends Module> ModuleDefinition<T> of(String key, final String name, final String prefix,
      final String[] depends,
      final String[] softDepends,
      final ModuleFactory<T> factory, final Supplier<LoadCondition> condition) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("ModuleDefinition Key cannot be null or empty!");
    }
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("ModuleDefinition Name cannot be null or empty!");
    }
    if (factory == null) {
      throw new IllegalArgumentException("ModuleDefinition Factory cannot be null!");
    }
    key = key.toLowerCase(Locale.ROOT);
    return new ModuleDefinition<>(
        key, name,
        prefix == null || prefix.isEmpty() ? defaultPrefix(
            key) : prefix,
        depends,
        softDepends, factory, condition == null ? LoadCondition::success : condition);
  }

  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String prefix,
      final ModuleFactory<T> factory, final Supplier<LoadCondition> condition) {
    return of(key, name, prefix, null, null, factory, condition);
  }

  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String prefix,
      final ModuleFactory<T> factory) {
    return of(key, name, prefix, null, null, factory, LoadCondition::success);
  }

  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String[] depends,
      final String[] softDepends, final ModuleFactory<T> factory, final Supplier<LoadCondition> condition) {
    return of(key, name, null, depends, softDepends, factory, condition);
  }

  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String[] depends,
      final String[] softDepends, final ModuleFactory<T> factory) {
    return of(key, name, null, depends, softDepends, factory, LoadCondition::success);
  }

  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name,
      final ModuleFactory<T> factory,
      final Supplier<LoadCondition> condition) {
    return of(key, name, null, null, null, factory, condition);
  }

  public static <T extends Module> ModuleDefinition<T> of(String key, final String name,
      final ModuleFactory<T> factory) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("ModuleDefinition ID cannot be null or empty!");
    }
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("ModuleDefinition Name cannot be null or empty!");
    }
    if (factory == null) {
      throw new IllegalArgumentException("ModuleDefinition Factory cannot be null!");
    }
    key = key.toLowerCase(Locale.ROOT);

    return new ModuleDefinition<>(key, name, defaultPrefix(key), null,
        null, factory, LoadCondition::success);
  }

  private static String defaultPrefix(final String key) {
    return "<gradient:#FFAA00:#FF8833:#FF5500> &b" + key.toUpperCase(Locale.ROOT) + "<dark_gray> » ";
  }

  public static class ModuleDefinitionBuilder<T extends Module> {
    private String key;
    private String name;
    private String prefix;
    private String[] depends;
    private String[] softDepends;
    private ModuleFactory<T> factory;
    private Supplier<LoadCondition> condition;

    public ModuleDefinitionBuilder() {
    }

    public ModuleDefinitionBuilder(final String key, final String name, final ModuleFactory<T> factory) {
      this.key = key;
      this.name = name;
      this.factory = factory;
    }

    public ModuleDefinitionBuilder<T> key(final String key) {
      this.key = key;
      return this;
    }

    public ModuleDefinitionBuilder<T> name(final String name) {
      this.name = name;
      return this;
    }

    public ModuleDefinitionBuilder<T> prefix(final String prefix) {
      this.prefix = prefix;
      return this;
    }

    public ModuleDefinitionBuilder<T> depends(final List<String> depends) {
      this.depends = depends.toArray(String[]::new);
      return this;
    }

    public ModuleDefinitionBuilder<T> depends(final String... depends) {
      this.depends = depends;
      return this;
    }

    public ModuleDefinitionBuilder<T> softDepends(final List<String> softDepends) {
      this.softDepends = softDepends.toArray(String[]::new);
      return this;
    }

    public ModuleDefinitionBuilder<T> softDepends(final String... softDepends) {
      this.softDepends = softDepends;
      return this;
    }

    public ModuleDefinitionBuilder<T> factory(final ModuleFactory<T> factory) {
      this.factory = factory;
      return this;
    }

    public ModuleDefinitionBuilder<T> condition(final Supplier<LoadCondition> condition) {
      this.condition = condition;
      return this;
    }

    public ModuleDefinition<T> build() {
      if (key == null || key.isEmpty()) {
        throw new IllegalArgumentException("ModuleDefinition ID cannot be null or empty!");
      }
      if (name == null || name.isEmpty()) {
        throw new IllegalArgumentException("ModuleDefinition Name cannot be null or empty!");
      }
      if (prefix == null || prefix.isEmpty()) {
        throw new IllegalArgumentException("ModuleDefinition Prefix cannot be null or empty!");
      }
      if (factory == null) {
        throw new IllegalArgumentException("ModuleDefinition Factory cannot be null!");
      }
      if (condition == null) {
        throw new IllegalArgumentException("ModuleDefinition Condition cannot be null!");
      }
      return new ModuleDefinition<>(key, name, prefix, softDepends, depends, factory, condition);
    }
  }
}
