package com.lemonlightmc.zenith.version;

import com.lemonlightmc.zenith.math.Range;

public class VersionRange extends Range<VersionRange, Version> {
  public static VersionRange ALL = new VersionRange();
  public static final Version MIN_VALUE = Version.MIN_VALUE;
  public static final Version MAX_VALUE = Version.MAX_VALUE;

  public VersionRange() {
    super(Version.MIN_VALUE, Version.MAX_VALUE);
  }

  public VersionRange(final Version min) {
    super(min, Version.MAX_VALUE);
  }

  public VersionRange(final Version min, final Version max) {
    super(min, true, max, false);
  }

  public VersionRange(final Version min, final boolean minInclusive, final Version max, final boolean maxInclusive) {
    super(min, minInclusive, max, maxInclusive);
  }

  public static VersionRange at(final Version pos) {
    return new VersionRange(pos, pos);
  }

  public static VersionRange from(final Version min, final Version max) {
    return new VersionRange(min, max);
  }

  public static VersionRange from(final Version min, final boolean minInclusive, final Version max,
      final boolean maxInclusive) {
    return new VersionRange(min, minInclusive, max, maxInclusive);
  }

  public static VersionRange from(final VersionRange range) {
    if (range == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return range.clone();
  }

  public static VersionRange encompassing(final VersionRange a, final VersionRange b) {
    if (a == null || b == null) {
      throw new IllegalArgumentException("Range cant be null");
    }
    return new VersionRange((a.getMin().compareTo(b.getMin())) != 1 ? a.getMin() : b.getMin(),
        (a.getMax().compareTo(b.getMax())) != -1 ? a.getMax() : b.getMax());
  }

  public static VersionRange rangeGreaterThanOrEq(final Version min) {
    return new VersionRange(min, Version.MAX_VALUE);
  }

  public static VersionRange rangeLessThanOrEq(final Version max) {
    return new VersionRange(Version.MIN_VALUE, max);
  }

  @Override
  public VersionRange intersection(final VersionRange range) {
    if (range == null || !overlaps(range)) {
      return null;
    }
    final Version newMin = getMin().compareTo(range.getMin()) > 0 ? getMin() : range.getMin();
    final Version newMax = getMax().compareTo(range.getMax()) < 0 ? getMax() : range.getMax();
    return new VersionRange(newMin, newMax);
  }

  @Override
  public VersionRange clamp(final VersionRange range) {
    if (range == null) {
      return this;
    }
    return new VersionRange(clamp(range.getMin()), clamp(range.getMax()));
  }

  @Override
  public Version getLength() {
    throw new UnsupportedOperationException("VersionRange does not support length");
  }

  @Override
  public boolean hasMaxValue() {
    return getMax() == Version.MAX_VALUE;
  }

  @Override
  public boolean hasMinValue() {
    return getMin() == Version.MIN_VALUE;
  }

  @Override
  public VersionRange clone() {
    return new VersionRange(getMin(), isMinInclusive(), getMax(), isMaxInclusive());
  }
}
