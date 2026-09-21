package com.lemonlightmc.zenith.modular;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.IZenithPlugin;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.events.BaseEvent;
import com.lemonlightmc.zenith.events.EventsAPI;
import com.lemonlightmc.zenith.exceptions.ModuleLoadException;

public class ModuleAPI {
  private final Map<String, Module> loadedModules = new HashMap<>();
  private final Map<String, ModuleDefinition<?>> registeredModules = new HashMap<>();

  private final IZenithPlugin plugin;

  public ModuleAPI(final IZenithPlugin plugin) {
    this.plugin = plugin;
  }

  public Module getOrNull(final String key) {
    return key == null || key.isEmpty() ? null : loadedModules.get(key);
  }

  public <M extends Module> M getOrNull(final String key, final Class<M> cls) {
    final Module module = key == null || key.isEmpty() ? null : loadedModules.get(key);
    if (module == null || !cls.isInstance(module)) {
      return null;
    }
    return cls.cast(module);
  }

  public Module getOrThrow(final String key) {
    final Module module = key == null || key.isEmpty() ? null : loadedModules.get(key);
    if (module == null) {
      throw new IllegalArgumentException("Module with key '" + key + "' is not registered!");
    }
    return module;
  }

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

  public Optional<Module> getOptional(final String key) {
    return key == null || key.isEmpty() ? Optional.empty() : Optional.of(loadedModules.get(key));
  }

  public <M extends Module> Optional<M> getOptional(final String key, final Class<M> cls) {
    return Optional.ofNullable(getOrNull(key, cls));
  }

  public Map<String, Module> getLoadedModules() {
    return loadedModules;
  }

  public Map<String, ModuleDefinition<?>> getRegisteredModules() {
    return registeredModules;
  }

  public boolean isEnabled(final String key) {
    final Module module = getOrNull(key);
    return module == null ? false : module.isEnabled();
  }

  public boolean isRegistered(final String key) {
    return key == null || key.isEmpty() ? false : registeredModules.containsKey(key);
  }

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

  public void unregister(final Module module) {
    unregister(module.key());
  }

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
      EventsAPI.call(new ModuleLoadEvent<>(module));
      return module.isEnabled();
    } catch (final ModuleLoadException exception) {
      this.plugin.getSlf4jLogger().error(
          "Failed trying to load module '%s': %s".formatted(definition.key(), exception.getMessage()));
    }
    return false;
  }

  public boolean unloadModule(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    return unloadModule(loadedModules.get(key));
  }

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

  public boolean reloadModule(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    return reloadModule(loadedModules.get(key));
  }

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

  public void loadAll() {
    for (final ModuleDefinition<?> definition : registeredModules.values()) {
      loadModule(definition.key());
    }
  }

  public void unloadAll() {
    for (final Module module : loadedModules.values()) {
      this.unloadModule(module);
    }
  }

  public void reloadAll() {
    for (final Module module : loadedModules.values()) {
      this.reloadModule(module);
    }
  }

  public void shutdown() {
    EventsAPI.call(new ModulesShutdownEvent());
    unloadAll();
    loadedModules.clear();
    registeredModules.clear();
  }

  public void computeModule(final String key, final Consumer<Module> consumer) {
    final Module module = getOrNull(key);
    if (module == null || !module.isEnabled()) {
      return;
    }
    consumer.accept(module);
  }

  public boolean computeModule(final String key, final Predicate<Module> consumer) {
    final Module module = getOrNull(key);
    if (module == null || !module.isEnabled()) {
      return false;
    }
    return consumer.test(module);
  }

  public <T> T computeModule(final String key, final Function<Module, T> consumer) {
    final Module module = getOrNull(key);
    if (module == null || !module.isEnabled()) {
      return null;
    }
    return consumer.apply(module);
  }

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

  public static class ModuleLoadEvent<T extends Module> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T module;

    public ModuleLoadEvent(final T module) {
      this.module = module;
    }

    public T module() {
      return module;
    }

    public String key() {
      return module.key();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ModuleUnloadEvent<T extends Module> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T module;

    public ModuleUnloadEvent(final T module) {
      this.module = module;
    }

    public T module() {
      return module;
    }

    public String key() {
      return module.key();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ModuleReloadEvent<T extends Module> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T module;

    public ModuleReloadEvent(final T module) {
      this.module = module;
    }

    public T module() {
      return module;
    }

    public String key() {
      return module.key();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ModulesShutdownEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    public ModulesShutdownEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }
}
