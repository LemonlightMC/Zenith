package com.lemonlightmc.zenith.commands.argumentsbase;

import com.lemonlightmc.zenith.commands.CommandSource;

public interface CommandResult<C, S> {
  public boolean execute(final S sender);

  public boolean execute(final CommandSource<S> source);

  public C command();

  public String[] args();

  public int getArgumentCount();

  public void modifyArguments(int idx, String value);

  @Override
  public boolean equals(final Object o);

  @Override
  public int hashCode();

  @Override
  public String toString();
}
