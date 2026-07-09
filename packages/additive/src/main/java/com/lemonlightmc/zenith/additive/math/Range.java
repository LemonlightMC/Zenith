package com.lemonlightmc.zenith.additive.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import com.lemonlightmc.zenith.additive.Cloneable;
import com.lemonlightmc.zenith.additive.math.ranges.BigDecimalRange;
import com.lemonlightmc.zenith.additive.math.ranges.BigIntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.ByteRange;
import com.lemonlightmc.zenith.additive.math.ranges.DoubleRange;
import com.lemonlightmc.zenith.additive.math.ranges.FloatRange;
import com.lemonlightmc.zenith.additive.math.ranges.IntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.LongRange;
import com.lemonlightmc.zenith.additive.math.ranges.ShortRange;

public abstract class Range<R extends Range<R, V>, V extends Comparable<V>> implements Cloneable<R>, Comparable<R> {

  public static BigDecimalRange at(final BigDecimal pos) {
    return new BigDecimalRange(pos, pos);
  }

  public static BigIntegerRange at(final BigInteger pos) {
    return new BigIntegerRange(pos, pos);
  }

  public static IntegerRange at(final int pos) {
    return new IntegerRange(pos, pos);
  }

  public static LongRange at(final long pos) {
    return new LongRange(pos, pos);
  }

  public static FloatRange at(final float pos) {
    return new FloatRange(pos, pos);
  }

  public static DoubleRange at(final double pos) {
    return new DoubleRange(pos, pos);
  }

  public static ShortRange at(final short pos) {
    return new ShortRange(pos, pos);
  }

  public static ByteRange at(final byte pos) {
    return new ByteRange(pos, pos);
  }

  public static BigDecimalRange between(final BigDecimal min, final BigDecimal max) {
    return new BigDecimalRange(min, max);
  }

  public static BigIntegerRange between(final BigInteger min, final BigInteger max) {
    return new BigIntegerRange(min, max);
  }

  public static IntegerRange between(final Integer min, final Integer max) {
    return new IntegerRange(min, max);
  }

  public static LongRange between(final Long min, final Long max) {
    return new LongRange(min, max);
  }

  public static FloatRange between(final Float min, final Float max) {
    return new FloatRange(min, max);
  }

  public static DoubleRange between(final Double min, final Double max) {
    return new DoubleRange(min, max);
  }

  public static ShortRange between(final Short min, final Short max) {
    return new ShortRange(min, max);
  }

  public static ByteRange between(final Byte min, final Byte max) {
    return new ByteRange(min, max);
  }

  public static BigIntegerRange asBigInteger(final String str) {
    return BigIntegerRange.from(str);
  }

  public static IntegerRange asInteger(final String str) {
    return IntegerRange.from(str);
  }

  public static LongRange asLong(final String str) {
    return LongRange.from(str);
  }

  public static FloatRange asFloat(final String str) {
    return FloatRange.from(str);
  }

  public static DoubleRange asDouble(final String str) {
    return DoubleRange.from(str);
  }

  public static ShortRange asShort(final String str) {
    return ShortRange.from(str);
  }

  public static ByteRange asByte(final String str) {
    return ByteRange.from(str);
  }

  public static BigDecimalRange encompassing(final BigDecimalRange a, final BigDecimalRange b) {
    return BigDecimalRange.encompassing(a, b);
  }

  public static BigIntegerRange encompassing(final BigIntegerRange a, final BigIntegerRange b) {
    return BigIntegerRange.encompassing(a, b);
  }

  public static IntegerRange encompassing(final IntegerRange a, final IntegerRange b) {
    return IntegerRange.encompassing(a, b);
  }

  public static LongRange encompassing(final LongRange a, final LongRange b) {
    return LongRange.encompassing(a, b);
  }

  public static FloatRange encompassing(final FloatRange a, final FloatRange b) {
    return FloatRange.encompassing(a, b);
  }

