package com.lemonlightmc.zenith.recipes.types;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.RecipeType;

public class ShapedCraftingRecipe extends CraftingRecipe {
  private String[] rows;
  private int size = 3;
  private Map<Character, Ingredient> ingredients = new HashMap<>();

  public ShapedCraftingRecipe(final String recipeName) {
    super(recipeName, RecipeType.CRAFTING_SHAPED);
  }

  public String[] getShape() {
    return rows.clone();
  }

  public String getShapeAsString() {
    final StringBuilder shape = new StringBuilder();
    for (final String row : rows) {
      shape.append(row).append("-");
    }
    shape.deleteCharAt(shape.length() - 1);
    return shape.toString();
  }

  public String getRow(final int index) {
    return rows[index];
  }

  public String[] getRows() {
    return rows;
  }

  public int getSize() {
    return size;
  }

  public ShapedCraftingRecipe setSize(final int size) {
    if (size < 1 || size > 12) {
      throw new IllegalArgumentException("Size must be between 1 and 12 (Soft Cap)");
    }
    this.size = size;
    return this;
  }

  public org.bukkit.inventory.ShapedRecipe toBukkit() {
    final org.bukkit.inventory.ShapedRecipe recipe = new org.bukkit.inventory.ShapedRecipe(getKey(),
        this.output.item());
    if (size != 3) {
      throw new UnsupportedOperationException("Bukkit currently doesn't support custom Crafting Sizes!");
    }
    recipe.shape(rows);

    for (final Ingredient ingredient : ingredients.values()) {
      recipe.setIngredient(ingredient.sign(), ingredient.choice());
    }
    if (category != null) {
      recipe.setCategory(category);
    }
    if (group != null && group.length() > 0) {
      recipe.setGroup(group);
    }
    return recipe;
  }

  public ShapedCraftingRecipe shape(final String shape) {
    return shape(shape.split("/(?:[a-zA-Z]{" + size + "}-?){" + size + " }/"));
  }

  public ShapedCraftingRecipe shape(final String... shape) {
    if (shape == null) {
      throw new IllegalArgumentException("Shape cannot be null");
    }
    if (shape.length == 0 || shape.length > size) {
      throw new IllegalArgumentException("Shape Colonm Size must be between 0 and " + size);
    }

    int lastLen = -1;
    for (final String row : shape) {
      if (row == null) {
        throw new IllegalArgumentException("Shape rows cannot be null");
      }
      if (row.length() == 0 || row.length() > size) {
        throw new IllegalArgumentException("Shape Row Size must be between 0 and " + size);
      }
      if (lastLen != -1 && lastLen != row.length()) {
        throw new IllegalArgumentException("Crafting recipes must be rectangular");
      }
      lastLen = row.length();
    }
    this.rows = new String[size];
    for (int i = 0; i < size; i++) {
      this.rows[i] = shape[i];
    }

    // Remove character mappings for characters that no longer exist in the shape
    final HashMap<Character, Ingredient> newIngredients = new HashMap<>();
    for (final String row : this.rows) {
      for (final char c : row.toCharArray()) {
        // Space in recipe shape must represent no ingredient
        if (c == ' ') {
          continue;
        }
        newIngredients.put(c, ingredients.get(c));
      }
    }
    this.ingredients = newIngredients;

    return this;
  }

  public ShapedCraftingRecipe setIngredient(final char key, final Ingredient ingredient) {
    if (key == ' ') {
      throw new IllegalArgumentException(
          "Shape key cannot be null because Space in Recipe Shape must represent no Ingredient");
    }
    if (ingredient == null) {
      throw new IllegalArgumentException("Ingredient cannot be null");
    }
    if (!ingredients.containsKey(key)) {
      throw new IllegalArgumentException("The key '" + key + "' does not exist in the recipe shape");
    }

    ingredients.put(key, ingredient);
    return this;
  }

  public Map<Character, ItemStack> getItemMap() {
    final HashMap<Character, ItemStack> result = new HashMap<Character, ItemStack>();
    for (final Map.Entry<Character, Ingredient> ingredient : ingredients.entrySet()) {
      if (ingredient.getValue() == null) {
        result.put(ingredient.getKey(), null);
      } else {
        result.put(ingredient.getKey(), ingredient.getValue().item().clone());
      }
    }
    return result;
  }

  public Map<Character, Ingredient> getIngredientMap() {
    final Map<Character, Ingredient> result = new HashMap<>();
    for (final Map.Entry<Character, Ingredient> ingredient : ingredients.entrySet()) {
      if (ingredient.getValue() == null) {
        result.put(ingredient.getKey(), null);
      } else {
        result.put(ingredient.getKey(), ingredient.getValue().clone());
      }
    }
    return result;
  }

  public Map<Character, RecipeChoice> getChoiceMap() {
    final Map<Character, RecipeChoice> result = new HashMap<>();
    for (final Map.Entry<Character, Ingredient> ingredient : ingredients.entrySet()) {
      if (ingredient.getValue() == null) {
        result.put(ingredient.getKey(), null);
      } else {
        result.put(ingredient.getKey(), ingredient.getValue().choice().clone());
      }
    }
    return result;
  }
}
