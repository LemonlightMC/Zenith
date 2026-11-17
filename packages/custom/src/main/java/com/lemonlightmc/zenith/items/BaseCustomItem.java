package com.lemonlightmc.zenith.items;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.recipes.types.Recipe;

/**
 * Base implementation for custom items managed by Zenith.
 *
 * <p>Custom items are identified by a persistent data key, can optionally be associated with a recipe, and can react to
 * common player and inventory interactions through overridable hook methods.
 */
public abstract class BaseCustomItem {

  /** The internal identifier stored on item persistent data. */
  public final String itemID;
  /** The namespaced key used to identify this custom item type. */
  public final NamespacedKey key;
  private ItemStack baseItem = null;
  private Recipe recipe;

  /** Persistent data key used to store the custom item identifier. */
  public static NamespacedKey customKey = new NamespacedKey(ZenithProvider.instance().getName().toLowerCase(),
      "customitem_id");
  /** Persistent data key used to store a unique instance identifier for custom items. */
  public static NamespacedKey uuidKey = new NamespacedKey(ZenithProvider.instance().getName().toLowerCase(),
      "customitem_uuid");

  /**
   * Creates the item stack instance that should be given to a player.
   *
   * @param itemStack the base item stack to derive from
   * @param player the player receiving the item, or {@code null} when not player-specific
   * @return the generated item stack
   */
  protected abstract ItemStack generateItem(ItemStack itemStack, Player player);

  /**
   * Returns metadata describing this custom item.
   *
   * @return the custom item data
   */
  public abstract CustomItemData itemData();

  /**
   * Creates a custom item definition backed by the given material.
   *
   * @param itemID the unique item identifier suffix
   * @param material the base material for the item
   */
  public BaseCustomItem(final String itemID, final Material material) {
    this.itemID = "customitem_" + itemID;
    this.key = new NamespacedKey(ZenithProvider.instance().getName().toLowerCase(), itemID);
    setBaseItem(new ItemStack(material, 1));
  }

  /**
   * Creates a custom item definition backed by the given item stack.
   *
   * @param itemID the unique item identifier suffix
   * @param item the base item stack for the item
   */
  public BaseCustomItem(final String itemID, final ItemStack item) {
    this.itemID = "customitem_" + itemID;
    this.key = new NamespacedKey(ZenithProvider.instance().getName().toLowerCase(), itemID);
    setBaseItem(item);
  }

  /**
   * Sets the base item stack used by this custom item and stores its identifier in persistent data.
   *
   * @param item the base item stack to assign
   */
  public void setBaseItem(ItemStack item) {
    if (item == null) {
      return;
    }
    final ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return;
    }
    final PersistentDataContainer pdc = meta.getPersistentDataContainer();
    if (pdc == null) {
      return;
    }
    pdc.set(customKey, PersistentDataType.STRING, itemID);
    item.setItemMeta(meta);
    this.baseItem = item;
  }

  /**
   * Associates a recipe with this custom item.
   *
   * @param recipe the recipe to associate
   * @throws IllegalArgumentException if {@code recipe} is {@code null}
   */
  public void setRecipe(Recipe recipe) {
    if (recipe == null) {
      throw new IllegalArgumentException("Invalid Item recipe for " + key);
    }
    this.recipe = recipe;
  }

  /**
   * Returns the recipe associated with this custom item, if any.
   *
   * @return the associated recipe, or {@code null} if none is set
   */
  public Recipe getRecipe() {
    return recipe;
  }

  /**
   * Handles a left-click interaction with this item.
   *
   * @param event the interaction event
   * @param uuid the unique instance identifier of the item, if present
   */
  public void leftClick(PlayerInteractEvent event, UUID uuid) {
  }

  /**
   * Handles a right-click interaction with this item.
   *
   * @param event the interaction event
   * @param uuid the unique instance identifier of the item, if present
   */
  public void rightClick(PlayerInteractEvent event, UUID uuid) {
  }

  /**
   * Handles damage dealt by this item to an entity.
   *
   * @param event the damage event
   * @param uuid the unique instance identifier of the item, if present
   */
  public void itemDamageEntity(EntityDamageByEntityEvent event, UUID uuid) {
  }

  /**
   * Handles a right-click interaction on an entity with this item.
   *
   * @param event the interaction event
   * @param uuid the unique instance identifier of the item, if present
   */
  public void rightClickOnEntity(PlayerInteractEntityEvent event, UUID uuid) {
  }

  /**
   * Handles selection of this item in the player's held slot.
   *
   * @param event the item switch event
   */
  public void itemSelectEvent(CustomItemSwitchedEvent event) {
  }

  /**
   * Handles switching away from this item in the player's held slot.
   *
   * @param event the item switch event
   */
  public void itemSwitchEvent(CustomItemSwitchedEvent event) {
  }

  /**
   * Handles dropping this item.
   *
   * @param event the drop event
   * @param uuid the unique instance identifier of the item, if present
   */
  public void itemDroppedEvent(PlayerDropItemEvent event, UUID uuid) {
  }

  /**
   * Handles crafting of this item.
   *
   * @param event the craft event
   */
  public void craftEvent(CraftItemEvent event) {

  }

  /**
   * Creates an item stack instance for the specified player.
   *
   * <p>If the item data marks this item as uniquely identifiable, a new UUID is stored on the generated item.
   *
   * @param player the player receiving the item, or {@code null} when not player-specific
   * @return a generated item stack for this custom item
   */
  public ItemStack getItem(final Player player) {
    ItemStack item = generateItem(baseItem, player);
    if (itemData().isUniqueId()) {
      ItemMeta meta = item.getItemMeta();
      meta.getPersistentDataContainer().set(BaseCustomItem.uuidKey, PersistentDataType.STRING,
          UUID.randomUUID().toString());
      item.setItemMeta(meta);
    }
    return item;
  }

  @Override
  public int hashCode() {
    int result = 31 + ((key == null) ? 0 : key.hashCode());
    return 31 * result + ((baseItem == null) ? 0 : baseItem.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    BaseCustomItem other = (BaseCustomItem) obj;
    return key == null && other.key != null && key.equals(other.key) && baseItem.equals(other.baseItem);
  }

  @Override
  public String toString() {
    return "BaseCustomItem [key=" + key + ",item" + baseItem + "]";
  }

}
