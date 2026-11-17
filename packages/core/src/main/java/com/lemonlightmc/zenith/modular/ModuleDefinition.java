package com.lemonlightmc.zenith.modular;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Immutable definition describing how a module is identified, created, and loaded.
 *
 * @param key the unique module key
 * @param name the display name of the module
 * @param prefix the message prefix associated with the module
 * @param depends required dependency keys
 * @param softDepends optional dependency keys
 * @param factory the factory used to create module instances
 * @param condition the supplier that determines whether the module can be loaded
 * @param <T> the module type
 */
public record ModuleDefinition<T extends Module>(String key, String name, String prefix, String[] depends,
    String[] softDepends,
    ModuleFactory<T> factory, Supplier<LoadCondition> condition) {

  /**
   * Creates a new module definition builder.
   *
   * @param <T> the module type
   * @return a new builder instance
   */
  public static <T extends Module> ModuleDefinitionBuilder<T> builder() {
    return new ModuleDefinitionBuilder<>();
  }

  /**
   * Creates a new module definition builder initialized with required values.
   *
   * @param key the module key
   * @param name the module name
   * @param factory the module factory
   * @param <T> the module type
   * @return a new builder instance
   */
  public static <T extends Module> ModuleDefinitionBuilder<T> builder(String key, final String name,
      final ModuleFactory<T> factory) {
    return new ModuleDefinitionBuilder<>(key, name, factory);
  }

  /**
   * Creates a module definition with the provided values.
   *
   * @param key the module key
   * @param name the module name
   * @param prefix the module prefix, or {@code null} to use the default prefix
   * @param depends required dependency keys
   * @param softDepends optional dependency keys
   * @param factory the module factory
   * @param condition the load condition supplier, or {@code null} for a successful condition
   * @param <T> the module type
   * @return the created module definition
   * @throws IllegalArgumentException if required arguments are invalid
   */
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

  /**
   * Creates a module definition without dependencies.
   *
   * @param key the module key
   * @param name the module name
   * @param prefix the module prefix
   * @param factory the module factory
   * @param condition the load condition supplier
   * @param <T> the module type
   * @return the created module definition
   */
  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String prefix,
      final ModuleFactory<T> factory, final Supplier<LoadCondition> condition) {
    return of(key, name, prefix, null, null, factory, condition);
  }

  /**
   * Creates a module definition without dependencies and with a successful load condition.
   *
   * @param key the module key
   * @param name the module name
   * @param prefix the module prefix
   * @param factory the module factory
   * @param <T> the module type
   * @return the created module definition
   */
  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String prefix,
      final ModuleFactory<T> factory) {
    return of(key, name, prefix, null, null, factory, LoadCondition::success);
  }

  /**
   * Creates a module definition with dependencies and a custom load condition.
   *
   * @param key the module key
   * @param name the module name
   * @param depends required dependency keys
   * @param softDepends optional dependency keys
   * @param factory the module factory
   * @param condition the load condition supplier
   * @param <T> the module type
   * @return the created module definition
   */
  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String[] depends,
      final String[] softDepends, final ModuleFactory<T> factory, final Supplier<LoadCondition> condition) {
    return of(key, name, null, depends, softDepends, factory, condition);
  }

  /**
   * Creates a module definition with dependencies and a successful load condition.
   *
   * @param key the module key
   * @param name the module name
   * @param depends required dependency keys
   * @param softDepends optional dependency keys
   * @param factory the module factory
   * @param <T> the module type
   * @return the created module definition
   */
  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name, final String[] depends,
      final String[] softDepends, final ModuleFactory<T> factory) {
    return of(key, name, null, depends, softDepends, factory, LoadCondition::success);
  }

  /**
   * Creates a module definition without dependencies and with a custom load condition.
   *
   * @param key the module key
   * @param name the module name
   * @param factory the module factory
   * @param condition the load condition supplier
   * @param <T> the module type
   * @return the created module definition
   */
  public static <T extends Module> ModuleDefinition<T> of(final String key, final String name,
      final ModuleFactory<T> factory,
      final Supplier<LoadCondition> condition) {
    return of(key, name, null, null, null, factory, condition);
  }

  /**
   * Creates a module definition with default prefix, no dependencies, and a successful load condition.
   *
   * @param key the module key
   * @param name the module name
   * @param factory the module factory
   * @param <T> the module type
   * @return the created module definition
   * @throws IllegalArgumentException if required arguments are invalid
   */
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

  /**
   * Builds the default prefix used for a module key.
   *
   * @param key the module key
   * @return the default formatted prefix
   */
  private static String defaultPrefix(final String key) {
    return "<gradient:#FFAA00:#FF8833:#FF5500> &b" + key.toUpperCase(Locale.ROOT) + "<dark_gray> » ";
  }

  /**
   * Builder for creating {@link ModuleDefinition} instances.
   *
   * @param <T> the module type
   */
  public static class ModuleDefinitionBuilder<T extends Module> {
    private String key;
    private String name;
    private String prefix;
    private String[] depends;
    private String[] softDepends;
    private ModuleFactory<T> factory;
    private Supplier<LoadCondition> condition;

    /**
     * Creates an empty module definition builder.
     */
    public ModuleDefinitionBuilder() {
    }

    /**
     * Creates a builder initialized with required module values.
     *
     * @param key the module key
     * @param name the module name
     * @param factory the module factory
     */
    public ModuleDefinitionBuilder(final String key, final String name, final ModuleFactory<T> factory) {
      this.key = key;
      this.name = name;
      this.factory = factory;
    }

    /**
     * Sets the module key.
     *
     * @param key the module key
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> key(final String key) {
      this.key = key;
      return this;
    }

    /**
     * Sets the module name.
     *
     * @param name the module name
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> name(final String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the module prefix.
     *
     * @param prefix the module prefix
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> prefix(final String prefix) {
      this.prefix = prefix;
      return this;
    }

    /**
     * Sets required dependency keys.
     *
     * @param depends the required dependency keys
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> depends(final List<String> depends) {
      this.depends = depends.toArray(String[]::new);
      return this;
    }

    /**
     * Sets required dependency keys.
     *
     * @param depends the required dependency keys
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> depends(final String... depends) {
      this.depends = depends;
      return this;
    }

    /**
     * Sets optional dependency keys.
     *
     * @param softDepends the optional dependency keys
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> softDepends(final List<String> softDepends) {
      this.softDepends = softDepends.toArray(String[]::new);
      return this;
    }

    /**
     * Sets optional dependency keys.
     *
     * @param softDepends the optional dependency keys
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> softDepends(final String... softDepends) {
      this.softDepends = softDepends;
      return this;
    }

    /**
     * Sets the module factory.
     *
     * @param factory the module factory
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> factory(final ModuleFactory<T> factory) {
      this.factory = factory;
      return this;
    }

    /**
     * Sets the load condition supplier.
     *
     * @param condition the load condition supplier
     * @return this builder instance
     */
    public ModuleDefinitionBuilder<T> condition(final Supplier<LoadCondition> condition) {
      this.condition = condition;
      return this;
    }

    /**
     * Builds the configured module definition.
     *
     * @return the built module definition
     * @throws IllegalArgumentException if required builder values are missing or invalid
     */
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
