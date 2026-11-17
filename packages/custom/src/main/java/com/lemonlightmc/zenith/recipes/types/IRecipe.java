package com.lemonlightmc.zenith.recipes.types;

import java.util.List;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Represents a custom recipe definition that can be converted to a Bukkit recipe.
 */
public interface IRecipe extends Keyed {

  /**
   * Returns the internal name of this recipe.
   *
   * @return the recipe name
   */
  public String getName();

  /**
   * Returns the type of this recipe.
   *
   * @return the recipe type
   */
  public RecipeType getType();

  /**
   * Converts this recipe into its Bukkit representation.
   *
   * @return the Bukkit recipe
   */
  public org.bukkit.inventory.Recipe toBukkit();

  /**
   * Returns the recipe result ingredient.
   *
   * @return the result ingredient
   */
  public Ingredient getResult();

  /**
   * Sets the recipe result from an ingredient.
   *
   * @param output the result ingredient
   * @return this recipe instance
   */
  public IRecipe setResult(final Ingredient output);

  /**
   * Sets the recipe result from a recipe choice.
   *
   * @param output the result choice
   * @return this recipe instance
   */
  public IRecipe setResult(final RecipeChoice output);

  /**
   * Sets the recipe result from a material.
   *
   * @param output the result material
   * @return this recipe instance
   */
  public IRecipe setResult(final Material output);

  /**
   * Sets the recipe result from an item stack.
   *
   * @param output the result item stack
   * @return this recipe instance
   */
  public IRecipe setResult(final ItemStack output);

  /**
   * Sets the recipe result from an item stack.
   *
   * @param output the result item stack
   * @param strict whether the item should be matched strictly
   * @return this recipe instance
   */
  public IRecipe setResult(final ItemStack output, boolean strict);

  /**
   * Returns the ingredients used by this recipe.
   *
   * @return the recipe ingredients
   */
  public List<Ingredient> getIngredients();

  /**
   * Sets the recipe ingredients.
   *
   * @param ingredients the ingredients to assign
   * @return this recipe instance
   */
  public IRecipe setIngredients(final List<Ingredient> ingredients);

  /**
   * Sets a single recipe ingredient.
   *
   * @param ingredients the ingredient to assign
   * @return this recipe instance
   */
  public IRecipe setIngredients(final Ingredient ingredients);

  /**
   * Sets the recipe ingredient from a recipe choice.
   *
   * @param ingredient the ingredient choice
   * @return this recipe instance
   */
  public IRecipe setIngredients(final RecipeChoice ingredient);

  /**
   * Sets the recipe ingredient from a material.
   *
   * @param ingredient the ingredient material
   * @return this recipe instance
   */
  public IRecipe setIngredients(final Material ingredient);

  /**
   * Sets the recipe ingredient from an item stack.
   *
   * @param ingredient the ingredient item stack
   * @return this recipe instance
   */
  public IRecipe setIngredients(final ItemStack ingredient);

  /**
   * Sets the recipe ingredient from an item stack.
   *
   * @param ingredient the ingredient item stack
   * @param strict whether the item should be matched strictly
   * @return this recipe instance
   */
  public IRecipe setIngredients(ItemStack ingredient, boolean strict);

  @Override
  public int hashCode();

  @Override
  public boolean equals(final Object obj);

  @Override
  public String toString();
}
