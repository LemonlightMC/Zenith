package com.lemonlightmc.zenith.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.recipes.types.Recipe;

/**
 * Manages registration, lookup, and discovery of custom recipes.
 *
 * <p>The manager tracks custom recipes by key, maintains a result-based cache, and synchronizes registrations with the
 * Bukkit server recipe registry.
 */
public final class RecipesManager {

  private static RecipesManager instance;
  private final HashMap<NamespacedKey, Recipe> recipes;
  private final HashMap<ItemStack, ArrayList<NamespacedKey>> itemCache;
  private PrepareCraftListener listener;

  /**
   * Creates a recipes manager.
   */
  public RecipesManager() {
    recipes = new HashMap<>();
    itemCache = new HashMap<>();
  }

  /**
   * Returns the shared recipes manager instance.
   *
   * @return the singleton recipes manager
   */
  public static RecipesManager getInstance() {
    if (instance == null) {
      instance = new RecipesManager();
    }
    return instance;
  }

  /**
   * Initializes recipe-related listeners for this manager.
   */
  public void init() {
    // listener = new PrepareCraftListener();
    // Zenith.getInstance().getServer().getPluginManager().registerEvents(listener,
    // Zenith.getInstance());
  }

  /**
   * Shuts down the manager and unregisters all managed recipes and listeners.
   */
  public void shutdown() {
    unregisterAll();
    HandlerList.unregisterAll(listener);
  }

  /**
   * Registers a custom recipe with the manager and Bukkit server.
   *
   * @param recipe the recipe to register
   * @throws IllegalArgumentException if the recipe is {@code null} or already registered
   */
  public void register(final Recipe recipe) {
    if (recipe == null) {
      throw new IllegalArgumentException("Recipe cannot be null");
    }
    if (hasRecipe(recipe)) {
      throw new IllegalArgumentException("Recipe already registered");
    }
    recipes.put(recipe.getKey(), recipe);
    addToItemCache(recipe);
    if (ZenithProvider.instance().getServer().getRecipe(recipe.getKey()) == null) {
      ZenithProvider.instance().getServer().addRecipe(recipe.toBukkit());
    }
    if (listener == null) {
      init();
    }
  }

  /**
   * Unregisters a recipe by its namespaced key.
   *
   * @param key the recipe key to unregister
   */
  public void unregister(final NamespacedKey key) {
    if (key == null) {
      return;
    }
    ZenithProvider.instance().getServer().removeRecipe(key);
    recipes.remove(key);
    removeFromCache(key);
  }

  /**
   * Unregisters the given recipe.
   *
   * @param recipe the recipe to unregister
   */
  public void unregister(final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    unregister(recipe.getKey());
  }

  /**
   * Unregisters all managed recipes from the manager and Bukkit server.
   */
  public void unregisterAll() {
    for (final NamespacedKey key : recipes.keySet()) {
      ZenithProvider.instance().getServer().removeRecipe(key);
    }
    recipes.clear();
    itemCache.clear();
  }

  /**
   * Returns the recipe registered under the given key.
   *
   * @param key the recipe key
   * @return the matching recipe, or {@code null} if none is found
   */
  public Recipe getRecipe(final NamespacedKey key) {
    if (key == null) {
      return null;
    }
    return recipes.get(key);
  }

  /**
   * Returns the recipe registered under the given key if it matches the specified type.
   *
   * @param key the recipe key
   * @param type the expected recipe type
   * @param <T> the recipe implementation type
   * @return the matching recipe, or {@code null} if none is found
   */
  @SuppressWarnings("unchecked")
  public <T extends Recipe> T getRecipe(final NamespacedKey key, final RecipeType type) {
    if (key == null || type == null) {
      return null;
    }
    final Recipe recipe = recipes.get(key);
    return recipe != null && recipe.getType().equals(type) ? (T) type.getRecipeClass().cast(recipe) : null;
  }

  /**
   * Returns all recipes that produce the given item.
   *
   * @param item the result item to match
   * @return the matching recipes, or {@code null} if the item is {@code null}
   */
  public List<Recipe> getRecipe(final ItemStack item) {
    if (item == null) {
      return null;
    }
    return itemCache.get(item).stream().map((keys) -> recipes.get(keys)).toList();
  }

