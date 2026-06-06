package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.RecipeType;

public class SmokingRecipe extends SmeltingRecipe {

  public SmokingRecipe(final String recipeName) {
    super(RecipeType.SMOKING, recipeName);
  }

  public SmokingRecipe(final String recipeName, final float experience, final int burningTime) {
    super(RecipeType.SMOKING, recipeName, experience, burningTime);
  }

  @Override
  public org.bukkit.inventory.SmokingRecipe toBukkit() {
    final org.bukkit.inventory.SmokingRecipe recipe = new org.bukkit.inventory.SmokingRecipe(getKey(),
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