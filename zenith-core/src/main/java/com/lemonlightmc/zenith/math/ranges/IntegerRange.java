package com.lemonlightmc.zenith.math.ranges;

import java.util.Comparator;

import com.lemonlightmc.zenith.exceptions.RangeException;
import com.lemonlightmc.zenith.math.NumberConversions;

public class IntegerRange implements Range<IntegerRange, Integer> {
  public static IntegerRange ALL = new IntegerRange();
  public static final int MIN_VALUE = Integer.MIN_VALUE;
  public static final int MAX_VALUE = Integer.MAX_VALUE;

  private final Integer min;
  private final Integer max;

  private static Comparator<IntegerRange> comparator = null;

  public IntegerRange() {
    this(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  public IntegerRange(final Integer min) {
    this(min, Integer.MAX_VALUE);
  }

  public IntegerRange(final Integer min, final Integer max) {
    this.min = min == null ? Integer.MIN_VALUE : min;
    this.max = max == null ? Integer.MAX_VALUE : max;
    if (this.max < this.min) {
      throw new RangeException("Range Maximum is smaller then Minimum: " + this.min + " - " + this.max);
    }
  }

  public static IntegerRange at(final Integer pos) {
    return new IntegerRange(pos, pos);
  }

  public static IntegerRange from(final Integer min, final Integer max) {
    return new IntegerRange(min, max);
  }

  public static IntegerRange from(final IntegerRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new IntegerRange(range.min, range.max);
  }

  public static IntegerRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new IntegerRange(arr[0].length() == 0 ? Integer.MIN_VALUE : NumberConversions.parseInt(arr[0]),
        arr[1].length() == 0 ? Integer.MAX_VALUE : NumberConversions.parseInt(arr[1]));
  }

  public static IntegerRange encompassing(final IntegerRange a, final IntegerRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new IntegerRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static IntegerRange rangeGreaterThanOrEq(final Integer min) {
    return new IntegerRange(min, Integer.MAX_VALUE);
  }

  public static IntegerRange rangeLessThanOrEq(final Integer max) {
    return new IntegerRange(Integer.MIN_VALUE, max);
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
  public boolean isAfter(final IntegerRange range) {
    return range == null ? true : min > range.max;
  }

  @Override
  public boolean isBefore(final Integer num) {
    return max < num;
  }

  @Override
  public boolean isBefore(final IntegerRange range) {
    return max < range.min;
  }

  @Override
  public boolean startsWith(final Integer num) {
    return num == null ? false : min.intValue() == num.intValue();
  }

  @Override
  public boolean startsWith(final IntegerRange range) {
    return range == null ? false : min.intValue() == range.min.intValue();
  }

  @Override
  public boolean endsWith(final Integer num) {
    return num == null ? false : max.intValue() == num.intValue();
  }

  @Override
  public boolean endsWith(final IntegerRange range) {
    return range == null ? false : max.intValue() == range.max.intValue();
  }

  @Override
  public boolean isEmpty() {
    return min.intValue() == max.intValue();
  }

  @Override
  public boolean contains(final Integer num) {
    return num == null ? false : isInRange(num);
  }

  @Override
  public boolean contains(final IntegerRange range) {
    return range == null ? false : isInRange(range.min) && isInRange(range.max);
  }

  @Override
  public boolean overlaps(final IntegerRange range) {
    return range == null ? false : !(max < range.min || min > range.max);
  }

  @Override
  public IntegerRange intersection(final IntegerRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new IntegerRange(Math.max(min, range.min), Math.min(max, range.max));
  }

  @Override
  public Integer clamp(final Integer num) {
    return num == null ? min : num < min ? min : num > max ? max : num;
  }

  @Override
  public IntegerRange clamp(final IntegerRange range) {
    if (range == null) {
      return new IntegerRange(min, max);
    }
    return new IntegerRange(clamp(range.min), clamp(range.max));
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
  public int compareTo(final IntegerRange o) {
    if (o == null) {
      return 1;
    }
    return Integer.compare(this.max - this.min, o.max - o.min);
  }

  @Override
  public Comparator<IntegerRange> getComparator() {
    if (comparator == null) {
      comparator = Range.createComparator();
    }
    return comparator;
  }

  @Override
  public IntegerRange clone() {
    return new IntegerRange(min, max);
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
    return 31 * (31 * min) + max;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final IntegerRange other = (IntegerRange) obj;
    return min.equals(other.min) && max.equals(other.max);
  }
}
