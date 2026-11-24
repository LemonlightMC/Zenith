package com.lemonlightmc.zenith.commands.argumentsbase;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.actions.ExecutionContext;

public interface CommandResult<C, S extends CommandSender> {

  /**
   * Executes this command for the given sender.
   *
   * @param sender the command sender
   * @return {@code true} if execution succeeds; {@code false} otherwise
   */
  public boolean execute(final S sender);

  /**
   * Executes this command using the sender from the given command source.
   *
   * @param source the command source providing the sender
   * @return {@code true} if execution succeeds; {@code false} otherwise
   */
  public boolean execute(final ExecutionContext<S> source);

  /**
   * Returns the command associated with this result.
   *
   * @return the command
   */
  public C command();

  /**
   * Returns the arguments associated with this result.
   *
   * @return the arguments
   */
  public String[] args();

  /**
   * Returns the number of arguments stored in this result.
   *
   * @return the argument count
   */
  public int argumentCount();

  /**
   * Replaces the argument at the specified index.
   *
   * @param idx   the argument index to modify
   * @param value the new argument value
   */
  public void modifyArguments(int idx, String value);
}
