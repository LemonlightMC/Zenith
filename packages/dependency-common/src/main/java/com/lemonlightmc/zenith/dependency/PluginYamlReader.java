package com.lemonlightmc.zenith.dependency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads minimal metadata (name, version) from the <code>plugin.yml</code> or
 * <code>paper-plugin.yml</code> inside a plugin jar without pulling in a full
 * YAML parser. Used to detect user-installed plugins so Hopper never overwrites
 * a jar a server admin put in place themselves.
 */
public final class PluginYamlReader {

  private static final String[] DESCRIPTOR_ENTRIES = { "paper-plugin.yml", "plugin.yml" };

  // Matches top-level `name: value` / `name: "value"` / `name: 'value'`
  private static final Pattern NAME_PATTERN = Pattern.compile(
      "^name\\s*:\\s*(?:\"([^\"]*)\"|'([^']*)'|([^#\\s][^#]*?))\\s*(?:#.*)?$");
  private static final Pattern VERSION_PATTERN = Pattern.compile(
      "^version\\s*:\\s*(?:\"([^\"]*)\"|'([^']*)'|([^#\\s][^#]*?))\\s*(?:#.*)?$");

  private PluginYamlReader() {
  }

  /**
   * Descriptor data extracted from a plugin jar.
   */
  public record Descriptor(String name, String version) {
  }

  /**
   * A jar file on disk together with its parsed descriptor.
   */
  public record Match(Path path, Descriptor descriptor) {
  }

  /**
   * Read the plugin descriptor from a jar, or null if none is found.
   */

  public static Descriptor read(final Path jarPath) {
    if (!Files.isRegularFile(jarPath)) {
      return null;
    }
    try (JarFile jar = new JarFile(jarPath.toFile())) {
      for (final String entryName : DESCRIPTOR_ENTRIES) {
        final JarEntry entry = jar.getJarEntry(entryName);
        if (entry == null)
          continue;
        try (InputStream is = jar.getInputStream(entry)) {
          final Descriptor d = parse(is);
          if (d != null)
            return d;
        }
      }
    } catch (final IOException e) {
      // Malformed/unreadable jar - treat as not a plugin.
    }
    return null;
  }

  /**
   * Collect every jar in <code>pluginsFolder</code> whose descriptor name
   * matches <code>pluginName</code> (case-insensitive). Returns each match
   * with its parsed descriptor so callers do not need to re-open the jar.
   */

  public static List<Match> findAll(final Path pluginsFolder, final String pluginName) {
    final List<Match> matches = new ArrayList<>();
    if (!Files.isDirectory(pluginsFolder)) {
      return matches;
    }
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginsFolder, "*.jar")) {
      for (final Path jar : stream) {
        final Descriptor d = read(jar);
        if (d != null && d.name().equalsIgnoreCase(pluginName)) {
          matches.add(new Match(jar, d));
        }
      }
    } catch (final IOException e) {
      // Ignore
    }
    return matches;
  }

  private static Descriptor parse(final InputStream is) throws IOException {
    String name = null;
    String version = null;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        // Only consider top-level keys (no leading whitespace, not a list/comment)
        if (line.isEmpty() || line.charAt(0) == ' ' || line.charAt(0) == '\t'
            || line.charAt(0) == '#' || line.charAt(0) == '-') {
          continue;
        }
        if (name == null) {
          final String v = match(NAME_PATTERN, line);
          if (v != null) {
            name = v;
            continue;
          }
        }
        if (version == null) {
          final String v = match(VERSION_PATTERN, line);
          if (v != null) {
            version = v;
          }
        }
        if (name != null && version != null)
          break;
      }
    }
    if (name == null)
      return null;
    return new Descriptor(name, version);
  }

  private static String match(final Pattern p, final String line) {
    final Matcher m = p.matcher(line);
    if (!m.matches())
      return null;
    for (int i = 1; i <= m.groupCount(); i++) {
      final String g = m.group(i);
      if (g != null)
        return g.trim();
    }
    return null;
  }
}