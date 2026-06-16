package com.lemonlightmc.zenith.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.exceptions.InvalidCommandNameException;
import com.lemonlightmc.zenith.commands.exceptions.MissingCommandExecutorException;
import com.lemonlightmc.zenith.commands.executors.AbstractCommand;
import com.lemonlightmc.zenith.commands.executors.BukkitExecutors.*;
import com.lemonlightmc.zenith.commands.executors.Executors.ExecutorType;
import com.lemonlightmc.zenith.commands.executors.RootCommand;
import com.lemonlightmc.zenith.exceptions.PlatformException;
import com.lemonlightmc.zenith.utils.ServerPlatform;
import com.lemonlightmc.zenith.utils.StringUtils;

public class SimpleCommand extends RootCommand<SimpleCommand, CommandSender> {

  private NamespacedKey key = null;
  private String[] usageDescription;
  private String helpMessage;

  public SimpleCommand(final NamespacedKey key) {
    super();
    setName(key);
  }

  @Override
  public SimpleCommand getInstance() {
    return this;
  }

  public static SimpleCommand create(final NamespacedKey key) {
    return new SimpleCommand(key);
  }

  public static SimpleSubCommand<CommandSender> subCommand(final String... aliases) {
    return new SimpleSubCommand<CommandSender>(aliases);
  }

  @Override
  public SimpleCommand register() {
    build();
    CommandAPI.register(this);
    return this;
  }

  @Override
  public SimpleCommand unregister() {
    CommandAPI.unregister(this);
    return this;
  }

  @Override
  public boolean isRegistered() {
    return CommandAPI.isRegistered(key.toString());
  }

  @Override
  public String getNamespace() {
    return key.getNamespace();
  }

  @Override
  public String getKey() {
    return key.getKey();
  }

  @Override
  public NamespacedKey getName() {
    return key;
  }

  @Override
  public SimpleCommand setName(final NamespacedKey key) {
    if (this.key != null && this.key.equals(key)) {
      return this;
    }
    if (key == null || key.getKey().length() == 0 || key.getKey().isBlank() || key.getNamespace().length() == 0
        || key.getNamespace().isBlank()) {
      throw new InvalidCommandNameException(key == null ? "null" : key.toString());
    }
    if (isRegistered()) {
      throw new IllegalStateException("Cannot change the name of a registered command!");
    }
    if (this.key != null) {
      removeAlias(this.key.getKey());
    }
    this.key = key;
    withAliases(this.key.getKey());
    return this;
  }

  @Override
  public SimpleCommand setKey(final String key) {
    setName(new NamespacedKey(getNamespace(), key));
    return this;
  }

  @Override
  public SimpleCommand setNamespacey(final String namespace) {
    setName(new NamespacedKey(namespace, getKey()));
    return this;
  }

  @Override
  public SimpleCommand withUsage(final String usage) {
    this.usageDescription = usage.split("\n");
    return this;
  }

  @Override
  public SimpleCommand withUsage(final String... usage) {
    this.usageDescription = usage;
    return this;
  }

  @Override
  public SimpleCommand withUsage(final List<String> usage) {
    this.usageDescription = usage.toArray(String[]::new);
    return this;
  }

  @Override
  public String[] getUsage() {
    return usageDescription;
  }

  @Override
  public SimpleCommand withHelp(
      final String shortDesc,
      final String fullDesc) {
    this.shortDescription = shortDesc;
    this.fullDescription = fullDesc;
    return this;
  }

  @Override
  public SimpleCommand withHelp(final List<String> help) {
    this.helpMessage = String.join("\n", help);
    return this;
  }

  @Override
  public SimpleCommand withHelp(final String help) {
    this.helpMessage = help;
    return this;
  }

  @Override
  public List<String> getHelp() {
    return helpMessage == null ? null : List.of(helpMessage.split("\n"));
  }

  // Player command executor
  public SimpleCommand executesPlayer(final PlayerCommandExecutor executor) {
    addExecutor(ExecutorType.PLAYER, executor);
    return getInstance();
  }

  public SimpleCommand executesPlayer(final PlayerExecutionInfo info) {
    addExecutor(ExecutorType.PLAYER, info);
    return getInstance();
  }

  // Entity command executor
  public SimpleCommand executesEntity(final EntityCommandExecutor executor) {
    addExecutor(ExecutorType.ENTITY, executor);
    return getInstance();
  }

  public SimpleCommand executesEntity(final EntityExecutionInfo info) {
    addExecutor(ExecutorType.ENTITY, info);
    return getInstance();
  }

