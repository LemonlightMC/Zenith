package com.lemonlightmc.zenith.commands.executors;

import com.lemonlightmc.zenith.commands.CommandRequirement;
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.SimpleSubCommand;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.exceptions.DuplicateArgumentException;
import com.lemonlightmc.zenith.commands.exceptions.GreedyArgumentException;
import com.lemonlightmc.zenith.commands.exceptions.OptionalArgumentException;
import com.lemonlightmc.zenith.commands.executors.Executors.ExecutorType;
import com.lemonlightmc.zenith.commands.executors.Executors.NormalExecutor;
import com.lemonlightmc.zenith.commands.suggestions.SuggestionInfo;
import com.lemonlightmc.zenith.commands.suggestions.Suggestions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public abstract class AbstractCommand<T extends AbstractCommand<T, S>, S> extends Executable<T, S> {

  protected List<Argument<?, ?, S>> arguments = new ArrayList<>();
  protected List<SimpleSubCommand<S>> subcommands = new ArrayList<>();
  protected Set<String> aliases = new HashSet<String>();

  protected List<CommandRequirement<CommandSource<S>>> requirements;
  protected String shortDescription;
  protected String fullDescription;
  private boolean hasOptional = false;

  @Override
  public abstract T getInstance();

  // Aliases

  public T withAliases(final Collection<String> aliases) {
    if (aliases != null && !aliases.isEmpty()) {
      for (final String alias : aliases) {
        this.aliases.add(alias.toLowerCase());
      }
    }
    return getInstance();
  }

  public T withAliases(final String... aliases) {
    if (aliases != null && aliases.length != 0) {
      for (final String alias : aliases) {
        this.aliases.add(alias.toLowerCase());
      }
    }
    return getInstance();
  }

  public T setAliases(final Set<String> aliases) {
    if (aliases != null && !aliases.isEmpty()) {
      this.aliases = aliases;
    }
    return getInstance();
  }

  public Set<String> getAliases() {
    return this.aliases;
  }

  public boolean hasAlias(final String alias) {
    return alias == null || alias.length() == 0 ? false : this.aliases.contains(alias.toLowerCase());
  }

  public T removeAlias(final String... aliases) {
    if (aliases != null && aliases.length != 0) {
      for (final String alias : aliases) {
        this.aliases.remove(alias.toLowerCase());
      }
    }
    return getInstance();
  }

  public T clearAliases() {
    this.aliases.clear();
    return getInstance();
  }

  // Arguments
  public T withArguments(final Argument<?, ?, S> arg, final Boolean optional) {
    if (arg == null) {
      return getInstance();
    }
    if (!arguments.isEmpty() && arguments.get(arguments.size() - 1).getType().isGreedy()) {
      throw new GreedyArgumentException(arg.getName(), GreedyArgumentException.buildArgsStr(arguments));
    }
    for (final Argument<?, ?, S> tempArg : arguments) {
      if (tempArg.getName().equals(arg.getName())) {
        throw new DuplicateArgumentException(arg.getName());
      }
    }
    if (optional != null) {
      arg.setOptional(optional);
    }

    final boolean seenOptional = arguments.stream().anyMatch(Argument::isOptional);
    if (arg.isOptional()) {
      hasOptional = true;
    } else if (seenOptional) {
      final Argument<?, ?, S> lastOptional = arguments.stream().filter(Argument::isOptional).reduce((a, b) -> b).orElse(null);
      if (lastOptional != null) {
        throw new OptionalArgumentException(arg.getName(), lastOptional.getName());
      }
      throw new OptionalArgumentException(arg.getName());
    } else {
      hasOptional = seenOptional;
    }

    arguments.add(arg);
    return getInstance();
  }

  @SafeVarargs
  public final T withArguments(final Argument<?, ?, S>... args) {
    if (args == null || args.length == 0) {
      return getInstance();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, null);
    }
    return getInstance();
  }

  public T withArguments(final Collection<Argument<?, ?, S>> args) {
    if (args == null || args.isEmpty()) {
      return getInstance();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, null);
    }
    return getInstance();
  }

  public T withArguments(final Collection<Argument<?, ?, S>> args, final boolean optional) {
    if (args == null || args.isEmpty()) {
      return getInstance();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, optional);
    }
    return getInstance();
  }

  public T withArguments(final Argument<?, ?, S>[] args, final boolean optional) {
    if (args == null || args.length == 0) {
      return getInstance();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, optional);
    }
    return getInstance();
  }

  public T withOptionalArguments(final Collection<Argument<?, ?, S>> args) {
    return withArguments(args, true);
  }

  @SafeVarargs
  public final T withOptionalArguments(final Argument<?, ?, S>... args) {
    return withArguments(args, true);
  }

  public T setArguments(final List<Argument<?, ?, S>> args) {
    arguments.clear();
    hasOptional = false;
    if (args != null && !args.isEmpty()) {
      for (final Argument<?, ?, S> arg : args) {
        withArguments(arg, null);
      }
    }
    return getInstance();
  }

  @SuppressWarnings("unchecked")
  public boolean hasArguments(final Argument<?, ?, S>... args) {
    return args != null && args.length != 0 && arguments.containsAll(List.of(args));
  }

  public boolean hasArguments() {
    return !arguments.isEmpty();
  }

  public List<Argument<?, ?, S>> getArguments() {
    return arguments;
  }

  public List<Argument<?, ?, S>> getOptionalArguments() {
    final List<Argument<?, ?, S>> list = new ArrayList<>();
    for (final Argument<?, ?, S> argument : arguments) {
      if (argument.isOptional()) {
        list.add(argument);
      }
    }
    return list;
  }

  private void recomputeHasOptional() {
    hasOptional = arguments.stream().anyMatch(Argument::isOptional);
  }

  public T removeArguments(final Collection<Argument<?, ?, S>> args) {
    if (args != null && !args.isEmpty()) {
      arguments.removeAll(args);
      recomputeHasOptional();
    }
    return getInstance();
  }

  @SafeVarargs
  public final T removeArguments(final Argument<?, ?, S>... args) {
    if (args != null && args.length != 0) {
      arguments.removeAll(List.of(args));
      recomputeHasOptional();
    }
    return getInstance();
  }

  public T clearArguments() {
    arguments.clear();
    hasOptional = false;
    return getInstance();
  }

  // Subcommands
  public T withSubcommands(final Collection<SimpleSubCommand<S>> subs) {
    if (subs != null && !subs.isEmpty()) {
      subcommands.addAll(subs);
    }
    return getInstance();
  }

  @SafeVarargs
  public final T withSubcommands(final SimpleSubCommand<S>... subs) {
    if (subs != null && subs.length != 0) {
      subcommands.addAll(List.of(subs));
    }
    return getInstance();
  }

  public T setSubcommands(final List<SimpleSubCommand<S>> subs) {
    subcommands = subs;
    return getInstance();
  }

  public boolean hasSubcommands(final Collection<T> subs) {
    return subs != null && !subs.isEmpty() && subcommands.containsAll(subs);
  }

  public boolean hasSubcommands() {
    return !subcommands.isEmpty();
  }

  public boolean isSubcommand(final String sub) {
    if (sub == null || sub.length() == 0) {
      return false;
    }
    final String key = sub.toLowerCase();
    return subcommands.stream().anyMatch((s) -> s.getAliases().contains(key) || s.getAliases().contains(sub));
  }

  public SimpleSubCommand<S> getSubcommand(final String sub) {
    if (sub == null || sub.length() == 0 || subcommands.isEmpty()) {
      return null;
    }
    final String key = sub.toLowerCase();
    return subcommands.stream()
        .filter((s) -> s.getAliases().contains(key) || s.getAliases().contains(sub))
        .findFirst()
        .orElse(null);
  }

  public List<SimpleSubCommand<S>> getSubcommands() {
    return subcommands;
  }

  @SafeVarargs
  public final T removeSubcommands(final SimpleSubCommand<S>... subs) {
    if (subs != null && subs.length > 0) {
      subcommands.removeAll(List.of(subs));
    }
    return getInstance();
  }

  public T removeSubcommands(final Collection<SimpleSubCommand<S>> subs) {
    if (subs != null && !subs.isEmpty()) {
      subcommands.removeAll(subs);
    }
    return getInstance();
  }

  public T clearSubcommands() {
    subcommands.clear();
    return getInstance();
  }

  // requirements
  public T withRequirement(final CommandRequirement<CommandSource<S>> requirement) {
    if (requirement == null) {
      return getInstance();
    }
    if (requirements == null) {
      requirements = new ArrayList<>();
    }
    requirements.add(requirement);
    return getInstance();
  }

  @SafeVarargs
  public final T withRequirement(final CommandRequirement<CommandSource<S>>... requirements) {
    if (requirements != null && requirements.length != 0) {
      for (final CommandRequirement<CommandSource<S>> requirement : requirements) {
        withRequirement(requirement);
      }
    }
    return getInstance();
  }

  public T withRequirement(final Collection<CommandRequirement<CommandSource<S>>> requirements) {
    if (requirements != null && !requirements.isEmpty()) {
      for (final CommandRequirement<CommandSource<S>> requirement : requirements) {
        withRequirement(requirement);
      }
    }
    return getInstance();
  }

  public T withRequirement(final Predicate<CommandSource<S>> requirement) {
    return withRequirement(requirement == null ? null : CommandRequirement.from(requirement));
  }

  public T withPermissions(final String... permissions) {
    if (permissions != null && permissions.length != 0) {
      for (final String perm : permissions) {
        withRequirement(CommandRequirement.permission(perm));
      }
    }
    return getInstance();
  }

  public T withPermissions(final Collection<String> permissions) {
    if (permissions != null && !permissions.isEmpty()) {
      for (final String perm : permissions) {
        withRequirement(CommandRequirement.permission(perm));
      }
    }
    return getInstance();
  }

  public T setRequirements(final List<CommandRequirement<CommandSource<S>>> requirements) {
    if (requirements != null && !requirements.isEmpty()) {
      this.requirements = requirements;
    }
    return getInstance();
  }

  public boolean hasRequirements() {
    return requirements != null && !requirements.isEmpty();
  }

  public T clearRequirements() {
    if (requirements != null) {
      requirements.clear();
      requirements = null;
    }
    return getInstance();
  }

  public List<CommandRequirement<CommandSource<S>>> getRequirements() {
    return requirements;
  }

  public boolean checkRequirements(final CommandSource<S> source) {
    if (requirements == null || requirements.isEmpty()) {
      return true;
    }
    for (final CommandRequirement<CommandSource<S>> requirement : requirements) {
      if (!requirement.test(source)) {
        return false;
      }
    }
    return true;
  }

  // other
  public String getShortDescription() {
    return shortDescription;
  }

  public T withShortDescription(final String desc) {
    this.shortDescription = desc;
    return getInstance();
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public T withFullDescription(final String desc) {
    this.fullDescription = desc;
    return getInstance();
  }

  protected boolean hasAnyExecutors() {
    if (hasExecutors()) {
      return true;
    }
    for (final SimpleSubCommand<S> sub : subcommands) {
      if (sub.hasAnyExecutors()) {
        return true;
      }
    }
    return false;
  }

  // execution
  public void run(final ExecutionInfo<S> info, final int idx) throws CommandException {
    final SimpleSubCommand<S> sub = getSubcommand(info.args().getRaw(idx));
    if (sub != null) {
      sub.run(info, idx + 1);
      return;
    }
    if (executors == null || executors.isEmpty()) {
      return;
    }
    NormalExecutor<?>[] ex = getExecutors(info.source().executorType());
    if (ex == null) {
      ex = getExecutors(ExecutorType.NATIVE);
    }
    if (ex == null) {
      ex = getExecutors(ExecutorType.ALL);
    }
    if (ex == null) {
      return;
    }
    for (final NormalExecutor<?> normalExecutor : ex) {
      normalExecutor.executeWith(info);
    }
  }

  public List<Suggestions<S>> tabComplete(final SuggestionInfo<S> info, int idx) {
    if (arguments.isEmpty() && subcommands.isEmpty()) {
      return null;
    }

    if (idx >= 0) {
      if (idx < info.args().count()) {
        final String subStr = info.args().getRaw(idx);
        final SimpleSubCommand<S> sub = getSubcommand(subStr);
        if (sub != null) {
          return sub.tabComplete(info, idx + 1);
        }
      }
      if (!subcommands.isEmpty() && idx == 0) {
        final List<String> list = new ArrayList<>();
        for (final SimpleSubCommand<S> sub : subcommands) {
          list.addAll(sub.getAliases());
        }
        if (!list.isEmpty()) {
          return List.of(Suggestions.from(list));
        }
      }
      if (arguments.isEmpty()) {
        return null;
      }
      idx = -(info.args().count() - idx);
    }

    final int argIndex = Math.abs(idx + 1);
    if (argIndex < 0 || argIndex >= arguments.size()) {
      return null;
    }
    final Argument<?, ?, S> arg = arguments.get(argIndex);
    return arg == null ? null : arg.getSuggestions();
  }

  @Override
  public String toString() {
    return "AbstractCommand [arguments=" + arguments + ", subcommands=" + subcommands + ", aliases=" + aliases
        + ", executors=" + executors + ", requirements=" + requirements + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), arguments, subcommands, aliases, requirements, shortDescription, fullDescription,
        hasOptional);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    final AbstractCommand<?, ?> other = (AbstractCommand<?, ?>) obj;
    return hasOptional == other.hasOptional
        && Objects.equals(arguments, other.arguments)
        && Objects.equals(subcommands, other.subcommands)
        && Objects.equals(aliases, other.aliases)
        && Objects.equals(requirements, other.requirements)
        && Objects.equals(shortDescription, other.shortDescription)
        && Objects.equals(fullDescription, other.fullDescription);
  }
}
