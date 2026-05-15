package com.lemonlightmc.zenith.modular;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.events.BaseEvent;
import com.lemonlightmc.zenith.events.EventsAPI;
import com.lemonlightmc.zenith.messages.Logger;

public class ModuleAPI {
  private static final Map<NamespacedKey, Module> modules = new HashMap<>(6);

  public static Module getModule(final NamespacedKey key) {
    return key == null ? null : modules.get(key);
  }

  public static <S extends Module> Optional<S> getModule(final NamespacedKey name, final Class<S> cls) {
    final Module module = modules.get(name);
    if (module == null || !cls.isInstance(module)) {
      return Optional.empty();
    }
    return Optional.of(cls.cast(module));
  }

  public static Map<NamespacedKey, Module> getModules() {
    return modules;
  }

  public static boolean isEnabled(final NamespacedKey key) {
    if (key == null) {
      return false;
    }
    final Module module = modules.get(key);
    return module == null ? false : module.isEnabled();
  }

  public static void register(final Module module) {
    if (module == null) {
      return;
    }
    if (modules.containsKey(module.getKey())) {
      Logger.warn("Module with key '" + module.getKey() + "' is already registered!");
      return;
    }
    modules.put(module.getKey(), module);
    module.register();
  }

  public static void unregister(final NamespacedKey key) {
    if (key == null) {
      return;
    }
    unregister(modules.get(key));
  }

  public static void unregister(final Module module) {
    if (module == null) {
      return;
    }
    final Module old = modules.remove(module.getKey());
    if (old == null) {
      return;
    }
    module.unregister();
  }

  public static boolean enable(final NamespacedKey key) {
    if (key == null) {
      return false;
    }
    return enable(modules.get(key));
  }

  public static boolean enable(final Module module) {
    if (module == null) {
      return false;
    }
    if (_loadDeps(module.getDepends(), false)) {
      return false;
    }
    _loadDeps(module.getSoftDepends(), true);
    module.enable();
    return module.isEnabled();
  }

  public static boolean disable(final NamespacedKey key) {
    if (key == null) {
      return false;
    }
    return disable(modules.get(key));
  }

  public static boolean disable(final Module module) {
    if (module == null) {
      return true;
    }
    module.disable();
    return !module.isEnabled();
  }

  public static void reload() {
    for (final Module module : modules.values()) {
      module.reload();
    }
  }

  public static void enableAll() {
    for (final Module module : modules.values()) {
      if (module == null) {
        return;
      }
      module.enable();
    }
  }

  public static void disableAll() {
    for (final Module module : modules.values()) {
      if (module == null) {
        return;
      }
      module.disable();
    }
  }

  public static void shutdown() {
    EventsAPI.call(new ModulesShutdownEvent());
    disableAll();
    modules.clear();
  }

  public static Module createModule(final Class<? extends Module> moduleCls) {
    try {
      final Module module = moduleCls.getDeclaredConstructor(moduleCls).newInstance();
      modules.put(module.getKey(), module);
      return module;
    } catch (final Exception e) {
      Logger.warn("Failed to create module: " + moduleCls.getName());
      return null;
    }
  }

  public static void computeModule(final NamespacedKey key, final Consumer<Module> consumer) {
    final Module module = modules.get(key);
    if (module == null || !module.isEnabled()) {
      return;
    }
    consumer.accept(module);
  }

  public static boolean computeModule(final NamespacedKey key, final Predicate<Module> consumer) {
    final Module module = modules.get(key);
    if (module == null || !module.isEnabled()) {
      return false;
    }
    return consumer.test(module);
  }

  public static <T> T computeModule(final NamespacedKey key, final Function<Module, T> consumer) {
    final Module module = modules.get(key);
    if (module == null || !module.isEnabled()) {
      return null;
    }
    return consumer.apply(module);
  }

  private static boolean _loadDeps(final List<NamespacedKey> deps, final boolean soft) {
    if (deps == null || deps.size() == 0) {
      return true;
    }
    boolean isEnabled = true;
    boolean success = true;
    for (final NamespacedKey key : deps) {
      isEnabled = enable(key);
      if (!soft && !isEnabled) {
        Logger.warn("Failed to load Dependencie '" + key + "'for Module");
        success = false;
      }
    }
    return success;
  }

  public static class ModuleEnableEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Module module;

    public ModuleEnableEvent(final Module module) {
      this.module = module;
    }

    public Module getModule() {
      return module;
    }

    public NamespacedKey getKey() {
      return module.getKey();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ModuleDisableEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Module module;

    public ModuleDisableEvent(final Module module) {
      this.module = module;
    }

    public Module getModule() {
      return module;
    }

    public NamespacedKey getKey() {
      return module.getKey();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ModuleRegisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Module module;

    public ModuleRegisterEvent(final Module module) {
      this.module = module;
    }

    public Module getModule() {
      return module;
    }

    public NamespacedKey getKey() {
      return module.getKey();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ModuleUnregisterEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Module module;

    public ModuleUnregisterEvent(final Module module) {
      this.module = module;
    }

    public Module getModule() {
      return module;
    }

    public NamespacedKey getKey() {
      return module.getKey();
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
