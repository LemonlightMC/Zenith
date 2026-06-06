package com.lemonlightmc.zenith.recipes.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import com.lemonlightmc.zenith.recipes.Ingredients;
import com.lemonlightmc.zenith.recipes.RecipeType;
import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.Ingredients.ItemIngredient;
import com.lemonlightmc.zenith.recipes.Ingredients.MaterialIngredient;
import com.lemonlightmc.zenith.recipes.Ingredients.StrictItemIngredient;

public abstract class Recipe implements IRecipe {

  protected final String name;
  protected final RecipeType type;
  protected Ingredient output;
  protected List<Ingredient> ingredients;

  public Recipe(final String recipeName, final RecipeType type) {
    this.name = recipeName;
    this.type = type;
  }

  @Override
  public NamespacedKey getKey() {
    return type.getNamespacedKey(name);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public RecipeType getType() {
    return type;
  }

  @Override
  public Ingredient getResult() {
    return output;
  }

  @Override
  public Recipe setResult(final Ingredient output) {
    if (output == null) {
      throw new IllegalArgumentException("Output item cannot be null");
    }
    this.output = output;
    return this;
  }

  @Override
  public Recipe setResult(final RecipeChoice output) {
    this.output = Ingredients.fromChoice(output).get(0);
    return this;
  }

  @Override
  public Recipe setResult(final Material output) {
    this.output = new MaterialIngredient(output);
    return this;
  }

  @Override
  public Recipe setResult(final ItemStack output) {
    this.output = new ItemIngredient(output);
    return this;
  }

  @Override
  public Recipe setResult(final ItemStack output, final boolean strict) {
    if (strict) {
      this.output = new StrictItemIngredient(output);
    } else {
      this.output = new ItemIngredient(output);
    }
    return this;
  }

  @Override
  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  @Override
  public Recipe setIngredients(final List<Ingredient> ingredients) {
    if (ingredients == null || ingredients.isEmpty()) {
      throw new IllegalArgumentException("Ingredients cannot be null or empty");
    }
    this.ingredients = ingredients;
    return this;
  }

  @Override
  public Recipe setIngredients(final Ingredient ingredients) {
    if (ingredients == null) {
      throw new IllegalArgumentException("Ingredients cannot be null or empty");
    }
    this.ingredients = List.of(ingredients);
    return this;
  }

  @Override
  public Recipe setIngredients(final RecipeChoice choice) {
    this.ingredients = Ingredients.fromChoice(choice);
    return this;
  }

  @Override
  public Recipe setIngredients(final Material ingredient) {
    this.ingredients = List.of(new MaterialIngredient(ingredient));
    return this;
  }

  @Override
  public Recipe setIngredients(final ItemStack ingredient) {
    this.ingredients = List.of(new ItemIngredient(ingredient));
    return this;
  }

  @Override
  public Recipe setIngredients(final ItemStack ingredient, final boolean strict) {
    if (strict) {
      this.ingredients = List.of(new ItemIngredient(ingredient));
    } else {
      this.ingredients = List.of(new MaterialIngredient(ingredient.getType()));
    }
    return this;
  }

  public List<ItemStack> getItemList() {
    final ArrayList<ItemStack> result = new ArrayList<ItemStack>(ingredients.size());
    for (final Ingredient ingredient : ingredients) {
      result.add(ingredient.item().clone());
    }
    return result;
  }

  public List<RecipeChoice> getChoiceList() {
    final List<RecipeChoice> result = new ArrayList<>(ingredients.size());
    for (final Ingredient ingredient : ingredients) {
      result.add(ingredient.choice());
    }
    return result;
  }

  @Override
  public int hashCode() {
    int result = 31 + ((name == null) ? 0 : name.hashCode());
    result = 31 * result + ((type == null) ? 0 : type.hashCode());
    result = 31 * result + ((output == null) ? 0 : output.hashCode());
    return 31 * result + ((ingredients == null) ? 0 : ingredients.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Recipe other = (Recipe) obj;
    if (name == null && other.name != null || output == null && other.output != null
        || ingredients == null && other.ingredients != null) {
      return false;
    }
    return name.equals(other.name) && type == other.type && output.equals(other.output)
        && ingredients.equals(other.ingredients);
  }

  @Override
  public String toString() {
    return "Recipe [name=" + name + ", type=" + type + ", output=" + output + ", ingredients=" + ingredients + "]";
  }
}
