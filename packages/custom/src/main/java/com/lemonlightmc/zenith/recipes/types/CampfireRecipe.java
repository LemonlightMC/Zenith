package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Represents a custom campfire cooking recipe.
 */
public class CampfireRecipe extends SmeltingRecipe {

  /**
   * Creates a campfire recipe with the given name.
   *
   * @param recipeName the internal recipe name
   */
  public CampfireRecipe(final String recipeName) {
    super(RecipeType.CAMPFIRE_COOKING, recipeName);
  }

  /**
   * Creates a campfire recipe with the given name, experience, and burning time.
   *
   * @param recipeName the internal recipe name
   * @param experience the experience reward
   * @param burningTime the cooking time in ticks
   */
  public CampfireRecipe(final String recipeName, final float experience, final int burningTime) {
    super(RecipeType.CAMPFIRE_COOKING, recipeName, experience, burningTime);
  }

  @Override
  public org.bukkit.inventory.CampfireRecipe toBukkit() {
    final org.bukkit.inventory.CampfireRecipe recipe = new org.bukkit.inventory.CampfireRecipe(getKey(),
        this.output.item(),
        Ingredients.toExactChoice(this.ingredients), experience, burningTime);
    if (category != null) {
      recipe.setCategory(category);
    }
    if (group != null && group.length() > 0) {
      recipe.setGroup(group);
    }
    return recipe;
  }
}
