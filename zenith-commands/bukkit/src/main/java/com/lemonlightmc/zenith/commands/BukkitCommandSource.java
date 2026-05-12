package com.lemonlightmc.zenith.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.apis.ChatAPI;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.executors.Executors.ExecutorType;

public record BukkitCommandSource<S extends CommandSender>(S sender, Location location, Entity entity)
    implements CommandSource<S> {

  public static <T extends CommandSender> BukkitCommandSource<T> from(final T sender) {
    return new BukkitCommandSource<T>(sender, getLocation(sender), getEntity(sender));
  }

  @Override
  public <T extends S> BukkitCommandSource<T> copyFor(final T newSender) {
    return new BukkitCommandSource<T>(newSender, location, entity);
  }

  @Override
  public BukkitCommandSource<S> copy() {
    return new BukkitCommandSource<S>(sender, location, entity);
  }

  @Override
  public boolean hasPermission(final String perm) {
    return perm == null || perm.length() == 0 || sender.hasPermission(perm);
  }

  @Override
  public void sendError(final CommandException e) {
    ChatAPI.send(sender, e.getMessage());
  }

  @Override
  public void sendMessage(final String str) {
    ChatAPI.send(sender, str);
  }

  @Override
  public World world() {
    return location != null ? location.getWorld() : null;
  }

  @Override
  public ExecutorType executorType() {
    return getExecutorType(sender);
  }

  public static Location getLocation(final CommandSender sender) {
    return switch (sender) {
      case final Entity entity -> entity.getLocation();
      case final BlockCommandSender block -> block.getBlock().getLocation();
      default -> null;
    };
  }

  public static Entity getEntity(final CommandSender sender) {
    return switch (sender) {
      case final Player player -> player;
      case final Entity entity -> entity;
      default -> null;
    };
  }

  public static World getWorld(final CommandSender sender) {
    return switch (sender) {
      case final Entity entity -> entity.getWorld();
      case final BlockCommandSender block -> block.getBlock().getWorld();
      default -> null;
    };
  }

  public static ExecutorType getExecutorType(final CommandSender sender) {
    if (sender == null) {
      return ExecutorType.ALL;
    } else if (sender instanceof Player) {
      return ExecutorType.PLAYER;
    } else if (sender instanceof Entity) {
      return ExecutorType.ENTITY;
    } else if (sender instanceof ConsoleCommandSender) {
      return ExecutorType.CONSOLE;
    } else if (sender instanceof BlockCommandSender) {
      return ExecutorType.BLOCK;
    } else if (sender instanceof ProxiedCommandSender) {
      return ExecutorType.PROXY;
    } else if (sender instanceof RemoteConsoleCommandSender) {
      return ExecutorType.REMOTE;
    }
    return ExecutorType.ALL;
  }
}
