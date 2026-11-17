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

/**
 * Bukkit-backed implementation of {@link CommandSource} providing sender, location, and entity context.
 *
 * @param <S> the command sender type
 * @param sender the underlying command sender
 * @param location the resolved sender location, or {@code null} if unavailable
 * @param entity the resolved sender entity, or {@code null} if unavailable
 */
public record BukkitCommandSource<S extends CommandSender>(S sender, Location location, Entity entity)
    implements CommandSource<S> {

  /**
   * Creates a command source from the given sender, resolving its location and entity context.
   *
   * @param <T> the sender type
   * @param sender the command sender to wrap
   * @return a new command source for the sender
   */
  public static <T extends CommandSender> BukkitCommandSource<T> from(final T sender) {
    return new BukkitCommandSource<T>(sender, getLocation(sender), getEntity(sender));
  }

  /**
   * Creates a copy of this source for a different sender while preserving location and entity context.
   *
   * @param <T> the new sender type
   * @param newSender the sender to associate with the copied source
   * @return a copied command source for the new sender
   */
  @Override
  public <T extends S> BukkitCommandSource<T> copyFor(final T newSender) {
    return new BukkitCommandSource<T>(newSender, location, entity);
  }

  /**
   * Creates a copy of this command source.
   *
   * @return a copied command source
   */
  @Override
  public BukkitCommandSource<S> copy() {
    return new BukkitCommandSource<S>(sender, location, entity);
  }

  /**
   * Checks whether the underlying sender has the specified permission.
   *
   * @param perm the permission node to check, or {@code null} or empty for no restriction
   * @return {@code true} if the sender has permission or no permission is required; {@code false} otherwise
   */
  @Override
  public boolean hasPermission(final String perm) {
    return perm == null || perm.length() == 0 || sender.hasPermission(perm);
  }

  /**
   * Sends a command error message to the underlying sender.
   *
   * @param e the command exception describing the error
   */
  @Override
  public void sendError(final CommandException e) {
    ChatAPI.send(sender, e.getMessage());
  }

  /**
   * Sends a message to the underlying sender.
   *
   * @param str the message to send
   */
  @Override
  public void sendMessage(final String str) {
    ChatAPI.send(sender, str);
  }

  /**
   * Returns the world associated with this source's location.
   *
   * @return the source world, or {@code null} if no location is available
   */
  @Override
  public World world() {
    return location != null ? location.getWorld() : null;
  }

  /**
   * Returns the executor type for the underlying sender.
   *
   * @return the resolved executor type
   */
  @Override
  public ExecutorType executorType() {
    return getExecutorType(sender);
  }

  /**
   * Resolves the location associated with a command sender.
   *
   * @param sender the command sender to inspect
   * @return the sender location, or {@code null} if unavailable
   */
  public static Location getLocation(final CommandSender sender) {
    return switch (sender) {
      case final Entity entity -> entity.getLocation();
      case final BlockCommandSender block -> block.getBlock().getLocation();
      default -> null;
    };
  }

  /**
   * Resolves the entity associated with a command sender.
   *
   * @param sender the command sender to inspect
   * @return the sender entity, or {@code null} if unavailable
   */
  public static Entity getEntity(final CommandSender sender) {
    return switch (sender) {
      case final Player player -> player;
      case final Entity entity -> entity;
      default -> null;
    };
  }

  /**
   * Resolves the world associated with a command sender.
   *
   * @param sender the command sender to inspect
   * @return the sender world, or {@code null} if unavailable
   */
  public static World getWorld(final CommandSender sender) {
    return switch (sender) {
      case final Entity entity -> entity.getWorld();
      case final BlockCommandSender block -> block.getBlock().getWorld();
      default -> null;
    };
  }

  /**
   * Resolves the executor type for a command sender.
   *
   * @param sender the command sender to inspect
   * @return the matching executor type, or {@link ExecutorType#ALL} if unknown or {@code null}
   */
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
