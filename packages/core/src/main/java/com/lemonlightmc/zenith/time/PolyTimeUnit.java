package com.lemonlightmc.zenith.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public enum PolyTimeUnit implements IPolyTimeUnit {
  TICKS(TimeUnit.MILLISECONDS, 50),
  NANOSSECONDS(TimeUnit.NANOSECONDS, 1),
  MICROSECONDS(TimeUnit.MICROSECONDS, 1),
  MILLISECONDS(TimeUnit.MILLISECONDS, 1),
  SECONDS(TimeUnit.SECONDS, 1),
  MINUTES(TimeUnit.MINUTES, 1),
  HOURS(TimeUnit.HOURS, 1),
  DAYS(TimeUnit.DAYS, 1),
  WEEKS(TimeUnit.DAYS, 7),
  MONTHS(TimeUnit.DAYS, 365 / 12),
  YEARS(TimeUnit.DAYS, 365);

  public final TimeUnit unit;
  public final Duration duration;
  public final String formalStringPlural;
  public final String formalStringSingular;
  public final String conciseString;

  PolyTimeUnit(final TimeUnit unit, final long scale) {
    this.unit = unit;
    this.duration = unit.toChronoUnit().getDuration().multipliedBy(scale);
    this.formalStringPlural = " " + unit.name().toLowerCase();
    this.formalStringSingular = " " + unit.name().substring(0, unit.name().length() - 1).toLowerCase();
    this.conciseString = String.valueOf(Character.toLowerCase(unit.name().charAt(0)));
  }

  @Override
  public long convert(final long srcDuration, final IPolyTimeUnit srcUnit) {
    final long durationInMillis = srcUnit.toMillis(srcDuration);
    return this.unit.convert(durationInMillis, TimeUnit.MILLISECONDS);
  }

  @Override
  public long toNanos() {
    return this.duration.toNanos();
  }

  @Override
  public long toNanos(final long duration) {
    return this.duration.toNanos() * duration;
  }

  @Override
  public long toMillis() {
    return this.duration.toMillis();
  }

  @Override
  public long toMillis(final long duration) {
    return this.duration.toMillis() * duration;
  }

  @Override
  public long toTicks() {
    return Ticks.fromDuration(this.duration);
  }

  @Override
  public long toTicks(final long duration) {
    return Ticks.fromDuration(this.duration) * duration;
  }

  @Override
  public long toSeconds() {
    return this.duration.toSeconds();
  }

  @Override
  public long toSeconds(final long duration) {
    return this.duration.toSeconds() * duration;
  }

  @Override
  public long toMinutes() {
    return this.duration.toMinutes();
  }

  @Override
  public long toMinutes(final long duration) {
    return this.duration.toMinutes() * duration;
  }

  @Override
  public long toHours() {
    return this.duration.toHours();
  }

  @Override
  public long toHours(final long duration) {
    return this.duration.toHours() * duration;
  }

  @Override
  public long toDays() {
    return this.duration.toDays();
  }

  @Override
  public long toDays(final long duration) {
    return this.duration.toDays() * duration;
  }

  @Override
  public long to(final long duration, final IPolyTimeUnit unit) {
    if (isSmaller(unit)) {
      return unit.toMillis() * this.duration.toMillis();
    } else if (isBigger(unit)) {
      return this.duration.toMillis() / unit.toMillis();
    } else {
      return this.duration.toMillis();
    }
  }

  @Override
  public ChronoUnit toChronoUnit() {
    return this.unit.toChronoUnit();
  }

  @Override
  public TimeUnit toTimeUnit() {
    return this.unit;
  }

  @Override
  public Duration toDuration() {
    return this.duration;
  }

  @Override
  public boolean isSame(final IPolyTimeUnit unit) {
    return this.duration.compareTo(unit.toDuration()) == 0;
  }

  @Override
  public boolean isBigger(final IPolyTimeUnit unit) {
    return this.duration.compareTo(unit.toDuration()) == 1;
  }

  @Override
  public boolean isSmaller(final IPolyTimeUnit unit) {
    return this.duration.compareTo(unit.toDuration()) == -1;
  }

  @Override
  public boolean between(final IPolyTimeUnit lower, final IPolyTimeUnit upper) {
    return this.duration.compareTo(upper.toDuration()) + this.duration.compareTo(lower.toDuration()) == 0;
  }

  @Override
  public String toString(final boolean concise, final long n) {
    if (concise) {
      return this.conciseString;
    }
    return n == 1 ? this.formalStringSingular : this.formalStringPlural;
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

  public static PolyTimeUnit from(final String s) {
    for (final PolyTimeUnit unit : values()) {
      if (s.equalsIgnoreCase(unit.formalStringSingular.trim()) ||
          s.equalsIgnoreCase(unit.formalStringPlural.trim()) ||
          s.equalsIgnoreCase(unit.conciseString)) {
        return unit;
      }
    }
    throw new IllegalArgumentException("No matching TimeUnit for string " + s);
  }

}
