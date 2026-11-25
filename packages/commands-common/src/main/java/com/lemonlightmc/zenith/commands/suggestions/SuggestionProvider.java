package com.lemonlightmc.zenith.commands.suggestions;

import java.util.List;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface SuggestionProvider<S extends CommandSender> {

  public List<String> getSuggestions(SuggestionInfo<S> info);
}
