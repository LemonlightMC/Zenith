package com.lemonlightmc.zenith.time;

import java.time.Duration;
import java.time.Instant;

public final class TimeUtils {
  public static long nanoTime() {
    return System.nanoTime();
  }

  public static long nowMillis() {
    return System.currentTimeMillis();
  }

  public static long nowNanos() {
    return System.currentTimeMillis() * 1000L;
  }

  public static long nowSeconds() {
    return System.currentTimeMillis() / 1000L;
  }

  public static Instant now() {
    return Instant.ofEpochMilli(System.currentTimeMillis());
  }

  public static Duration between(final Instant other) {
    return Duration.between(now(), other).abs();
  }

  public static Duration between(final Instant now, final Instant other) {
    return Duration.between(now, other).abs();
  }

  public static boolean isBefore(final long millis) {
    return System.currentTimeMillis() < millis;
  }

  public static boolean isNow(final long millis) {
    return System.currentTimeMillis() == millis;
  }

  public static boolean isAfter(final long millis) {
    return System.currentTimeMillis() > millis;
  }

  public static Duration duration(final long millis) {
    return duration(millis, PolyTimeUnit.MILLISECONDS);
  }

  public static Duration duration(final long millis, final PolyTimeUnit unit) {
    if (unit == null) {
      throw new NullPointerException("Unit cannot be null");
    }
    switch (unit) {
      case PolyTimeUnit.NANOSSECONDS:
        return Duration.ofNanos(millis);
      case PolyTimeUnit.MICROSECONDS:
        return Duration.ofNanos(millis / 1000);
      case PolyTimeUnit.MILLISECONDS:
        return Duration.ofMillis(millis);
      case PolyTimeUnit.SECONDS:
        return Duration.ofSeconds(millis);
      case PolyTimeUnit.MINUTES:
        return Duration.ofMinutes(millis);
      case PolyTimeUnit.HOURS:
        return Duration.ofHours(millis);
      case PolyTimeUnit.DAYS:
        return Duration.ofDays(millis);
      default:
        throw new AssertionError("unknown time unit: " + unit);
    }
  }

  public static String format(final Duration duration) {
    return DurationFormatter.format(duration, true);
  }

  public static String format(final Duration duration, final boolean concise) {
    return DurationFormatter.format(duration, concise);
  }

  public static String format(final Duration duration, final int elements) {
    return DurationFormatter.format(duration, true, elements);
  }

  public static String format(final Duration duration, final boolean concise, final int elements) {
    return DurationFormatter.format(duration, concise, elements);
  }

  private TimeUtils() {
    throw new UnsupportedOperationException(
        "This class cannot be instantiated");
  }
}
