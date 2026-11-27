package com.lemonlightmc.zenith.commands.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.ZenithConfig;
import com.lemonlightmc.zenith.ZenithProvider;

import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.commands.SimpleSubCommand;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.argumentsbase.ParsedArgument;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.executors.Executors.ExecutorType;
import com.lemonlightmc.zenith.commands.suggestions.SuggestionInfo;
import com.lemonlightmc.zenith.commands.suggestions.Suggestions;

public class CommandHandler<S extends CommandSender> {
  public static final int DEFAULT_MAX_TABCOMPLETIONS = 128;

  private static volatile Object vault;
  private static volatile boolean vaultAttempted;

  private final CommandContextFactory<S> sourceFactory;

  public CommandHandler(final CommandContextFactory<S> sourceFactory) {
    this.sourceFactory = sourceFactory;
  }

  public interface CommandContextFactory<S extends CommandSender> {
    public CommandContext<S> create(final RootCommand<?, S> cmd, String label, final S sender);
  }

  private static Object getVault() {
    final Object current = vault;
    if (current != null) {
      return current;
    }
    return discoverVault();
  }

  private static synchronized Object discoverVault() {
    if (vault != null || vaultAttempted) {
      return vault;
    }
    vaultAttempted = true;
    try {
      final Class<?> cls = Class.forName("com.lemonlightmc.zenith.integrations.vault.SimpleVault");
      final boolean hasVault = (boolean) cls.getMethod("hasVault").invoke(null);
      if (hasVault) {
        final Object created = cls.getMethod("create").invoke(null);
        final Object economy = cls.getMethod("getEconomy").invoke(created);
        if (economy != null) {
          vault = created;
        }
      }
    } catch (final Throwable ignored) {
      // Vault/Integrations not available
    }
    return vault;
  }

  public static boolean isEconomyAvailable() {
    final Object v = getVault();
    if (v == null) {
      return false;
    }
    try {
      return v.getClass().getMethod("getEconomy").invoke(v) != null;
    } catch (final Throwable ignored) {
      return false;
    }
  }

  public static boolean hasFunds(final Player player, final double amount) {
    final Object v = getVault();
    if (v == null || player == null) {
      return false;
    }
    try {
      final Object economy = v.getClass().getMethod("getEconomy").invoke(v);
      if (economy == null) {
        return false;
      }
      return (boolean) v.getClass().getMethod("checkMoney", Player.class, double.class).invoke(v, player, amount);
    } catch (final Throwable ignored) {
      return false;
    }
  }

  public static double getBalance(final Player player) {
    final Object v = getVault();
    if (v == null || player == null) {
      return 0.0D;
    }
    try {
      final Object economy = v.getClass().getMethod("getEconomy").invoke(v);
      if (economy == null) {
        return 0.0D;
      }
      final Object money = v.getClass().getMethod("getMoney", org.bukkit.OfflinePlayer.class).invoke(v, player);
      if (money instanceof final java.math.BigDecimal bd) {
        return bd.doubleValue();
      }
      return 0.0D;
    } catch (final Throwable ignored) {
      return 0.0D;
    }
  }

  public static boolean withdraw(final Player player, final double amount) {
    final Object v = getVault();
    if (v == null || player == null) {
      return false;
    }
    try {
      final Object economy = v.getClass().getMethod("getEconomy").invoke(v);
      if (economy == null) {
        return false;
      }
      final boolean has = (boolean) v.getClass().getMethod("checkMoney", Player.class, double.class)
          .invoke(v, player, amount);
      if (!has) {
        return false;
      }
      final Object response = v.getClass().getMethod("removeMoney", Player.class, double.class)
          .invoke(v, player, amount);
      if (response == null) {
        return false;
      }
      return (boolean) response.getClass().getMethod("transactionSuccess").invoke(response);
    } catch (final Throwable ignored) {
      return false;
    }
  }

