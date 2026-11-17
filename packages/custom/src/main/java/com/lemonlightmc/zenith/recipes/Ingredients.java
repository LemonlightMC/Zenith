package com.lemonlightmc.zenith.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;

/**
 * Utility methods and ingredient representations for custom recipe matching.
 */
public class Ingredients {

  /** An empty material recipe choice representing air. */
  public static final RecipeChoice.MaterialChoice EMPTRY = new RecipeChoice.MaterialChoice(Material.AIR);

  /**
   * Converts an ingredient into an exact recipe choice.
   *
   * @param ingredient the ingredient to convert
   * @return the exact recipe choice
   */
  public static RecipeChoice toExactChoice(final Ingredient ingredient) {
    return new RecipeChoice.ExactChoice(ingredient.item());
  }

  /**
   * Converts a list of ingredients into an exact recipe choice.
   *
   * @param ingredients the ingredients to convert
   * @return the exact recipe choice
   */
  public static RecipeChoice toExactChoice(final List<Ingredient> ingredients) {
    return new RecipeChoice.ExactChoice(ingredients.stream().map((ingredient) -> ingredient.item()).toList());
  }

  /**
   * Converts a material ingredient into a material recipe choice.
   *
   * @param ingredient the ingredient to convert
   * @return the material recipe choice
   * @throws ClassCastException if the ingredient is not material-based
   */
  public static RecipeChoice toMaterialChoice(final Ingredient ingredient) {
    if (ingredient instanceof final MaterialIngredient materialIngredient) {
      return new RecipeChoice.MaterialChoice(materialIngredient.getMaterial());
    } else {
      throw new ClassCastException("Cant cast non Material Ingredient to Material Choice");
    }
  }

  /**
   * Converts a list of material ingredients into a material recipe choice.
   *
   * @param ingredients the ingredients to convert
   * @return the material recipe choice
   * @throws ClassCastException if any ingredient is not material-based
   */
  public static RecipeChoice toMaterialChoice(final List<Ingredient> ingredients) {
    return new RecipeChoice.MaterialChoice(ingredients.stream().map((ingredient) -> {
      if (ingredient instanceof final MaterialIngredient materialIngredient) {
        return materialIngredient.getMaterial();
      } else {
        throw new ClassCastException("Cant cast non Material Ingredient to Material Choice");
      }
    }).toList());
  }

  /**
   * Creates a material ingredient.
   *
   * @param material the ingredient material
   * @return the material ingredient
   */
  public static MaterialIngredient from(final Material material) {
    return new MaterialIngredient(material, null);
  }

  /**
   * Creates a material ingredient with a shaped recipe sign.
   *
   * @param material the ingredient material
   * @param sign the shaped recipe sign
   * @return the material ingredient
   */
  public static MaterialIngredient from(final Material material, final Character sign) {
    return new MaterialIngredient(material, sign);
  }

  /**
   * Creates a tag ingredient.
   *
   * @param tag the material tag
   * @return the tag ingredient
   */
  public static TagIngredient from(final Tag<Material> tag) {
    return new TagIngredient(tag, null);
  }

  /**
   * Creates a tag ingredient with a shaped recipe sign.
   *
   * @param tag the material tag
   * @param sign the shaped recipe sign
   * @return the tag ingredient
   */
  public static TagIngredient from(final Tag<Material> tag, final Character sign) {
    return new TagIngredient(tag, sign);
  }

  /**
   * Creates an item ingredient.
   *
   * @param item the ingredient item stack
   * @return the item ingredient
   */
  public static ItemIngredient from(final ItemStack item) {
    return new ItemIngredient(item, null);
  }

  /**
   * Creates an item ingredient with a shaped recipe sign.
   *
   * @param item the ingredient item stack
   * @param sign the shaped recipe sign
   * @return the item ingredient
   */
  public static ItemIngredient from(final ItemStack item, final Character sign) {
    return new ItemIngredient(item, sign);
  }

  /**
   * Creates an item ingredient.
   *
   * @param item the ingredient item stack
   * @param strict whether the ingredient should be matched strictly
   * @return the item ingredient
   */
  public static ItemIngredient from(final ItemStack item, final boolean strict) {
    if (strict) {
      return new StrictItemIngredient(item, null);
    } else {
      return new ItemIngredient(item, null);
    }
  }

