package com.lemonlightmc.zenith.additive.time;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for converting between Minecraft ticks and standard time units.
 */
public final class Ticks {

  /** The number of game ticks in one second. */
  public static final int TICKS_PER_SECOND = 20;
  /** The number of milliseconds in one second. */
  public static final int MILLISECONDS_PER_SECOND = 1000;
  /** The number of milliseconds represented by one tick. */
  public static final int MILLISECONDS_PER_TICK = MILLISECONDS_PER_SECOND / TICKS_PER_SECOND;

  /**
   * Converts ticks to a {@link Duration}.
   *
   * @param ticks the tick count to convert
   * @return the equivalent duration
   */
  public static Duration toDuration(final long ticks) {
    return Duration.ofMillis(ticks * MILLISECONDS_PER_TICK);
  }

  /**
   * Converts a tick count in the given unit to a {@link Duration}.
   *
   * @param ticks the tick count to convert
   * @param unit the unit of the provided tick count
   * @return the equivalent duration
   */
  public static Duration toDuration(final long ticks, final TimeUnit unit) {
    if (unit == null) {
      return toDuration(ticks);
    }
    return Duration.ofMillis(unit.toMillis(ticks) * MILLISECONDS_PER_TICK);
  }

  /**
   * Converts a duration to ticks.
   *
   * @param duration the duration to convert
   * @return the equivalent tick count, or {@code 0} if {@code duration} is {@code null}
   */
  public static int fromDuration(final Duration duration) {
    if (duration == null) {
      return 0;
    }
    return (int) Math.ceil(duration.toMillis() / MILLISECONDS_PER_TICK);
  }

  /**
   * Converts milliseconds to ticks.
   *
   * @param millis the millisecond value to convert
   * @return the equivalent tick count
   */
  public static int from(final long millis) {
    return (int) Math.ceil(millis / MILLISECONDS_PER_TICK);
  }

  /**
   * Converts a duration value in the given unit to ticks.
   *
   * @param duration the duration value to convert
   * @param unit the unit of the provided value
   * @return the equivalent tick count
   */
  public static int from(final long duration, final TimeUnit unit) {
    return (int) Math.ceil(unit.toMillis(duration) / MILLISECONDS_PER_TICK);
  }

  /**
   * Converts a duration to ticks.
   *
   * @param duration the duration to convert
   * @return the equivalent tick count, or {@code 0} if {@code duration} is {@code null}
   */
  public static int from(final Duration duration) {
    if (duration == null) {
      return 0;
    }
    return (int) Math.ceil(duration.toMillis() / MILLISECONDS_PER_TICK);
  }

  /**
   * Converts ticks to the specified time unit.
   *
   * @param ticks the tick count to convert
   * @param unit the target time unit
   * @return the converted duration value
   */
  public static long to(final long ticks, final TimeUnit unit) {
    if (unit == null) {
      return ticks * MILLISECONDS_PER_TICK;
    }
    return unit.convert(ticks * MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
  }

  /**
   * Converts ticks to milliseconds.
   *
   * @param ticks the tick count to convert
   * @return the equivalent duration in milliseconds
   */
  public static long toMillis(final long ticks) {
    return ticks * 50;
  }

  /**
   * Converts milliseconds to ticks.
   *
   * @param millis the millisecond value to convert
   * @return the equivalent tick count
   */
  public static int fromMillis(final long millis) {
    return (int) Math.ceil(millis / MILLISECONDS_PER_TICK);
  }

  /**
   * Formats a tick count as a human-readable duration string.
   *
   * @param ticks the tick count to format
   * @return the formatted duration string
   */
  public static String format(final long ticks) {
    return DurationFormatter.format(toDuration(ticks), true);
  }

  /**
   * Formats a tick count in the given unit as a human-readable duration string.
   *
   * @param ticks the tick count to format
   * @param unit the unit of the provided tick count
   * @return the formatted duration string
   */
  public static String format(final long ticks, final TimeUnit unit) {
    return DurationFormatter.format(toDuration(ticks, unit), true);
  }

  private Ticks() {
    throw new UnsupportedOperationException(
        "This class cannot be instantiated");
  }
}
