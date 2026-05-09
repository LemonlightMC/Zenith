package com.lemonlightmc.zenith.math.ranges;

import java.util.Comparator;

import com.lemonlightmc.zenith.exceptions.RangeException;

public class LongRange implements Range<LongRange, Long> {
  public static LongRange ALL = new LongRange();

  private final Long min;
  private final Long max;

  private static final Comparator<LongRange> comparator = new Comparator<LongRange>() {
    @Override
    public int compare(final LongRange o1, final LongRange o2) {
      if (o1 == null) {
        return -1;
      }
      if (o2 == null) {
        return 1;
      }
      return Long.compare(o1.max - o1.min, o2.max - o2.min);
    }
  };

  public LongRange() {
    this(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  public LongRange(final Long min) {
    this(min, Long.MAX_VALUE);
  }

  public LongRange(final Long min, final Long max) {
    this.min = min == null ? Long.MIN_VALUE : min;
    this.max = max == null ? Long.MAX_VALUE : max;
    if (Math.signum(this.max - this.min) == -1.0d) {
      throw new RangeException("Long Argument Maximum is smaller Minimum: " + this.min + " - " + this.max);
    }
  }

  public static LongRange at(final Long pos) {
    return new LongRange(pos, pos);
  }

  public static LongRange of(final Long min, final Long max) {
    return new LongRange(min, max);
  }

  public static LongRange of(final LongRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new LongRange(range.min, range.max);
  }

  public static LongRange of(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parse(str);
    return new LongRange(arr[0].length() == 0 ? Long.MIN_VALUE : Long.decode(arr[0]),
        arr[1].length() == 0 ? Long.MAX_VALUE : Long.decode(arr[1]));
  }

  public static LongRange encompassing(final LongRange a, final LongRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new LongRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static LongRange rangeGreaterThanOrEq(final Long min) {
    return new LongRange(min, Long.MAX_VALUE);
  }

  public static LongRange rangeLessThanOrEq(final Long max) {
    return new LongRange(Long.MIN_VALUE, max);
  }

  @Override
  public Long getMin() {
    return this.min;
  }

  @Override
  public Long getMax() {
    return this.max;
  }

  @Override
  public Long getMiddle() {
    return min + (max - min) / 2;
  }

  @Override
  public boolean isLower(final Long num) {
    return num == null ? true : num < min;
  }

  @Override
  public boolean isHigher(final Long num) {
    return num == null ? false : num > max;
  }

  @Override
  public boolean isInRange(final Long num) {
    return num == null ? false : num >= min && num <= max;
  }

  @Override
  public boolean isOutsideRange(final Long num) {
    return num == null ? true : num < min || num > max;
  }

  @Override
  public boolean isAfter(final Long num) {
    return min > num;
  }

  @Override
  public boolean isAfter(final LongRange range) {
    return range == null ? true : min > range.max;
  }

  @Override
  public boolean isBefore(final Long num) {
    return max < num;
  }

  @Override
  public boolean isBefore(final LongRange range) {
    return max < range.min;
  }

  @Override
  public boolean startsWith(final Long num) {
    return num == null ? false : min.longValue() == num.longValue();
  }

  @Override
  public boolean startsWith(final LongRange range) {
    return range == null ? false : min.longValue() == range.min.longValue();
  }

  @Override
  public boolean endsWith(final Long num) {
    return num == null ? false : max.longValue() == num.longValue();
  }

  @Override
  public boolean endsWith(final LongRange range) {
    return range == null ? false : max.longValue() == range.max.longValue();
  }

  @Override
  public boolean isEmpty() {
    return min.longValue() == max.longValue();
  }

  @Override
  public boolean contains(final Long num) {
    return num == null ? false : isInRange(num);
  }

  @Override
  public boolean contains(final LongRange range) {
    return range == null ? false : isInRange(range.min) && isInRange(range.max);
  }

  @Override
  public boolean overlaps(final LongRange range) {
    return range == null ? false : !(max < range.min || min > range.max);
  }

  @Override
  public LongRange intersection(final LongRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new LongRange(Math.max(min, range.min), Math.min(max, range.max));
  }

  @Override
  public Long clamp(final Long num) {
    return num == null ? min : num < min ? min : num > max ? max : num;
  }

  @Override
  public LongRange clamp(final LongRange range) {
    if (range == null) {
      return new LongRange(min, max);
    }
    return new LongRange(clamp(range.min), clamp(range.max));
  }

  @Override
  public Long getLength() {
    return max - min;
  }

  @Override
  public boolean isMaxValue() {
    return max == Long.MAX_VALUE;
  }

  @Override
  public boolean isMinValue() {
    return max == Long.MIN_VALUE;
  }

  @Override
  public int compareTo(final LongRange o) {
    if (o == null) {
      return 1;
    }
    return Long.compare(this.max - this.min, o.max - o.min);
  }

  @Override
  public Comparator<LongRange> getComparator() {
    return LongRange.comparator;
  }

  @Override
  public LongRange clone() {
    return new LongRange(min, max);
  }

  @Override
  public String toString() {
    if (this.min == Long.MIN_VALUE && this.max == Long.MAX_VALUE) {
      return "";
    } else if (this.max == Long.MAX_VALUE) {
      return this.min + "..";
    } else if (this.min == Long.MIN_VALUE) {
      return ".." + this.max;
    } else {
      return this.min + ".." + this.max;
    }
  }

  @Override
  public int hashCode() {
    return 31 * (31 * Long.hashCode(min)) + Long.hashCode(max);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final LongRange other = (LongRange) obj;
    return min.equals(other.min) && max.equals(other.max);
  }
}
