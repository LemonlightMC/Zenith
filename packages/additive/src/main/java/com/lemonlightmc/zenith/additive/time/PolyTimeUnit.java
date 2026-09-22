package com.lemonlightmc.zenith.additive.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public enum PolyTimeUnit {
  TICKS(TimeUnit.MILLISECONDS, 50, "t"),
  NANOSSECONDS(TimeUnit.NANOSECONDS, 1, "n"),
  MICROSECONDS(TimeUnit.MICROSECONDS, 1, "mics"),
  MILLISECONDS(TimeUnit.MILLISECONDS, 1, "ms"),
  SECONDS(TimeUnit.SECONDS, 1, "s"),
  MINUTES(TimeUnit.MINUTES, 1, "min"),
  HOURS(TimeUnit.HOURS, 1, "h"),
  DAYS(TimeUnit.DAYS, 1, "d"),
  WEEKS(TimeUnit.DAYS, 7, "w"),
  MONTHS(TimeUnit.DAYS, 365 / 12, "M"),
  YEARS(TimeUnit.DAYS, 365, "y");

  private final TimeUnit unit;
  private final Duration duration;
  private String formalStringPlural;
  private String formalStringSingular;
  private final String conciseString;

  PolyTimeUnit(final TimeUnit unit, final long scale, final String conciseString) {
    this.unit = unit;
    this.duration = unit.toChronoUnit().getDuration().multipliedBy(scale);
    this.conciseString = conciseString;
  }

  private String getFormalSingularString() {
    if (formalStringSingular == null) {
      formalStringSingular = unit.name().substring(0, unit.name().length() - 1).toLowerCase(Locale.ROOT);
    }
    return formalStringSingular;
  }

  private String getFormalPluralString() {
    if (formalStringPlural == null) {
      formalStringPlural = unit.name().toLowerCase(Locale.ROOT);
    }
    return formalStringPlural;
  }

  public long convert(final long srcDuration, final PolyTimeUnit srcUnit) {
    final long durationInMillis = srcUnit.toMillis(srcDuration);
    return this.unit.convert(durationInMillis, TimeUnit.MILLISECONDS);
  }

  public long toNanos() {
    return this.duration.toNanos();
  }

  public long toNanos(final long duration) {
    return this.duration.toNanos() * duration;
  }

  public long toMillis() {
    return this.duration.toMillis();
  }

  public long toMillis(final long duration) {
    return this.duration.toMillis() * duration;
  }

  public long toTicks() {
    return Ticks.fromDuration(this.duration);
  }

  public long toTicks(final long duration) {
    return Ticks.fromDuration(this.duration) * duration;
  }

  public long toSeconds() {
    return this.duration.toSeconds();
  }

  public long toSeconds(final long duration) {
    return this.duration.toSeconds() * duration;
  }

  public long toMinutes() {
    return this.duration.toMinutes();
  }

  public long toMinutes(final long duration) {
    return this.duration.toMinutes() * duration;
  }

  public long toHours() {
    return this.duration.toHours();
  }

  public long toHours(final long duration) {
    return this.duration.toHours() * duration;
  }

  public long toDays() {
    return this.duration.toDays();
  }

  public long toDays(final long duration) {
    return this.duration.toDays() * duration;
  }

  public long to(final long duration, final PolyTimeUnit unit) {
    if (isSmaller(unit)) {
      return unit.toMillis() * this.duration.toMillis();
    } else if (isBigger(unit)) {
      return this.duration.toMillis() / unit.toMillis();
    } else {
      return this.duration.toMillis();
    }
  }

  public ChronoUnit toChronoUnit() {
    return this.unit.toChronoUnit();
  }

  public TimeUnit toTimeUnit() {
    return this.unit;
  }

  public Duration toDuration() {
    return this.duration;
  }

  public boolean isSame(final PolyTimeUnit unit) {
    return this.duration.compareTo(unit.toDuration()) == 0;
  }

  public boolean isBigger(final PolyTimeUnit unit) {
    return this.duration.compareTo(unit.toDuration()) == 1;
  }

  public boolean isSmaller(final PolyTimeUnit unit) {
    return this.duration.compareTo(unit.toDuration()) == -1;
  }

  public boolean between(final PolyTimeUnit lower, final PolyTimeUnit upper) {
    return this.duration.compareTo(upper.toDuration()) + this.duration.compareTo(lower.toDuration()) == 0;
  }

  public String toString(final boolean concise, final long n) {
    if (concise) {
      return conciseString;
    }
    if (n == 1) {
      return getFormalSingularString();
    } else {
      return getFormalPluralString();
    }
  }

  public static PolyTimeUnit from(final long millis) {
    if (millis > 0 && millis < 1000) {
      return millis % 50 == 0 ? TICKS : MILLISECONDS;
    } else if (millis >= 1000 && millis < 60000) {
      return SECONDS;
    } else if (millis >= 60000 && millis < 3600000) {
      return MINUTES;
    } else if (millis >= 3600000 && millis < 86400000) {
      return HOURS;
    } else if (millis >= 86400000 && millis < 604800000) {
      return DAYS;
    } else if (millis >= 604800000 && millis < 2629800000L) {
      return WEEKS;
    } else if (millis >= 2629800000L && millis < 31557600000L) {
      return MONTHS;
    } else if (millis >= 31557600000L) {
      return YEARS;
    }
    throw new IllegalArgumentException(
        "No matching TimeUnit for duration " + millis);
  }

  public static PolyTimeUnit fromPrecise(final long millis) {
    for (final PolyTimeUnit unit : values()) {
      if (unit.duration.toMillis() == millis) {
        return unit;
      }
    }
    throw new IllegalArgumentException(
        "No matching TimeUnit for duration " + millis);
  }

  public static PolyTimeUnit from(final Duration duration) {
    return from(duration.toMillis());
  }

  public static PolyTimeUnit from(final PolyTimeUnit unit) {
    return from(unit.toDuration().toMillis());
  }

  public static PolyTimeUnit from(final ChronoUnit unit) {
    return from(unit.getDuration().toMillis());
  }

  public static PolyTimeUnit from(final TimeUnit unit) {
    return from(unit.toChronoUnit().getDuration().toMillis());
  }

  public static PolyTimeUnit from(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    str = str.trim();
    for (final PolyTimeUnit unit : values()) {
      if (str.equalsIgnoreCase(unit.conciseString)) {
        return unit;
      }
    }
    for (final PolyTimeUnit unit : values()) {
      if (str.equalsIgnoreCase(unit.getFormalSingularString()) ||
          str.equalsIgnoreCase(unit.getFormalPluralString())) {
        return unit;
      }
    }
    return null;
  }
}
