package com.lemonlightmc.zenith.modular;

import com.lemonlightmc.zenith.IZenithPlugin;

/**
 * Factory for creating module instances from a {@link ModuleDefinition}.
 *
 * @param <T> the module type created by this factory
 */
@FunctionalInterface
public interface ModuleFactory<T extends Module> {

  /**
   * Creates and initializes a module instance for the given plugin and definition.
   *
   * @param plugin the plugin that owns the module
   * @param definition the definition describing the module
   * @return the created module instance
   */
  T load(IZenithPlugin plugin, ModuleDefinition<T> definition);
}
