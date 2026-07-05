package com.lemonlightmc.zenith.messages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.lemonlightmc.zenith.files.FileUtils;
import com.lemonlightmc.zenith.interfaces.Cloneable;
import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.utils.StringUtils;
import com.lemonlightmc.zenith.version.Version;

public class MessageBundle implements Cloneable<MessageBundle> {
  protected String name;
  protected Locale locale;
  protected String description;
  protected float progress; // between 1.0 - 100.0 (1 decimal percision)
  protected Version version;
  protected String[] contributors;

  protected Map<String, String> messages;
  protected long last_updated;
  protected long created_at;
  protected boolean uses_icu;

  private static final Version DEFAULT_VERSION = Version.FIRST_VERSION;
  private static final String[] DEFAULT_CONTRIBUTORS = new String[0];
  private static final String DEFAULT_DESC = "No description";

  public MessageBundle(final Locale locale, final String name, final Version version,
      final float progress, final String desc,
      final String[] contributors) {
    if (locale == null) {
      throw new IllegalArgumentException("Locale cannot be null for Message Bundle!");
    }
    this.locale = locale;
    this.name = name == null ? locale.toString() : name;
    this.version = version == null ? DEFAULT_VERSION : version;
    this.contributors = contributors == null || contributors.length == 0 ? DEFAULT_CONTRIBUTORS : contributors;
    this.description = desc == null || !desc.isBlank() ? DEFAULT_DESC : desc;
    this.progress = Math.max(Math.min(progress, 0), 100);
    this.progress = Math.round(this.progress * 10f) / 10f;
  }

  public MessageBundle(final Locale locale, final String name, final Version version,
      final float progress, final String desc,
      final String[] contributors, final Map<String, String> messages) {
    this(locale, name, version, progress, desc, contributors);
  }

  public MessageBundle(final String locale, final String name, final Version version,
      final float progress, final String desc,
      final String[] contributors) {
    this(StringUtils.parseLocale(locale), name, version, progress, desc, contributors);
  }

  public MessageBundle(final String locale, final Locale defaultLocale, final String name, final String version,
      final String progress, final String desc,
      final String contributors, final String uses_icu, final String last_updated, final String created_at,
      final Map<String, String> messages) {
    if (locale == null || locale.isBlank()) {
      throw new IllegalArgumentException("Locale cannot be empty for Message Bundle!");
    }
    this.locale = StringUtils.parseLocale(locale);
    if (this.locale == null) {
      this.locale = defaultLocale == null ? Locale.ENGLISH : defaultLocale;
    }
    if (this.locale == null) {
      throw new IllegalArgumentException("Locale cannot be empty for Message Bundle!");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be empty for Message Bundle!");
    }
    if (version == null || version.isBlank()) {
      throw new IllegalArgumentException("Version cannot be empty for Message Bundle!");
    }
    this.name = name == null ? this.locale.toString() : name;
    this.version = Version.semver(version);
    this.contributors = contributors == null || contributors.isEmpty() ? DEFAULT_CONTRIBUTORS
        : StringUtils.fastSplit(contributors, ',');
    this.description = desc == null || !desc.isBlank() ? DEFAULT_DESC : desc;
    this.progress = Math.max(Math.min(NumberConversions.parseFloat(progress), 0), 100);
    this.progress = Math.round(this.progress * 10f) / 10f;
    this.uses_icu = Boolean.parseBoolean(uses_icu);
    this.last_updated = NumberConversions.parseLong(last_updated);
    this.created_at = NumberConversions.parseLong(created_at);
    this.messages = messages == null || messages.isEmpty() ? null : messages;
  }

  public MessageBundle(final MessageBundle repo) {
    if (repo == null) {
      throw new IllegalArgumentException("Message Bundle cannot be null when cloning!");
    }
    this.name = repo.name;
    this.locale = repo.locale;
    this.version = repo.version;
    this.progress = repo.progress;
    this.description = repo.description;
    this.contributors = repo.contributors;
    this.messages = repo.messages;
    this.last_updated = repo.last_updated;
    this.created_at = repo.created_at;
  }