  // Command block command executor
  public SimpleCommand executesCommandBlock(final CommandBlockExecutor executor) {
    addExecutor(ExecutorType.BLOCK, executor);
    return getInstance();
  }

  public SimpleCommand executesCommandBlock(final CommandBlockExecutionInfo info) {
    addExecutor(ExecutorType.BLOCK, info);
    return getInstance();
  }

  // Console command executor
  public SimpleCommand executesConsole(final ConsoleCommandExecutor executor) {
    addExecutor(ExecutorType.CONSOLE, executor);
    return getInstance();
  }

  public SimpleCommand executesConsole(final ConsoleExecutionInfo info) {
    addExecutor(ExecutorType.CONSOLE, info);
    return getInstance();
  }

  // RemoteConsole command executor
  public SimpleCommand executesRemoteConsole(final RemoteConsoleCommandExecutor executor) {
    addExecutor(ExecutorType.REMOTE, executor);
    return getInstance();
  }

  public SimpleCommand executesRemoteConsole(final RemoteConsoleExecutionInfo info) {
    addExecutor(ExecutorType.REMOTE, info);
    return getInstance();
  }

  // Native command executor
  public SimpleCommand executesNative(final NativeCommandExecutor executor) {
    addExecutor(ExecutorType.NATIVE, executor);
    return getInstance();
  }

  public SimpleCommand executesNative(final NativeExecutionInfo info) {
    addExecutor(ExecutorType.NATIVE, info);
    return getInstance();
  }

  // Proxy command executor
  public SimpleCommand executesNative(final ProxyCommandExecutor executor) {
    addExecutor(ExecutorType.PROXY, executor);
    return getInstance();
  }

  public SimpleCommand executesNative(final ProxyExecutionInfo info) {
    addExecutor(ExecutorType.PROXY, info);
    return getInstance();
  }

  // Feedback-forwarding command executor
  public SimpleCommand executesFeedbackForwarding(final FeedbackForwardingExecutor executor) {
    if (!ServerPlatform.isPaper()) {
      throw new PlatformException(
          "Attempted to use a FeedbackForwardingCommandExecutor on a non-paper platform ("
              + ServerPlatform.detect().name() + ")!");
    }
    addExecutor(ExecutorType.FEEDBACK_FORWARDING, executor);
    return getInstance();
  }

  public SimpleCommand executesFeedbackForwarding(final FeedbackForwardingExecutionInfo info) {
    if (!ServerPlatform.isPaper()) {
      throw new PlatformException(
          "Attempted to use a FeedbackForwardingExecutionInfo on a non-paper platform ("
              + ServerPlatform.detect().name() + ")!");
    }
    addExecutor(ExecutorType.FEEDBACK_FORWARDING, info);
    return getInstance();
  }

  void build() {
    if (!hasAnyExecutors()) {
      throw new MissingCommandExecutorException(getName().toString());
    }
    if (fullDescription == null) {
      fullDescription = "The " + getKey() + " Command from " + getNamespace() + " (No Description)";
    }
    if (shortDescription == null) {
      shortDescription = "The " + getKey() + " Command from " + getNamespace() + " (No Description)";
    }
    if (usageDescription == null) {
      usageDescription = buildUsageString("/", this).toArray(String[]::new);
    }
    if (helpMessage == null) {
      final StringBuilder builder = new StringBuilder(shortDescription);
      builder.append("Description: ");
      builder.append(fullDescription);
      if (usageDescription != null) {
        builder.append("\nUsage:");
        for (final String usageLine : usageDescription) {
          builder.append(" ");
          builder.append(usageLine);
        }
      }
      builder.append("\nAliases: " + String.join(", ", aliases));
      helpMessage = builder.toString();
    }
  }

  private List<String> buildUsageString(final String str, final AbstractCommand<?, CommandSender> command) {
    final ArrayList<String> usageList = new ArrayList<>();
    String str2 = str + " <" + String.join("|", aliases) + ">";
    for (final SimpleSubCommand<CommandSender> subCmd : subcommands) {
      usageList.addAll(buildUsageString(str2, subCmd));
    }
    for (final Argument<?, ?, CommandSender> arg : command.getArguments()) {
      str2 += " ";
      str2 += arg.getHelpString();
    }
    usageList.add(str2);
    return usageList;
  }

  @Override
  public int hashCode() {
    int result = 31 * super.hashCode() + key.hashCode();
    result = 31 * result + Arrays.hashCode(usageDescription);
    result = 31 * result + ((helpMessage == null) ? 0 : helpMessage.hashCode());
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
    if (helpMessage == null && other.helpMessage != null) {
      return false;
    }
    return key.equals(other.key) && Arrays.equals(usageDescription, other.usageDescription)
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
