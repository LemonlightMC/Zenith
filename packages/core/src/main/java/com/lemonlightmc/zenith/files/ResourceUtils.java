package com.lemonlightmc.zenith.files;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.messages.Logger;

public class ResourceUtils {

  public static URL getResourceURL(final String path) {
    if (path == null || path.isEmpty()) {
      return null;
    }
    try {
      return ZenithProvider.getInstance().getClass().getClassLoader().getResource(path);
    } catch (final Exception e) {
      return null;
    }
  }

  public static URL getResourceURL(final Path path) {
    return path == null ? null : getResourceURL(path.toString());
  }

  public static File getResourceFile(final String path) {
    try {
      final URL url = getResourceURL(path);
      return url == null ? null : new File(url.toURI());
    } catch (final Exception e) {
      return null;
    }
  }

  public static File getResourceFile(final Path path) {
    return path == null ? null : getResourceFile(path.toString());
  }

  public static BufferedInputStream getResourceStream(final String path) {
    try {
      final URL url = getResourceURL(path);
      return url == null ? null : FileUtils.createInputStream(url.openStream());
    } catch (final Exception e) {
      return null;
    }
  }

  public static Reader getResourceReader(final String path) {
    try {
      final URL url = getResourceURL(path);
      if (url == null) {
        return null;
      }
      return FileUtils.createReader(url.openStream());
    } catch (final Exception e) {
      return null;
    }
  }

  public static FileResult saveResource(final File inFile, final File target) {
    return FileUtils.copy(inFile, target);
  }

  public static FileResult saveResource(final InputStream in, final File target) {
    return FileUtils.copy(in, target);
  }

  public static boolean hasResource(final String path) {
    return getResourceURL(path) != null;
  }

  public static boolean hasResource(final Path path) {
    return path != null && getResourceURL(path.toString()) != null;
  }

  public static List<URL> getResourcesURLs(final String path) {
    final Iterator<URL> urls = getResourcesIterator(path);
    if (urls == null) {
      return List.of();
    }
    final ArrayList<URL> l = new ArrayList<>();
    while (urls.hasNext())
      l.add(urls.next());
    return l;
  }

  public static Iterator<URL> getResourcesIterator(final String path) {
    if (path == null || path.isEmpty()) {
      return Collections.emptyListIterator();
    }
    try {
      return ZenithProvider.getInstance().getClass().getClassLoader().getResources(path).asIterator();
    } catch (final Exception e) {
      return Collections.emptyListIterator();
    }
  }

  public static List<File> getResourcesFiles(final String path) {
    return FileUtils.listFiles(getResourceFile(path));
  }

  public static Properties loadPropertiesFromJar(final String resourcePath) {
    final Properties props = new Properties();
    try (InputStream in = getResourceStream(resourcePath)) {
      if (in == null)
        return null;
      props.load(FileUtils.createReader(in));
      return props;
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Properties loadPropertiesFromFile(final File file) {
    if (!file.exists() || !file.isFile() || !file.getAbsolutePath().endsWith(".properties")) {
      return null;
    }
    final Properties props = new Properties();
    try (BufferedReader reader = FileUtils.createReader(file)) {
      props.load(reader);
      return props;
    } catch (final Exception e) {
      Logger.warn("Failed to read properties from: " + file.getPath());
      e.printStackTrace();
      return null;
    }
  }

  public static void savePropertiesToFile(final Properties props, final File file) {
    if (!file.exists() || !file.isFile() || !file.getAbsolutePath().endsWith(".properties")) {
      return;
    }
    try (BufferedWriter writer = FileUtils.createWriter(file)) {
      final List<String> keys = new ArrayList<>(props.stringPropertyNames());
      Collections.sort(keys);
      for (final String key : keys) {
        writer.write(key + "=" + props.getProperty(key));
        writer.newLine();
      }
    } catch (final Exception e) {
      Logger.warn("Failed to save properties to: " + file.getPath());
      e.printStackTrace();
    }
  }

  public static String getResourceBundleString(final ResourceBundle bundle, final String key) {
    try {
      return bundle.getString(key);
    } catch (final Exception e) {
      return null;
    }
  }

  public static String getResourceBundleString(final ResourceBundle bundle, final String key, final String def) {
    try {
      final String value = bundle.getString(key);
      return value != null ? value : def;
    } catch (final Exception e) {
      return def;
    }
  }

  public static boolean resourceBundleContainsKey(final ResourceBundle bundle, final String key) {
    try {
      return bundle.containsKey(key);
    } catch (final Exception e) {
      return false;
    }
  }

  public static int getResourceBundleSize(final ResourceBundle bundle) {
    try {
      return bundle.keySet().size();
    } catch (final Exception e) {
      return 0;
    }
  }

  public static List<String> getResourceBundleKeys(final ResourceBundle bundle) {
    try {
      return new ArrayList<>(bundle.keySet());
    } catch (final Exception e) {
      return List.of();
    }
  }

  public static List<String> getResourceBundleValues(final ResourceBundle bundle) {
    try {
      final List<String> values = new ArrayList<>();
      for (final String key : bundle.keySet()) {
        values.add(bundle.getString(key));
      }
      return values;
    } catch (final Exception e) {
      return List.of();
    }
  }

  public static Properties resourceBundleToProperties(final ResourceBundle bundle) {
    final Properties props = new Properties();
    try {
      for (final String key : bundle.keySet()) {
        props.setProperty(key, bundle.getString(key));
      }
    } catch (final Exception e) {
      return null;
    }
    return props;
  }
}
