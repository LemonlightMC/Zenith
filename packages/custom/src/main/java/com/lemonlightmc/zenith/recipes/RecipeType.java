package com.lemonlightmc.zenith.recipes;

import org.bukkit.NamespacedKey;

import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.messages.Logger;
import com.lemonlightmc.zenith.recipes.types.*;

import java.util.Arrays;
import java.util.List;

public enum RecipeType {

  CRAFTING_SHAPED(ShapedCraftingRecipe.class, 9, true, false, false),
  CRAFTING_SHAPELESS(ShapelessCraftingRecipe.class, 9, true, false, false),
  SMELTING(SmeltingRecipe.class, 1, false, true, true),
  SMOKING(SmokingRecipe.class, 1, false, true, true),
  BLASTING(BlastingRecipe.class, 1, false, true, false),
  CAMPFIRE_COOKING(CampfireRecipe.class, 1, false, false, true),
  STONE_CUTTING(StonecuttingRecipe.class, 1, false, false, false),
  SMITHING_TRIM(SmithingRecipe.class, 3, false, false, false, true),
  SMITHING_TRANSFORM(SmithingRecipe.class, 3, false, false, false, true),
  TRANSMUTE(TransmuteRecipe.class, 2, false, false, false, false),
  ANVIL(AnvilRecipe.class, 2, false, false, false, false),
  BREWING(BrewingRecipe.class, 2, false, false, false, false);

  public static List<RecipeType> smeltingRecipes() {
    return List.of(CAMPFIRE_COOKING, BLASTING, SMOKING, SMELTING);
  }

  public static List<RecipeType> craftingRecipes() {
    return List.of(CRAFTING_SHAPED, CRAFTING_SHAPELESS);
  }

  public static List<RecipeType> cookingRecipes() {
    return List.of(SMELTING, SMOKING, CAMPFIRE_COOKING);
  }

  public static List<RecipeType> categoryRecipes() {
    return List.of(SMELTING, SMOKING, BLASTING, CAMPFIRE_COOKING, CRAFTING_SHAPED, CRAFTING_SHAPELESS);
  }

  public static List<RecipeType> allRecipes() {
    return List.of(values());
  }

  public static boolean isSmeltingRecipe(final RecipeType type) {
    return smeltingRecipes().contains(type);
  }

  public static boolean isCraftingRecipe(final RecipeType type) {
    return craftingRecipes().contains(type);
  }

  public static boolean isValidRecipeType(final String type) {
    return Arrays.stream(values()).anyMatch(t -> t.name().equalsIgnoreCase(type));
  }

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

  public Recipe newRecipe(Object... args) {
    try {
      return (Recipe) cls.getDeclaredConstructor().newInstance(args);
    } catch (Exception e) {
      Logger.warn("Failed to create Recipe Instance");
      e.printStackTrace();
      return null;
    }
  }

  public Class<? extends Recipe> getRecipeClass() {
    return cls;
  }

  public Recipe cast(Recipe recipe) {
    return cls.cast(recipe);
  }

  public int getMaxIngredients() {
    return maxIngredients;
  }

  public NamespacedKey getNamespacedKey(final String key) {
    return new NamespacedKey(ZenithPlugin.getInstance(), name().toLowerCase() + "_" + key);
  }

  public boolean supportsCategory() {
    return isCrafting || isSmelting;
  }

  public boolean supportsPattern() {
    return this.equals(CRAFTING_SHAPED);
  }

  public boolean isCraftingRecipe() {
    return isCrafting;
  }

  public boolean isSmeltingRecipe() {
    return isSmelting;
  }

  public boolean isCookingRecipe() {
    return isCooking;
  }

  public boolean isSmithing() {
    return isSmithing;
  }
}