  public String getMessage(final String key) {
    if (key == null || key.length() == 0) {
      return null;
    }
    return messages.get(key);
  }

  public Map<String, String> getMessages() {
    return Map.copyOf(messages);
  }

  public int size() {
    return messages.size();
  }

  public boolean contains(final String key) {
    return messages.containsKey(key);
  }

  public List<String> getKeys() {
    return List.copyOf(messages.keySet());
  }

  public List<String> getValues() {
    return List.copyOf(messages.values());
  }

  public boolean isEmpty() {
    return messages == null || messages.isEmpty();
  }

  public boolean isLoaded() {
    return messages != null && !messages.isEmpty();
  }

  public Locale getLocale() {
    return locale;
  }

  public String getName() {
    return this.name;
  }

  public Version getVersion() {
    return version;
  }

  public float getProgress() {
    return progress;
  }

  public String getDescription() {
    return description;
  }

  public String[] getContributors() {
    return this.contributors;
  }

  public long getLastUpdated() {
    return last_updated;
  }

  public long getCreatedAt() {
    return created_at;
  }

  public boolean usesICU() {
    return uses_icu;
  }

  @Override
  public MessageBundle clone() {
    return new MessageBundle(this);
  }

  @Override
  public int hashCode() {
    int result = 31 * (31 + name.hashCode()) + locale.hashCode();
    result = 31 * result + ((version == null) ? 0 : version.hashCode());
    result = 31 * result + (int) (last_updated ^ (last_updated >>> 32));
    return 31 * result + (int) (created_at ^ (created_at >>> 32));
  }

