package com.lemonlightmc.zenith.plugin.annotation;

import org.bukkit.plugin.PluginLoadOrder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;

/**
 * Declares metadata used to generate a {@code plugin.yml} descriptor at compile time.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface PluginYmlCreator {

  /**
   * Returns the plugin name.
   *
   * @return the plugin name
   */
  @Nonnull
  String name();

  /**
   * Returns the plugin version.
   *
   * @return the plugin version, or an empty string to generate one automatically
   */
  @Nonnull
  String version() default "";

  /**
   * Returns the plugin description.
   *
   * @return the plugin description
   */
  @Nonnull
  String description() default "";

  /**
   * Returns the plugin load order.
   *
   * @return the plugin load order
   */
  @Nonnull
  PluginLoadOrder load() default PluginLoadOrder.POSTWORLD;

  /**
   * Returns the target Bukkit API version.
   *
   * @return the API version
   */
  String apiVersion() default "";

  /**
   * Returns the plugin authors.
   *
   * @return the plugin authors
   */
  @Nonnull
  String[] authors() default {};

  /**
   * Returns the plugin website URL.
   *
   * @return the plugin website
   */
  @Nonnull
  String website() default "";

  /**
   * Returns declared plugin dependencies.
   *
   * @return the plugin dependencies
   */
  @Nonnull
  PluginDependency[] depends() default {};

  /**
   * Returns hard plugin dependencies.
   *
   * @return the hard dependency names
   */
  @Nonnull
  String[] hardDepends() default {};

  /**
   * Returns soft plugin dependencies.
   *
   * @return the soft dependency names
   */
  @Nonnull
  String[] softDepends() default {};

  /**
   * Returns plugins that should be loaded after this plugin.
   *
   * @return the plugin names to load after this plugin
   */
  @Nonnull
  String[] loadBefore() default {};

  /**
   * Returns Maven Central libraries to load at runtime.
   *
   * @return the runtime library coordinates
   */
  @Nonnull
  String[] libraries() default {};
}