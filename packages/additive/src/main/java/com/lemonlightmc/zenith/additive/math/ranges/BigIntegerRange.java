package com.lemonlightmc.zenith.additive.math.ranges;

import java.math.BigInteger;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;

/**
 * A {@link Range} implementation for {@link BigInteger} values.
 */
public class BigIntegerRange extends Range<BigIntegerRange, BigInteger> {
  /** A range spanning all representable values for this range type. */
  public static BigIntegerRange ALL = new BigIntegerRange();
  /** The minimum bound used by this range type. */
  public static final BigInteger MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
  /** The maximum bound used by this range type. */
  public static final BigInteger MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

  /**
   * Creates a range spanning all representable values.
   */
  public BigIntegerRange() {
    super(MIN_VALUE, MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public BigIntegerRange(final BigInteger min) {
    super(min == null ? MIN_VALUE : min, MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public BigIntegerRange(final BigInteger min, final BigInteger max) {
    super(min == null ? MIN_VALUE : min, max == null ? MAX_VALUE : max);
  }

  /**
   * Creates a range with the specified bounds and inclusivity settings.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   */
  public BigIntegerRange(final BigInteger min, final boolean minInclusive, final BigInteger max,
      final boolean maxInclusive) {
    super(min == null ? MIN_VALUE : min, minInclusive, max == null ? MAX_VALUE : max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static BigIntegerRange at(final BigInteger pos) {
    return new BigIntegerRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static BigIntegerRange from(final BigInteger min, final BigInteger max) {
    return new BigIntegerRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static BigIntegerRange from(final BigIntegerRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigIntegerRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link BigIntegerRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static BigIntegerRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final BigInteger min = parsed.minStr() == null ? MIN_VALUE : NumberConversions.parseBigInteger(parsed.minStr());
    final BigInteger max = parsed.maxStr() == null ? MAX_VALUE : NumberConversions.parseBigInteger(parsed.maxStr());
    return new BigIntegerRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static BigIntegerRange encompassing(final BigIntegerRange a, final BigIntegerRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigIntegerRange(a.getMin().compareTo(b.getMin()) < 0 ? a.getMin() : b.getMin(),
        a.getMax().compareTo(b.getMax()) > 0 ? a.getMax() : b.getMax());
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static BigIntegerRange rangeGreaterThanOrEq(final BigInteger min) {
    return new BigIntegerRange(min, MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static BigIntegerRange rangeLessThanOrEq(final BigInteger max) {
    return new BigIntegerRange(MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigIntegerRange intersection(final BigIntegerRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new BigIntegerRange(getMin().max(range.getMin()), getMax().min(range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigIntegerRange clamp(final BigIntegerRange range) {
    if (range == null) {
      return this;
    }
    return new BigIntegerRange(clamp(range.getMin()), clamp(range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigInteger getLength() {
    return getMax().subtract(getMin());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMaxValue() {
    return getMax().equals(MAX_VALUE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMinValue() {
    return getMin().equals(MIN_VALUE);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigIntegerRange clone() {
    return new BigIntegerRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
