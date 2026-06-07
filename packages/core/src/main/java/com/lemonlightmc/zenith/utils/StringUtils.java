package com.lemonlightmc.zenith.utils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class StringUtils {
  private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);

  public record Replaceable(String placeholder, String value) {
    public String apply(final String message) {
      return message.replaceAll(placeholder, value);
    }
  }

  public static boolean isBlank(final String str) {
    return str == null || str.length() == 0;
  }

  public static boolean isEmpty(final String str) {
    return str == null;
  }

  public static String applyReplacements(String message, final String... replacements) {
    if (message == null || message.length() == 0)
      return null;
    final int len = replacements.length;
    for (int i = 0; i < len; i += 2) {
      if (i >= len) {
        break;
      }
      message = message.replaceAll(replacements[i], replacements[i + 1]);
    }
    return message;
  }

  public static String applyReplacements(String message, final Replaceable... replacements) {
    if (message == null || message.length() == 0)
      return null;
    message = message.replace("\\n", "\n");
    for (int i = 0; i < replacements.length; i++) {
      message = replacements[i].apply(message);
    }
    return message;
  }

  public static String bytesToHex(final byte[] bytes) {
    final byte[] hexChars = new byte[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      final int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars, StandardCharsets.UTF_8);
  }

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

  public static String join(final String delimiter, final String[] strings) {
    final StringBuilder sb = new StringBuilder();
    for (final String string : strings) {
      sb.append(delimiter).append(string);
    }
    return sb.substring(1);
  }

  public static String join(final String delimiter, final String[] strings, final int begin) {
    return join(delimiter, strings, begin, strings.length);
  }

  public static String join(final String delimiter, final String[] strings, final int begin, final int end) {
    final StringBuilder sb = new StringBuilder();
    for (int i = begin; i < end; i++) {
      sb.append(delimiter).append(strings[i]);
    }
    return sb.substring(1);
  }

  public static String join(final String delimiter, final Collection<String> strings) {
    final StringBuilder sb = new StringBuilder();
    for (final String string : strings) {
      sb.append(delimiter).append(string);
    }
    return sb.substring(1);
  }

  public static String join(final String delimiter, final List<String> strings, final int begin) {
    return join(delimiter, strings, begin, strings.size());
  }

  public static String join(final String delimiter, final List<String> strings, final int begin, final int end) {
    final StringBuilder sb = new StringBuilder();
    for (int i = begin; i < end; i++) {
      sb.append(delimiter).append(strings.get(i));
    }
    return sb.substring(1);
  }

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

  public static boolean startsWithIgnoreCase(final String string, final String prefix) {
    if (prefix == null) {
      return true;
    }
    if (string == null || string.length() < prefix.length()) {
      return false;
    }
    return string.regionMatches(true, 0, prefix, 0, prefix.length());
  }

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
