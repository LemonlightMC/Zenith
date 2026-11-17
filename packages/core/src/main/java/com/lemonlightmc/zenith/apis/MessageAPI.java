package com.lemonlightmc.zenith.apis;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.IZenithPlugin;
import com.lemonlightmc.zenith.ZenithConfig;
import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.additive.files.FileFilter;
import com.lemonlightmc.zenith.additive.files.FileUtils;
import com.lemonlightmc.zenith.messages.TranslationSource;
import com.lemonlightmc.zenith.messages.Translator;

/**
 * Provides access to localized message translations and translation sources.
 *
 * <p>The API resolves translations by locale, falls back to the configured default locale when needed, and can
 * optionally use a player's locale for lookups. Translation sources and translators can be registered manually or
 * discovered by scanning message files on disk.
 */
public class MessageAPI {
  private final Map<Locale, TranslationSource> sources = new ConcurrentHashMap<>();
  private final Map<Locale, Translator> translators = new ConcurrentHashMap<>();
  private Locale defaultLocale;
  private boolean usePlayerLocale;
  private boolean shouldLocalize;
  private List<Locale> allowedLocales;

  private static volatile MessageAPI api;

  /**
   * Creates a message API using localization settings from the current Zenith configuration.
   */
  public MessageAPI() {
    final ZenithConfig.Localization localization = ZenithProvider.config().localization;
    shouldLocalize = localization.enabled;
    if (!shouldLocalize) {
      defaultLocale = Locale.ENGLISH;
      usePlayerLocale = false;
      allowedLocales = List.of(Locale.ENGLISH);
    } else {
      allowedLocales = localization.allowedLocales;
      defaultLocale = localization.defaultLocale;
      usePlayerLocale = localization.usePlayerLocale;
    }
  }

  /**
   * Creates a message API with an explicit default locale.
   *
   * @param defaultLocale the locale to use when no other locale is selected
   * @throws IllegalArgumentException if {@code defaultLocale} is {@code null}
   */
  public MessageAPI(final Locale defaultLocale) {
    if (defaultLocale == null) {
      throw new IllegalArgumentException("Default locale cannot be null");
    }
    this.defaultLocale = defaultLocale;
  }

  /**
   * Returns the shared global message API instance.
   *
   * <p>If no instance has been created yet, a new one is initialized using the current Zenith configuration.
   *
   * @return the shared message API instance
   */
  public static MessageAPI instance() {
    if (api == null) {
      api = new MessageAPI();
    }
    return api;
  }

  /**
   * Returns the message API associated with the given plugin.
   *
   * @param plugin the plugin providing the message API
   * @return the plugin's message API instance
   * @throws IllegalArgumentException if {@code plugin} is {@code null}
   */
  public static MessageAPI instance(final IZenithPlugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    return plugin.getMessageAPI();
  }

  /**
   * Returns the locale used as the default translation fallback.
   *
   * @return the default locale
   */
  public Locale defaultLocale() {
    return defaultLocale;
  }

  /**
   * Sets the locale used as the default translation fallback.
   *
   * <p>If the locale is not already allowed, it is added to the set of allowed locales.
   *
   * @param locale the new default locale
   * @throws IllegalArgumentException if {@code locale} is {@code null}
   */
  public void setDefaultLocale(final Locale locale) {
    if (locale == null) {
      throw new IllegalArgumentException("Default locale cannot be null");
    }
    this.defaultLocale = locale;
    if (!allowedLocales.contains(locale)) {
      allowedLocales = new ArrayList<>(allowedLocales);
      allowedLocales.add(locale);
    }
  }

  /**
   * Translates a message key using the default locale.
   *
   * @param key the translation key to resolve
   * @return the translated message, or {@code null} if the key cannot be resolved
   */
  public String translate(final String key) {
    return translate(key, defaultLocale);
  }

  /**
   * Translates a message key for the specified locale.
   *
   * <p>If the requested locale is unavailable, not allowed, or does not contain the key, translation falls back to the
   * default locale.
   *
   * @param key the translation key to resolve
   * @param locale the preferred locale for translation
   * @return the translated message, or {@code null} if the key cannot be resolved
   */
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

  /**
   * Translates a message key for the specified player.
   *
   * <p>When player locale resolution is enabled, the player's locale is used; otherwise the default locale is used.
   *
   * @param key the translation key to resolve
   * @param locale the player whose locale may be used for translation
   * @return the translated message, or {@code null} if the key cannot be resolved
   */
  public String translate(final String key, final Player locale) {
    if (usePlayerLocale) {
      return translate(key, StringUtils.parseLocale(locale.getLocale()));
    }
    return translate(key, defaultLocale);
  }

  /**
   * Checks whether a translation exists for the given key in the default locale.
   *
   * @param key the translation key to check
   * @return {@code true} if a translation is available; {@code false} otherwise
   */
  public boolean canTranslate(final String key) {
    return translate(key, defaultLocale) != null;
  }

  /**
   * Checks whether a translation exists for the given key in the specified locale.
   *
   * @param key the translation key to check
   * @param locale the locale to check
   * @return {@code true} if a translation is available; {@code false} otherwise
   */
  public boolean canTranslate(final String key, final Locale locale) {
    return translate(key, locale) != null;
  }

  /**
   * Checks whether a translation exists for the given key for the specified player.
   *
   * @param key the translation key to check
   * @param locale the player whose locale may be used for the check
   * @return {@code true} if a translation is available; {@code false} otherwise
   */
  public boolean canTranslate(final String key, final Player locale) {
    if (usePlayerLocale) {
      return translate(key, StringUtils.parseLocale(locale.getLocale())) != null;
    }
    return translate(key, defaultLocale) != null;
  }

  /**
   * Registers a translation source for the specified locale.
   *
   * @param locale the locale associated with the source
   * @param source the translation source to register
   */
  public void register(final Locale locale, final TranslationSource source) {
    sources.put(locale, source);
  }

  /**
   * Registers a translator for the specified locale.
   *
   * @param locale the locale associated with the translator
   * @param translator the translator to register
   */
  public void register(final Locale locale, final Translator translator) {
    translators.put(locale, translator);
  }

  /**
   * Removes a registered translator.
   *
   * @param translator the translator to remove
   */
  public void unregister(final Translator translator) {
    translators.remove(translator.locale(), translator);
  }

  /**
   * Removes all registered translation sources and translators for the specified locale.
   *
   * @param locale the locale to remove
   */
  public void unregister(final Locale locale) {
    sources.remove(locale);
    translators.remove(locale);
  }

  /**
   * Removes all registered translation sources and translators.
   */
  public void unregisterAll() {
    sources.clear();
    translators.clear();
  }

  /**
   * Returns the registered translation sources by locale.
   *
   * @return a mutable map of locales to translation sources
   */
  public Map<Locale, TranslationSource> sources() {
    return sources;
  }

  /**
   * Returns the registered translators by locale.
   *
   * @return a mutable map of locales to translators
   */
  public Map<Locale, Translator> translators() {
    return translators;
  }

  /**
   * Scans a directory for translation files and registers discovered sources.
   *
   * <p>Only {@code .yml} and {@code .properties} files are considered. If the path is not a directory, no changes are
   * made.
   *
   * @param path the directory to scan for translation files
   */
  public void scan(final Path path) {
    if (!FileUtils.isDirectory(path)) {
      return;
    }
    List<File> files = FileUtils.listFiles(path, FileFilter.extensions(".yml", ".properties"));
    for (File file : files) {
      TranslationSource source = TranslationSource.from(file);
      sources.put(source.locale(), source);
    }
  }
}
