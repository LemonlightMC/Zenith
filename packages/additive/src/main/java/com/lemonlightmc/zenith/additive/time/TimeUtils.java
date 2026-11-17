package com.lemonlightmc.zenith.additive.time;

import java.time.Duration;
import java.time.Instant;

/**
 * Utility methods for working with current time values, durations, and time parsing.
 */
public final class TimeUtils {
  /**
   * Returns the current high-resolution time source value.
   *
   * @return the current nano time value
   */
  public static long nanoTime() {
    return System.nanoTime();
  }

  /**
   * Returns the current time in milliseconds.
   *
   * @return the current epoch time in milliseconds
   */
  public static long nowMillis() {
    return System.currentTimeMillis();
  }

  /**
   * Returns the current time in nanoseconds derived from the system clock.
   *
   * @return the current epoch time in nanoseconds
   */
  public static long nowNanos() {
    return System.currentTimeMillis() * 1000L;
  }

  /**
   * Returns the current time in seconds.
   *
   * @return the current epoch time in seconds
   */
  public static long nowSeconds() {
    return System.currentTimeMillis() / 1000L;
  }

  /**
   * Returns the current instant.
   *
   * @return the current {@link Instant}
   */
  public static Instant now() {
    return Instant.ofEpochMilli(System.currentTimeMillis());
  }

  /**
   * Returns the absolute duration between now and the given instant.
   *
   * @param other the instant to compare against now
   * @return the absolute duration between the instants
   */
  public static Duration between(final Instant other) {
    return Duration.between(now(), other).abs();
  }

  /**
   * Returns the absolute duration between two instants.
   *
   * @param now the starting instant
   * @param other the instant to compare against
   * @return the absolute duration between the instants
   */
  public static Duration between(final Instant now, final Instant other) {
    return Duration.between(now, other).abs();
  }

  /**
   * Checks whether the current time is before the given timestamp.
   *
   * @param millis the timestamp to compare against
   * @return {@code true} if the current time is before the timestamp
   */
  public static boolean isBefore(final long millis) {
    return System.currentTimeMillis() < millis;
  }

  /**
   * Checks whether the current time equals the given timestamp.
   *
   * @param millis the timestamp to compare against
   * @return {@code true} if the current time equals the timestamp
   */
  public static boolean isNow(final long millis) {
    return System.currentTimeMillis() == millis;
  }

  /**
   * Checks whether the current time is after the given timestamp.
   *
   * @param millis the timestamp to compare against
   * @return {@code true} if the current time is after the timestamp
   */
  public static boolean isAfter(final long millis) {
    return System.currentTimeMillis() > millis;
  }

  /**
   * Creates a duration from a millisecond value.
   *
   * @param millis the duration value in milliseconds
   * @return the created duration
   */
  public static Duration duration(final long millis) {
    return duration(millis, PolyTimeUnit.MILLISECONDS);
  }

  /**
   * Creates a duration from a value in the specified unit.
   *
   * @param millis the duration value
   * @param unit the unit of the provided value
   * @return the created duration
   * @throws NullPointerException if {@code unit} is {@code null}
   * @throws AssertionError if the unit is not supported
   */
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

  /**
   * Formats a duration using concise output.
   *
   * @param duration the duration to format
   * @return the formatted duration string
   */
  public static String format(final Duration duration) {
    return DurationFormatter.format(duration, true);
  }

  /**
   * Formats a duration.
   *
   * @param duration the duration to format
   * @param concise whether to use concise unit labels
   * @return the formatted duration string
   */
  public static String format(final Duration duration, final boolean concise) {
    return DurationFormatter.format(duration, concise);
  }

  /**
   * Formats a duration using concise output and a maximum number of elements.
   *
   * @param duration the duration to format
   * @param elements the maximum number of time elements to include
   * @return the formatted duration string
   */
  public static String format(final Duration duration, final int elements) {
    return DurationFormatter.format(duration, true, elements);
  }

  /**
   * Formats a duration.
   *
   * @param duration the duration to format
   * @param concise whether to use concise unit labels
   * @param elements the maximum number of time elements to include
   * @return the formatted duration string
   */
  public static String format(final Duration duration, final boolean concise, final int elements) {
    return DurationFormatter.format(duration, concise, elements);
  }

  /**
   * Parses a duration string into milliseconds.
   *
   * @param str the duration string to parse
   * @return the parsed duration in milliseconds
   */
  public static long parseDuration(final String str) {
    return DurationParser.parse(str);
  }

  /**
   * Parses a numeric duration string using the specified unit.
   *
   * @param str the duration string to parse
   * @param unit the unit to apply to the parsed value
   * @return the parsed duration in milliseconds
   */
  public static long parseDuration(final String str, final PolyTimeUnit unit) {
    return DurationParser.parse(str, unit);
  }

  private TimeUtils() {
    throw new UnsupportedOperationException(
        "This class cannot be instantiated");
  }
}
