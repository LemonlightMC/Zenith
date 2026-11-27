package com.lemonlightmc.zenith.commands.suggestions;

import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;
import com.lemonlightmc.zenith.commands.executors.CommandContext;

public record SuggestionInfo<S>(
                CommandContext<S> source,
                CommandArguments args,
                String currentInput) {
}
