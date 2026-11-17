package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;

/**
 * Represents a custom stonecutting recipe.
 */
public class StonecuttingRecipe extends Recipe {
  private String group = "";

  /**
   * Creates a stonecutting recipe with the given name and type.
   *
   * @param recipeName the internal recipe name
   * @param type the recipe type
   */
  public StonecuttingRecipe(final String recipeName, final RecipeType type) {
    super(recipeName, type);
  }

  /**
   * Creates a stonecutting recipe with the given name, type, result, and input.
   *
   * @param recipeName the internal recipe name
   * @param type the recipe type
   * @param output the recipe result ingredient
   * @param input the recipe input ingredient
   */
  public StonecuttingRecipe(final String recipeName, final RecipeType type, final Ingredient output,
      final Ingredient input) {
    super(recipeName, type);
    setIngredients(input);
    setResult(output);
  }

  /**
   * Returns the recipe group.
   *
   * @return the recipe group
   */
  public String getGroup() {
    return group;
  }

  /**
   * Sets the recipe group.
   *
   * @param group the recipe group
   * @return this recipe instance
   * @throws IllegalArgumentException if {@code group} is {@code null}
   */
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
