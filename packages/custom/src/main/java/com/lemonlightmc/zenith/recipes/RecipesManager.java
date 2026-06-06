package com.lemonlightmc.zenith.recipes;

import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.recipes.types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public final class RecipesManager {

  private static RecipesManager instance;
  private final HashMap<NamespacedKey, Recipe> recipes;
  private final HashMap<ItemStack, ArrayList<NamespacedKey>> itemCache;
  private PrepareCraftListener listener;

  public RecipesManager() {
    recipes = new HashMap<>();
    itemCache = new HashMap<>();
  }

  public static RecipesManager getInstance() {
    if (instance == null) {
      instance = new RecipesManager();
    }
    return instance;
  }

  public void init() {
    // listener = new PrepareCraftListener();
    // Zenith.getInstance().getServer().getPluginManager().registerEvents(listener,
    // Zenith.getInstance());
  }

  public void shutdown() {
    unregisterAll();
    HandlerList.unregisterAll(listener);
  }

  public void register(final Recipe recipe) {
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe cannot be null");
    }
    if (hasRecipe(recipe)) {
      throw new IllegalArgumentException("Recipe already registered");
    }
    recipes.put(recipe.getKey(), recipe);
    addToItemCache(recipe);
    if (ZenithPlugin.getInstance().getServer().getRecipe(recipe.getKey()) == null) {
      ZenithPlugin.getInstance().getServer().addRecipe(recipe.toBukkit());
    }
    if (listener == null) {
      init();
    }
  }

  public void unregister(final NamespacedKey key) {
    if (key == null) {
      return;
    }
    ZenithPlugin.getInstance().getServer().removeRecipe(key);
    recipes.remove(key);
    removeFromCache(key);
  }

  public void unregister(final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    unregister(recipe.getKey());
  }

  public void unregisterAll() {
    for (final NamespacedKey key : recipes.keySet()) {
      ZenithPlugin.getInstance().getServer().removeRecipe(key);
    }
    recipes.clear();
    itemCache.clear();
  }

  public Recipe getRecipe(final NamespacedKey key) {
    if (key == null) {
      return null;
    }
    return recipes.get(key);
  }

  @SuppressWarnings("unchecked")
  public <T extends Recipe> T getRecipe(final NamespacedKey key, final RecipeType type) {
    if (key == null || type == null) {
      return null;
    }
    final Recipe recipe = recipes.get(key);
    return recipe != null && recipe.getType().equals(type) ? (T) type.getRecipeClass().cast(recipe) : null;
  }

  public List<Recipe> getRecipe(final ItemStack item) {
    if (item == null) {
      return null;
    }
    return itemCache.get(item).stream().map((keys) -> recipes.get(keys)).toList();
  }

  @SuppressWarnings("unchecked")
  public <T extends Recipe> List<T> getRecipe(final ItemStack item, final RecipeType type) {
    if (item == null || type == null) {
      return null;
    }
    return itemCache.get(item).stream().map((keys) -> {
      final Recipe recipe = recipes.get(keys);
      return recipe != null && recipe.getType().equals(type) ? (T) type.getRecipeClass().cast(recipe) : null;
    }).toList();
  }

  public boolean hasRecipe(final Recipe recipe) {
    return recipe != null && recipes.containsValue(recipe);
  }

  public HashMap<NamespacedKey, Recipe> getRecipes() {
    return recipes;
  }

  public List<Recipe> getRecipesList() {
    return new ArrayList<>(recipes.values());
  }

  public org.bukkit.inventory.Recipe getBukkitRecipe(final NamespacedKey key) {
    if (key == null) {
      return null;
    }
    return ZenithPlugin.getInstance().getServer().getRecipe(key);
  }

  public List<org.bukkit.inventory.Recipe> getBukkitRecipe(final ItemStack item) {
    if (item == null) {
      return null;
    }
    return ZenithPlugin.getInstance().getServer().getRecipesFor(item);
  }

  public void grantRecipe(final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    grantRecipe(recipe.getKey());
  }

  public void grantRecipe(final NamespacedKey key) {
    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
      grantRecipe(player, key);
    }
  }

  public void grantRecipe(final Player player, final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    grantRecipe(player, recipe.getKey());
  }

  public void grantRecipe(final Player player, final NamespacedKey key) {
    if (player == null || key == null || player.hasDiscoveredRecipe(key))
      return;
    player.discoverRecipe(key);
  }

  public void revokeRecipe(final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    revokeRecipe(recipe.getKey());
  }

  public void revokeRecipe(final NamespacedKey key) {
    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
      revokeRecipe(player, key);
    }
  }

  public void revokeRecipe(final Player player, final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    revokeRecipe(player, recipe.getKey());
  }

  public void revokeRecipe(final Player player, final NamespacedKey key) {
    if (player == null || key == null || !player.hasDiscoveredRecipe(key))
      return;
    player.undiscoverRecipe(key);
  }

  public boolean hasDiscovered(final Player player, final Recipe recipe) {
    return recipe != null && hasDiscovered(player, recipe.getKey());
  }

  public boolean hasDiscovered(final Player player, final NamespacedKey key) {
    return player != null && key != null && player.hasDiscoveredRecipe(key);
  }

  @Override
  public int hashCode() {
    return 31 * recipes.hashCode() + 961;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final RecipesManager other = (RecipesManager) obj;
    if (recipes == null && other.recipes != null) {
      return false;
    }
    return recipes.equals(other.recipes);
  }

  @Override
  public String toString() {
    return "RecipesManager [recipes=" + recipes + "]";
  }

  private void addToItemCache(final Recipe recipe) {
    final ItemStack item = recipe.getResult().item();
    ArrayList<NamespacedKey> keys = itemCache.get(item);
    if (keys == null) {
      keys = new ArrayList<>();
      itemCache.put(item, keys);
    }
    keys.add(recipe.getKey());
  }

  private void removeFromCache(final NamespacedKey key) {
    final Iterator<ArrayList<NamespacedKey>> iter = itemCache.values().iterator();
    while (iter.hasNext()) {
      final ArrayList<NamespacedKey> keys = iter.next();
      if (keys.remove(key) || keys.size() == 0) {
        iter.remove();
      }
    }
  }
}
