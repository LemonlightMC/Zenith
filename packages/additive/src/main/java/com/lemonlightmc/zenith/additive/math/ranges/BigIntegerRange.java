package com.lemonlightmc.zenith.additive.math.ranges;

import java.math.BigInteger;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;

public class BigIntegerRange extends Range<BigIntegerRange, BigInteger> {
  public static BigIntegerRange ALL = new BigIntegerRange();
  public static final BigInteger MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
  public static final BigInteger MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

  public BigIntegerRange() {
    super(MIN_VALUE, MAX_VALUE);
  }

  public BigIntegerRange(final BigInteger min) {
    super(min == null ? MIN_VALUE : min, MAX_VALUE);
  }

  public BigIntegerRange(final BigInteger min, final BigInteger max) {
    super(min == null ? MIN_VALUE : min, max == null ? MAX_VALUE : max);
  }

  public BigIntegerRange(final BigInteger min, final boolean minInclusive, final BigInteger max,
      final boolean maxInclusive) {
    super(min == null ? MIN_VALUE : min, minInclusive, max == null ? MAX_VALUE : max, maxInclusive);
  }

  public static BigIntegerRange at(final BigInteger pos) {
    return new BigIntegerRange(pos, pos);
  }

  public static BigIntegerRange from(final BigInteger min, final BigInteger max) {
    return new BigIntegerRange(min, max);
  }

  public static BigIntegerRange from(final BigIntegerRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigIntegerRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static BigIntegerRange from(final String str) {
    if (str == null || str.length() == 0) {
      return ALL;
    }
    final String[] arr = Range.parseStr(str);
    return new BigIntegerRange(arr[0].length() == 0 ? MIN_VALUE : NumberConversions.parseBigInteger(arr[0]),
        arr[1].length() == 0 ? MAX_VALUE : NumberConversions.parseBigInteger(arr[1]));
  }

  public static BigIntegerRange encompassing(final BigIntegerRange a, final BigIntegerRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new BigIntegerRange(a.getMin().compareTo(b.getMin()) < 0 ? a.getMin() : b.getMin(),
        a.getMax().compareTo(b.getMax()) > 0 ? a.getMax() : b.getMax());
  }

  public static BigIntegerRange rangeGreaterThanOrEq(final BigInteger min) {
    return new BigIntegerRange(min, MAX_VALUE);
  }

  public static BigIntegerRange rangeLessThanOrEq(final BigInteger max) {
    return new BigIntegerRange(MIN_VALUE, max);
  }

  @Override
  public BigIntegerRange intersection(final BigIntegerRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new BigIntegerRange(getMin().max(range.getMin()), getMax().min(range.getMax()));
  }

  @Override
  public BigIntegerRange clamp(final BigIntegerRange range) {
    if (range == null) {
      return this;
    }
    return new BigIntegerRange(clamp(range.getMin()), clamp(range.getMax()));
  }

  @Override
  public BigInteger getLength() {
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
  public BigIntegerRange clone() {
    return new BigIntegerRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