  private static int maxTabCompletions() {
    try {
      if (!ZenithProvider.hasBackend()) {
        return DEFAULT_MAX_TABCOMPLETIONS;
      }
      final ZenithConfig config = ZenithProvider.config();
      final ZenithConfig.Commands commands = config == null ? null : config.commands;
      if (commands == null || commands.maxTabCompletions <= 0) {
        return DEFAULT_MAX_TABCOMPLETIONS;
      }
      return commands.maxTabCompletions;
    } catch (final Throwable ignored) {
      return DEFAULT_MAX_TABCOMPLETIONS;
    }
  }

  private static String requirementFailedMessage() {
    try {
      if (!ZenithProvider.hasBackend()) {
        return null;
      }
      final ZenithConfig config = ZenithProvider.config();
      final ZenithConfig.Commands commands = config == null ? null : config.commands;
      if (commands == null || commands.requirementFailedMessage == null
          || commands.requirementFailedMessage.isBlank()) {
        return null;
      }
      return commands.requirementFailedMessage;
    } catch (final Throwable ignored) {
      return null;
    }
  }

  public CommandContext<S> createCommandSource(final RootCommand<?, S> cmd, final String label, final S sender) {
    try {
      return sourceFactory.create(cmd, label, sender);
    } catch (final Exception e) {
      throw new CommandException("Failed to create CommandContext for Sender " + sender);
    }
  }

  public void run(final RootCommand<?, S> cmd, final String label, final S sender, final String[] args) {
    try {
      final CommandContext<S> source = createCommandSource(cmd, label, sender);
      final CommandArguments cmdArgs = parse(cmd, source, args == null ? new String[0] : args);
      if (cmdArgs == null) {
        return;
      }
      cmd.run(source, cmdArgs, 0, getExecutorType(sender));
    } catch (final CommandException ex) {
      throw ex;
    } catch (final Throwable ex) {
      throw new CommandException(
          "Exception while executing command '" +
              (cmd == null ? "null" : cmd.name()) +
              "' in plugin " +
              ZenithProvider.instance().getFullName(),
          ex);
    }
  }

  public List<String> tabComplete(
      final RootCommand<?, S> cmd,
      final String label,
      final S sender,
      String[] args) {
    if (args == null) {
      args = new String[0];
    }
    try {
      final CommandContext<S> source = createCommandSource(cmd, label, sender);
      if (!cmd.checkRequirements(source)) {
        return List.of();
      }
      final CommandArguments cmdArgs = parse(cmd, source, args);
      if (cmdArgs == null) {
        return List.of();
      }
      final String token = args.length > 0 ? args[args.length - 1] : "";
      final SuggestionInfo<S> info = new SuggestionInfo<S>(source, cmdArgs, token);

      final List<Suggestions<S>> temp = cmd.tabComplete(info, 0);
      if (temp == null) {
        return List.of();
      }
      final List<String> tabCompletions = new ArrayList<>();
      for (final Suggestions<S> suggestion : temp) {
        if (suggestion != null) {
          final Collection<String> list = suggestion.suggest(info);
          if (list != null && !list.isEmpty()) {
            tabCompletions.addAll(list);
          }
        }
      }
      tabCompletions.removeIf(s -> s == null || s.isEmpty() || !startsWithIgnoreCase(s, token));
      final int maxTabCompletions = maxTabCompletions();
      if (tabCompletions.size() > maxTabCompletions) {
        return new ArrayList<>(tabCompletions.subList(0, maxTabCompletions));
      } else {
        return tabCompletions;
      }

    } catch (final Throwable ex) {
      final StringBuilder message = new StringBuilder("Unhandled exception during tab completion for command '/");
      message
          .append(cmd == null ? "null" : cmd.name())
          .append(' ')
          .append(String.join(" ", args))
          .append("' in plugin ")
          .append(ZenithProvider.instance().getFullName());
      throw new CommandException(message.toString(), ex);
    }
  }

