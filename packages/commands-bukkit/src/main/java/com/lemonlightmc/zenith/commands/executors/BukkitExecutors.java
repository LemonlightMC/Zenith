package com.lemonlightmc.zenith.commands.executors;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.executors.Executors.*;
import com.lemonlightmc.zenith.actions.ExecutionContext;

/**
 * Bukkit-specific executor interfaces for handling commands from different
 * sender types.
 */
public class BukkitExecutors {

  /**
   * Executor for commands run by a {@link Player}.
   */
  @FunctionalInterface
  public interface PlayerCommandExecutor extends NormalExecutor<Player> {

    /**
     * Executes the command for a player source.
     *
     * @param source the player command source
     * @param args   the parsed command arguments
     * @throws CommandException if command execution fails
     */
    @Override
    void run(ExecutionContext<Player> source, CommandArguments args) throws CommandException;

    /**
     * Returns the executor type for player commands.
     *
     * @return {@link ExecutorType#PLAYER}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.PLAYER;
    }
  }

  /**
   * Executor for commands run by an {@link Entity}.
   */
  @FunctionalInterface
  public interface EntityCommandExecutor
      extends NormalExecutor<Entity> {

    /**
     * Executes the command for an entity source.
     *
     * @param source the entity command source
     * @param args   the parsed command arguments
     * @throws CommandException if command execution fails
     */
    @Override
    void run(ExecutionContext<Entity> source, CommandArguments args) throws CommandException;

    /**
     * Returns the executor type for entity commands.
     *
     * @return {@link ExecutorType#ENTITY}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.ENTITY;
    }
  }

  /**
   * Executor for commands run by a {@link BlockCommandSender}.
   */
  @FunctionalInterface
  public interface CommandBlockExecutor
      extends NormalExecutor<BlockCommandSender> {

    /**
     * Executes the command for a command block source.
     *
     * @param source the command block source
     * @param args   the parsed command arguments
     * @throws CommandException if command execution fails
     */
    @Override
    void run(ExecutionContext<BlockCommandSender> source, CommandArguments args)
        throws CommandException;

    /**
     * Returns the executor type for command block commands.
     *
     * @return {@link ExecutorType#BLOCK}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.BLOCK;
    }
  }

  /**
   * Executor for commands run by a {@link ConsoleCommandSender}.
   */
  @FunctionalInterface
  public interface ConsoleCommandExecutor
      extends NormalExecutor<ConsoleCommandSender> {

    /**
     * Executes the command for a console source.
     *
     * @param source the console command source
     * @param args   the parsed command arguments
     * @throws CommandException if command execution fails
     */
    @Override
    abstract void run(ExecutionContext<ConsoleCommandSender> source, CommandArguments args)
        throws CommandException;

    /**
     * Returns the executor type for console commands.
     *
     * @return {@link ExecutorType#CONSOLE}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.CONSOLE;
    }
  }

  /**
   * Executor for commands run by a {@link RemoteConsoleCommandSender}.
   */
  @FunctionalInterface
  public interface RemoteConsoleCommandExecutor
      extends
      NormalExecutor<RemoteConsoleCommandSender> {

    /**
     * Executes the command for a remote console source.
     *
     * @param source the remote console command source
     * @param args   the parsed command arguments
     * @throws CommandException if command execution fails
     */
    @Override
    void run(ExecutionContext<RemoteConsoleCommandSender> source, CommandArguments args)
        throws CommandException;

    /**
     * Returns the executor type for remote console commands.
     *
     * @return {@link ExecutorType#REMOTE}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.REMOTE;
    }
  }

  /**
   * Executor for feedback-forwarding command execution.
   */
  @FunctionalInterface
  public interface FeedbackForwardingExecutor
      extends
      NormalExecutor<CommandSender> {

    /**
     * Executes the command for a feedback-forwarding source.
     *
     * @param source the command source
     * @param args   the parsed command arguments
     */
    @Override
    void run(ExecutionContext<CommandSender> source, CommandArguments args);

    /**
     * Returns the executor type for feedback-forwarding commands.
     *
     * @return {@link ExecutorType#FEEDBACK_FORWARDING}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.FEEDBACK_FORWARDING;
    }
  }

  /**
   * Executor for commands run by a {@link ProxiedCommandSender}.
   */
  @FunctionalInterface
  public interface ProxyCommandExecutor
      extends NormalExecutor<ProxiedCommandSender> {

    /**
     * Executes the command for a proxied source.
     *
     * @param source the proxied command source
     * @param args   the parsed command arguments
     * @throws CommandException if command execution fails
     */
    @Override
    void run(ExecutionContext<ProxiedCommandSender> source, CommandArguments args) throws CommandException;

    /**
     * Returns the executor type for proxied commands.
     *
     * @return {@link ExecutorType#PROXY}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.PROXY;
    }
  }

  /**
   * Executor for commands run by a native proxied sender.
   */
  @FunctionalInterface
  public interface NativeCommandExecutor
      extends NormalExecutor<ProxiedCommandSender> {

    /**
     * Executes the command for a native proxied source.
     *
     * @param source the proxied command source
     * @param args   the parsed command arguments
     * @throws CommandException if command execution fails
     */
    @Override
    void run(ExecutionContext<ProxiedCommandSender> source, CommandArguments args) throws CommandException;

    /**
     * Returns the executor type for native commands.
     *
     * @return {@link ExecutorType#NATIVE}
     */
    @Override
    default ExecutorType getType() {
      return ExecutorType.NATIVE;
    }
  }
}
