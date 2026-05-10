package com.lemonlightmc.zenith.commands;

import com.lemonlightmc.zenith.commands.executors.AbstractCommand;

public class SimpleSubCommand<S> extends AbstractCommand<SimpleSubCommand<S>, S> {

  public SimpleSubCommand(final String... aliases) {
    super();
    withAliases(aliases);
  }

  public static <T> SimpleSubCommand<T> create(final String... aliases) {
    return new SimpleSubCommand<T>(aliases);
  }

  public SimpleSubCommand<S> getInstance() {
    return this;
  }

}
