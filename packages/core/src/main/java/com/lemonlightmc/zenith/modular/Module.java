package com.lemonlightmc.zenith.modular;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.Listener;

import com.lemonlightmc.zenith.IZenithPlugin;

/**
 * Base class for loadable Zenith modules.
 *
 * <p>Modules are created from a {@link ModuleDefinition} and managed by {@link ModuleAPI}.
 * Subclasses can override lifecycle hooks to load, unload, and reload module state.</p>
 */
public abstract class Module {

  /** The plugin that owns this module. */
  protected final IZenithPlugin plugin;
  /** The definition describing this module. */
  protected final ModuleDefinition<?> definition;
  /** Indicates whether this module is currently enabled. */
  protected boolean isEnabled = false;
  /** Listeners registered by this module for lifecycle management. */
  protected List<Listener> listeners;

  /**
   * Creates a new module instance.
   *
   * @param plugin the plugin that owns the module
   * @param definition the module definition
   * @throws IllegalArgumentException if {@code plugin} or {@code definition} is {@code null}
   */
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

  /**
   * Gets the plugin that owns this module.
   *
   * @return the owning plugin
   */
  public IZenithPlugin plugin() {
    return plugin;
  }

  /**
   * Gets the unique key of this module.
   *
   * @return the module key
   */
  public String key() {
    return definition.key();
  }

  /**
   * Gets the display name of this module.
   *
   * @return the module name
   */
  public String name() {
    return definition.name();
  }

  /**
   * Gets the data path associated with this module.
   *
   * @return the module data path
   */
  public Path path() {
    return plugin.getDataFolder().toPath().resolve("/modules/" + definition.key());
  }

  /**
   * Gets the required module dependencies declared by this module.
   *
   * @return the required dependency keys, or {@code null} if none are declared
   */
  public String[] depends() {
    return definition.depends();
  }

  /**
   * Gets the optional module dependencies declared by this module.
   *
   * @return the optional dependency keys, or {@code null} if none are declared
   */
  public String[] softDepends() {
    return definition.softDepends();
  }

  /**
   * Checks whether this module is currently enabled.
   *
   * @return {@code true} if enabled; {@code false} otherwise
   */
  public boolean isEnabled() {
    return isEnabled;
  }

  /**
   * Loads this module.
   *
   * <p>Subclasses can override this method to perform load-time initialization.</p>
   */
  public void loadModule() {
  }

  /**
   * Unloads this module.
   *
   * <p>Subclasses can override this method to release resources.</p>
   */
  public void unloadModule() {
  }

  /**
   * Reloads this module.
   *
   * <p>Subclasses can override this method to refresh module state.</p>
   */
  public void reloadModule() {

  }

  /**
   * Registers listeners to be managed by this module.
   *
   * @param listeners the listeners to register
   */
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