  /**
   * Creates an item ingredient with a shaped recipe sign.
   *
   * @param item the ingredient item stack
   * @param sign the shaped recipe sign
   * @param strict whether the ingredient should be matched strictly
   * @return the item ingredient
   */
  public static ItemIngredient from(final ItemStack item, final Character sign, final boolean strict) {
    if (strict) {
      return new StrictItemIngredient(item, sign);
    } else {
      return new ItemIngredient(item, sign);
    }
  }

  /**
   * Creates ingredients from a recipe choice.
   *
   * @param choice the recipe choice
   * @return the converted ingredients
   */
  public static List<Ingredient> from(final RecipeChoice choice) {
    return fromChoice(choice, null);
  }

  /**
   * Creates ingredients from a recipe choice with a shaped recipe sign.
   *
   * @param choice the recipe choice
   * @param sign the shaped recipe sign
   * @return the converted ingredients
   */
  public static List<Ingredient> from(final RecipeChoice choice, final Character sign) {
    return fromChoice(choice, sign);
  }

  /**
   * Creates a material ingredient.
   *
   * @param material the ingredient material
   * @return the material ingredient
   */
  public static MaterialIngredient fromMaterial(final Material material) {
    return new MaterialIngredient(material, null);
  }

  /**
   * Creates a material ingredient with a shaped recipe sign.
   *
   * @param material the ingredient material
   * @param sign the shaped recipe sign
   * @return the material ingredient
   */
  public static MaterialIngredient fromMaterial(final Material material, final Character sign) {
    return new MaterialIngredient(material, sign);
  }

  /**
   * Creates a tag ingredient.
   *
   * @param tag the material tag
   * @return the tag ingredient
   */
  public static TagIngredient fromTag(final Tag<Material> tag) {
    return new TagIngredient(tag, null);
  }

  /**
   * Creates a tag ingredient with a shaped recipe sign.
   *
   * @param tag the material tag
   * @param sign the shaped recipe sign
   * @return the tag ingredient
   */
  public static TagIngredient fromTag(final Tag<Material> tag, final Character sign) {
    return new TagIngredient(tag, sign);
  }

  /**
   * Creates an item ingredient.
   *
   * @param item the ingredient item stack
   * @return the item ingredient
   */
  public static ItemIngredient fromItem(final ItemStack item) {
    return new ItemIngredient(item, null);
  }

  /**
   * Creates an item ingredient with a shaped recipe sign.
   *
   * @param item the ingredient item stack
   * @param sign the shaped recipe sign
   * @return the item ingredient
   */
  public static ItemIngredient fromItem(final ItemStack item, final Character sign) {
    return new ItemIngredient(item, sign);
  }

  /**
   * Creates a strict item ingredient.
   *
   * @param item the ingredient item stack
   * @return the strict item ingredient
   */
  public static StrictItemIngredient fromStrictItem(final ItemStack item) {
    return new StrictItemIngredient(item, null);
  }

  /**
   * Creates a strict item ingredient with a shaped recipe sign.
   *
   * @param item the ingredient item stack
   * @param sign the shaped recipe sign
   * @return the strict item ingredient
   */
  public static StrictItemIngredient fromStrictItem(final ItemStack item, final Character sign) {
    return new StrictItemIngredient(item, sign);
  }

  /**
   * Creates ingredients from a recipe choice.
   *
   * @param choice the recipe choice
   * @return the converted ingredients
   */
  public static List<Ingredient> fromChoice(final RecipeChoice choice) {
    return fromChoice(choice, null);
  }

