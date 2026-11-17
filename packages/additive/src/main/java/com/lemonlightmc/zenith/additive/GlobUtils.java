package com.lemonlightmc.zenith.additive;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility methods for matching and searching file paths using glob patterns.
 */
public class GlobUtils {

  /**
   * Finds files in a directory that match the given glob pattern.
   *
   * @param directory the directory to search
   * @param pattern the glob pattern (for example, {@code "*.jar"})
   * @return a list of matching paths
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
   * Finds the first file in a directory that matches the given glob pattern.
   *
   * @param directory the directory to search
   * @param pattern the glob pattern
   * @return the first matching file, or {@code null} if none is found
   */
  public static Path findFile(Path directory, String pattern) {
    List<Path> files = findFiles(directory, pattern);
    return files.isEmpty() ? null : files.get(0);
  }

  /**
   * Checks whether a file name matches the given glob pattern.
   *
   * @param filename the file name to check
   * @param pattern the glob pattern
   * @return {@code true} if the file name matches the pattern
   */
  public static boolean matches(String filename, String pattern) {
    String regex = globToRegex(pattern);
    return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(filename).matches();
  }

  /**
   * Converts a glob pattern to an equivalent regular expression.
   *
   * @param glob the glob pattern to convert
   * @return the converted regular expression
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
