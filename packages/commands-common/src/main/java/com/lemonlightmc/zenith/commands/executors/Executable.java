package com.lemonlightmc.zenith.commands.executors;

import java.lang.reflect.Array;
import java.util.EnumMap;

import org.bukkit.command.CommandSender;
import com.lemonlightmc.zenith.commands.executors.Executors.*;

public abstract class Executable<T, S extends CommandSender> {
  protected EnumMap<ExecutorType, NormalExecutor<? extends S>[]> executors;

  public Executable() {
    executors = new EnumMap<>(ExecutorType.class);
  }

  protected abstract T self();

  public boolean hasExecutors() {
    return !executors.isEmpty();
  }

  public NormalExecutor<? extends S>[] executors(final ExecutorType type) {
    return executors.get(type);
  }

  public EnumMap<ExecutorType, NormalExecutor<? extends S>[]> executors() {
    return executors;
  }

  public void clearExecutors() {
    if (executors != null) {
      executors.clear();
    }
  }

  @SuppressWarnings("unchecked")
  protected void addExecutor(final ExecutorType type, final NormalExecutor<? extends S> executor) {
    final NormalExecutor<? extends S>[] ex = executors.get(type);
    if (ex == null || ex.length == 0) {
      executors.put(type, (NormalExecutor<S>[]) new NormalExecutor[] { executor });
    } else {
      final NormalExecutor<? extends S>[] newEx = (NormalExecutor<? extends S>[]) Array.newInstance(
          ex.getClass().getComponentType(),
          ex.length + 1);
      System.arraycopy(ex, 0, newEx, 0, ex.length);
      newEx[ex.length] = executor;
      executors.put(type, newEx);
    }
  }

  public <E extends S> T executes(final CommandExecutor<E> executor, final ExecutorType... types) {
    if (types == null || types.length == 0) {
      addExecutor(ExecutorType.ALL, executor);
    } else {
      for (final ExecutorType type : types) {
        addExecutor(type, executor);
      }
    }
    return self();
  }

  @Override
  public int hashCode() {
    return 31 + ((executors == null) ? 0 : executors.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Executable<?, ?> other = (Executable<?, ?>) obj;
    if (executors == null && other.executors != null) {
      return false;
    }
    return executors.equals(other.executors);
  }

  @Override
  public String toString() {
    return "Executable [executors=" + executors + "]";
  }
}
