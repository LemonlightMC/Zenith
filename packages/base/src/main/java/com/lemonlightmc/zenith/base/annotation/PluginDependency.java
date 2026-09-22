package com.lemonlightmc.zenith.base.annotation;

import javax.annotation.Nonnull;

/**
 * Represents a plugin dependency
 */
public @interface PluginDependency {

  /**
   * The name of the plugin
   *
   * @return the name of the plugin
   */
  @Nonnull
  String value();

  /**
   * If this is a "soft" dependency
   *
   * @return true if this is a "soft" dependency
   */
  boolean soft() default false;

}