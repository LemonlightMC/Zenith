package com.lemonlightmc.zenith.math.ranges;

import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.math.Range;

public class DoubleRange extends Range<DoubleRange, Double> {
  public static DoubleRange ALL = new DoubleRange();
  public static final double MIN_VALUE = Double.MIN_VALUE;
  public static final double MAX_VALUE = Double.MAX_VALUE;

  public DoubleRange() {
    super(Double.MIN_VALUE, Double.MAX_VALUE);
  }

  public DoubleRange(final Double min) {
    super(min == null ? Double.MIN_VALUE : min, Double.MAX_VALUE);
  }

  public DoubleRange(final Double min, final Double max) {
    super(min == null ? Double.MIN_VALUE : min, max == null ? Double.MAX_VALUE : max);
  }

  public DoubleRange(final Double min, final boolean minInclusive, final Double max, final boolean maxInclusive) {
    super(min == null ? Double.MIN_VALUE : min, minInclusive, max == null ? Double.MAX_VALUE : max, maxInclusive);
  }

  public static DoubleRange at(final Double pos) {
    return new DoubleRange(pos, pos);
  }

  public static DoubleRange from(final Double min, final Double max) {
    return new DoubleRange(min, max);
  }

  public static DoubleRange from(final DoubleRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new DoubleRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static DoubleRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new DoubleRange(arr[0].length() == 0 ? Double.MIN_VALUE : NumberConversions.parseDouble(arr[0]),
        arr[1].length() == 0 ? Double.MAX_VALUE : NumberConversions.parseDouble(arr[1]));
  }

  public static DoubleRange encompassing(final DoubleRange a, final DoubleRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new DoubleRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static DoubleRange rangeGreaterThanOrEq(final Double min) {
    return new DoubleRange(min, Double.MAX_VALUE);
  }

  public static DoubleRange rangeLessThanOrEq(final Double max) {
    return new DoubleRange(Double.MIN_VALUE, max);
  }

  @Override
  public DoubleRange intersection(final DoubleRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new DoubleRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public DoubleRange clamp(final DoubleRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new DoubleRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public Double getLength() {
    return getMax() - getMin();
  }

  @Override
  public boolean hasMaxValue() {
    return getMax() == Double.MAX_VALUE;
  }

  @Override
  public boolean hasMinValue() {
    return getMin() == Double.MIN_VALUE;
  }

  @Override
  public DoubleRange clone() {
    return new DoubleRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
