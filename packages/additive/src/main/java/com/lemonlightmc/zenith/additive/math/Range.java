package com.lemonlightmc.zenith.additive.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;
import com.lemonlightmc.zenith.additive.math.ranges.BigDecimalRange;
import com.lemonlightmc.zenith.additive.math.ranges.BigIntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.ByteRange;
import com.lemonlightmc.zenith.additive.math.ranges.DoubleRange;
import com.lemonlightmc.zenith.additive.math.ranges.FloatRange;
import com.lemonlightmc.zenith.additive.math.ranges.IntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.LongRange;
import com.lemonlightmc.zenith.additive.math.ranges.ShortRange;

/**
 * Represents a comparable range of values with configurable inclusivity at each bound.
 *
 * @param <R> the concrete range type
 * @param <V> the comparable value type contained in the range
 */
public abstract class Range<R extends Range<R, V>, V extends Comparable<V>> implements Cloneable<R>, Comparable<R> {

  /**
   * Creates a range containing only the specified {@link BigDecimal} value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static BigDecimalRange at(final BigDecimal pos) {
    return new BigDecimalRange(pos, pos);
  }

  /**
   * Creates a range containing only the specified {@link BigInteger} value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static BigIntegerRange at(final BigInteger pos) {
    return new BigIntegerRange(pos, pos);
  }

  /**
   * Creates a range containing only the specified integer value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static IntegerRange at(final int pos) {
    return new IntegerRange(pos, pos);
  }

  /**
   * Creates a range containing only the specified long value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static LongRange at(final long pos) {
    return new LongRange(pos, pos);
  }

  /**
   * Creates a range containing only the specified float value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static FloatRange at(final float pos) {
    return new FloatRange(pos, pos);
  }

  /**
   * Creates a range containing only the specified double value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static DoubleRange at(final double pos) {
    return new DoubleRange(pos, pos);
  }

  /**
   * Creates a range containing only the specified short value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static ShortRange at(final short pos) {
    return new ShortRange(pos, pos);
  }

  /**
   * Creates a range containing only the specified byte value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static ByteRange at(final byte pos) {
    return new ByteRange(pos, pos);
  }

  /**
   * Creates a range between the specified {@link BigDecimal} bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static BigDecimalRange between(final BigDecimal min, final BigDecimal max) {
    return new BigDecimalRange(min, max);
  }

  /**
   * Creates a range between the specified {@link BigInteger} bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static BigIntegerRange between(final BigInteger min, final BigInteger max) {
    return new BigIntegerRange(min, max);
  }

  /**
   * Creates a range between the specified integer bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static IntegerRange between(final Integer min, final Integer max) {
    return new IntegerRange(min, max);
  }

  /**
   * Creates a range between the specified long bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static LongRange between(final Long min, final Long max) {
    return new LongRange(min, max);
  }

  /**
   * Creates a range between the specified float bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static FloatRange between(final Float min, final Float max) {
    return new FloatRange(min, max);
  }

  /**
   * Creates a range between the specified double bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static DoubleRange between(final Double min, final Double max) {
    return new DoubleRange(min, max);
  }

  /**
   * Creates a range between the specified short bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static ShortRange between(final Short min, final Short max) {
    return new ShortRange(min, max);
  }

  /**
   * Creates a range between the specified byte bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static ByteRange between(final Byte min, final Byte max) {
    return new ByteRange(min, max);
  }

  /**
   * Parses a string into a {@link BigIntegerRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range
   */
  public static BigIntegerRange asBigInteger(final String str) {
    return BigIntegerRange.from(str);
  }

  /**
   * Parses a string into an {@link IntegerRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range
   */
  public static IntegerRange asInteger(final String str) {
    return IntegerRange.from(str);
  }

  /**
   * Parses a string into a {@link LongRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range
   */
  public static LongRange asLong(final String str) {
    return LongRange.from(str);
  }

  /**
   * Parses a string into a {@link FloatRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range
   */
  public static FloatRange asFloat(final String str) {
    return FloatRange.from(str);
  }

  /**
   * Parses a string into a {@link DoubleRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range
   */
  public static DoubleRange asDouble(final String str) {
    return DoubleRange.from(str);
  }

