package com.lemonlightmc.zenith.commands;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandResult;

public record BukkitCommandResult<S extends CommandSender>(Command command, String[] args)
    implements CommandResult<Command, S> {

  @Override
  public boolean execute(final S sender) {
    return command.execute(sender, command.getLabel(), args);
  }

  @Override
  public boolean execute(final CommandSource<S> source) {
    return command.execute(source.sender(), command.getLabel(), args);
  }

  @Override
  public int getArgumentCount() {
    return args.length;
  }

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
