package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.Range.RangeStringParsed;

/**
 * A {@link Range} implementation for {@link Float} values.
 */
public class FloatRange extends Range<FloatRange, Float> {
  /** A range spanning all representable {@code float} values. */
  public static final FloatRange ALL = new FloatRange();
  /** The minimum positive representable {@code float} value. */
  public static final float MIN_VALUE = Float.MIN_VALUE;
  /** The maximum representable {@code float} value. */
  public static final float MAX_VALUE = Float.MAX_VALUE;

  /**
   * Creates a range spanning all representable values.
   */
  public FloatRange() {
    super(Float.MIN_VALUE, Float.MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public FloatRange(final Float min) {
    super(min == null ? Float.MIN_VALUE : min, Float.MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public FloatRange(final Float min, final Float max) {
    super(min == null ? Float.MIN_VALUE : min, max == null ? Float.MAX_VALUE : max);
  }

  /**
   * Creates a range with the specified bounds and inclusivity settings.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   */
  public FloatRange(final Float min, final boolean minInclusive, final Float max, final boolean maxInclusive) {
    super(min == null ? Float.MIN_VALUE : min, minInclusive, max == null ? Float.MAX_VALUE : max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static FloatRange at(final Float pos) {
    return new FloatRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static FloatRange from(final Float min, final Float max) {
    return new FloatRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static FloatRange from(final FloatRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new FloatRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link FloatRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static FloatRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final Float min = parsed.minStr() == null ? Float.MIN_VALUE : NumberConversions.parseFloat(parsed.minStr());
    final Float max = parsed.maxStr() == null ? Float.MAX_VALUE : NumberConversions.parseFloat(parsed.maxStr());
    return new FloatRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static FloatRange encompassing(final FloatRange a, final FloatRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new FloatRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static FloatRange rangeGreaterThanOrEq(final Float min) {
    return new FloatRange(min, Float.MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static FloatRange rangeLessThanOrEq(final Float max) {
    return new FloatRange(Float.MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FloatRange intersection(final FloatRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new FloatRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FloatRange clamp(final FloatRange range) {
    if (range == null) {
      return this;
    }
    return new FloatRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Float getLength() {
    return getMax() - getMin();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMaxValue() {
    return getMax() == Float.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMinValue() {
    return getMin() == Float.MIN_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FloatRange clone() {
    return new FloatRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}