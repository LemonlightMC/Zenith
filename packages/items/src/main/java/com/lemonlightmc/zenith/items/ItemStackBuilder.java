package com.lemonlightmc.zenith.items;

import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.lemonlightmc.zenith.interfaces.Builder;

public class ItemStackBuilder implements Builder<ItemStack> {
  protected final ItemStack item;
  protected final ItemMeta meta;

  public ItemStackBuilder(final ItemStack item) {
    if (item == null) {
      throw new IllegalArgumentException("item cannot be null");
    }
    this.item = item;
    this.meta = item.getItemMeta();
  }

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

  public ItemStackBuilder(final Material material) {
    this.item = new ItemStack(material, 1);
    this.meta = item.getItemMeta();
  }

  public static ItemStackBuilder of(final Material material) {
    return new ItemStackBuilder(material);
  }

  public static ItemStackBuilder of(final Material material, final int amount) {
    return new ItemStackBuilder(material, amount);
  }

  public static ItemStackBuilder of(final ItemStack item) {
    return new ItemStackBuilder(item);
  }

  public ItemStack build() {
    item.setItemMeta(meta);
    return item;
  }

  /* -------------------- amount -------------------- */
  public int amount() {
    return item.getAmount();
  }

  public ItemStackBuilder amount(final int amount) {
    item.setAmount(amount);
    return this;
  }

  /* -------------------- name -------------------- */
  public boolean hasName() {
    return meta != null && meta.hasDisplayName();
  }

  public String name() {
    return meta.getDisplayName();
  }

  public ItemStackBuilder name(final String name) {
    meta.setDisplayName(name);
    return this;
  }

  public ItemStackBuilder material(final Material material) {
    item.setType(material);
    return this;
  }

  public ItemStackBuilder unbreakable(final boolean value) {
    meta.setUnbreakable(value);
    return this;
  }

  public ItemStackBuilder edit(final BiConsumer<ItemStack, ItemMeta> editor) {
    if (editor != null) {
      editor.accept(item, meta);
    }
    return this;
  }

  /* -------------------- lore -------------------- */
  public List<String> lore() {
    return meta.getLore() == null ? List.of() : meta.getLore();
  }

  public boolean hasLore() {
    return meta.getLore() != null && !meta.getLore().isEmpty();
  }

  public ItemStackBuilder lore(final String... lore) {
    meta.setLore(List.of(lore));
    return this;
  }

  public ItemStackBuilder lore(final List<String> lore) {
    meta.setLore(lore);
    return this;
  }

  public ItemStackBuilder appendLore(final String... lines) {
    if (meta.hasLore() && meta.getLore() != null) {
      meta.getLore().addAll(List.of(lines));
    } else {
      meta.setLore(List.of(lines));
    }
    return this;
  }

  /* -------------------- components -------------------- */
  public ItemStackBuilder components(final ItemStackComponent component) {
    if (component != null) {
      component.apply(this);
    }
    return this;
  }

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