  /**
   * Returns all recipes of the given type that produce the given item.
   *
   * @param item the result item to match
   * @param type the recipe type to filter by
   * @param <T> the recipe implementation type
   * @return the matching recipes, or {@code null} if the item or type is {@code null}
   */
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

  /**
   * Checks whether the given recipe is registered.
   *
   * @param recipe the recipe to check
   * @return {@code true} if the recipe is registered; {@code false} otherwise
   */
  public boolean hasRecipe(final Recipe recipe) {
    return recipe != null && recipes.containsValue(recipe);
  }

  /**
   * Returns the registered recipes keyed by their namespaced key.
   *
   * @return the registered recipe map
   */
  public HashMap<NamespacedKey, Recipe> getRecipes() {
    return recipes;
  }

  /**
   * Returns the registered recipes as a list.
   *
   * @return the registered recipes
   */
  public List<Recipe> getRecipesList() {
    return new ArrayList<>(recipes.values());
  }

  /**
   * Returns the Bukkit recipe registered under the given key.
   *
   * @param key the recipe key
   * @return the Bukkit recipe, or {@code null} if none is found
   */
  public org.bukkit.inventory.Recipe getBukkitRecipe(final NamespacedKey key) {
    if (key == null) {
      return null;
    }
    return ZenithProvider.instance().getServer().getRecipe(key);
  }

  /**
   * Returns Bukkit recipes that produce the given item.
   *
   * @param item the result item to match
   * @return the matching Bukkit recipes, or {@code null} if the item is {@code null}
   */
  public List<org.bukkit.inventory.Recipe> getBukkitRecipe(final ItemStack item) {
    if (item == null) {
      return null;
    }
    return ZenithProvider.instance().getServer().getRecipesFor(item);
  }

  /**
   * Grants the given recipe to all online players.
   *
   * @param recipe the recipe to grant
   */
  public void grantRecipe(final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    grantRecipe(recipe.getKey());
  }

  /**
   * Grants the recipe with the given key to all online players.
   *
   * @param key the recipe key to grant
   */
  public void grantRecipe(final NamespacedKey key) {
    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
      grantRecipe(player, key);
    }
  }

  /**
   * Grants the given recipe to the specified player.
   *
   * @param player the player to grant the recipe to
   * @param recipe the recipe to grant
   */
  public void grantRecipe(final Player player, final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    grantRecipe(player, recipe.getKey());
  }

  /**
   * Grants the recipe with the given key to the specified player.
   *
   * @param player the player to grant the recipe to
   * @param key the recipe key to grant
   */
  public void grantRecipe(final Player player, final NamespacedKey key) {
    if (player == null || key == null || player.hasDiscoveredRecipe(key))
      return;
    player.discoverRecipe(key);
  }

  /**
   * Revokes the given recipe from all online players.
   *
   * @param recipe the recipe to revoke
   */
  public void revokeRecipe(final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    revokeRecipe(recipe.getKey());
  }

  /**
   * Revokes the recipe with the given key from all online players.
   *
   * @param key the recipe key to revoke
   */
  public void revokeRecipe(final NamespacedKey key) {
    for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
      revokeRecipe(player, key);
    }
  }

  /**
   * Revokes the given recipe from the specified player.
   *
   * @param player the player to revoke the recipe from
   * @param recipe the recipe to revoke
   */
  public void revokeRecipe(final Player player, final Recipe recipe) {
    if (recipe == null) {
      return;
    }
    revokeRecipe(player, recipe.getKey());
  }

  /**
   * Revokes the recipe with the given key from the specified player.
   *
   * @param player the player to revoke the recipe from
   * @param key the recipe key to revoke
   */
  public void revokeRecipe(final Player player, final NamespacedKey key) {
    if (player == null || key == null || !player.hasDiscoveredRecipe(key))
      return;
    player.undiscoverRecipe(key);
  }

  /**
   * Checks whether the specified player has discovered the given recipe.
   *
   * @param player the player to check
   * @param recipe the recipe to check
   * @return {@code true} if the recipe has been discovered; {@code false} otherwise
   */
  public boolean hasDiscovered(final Player player, final Recipe recipe) {
    return recipe != null && hasDiscovered(player, recipe.getKey());
  }

  /**
   * Checks whether the specified player has discovered the recipe with the given key.
   *
   * @param player the player to check
   * @param key the recipe key to check
   * @return {@code true} if the recipe has been discovered; {@code false} otherwise
   */
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
