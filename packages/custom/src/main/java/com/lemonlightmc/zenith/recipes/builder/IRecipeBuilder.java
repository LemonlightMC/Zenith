package com.lemonlightmc.zenith.recipes.builder;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.types.IRecipe;

/**
 * Defines the contract for building custom recipes.
 */
public interface IRecipeBuilder extends Builder<IRecipe> {

  /**
   * Extends recipe building with smelting-specific options.
   */
  public static interface ISmeltingRecipeBuilder extends IRecipeBuilder {
    /**
     * Sets the recipe group.
     *
     * @param group the recipe group
     * @return this builder instance
     */
    IRecipeBuilder group(String group);

    /**
     * Returns the recipe group.
     *
     * @return the recipe group
     */
    String group();

    /**
     * Sets the cooking recipe book category.
     *
     * @param category the recipe category
     * @return this builder instance
     */
    IRecipeBuilder category(CookingBookCategory category);

    /**
     * Returns the cooking recipe book category.
     *
     * @return the recipe category
     */
    CookingBookCategory category();

    /**
     * Sets the cooking time in ticks.
     *
     * @param cookingTime the cooking time in ticks
     * @return this builder instance
     */
    IRecipeBuilder cookingTime(int cookingTime);

    /**
     * Returns the cooking time in ticks.
     *
     * @return the cooking time
     */
    int cookingTime();

    /**
     * Sets the experience reward.
     *
     * @param experience the experience reward
     * @return this builder instance
     */
    IRecipeBuilder experience(float experience);

    /**
     * Returns the experience reward.
     *
     * @return the experience reward
     */
    float experience();
  }

  /**
   * Extends recipe building with crafting-specific options.
   */
  public static interface ICraftingRecipeBuilder {
    /**
     * Sets the recipe group.
     *
     * @param group the recipe group
     * @return this builder instance
     */
    IRecipeBuilder group(String group);

    /**
     * Returns the recipe group.
     *
     * @return the recipe group
     */
    String group();

    /**
     * Sets the crafting recipe book category.
     *
     * @param category the recipe category
     * @return this builder instance
     */
    IRecipeBuilder category(CraftingBookCategory category);

    /**
     * Returns the crafting recipe book category.
     *
     * @return the recipe category
     */
    CraftingBookCategory category();

    /**
     * Sets the shaped crafting pattern.
     *
     * @param pattern the crafting pattern rows
     * @return this builder instance
     */
    IRecipeBuilder pattern(String... pattern);

    /**
     * Returns the shaped crafting pattern.
     *
     * @return the crafting pattern rows
     */
    String[] pattern();
  }

  /**
   * Sets the internal recipe name.
   *
   * @param name the recipe name
   * @return this builder instance
   */
  IRecipeBuilder name(String name);

  /**
   * Returns the internal recipe name.
   *
   * @return the recipe name
   */
  String name();

  /**
   * Sets the recipe result item.
   *
   * @param result the result item
   * @return this builder instance
   */
  IRecipeBuilder result(ItemStack result);

  /**
   * Returns the recipe result item.
   *
   * @return the result item
   */
  ItemStack result();

  /**
   * Sets the result amount.
   *
   * @param amount the result amount
   * @return this builder instance
   */
  IRecipeBuilder amount(int amount);

  /**
   * Returns the result amount.
   *
   * @return the result amount
   */
  int amount();

  /**
   * Sets the recipe type.
   *
   * @param type the recipe type
   * @return this builder instance
   */
  IRecipeBuilder type(RecipeType type);

  /**
   * Returns the recipe type.
   *
   * @return the recipe type
   */
  RecipeType type();

  /**
   * Returns the configured ingredients.
   *
   * @return the ingredient list
   */
  List<Ingredient> ingredients();

  /**
   * Adds an ingredient to the recipe.
   *
   * @param ingredient the ingredient to add
   * @return this builder instance
   */
  IRecipeBuilder ingredients(Ingredient ingredient);

  /**
   * Adds ingredients to the recipe.
   *
   * @param ingredients the ingredients to add
   * @return this builder instance
   */
  IRecipeBuilder ingredients(Ingredient... ingredients);

  /**
   * Adds ingredients to the recipe.
   *
   * @param ingredients the ingredients to add
   * @return this builder instance
   */
  IRecipeBuilder ingredients(List<Ingredient> ingredients);

  /**
   * Adds a tag-based ingredient to the recipe.
   *
   * @param tag the material tag
   * @return this builder instance
   */
  IRecipeBuilder ingredients(Tag<Material> tag);

  /**
   * Adds a tag-based ingredient to the recipe.
   *
   * @param tag the material tag
   * @param sign the shaped recipe sign
   * @return this builder instance
   */
  IRecipeBuilder ingredients(Tag<Material> tag, Character sign);

  /**
   * Adds an item-based ingredient to the recipe.
   *
   * @param item the ingredient item
   * @return this builder instance
   */
  IRecipeBuilder ingredients(ItemStack item);

  /**
   * Adds an item-based ingredient to the recipe.
   *
   * @param item the ingredient item
   * @param strict whether the ingredient should be matched strictly
   * @return this builder instance
   */
  IRecipeBuilder ingredients(ItemStack item, boolean strict);

  /**
   * Adds an item-based ingredient to the recipe.
   *
   * @param item the ingredient item
   * @param sign the shaped recipe sign
   * @return this builder instance
   */
  IRecipeBuilder ingredients(ItemStack item, Character sign);

  /**
   * Adds an item-based ingredient to the recipe.
   *
   * @param item the ingredient item
   * @param sign the shaped recipe sign
   * @param strict whether the ingredient should be matched strictly
   * @return this builder instance
   */
  IRecipeBuilder ingredients(ItemStack item, Character sign, boolean strict);

  /**
   * Adds a material ingredient to the recipe.
   *
   * @param material the ingredient material
   * @return this builder instance
   */
  IRecipeBuilder ingredients(Material material);

  /**
   * Adds a material ingredient to the recipe.
   *
   * @param material the ingredient material
   * @param sign the shaped recipe sign
   * @return this builder instance
   */
  IRecipeBuilder ingredients(Material material, Character sign);

  @Override
  IRecipe build();

  /**
   * Builds the recipe using the provided constructor arguments.
   *
   * @param args the recipe constructor arguments
   * @return the built recipe
   */
  IRecipe build(Object... args);

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

  @Override
  String toString();

}
