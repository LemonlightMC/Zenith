package com.lemonlightmc.zenith.recipes.builder;

import org.bukkit.inventory.recipe.CookingBookCategory;

import com.lemonlightmc.zenith.recipes.builder.IRecipeBuilder.*;
import com.lemonlightmc.zenith.recipes.types.*;

public class SmeltingRecipeBuilder extends RecipeBuilder implements ISmeltingRecipeBuilder {

  protected String group = "";
  protected CookingBookCategory category = CookingBookCategory.MISC;
  protected int cookingTime = 0;
  protected float experience = 0;

  @Override
  public SmeltingRecipeBuilder group(final String group) {
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
  public SmeltingRecipeBuilder category(final CookingBookCategory category) {
    if (type == null) {
      throw new IllegalArgumentException("Recipe type is not set");
    }
    this.category = category;
    return this;
  }

  @Override
  public CookingBookCategory category() {
    return category;
  }

  @Override
  public SmeltingRecipeBuilder cookingTime(final int cookingTime) {
    if (type == null) {
      throw new IllegalArgumentException("Recipe type is not set");
    }
    this.cookingTime = cookingTime;
    return this;
  }

  @Override
  public int cookingTime() {
    return cookingTime;
  }

  @Override
  public SmeltingRecipeBuilder experience(final float experience) {
    if (type == null) {
      throw new IllegalArgumentException("Recipe type is not set");
    }
    this.experience = experience;
    return this;
  }

  @Override
  public float experience() {
    return experience;
  }

  @Override
  public Recipe build() {
    final SmeltingRecipe recipe = (SmeltingRecipe) super.build(name);
    if (cookingTime == 0) {
      throw new IllegalArgumentException("Cooking time is not set");
    }
    recipe.setBurningTime(cookingTime);
    recipe.setExperience(experience);
    recipe.setCategory(category);
    recipe.setGroup(group);
    return recipe;
  }

  @Override
  public int hashCode() {
    int value = 31 + super.hashCode();
    value = 31 * value + cookingTime;
    value = 31 * value + ((group == null) ? 0 : group.hashCode());
    value = 31 * value + ((category == null) ? 0 : category.hashCode());
    return 31 * value + Float.floatToIntBits(experience);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SmeltingRecipeBuilder other = (SmeltingRecipeBuilder) obj;
    if (group == null && other.group != null || category == null && other.category != null) {
      return false;
    }
    return super.equals(obj) && cookingTime == other.cookingTime && group.equals(other.group)
        && category.equals(other.category)
        && Float.floatToIntBits(experience) == Float.floatToIntBits(other.experience);
  }

  @Override
  public String toString() {
    return "SmeltingRecipeBuilder [ingredientList=" + ingredientList + ", name=" + name + ", result=" + result
        + ", amount="
        + amount + ", type=" + type + ", group=" + group + ", category=" + category + ", cookingTime=" + cookingTime
        + ", experience=" + experience + "]";
  }
}
