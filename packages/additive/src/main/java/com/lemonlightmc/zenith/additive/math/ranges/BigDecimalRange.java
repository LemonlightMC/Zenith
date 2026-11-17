package com.lemonlightmc.zenith.additive.math.ranges;

import java.math.BigDecimal;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.Range.RangeStringParsed;

/**
 * A {@link Range} implementation for {@link BigDecimal} values.
 */
public class BigDecimalRange extends Range<BigDecimalRange, BigDecimal> {
  /** A range spanning all representable values for this range type. */
  public static BigDecimalRange ALL = new BigDecimalRange();
  /** The minimum bound used by this range type. */
  public static final BigDecimal MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
  /** The maximum bound used by this range type. */
  public static final BigDecimal MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);

  /**
   * Creates a range spanning all representable values.
   */
  public BigDecimalRange() {
    super(MIN_VALUE, MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public BigDecimalRange(final BigDecimal min) {
    super(min == null ? MIN_VALUE : min, MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public BigDecimalRange(final BigDecimal min, final BigDecimal max) {
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
  public BigDecimalRange(final BigDecimal min, final boolean minInclusive, final BigDecimal max,
      final boolean maxInclusive) {
    super(min == null ? MIN_VALUE : min, minInclusive, max == null ? MAX_VALUE : max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static BigDecimalRange at(final BigDecimal pos) {
    return new BigDecimalRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static BigDecimalRange from(final BigDecimal min, final BigDecimal max) {
    return new BigDecimalRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static BigDecimalRange from(final BigDecimalRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigDecimalRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link BigDecimalRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static BigDecimalRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final BigDecimal min = parsed.minStr() == null ? MIN_VALUE : NumberConversions.parseBigDecimal(parsed.minStr());
    final BigDecimal max = parsed.maxStr() == null ? MAX_VALUE : NumberConversions.parseBigDecimal(parsed.maxStr());
    return new BigDecimalRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static BigDecimalRange encompassing(final BigDecimalRange a, final BigDecimalRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigDecimalRange(a.getMin().compareTo(b.getMin()) < 0 ? a.getMin() : b.getMin(),
        a.getMax().compareTo(b.getMax()) > 0 ? a.getMax() : b.getMax());
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static BigDecimalRange rangeGreaterThanOrEq(final BigDecimal min) {
    return new BigDecimalRange(min, MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static BigDecimalRange rangeLessThanOrEq(final BigDecimal max) {
    return new BigDecimalRange(MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimalRange intersection(final BigDecimalRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    final BigDecimal newMin = getMin().compareTo(range.getMin()) > 0 ? getMin() : range.getMin();
    final BigDecimal newMax = getMax().compareTo(range.getMax()) < 0 ? getMax() : range.getMax();
    return new BigDecimalRange(newMin, newMax);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimalRange clamp(final BigDecimalRange range) {
    if (range == null) {
      return this;
    }
    return new BigDecimalRange(clamp(range.getMin()), clamp(range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BigDecimal getLength() {
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
  public BigDecimalRange clone() {
    return new BigDecimalRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }

}