package com.lemonlightmc.zenith.items;

import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.lemonlightmc.zenith.additive.interfaces.Builder;

/**
 * Fluent builder for creating and modifying {@link ItemStack} instances.
 */
public class ItemStackBuilder implements Builder<ItemStack> {
  /** The item stack being built. */
  protected final ItemStack item;
  /** The item meta associated with the item being built. */
  protected final ItemMeta meta;

  /**
   * Creates a builder for an existing item stack.
   *
   * @param item the item stack to build from
   * @throws IllegalArgumentException if the item is {@code null}
   */
  public ItemStackBuilder(final ItemStack item) {
    if (item == null) {
      throw new IllegalArgumentException("item cannot be null");
    }
    this.item = item;
    this.meta = item.getItemMeta();
  }

  /**
   * Creates a builder for a new item stack with the given material and amount.
   *
   * @param material the material of the new item stack
   * @param amount the stack amount
   * @throws IllegalArgumentException if the material is {@code null} or the amount is out of range
   */
  public ItemStackBuilder(final Material material, final int amount) {
    if (material == null) {
      throw new IllegalArgumentException("material cannot be null");
    }
    if (amount < 1 || amount > material.getMaxStackSize()) {
      throw new IllegalArgumentException("amount must be between 1 and " + material.getMaxStackSize());
    }
    this.item = new ItemStack(material, amount);
    this.meta = item.getItemMeta();
  }

  /**
   * Creates a builder for a new item stack with the given material.
   *
   * @param material the material of the new item stack
   */
  public ItemStackBuilder(final Material material) {
    this.item = new ItemStack(material, 1);
    this.meta = item.getItemMeta();
  }

  /**
   * Creates a builder for the given material.
   *
   * @param material the material of the new item stack
   * @return a new item stack builder
   */
  public static ItemStackBuilder of(final Material material) {
    return new ItemStackBuilder(material);
  }

  /**
   * Creates a builder for the given material and amount.
   *
   * @param material the material of the new item stack
   * @param amount the stack amount
   * @return a new item stack builder
   */
  public static ItemStackBuilder of(final Material material, final int amount) {
    return new ItemStackBuilder(material, amount);
  }

  /**
   * Creates a builder for an existing item stack.
   *
   * @param item the item stack to build from
   * @return a new item stack builder
   */
  public static ItemStackBuilder of(final ItemStack item) {
    return new ItemStackBuilder(item);
  }

  /**
   * Builds and returns the configured item stack.
   *
   * @return the built item stack
   */
  public ItemStack build() {
    item.setItemMeta(meta);
    return item;
  }

  /* -------------------- amount -------------------- */

  /**
   * Gets the current stack amount.
   *
   * @return the current stack amount
   */
  public int amount() {
    return item.getAmount();
  }

  /**
   * Sets the stack amount.
   *
   * @param amount the new stack amount
   * @return this builder
   */
  public ItemStackBuilder amount(final int amount) {
    item.setAmount(amount);
    return this;
  }

  /* -------------------- name -------------------- */

  /**
   * Checks whether the item has a custom display name.
   *
   * @return {@code true} if the item has a display name, otherwise {@code false}
   */
  public boolean hasName() {
    return meta != null && meta.hasDisplayName();
  }

  /**
   * Gets the item's display name.
   *
   * @return the item's display name
   */
  public String name() {
    return meta.getDisplayName();
  }

  /**
   * Sets the item's display name.
   *
   * @param name the new display name
   * @return this builder
   */
  public ItemStackBuilder name(final String name) {
    meta.setDisplayName(name);
    return this;
  }

  /**
   * Sets the item's material.
   *
   * @param material the new material
   * @return this builder
   */
  public ItemStackBuilder material(final Material material) {
    item.setType(material);
    return this;
  }

  /**
   * Sets whether the item is unbreakable.
   *
   * @param value {@code true} to make the item unbreakable
   * @return this builder
   */
  public ItemStackBuilder unbreakable(final boolean value) {
    meta.setUnbreakable(value);
    return this;
  }

  /**
   * Applies a custom edit operation to the item and its metadata.
   *
   * @param editor the editor to apply
   * @return this builder
   */
  public ItemStackBuilder edit(final BiConsumer<ItemStack, ItemMeta> editor) {
    if (editor != null) {
      editor.accept(item, meta);
    }
    return this;
  }

  /* -------------------- lore -------------------- */

  /**
   * Gets the item's lore lines.
   *
   * @return the lore lines, or an empty list if none are set
   */
  public List<String> lore() {
    return meta.getLore() == null ? List.of() : meta.getLore();
  }

  /**
   * Checks whether the item has lore.
   *
   * @return {@code true} if the item has lore, otherwise {@code false}
   */
  public boolean hasLore() {
    return meta.getLore() != null && !meta.getLore().isEmpty();
  }

  /**
   * Replaces the item's lore with the given lines.
   *
   * @param lore the lore lines to set
   * @return this builder
   */
  public ItemStackBuilder lore(final String... lore) {
    meta.setLore(List.of(lore));
    return this;
  }

  /**
   * Replaces the item's lore with the given lines.
   *
   * @param lore the lore lines to set
   * @return this builder
   */
  public ItemStackBuilder lore(final List<String> lore) {
    meta.setLore(lore);
    return this;
  }

  /**
   * Appends lore lines to the item.
   *
   * @param lines the lore lines to append
   * @return this builder
   */
  public ItemStackBuilder appendLore(final String... lines) {
    if (meta.hasLore() && meta.getLore() != null) {
      meta.getLore().addAll(List.of(lines));
    } else {
      meta.setLore(List.of(lines));
    }
    return this;
  }

  /* -------------------- components -------------------- */

  /**
   * Applies a single item stack component to this builder.
   *
   * @param component the component to apply
   * @return this builder
   */
  public ItemStackBuilder components(final ItemStackComponent component) {
    if (component != null) {
      component.apply(this);
    }
    return this;
  }

  /**
   * Applies multiple item stack components to this builder.
   *
   * @param components the components to apply
   * @return this builder
   */
  public ItemStackBuilder components(final ItemStackComponent... components) {
    if (components == null) {
      return this;
    }
    for (final ItemStackComponent component : components) {
      if (component != null) {
        component.apply(this);
      }
    }
    return this;
  }

  /**
   * Applies multiple item stack components to this builder.
   *
   * @param components the components to apply
   * @return this builder
   */
  public ItemStackBuilder components(final Iterable<ItemStackComponent> components) {
    if (components == null) {
      return this;
    }
    for (final ItemStackComponent component : components) {
      if (component != null) {
        component.apply(this);
      }
    }
    return this;
  }
}
