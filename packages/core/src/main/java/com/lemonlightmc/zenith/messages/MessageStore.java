package com.lemonlightmc.zenith.messages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lemonlightmc.zenith.messages.MessageBundle.MessageBundleSource;

public class MessageStore implements Iterable<MessageBundle> {
  private final Map<Locale, MessageBundle> bundles = new ConcurrentHashMap<>();
  private final Map<Locale, MessageBundleSource<?>> sources = new HashMap<>();
  private Locale defaultLocale = Locale.ENGLISH;

  public MessageStore() {
  }

  public MessageStore(final Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(final Locale locale) {
    this.defaultLocale = locale;
  }

  public String getMessage(final String key, Locale locale) {
    if (key == null || key.length() == 0) {
      return null;
    }
    locale = locale == null ? defaultLocale : locale;
    MessageBundle bundle = bundles.get(defaultLocale);
    if (bundle == null) {
      final MessageBundleSource<?> source = sources.get(defaultLocale);
      if (source == null) {
        return null;
      }
      bundle = source.resolve();
      bundles.put(defaultLocale, bundle);
    }
    return bundle.getMessage(key);
  }

  public String getMessage(final String key) {
    return getMessage(key, defaultLocale);
  }

  public void reloadAll() {
    bundles.clear();
  }

  public void loadAll() {

  }

  public void clearAll() {
    bundles.clear();
  }

  public void removeBundle(final Locale locale) {
    bundles.remove(locale);
  }

  public MessageBundle getBundle(final Locale locale) {
    return bundles.get(locale);
  }

  public Map<Locale, MessageBundle> getBundles() {
    return Map.copyOf(bundles);
  }

  public boolean hasBundle(final Locale locale) {
    return bundles.containsKey(locale);
  }

  @Override
  public Iterator<MessageBundle> iterator() {
    return bundles.values().iterator();
  }

  @Override
  public int hashCode() {
    return 31 * defaultLocale.hashCode() + bundles.hashCode();
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
    return defaultLocale.equals(other.defaultLocale) && bundles.equals(other.bundles);
  }

  @Override
  public String toString() {
    return "MessageStore [bundles=" + bundles + ", defaultLocale=" + defaultLocale + "]";
  }
}
