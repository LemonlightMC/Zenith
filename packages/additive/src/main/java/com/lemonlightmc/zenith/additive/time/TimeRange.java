package com.lemonlightmc.zenith.additive.time;

/**
 * Represents an inclusive time range that can optionally wrap around.
 */
public class TimeRange {
  /** The inclusive minimum value of the range. */
  public final int min; // inclusive
  /** The inclusive maximum value of the range. */
  public final int max; // inclusive

  /**
   * Creates a new time range.
   *
   * @param min the inclusive minimum value
   * @param max the inclusive maximum value
   */
  public TimeRange(final int min, final int max) {
    this.min = min;
    this.max = max;
  }

  /**
   * Checks whether the given value is contained in this range.
   *
   * @param t the value to test
   * @return {@code true} if the value is within the range
   */
  public boolean contains(final int t) {
    // support wrap-around (e.g., 23000-2000)
    return (min <= max) ? (t >= min && t <= max) : (t >= min || t <= max);
  }

  /**
   * Returns this range as a simple string representation.
   *
   * @return the range formatted as {@code min-max}
   */
  public String asString() {
    return min + "-" + max;
  }

  @Override
  public int hashCode() {
    return 31 * (31 + min) + max;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    TimeRange other = (TimeRange) obj;
    return min == other.min && max == other.max;
  }

  @Override
  public String toString() {
    return "TimeRange [min=" + min + ", max=" + max + "]";
  }
}
