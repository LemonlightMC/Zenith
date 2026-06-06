package com.lemonlightmc.zenith.math.ranges;

import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.math.Range;

public class StringRange extends Range<StringRange, Integer> {
  public static final StringRange ALL = new StringRange();
  public static final int MIN_VALUE = Integer.MIN_VALUE;
  public static final int MAX_VALUE = Integer.MAX_VALUE;

  public StringRange() {
    super(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  public StringRange(final Integer min) {
    super(min == null ? Integer.MIN_VALUE : min, Integer.MAX_VALUE);
  }

  public StringRange(final Integer min, final Integer max) {
    super(min == null ? Integer.MIN_VALUE : min, max == null ? Integer.MAX_VALUE : max);
  }

  public StringRange(final Integer min, final boolean minInclusive, final Integer max, final boolean maxInclusive) {
    super(min == null ? Integer.MIN_VALUE : min, minInclusive, max == null ? Integer.MAX_VALUE : max, maxInclusive);
  }

  public static StringRange at(final Integer pos) {
    return new StringRange(pos, pos);
  }

  public static StringRange from(final Integer min, final Integer max) {
    return new StringRange(min, max);
  }

  public static StringRange from(final StringRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new StringRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static StringRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new StringRange(arr[0].length() == 0 ? Integer.MIN_VALUE : NumberConversions.parseInt(arr[0]),
        arr[1].length() == 0 ? Integer.MAX_VALUE : NumberConversions.parseInt(arr[1]));
  }

  public static StringRange encompassing(final StringRange a, final StringRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new StringRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static StringRange rangeGreaterThanOrEq(final Integer min) {
    return new StringRange(min, Integer.MAX_VALUE);
  }

  public static StringRange rangeLessThanOrEq(final Integer max) {
    return new StringRange(Integer.MIN_VALUE, max);
  }

  @Override
  public StringRange intersection(final StringRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new StringRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public StringRange clamp(final StringRange range) {
    if (range == null) {
      return this;
    }
    return new StringRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
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
  public StringRange clone() {
    return new StringRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
