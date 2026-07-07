package com.lemonlightmc.zenith.messages;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessageAPI {
  private final Map<Locale, TranslationSource> sources = new HashMap<>();
  private final Map<Locale, Translator> translators = new HashMap<>();

  private Locale defaultLocale = Locale.ENGLISH;

  public MessageAPI() {
  }

  public MessageAPI(final Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(final Locale locale) {
    this.defaultLocale = locale;
  }

  public String translate(final String key) {
    if (key == null || key.length() == 0) {
      return null;
    }
    return translate(key, defaultLocale);
  }

  public String translate(final String key, Locale locale) {
    if (key == null || key.length() == 0) {
      return null;
    }
    locale = locale == null ? defaultLocale : locale;
    Translator translator = translators.get(defaultLocale);
    if (translator == null) {
      final TranslationSource source = sources.get(defaultLocale);
      if (source == null) {
        return null;
      }
      translator = source.retrieve();
      translators.put(defaultLocale, translator);
    }
    return translator.translate(key);
  }

  public boolean canTranslate(final String key) {
    return translate(key, defaultLocale) != null;
  }

  public boolean canTranslate(final String key, final Locale locale) {
    return translate(key, locale) != null;
  }

  public void register(final Locale locale, final TranslationSource source) {
    sources.put(locale, source);
  }

  public void register(final Locale locale, final Translator translator) {
    translators.put(locale, translator);
  }

  public void unregister(final Translator translator) {
    translators.remove(translator.locale(), translator);
  }

  public void unregister(final Locale locale) {
    sources.remove(locale);
    translators.remove(locale);
  }

  public void unregisterAll() {
    sources.clear();
    translators.clear();
  }

  public Map<Locale, TranslationSource> sources() {
    return sources;
  }

  public Map<Locale, Translator> translators() {
    return translators;
  }

}
