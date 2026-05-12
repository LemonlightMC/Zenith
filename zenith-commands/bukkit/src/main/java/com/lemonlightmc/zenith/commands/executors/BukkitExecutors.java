package com.lemonlightmc.zenith.commands.executors;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.executors.Executors.*;

public class BukkitExecutors {

  @FunctionalInterface
  public interface PlayerCommandExecutor
      extends NormalExecutor<Player> {

    @Override
    void run(CommandSource<Player> source, CommandArguments args) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.PLAYER;
    }
  }

  @FunctionalInterface
  public interface PlayerExecutionInfo
      extends NormalExecutorInfo<Player> {

    @Override
    void run(ExecutionInfo<Player> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.PLAYER;
    }
  }

  @FunctionalInterface
  public interface EntityCommandExecutor
      extends NormalExecutor<Entity> {

    @Override
    void run(CommandSource<Entity> source, CommandArguments args) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.ENTITY;
    }
  }

  @FunctionalInterface
  public interface EntityExecutionInfo
      extends NormalExecutorInfo<Entity> {

    @Override
    void run(ExecutionInfo<Entity> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.ENTITY;
    }
  }

  @FunctionalInterface
  public interface CommandBlockExecutor
      extends NormalExecutor<BlockCommandSender> {
    @Override
    void run(CommandSource<BlockCommandSender> source, CommandArguments args)
        throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.BLOCK;
    }
  }

  @FunctionalInterface
  public interface CommandBlockExecutionInfo
      extends NormalExecutorInfo<BlockCommandSender> {

    @Override
    void run(ExecutionInfo<BlockCommandSender> info)
        throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.BLOCK;
    }
  }

  @FunctionalInterface
  public interface ConsoleCommandExecutor
      extends NormalExecutor<ConsoleCommandSender> {

    @Override
    abstract void run(CommandSource<ConsoleCommandSender> source, CommandArguments args)
        throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.CONSOLE;
    }
  }

  @FunctionalInterface
  public interface ConsoleExecutionInfo
      extends NormalExecutorInfo<ConsoleCommandSender> {

    @Override
    void run(ExecutionInfo<ConsoleCommandSender> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.CONSOLE;
    }
  }

  @FunctionalInterface
  public interface RemoteConsoleCommandExecutor
      extends
      NormalExecutor<RemoteConsoleCommandSender> {

    @Override
    void run(CommandSource<RemoteConsoleCommandSender> source, CommandArguments args)
        throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.REMOTE;
    }
  }

  @FunctionalInterface
  public interface RemoteConsoleExecutionInfo
      extends
      NormalExecutorInfo<RemoteConsoleCommandSender> {

    @Override
    void run(ExecutionInfo<RemoteConsoleCommandSender> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.REMOTE;
    }
  }

  @FunctionalInterface
  public interface FeedbackForwardingExecutionInfo
      extends
      NormalExecutorInfo<CommandSender> {

    @Override
    void run(final ExecutionInfo<CommandSender> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.FEEDBACK_FORWARDING;
    }
  }

  @FunctionalInterface
  public interface FeedbackForwardingExecutor
      extends
      NormalExecutor<CommandSender> {

    @Override
    void run(CommandSource<CommandSender> source, CommandArguments args);

    @Override
    default ExecutorType getType() {
      return ExecutorType.FEEDBACK_FORWARDING;
    }
  }

  @FunctionalInterface
  public interface ProxyCommandExecutor
      extends NormalExecutor<ProxiedCommandSender> {

    @Override
    void run(CommandSource<ProxiedCommandSender> source, CommandArguments args) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.PROXY;
    }
  }

  @FunctionalInterface
  public interface ProxyExecutionInfo extends NormalExecutorInfo<ProxiedCommandSender> {

    @Override
    void run(ExecutionInfo<ProxiedCommandSender> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.PROXY;
    }
  }

  @FunctionalInterface
  public interface NativeCommandExecutor
      extends NormalExecutor<ProxiedCommandSender> {

    @Override
    void run(CommandSource<ProxiedCommandSender> source, CommandArguments args) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.NATIVE;
    }
  }

  @FunctionalInterface
  public interface NativeExecutionInfo
      extends NormalExecutorInfo<ProxiedCommandSender> {

    @Override
    void run(ExecutionInfo<ProxiedCommandSender> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.NATIVE;
    }
  }
}
