package com.lemonlightmc.zenith.commands.suggestions;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.exceptions.CommandException;

public abstract class BukkitSuggestions implements Suggestions<CommandSender> {

  public static Suggestions<CommandSender> empty() {
    return info -> List.of();
  }

  public static Suggestions<CommandSender> from(final String... strings) {
    return info -> List.of(strings);
  }

  public static Suggestions<CommandSender> from(final Collection<String> strings) {
    return info -> List.copyOf(strings);
  }

  public static Suggestions<CommandSender> from(
      final Function<SuggestionInfo<CommandSender>, Collection<String>> suggestions) {
    return info -> List.copyOf(suggestions.apply(info));
  }

  public static Suggestions<CommandSender> from(
      final Supplier<Collection<String>> suggestions) {
    return info -> List.copyOf(suggestions.get());
  }

  public static Suggestions<CommandSender> from(
      final Suggestions<CommandSender> suggestions) {
    return suggestions;
  }

  public static Suggestions<CommandSender> fromAsync(
      final CompletableFuture<Collection<String>> suggestions) {
    return (info) -> {
      try {
        return List.copyOf(suggestions.get());
      } catch (final Exception e) {
        return null;
      }
    };
  }

  public static Suggestions<CommandSender> fromAsync(
      final Function<SuggestionInfo<CommandSender>, CompletableFuture<Collection<String>>> suggestions) {
    return (info) -> {
      try {
        return List.copyOf(suggestions.apply(info).get());
      } catch (final Exception e) {
        return null;
      }
    };
  }

  public static Suggestions<CommandSender> fromProvider(
      final SuggestionProvider<CommandSender> provider) {
    return provider == null ? null : info -> provider.getSuggestions(info);
  }

  @SuppressWarnings("unchecked")
  public static Suggestions<CommandSender> fromProvider(final SuggestionProvider<CommandSender>... providers) {
    if (providers == null || providers.length == 0) {
      return null;
    }
    return info -> {
      final List<String> list = new ArrayList<>();
      for (final SuggestionProvider<CommandSender> provider : providers) {
        list.addAll(provider.getSuggestions(info));
      }
      return list;
    };
  }

  public static Suggestions<CommandSender> fromProvider(final Collection<SuggestionProvider<CommandSender>> providers) {
    if (providers == null || providers.isEmpty()) {
      return null;
    }
    return info -> {
      final List<String> list = new ArrayList<>();
      for (final SuggestionProvider<CommandSender> provider : providers) {
        list.addAll(provider.getSuggestions(info));
      }
      return list;
    };
  }

  @SuppressWarnings("unchecked")
  public static Suggestions<CommandSender> fromProvider(final Class<?> provider) {
    if (provider == null) {
      return null;
    }
    return info -> {
      try {
        return (List<String>) provider.getMethod("getSuggestions", SuggestionInfo.class).invoke(null, info);
      } catch (final Exception e) {
        return null;
      }
    };
  }

  public static Suggestions<CommandSender> fromProviderAsync(
      final CompletableFuture<SuggestionProvider<CommandSender>> future) {
    return (info) -> {
      try {
        final SuggestionProvider<CommandSender> provider = future.get();
        return provider == null ? null : provider.getSuggestions(info);
      } catch (final Exception e) {
        return List.of();
      }
    };
  }

  @Override
  public abstract Collection<String> suggest(SuggestionInfo<CommandSender> info) throws CommandException;
}
