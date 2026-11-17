package com.lemonlightmc.zenith.items;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a change in the player's held item involving custom item selections.
 *
 * <p>This event carries both the raw item stacks and resolved custom item instances for the previous and newly held
 * slots.
 */
public class CustomItemSwitchedEvent implements Cancellable {

  private final Player player;

  private final int oldSlot;

  private final int newSlot;

  private final ItemStack oldItemStack;

  private final ItemStack heldItemStack;

  private final BaseCustomItem heldCustomItem;

  private final BaseCustomItem oldCustomItem;

  private boolean cancel;

  /**
   * Creates a custom item switch event.
   *
   * @param player the player whose held item changed
   * @param oldSlot the previously selected slot
   * @param newSlot the newly selected slot
   * @param oldItemStack the item stack in the previous slot
   * @param heldItemStack the item stack in the new slot
   * @param heldCustomItem the custom item in the new slot, if any
   * @param oldCustomItem the custom item in the previous slot, if any
   */
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

  /**
   * Returns the player whose held item changed.
   *
   * @return the affected player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns the previously selected slot index.
   *
   * @return the previous slot index
   */
  public int getOldSlot() {
    return oldSlot;
  }

  /**
   * Returns the newly selected slot index.
   *
   * @return the new slot index
   */
  public int getNewSlot() {
    return newSlot;
  }

  /**
   * Returns the item stack from the previous slot.
   *
   * @return the previous item stack
   */
  public ItemStack getOldItem() {
    return oldItemStack;
  }

  /**
   * Returns the item stack from the new slot.
   *
   * @return the new item stack
   */
  public ItemStack getNewItem() {
    return heldItemStack;
  }

  /**
   * Returns the custom item from the previous slot, if any.
   *
   * @return the previous custom item, or {@code null} if none
   */
  public BaseCustomItem getOldCustomItem() {
    return oldCustomItem;
  }

  /**
   * Returns the custom item from the new slot, if any.
   *
   * @return the new custom item, or {@code null} if none
   */
  public BaseCustomItem getNewCustomItem() {
    return heldCustomItem;
  }

  /**
   * Returns whether this event is cancelled.
   *
   * @return {@code true} if the event is cancelled; {@code false} otherwise
   */
  @Override
  public boolean isCancelled() {
    return cancel;
  }

  /**
   * Sets whether this event is cancelled.
   *
   * @param cancel {@code true} to cancel the event; {@code false} otherwise
   */
  @Override
  public void setCancelled(final boolean cancel) {
    this.cancel = cancel;
  }

}
