package com.lemonlightmc.zenith.commands.executors;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.actions.ExecutionContext;

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
    void run(ExecutionContext<S> source, CommandArguments args) throws CommandException;

    @SuppressWarnings("unchecked")
    default int executeWith(final ExecutionContext<?> source, final CommandArguments args)
        throws CommandException {
      this.run((ExecutionContext<S>) source, args);
      return 1;
    }

    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }

  @FunctionalInterface
  public interface CommandExecutor<S extends CommandSender> extends NormalExecutor<S> {

    @Override
    void run(ExecutionContext<S> source, CommandArguments args) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }
}
