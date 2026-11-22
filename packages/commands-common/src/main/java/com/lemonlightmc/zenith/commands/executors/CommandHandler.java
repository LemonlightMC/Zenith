package com.lemonlightmc.zenith.commands.executors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.SimpleSubCommand;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.argumentsbase.ParsedArgument;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.suggestions.SuggestionInfo;
import com.lemonlightmc.zenith.commands.suggestions.Suggestions;

public class CommandHandler<S, C extends CommandSource<S>> {
  public static final int MAX_TABCOMPLETIONS = 512;

  private final Function<S, C> sourceFactory;

  public CommandHandler(final Function<S, C> sourceFactory) {
    this.sourceFactory = sourceFactory;
  }

  public C createCommandSource(final S sender) {
    try {
      return sourceFactory.apply(sender);
    } catch (final Exception e) {
      throw new CommandException("Failed to create CommandSource for Sender " + sender);
    }
  }

  public void run(final RootCommand<?, S> cmd, final S sender, final String[] args) {
    try {
      final C source = createCommandSource(sender);
      final CommandArguments cmdArgs = parse(cmd, source, args == null ? new String[0] : args);
      if (cmdArgs == null) {
        return;
      }
      final ExecutionInfo<S> info = new ExecutionInfo<S>(source, cmdArgs);
      cmd.run(info, 0);
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
      final C source = createCommandSource(sender);
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

  private CommandArguments parse(final RootCommand<?, S> cmd, final CommandSource<S> source,
      final String[] args) {
    final StringReader reader = new StringReader(String.join(" ", args));
    return _parseArguments(cmd, reader, source, args);
  }

  private CommandArguments _parseArguments(AbstractCommand<?, S> thisCmd,
      final StringReader reader,
      final CommandSource<S> source, final String[] args) {
    int i = 0;
    boolean hadSubcommand = true;
    while (hadSubcommand && args.length > i && thisCmd.hasSubcommands()) {
      hadSubcommand = false;
      for (final SimpleSubCommand<S> sub : thisCmd.subcommands().values()) {
        if (_isSubCommand(args[i], sub)) {
          if (!sub.checkRequirements(source)) {
            return null;
          }
          thisCmd = sub;
          i++;
          hadSubcommand = true;
          break;
        }
      }
    }

    final Map<String, ParsedArgument> parsedArgs = new LinkedHashMap<>();
    for (final Argument<?, ?, S> arg : thisCmd.arguments()) {
      if (!arg.checkRequirements(source)) {
        return null;
      }
      reader.skipWhitespace();
      final Object value = _parseArg(reader, source, arg);
      parsedArgs.put(arg.getName(), new ParsedArgument(arg.getName(), reader.getLastRead(), value));
    }
    return new CommandArguments(parsedArgs, reader.getString());
  }

  private Object _parseArg(final StringReader reader, final CommandSource<S> source,
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

  private boolean _isSubCommand(final String arg, final AbstractCommand<?, S> sub) {
    if (arg == null) {
      return false;
    }
    final String key = arg.toLowerCase(Locale.ROOT);
    return sub.aliases().contains(key) || sub.aliases().contains(arg);
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

}
