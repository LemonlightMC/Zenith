package com.lemonlightmc.zenith.commands.suggestions;

import java.util.List;

public interface SuggestionProvider<S> {

  public List<String> getSuggestions(SuggestionInfo<S> info);
}
