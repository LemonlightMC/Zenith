package com.lemonlightmc.zenith.items;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.lemonlightmc.zenith.apis.ChatAPI;
import com.lemonlightmc.zenith.messages.Logger;

public class CustomItemListener implements Listener {

  @EventHandler()
  public void clicks(PlayerInteractEvent event) {
    ItemStack item = event.getItem();
    Action action = event.getAction();
    Player player = event.getPlayer();

    PersistentDataContainer pdc = getPDC(item);
    BaseCustomItem citem = getCustomItem(pdc);
    UUID uuid = getUUID(pdc);
    if (citem == null || uuid == null) {
      return;
    }

    if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
      if (citem.itemData().containProperty("disableactions_thisistails")) {
        event.setCancelled(true);
      }

      try {
        citem.leftClick(event, uuid);
      } catch (Exception error) {
        ChatAPI.send(player, "Failed to execute click action!");
        error.printStackTrace();
      }
      return;
    }

    if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
      if (citem.itemData().containProperty("disableactions_thisistails")) {
        event.setCancelled(true);
      }

      try {
        citem.rightClick(event, uuid);
      } catch (Exception error) {
        ChatAPI.send(player, "Failed to execute click action!");
        error.printStackTrace();
      }
      return;
    }
  }

  @EventHandler
  public void onItemSwitch(PlayerItemHeldEvent event) {
    int newSlot = event.getNewSlot();
    int oldSlot = event.getPreviousSlot();
    Player player = event.getPlayer();

    ItemStack currentSlotItem = player.getInventory().getItem(newSlot);
    ItemStack oldSlotItem = player.getInventory().getItem(oldSlot);

    BaseCustomItem newCitem = getCustomItem(getPDC(currentSlotItem));
    BaseCustomItem oldCitem = getCustomItem(getPDC(oldSlotItem));

    CustomItemSwitchedEvent switchEvent = new CustomItemSwitchedEvent(player, oldSlot, newSlot, oldSlotItem,
        currentSlotItem, oldCitem, newCitem);

    if (oldCitem != null) {
      try {
        oldCitem.itemSwitchEvent(switchEvent);
      } catch (Exception error) {
        Logger.warn("Failed to execute action for custom item with ID: " + oldCitem.itemData().getId());
      }
    }
    if (newCitem != null) {
      try {
        newCitem.itemSelectEvent(switchEvent);
      } catch (Exception error) {
        Logger.warn("Failed to execute action for custom item with ID: " + newCitem.itemData().getId());
      }
    }

    event.setCancelled(switchEvent.isCancelled());
  }

  @EventHandler
  public void onSuccessfullCraft(CraftItemEvent event) {
    if (event.getWhoClicked() == null || !(event.getWhoClicked() instanceof Player)) {
      return;
    }
    PersistentDataContainer pdc = getPDC(event.getRecipe().getResult());
    BaseCustomItem citem = getCustomItem(pdc);
    if (citem == null) {
      return;
    }
    try {
      citem.craftEvent(event);
    } catch (Exception error) {
      Logger.warn("Failed to execute craftEvent for item with ID: " + citem.itemData().getId());
    }

    if (!event.isCancelled()) {
      if (citem.itemData().isUniqueId() && getUUID(getPDC(event.getCurrentItem())) == null) {
        event.setCurrentItem(citem.getItem((Player) event.getWhoClicked()));
      }
    }
  }

  @EventHandler()
  public void damage(EntityDamageByEntityEvent event) {
    if (event.getDamager() == null || !(event.getDamager() instanceof Player))
      return;

    PersistentDataContainer pdc = getPDC(((Player) event.getDamager()).getInventory().getItemInMainHand());
    BaseCustomItem citem = getCustomItem(pdc);
    if (citem == null) {
      return;
    }
    if (citem.itemData().containProperty("disableactions_thisistails")) {
      event.setCancelled(true);
    }
    try {
      citem.itemDamageEntity(event, getUUID(pdc));
    } catch (Exception error) {
      Logger.warn("Failed to execute action for custom item with ID: " + citem.itemData().getId());
    }
  }

  @EventHandler()
  public void onInteractionEventWithEntity(PlayerInteractEntityEvent event) {
    PersistentDataContainer pdc = getPDC(event.getPlayer().getInventory().getItemInMainHand());
    BaseCustomItem citem = getCustomItem(pdc);
    if (citem == null) {
      return;
    }

    try {
      citem.rightClickOnEntity(event, getUUID(pdc));
    } catch (Exception error) {
      Logger.warn("Failed to execute action for custom item with ID: " + citem.itemData().getId());
    }
  }

  @EventHandler
  private static void onDropItem(PlayerDropItemEvent event) {
    PersistentDataContainer pdc = getPDC(event.getItemDrop().getItemStack());
    BaseCustomItem citem = getCustomItem(pdc);
    if (citem == null) {
      return;
    }
    try {
      citem.itemDroppedEvent(event, getUUID(pdc));
    } catch (Exception error) {
      Logger.warn("Failed to execute action for custom item with ID: " + citem.itemData().getId());
    }
  }

  private static BaseCustomItem getCustomItem(PersistentDataContainer pdc) {
    if (pdc == null) {
      return null;
    }
    return CustomItemManager.getManager().get(
        pdc.get(BaseCustomItem.customKey, PersistentDataType.STRING));
  }

  public static UUID getUUID(PersistentDataContainer pdc) {
    if (pdc == null) {
      return null;
    }
    try {
      return UUID.fromString(pdc.get(BaseCustomItem.uuidKey, PersistentDataType.STRING));
    } catch (IllegalArgumentException | NullPointerException e) {
      return null;
    }
  }

  public static PersistentDataContainer getPDC(ItemStack item) {
    if (item == null) {
      return null;
    }
    ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return null;
    }
    return meta.getPersistentDataContainer();
  }
}
