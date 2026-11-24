package com.lemonlightmc.zenith;

import java.nio.file.Path;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.scheduler.Scheduler;

/**
 * Static API facade for Zenith runtime services.
 *
 * <p>The actual runtime behavior is provided by a {@link ZenithBackend} installed by the plugin package.
 */
public final class ZenithProvider {

  private static volatile ZenithBackend backend;

  private ZenithProvider() {
  }

  /**
   * Installs the runtime backend.
   *
   * @param backend the backend implementation
   */
  public static synchronized void install(final ZenithBackend backend) {
    if (backend == null) {
      throw new IllegalArgumentException("backend");
    }
    ZenithProvider.backend = backend;
  }

  /**
   * Returns the installed backend.
   *
   * @return the backend
   * @throws IllegalStateException if no backend has been installed
   */
  public static ZenithBackend backend() {
    final ZenithBackend current = backend;
    if (current == null) {
      throw new IllegalStateException("ZenithProvider backend has not been installed yet.");
    }
    return current;
  }

  /**
   * Returns whether a backend has been installed.
   *
   * @return {@code true} if installed
   */
  public static boolean hasBackend() {
    return backend != null;
  }

  public static ZenithConfig config() {
    return backend().config();
  }

  public static Path pluginsFolder() {
    return backend().pluginsFolder();
  }

  public static Path libariesFolder() {
    return backend().librariesFolder();
  }

  public static Path librariesFolder() {
    return backend().librariesFolder();
  }

  public static Path zenithFolder() {
    return backend().zenithFolder();
  }

  public static boolean hasInstance() {
    final ZenithBackend current = backend;
    return current != null && current.hasInstance();
  }

  public static IZenithPlugin instance() {
    return backend().instance();
  }

  public static Server server() {
    return backend().server();
  }

  public static Scheduler scheduler() {
    return backend().scheduler();
  }

  public static PluginManager pluginManager() {
    return backend().pluginManager();
  }

  public static MessageAPI messageAPI() {
    return backend().messageAPI();
  }

  public static void reloadZenithConfig() {
    backend().reloadConfig();
  }

  public static LoggerAdapter pluginLogger() {
    return backend().pluginLogger();
  }

  public static java.util.logging.Logger bukkitLogger() {
    return backend().bukkitLogger();
  }

  public static Logger zenithLogger() {
    return backend().zenithLogger();
  }

  public static Logger zenithLogger(final String subLogger) {
    return backend().zenithLogger(subLogger);
  }

  public static Logger zenithLogger(final String subLogger, final Level level) {
    return backend().zenithLogger(subLogger, level);
  }

  public static Logger zenithLogger(final Logger logger, final String subLogger) {
    return backend().zenithLogger(logger, subLogger);
  }

  public static Logger zenithLogger(final Logger logger, final String subLogger, final Level level) {
    return backend().zenithLogger(logger, subLogger, level);
  }
}
