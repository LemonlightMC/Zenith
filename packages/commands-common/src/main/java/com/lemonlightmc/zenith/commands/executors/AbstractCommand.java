package com.lemonlightmc.zenith.commands.executors;

import com.lemonlightmc.zenith.additive.StringUtils;
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
import com.lemonlightmc.zenith.actions.Condition;
import com.lemonlightmc.zenith.actions.ConditionContext;
import com.lemonlightmc.zenith.actions.Conditions;
import com.lemonlightmc.zenith.actions.Condition.ConditionSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.bukkit.command.CommandSender;

public abstract class AbstractCommand<T extends AbstractCommand<T, S>, S extends CommandSender>
    extends Executable<T, S> {

  // List instead of Set due n being small and dublicate check is
  // in the Command Building Phase
  protected List<String> aliases = new ArrayList<>();
  protected List<Argument<?, ?, S>> arguments = new ArrayList<>();
  // per alias mapping - keep initalialized to null when empty
  protected Map<String, SimpleSubCommand<S>> subcommands = null;
  // keep initalialized to null when empty
  protected ConditionSet<S> requirements = null;

  protected String shortDescription;
  protected String fullDescription;
  private boolean hasOptional = false;

  @Override
  public abstract T self();

  // ============ Usage ============
  public String getShortDescription() {
    return shortDescription;
  }

  public T withShortDescription(final String desc) {
    this.shortDescription = desc;
    return self();
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public T withFullDescription(final String desc) {
    this.fullDescription = desc;
    return self();
  }

  // ============ Aliases ============
  public T withAliases(String alias) {
    alias = StringUtils.prepareLowercase(alias);
    if (alias == null || aliases.contains(alias)) {
      return self();
    }
    aliases.add(alias);
    return self();
  }

  public T withAliases(final Collection<String> aliases) {
    if (aliases == null || aliases.isEmpty()) {
      return self();
    }
    for (final String alias : aliases) {
      withAliases(alias);
    }
    return self();
  }

  public T withAliases(final String... aliases) {
    if (aliases == null || aliases.length == 0) {
      return self();
    }
    for (final String alias : aliases) {
      withAliases(alias);
    }
    return self();
  }

  public T removeAliases(String alias) {
    if (aliases.isEmpty()) {
      return self();
    }
    alias = StringUtils.lowercase(alias);
    if (alias != null) {
      this.aliases.remove(alias);
    }
    return self();
  }

  public T removeAliases(final Collection<String> aliases) {
    if (aliases == null || aliases.isEmpty() || aliases.isEmpty()) {
      return self();
    }
    for (String alias : aliases) {
      alias = StringUtils.lowercase(alias);
      if (alias != null) {
        this.aliases.remove(alias);
      }
    }
    return self();
  }

  public T removeAliases(final String... aliases) {
    if (aliases == null || aliases.length == 0 || this.aliases.isEmpty()) {
      return self();
    }
    for (String alias : aliases) {
      alias = StringUtils.lowercase(alias);
      if (alias != null) {
        this.aliases.remove(alias);
      }
    }
    return self();
  }

  public T clearAliases() {
    if (aliases != null) {
      this.aliases.clear();
    }
    return self();
  }

  public List<String> aliases() {
    return this.aliases;
  }

  public boolean hasAlias(final String alias) {
    return alias == null || alias.isEmpty() || aliases.isEmpty() ? false
        : this.aliases.contains(StringUtils.lowercase(alias));
  }

  public boolean hasAliases() {
    return !aliases.isEmpty();
  }

  // ============ Requirements ============
  public T withRequirements(final Condition<S> condition) {
    requirements().add(condition);
    return self();
  }

  @SafeVarargs
  public final T withRequirements(final Condition<S>... conditions) {
    requirements().addAll(conditions);
    return self();
  }

  public T withRequirements(final Collection<Condition<S>> conditions) {
    requirements().addAll(conditions);
    return self();
  }

  public T withRequirements(final Predicate<ConditionContext<S>> predicate) {
    requirements().add(Condition.of(predicate));
    return self();
  }

  public T withPermissions(String permission) {
    permission = StringUtils.prepare(permission);
    if (permission == null) {
      return self();
    }
    requirements().add(Conditions.PermissionConditions.allowed(permission));
    return self();
  }

  public T withPermissions(final String... permissions) {
    if (permissions == null || permissions.length == 0) {
      return self();
    }
    for (String perm : permissions) {
      perm = StringUtils.prepare(perm);
      if (perm != null) {
        requirements().add(Conditions.PermissionConditions.allowed(perm));
      }
    }
    return self();
  }

  public T withPermissions(final Collection<String> permissions) {
    if (permissions == null || permissions.isEmpty()) {
      return self();
    }
    for (String perm : permissions) {
      perm = StringUtils.prepare(perm);
      if (perm != null) {
        requirements().add(Conditions.PermissionConditions.allowed(perm));
      }
    }
    return self();
  }

  public T setRequirements(final List<Condition<S>> requirements) {
    if (requirements == null) {
      return self();
    }
    if (requirements.isEmpty()) {
      this.requirements = null;
      return self();
    }
    requirements().addAll(requirements);
    return self();
  }

  public boolean hasRequirements() {
    return requirements != null && !requirements.isEmpty();
  }

  public T clearRequirements() {
    if (requirements != null) {
      requirements.clear();
    }
    return self();
  }

  public ConditionSet<S> requirements() {
    if (requirements == null) {
      requirements = new ConditionSet<S>();
    }
    return requirements;
  }

  public boolean checkRequirements(final ConditionContext<S> source) {
    if (requirements == null) {
      return true;
    }
    return requirements.test(source);
  }

  // ============ Arguments ============
  public T withArguments(final Argument<?, ?, S> arg, final Boolean optional) {
    if (arg == null) {
      return self();
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
      final Argument<?, ?, S> lastOptional = arguments.stream().filter(Argument::isOptional).reduce((a, b) -> b)
          .orElse(null);
      if (lastOptional != null) {
        throw new OptionalArgumentException(arg.getName(), lastOptional.getName());
      }
      throw new OptionalArgumentException(arg.getName());
    } else {
      hasOptional = seenOptional;
    }

    arguments.add(arg);
    return self();
  }

  @SafeVarargs
  public final T withArguments(final Argument<?, ?, S>... args) {
    if (args == null || args.length == 0) {
      return self();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, null);
    }
    return self();
  }

  public T withArguments(final Collection<Argument<?, ?, S>> args) {
    if (args == null || args.isEmpty()) {
      return self();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, null);
    }
    return self();
  }

  public T withArguments(final Collection<Argument<?, ?, S>> args, final boolean optional) {
    if (args == null || args.isEmpty()) {
      return self();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, optional);
    }
    return self();
  }

  public T withArguments(final Argument<?, ?, S>[] args, final boolean optional) {
    if (args == null || args.length == 0) {
      return self();
    }
    for (final Argument<?, ?, S> arg : args) {
      withArguments(arg, optional);
    }
    return self();
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
    return self();
  }

  @SuppressWarnings("unchecked")
  public boolean hasArguments(final Argument<?, ?, S>... args) {
    return args != null && args.length != 0 && arguments.containsAll(List.of(args));
  }

  public boolean hasArguments() {
    return !arguments.isEmpty();
  }

  public List<Argument<?, ?, S>> arguments() {
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
    return self();
  }

  @SafeVarargs
  public final T removeArguments(final Argument<?, ?, S>... args) {
    if (args != null && args.length != 0) {
      arguments.removeAll(List.of(args));
      recomputeHasOptional();
    }
    return self();
  }

  public T clearArguments() {
    arguments.clear();
    hasOptional = false;
    return self();
  }

  // ============ Subcommands ============
  public T withSubcommands(final Collection<SimpleSubCommand<S>> subs) {
    if (subs == null || subs.isEmpty()) {
      return self();
    }
    if (subcommands == null) {
      subcommands = new HashMap<>();
    }
    for (final SimpleSubCommand<S> sub : subs) {
      if (sub == null || sub.aliases.isEmpty()) {
        continue;
      }
      for (final String alias : sub.aliases) {
        subcommands.put(alias, sub);
      }
    }
    return self();
  }

  @SafeVarargs
  public final T withSubcommands(final SimpleSubCommand<S>... subs) {
    if (subs == null || subs.length == 0) {
      return self();
    }
    if (subcommands == null) {
      subcommands = new HashMap<>();
    }
    for (final SimpleSubCommand<S> sub : subs) {
      if (sub == null || sub.aliases.isEmpty()) {
        continue;
      }
      for (final String alias : sub.aliases) {
        subcommands.put(alias, sub);
      }
    }
    return self();
  }

  public final T withSubcommands(final SimpleSubCommand<S> sub) {
    if (sub == null || sub.aliases.isEmpty()) {
      return self();
    }
    if (subcommands == null) {
      subcommands = new HashMap<>();
    }
    for (final String alias : sub.aliases) {
      subcommands.put(alias, sub);
    }
    return self();
  }

  public T setSubcommands(final List<SimpleSubCommand<S>> subs) {
    if (subs == null) {
      return self();
    }
    if (subs.isEmpty()) {
      subcommands = null;
      return self();
    }
    if (subcommands == null) {
      subcommands = new HashMap<>();
    } else {
      subcommands.clear();
    }
    for (final SimpleSubCommand<S> sub : subs) {
      if (sub == null || sub.aliases.isEmpty()) {
        continue;
      }
      for (final String alias : sub.aliases) {
        subcommands.put(alias, sub);
      }
    }
    return self();
  }

  @SafeVarargs
  public final T removeSubcommands(final SimpleSubCommand<S>... subs) {
    if (subs == null || subs.length == 0 || !hasSubcommands()) {
      return self();
    }
    for (final SimpleSubCommand<S> sub : subs) {
      if (sub == null || sub.aliases.isEmpty()) {
        continue;
      }
      for (final String alias : sub.aliases) {
        subcommands.put(alias, sub);
      }
    }
    return self();
  }

  public T removeSubcommands(final Collection<SimpleSubCommand<S>> subs) {
    if (subs == null || subs.isEmpty() || !hasSubcommands()) {
      return self();
    }
    for (final SimpleSubCommand<S> sub : subs) {
      if (sub == null || sub.aliases.isEmpty()) {
        continue;
      }
      for (final String alias : sub.aliases) {
        subcommands.put(alias, sub);
      }
    }
    return self();
  }

  public T removeSubcommands(final SimpleSubCommand<S> sub) {
    if (sub == null || sub.aliases.isEmpty() || !hasSubcommands()) {
      return self();
    }
    for (final String alias : sub.aliases) {
      subcommands.put(alias, sub);
    }
    return self();
  }

  public T clearSubcommands() {
    if (subcommands != null) {
      subcommands.clear();
      subcommands = null;
    }
    return self();
  }

  public boolean hasSubcommand(final SimpleSubCommand<S> sub) {
    if (sub == null || sub.aliases.isEmpty() || !hasSubcommands()) {
      return false;
    }
    for (final String key : sub.aliases) {
      if (subcommands.containsKey(key)) {
        return true;
      }
    }
    return false;
  }

  public boolean hasSubcommands() {
    return subcommands != null && !subcommands.isEmpty();
  }

  public boolean isSubcommand(String sub) {
    if (!hasSubcommands()) {
      return false;
    }
    sub = StringUtils.prepareLowercase(sub);
    return sub != null && subcommands.containsKey(sub);
  }

  public SimpleSubCommand<S> getSubcommand(String sub) {
    if (!hasSubcommands()) {
      return null;
    }
    sub = StringUtils.prepareLowercase(sub);
    if (sub == null) {
      return null;
    }
    return subcommands.get(sub);
  }

  public Map<String, SimpleSubCommand<S>> subcommands() {
    return subcommands;
  }

  // ============ execution ============
  protected boolean hasAnyExecutors() {
    if (hasExecutors()) {
      return true;
    }
    if (!hasSubcommands()) {
      return false;
    }
    for (final SimpleSubCommand<S> sub : subcommands.values()) {
      if (sub.hasAnyExecutors()) {
        return true;
      }
    }
    return false;
  }

  protected void run(final ExecutionInfo<S> info, final int idx, final ExecutorType executorType)
      throws CommandException {
    final SimpleSubCommand<S> sub = getSubcommand(info.args().getRaw(idx));
    if (sub != null) {
      sub.run(info, idx + 1, executorType);
      return;
    }
    if (executors.isEmpty()) {
      return;
    }
    NormalExecutor<? extends S>[] ex = getExecutors(executorType);
    if (ex == null) {
      ex = getExecutors(ExecutorType.NATIVE);
    }
    if (ex == null) {
      ex = getExecutors(ExecutorType.ALL);
    }
    if (ex == null) {
      return;
    }
    for (final NormalExecutor<? extends S> normalExecutor : ex) {
      normalExecutor.executeWith(info);
    }
  }

  public List<Suggestions<S>> tabComplete(final SuggestionInfo<S> info, int idx) {
    if (arguments.isEmpty() && subcommands.isEmpty()) {
      return null;
    }

    if (idx >= 0) {
      if (idx < info.args().count()) {
        final SimpleSubCommand<S> sub = getSubcommand(info.args().getRaw(idx));
        if (sub != null) {
          if (!sub.checkRequirements(info.source())) {
            return null;
          }
          return sub.tabComplete(info, idx + 1);
        }
      }
      if (!subcommands.isEmpty() && idx == 0) {
        final List<String> list = new ArrayList<>();
        for (final SimpleSubCommand<S> sub : subcommands.values()) {
          if (!sub.checkRequirements(info.source())) {
            continue;
          }
          list.addAll(sub.aliases());
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
    if (arg == null || !arg.checkRequirements(info.source())) {
      return null;
    }
    return arg.getSuggestions();
  }

  @Override
  public String toString() {
    return "AbstractCommand [arguments=" + arguments + ", subcommands=" + subcommands + ", aliases=" + aliases
        + ", executors=" + executors + ", requirements=" + requirements + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), arguments, subcommands, aliases, requirements, shortDescription,
        fullDescription,
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
