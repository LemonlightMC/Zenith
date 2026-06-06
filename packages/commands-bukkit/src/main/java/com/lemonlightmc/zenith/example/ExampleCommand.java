package com.lemonlightmc.zenith.example;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.SimpleCommand;
import com.lemonlightmc.zenith.commands.arguments.NumberArguments.*;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.exceptions.CommandException;
import com.lemonlightmc.zenith.commands.suggestions.SuggestionInfo;
import com.lemonlightmc.zenith.commands.suggestions.Suggestions;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ExampleCommand extends SimpleCommand {

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

  public static void executes2(final CommandSource<Player> sender, final CommandArguments args)
      throws CommandException {
  }

  public static void executesConsole2(
      final CommandSource<ConsoleCommandSender> sender,
      final CommandArguments args) throws CommandException {
  }
}
