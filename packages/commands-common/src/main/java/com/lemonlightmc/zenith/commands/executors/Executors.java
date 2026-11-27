package com.lemonlightmc.zenith.commands.executors;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;

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

  public interface NormalExecutor<S> {
    void run(CommandContext<S> source, CommandArguments args) throws CommandException;

    @SuppressWarnings("unchecked")
    default int executeWith(final CommandContext<?> source, final CommandArguments args)
        throws CommandException {
      this.run((CommandContext<S>) source, args);
      return 1;
    }

    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }

  @FunctionalInterface
  public interface CommandExecutor<S> extends NormalExecutor<S> {

    @Override
    void run(CommandContext<S> source, CommandArguments args) throws CommandException;

    @Override
    default ExecutorType getType() {
      return ExecutorType.ALL;
    }
  }
}
