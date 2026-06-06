package com.lemonlightmc.zenith.recipes.builder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import com.lemonlightmc.zenith.messages.Logger;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.Ingredients.ItemIngredient;
import com.lemonlightmc.zenith.recipes.Ingredients.MaterialIngredient;
import com.lemonlightmc.zenith.recipes.Ingredients.StrictItemIngredient;
import com.lemonlightmc.zenith.recipes.Ingredients.TagIngredient;
import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.types.Recipe;

public class RecipeBuilder implements IRecipeBuilder {

  protected final List<Ingredient> ingredientList = new ArrayList<>();
  protected String name;
  protected ItemStack result;
  protected int amount = 1;
  protected RecipeType type;

  @Override
  public RecipeBuilder name(final String name) {
    if (type == null) {
      throw new IllegalArgumentException("Recipe type is not set");
    }
    this.name = name;
    return this;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public RecipeBuilder result(final ItemStack result) {
    if (type == null) {
      throw new IllegalArgumentException("Recipe type is not set");
    }
    this.result = result;
    return this;
  }

  @Override
  public ItemStack result() {
    return result;
  }

  @Override
  public RecipeBuilder amount(final int amount) {
    this.amount = amount;
    return this;
  }

  @Override
  public int amount() {
    return amount;
  }

  @Override
  public RecipeBuilder type(final RecipeType type) {
    this.type = type;
    return this;
  }

  @Override
  public RecipeType type() {
    return type;
  }

  /*
   * @Override
   * public RecipeBuilder pattern(final String... pattern) {
   * if (type == null) {
   * throw new IllegalArgumentException("Recipe type is not set");
   * }
   * if (type != RecipeType.CRAFTING_SHAPED) {
   * throw new IllegalArgumentException("Recipe type is not a shaped recipe");
   * }
   * if (pattern.length > 3) {
   * throw new IllegalArgumentException("Pattern is too long");
   * }
   * 
   * final boolean areLengthsValid = Arrays.stream(pattern)
   * .map(String::length)
   * .allMatch(len -> len >= 1 && len <= 3) &&
   * Arrays.stream(pattern)
   * .map(String::length)
   * .distinct()
   * .count() == 1;
   * 
   * if (!areLengthsValid) {
   * throw new IllegalArgumentException("Pattern is not valid");
   * }
   * 
   * this.pattern = pattern;
   * return this;
   * }
   * 
   * @Override
   * public String[] pattern() {
   * return pattern;
   * }
   */

  @Override
  public List<Ingredient> ingredients() {
    return ingredientList;
  }

  @Override
  public RecipeBuilder ingredients(final Ingredient ingredient) {
    if (type.getMaxIngredients() == ingredientList.size()) {
      Logger.warn("Too many ingredients");
      return this;
    }

    this.ingredientList.add(ingredient);
    return this;
  }

  @Override
  public RecipeBuilder ingredients(final Ingredient... ingredients) {
    ingredients(List.of(ingredients));
    return this;
  }

  @Override
  public RecipeBuilder ingredients(final List<Ingredient> ingredients) {
    if (ingredients == null || ingredients.size() == 0) {
      throw new IllegalArgumentException("No ingredients provided");
    }
    for (final Ingredient ingredient : ingredients) {
      ingredients(ingredient);
    }
    return this;
  }

  @Override
  public RecipeBuilder ingredients(final Tag<Material> tag) {
    return ingredients(tag, null);
  }

  @Override
  public RecipeBuilder ingredients(final Tag<Material> tag, final Character sign) {
    return ingredients(new TagIngredient(tag, sign));
  }

  @Override
  public RecipeBuilder ingredients(final ItemStack item) {
    return ingredients(new ItemIngredient(item, null));
  }

  @Override
  public RecipeBuilder ingredients(final ItemStack item, final Character sign) {
    return ingredients(new ItemIngredient(item, sign));
  }

  @Override
  public RecipeBuilder ingredients(final ItemStack item, final boolean strict) {
    return ingredients(item, null, strict);
  }

  @Override
  public RecipeBuilder ingredients(final ItemStack item, final Character sign, final boolean strict) {
    return ingredients(strict ? new StrictItemIngredient(item, sign)
        : new ItemIngredient(item, sign));
  }

  @Override
  public RecipeBuilder ingredients(final Material material) {
    return ingredients(material, null);
  }

  @Override
  public RecipeBuilder ingredients(final Material material, final Character sign) {
    return ingredients(new MaterialIngredient(material, sign));
  }

  @Override
  public Recipe build() {
    return build(name, result, ingredientList);
  }

  @Override
  public Recipe build(final Object... args) {
    if (name == null) {
      throw new IllegalArgumentException("Name is not set");
    }
    if (result == null) {
      throw new IllegalArgumentException("Result is not set");
    }
    if (type == null) {
      throw new IllegalArgumentException("Type is not set");
    }
    if (ingredientList.isEmpty()) {
      throw new IllegalArgumentException("Ingredients are not set");
    }
    /*
     * if (type == RecipeType.CRAFTING_SHAPED) {
     * if (pattern == null || pattern.length == 0) {
     * throw new IllegalArgumentException("Pattern is not set or empty");
     * }
     * for (final Ingredient ingredient : ingredientList) {
     * if (ingredient.sign() == null) {
     * throw new IllegalArgumentException("Ingredient sign is not set");
     * }
     * if (Arrays.stream(pattern)
     * .flatMapToInt(String::chars)
     * .noneMatch(c -> c == ingredient.sign())) {
     * throw new
     * IllegalArgumentException("Pattern does not contain the ingredient sign");
     * }
     * }
     * }
     */
    return type.newRecipe(args);
  }

  @Override
  public int hashCode() {
    int value = 31 + ingredientList.hashCode();
    value = 31 * value + ((name == null) ? 0 : name.hashCode());
    value = 31 * value + ((result == null) ? 0 : result.hashCode());
    value = 31 * value + ((type == null) ? 0 : type.hashCode());
    return 31 * value + amount;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final RecipeBuilder other = (RecipeBuilder) obj;
    if (name == null && other.name != null || result == null && other.result != null) {
      return false;
    }
    return name.equals(other.name) && result.equals(other.result) && amount == other.amount && type == other.type
        && ingredientList.equals(other.ingredientList);
  }

  @Override
  public String toString() {
    return "RecipeBuilder [ingredientList=" + ingredientList + ", name=" + name + ", result=" + result + ", amount="
        + amount + ", type=" + type + "]";
  }
}
