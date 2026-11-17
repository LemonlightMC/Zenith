package com.lemonlightmc.zenith.items;

import java.net.http.WebSocket.Listener;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.recipes.RecipesManager;

/**
 * Manages registration, lookup, and lifecycle of custom items.
 *
 * <p>The manager keeps a registry of {@link BaseCustomItem} instances, registers their recipes when present, and
 * installs the shared {@link CustomItemListener} when the first item is registered.
 */
public class CustomItemManager implements Listener {
  private static final CustomItemManager INSTANCE = new CustomItemManager();

  private final Map<String, BaseCustomItem> itemRegistry = new HashMap<>();
  private boolean hasRegisteredListener = false;
  private CustomItemListener listener;

  /**
   * Creates a custom item manager.
   */
  public CustomItemManager() {
  }

  /**
   * Returns the shared custom item manager instance.
   *
   * @return the singleton manager instance
   */
  public static CustomItemManager getManager() {
    return INSTANCE;
  }

  /**
   * Registers one or more custom items.
   *
   * @param handlers the custom items to register
   */
  public void register(final BaseCustomItem... handlers) {
    for (final BaseCustomItem handler : handlers) {
      this.register(handler);
    }
  }

  /**
   * Registers a custom item and its associated recipe, if available.
   *
   * @param item the custom item to register
   */
  public void register(final BaseCustomItem item) {
    itemRegistry.put(item.itemID, item);
    if (item.getRecipe() != null) {
      RecipesManager.getInstance().register(item.getRecipe());
    }

    if (!hasRegisteredListener) {
      listener = new CustomItemListener();
      Bukkit.getServer().getPluginManager().registerEvents(listener, ZenithProvider.instance());
      hasRegisteredListener = true;
    }
  }

  /**
   * Unregisters one or more custom items.
   *
   * @param handlers the custom items to unregister
   */
  public void unregister(final BaseCustomItem... handlers) {
    for (final BaseCustomItem handler : handlers) {
      unregister(handler);
    }
  }

  /**
   * Unregisters a custom item and its associated recipe, if available.
   *
   * @param item the custom item to unregister
   */
  public void unregister(final BaseCustomItem item) {
    itemRegistry.remove(item.itemID, item);
    if (item.getRecipe() != null) {
      RecipesManager.getInstance().unregister(item.getRecipe());
    }
  }

  /**
   * Returns the registered custom item with the given identifier.
   *
   * @param id the custom item identifier
   * @return the registered custom item, or {@code null} if none is registered
   */
  public BaseCustomItem get(final String id) {
    return itemRegistry.get(id);
  }

  /**
   * Checks whether a custom item with the given identifier is registered.
   *
   * @param id the custom item identifier
   * @return {@code true} if a matching custom item is registered; {@code false} otherwise
   */
  public boolean has(final String id) {
    return itemRegistry.containsKey(id);
  }

  /**
   * Returns the registry of custom items keyed by identifier.
   *
   * @return the registered custom items
   */
  public Map<String, BaseCustomItem> getItems() {
    return itemRegistry;
  }

  /**
   * Unregisters all custom items, recipes, and the shared event listener if it was registered.
   */
  public void unregisterAll() {
    itemRegistry.clear();
    RecipesManager.getInstance().unregisterAll();
    if (hasRegisteredListener) {
      HandlerList.unregisterAll(listener);
    }
  }
}