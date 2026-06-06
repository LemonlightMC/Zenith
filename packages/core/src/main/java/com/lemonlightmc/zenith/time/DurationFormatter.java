package com.lemonlightmc.zenith.time;

import java.time.Duration;

public enum DurationFormatter {
  LONG(false, Integer.MAX_VALUE),
  CONCISE(true, Integer.MAX_VALUE),
  CONCISE_LOW_elements(true, 3);

  private int elements;
  private boolean concise;

  DurationFormatter() {
    this.concise = true;
    this.elements = 3;
  }

  DurationFormatter(final boolean concise) {
    this.concise = concise;
    this.elements = 3;
  }

  DurationFormatter(final int elements) {
    this.concise = true;
    this.elements = elements;
  }

  DurationFormatter(final boolean concise, final int elements) {
    this.concise = concise;
    this.elements = elements;
  }

  public int getElements() {
    return elements;
  }

  public void setElements(int elements) {
    this.elements = elements;
  }

  public void setConcise(boolean concise) {
    this.concise = concise;
  }

  public boolean isConcise() {
    return concise;
  }

  public String format(final Duration duration) {
    return format(duration, concise, elements);
  }

  public static String format(final Duration duration, final boolean concise) {
    return format(duration, concise, 3);
  }

  public static String format(
      final Duration duration,
      final boolean concise,
      final int elements) {
    long seconds = duration.getSeconds();
    final StringBuilder output = new StringBuilder();
    int outputSize = 0;

    for (final PolyTimeUnit unit : PolyTimeUnit.values()) {
      final long n = seconds / unit.duration.toMillis();
      if (n > 0) {
        seconds -= unit.duration.toMillis() * n;
        output.append(' ').append(n).append(unit.toString(concise, n));
        outputSize++;
      }
      if (seconds <= 0 || outputSize >= elements) {
        break;
      }
    }

    if (output.length() == 0) {
      return ("0" + (PolyTimeUnit.SECONDS.toString(concise, 0)));
    }
    return output.substring(1);
  }
}
