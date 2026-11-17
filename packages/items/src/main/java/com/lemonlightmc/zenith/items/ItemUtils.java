package com.lemonlightmc.zenith.items;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Utility methods for creating common item stacks.
 */
public class ItemUtils {

  /**
   * Creates a player head item for the given player UUID and display name.
   *
   * @param uuid the unique identifier of the owning player
   * @param name the display name to set on the item
   * @return the created player head, or {@code null} if the UUID is {@code null}
   */
  public ItemStack getPlayerHead(UUID uuid, String name) {
    return uuid == null ? null : getPlayerHead(Bukkit.getOfflinePlayer(uuid), name);
  }

  /**
   * Creates a player head item for the given player UUID.
   *
   * @param uuid the unique identifier of the owning player
   * @return the created player head, or {@code null} if the UUID is {@code null}
   */
  public ItemStack getPlayerHead(UUID uuid) {
    if (uuid == null) {
      return null;
    }
    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
    return getPlayerHead(p, p.getName());
  }

  /**
   * Creates a player head item for the given offline player.
   *
   * @param p the owning offline player
   * @return the created player head, or {@code null} if the player is {@code null}
   */
  public ItemStack getPlayerHead(OfflinePlayer p) {
    return p == null ? null : getPlayerHead(p, p.getName());
  }

  /**
   * Creates a player head item for the given offline player and display name.
   *
   * @param p the owning offline player
   * @param name the display name to set on the item
   * @return the created player head, or {@code null} if the player is {@code null}
   */
  public ItemStack getPlayerHead(OfflinePlayer p, String name) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta meta = (SkullMeta) (item.hasItemMeta() ? item.getItemMeta()
        : Bukkit.getItemFactory().getItemMeta(item.getType()));
    assert meta != null;
    meta.setOwningPlayer(p);
    meta.setDisplayName(name);
    item.setItemMeta(meta);
    return item;
  }
}
