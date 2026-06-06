package com.lemonlightmc.zenith.recipes.types;

import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.RecipeChoice;
import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;

public class ShapelessCraftingRecipe extends CraftingRecipe {

  public ShapelessCraftingRecipe(final String recipeName) {
    super(recipeName, RecipeType.CRAFTING_SHAPELESS);
  }

  @Override
  public org.bukkit.inventory.ShapelessRecipe toBukkit() {
    final org.bukkit.inventory.ShapelessRecipe recipe = new org.bukkit.inventory.ShapelessRecipe(getKey(),
        this.output.item());
    for (final Ingredient ingredient : ingredients) {
      recipe.addIngredient(ingredient.choice());
    }
    if (category != null) {
      recipe.setCategory(category);
    }
    if (group != null && group.length() > 0) {
      recipe.setGroup(group);
    }
    return recipe;
  }

  public ShapelessCraftingRecipe addIngredients(final Ingredient ingredient) {
    ingredients.add(ingredient);
    return this;
  }

  public ShapelessCraftingRecipe addIngredients(final Ingredient ingredient, int count) {
    if (count <= 0) {
      return this;
    }
    while (count-- > 0) {
      ingredients.add(ingredient);
    }
    return this;
  }

  public ShapelessCraftingRecipe addIngredients(final RecipeChoice choice) {
    final List<Ingredient> ingredients = Ingredients.fromChoice(choice);
    for (final Ingredient ingredient : ingredients) {
      ingredients.add(ingredient);
    }
    return this;
  }

  public ShapelessCraftingRecipe removeIngredients(final RecipeChoice choice) {
    final List<Ingredient> ingredients = Ingredients.fromChoice(choice);
    for (final Ingredient ingredient : ingredients) {
      ingredients.remove(ingredient);
    }
    return this;
  }

  public ShapelessCraftingRecipe removeIngredients(final Ingredient ingredient) {
    ingredients.remove(ingredient);
    return this;
  }

  public ShapelessCraftingRecipe removeIngredients(final Ingredient ingredient, int count) {
    final Iterator<Ingredient> iterator = ingredients.iterator();
    while (count > 0 && iterator.hasNext()) {
      final Ingredient item = iterator.next();
      if (ingredient.equals(item)) {
        iterator.remove();
        count--;
      }
    }
    return this;
  }
}
