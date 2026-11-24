package com.lemonlightmc.zenith.commands.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
import com.lemonlightmc.zenith.actions.ExecutionContext;

public class CommandHandler<S extends CommandSender> {
  public static final int MAX_TABCOMPLETIONS = 128;

  public final String requirementFailedMessage; // TODO: Localize
  private final Function<S, ExecutionContext<S>> sourceFactory;

  public CommandHandler(final Function<S, ExecutionContext<S>> sourceFactory, final String requirementFailedMessage) {
    this.sourceFactory = sourceFactory;
    this.requirementFailedMessage = requirementFailedMessage;
  }

  public ExecutionContext<S> createCommandSource(final S sender) {
    try {
      return sourceFactory.apply(sender);
    } catch (final Exception e) {
      throw new CommandException("Failed to create ExecutionContext for Sender " + sender);
    }
  }

  public void run(final RootCommand<?, S> cmd, final S sender, final String[] args) {
    try {
      final ExecutionContext<S> source = createCommandSource(sender);
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
      final S sender,
      String[] args) {
    if (args == null) {
      args = new String[0];
    }
    try {
      final ExecutionContext<S> source = createCommandSource(sender);
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
      if (tabCompletions.size() > MAX_TABCOMPLETIONS) {
        return new ArrayList<>(tabCompletions.subList(0, MAX_TABCOMPLETIONS));
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

  private CommandArguments parse(AbstractCommand<?, S> thisCmd, final ExecutionContext<S> source,
      final String[] args) {
    final StringReader reader = new StringReader(String.join(" ", args));
    int i = 0;
    while (i++ < args.length) {
      final SimpleSubCommand<S> sub = thisCmd.getSubcommand(args[i]);
      if (sub == null) {
        break;
      }
      if (!sub.checkRequirements(source)) {
        source.sendMessage(requirementFailedMessage);
        return null;
      }
      thisCmd = sub;
    }

    final Map<String, ParsedArgument> parsedArgs = new LinkedHashMap<>();
    for (final Argument<?, ?, S> arg : thisCmd.arguments()) {
      if (!arg.checkRequirements(source)) {
        source.sendMessage(requirementFailedMessage);
        return null;
      }
      reader.skipWhitespace();
      final Object value = _parseArg(reader, source, arg);
      parsedArgs.put(arg.getName(), new ParsedArgument(arg.getName(), reader.getLastRead(), value));
    }
    return new CommandArguments(parsedArgs, reader.getString());
  }

  private Object _parseArg(final StringReader reader, final ExecutionContext<S> source,
      final Argument<?, ?, S> arg) {
    reader.point();
    try {
      final Object value = arg.parseArgument(source, reader, arg.getName());
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
