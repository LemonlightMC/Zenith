package com.lemonlightmc.zenith.math.ranges;

import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.math.Range;

public class ByteRange extends Range<ByteRange, Byte> {
  public static ByteRange ALL = new ByteRange();
  public static final byte MIN_VALUE = Byte.MIN_VALUE;
  public static final byte MAX_VALUE = Byte.MAX_VALUE;

  public ByteRange() {
    super(Byte.MIN_VALUE, Byte.MAX_VALUE);
  }

  public ByteRange(final Byte min) {
    super(min == null ? Byte.MIN_VALUE : min, Byte.MAX_VALUE);
  }

  public ByteRange(final Byte min, final Byte max) {
    super(min == null ? Byte.MIN_VALUE : min, max == null ? Byte.MAX_VALUE : max);
  }

  public ByteRange(final Byte min, final boolean minInclusive, final Byte max, final boolean maxInclusive) {
    super(min == null ? Byte.MIN_VALUE : min, minInclusive, max == null ? Byte.MAX_VALUE : max, maxInclusive);
  }

  public static ByteRange at(final Byte pos) {
    return new ByteRange(pos, pos);
  }

  public static ByteRange from(final Byte min, final Byte max) {
    return new ByteRange(min, max);
  }

  public static ByteRange from(final ByteRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ByteRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static ByteRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new ByteRange(arr[0].length() == 0 ? Byte.MIN_VALUE : NumberConversions.parseByte(arr[0]),
        arr[1].length() == 0 ? Byte.MAX_VALUE : NumberConversions.parseByte(arr[1]));
  }

  public static ByteRange encompassing(final ByteRange a, final ByteRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ByteRange((a.getMin() <= b.getMin()) ? a.getMin() : b.getMin(),
        (a.getMax() >= b.getMax()) ? a.getMax() : b.getMax());
  }

  public static ByteRange rangeGreaterThanOrEq(final Byte min) {
    return new ByteRange(min, Byte.MAX_VALUE);
  }

  public static ByteRange rangeLessThanOrEq(final Byte max) {
    return new ByteRange(Byte.MIN_VALUE, max);
  }

  @Override
  public ByteRange intersection(final ByteRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new ByteRange(getMin() >= range.getMin() ? getMin() : range.getMin(),
        getMax() <= range.getMax() ? getMax() : range.getMax());
  }

  @Override
  public ByteRange clamp(final ByteRange range) {
    if (range == null) {
      return this;
    }
    return new ByteRange((byte) Math.max(getMin(), range.getMin()), (byte) Math.min(getMax(), range.getMax()));
  }

  @Override
  public Byte getLength() {
    return (byte) (getMax() - getMin());
  }

  @Override
  public boolean hasMaxValue() {
    return getMax() == Byte.MAX_VALUE;
  }

  @Override
  public boolean hasMinValue() {
    return getMin() == Byte.MIN_VALUE;
  }

  @Override
  public ByteRange clone() {
    return new ByteRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
