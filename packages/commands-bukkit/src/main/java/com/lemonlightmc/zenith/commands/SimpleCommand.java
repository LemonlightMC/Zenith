package com.lemonlightmc.zenith.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.exceptions.InvalidCommandNameException;
import com.lemonlightmc.zenith.commands.exceptions.MissingCommandExecutorException;
import com.lemonlightmc.zenith.commands.executors.AbstractCommand;
import com.lemonlightmc.zenith.commands.executors.BukkitExecutors.*;
import com.lemonlightmc.zenith.commands.executors.Executors.ExecutorType;
import com.lemonlightmc.zenith.commands.executors.RootCommand;
import com.lemonlightmc.zenith.exceptions.PlatformException;
import com.lemonlightmc.zenith.utils.ServerPlatform;

/**
 * Bukkit-backed root command implementation for building, registering, and
 * executing Zenith commands.
 */
public class SimpleCommand extends RootCommand<SimpleCommand, CommandSender> {

  private NamespacedKey key = null;
  private String[] usageDescription;
  private String helpMessage;

  /**
   * Creates a new command with the given namespaced key.
   *
   * @param key the command key
   * @throws InvalidCommandNameException if the provided key is invalid
   */
  public SimpleCommand(final NamespacedKey key) {
    super();
    setKey(key);
  }

  /**
   * Returns this command instance.
   *
   * @return this command
   */
  @Override
  public SimpleCommand self() {
    return this;
  }

  /**
   * Creates a new simple command with the given namespaced key.
   *
   * @param key the command key
   * @return a new simple command
   */
  public static SimpleCommand create(final NamespacedKey key) {
    return new SimpleCommand(key);
  }

  /**
   * Creates a new subcommand with the given aliases.
   *
   * @param aliases the subcommand aliases
   * @return a new subcommand builder
   */
  public static SimpleSubCommand<CommandSender> subcommand(final String... aliases) {
    return new SimpleSubCommand<CommandSender>(aliases);
  }

  /**
   * Builds and registers this command with the server.
   *
   * @return this command
   */
  @Override
  public SimpleCommand register() {
    build();
    CommandAPI.register(this);
    return this;
  }

  /**
   * Unregisters this command from the server.
   *
   * @return this command
   */
  @Override
  public SimpleCommand unregister() {
    CommandAPI.unregister(this);
    return this;
  }

  /**
   * Checks whether this command is currently registered.
   *
   * @return {@code true} if the command is registered; {@code false} otherwise
   */
  @Override
  public boolean isRegistered() {
    return CommandAPI.isRegistered(key.toString());
  }

  /**
   * Returns the namespace of this command.
   *
   * @return the command namespace
   */
  @Override
  public String namespace() {
    return key.getNamespace();
  }

  /**
   * Returns the key portion of this command's name.
   *
   * @return the command key
   */
  @Override
  public NamespacedKey key() {
    return key;
  }

  /**
   * Returns the namespaced key identifying this command.
   *
   * @return the command name
   */
  @Override
  public String name() {
    return key.getKey();
  }

  /**
   * Updates the namespace portion of this command's namespaced name.
   *
   * @param namespace the new command namespace
   * @return this command
   */
  @Override
  public SimpleCommand setNamespace(final String namespace) {
    setKey(new NamespacedKey(namespace, name()));
    return this;
  }

  /**
   * Sets the namespaced key for this command.
   *
   * @param key the new command key
   * @return this command
   * @throws InvalidCommandNameException if the key is invalid
   * @throws IllegalStateException       if the command is already registered
   */
  @Override
  public SimpleCommand setName(final String name) {
    setKey(new NamespacedKey(namespace(), name));
    return this;
  }

  /**
   * Updates the key portion of this command's namespaced name.
   *
   * @param key the new command key
   * @return this command
   */
  @Override
  public SimpleCommand setKey(final NamespacedKey key) {
    if (key == null) {
      throw new IllegalArgumentException("Command key cannot be null!");
    }
    if (this.key.equals(key)) {
      return this;
    }
    if (isRegistered()) {
      throw new IllegalStateException("Cannot change the name of a registered command!");
    }
    removeAliases(this.key.getKey());
    this.key = key;
    withAliases(key.getKey());
    return this;
  }

  /**
   * Sets the usage description from a newline-separated string.
   *
   * @param usage the usage description
   * @return this command
   */
  @Override
  public SimpleCommand withUsage(final String usage) {
    this.usageDescription = usage == null ? null : usage.split("\n");
    return this;
  }

  /**
   * Sets the usage description lines for this command.
   *
   * @param usage the usage description lines
   * @return this command
   */
  @Override
  public SimpleCommand withUsage(final String... usage) {
    this.usageDescription = usage;
    return this;
  }

  /**
   * Sets the usage description lines for this command.
   *
   * @param usage the usage description lines
   * @return this command
   */
  @Override
  public SimpleCommand withUsage(final List<String> usage) {
    this.usageDescription = usage == null ? null : usage.toArray(String[]::new);
    return this;
  }

  /**
   * Returns the configured usage description lines.
   *
   * @return the usage description lines, or {@code null} if not set
   */
  @Override
  public String[] usage() {
    return usageDescription;
  }

