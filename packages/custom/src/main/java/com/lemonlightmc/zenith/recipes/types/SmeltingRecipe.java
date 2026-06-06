package com.lemonlightmc.zenith.recipes.types;

import org.bukkit.inventory.recipe.CookingBookCategory;

import com.lemonlightmc.zenith.recipes.RecipeType;

public abstract class SmeltingRecipe extends Recipe {

  protected float experience;
  protected int burningTime;
  protected String group = "";
  protected CookingBookCategory category = CookingBookCategory.MISC;

  public SmeltingRecipe(final RecipeType type, final String recipeName, final float experience, final int burningTime) {
    super(recipeName, type);
    this.setExperience(experience);
    this.setBurningTime(burningTime);
  }

  public SmeltingRecipe(final RecipeType type, final String recipeName) {
    super(recipeName, type);
  }

  public String getGroup() {
    return group;
  }

  public SmeltingRecipe setGroup(final String group) {
    if (group == null) {
      throw new IllegalArgumentException("Group cannot be null");
    }
    this.group = group;
    return this;
  }

  public float getExperience() {
    return experience;
  }

  public SmeltingRecipe setExperience(final float experience) {
    if (experience < 0) {
      throw new IllegalArgumentException("Experience cannot be negative");
    }
    this.experience = experience;
    return this;
  }

  public int getBurningTime() {
    return burningTime;
  }

  public SmeltingRecipe setBurningTime(final int burningTime) {
    if (burningTime < 1) {
      throw new IllegalArgumentException("Burning time must be at least 1 tick");
    }
    this.burningTime = burningTime;
    return this;
  }

  public CookingBookCategory getCategory() {
    return category;
  }

  public SmeltingRecipe setCategory(final CookingBookCategory category) {
    if (category == null) {
      throw new IllegalArgumentException("Category cannot be null");
    }
    this.category = category;
    return this;
  }

  @Override
  public int hashCode() {
    int result = 31 + super.hashCode();
    result = 31 * result + Float.floatToIntBits(experience);
    result = 31 * result + ((group == null) ? 0 : group.hashCode());
    result = 31 * result + ((category == null) ? 0 : category.hashCode());
    return 31 * result + burningTime;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SmeltingRecipe other = (SmeltingRecipe) obj;
    if (group == null && other.group != null) {
      return false;
    }
    return super.equals(obj) && burningTime == other.burningTime && category == other.category
        && group.equals(other.group)
        && Float.floatToIntBits(experience) == Float.floatToIntBits(other.experience);
  }

  @Override
  public String toString() {
    return "SmeltingRecipe [name=" + name + ", type=" + type + "experience=" + experience + ", burningTime="
        + burningTime + ", group=" + group
        + ", category=" + category + ",output=" + output + ", ingredients="
        + ingredients + "]";
  }

}
