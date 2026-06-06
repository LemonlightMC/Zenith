package com.lemonlightmc.zenith.recipes.builder;

import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.builder.IRecipeBuilder.ICraftingRecipeBuilder;
import com.lemonlightmc.zenith.recipes.types.CraftingRecipe;
import com.lemonlightmc.zenith.recipes.types.Recipe;
import com.lemonlightmc.zenith.recipes.types.ShapedCraftingRecipe;
import com.lemonlightmc.zenith.recipes.types.ShapelessCraftingRecipe;

import java.util.Arrays;

import org.bukkit.inventory.recipe.CraftingBookCategory;

public class CraftingRecipeBuilder extends RecipeBuilder implements ICraftingRecipeBuilder {

  protected String group = "";
  protected CraftingBookCategory category = CraftingBookCategory.MISC;
  protected String[] pattern;

  @Override
  public CraftingRecipeBuilder group(final String group) {
    if (type == null) {
      throw new IllegalArgumentException("Recipe type is not set");
    }
    this.group = group;
    return this;
  }

  @Override
  public String group() {
    return group;
  }

  @Override
  public CraftingRecipeBuilder category(final CraftingBookCategory category) {
    if (type == null) {
      throw new IllegalArgumentException("Recipe type is not set");
    }
    this.category = category;
    return this;
  }

  @Override
  public CraftingBookCategory category() {
    return category;
  }

  @Override
  public CraftingRecipeBuilder pattern(final String... pattern) {
    if (type != RecipeType.CRAFTING_SHAPED) {
      throw new IllegalArgumentException("Recipe type is not a shaped recipe");
    }
    if (pattern.length > 3) {
      throw new IllegalArgumentException("Pattern is too long");
    }

    final boolean areLengthsValid = Arrays.stream(pattern)
        .map(String::length)
        .allMatch(len -> len >= 1 && len <= 3) &&
        Arrays.stream(pattern)
            .map(String::length)
            .distinct()
            .count() == 1;

    if (!areLengthsValid) {
      throw new IllegalArgumentException("Pattern is not valid");
    }

    this.pattern = pattern;
    return this;
  }

  @Override
  public String[] pattern() {
    return pattern;
  }

  @Override
  public Recipe build() {
    CraftingRecipe recipe;
    if (type == RecipeType.CRAFTING_SHAPED) {
      if (pattern == null || pattern.length == 0) {
        throw new IllegalArgumentException("Pattern is not set or empty");
      }
      for (final Ingredient ingredient : ingredientList) {
        if (ingredient.sign() == null) {
          throw new IllegalArgumentException("Ingredient sign is not set");
        }
        if (Arrays.stream(pattern)
            .flatMapToInt(String::chars)
            .noneMatch(c -> c == ingredient.sign())) {
          throw new IllegalArgumentException("Pattern does not contain the ingredient sign");
        }
      }
      recipe = ((ShapedCraftingRecipe) super.build(name)).shape(pattern);
    } else {
      recipe = (ShapelessCraftingRecipe) super.build(name);
    }
    recipe.setCategory(category);
    recipe.setGroup(group);
    recipe.setIngredients(ingredientList);
    recipe.setResult(result);
    return recipe;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + ((group == null) ? 0 : group.hashCode());
    result = 31 * result + ((category == null) ? 0 : category.hashCode());
    return 31 * result + Arrays.hashCode(pattern);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    final CraftingRecipeBuilder other = (CraftingRecipeBuilder) obj;
    if (group == null && other.group != null) {
      return false;
    }
    return Arrays.equals(pattern, other.pattern) && group.equals(other.group) && category == other.category;
  }

  @Override
  public String toString() {
    return "CraftingRecipeBuilder [name=" + name + ", result=" + result + ", amount=" + amount + ", type=" + type
        + ", group()=" + group + ", category()=" + category + ", pattern()=" + Arrays.toString(pattern)
        + ", ingredients()=" + ingredientList + "]";
  }

}
