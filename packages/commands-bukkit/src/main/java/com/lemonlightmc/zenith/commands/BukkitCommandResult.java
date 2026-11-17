package com.lemonlightmc.zenith.commands;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandResult;

/**
 * Represents a parsed Bukkit command invocation ready for execution.
 *
 * @param <S> the command sender type
 * @param command the resolved Bukkit command
 * @param args the arguments to pass when executing the command
 */
public record BukkitCommandResult<S extends CommandSender>(Command command, String[] args)
    implements CommandResult<Command, S> {

  /**
   * Executes this command for the given sender.
   *
   * @param sender the command sender
   * @return {@code true} if execution succeeds; {@code false} otherwise
   */
  @Override
  public boolean execute(final S sender) {
    return command.execute(sender, command.getLabel(), args);
  }

  /**
   * Executes this command using the sender from the given command source.
   *
   * @param source the command source providing the sender
   * @return {@code true} if execution succeeds; {@code false} otherwise
   */
  @Override
  public boolean execute(final CommandSource<S> source) {
    return command.execute(source.sender(), command.getLabel(), args);
  }

  /**
   * Returns the number of arguments stored in this result.
   *
   * @return the argument count
   */
  @Override
  public int getArgumentCount() {
    return args.length;
  }

  /**
   * Replaces the argument at the specified index.
   *
   * @param idx the argument index to modify
   * @param value the new argument value
   */
  @Override
  public void modifyArguments(final int idx, final String value) {
    args[idx] = value;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final BukkitCommandResult<?> that = (BukkitCommandResult<?>) o;
    return command.equals(that.command) && Arrays.equals(args, that.args);
  }

  @Override
  public int hashCode() {
    return 31 * command.hashCode() + Arrays.hashCode(args);
  }

  @Override
  public String toString() {
    return "CommandResult [command=" + command + ", args=" + String.join(" ", args) + "]";
  }
}
