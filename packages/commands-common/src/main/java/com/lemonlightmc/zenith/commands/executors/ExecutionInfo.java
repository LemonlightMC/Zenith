package com.lemonlightmc.zenith.commands.executors;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;

public record ExecutionInfo<S>(CommandSource<S> source,
    CommandArguments args) {

  public S sender() {
    return source.sender();
  }

  public <T extends S> ExecutionInfo<T> copyFor(final T newSender) {
    return new ExecutionInfo<>(source.copyFor(newSender), args);
  }

  public ExecutionInfo<S> copy() {
    return new ExecutionInfo<>(source, args);
  }

}