package com.lemonlightmc.zenith.additive;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * General-purpose string and collection helper utilities.
 */
public class StringUtils {
  private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

  /**
   * Represents a placeholder replacement to apply to a string.
   *
   * @param placeholder the placeholder pattern to replace
   * @param value       the replacement value
   */
  public record Replaceable(String placeholder, String value) {

    /**
     * Applies this replacement to the given message.
     *
     * @param message the message to update
     * @return the updated message
     */
    public String apply(final String message) {
      return message.replaceAll(placeholder, value);
    }
  }

  /**
   * Formats a message pattern with a single argument.
   *
   * @param messagePattern message pattern to format
   * @param arg1           formatting argument
   * @return the formatted message, or an empty string if the pattern is
   *         {@code null} or empty
   */
  public static String format(final String messagePattern, final Object arg1) {
    if (messagePattern == null || messagePattern.isEmpty()) {
      return "";
    }
    if (arg1 == null) {
      return messagePattern;
    }
    return StringFormatter.format(messagePattern, new Object[] { arg1 });
  }

  /**
   * Formats a message pattern with two arguments.
   *
   * @param messagePattern message pattern to format
   * @param arg1           first formatting argument
   * @param arg2           second formatting argument
   * @return the formatted message, or an empty string if the pattern is
   *         {@code null} or empty
   */
  public static String format(final String messagePattern, final Object arg1, final Object arg2) {
    if (messagePattern == null || messagePattern.isEmpty()) {
      return "";
    }
    if (arg1 == null && arg2 == null) {
      return messagePattern;
    }
    return StringFormatter.format(messagePattern, new Object[] { arg1, arg2 });
  }

  /**
   * Formats a message pattern with an array of arguments.
   *
   * @param messagePattern message pattern to format
   * @param argArray       formatting arguments
   * @return the formatted message, or an empty string if the pattern is
   *         {@code null} or empty
   */
  public static String format(final String messagePattern, final Object[] argArray) {
    if (messagePattern == null || messagePattern.isEmpty()) {
      return "";
    }
    if (argArray == null || argArray.length == 0) {
      return messagePattern;
    }
    return StringFormatter.format(messagePattern, argArray);
  }

  /**
   * Checks whether the given string is {@code null} or empty.
   *
   * @param str string to check
   * @return {@code true} if the string is {@code null} or has length zero;
   *         otherwise {@code false}
   */
  public static boolean isBlank(final String str) {
    return str == null || str.length() == 0;
  }

  /**
   * Checks whether the given string is {@code null}.
   *
   * @param str string to check
   * @return {@code true} if the string is {@code null}; otherwise {@code false}
   */
  public static boolean isEmpty(final String str) {
    return str == null;
  }

  /**
   * Prepares the given string by trimming whitespace and returning {@code null}
   * if the result is empty.
   *
   * @param str string to prepare
   * 
   * @return the trimmed string, or {@code null} if the input is {@code null} or
   *         empty after trimming
   */
  public static String prepare(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    str = str.trim();
    return str.isEmpty() ? null : str;
  }

  /**
   * Prepares the given string by trimming whitespace, apply Lowercase and
   * returning {@code null}
   * if the result is empty.
   *
   * @param str string to prepare
   * 
   * @return the trimmed string, or {@code null} if the input is {@code null} or
   *         empty after trimming
   */
  public static String prepareLowercase(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    str = str.trim();
    return str.isEmpty() ? null : str.toLowerCase(Locale.ROOT);
  }

  public static String lowercase(final String str) {
    return str == null ? null : str.toLowerCase(Locale.ROOT);
  }

  public static String uppercase(final String str) {
    return str == null ? null : str.toUpperCase(Locale.ROOT);
  }

  /**
   * Applies placeholder replacements to the given message.
   *
   * @param message      message to update
   * @param replacements alternating placeholder and replacement values
   * @return the updated message, or {@code null} if the input message is
   *         {@code null} or empty
   */
  public static String applyReplacements(String message, final String... replacements) {
    if (message == null || message.length() == 0) {
      return null;
    }
    final int len = replacements.length - 1;
    if (len < 1) {
      return message;
    }
    for (int i = 0; i < len; i += 2) {
      message = message.replaceAll(replacements[i], replacements[i + 1]);
    }
    return message;
  }