  /**
   * Creates ingredients from a recipe choice with a shaped recipe sign.
   *
   * @param choice the recipe choice
   * @param sign the shaped recipe sign
   * @return the converted ingredients
   * @throws IllegalArgumentException if the recipe choice is {@code null} or unsupported
   */
  public static List<Ingredient> fromChoice(final RecipeChoice choice, final Character sign) {
    if (choice == null) {
      throw new IllegalArgumentException("RecipeChoice cannot be null");
    }
    if (choice instanceof final RecipeChoice.MaterialChoice materialChoice) {
      final List<Ingredient> ingredients = new java.util.ArrayList<>();
      for (final Material material : materialChoice.getChoices()) {
        ingredients.add(new MaterialIngredient(material, sign));
      }
      return ingredients;
    } else if (choice instanceof final RecipeChoice.ExactChoice exactChoice) {
      final List<Ingredient> ingredients = new java.util.ArrayList<>();
      for (final ItemStack item : exactChoice.getChoices()) {
        ingredients.add(new StrictItemIngredient(item, sign));
      }
      return ingredients;
    } else {
      throw new IllegalArgumentException("Unsupported RecipeChoice type: " + choice.getClass().getName());
    }
  }

  /**
   * Base representation of a recipe ingredient.
   */
  public static abstract class Ingredient implements Cloneable {

    /** The shaped recipe sign associated with this ingredient, if any. */
    protected final Character sign;

    /**
     * Creates an ingredient with an optional shaped recipe sign.
     *
     * @param sign the shaped recipe sign
     */
    public Ingredient(final Character sign) {
      this.sign = sign;
    }

    /**
     * Returns the shaped recipe sign for this ingredient.
     *
     * @return the shaped recipe sign, or {@code null} if none
     */
    public Character sign() {
      return this.sign;
    }

    /**
     * Checks whether the given item matches this ingredient.
     *
     * @param item the item to compare
     * @return {@code true} if the item matches; {@code false} otherwise
     */
    public abstract boolean isSimilar(ItemStack item);

    /**
     * Returns the Bukkit recipe choice representation of this ingredient.
     *
     * @return the recipe choice
     */
    public abstract RecipeChoice choice();

    /**
     * Returns a representative item stack for this ingredient.
     *
     * @return the representative item stack
     */
    public abstract ItemStack item();

    @Override
    public abstract Ingredient clone();
  }

  /**
   * Ingredient implementation backed by a material tag.
   */
  public static class TagIngredient extends Ingredient {

    /** The material tag defining accepted materials. */
    protected final Tag<Material> tag;

    /**
     * Creates a tag ingredient with an optional shaped recipe sign.
     *
     * @param tag the material tag
     * @param sign the shaped recipe sign
     * @throws IllegalArgumentException if the tag is {@code null} or empty
     */
    public TagIngredient(final Tag<Material> tag, final Character sign) {
      super(sign);
      if (tag == null || tag.getValues() == null || tag.getValues().isEmpty()) {
        throw new IllegalArgumentException("Tag cannot be null or empty");
      }
      this.tag = tag;
    }

    /**
     * Creates a tag ingredient.
     *
     * @param tag the material tag
     */
    public TagIngredient(final Tag<Material> tag) {
      this(tag, null);
    }

    /**
     * Returns the backing material tag.
     *
     * @return the material tag
     */
    public Tag<Material> getTag() {
      return tag;
    }

    @Override
    public boolean isSimilar(final ItemStack item) {
      return tag.isTagged(item.getType());
    }

    @Override
    public RecipeChoice choice() {
      return new RecipeChoice.MaterialChoice(tag);
    }

    @Override
    public ItemStack item() {
      final List<Material> materials = new ArrayList<>(tag.getValues());
      if (materials.isEmpty()) {
        throw new IllegalStateException("Tag " + tag.getKey() + " has no materials");
      }
      if (materials.size() > 1) {
        throw new IllegalStateException("Tag " + tag.getKey() + " has too many materials");
      }
      return new ItemStack(materials.get(0));
    }

    @Override
    public TagIngredient clone() {
      return new TagIngredient(tag, sign);
    }

    @Override
    public String toString() {
      return tag.getKey().toString();
    }
  }

  /**
   * Ingredient implementation backed by a single material.
   */
  public static class MaterialIngredient extends Ingredient {

    /** The accepted material. */
    protected final Material material;

