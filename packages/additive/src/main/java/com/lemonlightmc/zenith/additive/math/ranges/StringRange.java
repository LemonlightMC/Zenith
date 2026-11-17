package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.Range.RangeStringParsed;

/**
 * A {@link Range} implementation representing string length bounds.
 */
public class StringRange extends Range<StringRange, Integer> {
  /** A range spanning all representable values for this range type. */
  public static final StringRange ALL = new StringRange();
  /** The minimum bound used by this range type. */
  public static final int MIN_VALUE = Integer.MIN_VALUE;
  /** The maximum bound used by this range type. */
  public static final int MAX_VALUE = Integer.MAX_VALUE;

  /**
   * Creates a range spanning all representable values.
   */
  public StringRange() {
    super(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public StringRange(final Integer min) {
    super(min == null ? Integer.MIN_VALUE : min, Integer.MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public StringRange(final Integer min, final Integer max) {
    super(min == null ? Integer.MIN_VALUE : min, max == null ? Integer.MAX_VALUE : max);
  }

  /**
   * Creates a range with the specified bounds and inclusivity settings.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   */
  public StringRange(final Integer min, final boolean minInclusive, final Integer max, final boolean maxInclusive) {
    super(min == null ? Integer.MIN_VALUE : min, minInclusive, max == null ? Integer.MAX_VALUE : max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static StringRange at(final Integer pos) {
    return new StringRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static StringRange from(final Integer min, final Integer max) {
    return new StringRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static StringRange from(final StringRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new StringRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link StringRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static StringRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final Integer min = parsed.minStr() == null ? Integer.MIN_VALUE : NumberConversions.parseInt(parsed.minStr());
    final Integer max = parsed.maxStr() == null ? Integer.MAX_VALUE : NumberConversions.parseInt(parsed.maxStr());
    return new StringRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static StringRange encompassing(final StringRange a, final StringRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new StringRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static StringRange rangeGreaterThanOrEq(final Integer min) {
    return new StringRange(min, Integer.MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static StringRange rangeLessThanOrEq(final Integer max) {
    return new StringRange(Integer.MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StringRange intersection(final StringRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new StringRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StringRange clamp(final StringRange range) {
    if (range == null) {
      return this;
    }
    return new StringRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Integer getLength() {
    return getMax() - getMin();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMaxValue() {
    return getMax() == Integer.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMinValue() {
    return getMin() == Integer.MIN_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StringRange clone() {
    return new StringRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}