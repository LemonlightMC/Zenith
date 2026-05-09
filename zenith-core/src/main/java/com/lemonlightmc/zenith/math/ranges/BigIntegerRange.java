package com.lemonlightmc.zenith.math.ranges;

import java.math.BigInteger;
import java.util.Comparator;

import com.lemonlightmc.zenith.exceptions.RangeException;

public class BigIntegerRange implements Range<BigIntegerRange, BigInteger> {
  public static BigIntegerRange ALL = new BigIntegerRange();

  private final BigInteger min;
  private final BigInteger max;
  public static final BigInteger MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
  public static final BigInteger MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

  private static final Comparator<BigIntegerRange> comparator = new Comparator<BigIntegerRange>() {
    @Override
    public int compare(final BigIntegerRange o1, final BigIntegerRange o2) {
      if (o1 == null) {
        return -1;
      }
      if (o2 == null) {
        return 1;
      }
      return o1.max.subtract(o1.min).compareTo(o2.max.subtract(o2.min));
    }
  };

  public BigIntegerRange() {
    this(MIN_VALUE, MAX_VALUE);
  }

  public BigIntegerRange(final BigInteger min) {
    this(min, MAX_VALUE);
  }

  public BigIntegerRange(final BigInteger min, final BigInteger max) {
    this.min = min == null ? MIN_VALUE : min;
    this.max = max == null ? MAX_VALUE : max;
    if (this.max.subtract(this.min).signum() == -1) {
      throw new RangeException("BigInteger Argument Maximum is smaller Minimum: " + this.min + " - " + this.max);
    }
  }

  public static BigIntegerRange at(final BigInteger pos) {
    return new BigIntegerRange(pos, pos);
  }

  public static BigIntegerRange of(final BigInteger min, final BigInteger max) {
    return new BigIntegerRange(min, max);
  }

  public static BigIntegerRange of(final BigIntegerRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigIntegerRange(range.min, range.max);
  }

  public static BigIntegerRange of(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parse(str);
    return new BigIntegerRange(arr[0].length() == 0 ? MIN_VALUE : new BigInteger(arr[0]),
        arr[1].length() == 0 ? MAX_VALUE : new BigInteger(arr[1]));
  }

  public static BigIntegerRange encompassing(final BigIntegerRange a, final BigIntegerRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigIntegerRange(a.getMin().compareTo(b.getMin()) < 0 ? a.getMin() : b.getMin(),
        a.getMax().compareTo(b.getMax()) > 0 ? a.getMax() : b.getMax());
  }

  public static BigIntegerRange rangeGreaterThanOrEq(final BigInteger min) {
    return new BigIntegerRange(min, MAX_VALUE);
  }

  public static BigIntegerRange rangeLessThanOrEq(final BigInteger max) {
    return new BigIntegerRange(MIN_VALUE, max);
  }

  @Override
  public BigInteger getMin() {
    return this.min;
  }

  @Override
  public BigInteger getMiddle() {
    return min.add(max.subtract(min).divide(BigInteger.valueOf(2)));
  }

  @Override
  public BigInteger getMax() {
    return this.max;
  }

  @Override
  public boolean isLower(final BigInteger num) {
    return num == null ? true : num.compareTo(min) < 0;
  }

  @Override
  public boolean isHigher(final BigInteger num) {
    return num == null ? false : num.compareTo(max) > 0;
  }

  @Override
  public boolean isInRange(final BigInteger num) {
    return num == null ? false : num.compareTo(min) >= 0 && num.compareTo(max) <= 0;
  }

  @Override
  public boolean isOutsideRange(final BigInteger num) {
    return num == null ? true : num.compareTo(min) < 0 || num.compareTo(max) > 0;
  }

  @Override
  public boolean isAfter(final BigInteger num) {
    return min.compareTo(num) > 0;
  }

  @Override
  public boolean isAfter(final BigIntegerRange range) {
    return range == null ? true : min.compareTo(range.max) > 0;
  }

  @Override
  public boolean isBefore(final BigInteger num) {
    return max.compareTo(num) < 0;
  }

  @Override
  public boolean isBefore(final BigIntegerRange range) {
    return max.compareTo(range.min) < 0;
  }

  @Override
  public boolean startsWith(final BigInteger num) {
    return num == null ? false : min.equals(num);
  }

  @Override
  public boolean startsWith(final BigIntegerRange range) {
    return range == null ? false : min.equals(range.min);
  }

  @Override
  public boolean endsWith(final BigInteger num) {
    return num == null ? false : max.equals(num);
  }

  @Override
  public boolean endsWith(final BigIntegerRange range) {
    return range == null ? false : max.equals(range.min);
  }

  @Override
  public boolean isEmpty() {
    return min.equals(max);
  }

  @Override
  public boolean contains(final BigInteger num) {
    return num == null ? false : isInRange(num);
  }

  @Override
  public boolean contains(final BigIntegerRange range) {
    return range == null ? false : isInRange(range.min) && isInRange(range.max);
  }

  @Override
  public boolean overlaps(final BigIntegerRange range) {
    return range == null ? false : !(max.compareTo(range.min) < 0 || min.compareTo(range.max) > 0);
  }

  @Override
  public BigIntegerRange intersection(final BigIntegerRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new BigIntegerRange(min.max(range.min), max.min(range.max));
  }

  @Override
  public BigInteger clamp(final BigInteger num) {
    return num == null ? min : num.compareTo(min) < 0 ? min : num.compareTo(max) > 0 ? max : num;
  }

  @Override
  public BigIntegerRange clamp(final BigIntegerRange range) {
    if (range == null) {
      return new BigIntegerRange(min, max);
    }
    return new BigIntegerRange(clamp(range.min), clamp(range.max));
  }

  @Override
  public BigInteger getLength() {
    return max.subtract(min);
  }

  @Override
  public boolean isMaxValue() {
    return max == MAX_VALUE;
  }

  @Override
  public boolean isMinValue() {
    return max == MIN_VALUE;
  }

  @Override
  public int compareTo(final BigIntegerRange o) {
    if (o == null) {
      return 1;
    }
    return this.max.subtract(this.min).compareTo(o.max.subtract(o.min));
  }

  @Override
  public Comparator<BigIntegerRange> getComparator() {
    return BigIntegerRange.comparator;
  }

  @Override
  public BigIntegerRange clone() {
    return new BigIntegerRange(min, max);
  }

  @Override
  public String toString() {
    if (this.min == MIN_VALUE && this.max == MAX_VALUE) {
      return "";
    } else if (this.max == MAX_VALUE) {
      return this.min + "..";
    } else if (this.min == MIN_VALUE) {
      return ".." + this.max;
    } else {
      return this.min + ".." + this.max;
    }
  }

  @Override
  public int hashCode() {
    return 31 * (31 * min.hashCode()) + max.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final BigIntegerRange other = (BigIntegerRange) obj;
    return min.equals(other.min) && max.equals(other.max);
  }
}
