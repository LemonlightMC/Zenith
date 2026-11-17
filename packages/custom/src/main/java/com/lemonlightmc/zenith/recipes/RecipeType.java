package com.lemonlightmc.zenith.recipes;

import java.util.Arrays;
import java.util.List;

import org.bukkit.NamespacedKey;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.recipes.types.*;

/**
 * Enumerates supported custom recipe types and their capabilities.
 *
 * <p>Each type defines the backing recipe implementation, ingredient limits, and behavior flags used by recipe builders
 * and managers.
 */
public enum RecipeType {

  /** A shaped crafting recipe. */
  CRAFTING_SHAPED(ShapedCraftingRecipe.class, 9, true, false, false),
  /** A shapeless crafting recipe. */
  CRAFTING_SHAPELESS(ShapelessCraftingRecipe.class, 9, true, false, false),
  /** A furnace smelting recipe. */
  SMELTING(SmeltingRecipe.class, 1, false, true, true),
  /** A smoker recipe. */
  SMOKING(SmokingRecipe.class, 1, false, true, true),
  /** A blast furnace recipe. */
  BLASTING(BlastingRecipe.class, 1, false, true, false),
  /** A campfire cooking recipe. */
  CAMPFIRE_COOKING(CampfireRecipe.class, 1, false, false, true),
  /** A stonecutter recipe. */
  STONE_CUTTING(StonecuttingRecipe.class, 1, false, false, false),
  /** A smithing trim recipe. */
  SMITHING_TRIM(SmithingRecipe.class, 3, false, false, false, true),
  /** A smithing transform recipe. */
  SMITHING_TRANSFORM(SmithingRecipe.class, 3, false, false, false, true),
  /** A transmute crafting recipe. */
  TRANSMUTE(TransmuteRecipe.class, 2, false, false, false, false),
  /** An anvil recipe. */
  ANVIL(AnvilRecipe.class, 2, false, false, false, false),
  /** A brewing recipe. */
  BREWING(BrewingRecipe.class, 2, false, false, false, false);

  /**
   * Returns recipe types that perform smelting-like processing.
   *
   * @return the smelting recipe types
   */
  public static List<RecipeType> smeltingRecipes() {
    return List.of(CAMPFIRE_COOKING, BLASTING, SMOKING, SMELTING);
  }

  /**
   * Returns recipe types used for crafting.
   *
   * @return the crafting recipe types
   */
  public static List<RecipeType> craftingRecipes() {
    return List.of(CRAFTING_SHAPED, CRAFTING_SHAPELESS);
  }

  /**
   * Returns recipe types that use cooking behavior.
   *
   * @return the cooking recipe types
   */
  public static List<RecipeType> cookingRecipes() {
    return List.of(SMELTING, SMOKING, CAMPFIRE_COOKING);
  }

  /**
   * Returns recipe types that support recipe book categories.
   *
   * @return the category-capable recipe types
   */
  public static List<RecipeType> categoryRecipes() {
    return List.of(SMELTING, SMOKING, BLASTING, CAMPFIRE_COOKING, CRAFTING_SHAPED, CRAFTING_SHAPELESS);
  }

  /**
   * Returns all supported recipe types.
   *
   * @return all recipe types
   */
  public static List<RecipeType> allRecipes() {
    return List.of(values());
  }

  /**
   * Checks whether the given type is considered a smelting recipe.
   *
   * @param type the recipe type to check
   * @return {@code true} if the type is a smelting recipe; {@code false} otherwise
   */
  public static boolean isSmeltingRecipe(final RecipeType type) {
    return smeltingRecipes().contains(type);
  }

  /**
   * Checks whether the given type is considered a crafting recipe.
   *
   * @param type the recipe type to check
   * @return {@code true} if the type is a crafting recipe; {@code false} otherwise
   */
  public static boolean isCraftingRecipe(final RecipeType type) {
    return craftingRecipes().contains(type);
  }

  /**
   * Checks whether a string corresponds to a valid recipe type name.
   *
   * @param type the recipe type name to check
   * @return {@code true} if the name matches a recipe type; {@code false} otherwise
   */
  public static boolean isValidRecipeType(final String type) {
    return Arrays.stream(values()).anyMatch(t -> t.name().equalsIgnoreCase(type));
  }

