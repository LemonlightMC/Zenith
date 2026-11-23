package com.lemonlightmc.zenith.utils.actions;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.integrations.LuckPerms;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.milkbowl.vault2.economy.Economy;

public class Actions3 {

  public static class AddCurrencyAction extends Action<CommandSender> {

    private final double amount;

    public AddCurrencyAction(final double amount) {
      this(amount, null);
    }

    public AddCurrencyAction(final double amount, final Condition<CommandSender> conditions) {
      super(conditions);
      this.amount = amount;
    }

    public AddCurrencyAction(final double amount, final Condition<CommandSender> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    public double getAmount() {
      return amount;
    }

    @Override
    protected boolean run(final ConditionContext<CommandSender> context) {
      final Player player = context.player();
      if (player == null) {
        return false;
      }
      final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
      if (rsp == null || rsp.getProvider() == null) {
        return true;
      }
      rsp.getProvider().deposit(ZenithProvider.instance().getName(), player.getUniqueId(), BigDecimal.valueOf(amount));
      return true;
    }
  }

  public static class RemoveCurrencyAction extends Action<CommandSender> {

    private final double amount;

    public RemoveCurrencyAction(final double amount) {
      this(amount, null);
    }

    public RemoveCurrencyAction(final double amount, final Condition<CommandSender> conditions) {
      super(conditions);
      this.amount = amount;
    }

    public RemoveCurrencyAction(final double amount, final Condition<CommandSender> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    public double getAmount() {
      return amount;
    }

    @Override
    protected boolean run(final ConditionContext<CommandSender> context) {
      final Player player = context.player();
      if (player == null) {
        return false;
      }
      final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
      if (rsp == null || rsp.getProvider() == null) {
        return true;
      }
      rsp.getProvider().withdraw(ZenithProvider.instance().getName(), player.getUniqueId(), BigDecimal.valueOf(amount));
      return true;
    }
  }

  public static class PermissionAction extends Action<CommandSender> {

    public enum Mode {
      GRANT,
      REVOKE,
      SET
    }

    private final String permission;
    private final Mode mode;
    private final boolean value;

    public PermissionAction(final String permission, final Mode mode) {
      this(permission, mode, true, null);
    }

    public PermissionAction(final String permission, final Mode mode, final boolean value) {
      this(permission, mode, value, null);
    }

    public PermissionAction(final String permission, final Mode mode, final boolean value,
        final Condition<CommandSender> conditions) {
      super(conditions);
      this.permission = permission;
      this.mode = mode == null ? Mode.GRANT : mode;
      this.value = value;
    }

    public PermissionAction(final String permission, final Mode mode, final boolean value,
        final Condition<CommandSender> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.permission = permission;
      this.mode = mode == null ? Mode.GRANT : mode;
      this.value = value;
    }

    @Override
    protected boolean run(final ConditionContext<CommandSender> context) {
      if (permission == null || permission.isEmpty()) {
        return true;
      }
      final Player player = context.player();
      if (player == null) {
        return false;
      }
      if (LuckPerms.isEnabled()) {
        final net.luckperms.api.LuckPerms luckPerms = LuckPerms.getOrNull();
        if (luckPerms != null) {
          applyLuckPerms(luckPerms, player);
          return true;
        }
      }
      applyBukkit(player);
      return true;
    }

    private void applyLuckPerms(final net.luckperms.api.LuckPerms luckPerms, final Player player) {
      luckPerms.getUserManager().modifyUser(player.getUniqueId(), (final User user) -> {
        final Node baseNode = Node.builder(permission).build();
        user.data().remove(baseNode);
        if (mode == Mode.REVOKE) {
          user.data().add(Node.builder(permission).value(false).build());
        } else if (mode == Mode.SET) {
          user.data().add(Node.builder(permission).value(value).build());
        } else {
          user.data().add(Node.builder(permission).value(true).build());
        }
      });
    }

    private void applyBukkit(final Player player) {
      try {
        if (mode == Mode.REVOKE) {
          player.addAttachment(ZenithProvider.instance(), permission, false);
        } else if (mode == Mode.SET) {
          player.addAttachment(ZenithProvider.instance(), permission, value);
        } else {
          player.addAttachment(ZenithProvider.instance(), permission, true);
        }
      } catch (final Throwable ignored) {
      }
    }
  }
}
