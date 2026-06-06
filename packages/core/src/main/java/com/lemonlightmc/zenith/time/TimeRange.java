package com.lemonlightmc.zenith.time;

public class TimeRange {
  public final int min; // inclusive
  public final int max; // inclusive

  public TimeRange(final int min, final int max) {
    this.min = min;
    this.max = max;
  }

  public boolean contains(final int t) {
    // support wrap-around (e.g., 23000-2000)
    return (min <= max) ? (t >= min && t <= max) : (t >= min || t <= max);
  }

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
