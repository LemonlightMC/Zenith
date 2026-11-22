package com.lemonlightmc.zenith.commands;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.SimplePluginManager;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.exceptions.InvalidCommandNameException;
import com.lemonlightmc.zenith.commands.exceptions.MissingCommandExecutorException;
import com.lemonlightmc.zenith.commands.executors.CommandHandler;
import com.lemonlightmc.zenith.commands.executors.InternalExecutor;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;

/**
 * Central entry point for registering, resolving, dispatching, and managing
 * Bukkit commands.
 *
 * <p>
 * This API bridges Zenith's command abstractions with Bukkit's command map and
 * provides helpers for synchronous and
 * asynchronous command execution.
 */
public class CommandAPI {
  private static final Field COMMAND_MAP_FIELD;
  private static final Field KNOWN_COMMANDS_FIELD;
  private volatile static CommandMap commandMap;
  private volatile static Map<String, Command> knownCommandMap;

  /** The namespace used for command registration. */
  public volatile static String namespace;
  /** The shared command handler used for simple command dispatch. */
  public volatile static CommandHandler<CommandSender, BukkitCommandSource<CommandSender>> handler;

  static {
    try {
      COMMAND_MAP_FIELD = SimplePluginManager.class.getDeclaredField("commandMap");
      COMMAND_MAP_FIELD.setAccessible(true);
    } catch (final NoSuchFieldException | InaccessibleObjectException e) {
      throw new RuntimeException(e);
    }

    try {
      KNOWN_COMMANDS_FIELD = SimpleCommandMap.class.getDeclaredField("knownCommands");
      KNOWN_COMMANDS_FIELD.setAccessible(true);
    } catch (final NoSuchFieldException | InaccessibleObjectException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns Bukkit's active command map.
   *
   * @return the server command map
   * @throws IllegalStateException if the command map cannot be retrieved
   */
  public static CommandMap getCommandMap() {
    if (commandMap == null) {
      try {
        final Object obj = COMMAND_MAP_FIELD.get(
            Bukkit.getServer().getPluginManager());
        if (obj == null || !(obj instanceof CommandMap)) {
          throw new IllegalStateException("CommandMap is not available");
        }
        CommandAPI.commandMap = (CommandMap) obj;
      } catch (final Exception e) {
        throw new IllegalStateException("Failed to retrieve CommandMap", e);
      }
    }
    return commandMap;
  }

  /**
   * Returns Bukkit's internal map of known commands.
   *
   * @return the known command map
   * @throws IllegalStateException if the known command map cannot be retrieved
   */
  @SuppressWarnings("unchecked")
  public static Map<String, Command> getKnownCommandMap() {
    if (knownCommandMap == null) {
      try {
        getCommandMap();
        final Object obj = KNOWN_COMMANDS_FIELD.get(commandMap);
        if (obj == null || !Map.class.isAssignableFrom(obj.getClass())) {
          throw new IllegalStateException("knownCommandsMap is not available");
        }
        CommandAPI.knownCommandMap = (Map<String, Command>) obj;
      } catch (final Exception e) {
        throw new IllegalStateException("Failed to retrieve knownCommandsMap", e);
      }
    }
    return knownCommandMap;
  }

  /**
   * Returns the namespace currently used for command registration.
   *
   * @return the command namespace
   */
  public static String getNamespace() {
    if (namespace == null || namespace.isEmpty()) {
      try {
        CommandAPI.namespace = ZenithProvider.instance().namespace();
      } catch (final Exception ignored) {
      }
    }
    return namespace;
  }

  /**
   * Sets and returns the default command namespace based on the active Zenith
   * plugin.
   *
   * @return the updated command namespace
   */
  public static String setNamespace() {
    CommandAPI.namespace = ZenithProvider.instance().namespace();
    return namespace;
  }

  /**
   * Returns the shared command handler used for simple command execution.
   *
   * @return the command handler
   */
  public static CommandHandler<CommandSender, BukkitCommandSource<CommandSender>> getHandler() {
    if (handler == null) {
      handler = new CommandHandler<CommandSender, BukkitCommandSource<CommandSender>>(s -> BukkitCommandSource.from(s));
    }
    return handler;
  }

  /**
   * Creates a new simple command builder for the given command key.
   *
   * @param key the command key or label
   * @return a new simple command builder
   * @throws InvalidCommandNameException if {@code key} is {@code null} or empty
   */
  public static SimpleCommand command(String key) {
    if (key == null || key.length() == 0) {
      throw new InvalidCommandNameException(key);
    }
    key = key.startsWith("/") ? key.substring(1) : key;
    return new SimpleCommand(
        new NamespacedKey(ZenithProvider.instance().namespace(), key));
  }

  /**
   * Creates a new simple command builder for the given namespaced key.
   *
   * @param key the command key
   * @return a new simple command builder
   */
  public static SimpleCommand command(final NamespacedKey key) {
    return new SimpleCommand(key);
  }

  /**
   * Creates a new subcommand builder with the given aliases.
   *
   * @param aliases the subcommand aliases
   * @return a new subcommand builder
   */
  public static SimpleSubCommand<CommandSender> subCommand(final String... aliases) {
    return new SimpleSubCommand<CommandSender>(aliases);
  }

  /**
   * Registers a Zenith simple command with the server command map.
   *
   * @param command the command to register
   * @return {@code true} if registration succeeds; {@code false} otherwise
   */
  public static boolean register(final SimpleCommand command) {
    try {
      if (command == null) {
        throw new IllegalArgumentException("Command cannot be null");
      }
      if (command.aliases().isEmpty()) {
        throw new IllegalArgumentException("At least one alias must be provided");
      }
      command.build();
      final InternalExecutor cmd = new InternalExecutor(command, false);
      return getCommandMap().register(cmd.getLabel(), cmd.namespace(), cmd);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to register a command: " + (command == null ? "null" : command.name().toString()));
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Registers a Bukkit command with the server command map.
   *
   * @param command   the command to register
   * @param namespace the namespace to register the command under, or {@code null}
   *                  to use the default namespace
   * @return {@code true} if registration succeeds; {@code false} otherwise
   */
  public static boolean register(final Command command, String namespace) {
    try {
      if (command == null) {
        throw new IllegalArgumentException("Command cannot be null");
      }
      if (command.getAliases().isEmpty()) {
        throw new IllegalArgumentException("At least one alias must be provided");
      }
      namespace = namespace == null || namespace.isEmpty() ? getNamespace() : namespace;
      return getCommandMap().register(command.getLabel(), namespace, command);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to register a command: " + (command == null ? "null" : command.getName()));
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Assigns a combined executor and tab completer to an existing plugin command.
   *
   * @param key      the command key to resolve
   * @param executor the executor and tab completer to assign
   * @return {@code true} if the command was updated successfully
   * @throws MissingCommandExecutorException if {@code executor} is {@code null}
   * @throws IllegalArgumentException        if no matching plugin command is
   *                                         found
   */
  public static boolean register(final String key, final TabExecutor executor) {
    if (executor == null) {
      throw new MissingCommandExecutorException(key);
    }
    final PluginCommand cmd = getPluginCommand(key);
    if (cmd == null) {
      throw new IllegalArgumentException("No command found with the key: " + key);
    }
    cmd.setExecutor(executor);
    cmd.setTabCompleter(executor);
    return true;
  }

  /**
   * Assigns an executor and optional tab completer to an existing plugin command.
   *
   * @param key          the command key to resolve
   * @param executor     the command executor to assign
   * @param tabCompleter the tab completer to assign, or {@code null} to leave it
   *                     unchanged
   * @return {@code true} if the command was updated successfully
   * @throws MissingCommandExecutorException if {@code executor} is {@code null}
   * @throws IllegalArgumentException        if no matching plugin command is
   *                                         found
   */
  public static boolean register(final String key, final CommandExecutor executor, final TabCompleter tabCompleter) {
    if (executor == null) {
      throw new MissingCommandExecutorException(key);
    }
    final PluginCommand cmd = getPluginCommand(key);
    if (cmd == null) {
      throw new IllegalArgumentException("No command found with the key: " + key);
    }
    cmd.setExecutor(executor);
    if (tabCompleter != null) {
      cmd.setTabCompleter(tabCompleter);
    }
    return true;
  }

  /**
   * Assigns a combined executor and tab completer to an existing plugin command.
   *
   * @param key      the namespaced command key to resolve
   * @param executor the executor and tab completer to assign
   * @return {@code true} if the command was updated successfully; {@code false}
   *         if the key is {@code null}
   */
  public static boolean register(final NamespacedKey key, final TabExecutor executor) {
    return key == null ? false : register(key.toString(), executor);
  }

  /**
   * Assigns an executor and optional tab completer to an existing plugin command.
   *
   * @param key          the namespaced command key to resolve
   * @param executor     the command executor to assign
   * @param tabCompleter the tab completer to assign, or {@code null} to leave it
   *                     unchanged
   * @return {@code true} if the command was updated successfully; {@code false}
   *         if the key is {@code null}
   */
  public static boolean register(final NamespacedKey key, final CommandExecutor executor,
      final TabCompleter tabCompleter) {
    return key == null ? false : register(key.toString(), executor, tabCompleter);
  }

  /**
   * Unregisters a command by key from the server command map.
   *
   * @param key the command key to unregister
   * @return {@code true} if the command was removed or not present; {@code false}
   *         on failure
   */
  public static boolean unregister(final String key) {
    try {
      final Command command = getCommand(key);
      if (command == null) {
        getKnownCommandMap().remove(key);
        return true;
      }
      final boolean result = command.unregister(getCommandMap());
      getKnownCommandMap().remove(key);
      return result;
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to unregister a command: " + key);
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Unregisters a command by namespaced key from the server command map.
   *
   * @param key the namespaced command key to unregister
   * @return {@code true} if the command was removed or not present; {@code false}
   *         on failure
   */
  public static boolean unregister(final NamespacedKey key) {
    return key == null ? true : unregister(key.toString());
  }

  /**
   * Unregisters a simple command and its aliases from the server command map.
   *
   * @param command the command to unregister
   * @return {@code true} if the command was removed or not present; {@code false}
   *         on failure
   */
  public static boolean unregister(final SimpleCommand command) {
    if (command == null) {
      return true;
    }
    try {
      final Command cmd = getCommand(command.key().toString());
      boolean result = true;
      if (cmd != null) {
        result = cmd.unregister(getCommandMap());
      }

      getKnownCommandMap().remove(command.key().toString());
      getKnownCommandMap().remove(command.name());
      final String ns = command.namespace();
      for (final String alias : command.aliases()) {
        getKnownCommandMap().remove(alias);
        if (ns != null && !ns.isEmpty()) {
          getKnownCommandMap().remove(toNamespaced(ns, alias));
        }
      }
      return result;
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to unregister a command: " + command.key().toString());
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Unregisters a Bukkit command and its aliases from the server command map.
   *
   * @param command   the command to unregister
   * @param namespace the namespace the command was registered under, or
   *                  {@code null} to use the default namespace
   * @return {@code true} if the command was removed or not present; {@code false}
   *         on failure
   */
  public static boolean unregister(final Command command, String namespace) {
    if (command == null) {
      return true;
    }
    namespace = namespace == null || namespace.isEmpty() ? getNamespace() : namespace;
    try {
      final boolean result = command.unregister(getCommandMap());
      getKnownCommandMap().remove(command.getLabel());
      if (namespace != null && !namespace.isEmpty()) {
        getKnownCommandMap().remove(toNamespaced(namespace, command.getLabel()));
      }
      for (final String alias : command.getAliases()) {
        getKnownCommandMap().remove(alias);
        if (namespace != null && !namespace.isEmpty()) {
          getKnownCommandMap().remove(toNamespaced(namespace, alias));
        }
      }
      return result;
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to unregister a command: " + command.getLabel());
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Unregisters all commands registered under the specified namespace.
   *
   * @param namespace the namespace to unregister
   * @return {@code true} if all matching commands were unregistered successfully;
   *         {@code false} otherwise
   */
  public static boolean unregisterAll(final String namespace) {
    if (namespace == null || namespace.isEmpty()) {
      return unregisterAll();
    }
    for (final Map.Entry<String, Command> entry : getKnownCommandMap().entrySet()) {
      if (entry.getKey().startsWith(namespace + ":")) {
        if (!CommandAPI.unregister(entry.getValue(), namespace)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Unregisters all commands from the server command map.
   *
   * @return {@code true} if the command map was cleared successfully;
   *         {@code false} otherwise
   */
  public static boolean unregisterAll() {
    try {
      getCommandMap().clearCommands();
      return true;
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to unregister all commands");
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Resolves a command by key from the server command map.
   *
   * @param key the command key to resolve
   * @return the matching command, or {@code null} if none is found
   */
  public static Command getCommand(final String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }
    return getCommandMap().getCommand(key);
  }

  /**
   * Resolves a registered simple command by key.
   *
   * @param key the command key to resolve
   * @return the matching simple command, or {@code null} if none is found
   */
  public static SimpleCommand getSimpleCommand(final String key) {
    final Command cmd = getCommand(key);
    if (cmd == null || !(cmd instanceof InternalExecutor)) {
      return null;
    }
    return ((InternalExecutor) cmd).getSimpleCommand();
  }

  /**
   * Resolves a plugin command by key.
   *
   * @param key the command key to resolve
   * @return the matching plugin command, or {@code null} if none is found
   */
  public static PluginCommand getPluginCommand(final String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }
    return Bukkit.getPluginCommand(key);
  }

  /**
   * Resolves a command by namespaced key from the server command map.
   *
   * @param key the namespaced command key to resolve
   * @return the matching command, or {@code null} if none is found
   */
  public static Command getCommand(final NamespacedKey key) {
    return key == null ? null : getCommand(key.toString());
  }

  /**
   * Resolves a registered simple command by namespaced key.
   *
   * @param key the namespaced command key to resolve
   * @return the matching simple command, or {@code null} if none is found
   */
  public static SimpleCommand getSimpleCommand(final NamespacedKey key) {
    return key == null ? null : getSimpleCommand(key.toString());
  }

  /**
   * Resolves a plugin command by namespaced key.
   *
   * @param key the namespaced command key to resolve
   * @return the matching plugin command, or {@code null} if none is found
   */
  public static PluginCommand getPluginCommand(final NamespacedKey key) {
    return key == null ? null : getPluginCommand(key.toString());
  }

  /**
   * Checks whether a simple command is currently registered.
   *
   * @param command the command to check
   * @return {@code true} if the command is registered; {@code false} otherwise
   */
  public static boolean isRegistered(final SimpleCommand command) {
    return command == null ? false : isRegistered(command.name().toString());
  }

  /**
   * Checks whether a command key is currently registered.
   *
   * @param key the namespaced command key to check
   * @return {@code true} if the command is registered; {@code false} otherwise
   */
  public static boolean isRegistered(final NamespacedKey key) {
    return key == null ? false : isRegistered(key.toString());
  }

  /**
   * Checks whether a command key is currently registered.
   *
   * @param key the command key to check
   * @return {@code true} if the command is registered; {@code false} otherwise
   */
  public static boolean isRegistered(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    return getCommandMap().getCommand(key) != null;
  }

  /**
   * Returns an unmodifiable view of all known commands.
   *
   * @return the known commands
   */
  public static Collection<Command> getCommands() {
    return Collections.unmodifiableCollection(getKnownCommandMap().values());
  }

  /**
   * Dispatches a simple command through the shared command handler.
   *
   * @param sender  the command sender, or {@code null} to use the console sender
   * @param command the command to execute
   * @param args    the command arguments
   * @return {@code true} if the command was handled; {@code false} otherwise
   */
  public static boolean dispatch(CommandSender sender, final SimpleCommand command, final String[] args) {
    if (sender == null) {
      sender = Bukkit.getConsoleSender();
    }
    if (!getHandler().shouldHandle(command, sender, command.name())) {
      return false;
    }
    getHandler().run(command, sender, args);
    return true;
  }

  /**
   * Dispatches a command by label using Bukkit's command execution flow.
   *
   * @param sender the command sender, or {@code null} to use the console sender
   * @param label  the command label to execute
   * @param args   the full argument array, including the label at index {@code 0}
   * @return {@code true} if the command executed successfully; {@code false} if
   *         no command was found
   * @throws CommandException if command execution fails
   */
  public static boolean dispatch(final CommandSender sender, final String label, final String[] args) {
    final Command target = getCommand(label);
    if (target == null) {
      return false;
    }

    try {
      return target.execute(sender == null ? Bukkit.getConsoleSender() : sender, label,
          Arrays.copyOfRange(args, 1, args.length));
    } catch (final CommandException ex) {
      throw ex;
    } catch (final Throwable ex) {
      throw new CommandException("Unhandled exception executing '" + label + "' in " + target, ex);
    }
  }

  /**
   * Dispatches a simple command from the console sender.
   *
   * @param command the command to execute
   * @param args    the command arguments
   * @return {@code true} if the command was handled; {@code false} otherwise
   */
  public static boolean dispatch(final SimpleCommand command, final String[] args) {
    return dispatch(Bukkit.getConsoleSender(), command, args);
  }

  /**
   * Dispatches a command by label from the console sender.
   *
   * @param command the command label to execute
   * @param args    the full argument array, including the label at index
   *                {@code 0}
   * @return {@code true} if the command executed successfully; {@code false} if
   *         no command was found
   */
  public static boolean dispatch(final String command, final String[] args) {
    return dispatch(Bukkit.getConsoleSender(), command, args);
  }

  /**
   * Dispatches a raw command line from the specified sender.
   *
   * @param sender      the command sender
   * @param commandLine the command line to parse and dispatch
   * @return {@code true} if the command executed successfully; {@code false}
   *         otherwise
   */
  public static boolean dispatch(final CommandSender sender, final String commandLine) {
    final String[] args = commandLine.split(" ");
    if (args.length == 0) {
      return false;
    }
    return dispatch(sender, args[0], args);
  }

  /**
   * Dispatches a raw command line from the console sender.
   *
   * @param commandLine the command line to parse and dispatch
   * @return {@code true} if the command executed successfully; {@code false}
   *         otherwise
   */
  public static boolean dispatch(final String commandLine) {
    return dispatch(Bukkit.getConsoleSender(), commandLine);
  }

  /**
   * Dispatches a simple command asynchronously.
   *
   * @param sender  the command sender
   * @param command the command to execute
   * @param args    the command arguments
   */
  public static void dispatchAsync(final CommandSender sender, final SimpleCommand command, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(sender, command, args);
    });
  }

  /**
   * Dispatches a simple command asynchronously from the console sender.
   *
   * @param command the command to execute
   * @param args    the command arguments
   */
  public static void dispatchAsync(final SimpleCommand command, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(Bukkit.getConsoleSender(), command, args);
    });
  }

  /**
   * Dispatches a command by label asynchronously.
   *
   * @param sender the command sender
   * @param label  the command label to execute
   * @param args   the full argument array, including the label at index {@code 0}
   */
  public static void dispatchAsync(final CommandSender sender, final String label, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(sender, label, args);
    });
  }

  /**
   * Dispatches a command by label asynchronously from the console sender.
   *
   * @param label the command label to execute
   * @param args  the full argument array, including the label at index {@code 0}
   */
  public static void dispatchAsync(final String label, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(Bukkit.getConsoleSender(), label, args);
    });
  }

  /**
   * Dispatches a raw command line asynchronously.
   *
   * @param sender      the command sender
   * @param commandLine the command line to parse and dispatch
   */
  public static void dispatchAsync(final CommandSender sender, final String commandLine) {
    GlobalScheduler.runAsync(() -> {
      dispatch(sender, commandLine);
    });
  }

  /**
   * Dispatches a raw command line asynchronously from the console sender.
   *
   * @param commandLine the command line to parse and dispatch
   */
  public static void dispatchAsync(final String commandLine) {
    GlobalScheduler.runAsync(() -> {
      dispatch(Bukkit.getConsoleSender(), commandLine);
    });
  }

  private static String toNamespaced(final String namespace, final String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }
    if (key.startsWith(namespace + ":")) {
      return key;
    }
    return namespace + ":" + key;
  }

  /**
   * Broadcasts an administrative command feedback message to eligible recipients.
   *
   * @param source       the sender that triggered the command
   * @param message      the message to broadcast
   * @param sendToSource {@code true} to also send the message back to the source
   *                     when applicable
   */
  public static void broadcastCommandMessage(final CommandSender source, final String message,
      final boolean sendToSource) {
    final String colored = MessageFormatter.format("&7&o[" + source.getName() + ": " + message + "&7&o]");

    if (source instanceof final BlockCommandSender blockSender) {
      if (!blockSender.getBlock().getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
        GlobalLogger.info(colored);
        return;
      }
    } else if (source instanceof final CommandMinecart cartSender) {
      if (!cartSender.getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
        GlobalLogger.info(colored);
        return;
      }
    }

    if (sendToSource && !(source instanceof ConsoleCommandSender)) {
      source.sendMessage(colored);
    }
    for (final Permissible user : Bukkit.getPluginManager().getPermissionSubscriptions("bukkit.broadcast.admin")) {
      if (user instanceof CommandSender && user.hasPermission("bukkit.broadcast.admin")) {
        final CommandSender target = (CommandSender) user;

        if (target != source || user instanceof ConsoleCommandSender) {
          target.sendMessage(colored);
        }
      }
    }
  }
}