  private CommandArguments parse(AbstractCommand<?, S> thisCmd, final CommandContext<S> source,
      final String[] args) {
    final StringReader reader = new StringReader(String.join(" ", args));
    int i = 0;
    while (i++ < args.length) {
      final SimpleSubCommand<S> sub = thisCmd.getSubcommand(args[i]);
      if (sub == null) {
        break;
      }
      if (!sub.checkRequirements(source)) {
        final String requirementFailedMessage = requirementFailedMessage();
        if (requirementFailedMessage != null) {
          source.sendMessage(requirementFailedMessage);
        }
        return null;
      }

      thisCmd = sub;
    }

    final Map<String, ParsedArgument> parsedArgs = new LinkedHashMap<>();
    for (final Argument<?, ?, S> arg : thisCmd.arguments()) {
      if (!arg.checkRequirements(source)) {
        final String requirementFailedMessage = requirementFailedMessage();
        if (requirementFailedMessage != null) {
          source.sendMessage(requirementFailedMessage);
        }
        return null;
      }

      reader.skipWhitespace();
      final Object value = _parseArg(reader, source, arg);
      parsedArgs.put(arg.name(), new ParsedArgument(arg.name(), reader.getLastRead(), value));
    }
    return new CommandArguments(parsedArgs, reader.getString());
  }

  private Object _parseArg(final StringReader reader, final CommandContext<S> source,
      final Argument<?, ?, S> arg) {
    reader.point();
    try {
      final Object value = arg.parseArgument(source, reader, arg.name());
      reader.revokePoint();
      return value;
    } catch (final CommandSyntaxException e) {
      source.sendError(e);
      reader.resetCursor();
    } catch (final Exception e) {
      source.sendError(arg.createError(reader, reader.getLastRead()));
      reader.resetCursor();
    }
    return null;
  }

  private static boolean startsWithIgnoreCase(
      final String string,
      final String prefix) {
    if (prefix == null || prefix.length() == 0)
      return true;
    if (string == null || string.length() == 0)
      return false;
    if (string.length() < prefix.length()) {
      return false;
    }
    return string.regionMatches(true, 0, prefix, 0, prefix.length());
  }

  public boolean shouldHandle(final RootCommand<?, S> cmd, final S sender, final String label) {
    if (sender == null || cmd == null) {
      return false;
    }
    if (!ZenithProvider.instance().isEnabled()) {
      GlobalLogger.warn(
          "Cannot execute command '" +
              cmd.name() +
              "' in plugin " +
              ZenithProvider.instance().getDescription().getFullName() +
              " - plugin is disabled.");
      return false;
    }
    if (label == null || label.isBlank()) {
      return false;
    }
    final String normalized = StringUtils.lowercase(label);
    final String name = cmd.name() == null ? null : StringUtils.lowercase(cmd.name());
    return (name != null && name.equals(normalized))
        || cmd.aliases().contains(normalized)
        || cmd.aliases().contains(label);
  }

  /**
   * Resolves the executor type for a command sender.
   *
   * @param sender the command sender to inspect
   * @return the matching executor type, or {@link ExecutorType#ALL} if unknown or
   *         {@code null}
   */
  public static ExecutorType getExecutorType(final CommandSender sender) {
    if (sender == null) {
      return ExecutorType.ALL;
    } else if (sender instanceof Player) {
      return ExecutorType.PLAYER;
    } else if (sender instanceof Entity) {
      return ExecutorType.ENTITY;
    } else if (sender instanceof ConsoleCommandSender) {
      return ExecutorType.CONSOLE;
    } else if (sender instanceof BlockCommandSender) {
      return ExecutorType.BLOCK;
    } else if (sender instanceof ProxiedCommandSender) {
      return ExecutorType.PROXY;
    } else if (sender instanceof RemoteConsoleCommandSender) {
      return ExecutorType.REMOTE;
    }
    return ExecutorType.ALL;
  }
}
