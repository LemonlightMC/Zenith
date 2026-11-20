package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;

/**
 * A {@link Range} implementation for {@link Byte} values.
 */
public class ByteRange extends Range<ByteRange, Byte> {
  /** A range spanning all representable {@code byte} values. */
  public static ByteRange ALL = new ByteRange();
  /** The minimum representable {@code byte} value. */
  public static final byte MIN_VALUE = Byte.MIN_VALUE;
  /** The maximum representable {@code byte} value. */
  public static final byte MAX_VALUE = Byte.MAX_VALUE;

  /**
   * Creates a range spanning all representable values.
   */
  public ByteRange() {
    super(Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  /**
   * Creates a range with the specified lower bound and the maximum upper bound.
   *
   * @param min the lower bound
   */
  public ByteRange(final Byte min) {
    super(min == null ? Byte.MIN_VALUE : min, Byte.MAX_VALUE);
  }

  /**
   * Creates a range with the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   */
  public ByteRange(final Byte min, final Byte max) {
    super(min == null ? Byte.MIN_VALUE : min, max == null ? Byte.MAX_VALUE : max);
  }

  /**
   * Creates a range with the specified bounds and inclusivity settings.
   *
   * @param min          the lower bound
   * @param minInclusive whether the lower bound is inclusive
   * @param max          the upper bound
   * @param maxInclusive whether the upper bound is inclusive
   */
  public ByteRange(final Byte min, final boolean minInclusive, final Byte max, final boolean maxInclusive) {
    super(min == null ? Byte.MIN_VALUE : min, minInclusive, max == null ? Byte.MAX_VALUE : max, maxInclusive);
  }

  /**
   * Creates a range containing only the specified value.
   *
   * @param pos the value to represent
   * @return a range containing only the given value
   */
  public static ByteRange at(final Byte pos) {
    return new ByteRange(pos, pos);
  }

  /**
   * Creates a range spanning the specified bounds.
   *
   * @param min the lower bound
   * @param max the upper bound
   * @return a range spanning the given bounds
   */
  public static ByteRange from(final Byte min, final Byte max) {
    return new ByteRange(min, max);
  }

  /**
   * Creates a copy of the given range.
   *
   * @param range the range to copy
   * @return a copy of the given range
   * @throws IllegalArgumentException if the range is {@code null}
   */
  public static ByteRange from(final ByteRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ByteRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  /**
   * Parses a string into a {@link ByteRange}.
   *
   * @param str the string representation to parse
   * @return the parsed range, or {@link #ALL} if the string is {@code null} or blank
   */
  public static ByteRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final Byte min = parsed.minStr() == null ? Byte.MIN_VALUE : NumberConversions.parseByte(parsed.minStr());
    final Byte max = parsed.maxStr() == null ? Byte.MAX_VALUE : NumberConversions.parseByte(parsed.maxStr());
    return new ByteRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  /**
   * Returns the smallest range encompassing both ranges.
   *
   * @param a the first range
   * @param b the second range
   * @return the encompassing range
   * @throws IllegalArgumentException if either range is {@code null}
   */
  public static ByteRange encompassing(final ByteRange a, final ByteRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ByteRange((a.getMin() <= b.getMin()) ? a.getMin() : b.getMin(),
        (a.getMax() >= b.getMax()) ? a.getMax() : b.getMax());
  }

  /**
   * Creates a range from the specified lower bound to the maximum value.
   *
   * @param min the lower bound
   * @return a range from the given lower bound to the maximum value
   */
  public static ByteRange rangeGreaterThanOrEq(final Byte min) {
    return new ByteRange(min, Byte.MAX_VALUE);
  }

  /**
   * Creates a range from the minimum value to the specified upper bound.
   *
   * @param max the upper bound
   * @return a range from the minimum value to the given upper bound
   */
  public static ByteRange rangeLessThanOrEq(final Byte max) {
    return new ByteRange(Byte.MIN_VALUE, max);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ByteRange intersection(final ByteRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new ByteRange(getMin() >= range.getMin() ? getMin() : range.getMin(),
        getMax() <= range.getMax() ? getMax() : range.getMax());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ByteRange clamp(final ByteRange range) {
    if (range == null) {
      return this;
    }
    return new ByteRange((byte) Math.max(getMin(), range.getMin()), (byte) Math.min(getMax(), range.getMax()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Byte getLength() {
    return (byte) (getMax() - getMin());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMaxValue() {
    return getMax() == Byte.MAX_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMinValue() {
    return getMin() == Byte.MIN_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ByteRange clone() {
    return new ByteRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
