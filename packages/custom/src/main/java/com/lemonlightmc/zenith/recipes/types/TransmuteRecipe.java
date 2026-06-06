package com.lemonlightmc.zenith.recipes.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.RecipeType;

public class TransmuteRecipe extends CraftingRecipe {
  private final Ingredient input;
  private final Ingredient material;

  public TransmuteRecipe(String recipeName, ItemStack result, Ingredient input,
      Ingredient material) {
    super(recipeName, RecipeType.TRANSMUTE);
    setResult(result);
    this.input = input;
    this.material = material;
  }

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

  public Ingredient getInput() {
    return input.clone();
  }

  public Ingredient getMaterial() {
    return material.clone();
  }
}
