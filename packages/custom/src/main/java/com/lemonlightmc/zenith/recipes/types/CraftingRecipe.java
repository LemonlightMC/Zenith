package com.lemonlightmc.zenith.recipes.types;

import org.bukkit.inventory.recipe.CraftingBookCategory;

import com.lemonlightmc.zenith.recipes.RecipeType;

public abstract class CraftingRecipe extends Recipe {
  protected String group = "";
  protected CraftingBookCategory category = CraftingBookCategory.MISC;

  public CraftingRecipe(final String recipeName, final RecipeType type) {
    super(recipeName, type);
  }

  public String getGroup() {
    return group;
  }

  public CraftingRecipe setGroup(final String group) {
    if (group == null) {
      throw new IllegalArgumentException("Group cannot be null");
    }
    this.group = group;
    return this;
  }

  public CraftingBookCategory getCategory() {
    return category;
  }

  public CraftingRecipe setCategory(final CraftingBookCategory category) {
    if (category == null) {
      throw new IllegalArgumentException("Category cannot be null");
    }
    this.category = category;
    return this;
  }

  @Override
  public int hashCode() {
    int result = 31 + super.hashCode();
    result = 31 * result + ((group == null) ? 0 : group.hashCode());
    return 31 * result + ((category == null) ? 0 : category.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CraftingRecipe other = (CraftingRecipe) obj;
    if (group == null && other.group != null) {
      return false;
    }
    return super.equals(obj) && group.equals(other.group) && category == other.category;
  }

  @Override
  public String toString() {
    return "CraftingRecipe [name=" + name + ", type=" + type + "group=" + group + ", category=" + category + ",output="
        + output + ", ingredients=" + ingredients + "]";
  }

}
