package com.lemonlightmc.zenith.modular;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.lemonlightmc.zenith.IZenithPlugin;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.events.BaseEvent;
import com.lemonlightmc.zenith.events.EventsAPI;
import com.lemonlightmc.zenith.exceptions.ModuleLoadException;

/**
 * Manages registration, loading, unloading, and lookup of Zenith modules.
 */
public class ModuleAPI {
  private final Map<String, Module> loadedModules = new HashMap<>();
  private final Map<String, ModuleDefinition<?>> registeredModules = new HashMap<>();

  private final IZenithPlugin plugin;

  /**
   * Creates a new module API for the given plugin.
   *
   * @param plugin the plugin that owns managed modules
   */
  public ModuleAPI(final IZenithPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Gets a loaded module by key, or {@code null} if unavailable.
   *
   * @param key the module key
   * @return the loaded module, or {@code null} if not found
   */
  public Module getOrNull(final String key) {
    return key == null || key.isEmpty() ? null : loadedModules.get(key);
  }

  /**
   * Gets a loaded module by key and expected type, or {@code null} if unavailable or mismatched.
   *
   * @param key the module key
   * @param cls the expected module type
   * @param <M> the module type
   * @return the loaded module, or {@code null} if not found or not of the expected type
   */
  public <M extends Module> M getOrNull(final String key, final Class<M> cls) {
    final Module module = key == null || key.isEmpty() ? null : loadedModules.get(key);
    if (module == null || !cls.isInstance(module)) {
      return null;
    }
    return cls.cast(module);
  }

  /**
   * Gets a loaded module by key, throwing if it is not available.
   *
   * @param key the module key
   * @return the loaded module
   * @throws IllegalArgumentException if the module is not registered or loaded
   */
  public Module getOrThrow(final String key) {
    final Module module = key == null || key.isEmpty() ? null : loadedModules.get(key);
    if (module == null) {
      throw new IllegalArgumentException("Module with key '" + key + "' is not registered!");
    }
    return module;
  }

  /**
   * Gets a loaded module by key and expected type, throwing if unavailable or mismatched.
   *
   * @param key the module key
   * @param cls the expected module type
   * @param <M> the module type
   * @return the loaded module
   * @throws IllegalArgumentException if the module is unavailable or not of the expected type
   */
  public <M extends Module> M getOrThrow(final String key, final Class<M> cls) {
    final Module module = key == null || key.isEmpty() ? null : loadedModules.get(key);
    if (module == null) {
      throw new IllegalArgumentException("Module with key '" + key + "' is not registered!");
    }
    if (!cls.isInstance(module)) {
      throw new IllegalArgumentException("Module with key '" + key + "' is not of type " + cls.getName() + "!");
    }
    return cls.cast(module);
  }

  /**
   * Gets a loaded module by key as an optional value.
   *
   * @param key the module key
   * @return an optional containing the loaded module, if present
   */
  public Optional<Module> getOptional(final String key) {
    return key == null || key.isEmpty() ? Optional.empty() : Optional.of(loadedModules.get(key));
  }

  /**
   * Gets a loaded module by key and expected type as an optional value.
   *
   * @param key the module key
   * @param cls the expected module type
   * @param <M> the module type
   * @return an optional containing the loaded module, if present and of the expected type
   */
  public <M extends Module> Optional<M> getOptional(final String key, final Class<M> cls) {
    return Optional.ofNullable(getOrNull(key, cls));
  }

  /**
   * Gets the map of currently loaded modules keyed by module key.
   *
   * @return the loaded modules map
   */
  public Map<String, Module> getLoadedModules() {
    return loadedModules;
  }

  /**
   * Gets the map of registered module definitions keyed by module key.
   *
   * @return the registered module definitions map
   */
  public Map<String, ModuleDefinition<?>> getRegisteredModules() {
    return registeredModules;
  }

  /**
   * Checks whether the specified module is loaded and enabled.
   *
   * @param key the module key
   * @return {@code true} if the module is enabled; {@code false} otherwise
   */
  public boolean isEnabled(final String key) {
    final Module module = getOrNull(key);
    return module == null ? false : module.isEnabled();
  }

  /**
   * Checks whether the specified module definition is registered.
   *
   * @param key the module key
   * @return {@code true} if registered; {@code false} otherwise
   */
  public boolean isRegistered(final String key) {
    return key == null || key.isEmpty() ? false : registeredModules.containsKey(key);
  }

  /**
   * Registers a module definition for later loading.
   *
   * @param definition the module definition to register
   * @param <T> the module type
   */
  public <T extends Module> void register(final ModuleDefinition<T> definition) {
    if (definition == null) {
      return;
    }
    if (registeredModules.containsKey(definition.key())) {
      GlobalLogger.warn("Module with key '" + definition.key() + "' is already registered!");
      return;
    }
    registeredModules.put(definition.key(), definition);
  }

  /**
   * Unregisters and unloads the specified module.
   *
   * @param module the module to unregister
   */
  public void unregister(final Module module) {
    unregister(module.key());
  }

  /**
   * Unregisters and unloads the module with the specified key.
   *
   * @param key the module key
   */
  public void unregister(final String key) {
    if (key == null || key.isEmpty()) {
      return;
    }
    Module oldModule = loadedModules.remove(key);
    if (oldModule != null) {
      unloadModule(oldModule);
    }
    oldModule = null;
    final ModuleDefinition<?> old = registeredModules.remove(key);
    if (old == null) {
      return;
    }
  }

  /**
   * Loads a registered module by key.
   *
   * @param key the module key
   * @param <T> the module type
   * @return {@code true} if the module is enabled after loading; {@code false} otherwise
   * @throws IllegalStateException if the module is not registered
   */
  @SuppressWarnings("unchecked")
  public <T extends Module> boolean loadModule(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    final ModuleDefinition<T> definition = (ModuleDefinition<T>) registeredModules.get(key);
    if (definition == null) {
      throw new IllegalStateException("Module '" + key + "' is not registered!");
    }
    if (loadedModules.containsKey(key)) {
      GlobalLogger.warn("Module with key '" + definition.key() + "' is already loaded!");
      return true;
    }
    if (_loadDeps(definition.depends(), false)) {
      return false;
    }
    _loadDeps(definition.softDepends(), true);

    try {
      final LoadCondition condition = definition.condition().get();
      if (!condition.isSuccess()) {
        this.plugin.getSlf4jLogger().error("Module '%s' can not be loaded: '%s'".formatted(
            definition.key(), condition.reason().orElse(null)));
        return false;
      }

      final T module = definition.factory().load(plugin, definition);
      module.loadModule();
      if (module.listeners != null) {
        for (Listener listener : module.listeners) {
          EventsAPI.register(listener);
        }
      }
      EventsAPI.call(new ModuleLoadEvent<>(module));
      return module.isEnabled();
    } catch (final ModuleLoadException exception) {
      this.plugin.getSlf4jLogger().error(
          "Failed trying to load module '%s': %s".formatted(definition.key(), exception.getMessage()));
    }
    return false;
  }

  /**
   * Unloads a loaded module by key.
   *
   * @param key the module key
   * @return {@code true} if the unload flow completed; {@code false} if the module was unavailable
   */
  public boolean unloadModule(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    return unloadModule(loadedModules.get(key));
  }

  /**
   * Unloads the specified module instance.
   *
   * @param module the module to unload
   * @return {@code true} if the unload flow completed; {@code false} if {@code module} is {@code null}
   */
  public boolean unloadModule(final Module module) {
    if (module == null) {
      return false;
    }
    try {
      EventsAPI.call(new ModuleUnloadEvent<>(module));
      module.unloadModule();
    } catch (final ModuleLoadException exception) {
      this.plugin.getSlf4jLogger().error(
          "Failed trying to unload module '%s': %s".formatted(module.key(), exception.getMessage()));
    }
    module.isEnabled = false;
    return true;
  }

  /**
   * Reloads a loaded module by key.
   *
   * @param key the module key
   * @return {@code true} if the reload flow completed; {@code false} if the module was unavailable
   */
  public boolean reloadModule(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    return reloadModule(loadedModules.get(key));
  }

  /**
   * Reloads the specified module instance.
   *
   * @param module the module to reload
   * @return {@code true} if the reload flow completed; {@code false} if {@code module} is {@code null}
   */
  public boolean reloadModule(final Module module) {
    if (module == null) {
      return false;
    }
    try {
      module.reloadModule();
      EventsAPI.call(new ModuleReloadEvent<>(module));
    } catch (final ModuleLoadException exception) {
      this.plugin.getSlf4jLogger().error(
          "Failed trying to reload module '%s': %s".formatted(module.key(), exception.getMessage()));
    }
    return true;
  }

  /**
   * Attempts to load all registered modules.
   */
  public void loadAll() {
    for (final ModuleDefinition<?> definition : registeredModules.values()) {
      loadModule(definition.key());
    }
  }

  /**
   * Unloads all currently loaded modules.
   */
  public void unloadAll() {
    for (final Module module : loadedModules.values()) {
      this.unloadModule(module);
    }
  }

  /**
   * Reloads all currently loaded modules.
   */
  public void reloadAll() {
    for (final Module module : loadedModules.values()) {
      this.reloadModule(module);
    }
  }

  /**
   * Shuts down module management by unloading and clearing all module state.
   */
  public void shutdown() {
    EventsAPI.call(new ModulesShutdownEvent());
    unloadAll();
    loadedModules.clear();
    registeredModules.clear();
  }

  /**
   * Applies a consumer to an enabled module if it is available.
   *
   * @param key the module key
   * @param consumer the consumer to apply
   */
  public void computeModule(final String key, final Consumer<Module> consumer) {
    final Module module = getOrNull(key);
    if (module == null || !module.isEnabled()) {
      return;
    }
    consumer.accept(module);
  }

  /**
   * Applies a predicate to an enabled module if it is available.
   *
   * @param key the module key
   * @param consumer the predicate to apply
   * @return the predicate result, or {@code false} if the module is unavailable
   */
  public boolean computeModule(final String key, final Predicate<Module> consumer) {
    final Module module = getOrNull(key);
    if (module == null || !module.isEnabled()) {
      return false;
    }
    return consumer.test(module);
  }

  /**
   * Applies a function to an enabled module if it is available.
   *
   * @param key the module key
   * @param consumer the function to apply
   * @param <T> the result type
   * @return the function result, or {@code null} if the module is unavailable
   */
  public <T> T computeModule(final String key, final Function<Module, T> consumer) {
    final Module module = getOrNull(key);
    if (module == null || !module.isEnabled()) {
      return null;
    }
    return consumer.apply(module);
  }

  /**
   * Applies a function to an enabled module of a specific type if it is available.
   *
   * @param key the module key
   * @param cls the expected module type
   * @param consumer the function to apply
   * @param <M> the module type
   * @param <T> the result type
   * @return the function result, or {@code null} if the module is unavailable
   * @throws IllegalStateException if the module exists but is not of the expected type
   */
  @SuppressWarnings("unchecked")
  public <M extends Module, T> T computeModule(final String key, final Class<M> cls, final Function<M, T> consumer) {
    final Module module = getOrNull(key);
    if (module == null || !module.isEnabled()) {
      return null;
    }
    if (module.getClass() != cls) {
      throw new IllegalStateException("Module '" + key + "' is not of type " + cls.getName() + "!");
    }
    // Safely cast module
    return consumer.apply((M) module);
  }

  /**
   * Loads declared dependencies for a module.
   *
   * @param deps the dependency keys to process
   * @param soft whether missing dependencies should be treated as non-fatal
   * @return {@code true} if required dependencies were satisfied; {@code false} otherwise
   */
  private boolean _loadDeps(final String[] deps, final boolean soft) {
    if (deps == null || deps.length == 0) {
      return true;
    }
    boolean isEnabled = true;
    boolean success = true;
    for (final String key : deps) {
      if (key.startsWith("$")) {
        isEnabled = Bukkit.getPluginManager().isPluginEnabled(key.substring(1));
      } else {
        isEnabled = loadModule(key);
      }
      if (!isEnabled && !soft) {
        GlobalLogger.warn("Failed to load Dependency '" + key + "' for Module");
        success = false;
      }
    }
    return success;
  }

  /**
   * Event fired when a module has been loaded.
   *
   * @param <T> the module type
   */
  public static class ModuleLoadEvent<T extends Module> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T module;

    /**
     * Creates a new module load event.
     *
     * @param module the loaded module
     */
    public ModuleLoadEvent(final T module) {
      this.module = module;
    }

    /**
     * Gets the loaded module.
     *
     * @return the loaded module
     */
    public T module() {
      return module;
    }

    /**
     * Gets the key of the loaded module.
     *
     * @return the module key
     */
    public String key() {
      return module.key();
    }

    /** {@inheritDoc} */
    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Gets the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a module is being unloaded.
   *
   * @param <T> the module type
   */
  public static class ModuleUnloadEvent<T extends Module> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T module;

    /**
     * Creates a new module unload event.
     *
     * @param module the module being unloaded
     */
    public ModuleUnloadEvent(final T module) {
      this.module = module;
    }

    /**
     * Gets the module being unloaded.
     *
     * @return the unloaded module
     */
    public T module() {
      return module;
    }

    /**
     * Gets the key of the module being unloaded.
     *
     * @return the module key
     */
    public String key() {
      return module.key();
    }

    /** {@inheritDoc} */
    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Gets the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a module has been reloaded.
   *
   * @param <T> the module type
   */
  public static class ModuleReloadEvent<T extends Module> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T module;

    /**
     * Creates a new module reload event.
     *
     * @param module the reloaded module
     */
    public ModuleReloadEvent(final T module) {
      this.module = module;
    }

    /**
     * Gets the reloaded module.
     *
     * @return the reloaded module
     */
    public T module() {
      return module;
    }

    /**
     * Gets the key of the reloaded module.
     *
     * @return the module key
     */
    public String key() {
      return module.key();
    }

    /** {@inheritDoc} */
    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Gets the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when the module system is shutting down.
   */
  public static class ModulesShutdownEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Creates a new modules shutdown event.
     */
    public ModulesShutdownEvent() {
    }

    /** {@inheritDoc} */
    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Gets the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }
}
