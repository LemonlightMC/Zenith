package com.lemonlightmc.zenith.messages;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.IZenithPlugin;
import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.utils.StringUtils;

public class MessageAPI {
  private final Map<Locale, TranslationSource> sources = new ConcurrentHashMap<>();
  private final Map<Locale, Translator> translators = new ConcurrentHashMap<>();
  private Locale defaultLocale;
  private boolean usePlayerLocale;
  private boolean alwaysUseEnglish;
  private List<Locale> allowedLocales;

  private static volatile MessageAPI api;

  public MessageAPI() {
    alwaysUseEnglish = ZenithProvider.config().get("localization.enabled", false);
    if (alwaysUseEnglish) {
      defaultLocale = Locale.ENGLISH;
      usePlayerLocale = false;
      allowedLocales = List.of(Locale.ENGLISH);
      return;
    } else {
      allowedLocales = ZenithProvider.config().get("localization.allowed-locales", List.of(Locale.ENGLISH));
      defaultLocale = ZenithProvider.config().get("localization.default-locale", Locale.ENGLISH);
      usePlayerLocale = ZenithProvider.config().get("localization.use-player-locale", true);
    }
  }

  public MessageAPI(final Locale defaultLocale) {
    if (defaultLocale == null) {
      throw new IllegalArgumentException("Default locale cannot be null");
    }
    this.defaultLocale = defaultLocale;
  }

  public static MessageAPI instance() {
    if (api == null) {
      api = new MessageAPI();
    }
    return api;
  }

  public static MessageAPI instance(final IZenithPlugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    return plugin.getMessageAPI();
  }

  public Locale defaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(final Locale locale) {
    if (locale == null) {
      throw new IllegalArgumentException("Default locale cannot be null");
    }
    this.defaultLocale = locale;
    if (!allowedLocales.contains(locale)) {
      allowedLocales.add(locale);
    }
  }

  public String translate(final String key) {
    return translate(key, defaultLocale);
  }

  public String translate(final String key, final Locale locale) {
    if (key == null || key.length() == 0) {
      return null;
    }
    String result = null;
    // fast path for invalid or default locale
    if (locale == null || locale.equals(defaultLocale) || !allowedLocales.contains(locale)) {
      return translateDefault(key);
    }

    result = translateInternal(key, locale);
    if (result == null) {
      result = MessageAPI.instance().translateInternal(key, locale);
    }
    if (result == null) {
      return translateDefault(key);
    }
    return result;
  }

  private String translateDefault(final String key) {
    final String result = translateInternal(key, defaultLocale);
    if (result == null) {
      return MessageAPI.instance().translateInternal(key, defaultLocale);
    }
    return result;
  }

  private String translateInternal(final String key, final Locale locale) {
    Translator translator = translators.get(locale);
    if (translator == null) {
      final TranslationSource source = sources.get(locale);
      if (source == null) {
        return null;
      }
      translator = source.retrieve();
      translators.put(locale, translator);
    }
    return translator.translate(key);
  }

  public String translate(final String key, final Player locale) {
    if (usePlayerLocale) {
      return translate(key, StringUtils.parseLocale(locale.getLocale()));
    }
    return translate(key, defaultLocale);
  }

  public boolean canTranslate(final String key) {
    return translate(key, defaultLocale) != null;
  }

  public boolean canTranslate(final String key, final Locale locale) {
    return translate(key, locale) != null;
  }

  public boolean canTranslate(final String key, final Player locale) {
    if (usePlayerLocale) {
      return translate(key, StringUtils.parseLocale(locale.getLocale())) != null;
    }
    return translate(key, defaultLocale) != null;
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
