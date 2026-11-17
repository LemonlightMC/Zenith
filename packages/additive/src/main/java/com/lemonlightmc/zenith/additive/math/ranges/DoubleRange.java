package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.Range.RangeStringParsed;

/**
 * A {@link Range} implementation for {@link Double} values.
 */
public class DoubleRange extends Range<DoubleRange, Double> {
  /** A range spanning all representable {@code double} values. */
  public static DoubleRange ALL = new DoubleRange();
  /** The minimum positive representable {@code double} value. */
  public static final double MIN_VALUE = Double.MIN_VALUE;
  /** The maximum representable {@code double} value. */
  public static final double MAX_VALUE = Double.MAX_VALUE;

  /**
   * Creates a range spanning all representable values.
   */
  public DoubleRange() {
    super(Double.MIN_VALUE, Double.MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public DoubleRange(final Double min) {
    super(min == null ? Double.MIN_VALUE : min, Double.MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public DoubleRange(final Double min, final Double max) {
    super(min == null ? Double.MIN_VALUE : min, max == null ? Double.MAX_VALUE : max);
  }

  /**
   * Creates a range with the specified bounds and inclusivity settings.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   */
  public DoubleRange(final Double min, final boolean minInclusive, final Double max, final boolean maxInclusive) {
    super(min == null ? Double.MIN_VALUE : min, minInclusive, max == null ? Double.MAX_VALUE : max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static DoubleRange at(final Double pos) {
    return new DoubleRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static DoubleRange from(final Double min, final Double max) {
    return new DoubleRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static DoubleRange from(final DoubleRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new DoubleRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link DoubleRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static DoubleRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final Double min = parsed.minStr() == null ? Double.MIN_VALUE : NumberConversions.parseDouble(parsed.minStr());
    final Double max = parsed.maxStr() == null ? Double.MAX_VALUE : NumberConversions.parseDouble(parsed.maxStr());
    return new DoubleRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static DoubleRange encompassing(final DoubleRange a, final DoubleRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new DoubleRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static DoubleRange rangeGreaterThanOrEq(final Double min) {
    return new DoubleRange(min, Double.MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static DoubleRange rangeLessThanOrEq(final Double max) {
    return new DoubleRange(Double.MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DoubleRange intersection(final DoubleRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new DoubleRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DoubleRange clamp(final DoubleRange range) {
    if (range == null) {
      return this;
    }
    return new DoubleRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double getLength() {
    return getMax() - getMin();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMaxValue() {
    return getMax() == Double.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMinValue() {
    return getMin() == Double.MIN_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DoubleRange clone() {
    return new DoubleRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}