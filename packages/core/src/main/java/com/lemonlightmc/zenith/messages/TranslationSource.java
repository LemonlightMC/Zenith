package com.lemonlightmc.zenith.messages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.lemonlightmc.zenith.exceptions.TranslationException;
import com.lemonlightmc.zenith.files.FileUtils;

public interface TranslationSource {

  public Translator retrieve();

  public static TranslationBundleSource<Properties> from(final Locale locale, final Properties props) {
    if (props == null) {
      throw new IllegalArgumentException("Properties cannot be null for Message Bundle Source!");
    }
    return new TranslationBundleSource<>(locale, props, TranslationBundleSource::load);
  }

  public static TranslationBundleSource<ResourceBundle> from(final Locale locale, final ResourceBundle bundle) {
    if (bundle == null) {
      throw new IllegalArgumentException("ResourceBundle cannot be null for Message Bundle Source!");
    }
    return new TranslationBundleSource<>(locale, bundle, TranslationBundleSource::load);
  }

  public static TranslationBundleSource<File> from(final Locale locale, final File file) {
    if (file == null) {
      throw new IllegalArgumentException("File cannot be null for Message Bundle Source!");
    }
    return new TranslationBundleSource<>(locale, file, TranslationBundleSource::load);
  }

  public static TranslationBundleSource<Path> from(final Locale locale, final Path path) {
    if (path == null) {
      throw new IllegalArgumentException("Path cannot be null for Message Bundle Source!");
    }
    return new TranslationBundleSource<>(locale, path, TranslationBundleSource::load);
  }

  public static TranslationBundleSource<URI> from(final Locale locale, final URI uri) {
    if (uri == null) {
      throw new IllegalArgumentException("URI cannot be null for Message Bundle Source!");
    }
    return new TranslationBundleSource<>(locale, uri, TranslationBundleSource::load);
  }

  public static TranslationBundleSource<URL> from(final Locale locale, final URL url) {
    if (url == null) {
      throw new IllegalArgumentException("URL cannot be null for Message Bundle Source!");
    }
    return new TranslationBundleSource<>(locale, url, TranslationBundleSource::load);
  }

  public static TranslationBundleSource<Supplier<Map<String, String>>> from(final Locale locale,
      final Supplier<Map<String, String>> supplier) {
    if (supplier == null) {
      throw new IllegalArgumentException("Supplier cannot be null for Message Bundle Source!");
    }
    return new TranslationBundleSource<>(locale, supplier, TranslationBundleSource::load);
  }

  public static record TranslationBundleSource<T>(Locale locale, T source,
      BiFunction<Locale, T, TranslationBundle> mapper)
      implements TranslationSource {

    public TranslationBundle retrieve() {
      return mapper.apply(locale, source);
    }

    private static TranslationBundle load(final Locale tempLocale, final Properties props) {
      final Map<String, String> messages = new HashMap<>();
      for (final String key : props.stringPropertyNames()) {
        if (key == null || key.isBlank()) {
          continue;
        }
        messages.put(key, props.getProperty(key));
      }
      return new TranslationBundle(
          props.getProperty("meta.locale"),
          tempLocale,
          props.getProperty("meta.version"),
          props.getProperty("meta.author"),
          props.getProperty("meta.uses_icu"),
          props.getProperty("meta.last_updated"),
          props.getProperty("meta.created_at"),
          messages);
    }

    private static TranslationBundle load(final Locale tempLocale, final ResourceBundle bundle) {
      final Map<String, String> messages = new HashMap<>();
      for (final String key : bundle.keySet()) {
        if (key == null || key.isBlank()) {
          continue;
        }
        messages.put(key, bundle.getString(key));
      }
      return new TranslationBundle(
          bundle.getString("meta.locale"),
          tempLocale,
          bundle.getString("meta.version"),
          bundle.getString("meta.author"),
          bundle.getString("meta.uses_icu"),
          bundle.getString("meta.last_updated"),
          bundle.getString("meta.created_at"),
          messages);
    }

    private static TranslationBundle load(final Locale tempLocale, final File file) {
      final Properties props = new Properties();
      try (final InputStream in = FileUtils.createInputStream(file)) {
        props.load(in);
      } catch (final IOException | IllegalArgumentException e) {
        throw new TranslationException("Failed to load message bundle from file: " + file.getAbsolutePath(), e);
      }
      return TranslationBundleSource.load(tempLocale, props);
    }

    private static TranslationBundle load(final Locale tempLocale, final Path path) {
      final Properties props = new Properties();
      try (final InputStream in = FileUtils.createInputStream(path)) {
        props.load(in);
      } catch (final IOException | IllegalArgumentException e) {
        throw new TranslationException("Failed to load message bundle from path: " + path.toAbsolutePath(), e);
      }
      return TranslationBundleSource.load(tempLocale, props);
    }

    private static TranslationBundle load(final Locale tempLocale, final URI uri) {
      final Properties props = new Properties();
      try (final InputStream in = uri.toURL().openStream()) {
        props.load(in);
      } catch (final IOException | IllegalArgumentException e) {
        throw new TranslationException("Failed to load message bundle from URI: " + uri.toString(), e);
      }
      return TranslationBundleSource.load(tempLocale, props);
    }

    private static TranslationBundle load(final Locale tempLocale, final URL url) {
      final Properties props = new Properties();
      try (final InputStream in = url.openStream()) {
        props.load(in);
      } catch (final IOException | IllegalArgumentException e) {
        throw new TranslationException("Failed to load message bundle from URL: " + url.toString(), e);
      }
      return TranslationBundleSource.load(tempLocale, props);
    }

    private static TranslationBundle load(final Locale tempLocale, final Supplier<Map<String, String>> supplier) {
      final Map<String, String> messages = supplier.get();
      return new TranslationBundle(
          messages.get("meta.locale"),
          tempLocale,
          messages.get("meta.version"),
          messages.get("meta.author"),
          messages.get("meta.uses_icu"),
          messages.get("meta.last_updated"),
          messages.get("meta.created_at"),
          messages);
    }
  }

}
