package com.lemonlightmc.zenith.recipes.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Represents a custom transmute crafting recipe.
 */
public class TransmuteRecipe extends CraftingRecipe {
  private final Ingredient input;
  private final Ingredient material;

  /**
   * Creates a transmute recipe with an item stack result.
   *
   * @param recipeName the internal recipe name
   * @param result the result item stack
   * @param input the input ingredient
   * @param material the material ingredient
   */
  public TransmuteRecipe(String recipeName, ItemStack result, Ingredient input,
      Ingredient material) {
    super(recipeName, RecipeType.TRANSMUTE);
    setResult(result);
    this.input = input;
    this.material = material;
  }

  /**
   * Creates a transmute recipe with a material result.
   *
   * @param recipeName the internal recipe name
   * @param result the result material
   * @param input the input ingredient
   * @param material the material ingredient
   */
  public TransmuteRecipe(String recipeName, Material result, Ingredient input,
      Ingredient material) {
    super(recipeName, RecipeType.TRANSMUTE);
    setResult(result);
    this.input = input;
    this.material = material;
  }

  @Override
  public org.bukkit.inventory.Recipe toBukkit() {
    final org.bukkit.inventory.TransmuteRecipe recipe = new org.bukkit.inventory.TransmuteRecipe(
        getKey(),
        this.output.item(), input.choice(), material.choice());
    return recipe;
  }

  /**
   * Returns a copy of the input ingredient.
   *
   * @return the input ingredient
   */
  public Ingredient getInput() {
    return input.clone();
  }

  /**
   * Returns a copy of the material ingredient.
   *
   * @return the material ingredient
   */
  public Ingredient getMaterial() {
    return material.clone();
  }
}