  /**
   * Sets the short and full help descriptions for this command.
   *
   * @param shortDesc the short description
   * @param fullDesc  the full description
   * @return this command
   */
  @Override
  public SimpleCommand withHelp(
      final String shortDesc,
      final String fullDesc) {
    this.shortDescription = shortDesc;
    this.fullDescription = fullDesc;
    return this;
  }

  /**
   * Sets the help message from multiple lines.
   *
   * @param help the help message lines
   * @return this command
   */
  @Override
  public SimpleCommand withHelp(final List<String> help) {
    this.helpMessage = String.join("\n", help);
    return this;
  }

  /**
   * Sets the help message for this command.
   *
   * @param help the help message
   * @return this command
   */
  @Override
  public SimpleCommand withHelp(final String help) {
    this.helpMessage = help;
    return this;
  }

  /**
   * Returns the configured help message as individual lines.
   *
   * @return the help message lines, or {@code null} if not set
   */
  @Override
  public List<String> help() {
    return helpMessage == null ? null : List.of(helpMessage.split("\n"));
  }

  /**
   * Registers an executor for player senders.
   *
   * @param executor the player command executor
   * @return this command
   */
  public SimpleCommand executesPlayer(final PlayerCommandExecutor executor) {
    addExecutor(ExecutorType.PLAYER, executor);
    return self();
  }

  /**
   * Registers an execution info handler for player senders.
   *
   * @param info the player execution info handler
   * @return this command
   */
  public SimpleCommand executesPlayer(final PlayerExecutionInfo info) {
    addExecutor(ExecutorType.PLAYER, info);
    return self();
  }

  /**
   * Registers an executor for entity senders.
   *
   * @param executor the entity command executor
   * @return this command
   */
  public SimpleCommand executesEntity(final EntityCommandExecutor executor) {
    addExecutor(ExecutorType.ENTITY, executor);
    return self();
  }

  /**
   * Registers an execution info handler for entity senders.
   *
   * @param info the entity execution info handler
   * @return this command
   */
  public SimpleCommand executesEntity(final EntityExecutionInfo info) {
    addExecutor(ExecutorType.ENTITY, info);
    return self();
  }

  /**
   * Registers an executor for command block senders.
   *
   * @param executor the command block executor
   * @return this command
   */
  public SimpleCommand executesCommandBlock(final CommandBlockExecutor executor) {
    addExecutor(ExecutorType.BLOCK, executor);
    return self();
  }

  /**
   * Registers an execution info handler for command block senders.
   *
   * @param info the command block execution info handler
   * @return this command
   */
  public SimpleCommand executesCommandBlock(final CommandBlockExecutionInfo info) {
    addExecutor(ExecutorType.BLOCK, info);
    return self();
  }

  /**
   * Registers an executor for console senders.
   *
   * @param executor the console command executor
   * @return this command
   */
  public SimpleCommand executesConsole(final ConsoleCommandExecutor executor) {
    addExecutor(ExecutorType.CONSOLE, executor);
    return self();
  }

  /**
   * Registers an execution info handler for console senders.
   *
   * @param info the console execution info handler
   * @return this command
   */
  public SimpleCommand executesConsole(final ConsoleExecutionInfo info) {
    addExecutor(ExecutorType.CONSOLE, info);
    return self();
  }

  /**
   * Registers an executor for remote console senders.
   *
   * @param executor the remote console command executor
   * @return this command
   */
  public SimpleCommand executesRemoteConsole(final RemoteConsoleCommandExecutor executor) {
    addExecutor(ExecutorType.REMOTE, executor);
    return self();
  }

  /**
   * Registers an execution info handler for remote console senders.
   *
   * @param info the remote console execution info handler
   * @return this command
   */
  public SimpleCommand executesRemoteConsole(final RemoteConsoleExecutionInfo info) {
    addExecutor(ExecutorType.REMOTE, info);
    return self();
  }

  /**
   * Registers an executor for native senders.
   *
   * @param executor the native command executor
   * @return this command
   */
  public SimpleCommand executesNative(final NativeCommandExecutor executor) {
    addExecutor(ExecutorType.NATIVE, executor);
    return self();
  }

  /**
   * Registers an execution info handler for native senders.
   *
   * @param info the native execution info handler
   * @return this command
   */
  public SimpleCommand executesNative(final NativeExecutionInfo info) {
    addExecutor(ExecutorType.NATIVE, info);
    return self();
  }

  /**
   * Registers an executor for proxied senders.
   *
   * @param executor the proxy command executor
   * @return this command
   */
  public SimpleCommand executesProxy(final ProxyCommandExecutor executor) {
    addExecutor(ExecutorType.PROXY, executor);
    return self();
  }

  /**
   * Registers an execution info handler for proxied senders.
   *
   * @param info the proxy execution info handler
   * @return this command
   */
  public SimpleCommand executesProxy(final ProxyExecutionInfo info) {
    addExecutor(ExecutorType.PROXY, info);
    return self();
  }

