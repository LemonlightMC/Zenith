package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;

/**
 * A {@link Range} implementation for {@link Short} values.
 */
public class ShortRange extends Range<ShortRange, Short> {
  /** A range spanning all representable {@code short} values. */
  public static final ShortRange ALL = new ShortRange();
  /** The minimum representable {@code short} value. */
  public static final short MIN_VALUE = Short.MIN_VALUE;
  /** The maximum representable {@code short} value. */
  public static final short MAX_VALUE = Short.MAX_VALUE;

  /**
   * Creates a range spanning all representable values.
   */
  public ShortRange() {
    super(Short.MIN_VALUE, Short.MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public ShortRange(final Short min) {
    super(min == null ? Short.MIN_VALUE : min, Short.MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public ShortRange(final Short min, final Short max) {
    super(min == null ? Short.MIN_VALUE : min, max == null ? Short.MAX_VALUE : max);
  }

  /**
   * Creates a range with the specified bounds and inclusivity settings.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   */
  public ShortRange(final Short min, final boolean minInclusive, final Short max, final boolean maxInclusive) {
    super(min == null ? Short.MIN_VALUE : min, minInclusive, max == null ? Short.MAX_VALUE : max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static ShortRange at(final Short pos) {
    return new ShortRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static ShortRange from(final Short min, final Short max) {
    return new ShortRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static ShortRange from(final ShortRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ShortRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link ShortRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static ShortRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final Short min = parsed.minStr() == null ? Short.MIN_VALUE : NumberConversions.parseShort(parsed.minStr());
    final Short max = parsed.maxStr() == null ? Short.MAX_VALUE : NumberConversions.parseShort(parsed.maxStr());
    return new ShortRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static ShortRange encompassing(final ShortRange a, final ShortRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ShortRange((a.getMin() <= b.getMin()) ? a.getMin() : b.getMin(),
        (a.getMax() >= b.getMax()) ? a.getMax() : b.getMax());
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static ShortRange rangeGreaterThanOrEq(final Short min) {
    return new ShortRange(min, Short.MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static ShortRange rangeLessThanOrEq(final Short max) {
    return new ShortRange(Short.MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ShortRange intersection(final ShortRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new ShortRange(getMin() >= range.getMin() ? getMin() : range.getMin(),
        getMax() <= range.getMax() ? getMax() : range.getMax());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ShortRange clamp(final ShortRange range) {
    if (range == null) {
      return this;
    }
    return new ShortRange((short) Math.max(getMin(), range.getMin()), (short) Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Short getLength() {
    return (short) (getMax() - getMin());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMaxValue() {
    return getMax() == Short.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMinValue() {
    return getMin() == Short.MIN_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ShortRange clone() {
    return new ShortRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
