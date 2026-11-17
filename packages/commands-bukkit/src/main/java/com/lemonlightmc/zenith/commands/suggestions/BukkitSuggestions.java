package com.lemonlightmc.zenith.commands.suggestions;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.exceptions.CommandException;

/**
 * Bukkit-specific helpers and base implementation for command suggestion providers.
 */
public abstract class BukkitSuggestions implements Suggestions<CommandSender> {

  /**
   * Creates a suggestion provider that returns no suggestions.
   *
   * @return an empty suggestion provider
   */
  public static Suggestions<CommandSender> empty() {
    return info -> List.of();
  }

  /**
   * Creates a suggestion provider from fixed string values.
   *
   * @param strings the suggestion values
   * @return a suggestion provider returning the given values
   */
  public static Suggestions<CommandSender> from(final String... strings) {
    return info -> List.of(strings);
  }

  /**
   * Creates a suggestion provider from a collection of string values.
   *
   * @param strings the suggestion values
   * @return a suggestion provider returning the given values
   */
  public static Suggestions<CommandSender> from(final Collection<String> strings) {
    return info -> List.copyOf(strings);
  }

  /**
   * Creates a suggestion provider from a function that computes suggestions from suggestion info.
   *
   * @param suggestions the suggestion function
   * @return a suggestion provider backed by the function
   */
  public static Suggestions<CommandSender> from(
      final Function<SuggestionInfo<CommandSender>, Collection<String>> suggestions) {
    return info -> List.copyOf(suggestions.apply(info));
  }

  /**
   * Creates a suggestion provider from a supplier of suggestion values.
   *
   * @param suggestions the suggestion supplier
   * @return a suggestion provider backed by the supplier
   */
  public static Suggestions<CommandSender> from(
      final Supplier<Collection<String>> suggestions) {
    return info -> List.copyOf(suggestions.get());
  }

  /**
   * Returns the given suggestion provider instance.
   *
   * @param suggestions the suggestion provider
   * @return the same suggestion provider
   */
  public static Suggestions<CommandSender> from(
      final Suggestions<CommandSender> suggestions) {
    return suggestions;
  }

  /**
   * Creates a suggestion provider from an asynchronous future of suggestion values.
   *
   * @param suggestions the future providing suggestion values
   * @return a suggestion provider backed by the future
   */
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

  /**
   * Creates a suggestion provider from an asynchronous function of suggestion values.
   *
   * @param suggestions the function providing a future of suggestion values
   * @return a suggestion provider backed by the asynchronous function
   */
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

  /**
   * Creates a suggestion provider from a single suggestion provider instance.
   *
   * @param provider the suggestion provider
   * @return a wrapped suggestion provider, or {@code null} if the provider is {@code null}
   */
  public static Suggestions<CommandSender> fromProvider(
      final SuggestionProvider<CommandSender> provider) {
    return provider == null ? null : info -> provider.getSuggestions(info);
  }

  /**
   * Creates a suggestion provider that aggregates suggestions from multiple providers.
   *
   * @param providers the suggestion providers to aggregate
   * @return an aggregated suggestion provider, or {@code null} if no providers are supplied
   */
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

  /**
   * Creates a suggestion provider that aggregates suggestions from a collection of providers.
   *
   * @param providers the suggestion providers to aggregate
   * @return an aggregated suggestion provider, or {@code null} if no providers are supplied
   */
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

  /**
   * Creates a suggestion provider from a class exposing a static {@code getSuggestions} method.
   *
   * @param provider the provider class to reflectively invoke
   * @return a suggestion provider backed by the reflected method, or {@code null} if the class is {@code null}
   */
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

  /**
   * Creates a suggestion provider from an asynchronous future of a suggestion provider.
   *
   * @param future the future providing a suggestion provider
   * @return a suggestion provider backed by the future
   */
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

  /**
   * Produces suggestions for the given suggestion context.
   *
   * @param info the suggestion context
   * @return the suggested values
   * @throws CommandException if suggestion generation fails
   */
  @Override
  public abstract Collection<String> suggest(SuggestionInfo<CommandSender> info) throws CommandException;
}