  public static DoubleRange encompassing(final DoubleRange a, final DoubleRange b) {
    return DoubleRange.encompassing(a, b);
  }

  public static ShortRange encompassing(final ShortRange a, final ShortRange b) {
    return ShortRange.encompassing(a, b);
  }

  public static ByteRange encompassing(final ByteRange a, final ByteRange b) {
    return ByteRange.encompassing(a, b);
  }

  protected static String[] parseStr(String str) {
    if (str == null || str.length() == 0) {
      return new String[] { "", "" };
    }
    if (str.charAt(0) == '[') {
      str = str.substring(1);
    }
    if (str.charAt(str.length() - 1) == ']') {
      str = str.substring(0, str.length() - 1);
    }
    final String[] parts = str.split("[,\\.\\s]+");
    if (parts == null || parts.length != 2) {
      return new String[] { "", "" };
    }

    return new String[] { parts[0].strip(), parts[1].strip() };
  }

  private V min;
  private boolean minInclusive;
  private V max;
  private boolean maxInclusive;
  private static Comparator<Range<?, ?>> comparator = null;

  public Range(final V min, final V max) {
    this(min, true, max, true);
  }

  public Range(final V min, final boolean minInclusive, final V max, final boolean maxInclusive) {
    if (min == null || max == null) {
      throw new IllegalArgumentException("Min and Max cannot be null");
    }
    if (min.compareTo(max) > 0) {
      throw new IllegalArgumentException("Min cannot be greater than Max");
    }
    this.min = min;
    this.minInclusive = minInclusive;
    this.max = max;
    this.maxInclusive = maxInclusive;
  }

  public V getMin() {
    return min;
  }

  public V getMax() {
    return max;
  }

  public boolean isMinInclusive() {
    return minInclusive;
  }

  public boolean isMaxInclusive() {
    return maxInclusive;
  }

  public boolean isLower(final V num) {
    if (num == null) {
      return true;
    }
    // if min is inclusive num < min, otherwise num <= min
    return minInclusive ? num.compareTo(min) < 0 : num.compareTo(min) <= 0;
  }

  public boolean isLower(final R range) {
    if (range == null) {
      return false;
    }
    return isLower(range.getMax());
  }

  public boolean isHigher(final V num) {
    if (num == null) {
      return false;
    }
    // if max is inclusive num > max, otherwise num >= max
    return maxInclusive ? num.compareTo(max) > 0 : num.compareTo(max) >= 0;
  }

  public boolean isHigher(final R range) {
    if (range == null) {
      return false;
    }
    return isHigher(range.getMin());
  }

  public boolean isInRange(final V num) {
    if (num == null) {
      return false;
    }

    // num >= min if min is inclusive, otherwise num > min
    final boolean minCheck = minInclusive ? num.compareTo(min) >= 0 : num.compareTo(min) > 0;
    // num <= max if max is inclusive, otherwise num < max
    final boolean maxCheck = maxInclusive ? num.compareTo(max) <= 0 : num.compareTo(max) < 0;

    return minCheck && maxCheck;
  }

  public boolean isOutsideRange(final V num) {
    return num == null ? true : !isInRange(num);
  }

  public boolean isAfter(final V num) {
    if (num == null) {
      return true;
    }
    // if min is inclusive num < min, otherwise num <= min
    return minInclusive ? min.compareTo(num) > 0 : min.compareTo(num) >= 0;
  }

  public boolean isAfter(final R range) {
    if (range == null) {
      return true;
    }
    // if min is inclusive num < min, otherwise num <= min
    return minInclusive ? min.compareTo(range.getMax()) > 0 : min.compareTo(range.getMax()) >= 0;
  }

  public boolean isBefore(final V num) {
    if (num == null) {
      return false;
    }
    // if max is inclusive num > max, otherwise num >= max
    return maxInclusive ? max.compareTo(num) < 0 : max.compareTo(num) <= 0;
  }

