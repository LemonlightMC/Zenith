package com.lemonlightmc.zenith.commands.argumentsbase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.commands.CommandRequirement;
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.suggestions.Suggestions;

public abstract class Argument<Type, ArgType extends Argument<Type, ArgType, S>, S> {
  protected String name;
  protected ArgumentType rawType;
  protected Class<Type> primitiveType;
  protected boolean isOptional = false;
  protected boolean isListed = true;
  protected List<CommandRequirement<CommandSource<S>>> requirements;
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

  public abstract ArgType getInstance();

  public CommandSyntaxException createError(final StringReader reader, final String value) {
    return new CommandSyntaxException(reader,
        "Invalid Value '" + value + "' for " + rawType.getName() + " Argument '" + name + "'");
  }

  public abstract Type parseArgument(CommandSource<S> source, StringReader reader, String key)
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
    return getInstance();
  }

  // Optional
  public boolean isOptional() {
    return isOptional;
  }

  public ArgType setOptional(final boolean optional) {
    this.isOptional = optional;
    return getInstance();
  }

  // Suggestions

  public ArgType withSuggestions(final Suggestions<S> func) {
    this.suggestions.add(func);
    return getInstance();
  }

  public ArgType withSuggestions(final String... suggestions) {
    this.suggestions.add(Suggestions.from(suggestions));
    return getInstance();
  }

  public ArgType withSuggestions(final Collection<String> suggestions) {
    this.suggestions.add(Suggestions.from(suggestions));
    return getInstance();
  }

  public List<Suggestions<S>> getSuggestions() {
    return this.suggestions;
  }

  // requirements

  public ArgType withRequirement(final CommandRequirement<CommandSource<S>> requirement) {
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
  public final ArgType withRequirement(final CommandRequirement<CommandSource<S>>... requirements) {
    if (requirements != null && requirements.length != 0) {
      for (final CommandRequirement<CommandSource<S>> requirement : requirements) {
        withRequirement(requirement);
      }
    }
    return getInstance();
  }

  public ArgType withRequirement(final Collection<CommandRequirement<CommandSource<S>>> requirements) {
    if (requirements != null && !requirements.isEmpty()) {
      for (final CommandRequirement<CommandSource<S>> requirement : requirements) {
        withRequirement(requirement);
      }
    }
    return getInstance();
  }

  public ArgType withRequirement(final Predicate<CommandSource<S>> requirement) {
    return withRequirement(requirement == null ? null : CommandRequirement.from(requirement));
  }

  public ArgType withPermissions(final String... permissions) {
    if (permissions != null && permissions.length != 0) {
      for (final String perm : permissions) {
        withRequirement(CommandRequirement.permission(perm));
      }
    }
    return getInstance();
  }

  public ArgType withPermissions(final Collection<String> permissions) {
    if (permissions != null && !permissions.isEmpty()) {
      for (final String perm : permissions) {
        withRequirement(CommandRequirement.permission(perm));
      }
    }
    return getInstance();
  }

  public ArgType setRequirements(final List<CommandRequirement<CommandSource<S>>> requirements) {
    if (requirements != null && !requirements.isEmpty()) {
      this.requirements = requirements;
    }
    return getInstance();
  }

  public boolean hasRequirements() {
    return requirements != null && !requirements.isEmpty();
  }

  public ArgType clearRequirements() {
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

  // Help
  public String getHelpString() {
    if (!isListed) {
      return name;
    }
    return isOptional ? "[" + name + "]" : "<" + name + ">";
  }

  @Override
  public int hashCode() {
    int result = 31 + name.hashCode();
    result = 31 * result + rawType.hashCode();
    result = 31 * result + primitiveType.hashCode();
    result = 31 * result + ((requirements == null) ? 0 : suggestions.hashCode());
    result = 31 * result + ((suggestions == null) ? 0 : suggestions.hashCode());
    result = 31 * result + (isOptional ? 1231 : 1237);
    return 31 * result + (isListed ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (getInstance() == obj) {
      return true;
    }
    if (obj == null || getInstance().getClass() != obj.getClass()) {
      return false;
    }
    final Argument<?, ?, ?> other = (Argument<?, ?, ?>) obj;
    if (name == null && other.name != null || rawType == null && other.rawType != null
        || primitiveType == null && other.primitiveType != null || requirements == null && other.requirements != null
        || suggestions == null && other.suggestions != null) {
      return false;
    }
    return isListed == other.isListed && isOptional == other.isOptional
        && rawType == other.rawType
        && name.equals(other.name) && name.equals(other.name)
        && primitiveType.equals(other.primitiveType)
        && requirements.equals(other.requirements)
        && suggestions.equals(other.suggestions);
  }

  @Override
  public String toString() {
    return toStringWithMore(null);
  }

  public String toStringWithMore(final String str) {
    return getInstance().getClass().getName() + " [name=" + name + ", rawType=" + rawType + ", primitiveType="
        + primitiveType + ", isOptional="
        + isOptional + ", isListed=" + isListed + ", requirements=" + requirements
        + ", suggestions=" + suggestions + (str == null || str.isEmpty() ? "]" : ", " + str + "]");
  }
}
