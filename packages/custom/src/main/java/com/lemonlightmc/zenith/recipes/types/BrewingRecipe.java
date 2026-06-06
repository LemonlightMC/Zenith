package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;

public class BrewingRecipe extends Recipe {

  private Ingredient potion;

  public BrewingRecipe(String name, Ingredient result, Ingredient potion, Ingredient ingredient) {
    super(name, RecipeType.BREWING);
    this.potion = potion;
    setResult(result);
    setIngredients(ingredient);
  }

  public Ingredient setPotion() {
    return potion;
  }

  public BrewingRecipe setPotion(Ingredient potion) {
    this.potion = potion;
    return this;
  }

  @Override
  public org.bukkit.inventory.Recipe toBukkit() {
    throw new UnsupportedOperationException("Unimplemented method 'toBukkit'");
  }
}
