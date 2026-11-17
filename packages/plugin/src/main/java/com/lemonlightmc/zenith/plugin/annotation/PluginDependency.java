package com.lemonlightmc.zenith.plugin.annotation;

import javax.annotation.Nonnull;

/**
 * Declares a dependency on another plugin.
 */
public @interface PluginDependency {

  /**
   * Returns the name of the required plugin.
   *
   * @return the plugin name
   */
  @Nonnull
  String value();

  /**
   * Returns whether this dependency is optional.
   *
   * @return {@code true} if this is a soft dependency; {@code false} otherwise
   */
  boolean soft() default false;

}