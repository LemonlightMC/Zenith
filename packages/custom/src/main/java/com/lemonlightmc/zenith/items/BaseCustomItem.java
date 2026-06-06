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

import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.recipes.types.Recipe;

public abstract class BaseCustomItem {

  public final String itemID;
  public final NamespacedKey key;
  private ItemStack baseItem = null;
  private Recipe recipe;

  public static NamespacedKey customKey = new NamespacedKey(ZenithPlugin.getInstance().getName().toLowerCase(),
      "customitem_id");
  public static NamespacedKey uuidKey = new NamespacedKey(ZenithPlugin.getInstance().getName().toLowerCase(),
      "customitem_uuid");

  protected abstract ItemStack generateItem(ItemStack itemStack, Player player);

  public abstract CustomItemData itemData();

  public BaseCustomItem(final String itemID, final Material material) {
    this.itemID = "customitem_" + itemID;
    this.key = new NamespacedKey(ZenithPlugin.getInstance().getName().toLowerCase(), itemID);
    setBaseItem(new ItemStack(material, 1));
  }

  public BaseCustomItem(final String itemID, final ItemStack item) {
    this.itemID = "customitem_" + itemID;
    this.key = new NamespacedKey(ZenithPlugin.getInstance().getName().toLowerCase(), itemID);
    setBaseItem(item);
  }

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

  public void setRecipe(Recipe recipe) {
    if (recipe == null) {
      throw new IllegalArgumentException("Invalid Item recipe for " + key);
    }
    this.recipe = recipe;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public void leftClick(PlayerInteractEvent event, UUID uuid) {
  }

  public void rightClick(PlayerInteractEvent event, UUID uuid) {
  }

  public void itemDamageEntity(EntityDamageByEntityEvent event, UUID uuid) {
  }

  public void rightClickOnEntity(PlayerInteractEntityEvent event, UUID uuid) {
  }

  public void itemSelectEvent(CustomItemSwitchedEvent event) {
  }

  public void itemSwitchEvent(CustomItemSwitchedEvent event) {
  }

  public void itemDroppedEvent(PlayerDropItemEvent event, UUID uuid) {
  }

  public void craftEvent(CraftItemEvent event) {

  }

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
