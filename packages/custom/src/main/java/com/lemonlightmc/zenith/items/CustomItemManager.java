package com.lemonlightmc.zenith.items;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.recipes.RecipesManager;

public class CustomItemManager implements Listener {
  private static final CustomItemManager INSTANCE = new CustomItemManager();

  private final Map<String, BaseCustomItem> itemRegistry = new HashMap<>();
  private boolean hasRegisteredListener = false;
  private CustomItemListener listener;

  public CustomItemManager() {
  }

  public static CustomItemManager getManager() {
    return INSTANCE;
  }

  public void register(final BaseCustomItem... handlers) {
    for (final BaseCustomItem handler : handlers) {
      this.register(handler);
    }
  }

  public void register(final BaseCustomItem item) {
    itemRegistry.put(item.itemID, item);
    if (item.getRecipe() != null) {
      RecipesManager.getInstance().register(item.getRecipe());
    }

    if (!hasRegisteredListener) {
      listener = new CustomItemListener();
      Bukkit.getServer().getPluginManager().registerEvents(listener, ZenithPlugin.getInstance());
      hasRegisteredListener = true;
    }
  }

  public void unregister(final BaseCustomItem... handlers) {
    for (final BaseCustomItem handler : handlers) {
      unregister(handler);
    }
  }

  public void unregister(final BaseCustomItem item) {
    itemRegistry.remove(item.itemID, item);
    if (item.getRecipe() != null) {
      RecipesManager.getInstance().unregister(item.getRecipe());
    }
  }

  public BaseCustomItem get(final String id) {
    return itemRegistry.get(id);
  }

  public boolean has(final String id) {
    return itemRegistry.containsKey(id);
  }

  public Map<String, BaseCustomItem> getItems() {
    return itemRegistry;
  }

  public void unregisterAll() {
    itemRegistry.clear();
    RecipesManager.getInstance().unregisterAll();
    if (hasRegisteredListener) {
      HandlerList.unregisterAll(listener);
    }
  }
}