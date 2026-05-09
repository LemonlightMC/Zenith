package com.lemonlightmc.zenith.math.ranges;

import java.util.Comparator;

import com.lemonlightmc.zenith.exceptions.RangeException;

public class DoubleRange implements Range<DoubleRange, Double> {
  public static DoubleRange ALL = new DoubleRange();

  private final Double min;
  private final Double max;

  private static final Comparator<DoubleRange> comparator = new Comparator<DoubleRange>() {
    @Override
    public int compare(final DoubleRange o1, final DoubleRange o2) {
      if (o1 == null) {
        return -1;
      }
      if (o2 == null) {
        return 1;
      }
      return Double.compare(o1.max - o1.min, o2.max - o2.min);
    }
  };

  public DoubleRange() {
    this(Double.MIN_VALUE, Double.MAX_VALUE);
  }

  public DoubleRange(final Double min) {
    this(min, Double.MAX_VALUE);
  }

  public DoubleRange(final Double min, final Double max) {
    this.min = min == null ? Double.MIN_VALUE : min;
    this.max = max == null ? Double.MAX_VALUE : max;
    if (Math.signum(this.max - this.min) == -1.0d) {
      throw new RangeException("Double Argument Maximum is smaller Minimum: " + this.min + " - " + this.max);
    }
  }

  public static DoubleRange at(final Double pos) {
    return new DoubleRange(pos, pos);
  }

  public static DoubleRange of(final Double min, final Double max) {
    return new DoubleRange(min, max);
  }

  public static DoubleRange of(final DoubleRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new DoubleRange(range.min, range.max);
  }

  public static DoubleRange of(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parse(str);
    return new DoubleRange(arr[0].length() == 0 ? Double.MIN_VALUE : Double.parseDouble(arr[0]),
        arr[1].length() == 0 ? Double.MAX_VALUE : Double.parseDouble(arr[1]));
  }

  public static DoubleRange encompassing(final DoubleRange a, final DoubleRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new DoubleRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static DoubleRange rangeGreaterThanOrEq(final Double min) {
    return new DoubleRange(min, Double.MAX_VALUE);
  }

  public static DoubleRange rangeLessThanOrEq(final Double max) {
    return new DoubleRange(Double.MIN_VALUE, max);
  }

  @Override
  public Double getMin() {
    return this.min;
  }

  @Override
  public Double getMiddle() {
    return min + (max - min) / 2;
  }

  @Override
  public Double getMax() {
    return this.max;
  }

  @Override
  public boolean isLower(final Double num) {
    return num == null ? true : num < min;
  }

  @Override
  public boolean isHigher(final Double num) {
    return num == null ? false : num > max;
  }

  @Override
  public boolean isInRange(final Double num) {
    return num == null ? false : num >= min && num <= max;
  }

  @Override
  public boolean isOutsideRange(final Double num) {
    return num == null ? true : num < min || num > max;
  }

  @Override
  public boolean isAfter(final Double num) {
    return min > num;
  }

  @Override
  public boolean isAfter(final DoubleRange range) {
    return range == null ? true : min > range.max;
  }

  @Override
  public boolean isBefore(final Double num) {
    return max < num;
  }

  @Override
  public boolean isBefore(final DoubleRange range) {
    return max < range.min;
  }

  @Override
  public boolean startsWith(final Double num) {
    return num == null ? false : min.doubleValue() == num.doubleValue();
  }

  @Override
  public boolean startsWith(final DoubleRange range) {
    return range == null ? false : min.doubleValue() == range.min.doubleValue();
  }

  @Override
  public boolean endsWith(final Double num) {
    return num == null ? false : max.doubleValue() == num.doubleValue();
  }

  @Override
  public boolean endsWith(final DoubleRange range) {
    return range == null ? false : max.doubleValue() == range.max.doubleValue();
  }

  @Override
  public boolean isEmpty() {
    return min == max;
  }

  @Override
  public boolean contains(final Double num) {
    return num == null ? false : isInRange(num);
  }

  @Override
  public boolean contains(final DoubleRange range) {
    return range == null ? false : isInRange(range.min) && isInRange(range.max);
  }

  @Override
  public boolean overlaps(final DoubleRange range) {
    return range == null ? false : !(max < range.min || min > range.max);
  }

  @Override
  public DoubleRange intersection(final DoubleRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new DoubleRange(Math.max(min, range.min), Math.min(max, range.max));
  }

  @Override
  public Double clamp(final Double num) {
    return num == null ? min : num < min ? min : num > max ? max : num;
  }

  @Override
  public DoubleRange clamp(final DoubleRange range) {
    if (range == null) {
      return new DoubleRange(min, max);
    }
    return new DoubleRange(clamp(range.min), clamp(range.max));
  }

  @Override
  public Double getLength() {
    return max - min;
  }

  @Override
  public boolean isMaxValue() {
    return max == Double.MAX_VALUE;
  }

  @Override
  public boolean isMinValue() {
    return max == Double.MIN_VALUE;
  }

  @Override
  public int compareTo(final DoubleRange o) {
    if (o == null) {
      return 1;
    }
    return Double.compare(this.max - this.min, o.max - o.min);
  }

  @Override
  public Comparator<DoubleRange> getComparator() {
    return DoubleRange.comparator;
  }

  @Override
  public DoubleRange clone() {
    return new DoubleRange(min, max);
  }

  @Override
  public String toString() {
    if (this.min == Double.MIN_VALUE && this.max == Double.MAX_VALUE) {
      return "";
    } else if (this.max == Double.MAX_VALUE) {
      return this.min + "..";
    } else if (this.min == Double.MIN_VALUE) {
      return ".." + this.max;
    } else {
      return this.min + ".." + this.max;
    }
  }

  @Override
  public int hashCode() {
    return 31 * (31 * Double.hashCode(min)) + Double.hashCode(max);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final DoubleRange other = (DoubleRange) obj;
    return min.equals(other.min) && max.equals(other.max);
  }
}
