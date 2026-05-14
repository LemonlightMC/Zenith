package com.lemonlightmc.zenith.math.ranges;

import java.util.Comparator;

import com.lemonlightmc.zenith.exceptions.RangeException;
import com.lemonlightmc.zenith.math.NumberConversions;

public class ByteRange implements Range<ByteRange, Byte> {
  public static ByteRange ALL = new ByteRange();
  public static final byte MIN_VALUE = Byte.MIN_VALUE;
  public static final byte MAX_VALUE = Byte.MAX_VALUE;

  private final Byte min;
  private final Byte max;

  private static Comparator<ByteRange> comparator = null;

  public ByteRange() {
    this(Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  public ByteRange(final Byte min) {
    this(min, Byte.MAX_VALUE);
  }

  public ByteRange(final Byte min, final Byte max) {
    this.min = min == null ? Byte.MIN_VALUE : min;
    this.max = max == null ? Byte.MAX_VALUE : max;
    if (this.max < this.min) {
      throw new RangeException("Range Maximum is smaller then Minimum: " + this.min + " - " + this.max);
    }
  }

  public static ByteRange at(final Byte pos) {
    return new ByteRange(pos, pos);
  }

  public static ByteRange from(final Byte min, final Byte max) {
    return new ByteRange(min, max);
  }

  public static ByteRange from(final ByteRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ByteRange(range.min, range.max);
  }

  public static ByteRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new ByteRange(arr[0].length() == 0 ? Byte.MIN_VALUE : NumberConversions.parseByte(arr[0]),
        arr[1].length() == 0 ? Byte.MAX_VALUE : NumberConversions.parseByte(arr[1]));
  }

  public static ByteRange encompassing(final ByteRange a, final ByteRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ByteRange((a.getMin() <= b.getMin()) ? a.getMin() : b.getMin(),
        (a.getMax() >= b.getMax()) ? a.getMax() : b.getMax());
  }

  public static ByteRange rangeGreaterThanOrEq(final Byte min) {
    return new ByteRange(min, Byte.MAX_VALUE);
  }

  public static ByteRange rangeLessThanOrEq(final Byte max) {
    return new ByteRange(Byte.MIN_VALUE, max);
  }

  @Override
  public Byte getMin() {
    return this.min;
  }

  @Override
  public Byte getMiddle() {
    return (byte) (min + (max - min) / 2);
  }

  @Override
  public Byte getMax() {
    return this.max;
  }

  @Override
  public boolean isLower(final Byte num) {
    return num == null ? true : num < min;
  }

  @Override
  public boolean isHigher(final Byte num) {
    return num == null ? false : num > max;
  }

  @Override
  public boolean isInRange(final Byte num) {
    return num == null ? false : num >= min && num <= max;
  }

  @Override
  public boolean isOutsideRange(final Byte num) {
    return num == null ? true : num < min || num > max;
  }

  @Override
  public boolean isAfter(final Byte num) {
    return min > num;
  }

  @Override
  public boolean isAfter(final ByteRange range) {
    return range == null ? true : min > range.max;
  }

  @Override
  public boolean isBefore(final Byte num) {
    return max < num;
  }

  @Override
  public boolean isBefore(final ByteRange range) {
    return max < range.min;
  }

  @Override
  public boolean startsWith(final Byte num) {
    return num == null ? false : min.byteValue() == num.byteValue();
  }

  @Override
  public boolean startsWith(final ByteRange range) {
    return range == null ? false : min.byteValue() == range.min.byteValue();
  }

  @Override
  public boolean endsWith(final Byte num) {
    return num == null ? false : max.byteValue() == num.byteValue();
  }

  @Override
  public boolean endsWith(final ByteRange range) {
    return range == null ? false : max.byteValue() == range.max.byteValue();
  }

  @Override
  public boolean isEmpty() {
    return min.byteValue() == max.byteValue();
  }

  @Override
  public boolean contains(final Byte num) {
    return num == null ? false : isInRange(num);
  }

  @Override
  public boolean contains(final ByteRange range) {
    return range == null ? false : isInRange(range.min) && isInRange(range.max);
  }

  @Override
  public boolean overlaps(final ByteRange range) {
    return range == null ? false : !(max < range.min || min > range.max);
  }

  @Override
  public ByteRange intersection(final ByteRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new ByteRange(min >= range.min ? min : range.min, max <= range.max ? max : range.max);
  }

  @Override
  public Byte clamp(final Byte num) {
    return num == null ? min : num < min ? min : num > max ? max : num;
  }

  @Override
  public ByteRange clamp(final ByteRange range) {
    if (range == null) {
      return new ByteRange(min, max);
    }
    return new ByteRange(clamp(range.min), clamp(range.max));
  }

  @Override
  public Byte getLength() {
    return (byte) (max - min);
  }

  @Override
  public boolean isMaxValue() {
    return max == Byte.MAX_VALUE;
  }

  @Override
  public boolean isMinValue() {
    return max == Byte.MIN_VALUE;
  }

  @Override
  public int compareTo(final ByteRange o) {
    if (o == null) {
      return 1;
    }
    return Integer.compare(this.max.intValue() - this.min.intValue(), o.max.intValue() - o.min.intValue());
  }

  @Override
  public Comparator<ByteRange> getComparator() {
    if (comparator == null) {
      comparator = Range.createComparator();
    }
    return comparator;
  }

  @Override
  public ByteRange clone() {
    return new ByteRange(min, max);
  }

  @Override
  public String toString() {
    if (this.min == Byte.MIN_VALUE && this.max == Byte.MAX_VALUE) {
      return "";
    } else if (this.max == Byte.MAX_VALUE) {
      return this.min + "..";
    } else if (this.min == Byte.MIN_VALUE) {
      return ".." + this.max;
    } else {
      return this.min + ".." + this.max;
    }
  }

  @Override
  public int hashCode() {
    return 31 * (31 * Byte.hashCode(min)) + Byte.hashCode(max);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ByteRange other = (ByteRange) obj;
    return min.equals(other.min) && max.equals(other.max);
  }
}
