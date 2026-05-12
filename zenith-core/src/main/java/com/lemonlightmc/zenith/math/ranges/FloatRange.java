package com.lemonlightmc.zenith.math.ranges;

import java.util.Comparator;

import com.lemonlightmc.zenith.exceptions.RangeException;

public class FloatRange implements Range<FloatRange, Float> {
  public static final FloatRange ALL = new FloatRange();

  private final Float min;
  private final Float max;

  private static final Comparator<FloatRange> comparator = new Comparator<FloatRange>() {
    @Override
    public int compare(final FloatRange o1, final FloatRange o2) {
      if (o1 == null) {
        return -1;
      }
      if (o2 == null) {
        return 1;
      }
      return Float.compare(o1.max - o1.min, o2.max - o2.min);
    }
  };

  public FloatRange() {
    this(Float.MIN_VALUE, Float.MAX_VALUE);
  }

  public FloatRange(final Float min) {
    this(min, Float.MAX_VALUE);
  }

  public FloatRange(final Float min, final Float max) {
    this.min = min == null ? Float.MIN_VALUE : min;
    this.max = max == null ? Float.MAX_VALUE : max;
    if (Math.signum(this.max - this.min) == -1.0d) {
      throw new RangeException("Float Argument Maximum is smaller Minimum: " + this.min + " - " + this.max);
    }
  }

  public static FloatRange at(final Float pos) {
    return new FloatRange(pos, pos);
  }

  public static FloatRange of(final Float min, final Float max) {
    return new FloatRange(min, max);
  }

  public static FloatRange of(final FloatRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new FloatRange(range.min, range.max);
  }

  public static FloatRange of(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parse(str);
    return new FloatRange(arr[0].length() == 0 ? Float.MIN_VALUE : Float.parseFloat(arr[0]),
        arr[1].length() == 0 ? Float.MAX_VALUE : Float.parseFloat(arr[1]));
  }

  public static FloatRange encompassing(final FloatRange a, final FloatRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new FloatRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static FloatRange rangeGreaterThanOrEq(final Float min) {
    return new FloatRange(min, Float.MAX_VALUE);
  }

  public static FloatRange rangeLessThanOrEq(final Float max) {
    return new FloatRange(Float.MIN_VALUE, max);
  }

  @Override
  public Float getMin() {
    return this.min;
  }

  @Override
  public Float getMiddle() {
    return min + (max - min) / 2;
  }

  @Override
  public Float getMax() {
    return this.max;
  }

  @Override
  public boolean isLower(final Float num) {
    return num == null ? true : num < min;
  }

  @Override
  public boolean isHigher(final Float num) {
    return num == null ? false : num > max;
  }

  @Override
  public boolean isInRange(final Float num) {
    return num == null ? false : num >= min && num <= max;
  }

  @Override
  public boolean isOutsideRange(final Float num) {
    return num == null ? true : num < min || num > max;
  }

  @Override
  public boolean isAfter(final Float num) {
    return min > num;
  }

  @Override
  public boolean isAfter(final FloatRange range) {
    return range == null ? true : min > range.max;
  }

  @Override
  public boolean isBefore(final Float num) {
    return max < num;
  }

  @Override
  public boolean isBefore(final FloatRange range) {
    return max < range.min;
  }

  @Override
  public boolean startsWith(final Float num) {
    return num == null ? false : min.floatValue() == num.floatValue();
  }

  @Override
  public boolean startsWith(final FloatRange range) {
    return range == null ? false : min.floatValue() == range.min.floatValue();
  }

  @Override
  public boolean endsWith(final Float num) {
    return num == null ? false : max.floatValue() == num.floatValue();
  }

  @Override
  public boolean endsWith(final FloatRange range) {
    return range == null ? false : max.floatValue() == range.max.floatValue();
  }

  @Override
  public boolean isEmpty() {
    return min == max;
  }

  @Override
  public boolean contains(final Float num) {
    return num == null ? false : isInRange(num);
  }

  @Override
  public boolean contains(final FloatRange range) {
    return range == null ? false : isInRange(range.min) && isInRange(range.max);
  }

  @Override
  public boolean overlaps(final FloatRange range) {
    return range == null ? false : !(max < range.min || min > range.max);
  }

  @Override
  public FloatRange intersection(final FloatRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new FloatRange(Math.max(min, range.min), Math.min(max, range.max));
  }

  @Override
  public Float clamp(final Float num) {
    return num == null ? min : num < min ? min : num > max ? max : num;
  }

  @Override
  public FloatRange clamp(final FloatRange range) {
    if (range == null) {
      return new FloatRange(min, max);
    }
    return new FloatRange(clamp(range.min), clamp(range.max));
  }

  @Override
  public Float getLength() {
    return max - min;
  }

  @Override
  public boolean isMaxValue() {
    return max == Float.MAX_VALUE;
  }

  @Override
  public boolean isMinValue() {
    return max == Float.MIN_VALUE;
  }

  @Override
  public int compareTo(final FloatRange o) {
    if (o == null) {
      return 1;
    }
    return Float.compare(this.max - this.min, o.max - o.min);
  }

  @Override
  public Comparator<FloatRange> getComparator() {
    return FloatRange.comparator;
  }

  @Override
  public FloatRange clone() {
    return new FloatRange(min, max);
  }

  @Override
  public String toString() {
    if (this.min == Float.MIN_VALUE && this.max == Float.MAX_VALUE) {
      return "";
    } else if (this.max == Float.MAX_VALUE) {
      return this.min + "..";
    } else if (this.min == Float.MIN_VALUE) {
      return ".." + this.max;
    } else {
      return this.min + ".." + this.max;
    }
  }

  @Override
  public int hashCode() {
    return 31 * (31 * Float.hashCode(min)) + Float.hashCode(max);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final FloatRange other = (FloatRange) obj;
    return min.equals(other.min) && max.equals(other.max);
  }
}
