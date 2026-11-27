package com.lemonlightmc.zenith.commands.suggestions;

import java.util.List;

@FunctionalInterface
public interface SuggestionProvider<S> {

  public List<String> getSuggestions(SuggestionInfo<S> info);
}
