package com.lemonlightmc.zenith.items;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtils {

  public ItemStack getPlayerHead(UUID uuid, String name) {
    return uuid == null ? null : getPlayerHead(Bukkit.getOfflinePlayer(uuid), name);
  }

  public ItemStack getPlayerHead(UUID uuid) {
    if (uuid == null) {
      return null;
    }
    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
    return getPlayerHead(p, p.getName());
  }

  public ItemStack getPlayerHead(OfflinePlayer p) {
    return p == null ? null : getPlayerHead(p, p.getName());
  }

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
