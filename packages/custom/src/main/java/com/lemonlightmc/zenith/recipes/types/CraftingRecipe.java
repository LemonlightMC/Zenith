package com.lemonlightmc.zenith.recipes.types;

import org.bukkit.inventory.recipe.CraftingBookCategory;

import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Base implementation for crafting recipes.
 */
public abstract class CraftingRecipe extends Recipe {
  /** The crafting recipe group. */
  protected String group = "";
  /** The crafting recipe book category. */
  protected CraftingBookCategory category = CraftingBookCategory.MISC;

  /**
   * Creates a crafting recipe with the given name and type.
   *
   * @param recipeName the internal recipe name
   * @param type the recipe type
   */
  public CraftingRecipe(final String recipeName, final RecipeType type) {
    super(recipeName, type);
  }

  /**
   * Returns the crafting recipe group.
   *
   * @return the recipe group
   */
  public String getGroup() {
    return group;
  }

  /**
   * Sets the crafting recipe group.
   *
   * @param group the recipe group
   * @return this recipe instance
   * @throws IllegalArgumentException if {@code group} is {@code null}
   */
  public CraftingRecipe setGroup(final String group) {
    if (group == null) {
      throw new IllegalArgumentException("Group cannot be null");
    }
    this.group = group;
    return this;
  }

  /**
   * Returns the crafting recipe book category.
   *
   * @return the recipe category
   */
  public CraftingBookCategory getCategory() {
    return category;
  }

  /**
   * Sets the crafting recipe book category.
   *
   * @param category the recipe category
   * @return this recipe instance
   * @throws IllegalArgumentException if {@code category} is {@code null}
   */
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
