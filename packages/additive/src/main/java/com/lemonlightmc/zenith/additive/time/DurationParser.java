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

  public static long parse(String str) {
    if (str == null || str.length() == 0) {
      return 0l;
    }
    String[] parts = pattern1.split(str);
    long time = 0l;
    for (String part : parts) {
      time += parseSingle(part);
    }
    return time;
  }

  public static long parseSingle(String part) {
    if (part == null || part.length() == 0) {
      return 0l;
    }
    String[] parts2 = pattern2.split(part);
    if (parts2 == null || parts2.length != 2) {
      return 0l;
    }
    try {
      long time = NumberConversions.parseLong(parts2[0]);
      return PolyTimeUnit.from(parts2[1]).toMillis(time);
    } catch (Exception e) {
      return 0l;
    }
  }
}
