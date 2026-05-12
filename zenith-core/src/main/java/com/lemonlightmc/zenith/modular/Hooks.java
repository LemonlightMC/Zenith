package com.lemonlightmc.zenith.modular;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.interfaces.Reloadable;

public class Hooks<H extends IModule> implements Reloadable {
  private final ModuleManager<H> manager = new ModuleManager<>();
  private static Hooks<Hook> instance;

  public static Hooks<Hook> getInstance() {
    if (instance == null) {
      instance = new Hooks<Hook>();
    }
    return instance;
  }

  public static abstract class Hook extends Module {
    public Hook(final String name) {
      super(name);
    }
  }

  public HashMap<String, H> getHooks() {
    return manager.getModules();
  }

  public H getHook(final String name) {
    return manager.getModule(name);
  }

  public <T extends H> T getHook(final String name, final Class<T> cls) {
    return manager.getModule(name, cls);
  }

  public boolean isEnabled(final String name) {
    return manager.isEnabled(name);
  }

  public void enable(final String name) {
    manager.enable(name);
  }

  public void disable(final String name) {
    manager.disable(name);
  }

  public void enableAll() {
    manager.enableAll();
  }

  @Override
  public void reload() {
    manager.reload();
  }

  public void disableAll() {
    manager.disableAll();
  }

  public void shutdown() {
    manager.shutdown();
  }

  public H createHook(final Class<H> hookCls) {
    return manager.createModule(hookCls);
  }

  public void consumeHook(final String name, final Consumer<H> consumer) {
    manager.consumeModule(name, consumer);
  }

  public boolean predicateHook(final String name, final Predicate<H> consumer) {
    return manager.predicateModule(name, consumer);
  }

}
