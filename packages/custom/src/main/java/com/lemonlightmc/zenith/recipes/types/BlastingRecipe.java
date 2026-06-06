package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.RecipeType;

public class BlastingRecipe extends SmeltingRecipe {

  public BlastingRecipe(final String recipeName) {
    super(RecipeType.BLASTING, recipeName);
  }

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