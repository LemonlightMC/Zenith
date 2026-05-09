package com.lemonlightmc.zenith.math.ranges;

import java.util.Comparator;

import com.lemonlightmc.zenith.exceptions.RangeException;

public class StringRange implements Range<StringRange, Integer> {
  public static final StringRange ALL = new StringRange();

  private final Integer min;
  private final Integer max;

  private static final Comparator<StringRange> comparator = new Comparator<StringRange>() {
    @Override
    public int compare(final StringRange o1, final StringRange o2) {
      if (o1 == null) {
        return -1;
      }
      if (o2 == null) {
        return 1;
      }
      return Integer.compare(o1.max - o1.min, o2.max - o2.min);
    }
  };

  public StringRange() {
    this(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  public StringRange(final Integer min) {
    this(min, Integer.MAX_VALUE);
  }

  public StringRange(final Integer min, final Integer max) {
    this.min = min == null ? Integer.MIN_VALUE : min;
    this.max = max == null ? Integer.MAX_VALUE : max;
    if (Math.signum(this.max - this.min) == -1.0d) {
      throw new RangeException("Integer Argument Maximum is smaller Minimum: " + this.min + " - " + this.max);
    }
  }

  public static StringRange at(final Integer pos) {
    return new StringRange(pos, pos);
  }

  public static StringRange of(final Integer min, final Integer max) {
    return new StringRange(min, max);
  }

  public static StringRange of(final StringRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new StringRange(range.min, range.max);
  }

  public static StringRange of(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parse(str);
    return new StringRange(arr[0].length() == 0 ? Integer.MIN_VALUE : Integer.decode(arr[0]),
        arr[1].length() == 0 ? Integer.MAX_VALUE : Integer.decode(arr[1]));
  }

  public static StringRange encompassing(final StringRange a, final StringRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new StringRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static StringRange rangeGreaterThanOrEq(final Integer min) {
    return new StringRange(min, Integer.MAX_VALUE);
  }

  public static StringRange rangeLessThanOrEq(final Integer max) {
    return new StringRange(Integer.MIN_VALUE, max);
  }

  @Override
  public Integer getMin() {
    return this.min;
  }

  @Override
  public Integer getMiddle() {
    return min + (max - min) / 2;
  }

  @Override
  public Integer getMax() {
    return this.max;
  }

  @Override
  public boolean isLower(final Integer num) {
    return num == null ? true : num < min;
  }

  @Override
  public boolean isHigher(final Integer num) {
    return num == null ? false : num > max;
  }

  @Override
  public boolean isInRange(final Integer num) {
    return num == null ? false : num >= min && num <= max;
  }

  @Override
  public boolean isOutsideRange(final Integer num) {
    return num == null ? true : num < min || num > max;
  }

  @Override
  public boolean isAfter(final Integer num) {
    return min > num;
  }

  @Override
  public boolean isAfter(final StringRange range) {
    return range == null ? true : min > range.max;
  }

  @Override
  public boolean isBefore(final Integer num) {
    return max < num;
  }

  @Override
  public boolean isBefore(final StringRange range) {
    return max < range.min;
  }

  @Override
  public boolean startsWith(final Integer num) {
    return num == null ? false : min.intValue() == num.intValue();
  }

  @Override
  public boolean startsWith(final StringRange range) {
    return range == null ? false : min.intValue() == range.min.intValue();
  }

  @Override
  public boolean endsWith(final Integer num) {
    return num == null ? false : max.intValue() == num.intValue();
  }

  @Override
  public boolean endsWith(final StringRange range) {
    return range == null ? false : max.intValue() == range.max.intValue();
  }

  @Override
  public boolean isEmpty() {
    return min == max;
  }

  @Override
  public boolean contains(final Integer num) {
    return num == null ? false : isInRange(num);
  }

  @Override
  public boolean contains(final StringRange range) {
    return range == null ? false : isInRange(range.min) && isInRange(range.max);
  }

  @Override
  public boolean overlaps(final StringRange range) {
    return range == null ? false : !(max < range.min || min > range.max);
  }

  @Override
  public StringRange intersection(final StringRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new StringRange(Math.max(min, range.min), Math.min(max, range.max));
  }

  @Override
  public Integer clamp(final Integer num) {
    return num == null ? min : num < min ? min : num > max ? max : num;
  }

  @Override
  public StringRange clamp(final StringRange range) {
    if (range == null) {
      return new StringRange(min, max);
    }
    return new StringRange(clamp(range.min), clamp(range.max));
  }

  @Override
  public Integer getLength() {
    return max - min;
  }

  @Override
  public boolean isMaxValue() {
    return max == Integer.MAX_VALUE;
  }

  @Override
  public boolean isMinValue() {
    return max == Integer.MIN_VALUE;
  }

  @Override
  public int compareTo(final StringRange o) {
    if (o == null) {
      return 1;
    }
    return Integer.compare(this.max - this.min, o.max - o.min);
  }

  @Override
  public Comparator<StringRange> getComparator() {
    return StringRange.comparator;
  }

  @Override
  public StringRange clone() {
    return new StringRange(min, max);
  }

  @Override
  public String toString() {
    if (this.min == Integer.MIN_VALUE && this.max == Integer.MAX_VALUE) {
      return "";
    } else if (this.max == Integer.MAX_VALUE) {
      return this.min + "..";
    } else if (this.min == Integer.MIN_VALUE) {
      return ".." + this.max;
    } else {
      return this.min + ".." + this.max;
    }
  }

  @Override
  public int hashCode() {
    return 31 * (31 * Integer.hashCode(min)) + Integer.hashCode(max);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final StringRange other = (StringRange) obj;
    return min.equals(other.min) && max.equals(other.max);
  }
}
