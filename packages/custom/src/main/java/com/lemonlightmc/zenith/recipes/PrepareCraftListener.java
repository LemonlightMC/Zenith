package com.lemonlightmc.zenith.recipes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;

import com.lemonlightmc.zenith.recipes.Ingredients.Ingredient;
import com.lemonlightmc.zenith.recipes.types.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrepareCraftListener implements Listener {

  public PrepareCraftListener() {
  }

  private static boolean isSimilar(final ItemStack item, final Ingredient itemIngredient) {
    return itemIngredient.isSimilar(item);
  }

  @EventHandler
  public void onSmelt(final BlockCookEvent event) {
    if (event.isCancelled()) {
      return;
    }
    final ItemStack item = event.getSource();
    if (item == null || item.getType() == Material.AIR)
      return;
    final ItemStack result = event.getResult();
    final List<SmeltingRecipe> recipes = RecipesManager.getInstance().getRecipe(result, RecipeType.SMELTING);

    boolean found = false;
    for (final SmeltingRecipe itemRecipe : recipes) {
      if (isSimilar(item, itemRecipe.getIngredients().get(0))) {
        found = true;
        event.setResult(itemRecipe.getResult().item());
        break;
      }
    }
    if (found) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSmithingTransform(final PrepareSmithingEvent event) {
    if (event.getInventory().getRecipe() == null) {
      return;
    }
    final SmithingRecipe recipe = (SmithingRecipe) event.getInventory().getRecipe();

    final ItemStack item = event.getResult();
    if (item == null || item.getType() == Material.AIR)
      return;

    final ItemStack template = event.getInventory().getItem(0);
    final ItemStack base = event.getInventory().getItem(1);
    final ItemStack addition = event.getInventory().getItem(2);

    if (recipe instanceof SmithingTrimRecipe) {
      return;
    }
    final SmeltingRecipe itemRecipe = RecipesManager.getInstance().getRecipe(recipe.getKey(),
        RecipeType.SMITHING_TRANSFORM);
    if (itemRecipe == null) {
      return;
    }
    final Ingredient templateIngredient = itemRecipe.getIngredients().get(0);
    final Ingredient baseIngredient = itemRecipe.getIngredients().get(1);
    final Ingredient additionIngredient = itemRecipe.getIngredients().get(2);

    if (!isSimilar(template, templateIngredient)
        || !isSimilar(base, baseIngredient)
        || !isSimilar(addition, additionIngredient)) {
      event.setResult(new ItemStack(Material.AIR));
      return;
    } else {
      event.setResult(itemRecipe.getResult().item());
    }
  }

  @EventHandler
  public void onPrepareCraft(final PrepareItemCraftEvent event) {
    final org.bukkit.inventory.Recipe recipe = event.getRecipe();
    if (recipe == null) {
      return;
    }

    if (recipe instanceof final org.bukkit.inventory.ShapedRecipe shapedRecipe) {
      final ShapedCraftingRecipe itemRecipe = RecipesManager.getInstance().getRecipe(shapedRecipe.getKey(),
          RecipeType.CRAFTING_SHAPED);
      PrepareCraftListener.checkGoodShapedRecipe(itemRecipe, event);
    }

    if (recipe instanceof final ShapelessRecipe shapelessRecipe) {
      final ShapelessCraftingRecipe itemRecipe = RecipesManager.getInstance().getRecipe(shapelessRecipe.getKey(),
          RecipeType.CRAFTING_SHAPELESS);
      PrepareCraftListener.checkGoodShapelessRecipe(itemRecipe, event);
    }
  }

  private static void checkGoodShapedRecipe(final ShapedCraftingRecipe itemRecipe, final PrepareItemCraftEvent event) {
    ItemStack[] matrix = event.getInventory().getMatrix();
    matrix = Arrays.stream(matrix).filter(stack -> stack != null && stack.getType() != Material.AIR)
        .toArray(ItemStack[]::new);
    final String[] pattern = itemRecipe.getShapeAsString().split("");

    for (int i = 0; i < matrix.length; i++) {
      final AtomicBoolean isSimilar = new AtomicBoolean(true);
      final ItemStack stack = matrix[i];
      final char sign = pattern[i].charAt(0);
      itemRecipe.getIngredients().stream().filter(ingredient -> ingredient.sign() == sign).findFirst()
          .ifPresent(ingredient -> {
            isSimilar.set(ingredient.isSimilar(stack));
          });
      if (!isSimilar.get()) {
        event.getInventory().setResult(new ItemStack(Material.AIR));
        return;
      }
    }
  }

  private static void checkGoodShapelessRecipe(final ShapelessCraftingRecipe itemRecipe,
      final PrepareItemCraftEvent event) {
    final List<ItemStack> matrix = Arrays.stream(event.getInventory().getMatrix()).filter(s -> s != null)
        .filter(it -> it.getType() != Material.AIR).toList();
    final List<Ingredient> itemIngredients = itemRecipe.getIngredients();

    final AtomicBoolean isSimilar = new AtomicBoolean(true);
    for (final Ingredient ingredient : itemIngredients) {
      final boolean found = matrix.stream().anyMatch(stack -> {
        if (stack == null || stack.getType() == Material.AIR)
          return false;
        return ingredient.isSimilar(stack);
      });
      if (!found) {
        isSimilar.set(false);
        break;
      }
    }

    if (!isSimilar.get() || matrix.size() != itemIngredients.size()) {
      event.getInventory().setResult(new ItemStack(Material.AIR));
      return;
    }
  }
}
