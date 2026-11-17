package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.Range.RangeStringParsed;

/**
 * A {@link Range} implementation for {@link Long} values.
 */
public class LongRange extends Range<LongRange, Long> {
  /** A range spanning all representable {@code long} values. */
  public static LongRange ALL = new LongRange();
  /** The minimum representable {@code long} value. */
  public static final long MIN_VALUE = Long.MIN_VALUE;
  /** The maximum representable {@code long} value. */
  public static final long MAX_VALUE = Long.MAX_VALUE;

  /**
   * Creates a range spanning all representable values.
   */
  public LongRange() {
    super(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public LongRange(final Long min) {
    super(min, Long.MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public LongRange(final Long min, final Long max) {
    super(min, max);
  }

  /**
   * Creates a range with the specified bounds and inclusivity settings.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   */
  public LongRange(final Long min, final boolean minInclusive, final Long max, final boolean maxInclusive) {
    super(min, minInclusive, max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static LongRange at(final Long pos) {
    return new LongRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static LongRange from(final Long min, final Long max) {
    return new LongRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static LongRange from(final LongRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new LongRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link LongRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static LongRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final Long min = parsed.minStr() == null ? Long.MIN_VALUE : NumberConversions.parseLong(parsed.minStr());
    final Long max = parsed.maxStr() == null ? Long.MAX_VALUE : NumberConversions.parseLong(parsed.maxStr());
    return new LongRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static LongRange encompassing(final LongRange a, final LongRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new LongRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static LongRange rangeGreaterThanOrEq(final Long min) {
    return new LongRange(min, Long.MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static LongRange rangeLessThanOrEq(final Long max) {
    return new LongRange(Long.MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LongRange intersection(final LongRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new LongRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LongRange clamp(final LongRange range) {
    if (range == null) {
      return this;
    }
    return new LongRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getLength() {
    return getMax() - getMin();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMaxValue() {
    return getMax() == Long.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMinValue() {
    return getMin() == Long.MIN_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LongRange clone() {
    return new LongRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }

}