  /**
   * Resolves a recipe type from its name.
   *
   * @param type the recipe type name
   * @return the matching recipe type
   * @throws IllegalArgumentException if the name does not match any recipe type
   */
  public static RecipeType fromString(final String type) {
    return allRecipes().stream()
        .filter(t -> t.name().equalsIgnoreCase(type))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid recipe type: " + type));
  }

  private final Class<? extends Recipe> cls;
  private final int maxIngredients;
  private final boolean isCrafting;
  private final boolean isSmelting;
  private final boolean isCooking;
  private final boolean isSmithing;

  RecipeType(final Class<? extends Recipe> cls, final int maxIngredients, final boolean isCrafting,
      final boolean isSmelting,
      final boolean isCooking) {
    this.cls = cls;
    this.maxIngredients = maxIngredients;
    this.isCrafting = isCrafting;
    this.isSmelting = isSmelting;
    this.isCooking = isCooking;
    this.isSmithing = false;
  }

  RecipeType(final Class<? extends Recipe> cls, final int maxIngredients, final boolean isCrafting,
      final boolean isSmelting,
      final boolean isCooking, final boolean isSmithing) {
    this.cls = cls;
    this.maxIngredients = maxIngredients;
    this.isCrafting = isCrafting;
    this.isSmelting = isSmelting;
    this.isCooking = isCooking;
    this.isSmithing = isSmithing;
  }

  /**
   * Creates a new recipe instance for this type.
   *
   * @param args the constructor arguments for the recipe implementation
   * @return the created recipe, or {@code null} if instantiation fails
   */
  public Recipe newRecipe(Object... args) {
    try {
      return (Recipe) cls.getDeclaredConstructor().newInstance(args);
    } catch (Exception e) {
      Logger.warn("Failed to create Recipe Instance");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the recipe implementation class for this type.
   *
   * @return the recipe class
   */
  public Class<? extends Recipe> getRecipeClass() {
    return cls;
  }

  /**
   * Casts a recipe to this type's recipe implementation.
   *
   * @param recipe the recipe to cast
   * @return the cast recipe
   */
  public Recipe cast(Recipe recipe) {
    return cls.cast(recipe);
  }

  /**
   * Returns the maximum number of ingredients supported by this recipe type.
   *
   * @return the maximum ingredient count
   */
  public int getMaxIngredients() {
    return maxIngredients;
  }

  /**
   * Creates a namespaced key for a recipe of this type.
   *
   * @param key the recipe-specific key suffix
   * @return the namespaced key for the recipe
   */
  public NamespacedKey getNamespacedKey(final String key) {
    return new NamespacedKey(ZenithProvider.instance(), name().toLowerCase() + "_" + key);
  }

  /**
   * Returns whether this recipe type supports recipe book categories.
   *
   * @return {@code true} if categories are supported; {@code false} otherwise
   */
  public boolean supportsCategory() {
    return isCrafting || isSmelting;
  }

  /**
   * Returns whether this recipe type supports shaped crafting patterns.
   *
   * @return {@code true} if patterns are supported; {@code false} otherwise
   */
  public boolean supportsPattern() {
    return this.equals(CRAFTING_SHAPED);
  }

  /**
   * Returns whether this recipe type represents a crafting recipe.
   *
   * @return {@code true} if this is a crafting recipe; {@code false} otherwise
   */
  public boolean isCraftingRecipe() {
    return isCrafting;
  }

  /**
   * Returns whether this recipe type represents a smelting recipe.
   *
   * @return {@code true} if this is a smelting recipe; {@code false} otherwise
   */
  public boolean isSmeltingRecipe() {
    return isSmelting;
  }

  /**
   * Returns whether this recipe type represents a cooking recipe.
   *
   * @return {@code true} if this is a cooking recipe; {@code false} otherwise
   */
  public boolean isCookingRecipe() {
    return isCooking;
  }

  /**
   * Returns whether this recipe type represents a smithing recipe.
   *
   * @return {@code true} if this is a smithing recipe; {@code false} otherwise
   */
  public boolean isSmithing() {
    return isSmithing;
  }
}
