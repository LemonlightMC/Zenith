package com.lemonlightmc.zenith.commands.executors;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.actions.ConditionContext;

public record ExecutionInfo<S extends CommandSender>(ConditionContext<S> source,
    CommandArguments args) {

  public S sender() {
    return source.sender();
  }

  public <T extends S> ExecutionInfo<T> copyFor(final T newSender) {
    return new ExecutionInfo<T>(source.copyFor(newSender), args);
  }

  public ExecutionInfo<S> copy() {
    return new ExecutionInfo<>(source, args);
  }

}