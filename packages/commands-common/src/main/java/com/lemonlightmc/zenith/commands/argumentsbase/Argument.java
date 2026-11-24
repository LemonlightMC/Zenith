package com.lemonlightmc.zenith.commands.argumentsbase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.suggestions.Suggestions;
import com.lemonlightmc.zenith.actions.ExecutionContext;
import com.lemonlightmc.zenith.actions.Conditions;
import com.lemonlightmc.zenith.actions.Condition;
import com.lemonlightmc.zenith.actions.Condition.ConditionSet;

public abstract class Argument<Type, ArgType extends Argument<Type, ArgType, S>, S extends CommandSender> {
  protected String name;
  protected ArgumentType rawType;
  protected Class<Type> primitiveType;
  protected boolean isOptional = false;
  protected boolean isListed = true;
  protected ConditionSet<S> requirements;
  protected List<Suggestions<S>> suggestions = new ArrayList<>(4);

  protected Argument(final String name, final Class<Type> primitiveType, final ArgumentType rawType) {
    if (name == null || name.length() == 0) {
      throw new IllegalArgumentException("Invalid Argument Name");
    }
    if (primitiveType == null || !(primitiveType instanceof Class<Type>)) {
      throw new IllegalArgumentException("Invalid Primitive Type");
    }
    if (rawType == null || !(rawType instanceof ArgumentType)) {
      throw new IllegalArgumentException("Invalid Argument Type");
    }
    this.name = name;
    this.rawType = rawType;
    this.primitiveType = primitiveType;
  }

  public abstract ArgType self();

  public CommandSyntaxException createError(final StringReader reader, final String value) {
    return new CommandSyntaxException(reader,
        "Invalid Value '" + value + "' for " + rawType.getName() + " Argument '" + name + "'");
  }

  public abstract Type parseArgument(ExecutionContext<S> source, StringReader reader, String key)
      throws CommandSyntaxException;

  public String getName() {
    return name;
  }

  public ArgumentType getType() {
    return rawType;
  }

  public Class<Type> getPrimitiveType() {
    return primitiveType;
  }

  public ArgumentType getArgumentType() {
    return rawType;
  }

  // Listed
  public boolean isListed() {
    return this.isListed;
  }

  public ArgType setListed(final boolean listed) {
    this.isListed = listed;
    return self();
  }

  // Optional
  public boolean isOptional() {
    return isOptional;
  }

  public ArgType setOptional(final boolean optional) {
    this.isOptional = optional;
    return self();
  }

  // Suggestions

  public ArgType withSuggestions(final Suggestions<S> func) {
    this.suggestions.add(func);
    return self();
  }

  public ArgType withSuggestions(final String... suggestions) {
    this.suggestions.add(Suggestions.from(suggestions));
    return self();
  }

  public ArgType withSuggestions(final Collection<String> suggestions) {
    this.suggestions.add(Suggestions.from(suggestions));
    return self();
  }

  public List<Suggestions<S>> getSuggestions() {
    return this.suggestions;
  }

  // ============ Requirements ============
  public ArgType withRequirements(final Condition<S> condition) {
    requirements().add(condition);
    return self();
  }

  @SafeVarargs
  public final ArgType withRequirements(final Condition<S>... conditions) {
    requirements().addAll(conditions);
    return self();
  }

  public ArgType withRequirements(final Collection<Condition<S>> conditions) {
    requirements().addAll(conditions);
    return self();
  }

  public ArgType withRequirements(final Predicate<ExecutionContext<S>> predicate) {
    requirements().add(Condition.of(predicate));
    return self();
  }

  public ArgType withPermissions(String permission) {
    permission = StringUtils.prepare(permission);
    if (permission == null) {
      return self();
    }
    requirements().add(Conditions.PermissionConditions.allowed(permission));
    return self();
  }

  public ArgType withPermissions(final String... permissions) {
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

  public ArgType withPermissions(final Collection<String> permissions) {
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

  public ArgType setRequirements(final List<Condition<S>> requirements) {
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

  public ArgType clearRequirements() {
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

  public boolean checkRequirements(final ExecutionContext<S> source) {
    if (requirements == null) {
      return true;
    }
    return requirements.test(source);
  }

  // Help
  public String getHelpString() {
    if (!isListed) {
      return name;
    }
    return isOptional ? "[" + name + "]" : "<" + name + ">";
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, rawType, primitiveType, isOptional, isListed, requirements, suggestions);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Argument<?, ?, ?> other = (Argument<?, ?, ?>) obj;
    return isOptional == other.isOptional
        && isListed == other.isListed
        && rawType == other.rawType
        && Objects.equals(name, other.name)
        && Objects.equals(primitiveType, other.primitiveType)
        && Objects.equals(requirements, other.requirements)
        && Objects.equals(suggestions, other.suggestions);
  }

  @Override
  public String toString() {
    return toStringWithMore(null);
  }

  public String toStringWithMore(final String str) {
    return self().getClass().getName() + " [name=" + name + ", rawType=" + rawType + ", primitiveType="
        + primitiveType + ", isOptional="
        + isOptional + ", isListed=" + isListed + ", requirements=" + requirements
        + ", suggestions=" + suggestions + (str == null || str.isEmpty() ? "]" : ", " + str + "]");
  }
}
