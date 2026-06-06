package com.lemonlightmc.zenith.commands.argumentsbase;

public record ParsedArgument(
        String name,
        String raw,
        Object value) {
}
