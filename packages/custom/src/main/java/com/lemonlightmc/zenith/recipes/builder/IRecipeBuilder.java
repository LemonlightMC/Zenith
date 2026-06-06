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

public interface IRecipeBuilder extends Builder<IRecipe> {

  public static interface ISmeltingRecipeBuilder extends IRecipeBuilder {
    IRecipeBuilder group(String group);

    String group();

    IRecipeBuilder category(CookingBookCategory category);

    CookingBookCategory category();

    IRecipeBuilder cookingTime(int cookingTime);

    int cookingTime();

    IRecipeBuilder experience(float experience);

    float experience();
  }

  public static interface ICraftingRecipeBuilder {
    IRecipeBuilder group(String group);

    String group();

    IRecipeBuilder category(CraftingBookCategory category);

    CraftingBookCategory category();

    IRecipeBuilder pattern(String... pattern);

    String[] pattern();
  }

  IRecipeBuilder name(String name);

  String name();

  IRecipeBuilder result(ItemStack result);

  ItemStack result();

  IRecipeBuilder amount(int amount);

  int amount();

  IRecipeBuilder type(RecipeType type);

  RecipeType type();

  List<Ingredient> ingredients();

  IRecipeBuilder ingredients(Ingredient ingredient);

  IRecipeBuilder ingredients(Ingredient... ingredients);

  IRecipeBuilder ingredients(List<Ingredient> ingredients);

  IRecipeBuilder ingredients(Tag<Material> tag);

  IRecipeBuilder ingredients(Tag<Material> tag, Character sign);

  IRecipeBuilder ingredients(ItemStack item);

  IRecipeBuilder ingredients(ItemStack item, boolean strict);

  IRecipeBuilder ingredients(ItemStack item, Character sign);

  IRecipeBuilder ingredients(ItemStack item, Character sign, boolean strict);

  IRecipeBuilder ingredients(Material material);

  IRecipeBuilder ingredients(Material material, Character sign);

  @Override
  IRecipe build();

  IRecipe build(Object... args);

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

  @Override
  String toString();

}