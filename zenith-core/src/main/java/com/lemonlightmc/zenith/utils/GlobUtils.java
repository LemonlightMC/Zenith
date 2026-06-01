package com.lemonlightmc.zenith.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GlobUtils {

  /**
   * Find files matching a glob pattern in a directory.
   *
   * @param directory the directory to search
   * @param pattern   the glob pattern (e.g., "*.jar", "Plugin-*.jar")
   * @return list of matching paths
   */

  public static List<Path> findFiles(Path directory, String pattern) {
    List<Path> results = new ArrayList<>();

    if (!Files.isDirectory(directory)) {
      return results;
    }

    // Convert glob to regex
    String regex = globToRegex(pattern);
    Pattern compiled = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        if (compiled.matcher(path.getFileName().toString()).matches()) {
          results.add(path);
        }
      }
    } catch (IOException e) {
      // Ignore and return empty list
    }

    return results;
  }

  /**
   * Find a single file matching a glob pattern.
   *
   * @param directory the directory to search
   * @param pattern   the glob pattern
   * @return the first matching file, or null if none found
   */

  public static Path findFile(Path directory, String pattern) {
    List<Path> files = findFiles(directory, pattern);
    return files.isEmpty() ? null : files.get(0);
  }

  /**
   * Check if a filename matches a glob pattern.
   *
   * @param filename the filename to check
   * @param pattern  the glob pattern
   * @return true if matches
   */
  public static boolean matches(String filename, String pattern) {
    String regex = globToRegex(pattern);
    return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(filename).matches();
  }

  /**
   * Convert a glob pattern to a regex pattern.
   */

  private static String globToRegex(String glob) {
    StringBuilder regex = new StringBuilder();
    regex.append("^");

    for (int i = 0; i < glob.length(); i++) {
      char c = glob.charAt(i);
      switch (c) {
        case '*':
          if (i + 1 < glob.length() && glob.charAt(i + 1) == '*') {
            regex.append(".*");
            i++;
          } else {
            regex.append("[^/]*");
          }
          break;
        case '?':
          regex.append("[^/]");
          break;
        case '.':
        case '(':
        case ')':
        case '[':
        case ']':
        case '{':
        case '}':
        case '+':
        case '^':
        case '$':
        case '|':
        case '\\':
          regex.append("\\").append(c);
          break;
        default:
          regex.append(c);
      }
    }

    regex.append("$");
    return regex.toString();
  }
}
