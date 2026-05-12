package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.RecipeType;

public class FurnaceRecipe extends SmeltingRecipe {

  public FurnaceRecipe(final String recipeName) {
    super(RecipeType.SMELTING, recipeName);
  }

  public FurnaceRecipe(final String recipeName, final float experience, final int burningTime) {
    super(RecipeType.SMELTING, recipeName, experience, burningTime);
  }

  @Override
  public org.bukkit.inventory.FurnaceRecipe toBukkit() {
    final org.bukkit.inventory.FurnaceRecipe recipe = new org.bukkit.inventory.FurnaceRecipe(getKey(),
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