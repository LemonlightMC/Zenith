package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Represents a custom blasting recipe.
 */
public class BlastingRecipe extends SmeltingRecipe {

  /**
   * Creates a blasting recipe with the given name.
   *
   * @param recipeName the internal recipe name
   */
  public BlastingRecipe(final String recipeName) {
    super(RecipeType.BLASTING, recipeName);
  }

  /**
   * Creates a blasting recipe with the given name, experience, and burning time.
   *
   * @param recipeName the internal recipe name
   * @param experience the experience reward
   * @param burningTime the cooking time in ticks
   */
  public BlastingRecipe(final String recipeName, final float experience, final int burningTime) {
    super(RecipeType.BLASTING, recipeName, experience, burningTime);
  }

  @Override
  public org.bukkit.inventory.BlastingRecipe toBukkit() {
    final org.bukkit.inventory.BlastingRecipe recipe = new org.bukkit.inventory.BlastingRecipe(getKey(),
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
