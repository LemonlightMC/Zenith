package com.lemonlightmc.zenith.additive.time;

import java.util.regex.Pattern;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

public class DurationParser {
  private static final Pattern pattern1 = Pattern
      .compile(
          "[1-9]+(?:\\.\\w+)?\\s*(?:tick|ticks|sec|second|seconds|min|minute|minutes|hour|hours|day|days|week|weeks|month|months|year|years|[tsmhdwy])");
  private static final Pattern pattern2 = Pattern
      .compile(
          "([1-9]+(?:\\.\\w+)?\\s*)(tick|ticks|sec|second|seconds|min|minute|minutes|hour|hours|day|days|week|weeks|month|months|year|years|[tsmhdwy])");

  public static long parse(final String str) {
    if (str == null || str.length() == 0) {
      return 0l;
    }
    final long time = 0l;
    for (final String part : pattern1.split(str)) {
      if (part.isEmpty()) {
        continue;
      }
      final String[] parts2 = pattern2.split(part);
      if (parts2.length != 2) {
        continue;
      }
      try {
        return PolyTimeUnit.from(parts2[1]).toMillis() * NumberConversions.parseLong(parts2[0]);
      } catch (final Exception e) {
        continue;
      }
    }
    return time;
  }

  public static long parse(final String str, final PolyTimeUnit unit) {
    if (str == null || str.length() == 0) {
      return 0l;
    }
    try {
      return NumberConversions.parseLong(str) * unit.toMillis();
    } catch (final Exception e) {
      return 0;
    }
  }
}
