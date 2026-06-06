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
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.SimplePluginManager;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.exceptions.InvalidCommandNameException;
import com.lemonlightmc.zenith.commands.exceptions.MissingCommandExecutorException;
import com.lemonlightmc.zenith.commands.executors.CommandHandler;
import com.lemonlightmc.zenith.commands.executors.InternalExecutor;
import com.lemonlightmc.zenith.messages.Logger;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;

public class CommandAPI {
  private static final Field COMMAND_MAP_FIELD;
  private static final Field KNOWN_COMMANDS_FIELD;
  private volatile static CommandMap commandMap;
  private volatile static Map<String, Command> knownCommandMap;

  public volatile static String namespace;
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

  @SuppressWarnings("unchecked")
  public static Map<String, Command> getKnownCommandMap() {
    if (knownCommandMap == null) {
      try {
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

  public static String getNamespace() {
    return namespace;
  }

  public static String setNamespace() {
    namespace = ZenithProvider.getInstance().getKey();
    return namespace;
  }

  public static CommandHandler<CommandSender, BukkitCommandSource<CommandSender>> getHandler() {
    if (handler == null) {
      handler = new CommandHandler<CommandSender, BukkitCommandSource<CommandSender>>(s -> BukkitCommandSource.from(s));
    }
    return handler;
  }

  public static SimpleCommand command(String key) {
    if (key == null || key.length() == 0) {
      throw new InvalidCommandNameException(key);
    }
    key = key.startsWith("/") ? key.substring(1) : key;
    return new SimpleCommand(
        new NamespacedKey(ZenithProvider.getInstance().getKey(), key));
  }

  public static SimpleCommand command(final NamespacedKey key) {
    return new SimpleCommand(key);
  }

  public static SimpleSubCommand<CommandSender> subCommand(final String... aliases) {
    return new SimpleSubCommand<CommandSender>(aliases);
  }

  public static boolean register(final SimpleCommand command) {
    try {
      if (command == null) {
        throw new IllegalArgumentException("Command cannot be null");
      }
      if (command.getAliases().size() == 0) {
        throw new IllegalArgumentException("At least one alias must be provided");
      }
      if (!command.hasExecutors() && (!command.getSubcommands().isEmpty() || command.hasArguments())) {
        throw new MissingCommandExecutorException(command.getName().toString());
      }
      command.build();
      final InternalExecutor cmd = new InternalExecutor(command, true);
      return getCommandMap().register(cmd.getLabel(), cmd.getNamespace(), cmd);
    } catch (final Exception e) {
      Logger.warn("Failed to register a command: " + (command == null ? "null" : command.getName().toString()));
      e.printStackTrace();
      return false;
    }
  }

  public static boolean register(final Command command, String namespace) {
    try {
      if (command == null) {
        throw new IllegalArgumentException("Command cannot be null");
      }
      if (command.getAliases().size() == 0) {
        throw new IllegalArgumentException("At least one alias must be provided");
      }
      namespace = namespace == null || namespace.isEmpty() ? getNamespace() : namespace;
      return getCommandMap().register(command.getLabel(), namespace, command);
    } catch (final Exception e) {
      Logger.warn("Failed to register a command: " + (command == null ? "null" : command.getName()));
      e.printStackTrace();
      return false;
    }
  }

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

  public static boolean register(final NamespacedKey key, final TabExecutor executor) {
    return key == null ? false : register(key.toString(), executor);
  }

  public static boolean register(final NamespacedKey key, final CommandExecutor executor,
      final TabCompleter tabCompleter) {
    return key == null ? false : register(key.toString(), executor, tabCompleter);
  }

  public static boolean unregister(final String key) {
    try {
      final Command command = getCommand(key);
      if (command == null) {
        return true;
      }
      getKnownCommandMap().remove(key);
      command.unregister(getCommandMap());
      return true;
    } catch (final Exception e) {
      Logger.warn("Failed to unregister a command: " + key);
      e.printStackTrace();
      return false;
    }
  }

  public static boolean unregister(final NamespacedKey key) {
    return key == null ? true : unregister(key.toString());
  }

  public static boolean unregister(final SimpleCommand command) {
    if (command == null) {
      return true;
    }
    try {
      getKnownCommandMap().remove(command.getName().toString());
      getKnownCommandMap().remove(command.getKey());
      for (final String alias : command.getAliases()) {
        getKnownCommandMap().remove(alias);
        getKnownCommandMap().remove(toNamespaced(getNamespace(), alias));
      }
      final Command cmd = getCommand(command.getName().toString());
      if (cmd == null) {
        return true;
      }
      cmd.unregister(getCommandMap());
      return true;
    } catch (final Exception e) {
      Logger.warn("Failed to unregister a command: " + command.getName().toString());
      e.printStackTrace();
      return false;
    }
  }

  public static boolean unregister(final Command command, String namespace) {
    if (command == null) {
      return true;
    }
    namespace = namespace == null || namespace.isEmpty() ? getNamespace() : namespace;
    try {
      getKnownCommandMap().remove(command.getLabel());
      getKnownCommandMap().remove(toNamespaced(namespace, command.getLabel()));
      for (final String alias : command.getAliases()) {
        getKnownCommandMap().remove(alias);
        getKnownCommandMap().remove(toNamespaced(namespace, alias));
      }
      command.unregister(getCommandMap());
      return true;
    } catch (final Exception e) {
      Logger.warn("Failed to unregister a command: " + command.getLabel());
      e.printStackTrace();
      return false;
    }
  }

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

  public static boolean unregisterAll() {
    try {
      getCommandMap().clearCommands();
      return true;
    } catch (final Exception e) {
      Logger.warn("Failed to unregister all commands");
      e.printStackTrace();
      return false;
    }
  }

  public static Command getCommand(final String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }
    return getCommandMap().getCommand(key);
  }

  public static SimpleCommand getSimpleCommand(final String key) {
    final Command cmd = getCommand(key);
    if (cmd == null || !(cmd instanceof InternalExecutor)) {
      return null;
    }
    return ((InternalExecutor) cmd).getSimpleCommand();
  }

  public static PluginCommand getPluginCommand(final String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }
    return Bukkit.getPluginCommand(key);
  }

  public static Command getCommand(final NamespacedKey key) {
    return key == null ? null : getCommand(key.toString());
  }

  public static SimpleCommand getSimpleCommand(final NamespacedKey key) {
    return key == null ? null : getSimpleCommand(key.toString());
  }

  public static PluginCommand getPluginCommand(final NamespacedKey key) {
    return key == null ? null : getPluginCommand(key.toString());
  }

  public static boolean isRegistered(final SimpleCommand command) {
    return command == null ? false : isRegistered(command.getName().toString());
  }

  public static boolean isRegistered(final NamespacedKey key) {
    return key == null ? false : isRegistered(key.toString());
  }

  public static boolean isRegistered(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    return getCommandMap().getCommand(key) != null;
  }

  public static Collection<Command> getCommands() {
    return Collections.unmodifiableCollection(getKnownCommandMap().values());
  }

  public static boolean dispatch(CommandSender sender, final SimpleCommand command, final String[] args) {
    if (sender == null) {
      sender = Bukkit.getConsoleSender();
    }
    if (!getHandler().shouldHandle(command, sender, command.getKey())) {
      return false;
    }
    getHandler().run(command, sender, args);
    return true;
  }

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

  public static boolean dispatch(final SimpleCommand command, final String[] args) {
    return dispatch(Bukkit.getConsoleSender(), command, args);
  }

  public static boolean dispatch(final String command, final String[] args) {
    return dispatch(Bukkit.getConsoleSender(), command, args);
  }

  public static boolean dispatch(final CommandSender sender, final String commandLine) {
    final String[] args = commandLine.split(" ");
    if (args.length == 0) {
      return false;
    }
    return dispatch(sender, args[0], args);
  }

  public static boolean dispatch(final String commandLine) {
    return dispatch(Bukkit.getConsoleSender(), commandLine);
  }

  public static void dispatchAsync(final CommandSender sender, final SimpleCommand command, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(sender, command, args);
    });
  }

  public static void dispatchAsync(final SimpleCommand command, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(Bukkit.getConsoleSender(), command, args);
    });
  }

  public static void dispatchAsync(final CommandSender sender, final String label, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(sender, label, args);
    });
  }

  public static void dispatchAsync(final String label, final String[] args) {
    GlobalScheduler.runAsync(() -> {
      dispatch(Bukkit.getConsoleSender(), label, args);
    });
  }

  public static void dispatchAsync(final CommandSender sender, final String commandLine) {
    GlobalScheduler.runAsync(() -> {
      dispatch(sender, commandLine);
    });
  }

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

  public static void broadcastCommandMessage(final CommandSender source, final String message,
      final boolean sendToSource) {
    final String colored = MessageFormatter.format("&7&o[" + source.getName() + ": " + message + "&7&o]");

    if (source instanceof final BlockCommandSender blockSender) {
      if (!blockSender.getBlock().getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
        Logger.info(colored);
        return;
      }
    } else if (source instanceof final CommandMinecart cartSender) {
      if (!cartSender.getWorld().getGameRuleValue(GameRule.COMMAND_BLOCK_OUTPUT)) {
        Logger.info(colored);
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
