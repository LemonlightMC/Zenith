package com.lemonlightmc.zenith.items;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class CustomItemSwitchedEvent implements Cancellable {

  private final Player player;

  private final int oldSlot;

  private final int newSlot;

  private final ItemStack oldItemStack;

  private final ItemStack heldItemStack;

  private final BaseCustomItem heldCustomItem;

  private final BaseCustomItem oldCustomItem;

  private boolean cancel;

  public CustomItemSwitchedEvent(final Player player, final int oldSlot, final int newSlot,
      final ItemStack oldItemStack,
      final ItemStack heldItemStack, final BaseCustomItem heldCustomItem, final BaseCustomItem oldCustomItem) {
    this.player = player;
    this.oldSlot = oldSlot;
    this.newSlot = newSlot;
    this.oldItemStack = oldItemStack;
    this.heldItemStack = heldItemStack;
    this.heldCustomItem = heldCustomItem;
    this.oldCustomItem = oldCustomItem;
  }

  public Player getPlayer() {
    return player;
  }

  public int getOldSlot() {
    return oldSlot;
  }

  public int getNewSlot() {
    return newSlot;
  }

  public ItemStack getOldItem() {
    return oldItemStack;
  }

  public ItemStack getNewItem() {
    return heldItemStack;
  }

  public BaseCustomItem getOldCustomItem() {
    return oldCustomItem;
  }

  public BaseCustomItem getNewCustomItem() {
    return heldCustomItem;
  }

  @Override
  public boolean isCancelled() {
    return cancel;
  }

  @Override
  public void setCancelled(final boolean cancel) {
    this.cancel = cancel;
  }

}
