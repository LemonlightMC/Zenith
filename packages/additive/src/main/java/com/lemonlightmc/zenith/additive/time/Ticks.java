package com.lemonlightmc.zenith.additive.time;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class Ticks {

  public static final int TICKS_PER_SECOND = 20;
  public static final int MILLISECONDS_PER_SECOND = 1000;
  public static final int MILLISECONDS_PER_TICK = MILLISECONDS_PER_SECOND / TICKS_PER_SECOND;

  public static Duration toDuration(final long ticks) {
    return Duration.ofMillis(ticks * MILLISECONDS_PER_TICK);
  }

  public static Duration toDuration(final long ticks, final TimeUnit unit) {
    if (unit == null) {
      return toDuration(ticks);
    }
    return Duration.ofMillis(unit.toMillis(ticks) * MILLISECONDS_PER_TICK);
  }

  public static int fromDuration(final Duration duration) {
    if (duration == null) {
      return 0;
    }
    return (int) Math.ceil(duration.toMillis() / MILLISECONDS_PER_TICK);
  }

  public static int from(final long millis) {
    return (int) Math.ceil(millis / MILLISECONDS_PER_TICK);
  }

  public static int from(final long duration, final TimeUnit unit) {
    return (int) Math.ceil(unit.toMillis(duration) / MILLISECONDS_PER_TICK);
  }

  public static int from(final Duration duration) {
    if (duration == null) {
      return 0;
    }
    return (int) Math.ceil(duration.toMillis() / MILLISECONDS_PER_TICK);
  }

  public static long to(final long ticks, final TimeUnit unit) {
    if (unit == null) {
      return ticks * MILLISECONDS_PER_TICK;
    }
    return unit.convert(ticks * MILLISECONDS_PER_TICK, TimeUnit.MILLISECONDS);
  }

  public static long toMillis(final long ticks) {
    return ticks * 50;
  }

  public static int fromMillis(final long millis) {
    return (int) Math.ceil(millis / MILLISECONDS_PER_TICK);
  }

  public static String format(final long ticks) {
    return DurationFormatter.format(toDuration(ticks), true);
  }

  public static String format(final long ticks, final TimeUnit unit) {
    return DurationFormatter.format(toDuration(ticks, unit), true);
  }

  private Ticks() {
    throw new UnsupportedOperationException(
        "This class cannot be instantiated");
  }
}
