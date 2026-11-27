package com.lemonlightmc.zenith.example;

import com.lemonlightmc.zenith.commands.SimpleCommand;
import com.lemonlightmc.zenith.commands.arguments.NumberArguments.*;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.executors.CommandContext;
import com.lemonlightmc.zenith.commands.suggestions.SuggestionInfo;
import com.lemonlightmc.zenith.commands.suggestions.Suggestions;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Example command demonstrating basic command registration and executor wiring.
 */
public class ExampleCommand extends SimpleCommand {

  /**
   * Creates and configures the example command.
   */
  public ExampleCommand() {
    super(NamespacedKey.fromString("moreplugins:example"));
    withAliases("example2");
    withShortDescription("An example command");
    withFullDescription("An example command that does random stuff!");
    withUsage("&cUsage: /example <random|player>");
    withPermissions("moreplugins.example");
    // withHelp(List.of("&e/example &7- &fAn example command"));
    withArguments(new DoubleArgument("amount").withSuggestions((final SuggestionInfo<CommandSender> info) -> {
      return List.of("64", "128");
    }));
    withArguments(new DoubleArgument("amount2").withSuggestions("1", "2", "3"));
    DoubleArgument amount3 = new DoubleArgument("amount3").withSuggestions(Suggestions.<CommandSender>from("1", "2"));
    withArguments(amount3);
    executes(ExampleCommand::executes2);
    executesConsole(ExampleCommand::executesConsole2);
  }

  /**
   * Handles command execution for player senders.
   *
   * @param sender the player command source
   * @param args   the parsed command arguments
   * @throws CommandException if command execution fails
   */
  public static void executes2(final CommandContext<Player> sender, final CommandArguments args)
      throws CommandException {
  }

  /**
   * Handles command execution for console senders.
   *
   * @param sender the console command source
   * @param args   the parsed command arguments
   * @throws CommandException if command execution fails
   */
  public static void executesConsole2(
      final CommandContext<ConsoleCommandSender> sender,
      final CommandArguments args) throws CommandException {
  }
}
