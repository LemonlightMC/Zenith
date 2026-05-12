package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;

public class StonecuttingRecipe extends Recipe {
  private String group = "";

  public StonecuttingRecipe(final String recipeName, final RecipeType type) {
    super(recipeName, type);
  }

  public StonecuttingRecipe(final String recipeName, final RecipeType type, final Ingredient output,
      final Ingredient input) {
    super(recipeName, type);
    setIngredients(input);
    setResult(output);
  }

  public String getGroup() {
    return group;
  }

  public StonecuttingRecipe setGroup(final String group) {
    if (group == null) {
      throw new IllegalArgumentException("Group cannot be null");
    }
    this.group = group;
    return this;
  }

  @Override
  public org.bukkit.inventory.StonecuttingRecipe toBukkit() {
    final org.bukkit.inventory.StonecuttingRecipe recipe = new org.bukkit.inventory.StonecuttingRecipe(getKey(),
        this.output.item(), Ingredients.toExactChoice(this.ingredients));
    if (group != null && group.length() > 0) {
      recipe.setGroup(group);
    }
    return recipe;
  }

}
