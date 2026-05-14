package com.lemonlightmc.zenith.math.ranges;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import com.lemonlightmc.zenith.interfaces.Cloneable;

// TODO: Rework endsWith/startsWith
public interface Range<T extends Range<T, V>, V extends Number> extends Cloneable<T>, Comparable<T> {

  public static BigDecimalRange at(final BigDecimal pos) {
    return new BigDecimalRange(pos, pos);
  }

  public static BigIntegerRange at(final BigInteger pos) {
    return new BigIntegerRange(pos, pos);
  }

  public static IntegerRange at(final int pos) {
    return new IntegerRange(pos, pos);
  }

  public static LongRange at(final long pos) {
    return new LongRange(pos, pos);
  }

  public static FloatRange at(final float pos) {
    return new FloatRange(pos, pos);
  }

  public static DoubleRange at(final double pos) {
    return new DoubleRange(pos, pos);
  }

  public static ShortRange at(final short pos) {
    return new ShortRange(pos, pos);
  }

  public static ByteRange at(final byte pos) {
    return new ByteRange(pos, pos);
  }

  public static BigDecimalRange between(final BigDecimal min, final BigDecimal max) {
    return new BigDecimalRange(min, max);
  }

  public static BigIntegerRange between(final BigInteger min, final BigInteger max) {
    return new BigIntegerRange(min, max);
  }

  public static IntegerRange between(final Integer min, final Integer max) {
    return new IntegerRange(min, max);
  }

  public static LongRange between(final Long min, final Long max) {
    return new LongRange(min, max);
  }

  public static FloatRange between(final Float min, final Float max) {
    return new FloatRange(min, max);
  }

  public static DoubleRange between(final Double min, final Double max) {
    return new DoubleRange(min, max);
  }

  public static ShortRange between(final Short min, final Short max) {
    return new ShortRange(min, max);
  }

  public static ByteRange between(final Byte min, final Byte max) {
    return new ByteRange(min, max);
  }

  public static BigIntegerRange asBigInteger(final String str) {
    return BigIntegerRange.from(str);
  }

  public static IntegerRange asInteger(final String str) {
    return IntegerRange.from(str);
  }

  public static LongRange asLong(final String str) {
    return LongRange.from(str);
  }

  public static FloatRange asFloat(final String str) {
    return FloatRange.from(str);
  }

  public static DoubleRange asDouble(final String str) {
    return DoubleRange.from(str);
  }

  public static ShortRange asShort(final String str) {
    return ShortRange.from(str);
  }

  public static ByteRange asByte(final String str) {
    return ByteRange.from(str);
  }

  public static BigDecimalRange encompassing(final BigDecimalRange a, final BigDecimalRange b) {
    return BigDecimalRange.encompassing(a, b);
  }

  public static BigIntegerRange encompassing(final BigIntegerRange a, final BigIntegerRange b) {
    return BigIntegerRange.encompassing(a, b);
  }

  public static IntegerRange encompassing(final IntegerRange a, final IntegerRange b) {
    return IntegerRange.encompassing(a, b);
  }

  public static LongRange encompassing(final LongRange a, final LongRange b) {
    return LongRange.encompassing(a, b);
  }

  public static FloatRange encompassing(final FloatRange a, final FloatRange b) {
    return FloatRange.encompassing(a, b);
  }

  public static DoubleRange encompassing(final DoubleRange a, final DoubleRange b) {
    return DoubleRange.encompassing(a, b);
  }

  public static ShortRange encompassing(final ShortRange a, final ShortRange b) {
    return ShortRange.encompassing(a, b);
  }

  public static ByteRange encompassing(final ByteRange a, final ByteRange b) {
    return ByteRange.encompassing(a, b);
  }

  static String[] parseStr(String str) {
    if (str == null || str.length() == 0) {
      return new String[] { "", "" };
    }
    if (str.charAt(0) == '[') {
      str = str.substring(1);
    }
    if (str.charAt(str.length() - 1) == ']') {
      str = str.substring(0, str.length() - 1);
    }
    final String[] parts = str.split("[,\\.\\s]+");
    if (parts == null || parts.length != 2) {
      return new String[] { "", "" };
    }

    return new String[] { parts[0].strip(), parts[1].strip() };
  }

  static <T extends Range<T, V>, V extends Number> Comparator<T> createComparator() {
    return new Comparator<T>() {
      @Override
      public int compare(final T range1, final T range2) {
        if (range1 == null) {
          return -1;
        }
        if (range2 == null) {
          return 1;
        }
        return range1.compareTo(range2);
      }
    };
  }

  V getMin();

  V getMiddle();

  V getMax();

  boolean isLower(V i);

  boolean isHigher(V i);

  boolean isInRange(V i);

  boolean isOutsideRange(V i);

  boolean isAfter(V num);

  boolean isAfter(T range);

  boolean isBefore(V num);

  boolean isBefore(T range);

  boolean startsWith(V num);

  boolean startsWith(T range);

  boolean endsWith(V num);

  boolean endsWith(T range);

  boolean isEmpty();

  boolean contains(V num);

  boolean contains(T range);

  boolean overlaps(T range);

  T intersection(T range);

  V clamp(V num);

  T clamp(T range);

  V getLength();

  boolean isMaxValue();

  boolean isMinValue();

  @Override
  int compareTo(T o);

  Comparator<T> getComparator();

  @Override
  T clone();

  @Override
  String toString();

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

}