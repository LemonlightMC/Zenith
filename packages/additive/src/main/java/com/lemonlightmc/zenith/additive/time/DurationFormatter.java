package com.lemonlightmc.zenith.additive.time;

import java.time.Duration;

/**
 * Formats {@link Duration} values into human-readable strings.
 */
public enum DurationFormatter {
  /** Formats durations using long unit names. */
  LONG(false, Integer.MAX_VALUE),
  /** Formats durations using concise unit labels. */
  CONCISE(true, Integer.MAX_VALUE),
  /** Formats durations using concise unit labels with a small element limit. */
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

  /**
   * Returns the maximum number of elements included in formatted output.
   *
   * @return the maximum number of formatted elements
   */
  public int getElements() {
    return elements;
  }

  /**
   * Sets the maximum number of elements included in formatted output.
   *
   * @param elements the maximum number of formatted elements
   */
  public void setElements(final int elements) {
    this.elements = elements;
  }

  /**
   * Sets whether formatted output uses concise unit labels.
   *
   * @param concise {@code true} to use concise unit labels
   */
  public void setConcise(final boolean concise) {
    this.concise = concise;
  }

  /**
   * Returns whether formatted output uses concise unit labels.
   *
   * @return {@code true} if concise unit labels are used
   */
  public boolean isConcise() {
    return concise;
  }

  /**
   * Formats the given duration using this formatter's settings.
   *
   * @param duration the duration to format
   * @return the formatted duration string
   */
  public String format(final Duration duration) {
    return format(duration, concise, elements);
  }

  /**
   * Formats the given duration.
   *
   * @param duration the duration to format
   * @param concise whether to use concise unit labels
   * @return the formatted duration string
   */
  public static String format(final Duration duration, final boolean concise) {
    return format(duration, concise, 3);
  }

  /**
   * Formats the given duration.
   *
   * @param duration the duration to format
   * @param concise whether to use concise unit labels
   * @param elements the maximum number of time elements to include
   * @return the formatted duration string
   */
  public static String format(
      final Duration duration,
      final boolean concise,
      final int elements) {
    long seconds = duration.getSeconds();
    final StringBuilder output = new StringBuilder();
    int outputSize = 0;

    for (final PolyTimeUnit unit : PolyTimeUnit.values()) {
      final long n = seconds / unit.toDuration().toMillis();
      if (n > 0) {
        seconds -= unit.toDuration().toMillis() * n;
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
