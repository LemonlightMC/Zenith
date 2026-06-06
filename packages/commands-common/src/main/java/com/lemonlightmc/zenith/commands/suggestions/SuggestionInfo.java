package com.lemonlightmc.zenith.commands.suggestions;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandArguments;

public record SuggestionInfo<S>(
        CommandSource<S> source,
        CommandArguments args,
        String currentInput) {
}
