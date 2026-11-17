package com.lemonlightmc.zenith.commands.executors;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.commands.CommandAPI;
import com.lemonlightmc.zenith.commands.SimpleCommand;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;

/**
 * Bukkit {@link Command} adapter that delegates execution and tab completion to a {@link SimpleCommand}.
 */
public class InternalExecutor extends Command {

  private final SimpleCommand cmd;
  private final boolean runAsync;

  /**
   * Creates a new internal executor for the given simple command.
   *
   * @param cmd the simple command to delegate to
   * @param runAsync {@code true} to execute command handling asynchronously
   * @throws IllegalArgumentException if {@code cmd} is {@code null}
   */
  public InternalExecutor(final SimpleCommand cmd, final boolean runAsync) {
    super(cmd == null ? null : cmd.getKey());
    if (cmd == null) {
      throw new IllegalArgumentException("Command cannot be null");
    }
    this.cmd = cmd;
    super.setAliases(List.copyOf(cmd.getAliases()));
    this.runAsync = runAsync;
  }

  /**
   * Returns the delegated simple command.
   *
   * @return the simple command
   */
  public SimpleCommand getSimpleCommand() {
    return cmd;
  }

  /**
   * Returns the namespace of the delegated command.
   *
   * @return the command namespace
   */
  public String getNamespace() {
    return cmd.getNamespace();
  }

  /**
   * Returns the command label.
   *
   * @return the command label
   */
  @Override
  public String getLabel() {
    return super.getName();
  }

  /**
   * Updates the command label and synchronizes it with the delegated command key.
   *
   * @param label the new command label
   * @return {@code true} if the label was updated; {@code false} otherwise
   */
  @Override
  public boolean setLabel(final String label) {
    if (super.setLabel(label)) {
      cmd.setKey(label);
      return true;
    }
    return false;
  }

  /**
   * Executes this command, optionally dispatching handling asynchronously.
   *
   * @param sender the command sender
   * @param label0 the command label used
   * @param args the command arguments
   * @return {@code true} after handling is scheduled or completed
   */
  @Override
  public boolean execute(final CommandSender sender, final String label0, final String[] args) {
    if (!CommandAPI.getHandler().shouldHandle(cmd, sender, label0)) {
      return true;
    }
    if (!runAsync) {
      CommandAPI.getHandler().run(cmd, sender, args);
      return true;
    }
    GlobalScheduler
        .runAsync(() -> {
          CommandAPI.getHandler().run(cmd, sender, args);
        });
    return true;
  }

  /**
   * Provides tab-completion suggestions for this command.
   *
   * @param sender the command sender
   * @param label the command label used
   * @param args the current command arguments
   * @param location the sender location, if available
   * @return the tab-completion suggestions
   */
  @Override
  public List<String> tabComplete(
      final CommandSender sender,
      final String label,
      final String[] args,
      final Location location) {
    if (!CommandAPI.getHandler().shouldHandle(cmd, sender, label)) {
      return List.of();
    }
    return CommandAPI.getHandler().tabComplete(cmd, sender, args);
  }

  @Override
  public String toString() {
    return cmd.getName().toString() + "(" + ZenithProvider.instance().getDescription().getFullName() + ")";
  }

  @Override
  public int hashCode() {
    return 31 + cmd.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    return cmd.equals(((InternalExecutor) obj).cmd);
  }
}
