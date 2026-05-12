package com.lemonlightmc.zenith.modular;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.interfaces.Reloadable;
import com.lemonlightmc.zenith.messages.Logger;

public class ModuleManager<T extends IModule> implements Reloadable {
  private final HashMap<String, T> modules = new HashMap<>(6);
  private static ModuleManager<?> instance;

  public static ModuleManager<?> getInstance() {
    if (instance == null) {
      instance = new ModuleManager<IModule>();
    }
    return instance;
  }

  public HashMap<String, T> getModules() {
    return modules;
  }

  public T getModule(final String key) {
    return modules.get(key);
  }

  public <S extends T> S getModule(final String name, final Class<S> cls) {
    final IModule module = modules.get(name);
    if (module == null || !cls.isInstance(module)) {
      return null;
    }
    return cls.cast(module);
  }

  public boolean isEnabled(final String key) {
    final T module = modules.get(key);
    if (module == null) {
      return false;
    }
    return module.isEnabled();
  }

  public boolean enable(final String key) {
    final T module = modules.get(key);
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

  public boolean disable(final String key) {
    final T module = modules.get(key);
    if (module == null) {
      return true;
    }
    module.disable();
    return !module.isEnabled();
  }

  @Override
  public void reload() {
    for (final T module : modules.values()) {
      module.reload();
    }
  }

  public void enableAll() {
    for (final T module : modules.values()) {
      if (module == null) {
        return;
      }
      module.enable();
    }
  }

  public void disableAll() {
    for (final T module : modules.values()) {
      if (module == null) {
        return;
      }
      module.disable();
    }
  }

  public void shutdown() {
    disableAll();
    modules.clear();
  }

  public T createModule(final Class<T> moduleCls) {
    try {
      final T module = moduleCls.getDeclaredConstructor(moduleCls).newInstance();
      modules.put(module.getKey(), module);
      return module;
    } catch (final Exception e) {
      Logger.warn("Failed to create module: " + moduleCls.getName());
      return null;
    }
  }

  public void consumeModule(final String key, final Consumer<T> consumer) {
    final T module = modules.get(key);
    if (module == null || !module.isEnabled()) {
      return;
    }
    consumer.accept(module);
  }

  public boolean predicateModule(final String key, final Predicate<T> consumer) {
    final T module = modules.get(key);
    if (module == null || !module.isEnabled()) {
      return false;
    }
    return consumer.test(module);
  }

  private boolean _loadDeps(final List<String> deps, final boolean soft) {
    if (deps == null || deps.size() == 0) {
      return true;
    }
    boolean isEnabled = true;
    boolean success = true;
    for (final String key : deps) {
      isEnabled = enable(key);
      if (!soft && !isEnabled) {
        Logger.warn("Failed to load Dependencie '" + key + "'for Module");
        success = false;
      }
    }
    return success;
  }
}
