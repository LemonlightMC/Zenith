package com.lemonlightmc.zenith.commands;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.executors.AbstractCommand;

public class SimpleSubCommand<S extends CommandSender> extends AbstractCommand<SimpleSubCommand<S>, S> {

  public SimpleSubCommand(final String... aliases) {
    super();
    withAliases(aliases);
  }

  public static <T extends CommandSender> SimpleSubCommand<T> create(final String... aliases) {
    return new SimpleSubCommand<T>(aliases);
  }

  @Override
  public SimpleSubCommand<S> self() {
    return this;
  }

}
