package com.lemonlightmc.zenith.math.ranges;

import java.math.BigDecimal;

import com.lemonlightmc.zenith.math.NumberConversions;
import com.lemonlightmc.zenith.math.Range;

public class BigDecimalRange extends Range<BigDecimalRange, BigDecimal> {
  public static BigDecimalRange ALL = new BigDecimalRange();
  public static final BigDecimal MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
  public static final BigDecimal MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);

  public BigDecimalRange() {
    super(MIN_VALUE, MAX_VALUE);
  }

  public BigDecimalRange(final BigDecimal min) {
    super(min == null ? MIN_VALUE : min, MAX_VALUE);
  }

  public BigDecimalRange(final BigDecimal min, final BigDecimal max) {
    super(min == null ? MIN_VALUE : min, max == null ? MAX_VALUE : max);
  }

  public BigDecimalRange(final BigDecimal min, final boolean minInclusive, final BigDecimal max,
      final boolean maxInclusive) {
    super(min == null ? MIN_VALUE : min, minInclusive, max == null ? MAX_VALUE : max, maxInclusive);
  }

  public static BigDecimalRange at(final BigDecimal pos) {
    return new BigDecimalRange(pos, pos);
  }

  public static BigDecimalRange from(final BigDecimal min, final BigDecimal max) {
    return new BigDecimalRange(min, max);
  }

  public static BigDecimalRange from(final BigDecimalRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigDecimalRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static BigDecimalRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new BigDecimalRange(arr[0].length() == 0 ? MIN_VALUE : NumberConversions.parseBigDecimal(arr[0]),
        arr[1].length() == 0 ? MAX_VALUE : NumberConversions.parseBigDecimal(arr[1]));
  }

  public static BigDecimalRange encompassing(final BigDecimalRange a, final BigDecimalRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigDecimalRange(a.getMin().compareTo(b.getMin()) < 0 ? a.getMin() : b.getMin(),
        a.getMax().compareTo(b.getMax()) > 0 ? a.getMax() : b.getMax());
  }

  public static BigDecimalRange rangeGreaterThanOrEq(final BigDecimal min) {
    return new BigDecimalRange(min, MAX_VALUE);
  }

  public static BigDecimalRange rangeLessThanOrEq(final BigDecimal max) {
    return new BigDecimalRange(MIN_VALUE, max);
  }

  @Override
  public BigDecimalRange intersection(final BigDecimalRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    final BigDecimal newMin = getMin().compareTo(range.getMin()) > 0 ? getMin() : range.getMin();
    final BigDecimal newMax = getMax().compareTo(range.getMax()) < 0 ? getMax() : range.getMax();
    return new BigDecimalRange(newMin, newMax);
  }

  @Override
  public BigDecimalRange clamp(final BigDecimalRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigDecimalRange(clamp(range.getMin()), clamp(range.getMax()));
  }

  @Override
  public BigDecimal getLength() {
    return getMax().subtract(getMin());
  }

  @Override
  public boolean hasMaxValue() {
    return getMax().equals(MAX_VALUE);
  }

  @Override
  public boolean hasMinValue() {
    return getMin().equals(MIN_VALUE);
  }

  @Override
  public BigDecimalRange clone() {
    return new BigDecimalRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }

}