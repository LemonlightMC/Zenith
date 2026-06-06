package com.lemonlightmc.zenith.commands.argumentsbase;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class CommandArguments {
  public ParsedArgument[] args;
  public Map<String, ParsedArgument> argsMap;
  public String fullInput;

  public CommandArguments(
      final Map<String, ParsedArgument> argsMap, final String fullInput) {
    this.args = argsMap.values().toArray(ParsedArgument[]::new);
    this.argsMap = argsMap;
    this.fullInput = fullInput;
  }

  public String getFullInput() {
    return fullInput;
  }

  public int count() {
    return args.length;
  }

  public Map<String, ParsedArgument> map() {
    return Collections.unmodifiableMap(argsMap);
  }

  public Set<String> keys() {
    return Collections.unmodifiableSet(argsMap.keySet());
  }

  public Collection<ParsedArgument> values() {
    return Collections.unmodifiableCollection(argsMap.values());
  }

  // normal
  public Object get(final int index) {
    if (args.length <= index) {
      return null;
    } else {
      return args[index] == null ? null : args[index].value();
    }
  }

  public Object get(final String nodeName) {
    return argsMap.get(nodeName);
  }

  public Object getLast() {
    return args.length == 0 ? null : get(args.length - 1);
  }

  public Optional<Object> getOptional(final int index) {
    return Optional.ofNullable(get(index));
  }

  public Optional<Object> getOptional(final String nodeName) {
    return Optional.ofNullable(get(nodeName));
  }

  public Optional<Object> getLastOptional() {
    return Optional.ofNullable(getLast());
  }

  public Object getOrThrow(final int index) {
    return Objects.requireNonNull(get(index));
  }

  public Object getOrThrow(final String nodeName) {
    return Objects.requireNonNull(get(nodeName));
  }

  public Object getLastOrThrow() {
    return Objects.requireNonNull(getLast());
  }

  public Object getOrDefault(final int index, final Object defaultValue) {
    if (args.length <= index) {
      return defaultValue;
    } else {
      return args[index] == null ? null : args[index].value();
    }
  }

  public Object getOrDefault(final String nodeName, final Object defaultValue) {
    final ParsedArgument v = argsMap.get(nodeName);
    return v == null ? defaultValue : v.value();
  }

  public Object getOrDefault(final int index, final Supplier<?> defaultValue) {
    if (args.length <= index) {
      return defaultValue.get();
    } else {
      return args[index] == null ? null : args[index].value();
    }
  }

  public Object getOrDefault(final String nodeName, final Supplier<?> defaultValue) {
    final ParsedArgument v = argsMap.get(nodeName);
    return v == null ? defaultValue.get() : v.value();
  }

  public Object getLastOrDefault(final Object defaultValue) {
    final Object obj = getLast();
    return obj == null ? defaultValue : obj;
  }

  public Object getLastOrDefault(final Supplier<?> defaultValue) {
    final Object obj = getLast();
    return obj == null ? defaultValue.get() : obj;
  }

  // raw
  public String getRaw(final int index) {
    if (args.length <= index) {
      return null;
    } else {
      return args[index] == null ? null : args[index].raw();
    }
  }

  public String getRaw(final String nodeName) {
    final ParsedArgument v = argsMap.get(nodeName);
    return v == null ? null : v.raw();
  }

  public String getLastRaw() {
    return args.length == 0 ? null : getRaw(args.length - 1);
  }

  public String getRawOrThrow(final int index) {
    return Objects.requireNonNull(getRaw(index));
  }

  public String getRawOrThrow(final String nodeName) {
    return Objects.requireNonNull(getRaw(nodeName));
  }

  public String getRawLastOrThrow() {
    return Objects.requireNonNull(getLastRaw());
  }

  public Optional<String> getRawOptional(final int index) {
    return Optional.ofNullable(getRaw(index));
  }

  public Optional<String> getRawOptional(final String nodeName) {
    return Optional.ofNullable(getRaw(nodeName));
  }

  public Optional<String> getRawLastOptional() {
    return Optional.ofNullable(getLastRaw());
  }

  public String getOrDefaultRaw(final int index, final String defaultValue) {
    if (args.length <= index) {
      return defaultValue;
    } else {
      return args[index] == null ? null : args[index].raw();
    }
  }

  public String getOrDefaultRaw(final String nodeName, final String defaultValue) {
    final ParsedArgument v = argsMap.get(nodeName);
    return v == null ? defaultValue : v.raw();
  }

  public String getOrDefaultRaw(final int index, final Supplier<String> defaultValue) {
    if (args.length <= index) {
      return defaultValue.get();
    } else {
      return args[index] == null ? null : args[index].raw();
    }
  }

  public String getOrDefaultRaw(
      final String nodeName,
      final Supplier<String> defaultValue) {
    final ParsedArgument v = argsMap.get(nodeName);
    return v == null ? defaultValue.get() : v.raw();
  }

  public String getRawLastOrDefault(final String defaultValue) {
    final String obj = getLastRaw();
    return obj == null ? defaultValue : obj;
  }

  public String getRawLastOrDefault(final Supplier<String> defaultValue) {
    final String obj = getLastRaw();
    return obj == null ? defaultValue.get() : obj;
  }

  // unchecked
  @SuppressWarnings("unchecked")
  public <T> T getUnchecked(final int index) {
    return (T) get(index);
  }

  @SuppressWarnings("unchecked")
  public <T> T getUnchecked(final String nodeName) {
    return (T) get(nodeName);
  }

  @SuppressWarnings("unchecked")
  public <T> T getUncheckedLast() {
    return args.length == 0 ? null : (T) get(args.length - 1);
  }

  public <T> T getUncheckedLastOrThrow() {
    return Objects.requireNonNull(getUncheckedLast());
  }

  public <T> T getUncheckedOrThrow(final int index) {
    return Objects.requireNonNull(getUnchecked(index));
  }

  public <T> T getUncheckedOrThrow(final String nodeName) {
    return Objects.requireNonNull(getUnchecked(nodeName));
  }

  public <T> Optional<T> getUncheckedOptional(final int index) {
    return Optional.ofNullable(getUnchecked(index));
  }

  public <T> Optional<T> getUncheckedOptional(final String nodeName) {
    return Optional.ofNullable(getUnchecked(nodeName));
  }

  public <T> Optional<T> getUncheckedLastOptional() {
    return Optional.ofNullable(getUncheckedLast());
  }

  @SuppressWarnings("unchecked")
  public <T> T getUncheckedOrDefault(final int index, final T defaultValue) {
    return (T) getOrDefault(index, defaultValue);
  }

  @SuppressWarnings("unchecked")
  public <T> T getUncheckedOrDefault(final String nodeName, final T defaultValue) {
    return (T) getOrDefault(nodeName, defaultValue);
  }

  @SuppressWarnings("unchecked")
  public <T> T getUncheckedOrDefault(final int index, final Supplier<T> defaultValue) {
    return (T) getOrDefault(index, defaultValue);
  }

  @SuppressWarnings("unchecked")
  public <T> T getUncheckedOrDefault(
      final String nodeName,
      final Supplier<T> defaultValue) {
    return (T) getOrDefault(nodeName, defaultValue);
  }

  public <T> T getUncheckedLastOrDefault(final T defaultValue) {
    final T obj = getUncheckedLast();
    return obj == null ? defaultValue : obj;
  }

  public <T> T getUncheckedLastOrDefault(final Supplier<T> defaultValue) {
    final T obj = getUncheckedLast();
    return obj == null ? defaultValue.get() : obj;
  }

  // byArgument
  public <T> T getByArgument(final Argument<T, ?, ?> argumentType) {
    return castArgument(
        get(argumentType.getName()),
        argumentType.getPrimitiveType(),
        argumentType.getName());
  }

  public <T> Optional<T> getByArgumentOptional(final Argument<T, ?, ?> argumentType) {
    return Optional.ofNullable(getByArgument(argumentType));
  }

  public <T> T getByArgumentOrDefault(
      final Argument<T, ?, ?> argumentType,
      final T defaultValue) {
    final T argument = getByArgument(argumentType);
    return argument == null ? defaultValue : argument;
  }

  public <T> T getByClass(final String nodeName, final Class<T> argumentType) {
    return castArgument(get(nodeName), argumentType, nodeName);
  }

  public <T> T getByClass(final int index, final Class<T> argumentType) {
    return castArgument(get(index), argumentType, index);
  }

  public <T> Optional<T> getByClassOptional(final String nodeName, final Class<T> argumentType) {
    return Optional.ofNullable(getByClass(nodeName, argumentType));
  }

  public <T> Optional<T> getByClassOptional(final int index, final Class<T> argumentType) {
    return Optional.ofNullable(getByClass(index, argumentType));
  }

  public <T> T getByClassOrThrow(final String nodeName, final Class<T> argumentType) {
    return Objects.requireNonNull(getByClass(nodeName, argumentType));
  }

  public <T> T getByClassOrThrow(final int index, final Class<T> argumentType) {
    return Objects.requireNonNull(getByClass(index, argumentType));
  }

  public <T> T getByClassOrDefault(
      final String nodeName,
      final Class<T> argumentType,
      final T defaultValue) {
    final T argument = getByClass(nodeName, argumentType);
    return (argument != null) ? argument : defaultValue;
  }

  public <T> T getByClassOrDefault(
      final int index,
      final Class<T> argumentType,
      final T defaultValue) {
    final T argument = getByClass(index, argumentType);
    return argument == null ? defaultValue : argument;
  }

  @Override
  public int hashCode() {
    int result = 31 + Arrays.deepHashCode(args);
    result = 31 * result + ((argsMap == null) ? 0 : argsMap.hashCode());
    result = 31 * result + ((fullInput == null) ? 0 : fullInput.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CommandArguments other = (CommandArguments) obj;
    if (argsMap == null && other.argsMap != null || fullInput == null && other.fullInput != null) {
      return false;
    }
    return argsMap.equals(other.argsMap) && fullInput.equals(other.fullInput) && equalsArgs(args, other.args);
  }

  @Override
  public String toString() {
    return "CommandArguments [args=" + Arrays.toString(args) + ",fullInput=" + fullInput + "]";
  }

  private static boolean equalsArgs(
      final ParsedArgument[] a1,
      final ParsedArgument[] a2) {
    if (a1 == a2)
      return true;
    if (a1 == null || a2 == null)
      return false;
    final int length = a1.length;
    if (a2.length != length)
      return false;

    for (int i = 0; i < length; i++) {
      final ParsedArgument e1 = a1[i];
      final ParsedArgument e2 = a2[i];

      if (e1 == e2)
        continue;
      if (e1 == null)
        return false;
      if (!e1.equals(e2))
        return false;
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  private static <T> T castArgument(
      final Object argument,
      final Class<T> argType,
      final String argName) {
    if (argument == null) {
      return null;
    }
    try {
      return (T) argument;
    } catch (final Exception e) {
      throw new IllegalArgumentException("Argument '" +
          argName +
          "' is defined as " +
          argument.getClass().getSimpleName() +
          ", not " +
          argType.getSimpleName());
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T castArgument(
      final Object argument,
      final Class<T> argType,
      final int argIndex) {
    if (argument == null) {
      return null;
    }
    try {
      return (T) argument;
    } catch (final Exception e) {
      throw new IllegalArgumentException("Argument at index '" +
          argIndex +
          "' is defined as " +
          argument.getClass().getSimpleName() +
          ", not " +
          argType.getSimpleName());
    }
  }
}