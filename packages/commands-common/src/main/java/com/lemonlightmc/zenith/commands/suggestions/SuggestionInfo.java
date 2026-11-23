package com.lemonlightmc.zenith.commands.suggestions;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.actions.ConditionContext;

public record SuggestionInfo<S extends CommandSender>(
        ConditionContext<S> source,
        CommandArguments args,
        String currentInput) {
}
