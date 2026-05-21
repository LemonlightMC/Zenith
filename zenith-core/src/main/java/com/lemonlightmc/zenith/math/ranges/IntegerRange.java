package com.lemonlightmc.zenith.math.ranges;

import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.math.Range;

public class IntegerRange extends Range<IntegerRange, Integer> {
  public static IntegerRange ALL = new IntegerRange();
  public static final int MIN_VALUE = Integer.MIN_VALUE;
  public static final int MAX_VALUE = Integer.MAX_VALUE;

  public IntegerRange() {
    super(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  public IntegerRange(final Integer min) {
    super(min == null ? Integer.MIN_VALUE : min, Integer.MAX_VALUE);
  }

  public IntegerRange(final Integer min, final Integer max) {
    super(min == null ? Integer.MIN_VALUE : min, max == null ? Integer.MAX_VALUE : max);
  }

  public IntegerRange(final Integer min, final boolean minInclusive, final Integer max, final boolean maxInclusive) {
    super(min == null ? Integer.MIN_VALUE : min, minInclusive, max == null ? Integer.MAX_VALUE : max, maxInclusive);
  }

  public static IntegerRange at(final Integer pos) {
    return new IntegerRange(pos, pos);
  }

  public static IntegerRange from(final Integer min, final Integer max) {
    return new IntegerRange(min, max);
  }

  public static IntegerRange from(final IntegerRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new IntegerRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static IntegerRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new IntegerRange(arr[0].length() == 0 ? Integer.MIN_VALUE : NumberConversions.parseInt(arr[0]),
        arr[1].length() == 0 ? Integer.MAX_VALUE : NumberConversions.parseInt(arr[1]));
  }

  public static IntegerRange encompassing(final IntegerRange a, final IntegerRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new IntegerRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static IntegerRange rangeGreaterThanOrEq(final Integer min) {
    return new IntegerRange(min, Integer.MAX_VALUE);
  }

  public static IntegerRange rangeLessThanOrEq(final Integer max) {
    return new IntegerRange(Integer.MIN_VALUE, max);
  }

  @Override
  public IntegerRange intersection(final IntegerRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new IntegerRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public IntegerRange clamp(final IntegerRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new IntegerRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public Integer getLength() {
    return getMax() - getMin();
  }

  @Override
  public boolean hasMaxValue() {
    return getMax() == Integer.MAX_VALUE;
  }

  @Override
  public boolean hasMinValue() {
    return getMin() == Integer.MIN_VALUE;
  }

  @Override
  public IntegerRange clone() {
    return new IntegerRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
