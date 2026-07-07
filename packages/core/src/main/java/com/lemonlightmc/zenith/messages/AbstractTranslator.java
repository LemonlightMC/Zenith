package com.lemonlightmc.zenith.messages;

import java.util.Locale;

import com.lemonlightmc.zenith.utils.StringUtils;
import com.lemonlightmc.zenith.version.Version;

public abstract class AbstractTranslator implements Translator {
  protected Locale locale;
  protected Version version;
  protected String author;

  public AbstractTranslator(final String locale, final Locale defaultLocale, final String version,
      final String author) {
    if (locale == null || locale.isBlank()) {
      throw new IllegalArgumentException("Locale cannot be empty for Translator!");
    }
    this.locale = StringUtils.parseLocale(locale);
    if (this.locale == null) {
      this.locale = defaultLocale == null ? Locale.ENGLISH : defaultLocale;
    }
    if (this.locale == null) {
      throw new IllegalArgumentException("Locale cannot be empty for Translator!");
    }
    if (version == null || version.isBlank()) {
      throw new IllegalArgumentException("Version cannot be empty for Translator!");
    }
    this.version = Version.semver(version);
    this.author = author == null || author.isBlank() ? null : author;
  }

  public AbstractTranslator(final Translator translator) {
    if (translator == null) {
      throw new IllegalArgumentException("Translator cannot be null when cloning!");
    }
    this.locale = translator.locale();
    this.version = translator.version();
    this.author = translator.author();
  }

  @Override
  public abstract String translate(final String key);

  @Override
  public abstract boolean canTranslate(final String key);

  public String author() {
    return null;
  }

  public Locale locale() {
    return locale;
  }

  public Version version() {
    return version;
  }

  @Override
  public int hashCode() {
    final int result = 31 * (31 + locale.hashCode()) + version.hashCode();
    return 31 * result + ((author == null) ? 0 : author.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AbstractTranslator other = (AbstractTranslator) obj;
    return locale.equals(other.locale) && version.equals(other.version)
        && (author == null ? other.author == null : author.equals(other.author));
  }

  @Override
  public String toString() {
    return "AbstractTranslator [locale=" + locale + ", version=" + version + ", author=" + author + "]";
  }
}
