package com.lemonlightmc.zenith.commands.suggestions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import com.lemonlightmc.zenith.commands.exceptions.CommandException;

@FunctionalInterface
public interface Suggestions<S> {

  abstract Collection<String> suggest(SuggestionInfo<S> info) throws CommandException;

  public static <T> Suggestions<T> empty() {
    return info -> List.of();
  }

  public static <T> Suggestions<T> from(final String... strings) {
    return info -> List.of(strings);
  }

  public static <T> Suggestions<T> from(final Collection<String> strings) {
    return info -> List.copyOf(strings);
  }

  public static <T> Suggestions<T> from(
      final Function<SuggestionInfo<T>, Collection<String>> suggestions) {
    return info -> List.copyOf(suggestions.apply(info));
  }

  public static <T> Suggestions<T> from(
      final Supplier<Collection<String>> suggestions) {
    return info -> List.copyOf(suggestions.get());
  }

  public static <T> Suggestions<T> from(final Suggestions<T> suggestions) {
    return suggestions;
  }

  public static <T> Suggestions<T> fromAsync(
      final CompletableFuture<Collection<String>> suggestions) {
    return (info) -> {
      try {
        return List.copyOf(suggestions.get());
      } catch (final Exception e) {
        return null;
      }
    };
  }

  public static <T> Suggestions<T> fromAsync(
      final Function<SuggestionInfo<T>, CompletableFuture<Collection<String>>> suggestions) {
    return (info) -> {
      try {
        return List.copyOf(suggestions.apply(info).get());
      } catch (final Exception e) {
        return null;
      }
    };
  }

  public static <T> Suggestions<T> fromProvider(
      final SuggestionProvider<T> provider) {
    return provider == null ? null : info -> provider.getSuggestions(info);
  }

  @SuppressWarnings("unchecked")
  public static <T> Suggestions<T> fromProvider(final SuggestionProvider<T>... providers) {
    if (providers == null || providers.length == 0) {
      return null;
    }
    return info -> {
      final List<String> list = new ArrayList<>();
      for (final SuggestionProvider<T> provider : providers) {
        list.addAll(provider.getSuggestions(info));
      }
      return list;
    };
  }

  public static <T> Suggestions<T> fromProvider(final Collection<SuggestionProvider<T>> providers) {
    if (providers == null || providers.isEmpty()) {
      return null;
    }
    return info -> {
      final List<String> list = new ArrayList<>();
      for (final SuggestionProvider<T> provider : providers) {
        list.addAll(provider.getSuggestions(info));
      }
      return list;
    };
  }

  @SuppressWarnings("unchecked")
  public static <T> Suggestions<T> fromProvider(final Class<?> provider) {
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

  public static <T> Suggestions<T> fromProviderAsync(
      final CompletableFuture<SuggestionProvider<T>> future) {
    return (info) -> {
      try {
        final SuggestionProvider<T> provider = future.get();
        return provider == null ? null : provider.getSuggestions(info);
      } catch (final Exception e) {
        return List.of();
      }
    };
  }
}
