package com.lemonlightmc.zenith.messages;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.version.Version;

public class TranslationBundle extends AbstractTranslator {
  protected Map<String, String> messages;
  protected long last_updated;
  protected long created_at;
  protected boolean uses_icu;

  public TranslationBundle(final String locale, final Locale defaultLocale, final String version,
      final String author, final String uses_icu, final String last_updated, final String created_at,
      final Map<String, String> messages) {
    super(locale, defaultLocale, version, author);
    this.uses_icu = Boolean.parseBoolean(uses_icu);
    this.last_updated = NumberConversions.parseLong(last_updated);
    this.created_at = NumberConversions.parseLong(created_at);
    this.messages = messages == null || messages.isEmpty() ? null : messages;
  }

  public TranslationBundle(final TranslationBundle bundle) {
    super(bundle);
    this.messages = bundle.messages;
    this.last_updated = bundle.last_updated;
    this.created_at = bundle.created_at;
  }

  @Override
  public String translate(final String key) {
    if (key == null || key.length() == 0) {
      return null;
    }
    return messages.get(key);
  }

  @Override
  public boolean canTranslate(final String key) {
    return messages != null && messages.containsKey(key);
  }

  public Locale locale() {
    return locale;
  }

  public Version version() {
    return version;
  }

  public String author() {
    return null;
  }

  public long lastUpdated() {
    return last_updated;
  }

  public long createdAt() {
    return created_at;
  }

  public boolean usesICU() {
    return uses_icu;
  }

  public int size() {
    return messages == null ? 0 : messages.size();
  }

  public boolean contains(final String key) {
    return messages == null ? false : messages.containsKey(key);
  }

  public List<String> keys() {
    return messages == null ? List.of() : List.copyOf(messages.keySet());
  }

  public List<String> values() {
    return messages == null ? List.of() : List.copyOf(messages.values());
  }

  public boolean isEmpty() {
    return messages == null || messages.isEmpty();
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + ((messages == null) ? 0 : messages.hashCode());
    result = 31 * result + (int) (last_updated ^ (last_updated >>> 32));
    result = 31 * result + (int) (created_at ^ (created_at >>> 32));
    result = 31 * result + (uses_icu ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
      return false;
    }
    final TranslationBundle other = (TranslationBundle) obj;
    if (messages == null) {
      if (other.messages != null) {
        return false;
      }
    } else if (!messages.equals(other.messages)) {
      return false;
    }
    return last_updated == other.last_updated && created_at == other.created_at && uses_icu == other.uses_icu;
  }

  @Override
  public String toString() {
    return "TranslationBundle [locale=" + locale + ", version=" + version + ", author=" + author + ", last_updated="
        + last_updated + ", created_at=" + created_at + "]";
  }
}
