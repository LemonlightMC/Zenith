package com.lemonlightmc.zenith.messages;

import java.util.Locale;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.additive.version.Version;

/**
 * Provides locale-aware translation lookup for message keys.
 */
public interface Translator {

  /**
   * Gets the locale served by this translator.
   *
   * @return the translator locale
   */
  public Locale locale();

  /**
   * Gets the version of the translation data.
   *
   * @return the translation version
   */
  public Version version();

  /**
   * Gets the author of the translation data.
   *
   * @return the translation author, or {@code null} if unavailable
   */
  public String author();

  /**
   * Translates a message key.
   *
   * @param key translation key to resolve
   * @return the translated value, or {@code null} if unavailable
   */
  public String translate(final String key);

  /**
   * Checks whether this translator can resolve the given key.
   *
   * @param key translation key to check
   * @return {@code true} if the key can be translated; otherwise {@code false}
   */
  public boolean canTranslate(final String key);

  /**
   * Checks whether this translator can resolve the given key for a player.
   *
   * @param key translation key to check
   * @param player player context
   * @return {@code true} if the key can be translated; otherwise {@code false}
   */
  default public boolean canTranslate(final String key, final Player player) {
    return canTranslate(key);
  }

  /**
   * Translates a message key for a player.
   *
   * @param key translation key to resolve
   * @param player player context
   * @return the translated value, or {@code null} if unavailable
   */
  default public String translate(final String key, final Player player) {
    return translate(key);
  }

}