  /**
   * Parses a string into a {@link ShortRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range
   */
  public static ShortRange asShort(final String str) {
    return ShortRange.from(str);
  }

  /**
   * Parses a string into a {@link ByteRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range
   */
  public static ByteRange asByte(final String str) {
    return ByteRange.from(str);
  }

  /**
   * Parses a string into an appropriate numeric range implementation.
   *
   * @param str the string representation to parse
   * @param <R> the concrete range type
   * @param <T> the comparable value type
   * @return the parsed range
   */
  public static <R extends Range<R, T>, T extends Comparable<T>> Range<R, T> fromString(final String str) {
    return fromString(parseStr(str));
  }

  /**
   * Creates an appropriate numeric range implementation from parsed range components.
   *
   * @param parsed the parsed range components
   * @param <R>    the concrete range type
   * @param <T>    the comparable value type
   * @return the parsed range
   */
  @SuppressWarnings("unchecked")
  public static <R extends Range<R, T>, T extends Comparable<T>> Range<R, T> fromString(
      final RangeStringParsed parsed) {
    if (NumberConversions.isDecimal(parsed.minStr()) || NumberConversions.isDecimal(parsed.maxStr())) {
      final Double min = (parsed.minStr() == null) ? null : NumberConversions.parseDouble(parsed.minStr());
      final Double max = (parsed.maxStr() == null) ? null : NumberConversions.parseDouble(parsed.maxStr());
      return (Range<R, T>) new DoubleRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
    }

    if ((parsed.minStr() == null || NumberConversions.fitsInt(parsed.minStr()))
        && (parsed.maxStr() == null || NumberConversions.fitsInt(parsed.maxStr()))) {
      final Integer min = (parsed.minStr() == null) ? null : NumberConversions.parseInt(parsed.minStr());
      final Integer max = (parsed.maxStr() == null) ? null : NumberConversions.parseInt(parsed.maxStr());
      return (Range<R, T>) new IntegerRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
    }

    if ((parsed.minStr() == null || NumberConversions.fitsLong(parsed.minStr()))
        && (parsed.maxStr() == null || NumberConversions.fitsLong(parsed.maxStr()))) {
      final Long min = (parsed.minStr() == null) ? null : NumberConversions.parseLong(parsed.minStr());
      final Long max = (parsed.maxStr() == null) ? null : NumberConversions.parseLong(parsed.maxStr());
      return (Range<R, T>) new LongRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
    }

    final BigInteger min = (parsed.minStr() == null) ? null : NumberConversions.parseBigInteger(parsed.minStr());
    final BigInteger max = (parsed.maxStr() == null) ? null : NumberConversions.parseBigInteger(parsed.maxStr());
    return (Range<R, T>) new BigIntegerRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both {@link BigDecimalRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static BigDecimalRange encompassing(final BigDecimalRange a, final BigDecimalRange b) {
    return BigDecimalRange.encompassing(a, b);
  }

  /**
   * Returns the smallest range encompassing both {@link BigIntegerRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static BigIntegerRange encompassing(final BigIntegerRange a, final BigIntegerRange b) {
    return BigIntegerRange.encompassing(a, b);
  }

  /**
   * Returns the smallest range encompassing both {@link IntegerRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static IntegerRange encompassing(final IntegerRange a, final IntegerRange b) {
    return IntegerRange.encompassing(a, b);
  }

  /**
   * Returns the smallest range encompassing both {@link LongRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static LongRange encompassing(final LongRange a, final LongRange b) {
    return LongRange.encompassing(a, b);
  }

  /**
   * Returns the smallest range encompassing both {@link FloatRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static FloatRange encompassing(final FloatRange a, final FloatRange b) {
    return FloatRange.encompassing(a, b);
  }

  /**
   * Returns the smallest range encompassing both {@link DoubleRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static DoubleRange encompassing(final DoubleRange a, final DoubleRange b) {
    return DoubleRange.encompassing(a, b);
  }

  /**
   * Returns the smallest range encompassing both {@link ShortRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static ShortRange encompassing(final ShortRange a, final ShortRange b) {
    return ShortRange.encompassing(a, b);
  }

  /**
   * Returns the smallest range encompassing both {@link ByteRange} instances.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   */
  public static ByteRange encompassing(final ByteRange a, final ByteRange b) {
    return ByteRange.encompassing(a, b);
  }

  /**
   * Parses a range string into its individual components.
   *
   * @param str the string representation of a range
   * @return the parsed range components
   */
  protected static RangeStringParsed parseStr(String str) {
    if (str == null || str.isBlank()) {
      return new RangeStringParsed(null, null, true, true);
    }
    str = str.trim();

    boolean minInclusive = true;
    boolean maxInclusive = true;

    if (str.startsWith("[")) {
      minInclusive = true;
      str = str.substring(1);
    } else if (str.startsWith("(")) {
      minInclusive = false;
      str = str.substring(1);
    }

    if (str.endsWith("]")) {
      maxInclusive = true;
      str = str.substring(0, str.length() - 1);
    } else if (str.endsWith(")")) {
      maxInclusive = false;
      str = str.substring(0, str.length() - 1);
    }

    str = str.trim();
    if (str.isEmpty()) {
      return new RangeStringParsed(null, null, minInclusive, maxInclusive);
    }

    String minStr = null;
    String maxStr = null;

    final int rangeIdx = str.indexOf("..");
    if (rangeIdx >= 0) {
      minStr = str.substring(0, rangeIdx);
      maxStr = str.substring(rangeIdx + 2);
    } else {
      final String[] parts = str.split("\\s*,\\s*", 2);
      if (parts.length == 2) {
        minStr = parts[0];
        maxStr = parts[1];
      } else {
        minStr = str;
        maxStr = str;
      }
    }

    return RangeStringParsed.from(minStr, maxStr, minInclusive, maxInclusive);
  }

  /**
   * Represents the parsed components of a range string.
   *
   * @param minStr       the lower bound string, or {@code null} if unbounded
   * @param maxStr       the upper bound string, or {@code null} if unbounded
   * @param minInclusive whether the lower bound is inclusive
   * @param maxInclusive whether the upper bound is inclusive
   */
  public static final record RangeStringParsed(String minStr, String maxStr, boolean minInclusive,
      boolean maxInclusive) {

    /**
     * Creates a normalized parsed range representation.
     *
     * @param minStr       the lower bound string
     * @param maxStr       the upper bound string
     * @param minInclusive whether the lower bound is inclusive
     * @param maxInclusive whether the upper bound is inclusive
     * @return a normalized parsed range representation
     */
    public static RangeStringParsed from(String minStr, String maxStr, final boolean minInclusive,
        final boolean maxInclusive) {
      if (minStr != null) {
        minStr = minStr.trim();
        minStr = minStr.isEmpty() ? null : minStr;
      }
      if (maxStr != null) {
        maxStr = maxStr.trim();
        maxStr = maxStr.isEmpty() ? null : maxStr;
      }
      return new RangeStringParsed(minStr, maxStr, minInclusive, maxInclusive);
    }

    /**
     * Returns the string representation of this parsed range.
     *
     * @return the string representation of this parsed range
     */
    public String toString() {
      return (minInclusive ? "[" : "(") + (minStr == null ? "" : minStr) + ".." + (maxStr == null ? "" : maxStr)
          + (maxInclusive ? "]" : ")");
    }
  }

  private V min;
  private boolean minInclusive;
  private V max;
  private boolean maxInclusive;
  private static Comparator<Range<?, ?>> comparator = null;

  /**
   * Creates a range with inclusive bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @throws IllegalArgumentException if either bound is {@code null} or {@code min} is greater than {@code max}
   */
  public Range(final V min, final V max) {
    this(min, true, max, true);
  }

  /**
   * Creates a range with configurable bound inclusivity.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   * @throws IllegalArgumentException if either bound is {@code null} or {@code min} is greater than {@code max}
   */
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

  /**
   * Returns the lower bound of this range.
   *
   * @return the lower bound
   */
  public V getMin() {
    return min;
  }

  /**
   * Returns the upper bound of this range.
   *
   * @return the upper bound
   */
  public V getMax() {
    return max;
  }

  /**
   * Returns whether the lower bound is inclusive.
   *
   * @return {@code true} if the lower bound is inclusive; otherwise {@code false}
   */
  public boolean isMinInclusive() {
    return minInclusive;
  }

  /**
   * Returns whether the upper bound is inclusive.
   *
   * @return {@code true} if the upper bound is inclusive; otherwise {@code false}
   */
  public boolean isMaxInclusive() {
    return maxInclusive;
  }

  /**
   * Checks whether a value lies below this range.
   *
   * @param num the value to check
   * @return {@code true} if the value is below this range; otherwise {@code false}
   */
  public boolean isLower(final V num) {
    if (num == null) {
      return true;
    }
    // if min is inclusive num < min, otherwise num <= min
    return minInclusive ? num.compareTo(min) < 0 : num.compareTo(min) <= 0;
  }

  /**
   * Checks whether another range lies entirely below this range.
   *
   * @param range the range to check
   * @return {@code true} if the other range is below this range; otherwise {@code false}
   */
  public boolean isLower(final R range) {
    if (range == null) {
      return false;
    }
    return isLower(range.getMax());
  }

  /**
   * Checks whether a value lies above this range.
   *
   * @param num the value to check
   * @return {@code true} if the value is above this range; otherwise {@code false}
   */
  public boolean isHigher(final V num) {
    if (num == null) {
      return false;
    }
    // if max is inclusive num > max, otherwise num >= max
    return maxInclusive ? num.compareTo(max) > 0 : num.compareTo(max) >= 0;
  }

  /**
   * Checks whether another range lies entirely above this range.
   *
   * @param range the range to check
   * @return {@code true} if the other range is above this range; otherwise {@code false}
   */
  public boolean isHigher(final R range) {
    if (range == null) {
      return false;
    }
    return isHigher(range.getMin());
  }

  /**
   * Checks whether a value falls within this range.
   *
   * @param num the value to check
   * @return {@code true} if the value is within this range; otherwise {@code false}
   */
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

  /**
   * Checks whether a value falls outside this range.
   *
   * @param num the value to check
   * @return {@code true} if the value is outside this range; otherwise {@code false}
   */
  public boolean isOutsideRange(final V num) {
    return num == null ? true : !isInRange(num);
  }

  /**
   * Checks whether this range starts after the specified value.
   *
   * @param num the value to compare against
   * @return {@code true} if this range starts after the value; otherwise {@code false}
   */
  public boolean isAfter(final V num) {
    if (num == null) {
      return true;
    }
    // if min is inclusive num < min, otherwise num <= min
    return minInclusive ? min.compareTo(num) > 0 : min.compareTo(num) >= 0;
  }

  /**
   * Checks whether this range starts after the specified range.
   *
   * @param range the range to compare against
   * @return {@code true} if this range starts after the other range; otherwise {@code false}
   */
  public boolean isAfter(final R range) {
    if (range == null) {
      return true;
    }
    // if min is inclusive num < min, otherwise num <= min
    return minInclusive ? min.compareTo(range.getMax()) > 0 : min.compareTo(range.getMax()) >= 0;
  }

  /**
   * Checks whether this range ends before the specified value.
   *
   * @param num the value to compare against
   * @return {@code true} if this range ends before the value; otherwise {@code false}
   */
  public boolean isBefore(final V num) {
    if (num == null) {
      return false;
    }
    // if max is inclusive num > max, otherwise num >= max
    return maxInclusive ? max.compareTo(num) < 0 : max.compareTo(num) <= 0;
  }

  /**
   * Checks whether this range ends before the specified range.
   *
   * @param range the range to compare against
   * @return {@code true} if this range ends before the other range; otherwise {@code false}
   */
  public boolean isBefore(final R range) {
    if (range == null) {
      return false;
    }
    // if max is inclusive num < max, otherwise num <= max
    return maxInclusive ? max.compareTo(range.getMin()) < 0 : max.compareTo(range.getMin()) <= 0;
  }

  /**
   * Checks whether this range starts at the specified value.
   *
   * @param num the value to compare against
   * @return {@code true} if the lower bound equals the value; otherwise {@code false}
   */
  public boolean startsWith(final V num) {
    return num == null ? false : min.equals(num);
  }

  /**
   * Checks whether this range starts at the same value as another range.
   *
   * @param range the range to compare against
   * @return {@code true} if the lower bounds are equal; otherwise {@code false}
   */
  public boolean startsWith(final R range) {
    return range == null ? false : min == range.getMin();
  }

  /**
   * Checks whether this range ends at the specified value.
   *
   * @param num the value to compare against
   * @return {@code true} if the upper bound equals the value; otherwise {@code false}
   */
  public boolean endsWith(final V num) {
    return num == null ? false : max.equals(num);
  }

  /**
   * Checks whether this range ends at the same value as another range.
   *
   * @param range the range to compare against
   * @return {@code true} if the upper bounds are equal; otherwise {@code false}
   */
  public boolean endsWith(final R range) {
    return range == null ? false : max == range.getMax();
  }

  /**
   * Checks whether this range contains exactly one value.
   *
   * @return {@code true} if the lower and upper bounds are equal; otherwise {@code false}
   */
  public boolean isEmpty() {
    return min.equals(max);
  }

  /**
   * Checks whether this range contains the specified value.
   *
   * @param num the value to check
   * @return {@code true} if the value is contained in this range; otherwise {@code false}
   */
  public boolean contains(final V num) {
    return num == null ? false : isInRange(num);
  }

  /**
   * Checks whether this range fully contains another range.
   *
   * @param range the range to check
   * @return {@code true} if the other range is fully contained in this range; otherwise {@code false}
   */
  public boolean contains(final R range) {
    return range == null ? false : isInRange(range.getMin()) && isInRange(range.getMax());
  }

  /**
   * Checks whether this range overlaps with another range.
   *
   * @param range the range to check
   * @return {@code true} if the ranges overlap; otherwise {@code false}
   */
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

  /**
   * Returns the intersection of this range and another range.
   *
   * @param range the range to intersect with
   * @return the intersecting range, or {@code null} if the ranges do not overlap
   */
  public abstract R intersection(R range);

  /**
   * Clamps a value to the bounds of this range.
   *
   * @param num the value to clamp
   * @return the clamped value
   */
  public V clamp(final V num) {
    return num == null ? min : num.compareTo(min) == -1 ? min : num.compareTo(max) == 1 ? max : num;
  }

  /**
   * Clamps another range to the bounds of this range.
   *
   * @param range the range to clamp
   * @return the clamped range
   */
  public abstract R clamp(R range);

  /**
   * Returns the length of this range.
   *
   * @return the range length
   */
  protected abstract V getLength();

  /**
   * Returns whether this range uses the minimum representable value as its lower bound.
   *
   * @return {@code true} if the lower bound is the minimum representable value; otherwise {@code false}
   */
  public abstract boolean hasMinValue();

  /**
   * Returns whether this range uses the maximum representable value as its upper bound.
   *
   * @return {@code true} if the upper bound is the maximum representable value; otherwise {@code false}
   */
  public abstract boolean hasMaxValue();

  /**
   * Creates a copy of this range.
   *
   * @return a copy of this range
   */
  @Override
  public abstract R clone();

  /**
   * Compares this range with another range.
   *
   * @param o the range to compare to
   * @return a negative integer, zero, or a positive integer as this range is less than, equal to, or greater than the
   *         specified range
   */
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

  /**
   * Returns a comparator for comparing ranges.
   *
   * @return a comparator for ranges
   */
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

  /**
   * Returns a string representation of this range.
   *
   * @return the string representation of this range
   */
  @Override
  public String toString() {
    final String minPart = hasMinValue() ? "" : String.valueOf(min);
    final String maxPart = hasMaxValue() ? "" : String.valueOf(max);
    if (minPart.isEmpty() && maxPart.isEmpty()) {
      return "";
    }
    return (minInclusive ? "[" : "(") + minPart + ".." + maxPart + (maxInclusive ? "]" : ")");
  }

  /**
   * Returns a hash code for this range.
   *
   * @return the hash code for this range
   */
  @Override
  public int hashCode() {
    int result = 31 + min.hashCode();
    result = 31 * result + (minInclusive ? 1231 : 1237);
    result = 31 * result + max.hashCode();
    return 31 * result + (maxInclusive ? 1231 : 1237);
  }

  /**
   * Checks whether this range is equal to another object.
   *
   * @param obj the object to compare with
   * @return {@code true} if the objects represent the same range; otherwise {@code false}
   */
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