  public boolean isBefore(final R range) {
    if (range == null) {
      return false;
    }
    // if max is inclusive num < max, otherwise num <= max
    return maxInclusive ? max.compareTo(range.getMin()) < 0 : max.compareTo(range.getMin()) <= 0;
  }

  public boolean startsWith(final V num) {
    return num == null ? false : min.equals(num);
  }

  public boolean startsWith(final R range) {
    return range == null ? false : min == range.getMin();
  }

  public boolean endsWith(final V num) {
    return num == null ? false : max.equals(num);
  }

  public boolean endsWith(final R range) {
    return range == null ? false : max == range.getMax();
  }

  public boolean isEmpty() {
    return min.equals(max);
  }

  public boolean contains(final V num) {
    return num == null ? false : isInRange(num);
  }

  public boolean contains(final R range) {
    return range == null ? false : isInRange(range.getMin()) && isInRange(range.getMax());
  }

  public boolean overlaps(final R range) {
    if (range == null)
      return false;

    // Check if max of this range is before min of other range
    final int maxVsOtherMin = max.compareTo(range.getMin());
    if (maxVsOtherMin < 0)
      return false;
    if (maxVsOtherMin == 0 && (!maxInclusive || !range.isMinInclusive()))
      return false;

    // Check if min of this range is after max of other range
    final int minVsOtherMax = min.compareTo(range.getMax());
    if (minVsOtherMax > 0)
      return false;
    if (minVsOtherMax == 0 && (!minInclusive || !range.isMaxInclusive()))
      return false;

    return true;
  }

  public abstract R intersection(R range);

  public V clamp(final V num) {
    return num == null ? min : num.compareTo(min) == -1 ? min : num.compareTo(max) == 1 ? max : num;
  }

  public abstract R clamp(R range);

  protected abstract V getLength();

  public abstract boolean hasMinValue();

  public abstract boolean hasMaxValue();

  @Override
  public abstract R clone();

  @Override
  public int compareTo(final R o) {
    if (o == null) {
      return 1;
    }

    // Compare minimums first
    // If Minimums are equal, compare min inclusivity
    // inclusive (true) comes before exclusive (false)
    final int minComparison = min.compareTo(o.getMin());
    if (minComparison != 0) {
      return minComparison;
    } else if (minInclusive != o.isMinInclusive()) {
      return minInclusive ? -1 : 1;
    }

    // Minimums and their inclusivity are equal, compare maximums
    // If Maximums are equal, compare max inclusivity
    // exclusive (false) comes before inclusive (true)
    final int maxComparison = max.compareTo(o.getMax());
    if (maxComparison != 0) {
      return maxComparison;
    } else if (maxInclusive != o.isMaxInclusive()) {
      return maxInclusive ? 1 : -1;
    }

    // All are equal
    return 0;
  }

  @SuppressWarnings("unchecked")
  public Comparator<R> getComparator() {
    if (comparator == null) {
      comparator = (Comparator<Range<?, ?>>) new Comparator<R>() {
        @Override
        public int compare(final R range1, final R range2) {
          if (range1 == null) {
            return -1;
          }
          if (range2 == null) {
            return 1;
          }
          return range1.compareTo(range2);
        }
      };
      ;
    }
    return (Comparator<R>) comparator;
  }

  @Override
  public String toString() {
    if (hasMinValue() && hasMaxValue()) {
      return "";
    } else if (hasMaxValue()) {
      return this.min + "..";
    } else if (hasMinValue()) {
      return ".." + this.max;
    } else {
      return this.min + ".." + this.max;
    }
  }

  @Override
  public int hashCode() {
    int result = 31 + min.hashCode();
    result = 31 * result + (minInclusive ? 1231 : 1237);
    result = 31 * result + max.hashCode();
    return 31 * result + (maxInclusive ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Range<?, ?> other = (Range<?, ?>) obj;
    return minInclusive == other.minInclusive && maxInclusive == other.maxInclusive && min.equals(other.min)
        && max.equals(other.max);
  }

}