package com.lemonlightmc.zenith.modular;

import com.lemonlightmc.zenith.IZenithPlugin;

@FunctionalInterface
public interface ModuleFactory<T extends Module> {

  T load(IZenithPlugin plugin, ModuleDefinition<T> definition);
}
