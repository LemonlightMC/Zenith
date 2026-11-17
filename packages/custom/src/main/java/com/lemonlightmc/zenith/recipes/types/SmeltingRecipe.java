package com.lemonlightmc.zenith.recipes.types;

import org.bukkit.inventory.recipe.CookingBookCategory;

import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Base implementation for smelting and cooking recipes.
 */
public abstract class SmeltingRecipe extends Recipe {

  /** The experience reward for this recipe. */
  protected float experience;
  /** The cooking time in ticks. */
  protected int burningTime;
  /** The recipe group. */
  protected String group = "";
  /** The cooking recipe book category. */
  protected CookingBookCategory category = CookingBookCategory.MISC;

  /**
   * Creates a smelting recipe with the given properties.
   *
   * @param type the recipe type
   * @param recipeName the internal recipe name
   * @param experience the experience reward
   * @param burningTime the cooking time in ticks
   */
  public SmeltingRecipe(final RecipeType type, final String recipeName, final float experience, final int burningTime) {
    super(recipeName, type);
    this.setExperience(experience);
    this.setBurningTime(burningTime);
  }

  /**
   * Creates a smelting recipe with the given type and name.
   *
   * @param type the recipe type
   * @param recipeName the internal recipe name
   */
  public SmeltingRecipe(final RecipeType type, final String recipeName) {
    super(recipeName, type);
  }

  /**
   * Returns the recipe group.
   *
   * @return the recipe group
   */
  public String getGroup() {
    return group;
  }

  /**
   * Sets the recipe group.
   *
   * @param group the recipe group
   * @return this recipe instance
   * @throws IllegalArgumentException if {@code group} is {@code null}
   */
  public SmeltingRecipe setGroup(final String group) {
    if (group == null) {
      throw new IllegalArgumentException("Group cannot be null");
    }
    this.group = group;
    return this;
  }

  /**
   * Returns the experience reward.
   *
   * @return the experience reward
   */
  public float getExperience() {
    return experience;
  }

  /**
   * Sets the experience reward.
   *
   * @param experience the experience reward
   * @return this recipe instance
   * @throws IllegalArgumentException if {@code experience} is negative
   */
  public SmeltingRecipe setExperience(final float experience) {
    if (experience < 0) {
      throw new IllegalArgumentException("Experience cannot be negative");
    }
    this.experience = experience;
    return this;
  }

  /**
   * Returns the cooking time in ticks.
   *
   * @return the cooking time
   */
  public int getBurningTime() {
    return burningTime;
  }

  /**
   * Sets the cooking time in ticks.
   *
   * @param burningTime the cooking time in ticks
   * @return this recipe instance
   * @throws IllegalArgumentException if {@code burningTime} is less than 1
   */
  public SmeltingRecipe setBurningTime(final int burningTime) {
    if (burningTime < 1) {
      throw new IllegalArgumentException("Burning time must be at least 1 tick");
    }
    this.burningTime = burningTime;
    return this;
  }

  /**
   * Returns the cooking recipe book category.
   *
   * @return the recipe category
   */
  public CookingBookCategory getCategory() {
    return category;
  }

  /**
   * Sets the cooking recipe book category.
   *
   * @param category the recipe category
   * @return this recipe instance
   * @throws IllegalArgumentException if {@code category} is {@code null}
   */
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
