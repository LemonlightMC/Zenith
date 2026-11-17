package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;

/**
 * Represents a custom brewing recipe.
 */
public class BrewingRecipe extends Recipe {

  private Ingredient potion;

  /**
   * Creates a brewing recipe.
   *
   * @param name the internal recipe name
   * @param result the recipe result ingredient
   * @param potion the base potion ingredient
   * @param ingredient the brewing ingredient
   */
  public BrewingRecipe(String name, Ingredient result, Ingredient potion, Ingredient ingredient) {
    super(name, RecipeType.BREWING);
    this.potion = potion;
    setResult(result);
    setIngredients(ingredient);
  }

  /**
   * Returns the base potion ingredient.
   *
   * @return the potion ingredient
   */
  public Ingredient setPotion() {
    return potion;
  }

  /**
   * Sets the base potion ingredient.
   *
   * @param potion the potion ingredient
   * @return this recipe instance
   */
  public BrewingRecipe setPotion(Ingredient potion) {
    this.potion = potion;
    return this;
  }

  @Override
  public org.bukkit.inventory.Recipe toBukkit() {
    throw new UnsupportedOperationException("Unimplemented method 'toBukkit'");
  }
}
