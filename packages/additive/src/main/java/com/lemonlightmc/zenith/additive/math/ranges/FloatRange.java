package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;

public class FloatRange extends Range<FloatRange, Float> {
  public static final FloatRange ALL = new FloatRange();
  public static final float MIN_VALUE = Float.MIN_VALUE;
  public static final float MAX_VALUE = Float.MAX_VALUE;

  public FloatRange() {
    super(Float.MIN_VALUE, Float.MAX_VALUE);
  }

  public FloatRange(final Float min) {
    super(min == null ? Float.MIN_VALUE : min, Float.MAX_VALUE);
  }

  public FloatRange(final Float min, final Float max) {
    super(min == null ? Float.MIN_VALUE : min, max == null ? Float.MAX_VALUE : max);
  }

  public FloatRange(final Float min, final boolean minInclusive, final Float max, final boolean maxInclusive) {
    super(min == null ? Float.MIN_VALUE : min, minInclusive, max == null ? Float.MAX_VALUE : max, maxInclusive);
  }

  public static FloatRange at(final Float pos) {
    return new FloatRange(pos, pos);
  }

  public static FloatRange from(final Float min, final Float max) {
    return new FloatRange(min, max);
  }

  public static FloatRange from(final FloatRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new FloatRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static FloatRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new FloatRange(arr[0].length() == 0 ? Float.MIN_VALUE : NumberConversions.parseFloat(arr[0]),
        arr[1].length() == 0 ? Float.MAX_VALUE : NumberConversions.parseFloat(arr[1]));
  }

  public static FloatRange encompassing(final FloatRange a, final FloatRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new FloatRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static FloatRange rangeGreaterThanOrEq(final Float min) {
    return new FloatRange(min, Float.MAX_VALUE);
  }

  public static FloatRange rangeLessThanOrEq(final Float max) {
    return new FloatRange(Float.MIN_VALUE, max);
  }

  @Override
  public FloatRange intersection(final FloatRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new FloatRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public FloatRange clamp(final FloatRange range) {
    if (range == null) {
      return this;
    }
    return new FloatRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public Float getLength() {
    return getMax() - getMin();
  }

  @Override
  public boolean hasMaxValue() {
    return getMax() == Float.MAX_VALUE;
  }

  @Override
  public boolean hasMinValue() {
    return getMin() == Float.MIN_VALUE;
  }

  @Override
  public FloatRange clone() {
    return new FloatRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
