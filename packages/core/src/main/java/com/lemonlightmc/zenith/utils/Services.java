package com.lemonlightmc.zenith.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

import com.lemonlightmc.zenith.ZenithProvider;

import java.util.Collection;
import java.util.Optional;

/**
 * Utility class for interacting with the Bukkit {@link ServicesManager}.
 */
public final class Services {

  /**
   * Loads a service instance, throwing a {@link IllegalStateException} if no
   * registration is present.
   *
   * @param cls the service class
   * @param <T> the service class type
   * @return the service instance
   */

  public static <T> T getOrThrow(final Class<T> cls) {
    if (cls == null) {
      throw new IllegalArgumentException("Service Class cannot be null");
    }

    final RegisteredServiceProvider<T> registration = Bukkit.getServicesManager().getRegistration(cls);
    if (registration == null || registration.getProvider() == null) {
      throw new IllegalStateException("No registration present for service '" + cls.getName() + "'");
    }
    return registration.getProvider();
  }

  /**
   * Loads a service instance
   *
   * @param cls the service class
   * @param <T> the service class type
   * @return the service instance, as an optional
   */

  public static <T> Optional<T> get(final Class<T> cls) {
    if (cls == null) {
      throw new IllegalArgumentException("Service Class cannot be null");
    }
    final RegisteredServiceProvider<T> registration = Bukkit.getServicesManager().getRegistration(cls);
    if (registration == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(registration.getProvider());
  }

  /**
   * Provides a service.
   *
   * @param cls      the service class
   * @param instance the service instance
   * @param plugin   the plugin to register the service to
   * @param priority the priority to register the service instance at
   * @param <T>      the service class type
   * @return the same service instance
   */

  public static <T> T provide(final Class<T> cls, final T instance, final Plugin plugin,
      final ServicePriority priority) {
    if (cls == null) {
      throw new IllegalArgumentException("Service Class cannot be null");
    }
    if (instance == null) {
      throw new IllegalArgumentException("Service Instance cannot be null");
    }
    if (plugin == null) {
      throw new IllegalArgumentException("Providing Plugin cannot be null");
    }
    if (priority == null) {
      throw new IllegalArgumentException("Service Priority cannot be null");
    }
    Bukkit.getServicesManager().register(cls, instance, plugin, priority);
    return instance;
  }

  /**
   * Provides a service.
   *
   * @param cls      the service class
   * @param instance the service instance
   * @param priority the priority to register the service instance at
   * @param <T>      the service class type
   * @return the same service instance
   */

  public static <T> T provide(final Class<T> cls, final T instance, final ServicePriority priority) {
    if (cls == null) {
      throw new IllegalArgumentException("Service Class cannot be null");
    }
    if (instance == null) {
      throw new IllegalArgumentException("Service Instance cannot be null");
    }
    if (priority == null) {
      throw new IllegalArgumentException("Service Priority cannot be null");
    }
    Bukkit.getServicesManager().register(cls, instance, ZenithProvider.instance(), priority);
    return instance;
  }

  /**
   * Provides a service.
   *
   * @param cls      the service class
   * @param instance the service instance
   * @param <T>      the service class type
   * @return the same service instance
   */

  public static <T> T provide(final Class<T> cls, final T instance) {
    if (cls == null) {
      throw new IllegalArgumentException("Service Class cannot be null");
    }
    if (instance == null) {
      throw new IllegalArgumentException("Service Instance cannot be null");
    }
    Bukkit.getServicesManager().register(cls, instance, ZenithProvider.instance(), ServicePriority.Normal);
    return instance;
  }

  public static <T> void unregister(final Class<T> cls, final T instance) {
    if (cls == null) {
      throw new IllegalArgumentException("Service Class cannot be null");
    }
    if (instance == null) {
      throw new IllegalArgumentException("Service Instance cannot be null");
    }
    Bukkit.getServicesManager().unregister(cls, instance);
  }

  public static <T> void unregister(final T instance) {
    if (instance == null) {
      throw new IllegalArgumentException("Service Instance cannot be null");
    }
    Bukkit.getServicesManager().unregister(instance);
  }

  public static void unregisterAll(final Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    Bukkit.getServicesManager().unregisterAll(plugin);
  }

  public static void unregisterAll() {
    Bukkit.getServicesManager().unregisterAll(ZenithProvider.instance());
  }

  public static Collection<RegisteredServiceProvider<?>> getRegistrations() {
    return Bukkit.getServicesManager().getRegistrations(ZenithProvider.instance());
  }

  public static Collection<Class<?>> getKnownServices() {
    return Bukkit.getServicesManager().getKnownServices();
  }

  public static <T> boolean isProvidedFor(@NotNull final Class<T> service) {
    return service == null ? false : Bukkit.getServicesManager().isProvidedFor(service);
  }

  private Services() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }
}