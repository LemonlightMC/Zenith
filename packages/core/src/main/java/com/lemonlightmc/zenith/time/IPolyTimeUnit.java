package com.lemonlightmc.zenith.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public interface IPolyTimeUnit extends Cloneable {

  public long convert(final long srcDuration, final IPolyTimeUnit srcUnit);

  public long toNanos();

  public long toNanos(final long duration);

  public long toMillis();

  public long toMillis(final long duration);

  public long toTicks();

  public long toTicks(final long duration);

  public long toSeconds();

  public long toSeconds(final long duration);

  public long toMinutes();

  public long toMinutes(final long duration);

  public long toHours();

  public long toHours(final long duration);

  public long toDays();

  public long toDays(final long duration);

  public long to(final long duration, final IPolyTimeUnit unit);

  public ChronoUnit toChronoUnit();

  public TimeUnit toTimeUnit();

  public Duration toDuration();

  public boolean isSame(final IPolyTimeUnit unit);

  public boolean isBigger(final IPolyTimeUnit unit);

  public boolean isSmaller(final IPolyTimeUnit unit);

  public boolean between(final IPolyTimeUnit lower, final IPolyTimeUnit upper);

  public String toString(final boolean concise, final long n);
}
