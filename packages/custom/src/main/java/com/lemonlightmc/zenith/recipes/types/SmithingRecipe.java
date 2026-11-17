package com.lemonlightmc.zenith.recipes.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimPattern;

import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.RecipeType;

/**
 * Represents a custom smithing transform or trim recipe.
 */
public class SmithingRecipe extends Recipe {
  private Ingredient base;
  private Ingredient addition;
  private Ingredient template;
  private TrimPattern trimPattern;

  /**
   * Creates a smithing transform recipe with the given name.
   *
   * @param recipeName the internal recipe name
   */
  public SmithingRecipe(final String recipeName) {
    super(recipeName, RecipeType.SMITHING_TRANSFORM);
  }

  /**
   * Creates a smithing transform recipe.
   *
   * @param recipeName the internal recipe name
   * @param template the template ingredient
   * @param base the base ingredient
   * @param addition the addition ingredient
   * @param trimPattern the trim pattern
   */
  public SmithingRecipe(final String recipeName, final Ingredient template, final Ingredient base,
      final Ingredient addition,
      final TrimPattern trimPattern) {
    super(recipeName, RecipeType.SMITHING_TRANSFORM);
    setResult(new ItemStack(Material.AIR));
    this.base = base;
    this.template = template;
    this.addition = addition;
    this.trimPattern = trimPattern;
  }

  /**
   * Creates a smithing trim recipe.
   *
   * @param recipeName the internal recipe name
   * @param result the result item stack
   * @param template the template ingredient
   * @param base the base ingredient
   * @param addition the addition ingredient
   */
  public SmithingRecipe(final String recipeName, final ItemStack result, final Ingredient template,
      final Ingredient base,
      final Ingredient addition) {
    super(recipeName, RecipeType.SMITHING_TRIM);
    setResult(result);
    this.template = template;
    this.base = base;
    this.addition = addition;
  }

  @Override
  public org.bukkit.inventory.Recipe toBukkit() {
    if (trimPattern != null) {
      final org.bukkit.inventory.SmithingTransformRecipe recipe = new org.bukkit.inventory.SmithingTransformRecipe(
          getKey(),
          this.output.item(), template.choice(), base.choice(), addition.choice());
      return recipe;
    } else {
      final org.bukkit.inventory.SmithingTrimRecipe recipe = new org.bukkit.inventory.SmithingTrimRecipe(
          getKey(),
          template.choice(), base.choice(), addition.choice(), trimPattern);
      return recipe;
    }
  }

  /**
   * Returns a copy of the template ingredient.
   *
   * @return the template ingredient, or {@code null} if not set
   */
  public Ingredient getTemplate() {
    return (template != null) ? template.clone() : null;
  }

  /**
   * Returns a copy of the base ingredient.
   *
   * @return the base ingredient, or {@code null} if not set
   */
  public Ingredient getBase() {
    return (base != null) ? base.clone() : null;
  }

  /**
   * Returns a copy of the addition ingredient.
   *
   * @return the addition ingredient, or {@code null} if not set
   */
  public Ingredient getAddition() {
    return (addition != null) ? addition.clone() : null;
  }

  /**
   * Returns the trim pattern for this recipe.
   *
   * @return the trim pattern, or {@code null} if not set
   */
  public TrimPattern getTrimPattern() {
    return trimPattern;
  }

  /**
   * Attempts to infer a trim pattern from the given template ingredient.
   *
   * @param template the template ingredient
   * @return the inferred trim pattern
   */
  public static TrimPattern guessPattern(final Ingredient template) {
    return guessPattern(template.item());
  }

  /**
   * Attempts to infer a trim pattern from the given template item.
   *
   * @param template the template item
   * @return the inferred trim pattern
   */
  @Deprecated(since = "1.21.5")
  public static TrimPattern guessPattern(final ItemStack template) {
    if (template == null) {
      return TrimPattern.SENTRY;
    }

    return switch (template.getType()) {
      case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.SENTRY;
      case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.DUNE;
      case COAST_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.COAST;
      case WILD_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.WILD;
      case WARD_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.WARD;
      case EYE_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.EYE;
      case VEX_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.VEX;
      case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.TIDE;
      case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.SNOUT;
      case RIB_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.RIB;
      case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.SPIRE;
      case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.WAYFINDER;
      case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.SHAPER;
      case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.SILENCE;
      case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.RAISER;
      case HOST_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.HOST;
      case FLOW_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.FLOW;
      case BOLT_ARMOR_TRIM_SMITHING_TEMPLATE ->
        TrimPattern.BOLT;
      default ->
        TrimPattern.SENTRY;
    };
  }
}