  /**
   * Applies the given replacements to the message.
   *
   * @param message      message to update
   * @param replacements replacements to apply
   * @return the updated message, or {@code null} if the input message is
   *         {@code null} or empty
   */
  public static String applyReplacements(String message, final Replaceable... replacements) {
    if (message == null || message.length() == 0) {
      return null;
    }
    final int len = replacements.length;
    if (len == 0) {
      return message;
    }
    for (int i = 0; i < replacements.length; i++) {
      message = replacements[i].apply(message);
    }
    return message;
  }

  /**
   * Converts the given byte array to a lowercase hexadecimal string.
   *
   * @param bytes bytes to convert
   * @return the hexadecimal representation of the byte array
   */
  public static String bytesToHex(final byte[] bytes) {
    final byte[] hexChars = new byte[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars, StandardCharsets.UTF_8);
  }

  /**
   * Parses a locale string in {@code language[_country[_variant]]} format.
   *
   * @param string locale string to parse
   * @return the parsed {@link Locale}, or {@code null} if the input is
   *         {@code null}, empty, or unsupported
   */
  public static Locale parseLocale(final String string) {
    if (string == null || string.isEmpty()) {
      return null;
    }
    final String[] segments = string.split("_", 3); // language_country_variant
    final int length = segments.length;
    if (length == 1) {
      return Locale.of(string); // language
    } else if (length == 2) {
      return Locale.of(segments[0], segments[1]); // language + country
    } else if (length == 3) {
      return Locale.of(segments[0], segments[1], segments[2]); // language + country + variant
    }
    return null;
  }

  /**
   * Joins the given strings using the specified delimiter.
   *
   * @param delimiter delimiter to insert between elements
   * @param strings   strings to join
   * @return the joined string
   */
  public static String join(final String delimiter, final String[] strings) {
    final StringBuilder sb = new StringBuilder();
    for (final String string : strings) {
      sb.append(delimiter).append(string);
    }
    return sb.substring(1);
  }

  /**
   * Joins a range of the given string array using the specified delimiter.
   *
   * @param delimiter delimiter to insert between elements
   * @param strings   strings to join
   * @param begin     start index, inclusive
   * @return the joined string
   */
  public static String join(final String delimiter, final String[] strings, final int begin) {
    return join(delimiter, strings, begin, strings.length);
  }

  /**
   * Joins a range of the given string array using the specified delimiter.
   *
   * @param delimiter delimiter to insert between elements
   * @param strings   strings to join
   * @param begin     start index, inclusive
   * @param end       end index, exclusive
   * @return the joined string
   */
  public static String join(final String delimiter, final String[] strings, final int begin, final int end) {
    final StringBuilder sb = new StringBuilder();
    for (int i = begin; i < end; i++) {
      sb.append(delimiter).append(strings[i]);
    }
    return sb.substring(1);
  }

  /**
   * Joins the given string collection using the specified delimiter.
   *
   * @param delimiter delimiter to insert between elements
   * @param strings   strings to join
   * @return the joined string
   */
  public static String join(final String delimiter, final Collection<String> strings) {
    final StringBuilder sb = new StringBuilder();
    for (final String string : strings) {
      sb.append(delimiter).append(string);
    }
    return sb.substring(1);
  }

  /**
   * Joins a range of the given string list using the specified delimiter.
   *
   * @param delimiter delimiter to insert between elements
   * @param strings   strings to join
   * @param begin     start index, inclusive
   * @return the joined string
   */
  public static String join(final String delimiter, final List<String> strings, final int begin) {
    return join(delimiter, strings, begin, strings.size());
  }

  /**
   * Joins a range of the given string list using the specified delimiter.
   *
   * @param delimiter delimiter to insert between elements
   * @param strings   strings to join
   * @param begin     start index, inclusive
   * @param end       end index, exclusive
   * @return the joined string
   */
  public static String join(final String delimiter, final List<String> strings, final int begin, final int end) {
    final StringBuilder sb = new StringBuilder();
    for (int i = begin; i < end; i++) {
      sb.append(delimiter).append(strings.get(i));
    }
    return sb.substring(1);
  }