  /**
   * Registers a feedback-forwarding executor for Paper platforms.
   *
   * @param executor the feedback-forwarding executor
   * @return this command
   * @throws PlatformException if called on a non-Paper platform
   */
  public SimpleCommand executesFeedbackForwarding(final FeedbackForwardingExecutor executor) {
    if (!ServerPlatform.isPaper()) {
      throw new PlatformException(
          "Attempted to use a FeedbackForwardingCommandExecutor on a non-paper platform ("
              + ServerPlatform.detect().name() + ")!");
    }
    addExecutor(ExecutorType.FEEDBACK_FORWARDING, executor);
    return self();
  }

  /**
   * Registers a feedback-forwarding execution info handler for Paper platforms.
   *
   * @param info the feedback-forwarding execution info handler
   * @return this command
   * @throws PlatformException if called on a non-Paper platform
   */
  public SimpleCommand executesFeedbackForwarding(final FeedbackForwardingExecutionInfo info) {
    if (!ServerPlatform.isPaper()) {
      throw new PlatformException(
          "Attempted to use a FeedbackForwardingExecutionInfo on a non-paper platform ("
              + ServerPlatform.detect().name() + ")!");
    }
    addExecutor(ExecutorType.FEEDBACK_FORWARDING, info);
    return self();
  }

  /**
   * Prepares this command for registration by validating executors and populating
   * default metadata.
   *
   * @throws MissingCommandExecutorException if no executors are configured
   */
  void build() {
    if (!hasAnyExecutors()) {
      throw new MissingCommandExecutorException(name().toString());
    }
    if (fullDescription == null || fullDescription.isBlank()) {
      fullDescription = shortDescription == null || shortDescription.isBlank()
          ? "Executes the " + key() + " command."
          : shortDescription;
    }
    if (shortDescription == null || shortDescription.isBlank()) {
      shortDescription = fullDescription;
    }
    if (usageDescription == null || usageDescription.length == 0) {
      usageDescription = buildUsageString("/", this, name()).toArray(String[]::new);
    }
    if (helpMessage == null || helpMessage.isBlank()) {
      final StringBuilder builder = new StringBuilder();
      builder.append(shortDescription);
      if (!Objects.equals(shortDescription, fullDescription)) {
        builder.append('\n').append(fullDescription);
      }

      if (usageDescription != null && usageDescription.length != 0) {
        builder.append("\nUsage:");
        for (final String usageLine : usageDescription) {
          if (usageLine != null && !usageLine.isBlank()) {
            builder.append("\n  ").append(usageLine);
          }
        }
      }

      final List<String> aliasList = new ArrayList<>();
      if (aliases != null && !aliases.isEmpty()) {
        for (final String alias : aliases) {
          if (alias != null && !alias.isBlank() && !alias.equalsIgnoreCase(name())) {
            aliasList.add(alias);
          }
        }
      }
      if (!aliasList.isEmpty()) {
        aliasList.sort(String.CASE_INSENSITIVE_ORDER);
        builder.append("\nAliases: ").append(String.join(", ", aliasList));
      }

      helpMessage = builder.toString();
    }
  }

  private List<String> buildUsageString(final String prefix, final AbstractCommand<?, CommandSender> command,
      final String label) {
    final List<String> usages = new ArrayList<>();
    final String effectiveLabel = (label == null || label.isBlank()) ? formatAliases(command.aliases()) : label;
    final String base = prefix + effectiveLabel;

    final StringBuilder own = new StringBuilder(base);
    if (command.arguments() != null) {
      for (final Argument<?, ?, CommandSender> arg : command.arguments()) {
        own.append(' ').append(arg.getHelpString());
      }
    }

    if (command.hasSubcommands()) {
      if (command.hasExecutors()) {
        usages.add(own.toString());
      }
      for (final SimpleSubCommand<CommandSender> subCmd : command.subcommands().values()) {
        usages.addAll(buildUsageString(base + " ", subCmd, formatAliases(subCmd.aliases())));
      }
    } else {
      usages.add(own.toString());
    }
    return usages;
  }

  private String formatAliases(final java.util.Collection<String> aliases) {
    if (aliases == null || aliases.isEmpty()) {
      return "<command>";
    }
    if (aliases.size() == 1) {
      return aliases.iterator().next();
    }
    final List<String> sorted = new ArrayList<>(aliases);
    sorted.sort(String.CASE_INSENSITIVE_ORDER);
    return "<" + String.join("|", sorted) + ">";
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(super.hashCode(), key, helpMessage);
    result = 31 * result + Arrays.hashCode(usageDescription);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || getClass() != obj.getClass()) {
      return false;
    }
    final SimpleCommand other = (SimpleCommand) obj;
    return Objects.equals(key, other.key)
        && Arrays.equals(usageDescription, other.usageDescription)
        && Objects.equals(helpMessage, other.helpMessage);
  }

  @Override
  public String toString() {

    return "SimpleCommand [key=" + key + ", shortDescription=" + shortDescription + ", usageDescription="
        + StringUtils.join("\n", usageDescription) + ", arguments=" + arguments + ", subcommands=" + subcommands
        + ", aliases="
        + aliases
        + "]";
  }

}
