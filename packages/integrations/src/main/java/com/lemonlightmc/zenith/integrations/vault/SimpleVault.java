package com.lemonlightmc.zenith.integrations.vault;

import java.math.BigDecimal;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.messages.Logger;

import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;

public class SimpleVault {
  public static boolean hasVault() {
    return Bukkit.getServer().getPluginManager().getPlugin("Vault") != null;
  }

  public static SimpleVault create() {
    return new SimpleVault();
  }

  private Economy economy = null;
  private String name;

  public SimpleVault() {
    if (!setupEconomy()) {
      Logger.warn("Could not find Vault");
    }
  }

  public boolean setupEconomy() {
    if (!hasVault()) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = Bukkit
        .getServer()
        .getServicesManager()
        .getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    this.economy = rsp.getProvider();
    name = ZenithProvider.getInstance().getName();
    return true;
  }

  public Economy getEconomy() {
    return economy;
  }

  // get

  public BigDecimal getMoney(OfflinePlayer player) {
    return getMoney(player.getUniqueId());
  }

  public BigDecimal getMoney(UUID player) {
    try {
      return economy.balance(name, player);
    } catch (NullPointerException ignore) {
      return BigDecimal.ZERO;
    }
  }

  // check

  public boolean checkMoney(OfflinePlayer player, double amount) {
    return getMoney(player.getUniqueId()).compareTo(new BigDecimal(amount)) >= 0;
  }

  public boolean checkMoney(OfflinePlayer player, BigDecimal amount) {
    return getMoney(player.getUniqueId()).compareTo(amount) >= 0;
  }

  public boolean checkMoney(UUID player, BigDecimal amount) {
    return getMoney(player).compareTo(amount) >= 0;
  }

  public boolean checkMoney(UUID player, double amount) {
    return getMoney(player).compareTo(new BigDecimal(amount)) >= 0;
  }

  // remove

  public EconomyResponse removeMoney(OfflinePlayer player, double amount) {
    return economy.withdraw(name, player.getUniqueId(), new BigDecimal(amount));
  }

  public EconomyResponse removeMoney(OfflinePlayer player, BigDecimal amount) {
    return economy.withdraw(name, player.getUniqueId(), amount);
  }

  public EconomyResponse removeMoney(UUID player, BigDecimal amount) {
    return economy.withdraw(name, player, amount);
  }

  public EconomyResponse removeMoney(UUID player, double amount) {
    return economy.withdraw(name, player, new BigDecimal(amount));
  }

  // add

  public EconomyResponse addMoney(OfflinePlayer player, double amount) {
    return economy.deposit(name, player.getUniqueId(), new BigDecimal(amount));
  }

  public EconomyResponse addMoney(OfflinePlayer player, BigDecimal amount) {
    return economy.deposit(name, player.getUniqueId(), amount);
  }

  public EconomyResponse addMoney(UUID player, BigDecimal amount) {
    return economy.deposit(name, player, amount);
  }

  public EconomyResponse addMoney(UUID player, double amount) {
    return economy.deposit(name, player, new BigDecimal(amount));
  }
}