  /**
   * Copies strings from the given iterable that start with the provided token,
   * ignoring case.
   *
   * @param token      token to match at the start of each string
   * @param originals  source strings to scan
   * @param collection target collection to populate; if {@code null}, a new
   *                   collection is created
   * @param <T>        collection type parameter
   * @return the populated collection containing matching strings
   */
  @SuppressWarnings("unchecked")
  public static <T extends Collection<? super String>> T copyPartialMatches(final String token,
      final Iterable<String> originals, T collection) {
    if (collection == null) {
      collection = (T) new ArrayList<>();
    }
    if (originals == null) {
      return collection;
    }
    for (final String string : originals) {
      if (startsWithIgnoreCase(string, token)) {
        collection.add(string);
      }
    }

    return collection;
  }

  /**
   * Checks whether the given string starts with the specified prefix, ignoring
   * case.
   *
   * @param string string to test
   * @param prefix prefix to look for
   * @return {@code true} if the string starts with the prefix ignoring case, or
   *         if the prefix is {@code null}
   */
  public static boolean startsWithIgnoreCase(final String string, final String prefix) {
    if (prefix == null) {
      return true;
    }
    if (string == null || string.length() < prefix.length()) {
      return false;
    }
    return string.regionMatches(true, 0, prefix, 0, prefix.length());
  }

  /**
   * Splits the given string on the specified character using a fast index-based
   * approach.
   *
   * @param str string to split
   * @param ch  delimiter character
   * @return an array containing the split segments
   */
  public static String[] fastSplit(final String str, final char ch) {
    int off = 0;
    int next;
    final ArrayList<String> list = new ArrayList<>();
    while ((next = str.indexOf(ch, off)) != -1) {
      list.add(str.substring(off, next));
      off = next + 1;
    }

    if (off == 0) {
      return new String[] { str };
    }
    // Add remaining segment
    if (off < str.length()) {
      list.add(str.substring(off, str.length()));
    }
    return list.toArray(new String[list.size()]);
  }

  /**
   * Escapes special characters in the given string using backslash escape
   * sequences.
   *
   * @param input string to escape
   * @return the escaped string, or an empty string if the input is {@code null}
   *         or empty
   */
  public static String escape(final String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }
    final StringBuilder builder = new StringBuilder(input.length());
    final int len = input.length();
    for (int i = 0; i < len; i++) {
      final char c = input.charAt(i);
      switch (c) {
        case '\\' -> builder.append("\\\\");
        case '\n' -> builder.append("\\n");
        case '\r' -> builder.append("\\r");
        case '\t' -> builder.append("\\t");
        case '\f' -> builder.append("\\f");
        case '\b' -> builder.append("\\b");
        case '"' -> builder.append("\\\"");
        default -> {
          if (c < 0x20) {
            builder.append(String.format("\\u%04x", (int) c));
          } else {
            builder.append(c);
          }
        }
      }
    }
    return builder.toString();
  }

  /**
   * Unescapes backslash escape sequences in the given string.
   *
   * @param input string to unescape
   * @return the unescaped string, or the original input if it is {@code null} or
   *         empty
   */
  public static String unescape(final String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    final StringBuilder builder = new StringBuilder(input.length());
    final int len = input.length();
    for (int i = 0; i < len; i++) {
      final char c = input.charAt(i);
      if (c != '\\') {
        builder.append(c);
        continue;
      }

      // Escape sequence
      if (i == len - 1) {
        // Trailing backslash, append it
        builder.append('\\');
        break;
      }

      final char esc = input.charAt(++i);
      switch (esc) {
        case '\\' -> builder.append('\\');
        case 'n' -> builder.append('\n');
        case 'r' -> builder.append('\r');
        case 't' -> builder.append('\t');
        case 'f' -> builder.append('\f');
        case 'b' -> builder.append('\b');
        case '"' -> builder.append('"');
        case 'u' -> {
          // Unicode escape: expect 4 hex digits
          if (i + 4 >= len) {
            // Not enough chars for unicode escape, append literal
            builder.append("\\u");
            continue;
          }
          int code = 0;
          boolean ok = true;
          for (int j = 0; j < 4; j++) {
            final char ch = input.charAt(i + 1 + j);
            final int digit = Character.digit(ch, 16);
            if (digit == -1) {
              ok = false;
              break;
            }
            code = (code << 4) + digit;
          }
          if (ok) {
            builder.append((char) code);
            i += 4; // consumed 4 hex chars
          } else {
            // Invalid \\u sequence, append literal
            builder.append("\\u");
          }
        }
        default -> builder.append(esc);
      }
    }
    return builder.toString();
  }
}
