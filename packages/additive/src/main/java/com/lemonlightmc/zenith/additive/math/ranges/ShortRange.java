package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;

public class ShortRange extends Range<ShortRange, Short> {
  public static final ShortRange ALL = new ShortRange();
  public static final short MIN_VALUE = Short.MIN_VALUE;
  public static final short MAX_VALUE = Short.MAX_VALUE;

  public ShortRange() {
    super(Short.MIN_VALUE, Short.MAX_VALUE);
  }

  public ShortRange(final Short min) {
    super(min == null ? Short.MIN_VALUE : min, Short.MAX_VALUE);
  }

  public ShortRange(final Short min, final Short max) {
    super(min == null ? Short.MIN_VALUE : min, max == null ? Short.MAX_VALUE : max);
  }

  public ShortRange(final Short min, final boolean minInclusive, final Short max, final boolean maxInclusive) {
    super(min == null ? Short.MIN_VALUE : min, minInclusive, max == null ? Short.MAX_VALUE : max, maxInclusive);
  }

  public static ShortRange at(final Short pos) {
    return new ShortRange(pos, pos);
  }

  public static ShortRange from(final Short min, final Short max) {
    return new ShortRange(min, max);
  }

  public static ShortRange from(final ShortRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ShortRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static ShortRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new ShortRange(arr[0].length() == 0 ? Short.MIN_VALUE : NumberConversions.parseShort(arr[0]),
        arr[1].length() == 0 ? Short.MAX_VALUE : NumberConversions.parseShort(arr[1]));
  }

  public static ShortRange encompassing(final ShortRange a, final ShortRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new ShortRange((a.getMin() <= b.getMin()) ? a.getMin() : b.getMin(),
        (a.getMax() >= b.getMax()) ? a.getMax() : b.getMax());
  }

  public static ShortRange rangeGreaterThanOrEq(final Short min) {
    return new ShortRange(min, Short.MAX_VALUE);
  }

  public static ShortRange rangeLessThanOrEq(final Short max) {
    return new ShortRange(Short.MIN_VALUE, max);
  }

  @Override
  public ShortRange intersection(final ShortRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new ShortRange(getMin() >= range.getMin() ? getMin() : range.getMin(),
        getMax() <= range.getMax() ? getMax() : range.getMax());
  }

  @Override
  public ShortRange clamp(final ShortRange range) {
    if (range == null) {
      return this;
    }
    return new ShortRange((short) Math.max(getMin(), range.getMin()), (short) Math.min(getMax(), range.getMax()));
  }

  @Override
  public Short getLength() {
    return (short) (getMax() - getMin());
  }

  @Override
  public boolean hasMaxValue() {
    return getMax() == Short.MAX_VALUE;
  }

  @Override
  public boolean hasMinValue() {
    return getMin() == Short.MIN_VALUE;
  }

  @Override
  public ShortRange clone() {
    return new ShortRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
