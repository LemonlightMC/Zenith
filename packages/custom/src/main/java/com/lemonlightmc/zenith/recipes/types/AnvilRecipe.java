package com.lemonlightmc.zenith.recipes.types;

import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Represents a custom anvil recipe.
 */
public class AnvilRecipe extends Recipe {

  private Ingredient base;
  private Ingredient addition;
  private int costLevel = 0;

  /**
   * Creates an anvil recipe.
   *
   * @param name the internal recipe name
   * @param output the recipe result ingredient
   * @param base the base ingredient
   * @param addition the addition ingredient
   */
  public AnvilRecipe(String name, Ingredient output, Ingredient base,
      Ingredient addition) {
    super(name, RecipeType.ANVIL);
    this.base = base;
    this.output = output;
    this.addition = addition;
  }

  /**
   * Returns the base ingredient.
   *
   * @return the base ingredient
   */
  public Ingredient getBase() {
    return base;
  }

  /**
   * Sets the base ingredient.
   *
   * @param base the base ingredient
   * @return this recipe instance
   */
  public AnvilRecipe setBase(Ingredient base) {
    this.base = base;
    return this;
  }

  /**
   * Returns the addition ingredient.
   *
   * @return the addition ingredient
   */
  public Ingredient getAddition() {
    return addition;
  }

  /**
   * Sets the addition ingredient.
   *
   * @param addition the addition ingredient
   * @return this recipe instance
   */
  public AnvilRecipe setAddition(Ingredient addition) {
    this.addition = addition;
    return this;
  }

  /**
   * Returns the anvil cost level.
   *
   * @return the cost level
   */
  public int costLevel() {
    return costLevel;
  }

  /**
   * Sets the anvil cost level.
   *
   * @param costLevel the cost level
   * @return this recipe instance
   */
  public AnvilRecipe setCostLevel(int costLevel) {
    this.costLevel = costLevel;
    return this;
  }

  @Override
  public org.bukkit.inventory.Recipe toBukkit() {
    throw new UnsupportedOperationException("Unimplemented method 'toBukkit'");
  }
}