  // when ever messages, contributors, the progress, version, description, etc
  // changes, then the last_updated
  // field should change too!!!
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final MessageBundle other = (MessageBundle) obj;
    return last_updated == other.last_updated && created_at == other.created_at && name.equals(other.name)
        && locale.equals(other.locale) && version.equals(other.version);
  }

  @Override
  public String toString() {
    return "MessageBundle [name=" + name + ", locale=" + locale + ", description=" + description + ", progress="
        + progress + ", version=" + version + ", contributors=" + Arrays.toString(contributors) + ", last_updated="
        + last_updated + ", created_at=" + created_at + "]";
  }

  public static record MessageBundleSource<T>(Locale locale, T source, BiFunction<Locale, T, MessageBundle> mapper) {

    public MessageBundle resolve() {
      return mapper.apply(locale, source);
    }

    public static MessageBundleSource<Properties> from(final Locale locale, final Properties props) {
      if (props == null) {
        throw new IllegalArgumentException("Properties cannot be null for Message Bundle Source!");
      }
      return new MessageBundleSource<>(locale, props, MessageBundleSource::load);
    }

    public static MessageBundleSource<ResourceBundle> from(final Locale locale, final ResourceBundle bundle) {
      if (bundle == null) {
        throw new IllegalArgumentException("ResourceBundle cannot be null for Message Bundle Source!");
      }
      return new MessageBundleSource<>(locale, bundle, MessageBundleSource::load);
    }

    public static MessageBundleSource<File> from(final Locale locale, final File file) {
      if (file == null) {
        throw new IllegalArgumentException("File cannot be null for Message Bundle Source!");
      }
      return new MessageBundleSource<>(locale, file, MessageBundleSource::load);
    }

    public static MessageBundleSource<Path> from(final Locale locale, final Path path) {
      if (path == null) {
        throw new IllegalArgumentException("Path cannot be null for Message Bundle Source!");
      }
      return new MessageBundleSource<>(locale, path, MessageBundleSource::load);
    }

    public static MessageBundleSource<URI> from(final Locale locale, final URI uri) {
      if (uri == null) {
        throw new IllegalArgumentException("URI cannot be null for Message Bundle Source!");
      }
      return new MessageBundleSource<>(locale, uri, MessageBundleSource::load);
    }

    public static MessageBundleSource<URL> from(final Locale locale, final URL url) {
      if (url == null) {
        throw new IllegalArgumentException("URL cannot be null for Message Bundle Source!");
      }
      return new MessageBundleSource<>(locale, url, MessageBundleSource::load);
    }

    public static MessageBundleSource<Supplier<Map<String, String>>> from(final Locale locale,
        final Supplier<Map<String, String>> supplier) {
      if (supplier == null) {
        throw new IllegalArgumentException("Supplier cannot be null for Message Bundle Source!");
      }
      return new MessageBundleSource<>(locale, supplier, MessageBundleSource::load);
    }

    public static MessageBundle load(final Locale tempLocale, final Properties props) {
      final Map<String, String> messages = new HashMap<>();
      for (final String key : props.stringPropertyNames()) {
        if (key == null || key.isBlank()) {
          continue;
        }
        messages.put(key, props.getProperty(key));
      }
      return new MessageBundle(
          props.getProperty("meta.locale"),
          tempLocale,
          props.getProperty("meta.name"),
          props.getProperty("meta.version"),
          props.getProperty("meta.progress"),
          props.getProperty("meta.description"),
          props.getProperty("meta.contributors"),
          props.getProperty("meta.uses_icu"),
          props.getProperty("meta.last_updated"),
          props.getProperty("meta.created_at"),
          messages);
    }

    public static MessageBundle load(final Locale tempLocale, final ResourceBundle bundle) {
      final Map<String, String> messages = new HashMap<>();
      for (final String key : bundle.keySet()) {
        if (key == null || key.isBlank()) {
          continue;
        }
        messages.put(key, bundle.getString(key));
      }
      return new MessageBundle(
          bundle.getString("meta.locale"),
          tempLocale,
          bundle.getString("meta.name"),
          bundle.getString("meta.version"),
          bundle.getString("meta.progress"),
          bundle.getString("meta.description"),
          bundle.getString("meta.contributors"),
          bundle.getString("meta.uses_icu"),
          bundle.getString("meta.last_updated"),
          bundle.getString("meta.created_at"),
          messages);
    }

    public static MessageBundle load(final Locale tempLocale, final File file) {
      final Properties props = new Properties();
      try (final InputStream in = FileUtils.createInputStream(file)) {
        props.load(in);
      } catch (final IOException e) {
        throw new RuntimeException("Failed to load message bundle from file: " + file.getAbsolutePath(), e);
      }
      return load(tempLocale, props);
    }

    public static MessageBundle load(final Locale tempLocale, final Path path) {
      final Properties props = new Properties();
      try (final InputStream in = FileUtils.createInputStream(path)) {
        props.load(in);
      } catch (final IOException e) {
        throw new RuntimeException("Failed to load message bundle from path: " + path.toAbsolutePath(), e);
      }
      return load(tempLocale, props);
    }

    public static MessageBundle load(final Locale tempLocale, final URI uri) {
      final Properties props = new Properties();
      try (final InputStream in = uri.toURL().openStream()) {
        props.load(in);
      } catch (final IOException e) {
        throw new RuntimeException("Failed to load message bundle from URI: " + uri.toString(), e);
      }
      return load(tempLocale, props);
    }

    public static MessageBundle load(final Locale tempLocale, final URL url) {
      final Properties props = new Properties();
      try (final InputStream in = url.openStream()) {
        props.load(in);
      } catch (final IOException e) {
        throw new RuntimeException("Failed to load message bundle from URL: " + url.toString(), e);
      }
      return load(tempLocale, props);
    }

    public static MessageBundle load(final Locale tempLocale, final Supplier<Map<String, String>> supplier) {
      final Map<String, String> messages = supplier.get();
      return new MessageBundle(
          messages.get("meta.locale"),
          tempLocale,
          messages.get("meta.name"),
          messages.get("meta.version"),
          messages.get("meta.progress"),
          messages.get("meta.description"),
          messages.get("meta.contributors"),
          messages.get("meta.uses_icu"),
          messages.get("meta.last_updated"),
          messages.get("meta.created_at"),
          messages);
    }
  }
}
