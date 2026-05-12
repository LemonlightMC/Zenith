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
    if (arguments.getLast().getType().isGreedy()) {
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
    if (arg.isOptional()) {
      hasOptional = true;
    } else if (!hasOptional) {
      throw new OptionalArgumentException(arg.getName(), arguments.getLast().getName());
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
    arguments = args;
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
    final List<Argument<?, ?, S>> list = List.copyOf(arguments);
    list.removeIf(a -> !a.isOptional());
    return list;
  }

  public T removeArguments(final Collection<Argument<?, ?, S>> args) {
    if (args != null && !args.isEmpty()) {
      arguments.removeAll(args);
    }
    return getInstance();
  }

  @SafeVarargs
  public final T removeArguments(final Argument<?, ?, S>... args) {
    if (args != null && args.length != 0) {
      arguments.removeAll(List.of(args));
    }
    return getInstance();
  }

  public T clearArguments() {
    arguments.clear();
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
    return sub != null && sub.length() != 0
        && subcommands.stream().anyMatch((s) -> s.getAliases().contains(sub));
  }

  public SimpleSubCommand<S> getSubcommand(final String sub) {
    return sub == null || sub.length() == 0 || subcommands.isEmpty() ? null
        : subcommands.stream().filter((s) -> s.getAliases().contains(sub)).findFirst().orElse(null);
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
    if (arguments.isEmpty()) {
      if (subcommands.isEmpty()) {
        return null;
      }
      final List<String> list = new ArrayList<>();
      for (final SimpleSubCommand<S> sub : subcommands) {
        for (final String string : sub.aliases) {
          list.add(string);
        }
      }
      return List.of(Suggestions.from(list));
    }
    if (idx >= 0) {
      final String subStr = info.args().getRaw(idx);
      final SimpleSubCommand<S> sub = getSubcommand(subStr);
      if (sub != null) {
        return sub.tabComplete(info, idx + 1);
      }
      idx = 0 - (info.args().count() - idx);
    }
    if (idx == 0) {
      return null;
    }
    final Argument<?, ?, S> arg = arguments.get(Math.abs(idx + 1));
    return arg == null ? null : arg.getSuggestions();
  }

  @Override
  public String toString() {
    return "AbstractCommand [arguments=" + arguments + ", subcommands=" + subcommands + ", aliases=" + aliases
        + ", executors=" + executors + ", requirements=" + requirements + "]";
  }

  @Override
  public int hashCode() {
    int result = 31 * super.hashCode() + ((arguments == null) ? 0 : arguments.hashCode());
    result = 31 * result + ((subcommands == null) ? 0 : subcommands.hashCode());
    result = 31 * result + ((aliases == null) ? 0 : aliases.hashCode());
    result = 31 * result + ((requirements == null) ? 0 : requirements.hashCode());
    result = 31 * result + ((shortDescription == null) ? 0 : shortDescription.hashCode());
    result = 31 * result + ((fullDescription == null) ? 0 : fullDescription.hashCode());
    return 31 * result + (hasOptional ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    final AbstractCommand<?, ?> other = (AbstractCommand<?, ?>) obj;
    if (arguments == null && other.arguments != null || subcommands == null && other.subcommands != null
        || aliases == null && other.aliases != null || requirements == null && other.requirements != null
        || fullDescription == null && other.fullDescription != null
        || shortDescription == null && other.shortDescription != null) {
      return false;
    }
    return hasOptional == other.hasOptional && fullDescription.equals(other.fullDescription)
        && shortDescription.equals(other.shortDescription)
        && arguments.equals(other.arguments) && subcommands.equals(other.subcommands) && aliases.equals(other.aliases)
        && requirements.equals(other.requirements);
  }
}
