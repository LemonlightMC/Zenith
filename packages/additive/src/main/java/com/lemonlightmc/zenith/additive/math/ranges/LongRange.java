package com.lemonlightmc.zenith.additive.math.ranges;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.additive.math.Range;
import com.lemonlightmc.zenith.additive.math.Range.RangeStringParsed;

public class LongRange extends Range<LongRange, Long> {
  public static LongRange ALL = new LongRange();
  public static final long MIN_VALUE = Long.MIN_VALUE;
  public static final long MAX_VALUE = Long.MAX_VALUE;

  public LongRange() {
    super(Long.MIN_VALUE, Long.MAX_VALUE);
  }

  public LongRange(final Long min) {
    super(min, Long.MAX_VALUE);
  }

  public LongRange(final Long min, final Long max) {
    super(min, max);
  }

  public LongRange(final Long min, final boolean minInclusive, final Long max, final boolean maxInclusive) {
    super(min, minInclusive, max, maxInclusive);
  }

  public static LongRange at(final Long pos) {
    return new LongRange(pos, pos);
  }

  public static LongRange from(final Long min, final Long max) {
    return new LongRange(min, max);
  }

  public static LongRange from(final LongRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new LongRange(range.getMin(), range.isMinInclusive(), range.getMax(), range.isMaxInclusive());
  }

  public static LongRange from(final String str) {
    if (str == null || str.isBlank()) {
      return ALL;
    }
    final RangeStringParsed parsed = Range.parseStr(str);
    final Long min = parsed.minStr() == null ? Long.MIN_VALUE : NumberConversions.parseLong(parsed.minStr());
    final Long max = parsed.maxStr() == null ? Long.MAX_VALUE : NumberConversions.parseLong(parsed.maxStr());
    return new LongRange(min, parsed.minInclusive(), max, parsed.maxInclusive());
  }

  public static LongRange encompassing(final LongRange a, final LongRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new LongRange(Math.min(a.getMin(), b.getMin()),
        Math.max(a.getMax(), b.getMax()));
  }

  public static LongRange rangeGreaterThanOrEq(final Long min) {
    return new LongRange(min, Long.MAX_VALUE);
  }

  public static LongRange rangeLessThanOrEq(final Long max) {
    return new LongRange(Long.MIN_VALUE, max);
  }

  @Override
  public LongRange intersection(final LongRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    return new LongRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public LongRange clamp(final LongRange range) {
    if (range == null) {
      return this;
    }
    return new LongRange(Math.max(getMin(), range.getMin()), Math.min(getMax(), range.getMax()));
  }

  @Override
  public Long getLength() {
    return getMax() - getMin();
  }

  @Override
  public boolean hasMaxValue() {
    return getMax() == Long.MAX_VALUE;
  }

  @Override
  public boolean hasMinValue() {
    return getMin() == Long.MIN_VALUE;
  }

  @Override
  public LongRange clone() {
    return new LongRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }

}
