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

public class Ingredients {

  public static final RecipeChoice.MaterialChoice EMPTRY = new RecipeChoice.MaterialChoice(Material.AIR);

  public static RecipeChoice toExactChoice(final Ingredient ingredient) {
    return new RecipeChoice.ExactChoice(ingredient.item());
  }

  public static RecipeChoice toExactChoice(final List<Ingredient> ingredients) {
    return new RecipeChoice.ExactChoice(ingredients.stream().map((ingredient) -> ingredient.item()).toList());
  }

  public static RecipeChoice toMaterialChoice(final Ingredient ingredient) {
    if (ingredient instanceof final MaterialIngredient materialIngredient) {
      return new RecipeChoice.MaterialChoice(materialIngredient.getMaterial());
    } else {
      throw new ClassCastException("Cant cast non Material Ingredient to Material Choice");
    }
  }

  public static RecipeChoice toMaterialChoice(final List<Ingredient> ingredients) {
    return new RecipeChoice.MaterialChoice(ingredients.stream().map((ingredient) -> {
      if (ingredient instanceof final MaterialIngredient materialIngredient) {
        return materialIngredient.getMaterial();
      } else {
        throw new ClassCastException("Cant cast non Material Ingredient to Material Choice");
      }
    }).toList());
  }

  public static MaterialIngredient from(final Material material) {
    return new MaterialIngredient(material, null);
  }

  public static MaterialIngredient from(final Material material, final Character sign) {
    return new MaterialIngredient(material, sign);
  }

  public static TagIngredient from(final Tag<Material> tag) {
    return new TagIngredient(tag, null);
  }

  public static TagIngredient from(final Tag<Material> tag, final Character sign) {
    return new TagIngredient(tag, sign);
  }

  public static ItemIngredient from(final ItemStack item) {
    return new ItemIngredient(item, null);
  }

  public static ItemIngredient from(final ItemStack item, final Character sign) {
    return new ItemIngredient(item, sign);
  }

  public static ItemIngredient from(final ItemStack item, final boolean strict) {
    if (strict) {
      return new StrictItemIngredient(item, null);
    } else {
      return new ItemIngredient(item, null);
    }
  }

  public static ItemIngredient from(final ItemStack item, final Character sign, final boolean strict) {
    if (strict) {
      return new StrictItemIngredient(item, sign);
    } else {
      return new ItemIngredient(item, sign);
    }
  }

  public static List<Ingredient> from(final RecipeChoice choice) {
    return fromChoice(choice, null);
  }

  public static List<Ingredient> from(final RecipeChoice choice, final Character sign) {
    return fromChoice(choice, sign);
  }

  public static MaterialIngredient fromMaterial(final Material material) {
    return new MaterialIngredient(material, null);
  }

  public static MaterialIngredient fromMaterial(final Material material, final Character sign) {
    return new MaterialIngredient(material, sign);
  }

  public static TagIngredient fromTag(final Tag<Material> tag) {
    return new TagIngredient(tag, null);
  }

  public static TagIngredient fromTag(final Tag<Material> tag, final Character sign) {
    return new TagIngredient(tag, sign);
  }

  public static ItemIngredient fromItem(final ItemStack item) {
    return new ItemIngredient(item, null);
  }

  public static ItemIngredient fromItem(final ItemStack item, final Character sign) {
    return new ItemIngredient(item, sign);
  }

  public static StrictItemIngredient fromStrictItem(final ItemStack item) {
    return new StrictItemIngredient(item, null);
  }

  public static StrictItemIngredient fromStrictItem(final ItemStack item, final Character sign) {
    return new StrictItemIngredient(item, sign);
  }

  public static List<Ingredient> fromChoice(final RecipeChoice choice) {
    return fromChoice(choice, null);
  }

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

  public static abstract class Ingredient implements Cloneable {

    protected final Character sign;

    public Ingredient(final Character sign) {
      this.sign = sign;
    }

    public Character sign() {
      return this.sign;
    }

    public abstract boolean isSimilar(ItemStack item);

    public abstract RecipeChoice choice();

    public abstract ItemStack item();

    @Override
    public abstract Ingredient clone();
  }

  public static class TagIngredient extends Ingredient {

    protected final Tag<Material> tag;

    public TagIngredient(final Tag<Material> tag, final Character sign) {
      super(sign);
      if (tag == null || tag.getValues() == null || tag.getValues().isEmpty()) {
        throw new IllegalArgumentException("Tag cannot be null or empty");
      }
      this.tag = tag;
    }

    public TagIngredient(final Tag<Material> tag) {
      this(tag, null);
    }

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

  public static class MaterialIngredient extends Ingredient {

    protected final Material material;

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

    public MaterialIngredient(final Material material) {
      this(material, null);
    }

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

  public static class ItemIngredient extends Ingredient {

    protected final ItemStack item;

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

  public static class StrictItemIngredient extends ItemIngredient {

    public StrictItemIngredient(final ItemStack item, final Character sign) {
      super(item, sign);
    }

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

  public static class OraxenIngredient extends Ingredient {

    private final ItemStack item;
    private final String id;

    public OraxenIngredient(final String id, final Character sign) {
      super(sign);
      final var builder = OraxenItems.getItemById(id);
      if (builder == null) {
        throw new IllegalArgumentException("Oraxen item with id " + id + " not found");
      }
      this.item = builder.build();
      this.id = id;
    }

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

  public static class ItemsAdderIngredient extends Ingredient {

    private final String data;

    public ItemsAdderIngredient(final String data, final Character sign) {
      super(sign);
      this.data = data;
    }

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