    /**
     * Creates a material ingredient with an optional shaped recipe sign.
     *
     * @param material the ingredient material
     * @param sign the shaped recipe sign
     * @throws IllegalArgumentException if the material is {@code null} or air
     */
    public MaterialIngredient(final Material material, final Character sign) {
      super(sign);
      if (material == null) {
        throw new IllegalArgumentException("Material cannot be null");
      }
      if (material.isAir()) {
        throw new IllegalArgumentException("Material cannot be AIR");
      }
      this.material = material;
    }

    /**
     * Creates a material ingredient.
     *
     * @param material the ingredient material
     */
    public MaterialIngredient(final Material material) {
      this(material, null);
    }

    /**
     * Returns the accepted material.
     *
     * @return the material
     */
    public Material getMaterial() {
      return material;
    }

    @Override
    public boolean isSimilar(final ItemStack item) {
      return item.getType() == this.material;
    }

    @Override
    public RecipeChoice choice() {
      return new RecipeChoice.MaterialChoice(this.material);
    }

    @Override
    public ItemStack item() {
      return new ItemStack(this.material);
    }

    @Override
    public MaterialIngredient clone() {
      return new MaterialIngredient(material, sign);
    }

    @Override
    public String toString() {
      return this.material.toString();
    }
  }

  /**
   * Ingredient implementation that matches item stacks using partial metadata comparison.
   */
  public static class ItemIngredient extends Ingredient {

    /** The representative item stack for this ingredient. */
    protected final ItemStack item;

    /**
     * Creates an item ingredient with an optional shaped recipe sign.
     *
     * @param item the ingredient item stack
     * @param sign the shaped recipe sign
     * @throws IllegalArgumentException if the item is {@code null} or air
     */
    public ItemIngredient(final ItemStack item, final Character sign) {
      super(sign);
      if (item == null) {
        throw new IllegalArgumentException("ItemStack cannot be null");
      }
      if (item.getType().isAir()) {
        throw new IllegalArgumentException("ItemStack cannot be AIR");
      }
      this.item = item;
    }

    /**
     * Creates an item ingredient.
     *
     * @param item the ingredient item stack
     */
    public ItemIngredient(final ItemStack item) {
      this(item, null);
    }

    @Override
    public boolean isSimilar(final ItemStack item) {

      return item.getType() == this.item.getType()
          && item.getAmount() >= this.item.getAmount()
          && item.hasItemMeta() == this.item.hasItemMeta()
          && (!item.hasItemMeta() || similarMeta(item.getItemMeta(), this.item.getItemMeta()));
    }

    private static boolean similarMeta(final ItemMeta sourceMeta, final ItemMeta ingredientMeta) {
      for (final NamespacedKey key : sourceMeta.getPersistentDataContainer().getKeys()) {
        if (!ingredientMeta.getPersistentDataContainer().has(key)) {
          System.out.println("Key " + key + " not found in ingredient meta");
          return false;
        }
      }

      final boolean lore = sourceMeta.hasLore() == ingredientMeta.hasLore() && (!sourceMeta.hasLore()
          || Objects.equals(sourceMeta.getLore(), ingredientMeta.getLore()));

      final boolean customData = sourceMeta.hasCustomModelDataComponent() == ingredientMeta
          .hasCustomModelDataComponent()
          && (!sourceMeta.hasCustomModelDataComponent()
              || sourceMeta.getCustomModelDataComponent() == ingredientMeta.getCustomModelDataComponent());

      return lore && customData;
    }

    @Override
    public ItemStack item() {
      return this.item;
    }

    @Override
    public RecipeChoice choice() {
      return new RecipeChoice.MaterialChoice(this.item.getType());
    }

    @Override
    public ItemIngredient clone() {
      return new ItemIngredient(item, sign);
    }

    @Override
    public String toString() {
      return "ItemIngredient [item=" + item + ']';
    }
  }

  /**
   * Ingredient implementation that requires exact item stack similarity.
   */
  public static class StrictItemIngredient extends ItemIngredient {

    /**
     * Creates a strict item ingredient with an optional shaped recipe sign.
     *
     * @param item the ingredient item stack
     * @param sign the shaped recipe sign
     */
    public StrictItemIngredient(final ItemStack item, final Character sign) {
      super(item, sign);
    }

