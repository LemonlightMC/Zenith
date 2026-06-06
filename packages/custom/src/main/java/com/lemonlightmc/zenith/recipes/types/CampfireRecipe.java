package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.RecipeType;

public class CampfireRecipe extends SmeltingRecipe {

  public CampfireRecipe(final String recipeName) {
    super(RecipeType.CAMPFIRE_COOKING, recipeName);
  }

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