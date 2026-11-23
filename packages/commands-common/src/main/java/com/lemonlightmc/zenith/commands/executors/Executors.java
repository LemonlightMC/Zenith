package com.lemonlightmc.zenith.commands.executors;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.actions.ConditionContext;

public class Executors {

  public enum ExecutorType {
    ALL,

    PLAYER,

    ENTITY,

    CONSOLE,

    BLOCK,

    // NativeProxyCommandSender
    PROXY,

    // NativeProxyCommandSender (always)
    NATIVE,

    // RemoteConsoleCommandSender
    REMOTE,

    // Paper's FeedbackForwardingSender
    FEEDBACK_FORWARDING,
  }

  public interface NormalExecutor<S extends CommandSender> {
    void run(ConditionContext<S> source, CommandArguments args) throws CommandException;

    default void run(
        final ExecutionInfo<S> info)
        throws CommandException {
      this.run(info.source(), info.args());
    }

    @SuppressWarnings("unchecked")
    default int executeWith(final ConditionContext<?> source, final CommandArguments args)
        throws CommandException {
      this.run((ConditionContext<S>) source, args);
      return 1;
    }

    @SuppressWarnings("unchecked")
    default int executeWith(final ExecutionInfo<?> info)
        throws CommandException {
      this.run((ExecutionInfo<S>) info);
      return 1;
    }

    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }

  public interface NormalExecutorInfo<S extends CommandSender> extends NormalExecutor<S> {
    @Override
    void run(ExecutionInfo<S> info) throws CommandException;

    @Override
    default void run(
        final ConditionContext<S> source, final CommandArguments args)
        throws CommandException {
      this.run(new ExecutionInfo<>(source, args));
    }

    @Override
    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }

  @FunctionalInterface
  public interface CommandExecutor<S extends CommandSender> extends NormalExecutor<S> {

    @Override
    void run(ConditionContext<S> source, CommandArguments args) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }

  @FunctionalInterface
  public interface CommandExecutionInfo<S extends CommandSender> extends NormalExecutorInfo<S> {

    @Override
    void run(ExecutionInfo<S> info) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }
}