    /**
     * Creates a strict item ingredient.
     *
     * @param item the ingredient item stack
     */
    public StrictItemIngredient(final ItemStack item) {
      super(item, null);
    }

    @Override
    public boolean isSimilar(final ItemStack item) {
      return item.isSimilar(this.item);
    }

    @Override
    public RecipeChoice choice() {
      return new RecipeChoice.ExactChoice(this.item);
    }

    @Override
    public ItemStack item() {
      return this.item;
    }

    @Override
    public StrictItemIngredient clone() {
      return new StrictItemIngredient(item, sign);
    }

    @Override
    public String toString() {
      return "StrictItemIngredient [item=" + item + ']';
    }
  }

  /**
   * Ingredient implementation backed by an Oraxen custom item identifier.
   */
  public static class OraxenIngredient extends Ingredient {

    private final ItemStack item;
    private final String id;

    /**
     * Creates an Oraxen ingredient with an optional shaped recipe sign.
     *
     * @param id the Oraxen item identifier
     * @param sign the shaped recipe sign
     * @throws IllegalArgumentException if the Oraxen item cannot be found
     */
    public OraxenIngredient(final String id, final Character sign) {
      super(sign);
      final var builder = OraxenItems.getItemById(id);
      if (builder == null) {
        throw new IllegalArgumentException("Oraxen item with id " + id + " not found");
      }
      this.item = builder.build();
      this.id = id;
    }

    /**
     * Creates an Oraxen ingredient.
     *
     * @param id the Oraxen item identifier
     */
    public OraxenIngredient(final String id) {
      this(id, null);
    }

    @Override
    public boolean isSimilar(final ItemStack item) {
      if (!item.hasItemMeta() || item.getItemMeta().getPersistentDataContainer().isEmpty()) {
        return false;
      }

      if (item.getType() != item.getType()) {
        return false;
      }

      final PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
      if (container.has(OraxenItems.ITEM_ID, PersistentDataType.STRING)) {
        return container.getOrDefault(OraxenItems.ITEM_ID, PersistentDataType.STRING, "ERROR")
            .equals(id);
      }
      return false;
    }

    /**
     * Returns the Oraxen item identifier.
     *
     * @return the Oraxen item identifier
     */
    public String getId() {
      return id;
    }

    @Override
    public RecipeChoice choice() {
      return new RecipeChoice.ExactChoice(item);
    }

    @Override
    public String toString() {
      return this.id;
    }

    @Override
    public ItemStack item() {
      return item;
    }

    @Override
    public Ingredient clone() {
      return new OraxenIngredient(id, sign);
    }
  }

  /**
   * Ingredient implementation backed by an ItemsAdder custom item identifier.
   */
  public static class ItemsAdderIngredient extends Ingredient {

    private final String data;

    /**
     * Creates an ItemsAdder ingredient with an optional shaped recipe sign.
     *
     * @param data the ItemsAdder item identifier
     * @param sign the shaped recipe sign
     */
    public ItemsAdderIngredient(final String data, final Character sign) {
      super(sign);
      this.data = data;
    }

    /**
     * Creates an ItemsAdder ingredient.
     *
     * @param data the ItemsAdder item identifier
     */
    public ItemsAdderIngredient(final String data) {
      this(data, null);
    }

    @Override
    public boolean isSimilar(final ItemStack ingredient) {
      final CustomStack item = CustomStack.byItemStack(ingredient);
      if (item == null)
        return false;
      return item.getNamespacedID().equals(getCustomStack().getNamespacedID());
    }

    @Override
    public RecipeChoice choice() {
      return new RecipeChoice.ExactChoice(getCustomStack().getItemStack());
    }

    private CustomStack getCustomStack() {
      final CustomStack customStack = CustomStack.getInstance(data);
      if (customStack == null) {
        throw new IllegalArgumentException("The item " + data + " is not registered in ItemsAdder.");
      }
      return customStack;
    }

    @Override
    public String toString() {
      return this.getCustomStack().getNamespacedID();
    }

    @Override
    public ItemStack item() {
      return getCustomStack().getItemStack();
    }

    @Override
    public Ingredient clone() {
      return new ItemsAdderIngredient(data);
    }
  }
}
