package com.lemonlightmc.zenith.modular;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;

import com.lemonlightmc.zenith.IZenithPlugin;

public abstract class Module {

  protected final IZenithPlugin plugin;
  protected final ModuleDefinition<?> definition;
  protected boolean isEnabled = false;
  protected List<Listener> listeners;

  public Module(final IZenithPlugin plugin, final ModuleDefinition<?> definition) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null!");
    }
    if (definition == null) {
      throw new IllegalArgumentException("ModuleDefinition cannot be null!");
    }
    this.plugin = plugin;
    this.definition = definition;
  }

  public IZenithPlugin plugin() {
    return plugin;
  }

  public String key() {
    return definition.key();
  }

  public String name() {
    return definition.name();
  }

  public Path path() {
    return plugin.getDataFolder().toPath().resolve("/modules/" + definition.key());
  }

  public String[] depends() {
    return definition.depends();
  }

  public String[] softDepends() {
    return definition.softDepends();
  }

  public boolean isEnabled() {
    return isEnabled;
  }

  public void loadModule() {
  }

  public void unloadModule() {
  }

  public void reloadModule() {

  }

  public void registerListeners(Listener... listeners) {
    if (this.listeners == null) {
      this.listeners = new ArrayList<>();
    }
    for (Listener listener : listeners) {
      if (listener != null) {
        this.listeners.add(listener);
      }
    }
  }

  @Override
  public int hashCode() {
    int result = 31 + plugin.hashCode();
    result = 31 * result + definition.hashCode();
    return 31 * result + (isEnabled ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Module other = (Module) obj;
    return isEnabled == other.isEnabled && definition.equals(other.definition)
        && plugin.equals(other.plugin);
  }

  @Override
  public String toString() {
    return "Module [isEnabled=" + isEnabled + ", key()=" + definition.key() + ", name()=" + definition.name()
        + ", path()=" + path().toString() + "]";
  }

}
