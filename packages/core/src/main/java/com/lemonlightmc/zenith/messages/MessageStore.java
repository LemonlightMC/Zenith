package com.lemonlightmc.zenith.messages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MessageStore implements Iterable<MessageRepository<?>> {
  private final Map<Locale, MessageRepository<?>> repos = new HashMap<>();
  private Locale defaultLocale = Locale.ENGLISH;

  public MessageStore() {
  }

  public MessageStore(final Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  public void reloadAll() {
    for (final MessageRepository<?> repo : repos.values()) {
      if (repo.isEmpty()) {
        repo.load();
      }
    }
  }

  public void loadAll() {
    for (final MessageRepository<?> repo : repos.values()) {
      if (repo.isEmpty()) {
        repo.load();
      }
    }
  }

  public void clearAll() {
    repos.clear();
  }

  public String getMessage(final String key, final Locale locale) {
    if (key == null || key.length() == 0) {
      return null;
    }
    final MessageRepository<?> repo = repos.get(locale);
    return repo == null ? null : repo.getMessage(key);
  }

  public String getMessage(final String key) {
    if (key == null || key.length() == 0) {
      return null;
    }
    final MessageRepository<?> repo = repos.get(defaultLocale);
    return repo == null ? null : repo.getMessage(key);
  }

  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(final Locale locale) {
    this.defaultLocale = locale;
  }

  public void removeRepo(final Locale locale) {
    repos.remove(locale);
  }

  public MessageRepository<?> getRepo(final Locale locale) {
    return repos.get(locale);
  }

  public Map<Locale, MessageRepository<?>> getRepos() {
    return repos;
  }

  public boolean hasRepo(final Locale locale) {
    return repos.containsKey(locale);
  }

  @Override
  public Iterator<MessageRepository<?>> iterator() {
    return repos.values().iterator();
  }

  @Override
  public int hashCode() {
    return 31 * defaultLocale.hashCode() + repos.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final MessageStore other = (MessageStore) obj;
    return defaultLocale.equals(other.defaultLocale) && repos.equals(other.repos);
  }

  @Override
  public String toString() {
    return "MessageStore [repos=" + repos + ", defaultLocale=" + defaultLocale + "]";
  }
}
